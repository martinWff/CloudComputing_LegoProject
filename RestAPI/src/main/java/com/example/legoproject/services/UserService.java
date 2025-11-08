package com.example.legoproject.services;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.legoproject.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.Session;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.json.JsonBuilderFactory;
import redis.clients.jedis.params.GetExParams;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UserService {

    private final CosmosClient cosmosClient;
    private final CosmosContainer container;
    private final CosmosContainer sessionContainer;
    private final JedisPool jedisPool;

    @Autowired
    private ObjectMapper objectMapper;

    private SecureRandom secureRandom;

    private int cookieExpiration = 3600;
    private int redisExpiration = 3600;

    @Autowired
    public UserService(CosmosClient cosmosClient, String databaseName,JedisPool jedisPool) {
        this.cosmosClient = cosmosClient;
        this.container = cosmosClient.getDatabase(databaseName).getContainer("Users");
        this.sessionContainer = cosmosClient.getDatabase(databaseName).getContainer("Sessions");
        this.jedisPool = jedisPool;

        this.secureRandom = new SecureRandom();
    }

    public UserProfile getUser(String id) {

        try (Jedis jedis = jedisPool.getResource()) {


            String cachedUser = jedis.get("User:"+id);
            jedis.expire("User:"+id,redisExpiration);

            if (cachedUser == null) {
                String query = "SELECT c.id,c.username, c.dateOfCreation FROM c WHERE c.id = @id OR c.username = @username";
                SqlQuerySpec querySpec = new SqlQuerySpec(query,
                        Arrays.asList(new SqlParameter("@username", id), new SqlParameter("@id", id)));

                CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

                CosmosPagedIterable<User> results = container.queryItems(
                        querySpec,
                        options,
                        User.class
                );

                //Checks if we found any results
                Iterator<User> iterator = results.iterator();

                if (iterator.hasNext()) {
                    User p = iterator.next();

                    if (p.getIsDeleted()) {
                        return new UserProfile(null, "Deleted User", Instant.now(), null, 0);
                    }


                    return new UserProfile(p.getId(), p.getUsername(), p.getDateOfCreation(), p.getAvatar(), p.getPower());

                } else {
                    return null;
                }
            }

            try {
                return objectMapper.readValue(cachedUser, UserProfile.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize JSON", e);
            }
        }
    }

    public UserProfile[] getUserProfiles(String[] userIds) {

        if (userIds == null)
            return null;

        if (userIds.length == 0)
            return new UserProfile[0];


        try (Jedis jedis = jedisPool.getResource()) {


            String[] redisArray = Arrays.stream(userIds).map(s -> "USER:"+s ).toArray(String[]::new);


            List<String> cachedResults = jedis.mget(redisArray);

            List<AbstractMap.SimpleImmutableEntry<String,String>> mapAsList = IntStream.range(0, cachedResults.size())
                    .mapToObj(i -> new AbstractMap.SimpleImmutableEntry<String,String>(userIds[i],cachedResults.get(i)))
                    .collect(Collectors.toList());


            Map<String, UserProfile> redisMap = mapAsList.stream().filter(s -> s.getValue() != null).collect(Collectors.toMap(s -> s.getKey(), s-> {

                UserProfile p = null;
                try {
                    p = objectMapper.readValue(s.getValue(), UserProfile.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize JSON", e);
                }

                return p;
            }));

            String[] remainingUserIds = mapAsList.stream().filter(o -> o.getValue() == null).map(s -> s.getKey()).toArray(String[]::new);

            if (remainingUserIds.length > 0) {

                SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.isDeleted = false AND ARRAY_CONTAINS(@userIds,c.id)",
                        Arrays.asList(new SqlParameter("@userIds",remainingUserIds)));
                CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

                //saved pages results of the query
                CosmosPagedIterable<UserProfile> results = container.queryItems(
                        querySpec,
                        options,
                        UserProfile.class
                );

                if (results.iterator().hasNext()) {
                    //UserProfile profile = results.iterator().next();

                    List<UserProfile> cosmosValues = results.stream().collect(Collectors.toList());

                    Map<String,UserProfile> cosmosMap = cosmosValues.stream().collect(Collectors.toMap(s -> s.getId(), s -> s));



                  /*  String[] redisPair = IntStream.range(0,cosmosValues.size())
                            .mapToObj(i -> Arrays.asList("User:"+cosmosValues.get(i).getId(),builder.toJson(cosmosValues.get(i))))
                            .flatMap(List::stream).toArray(String[]::new);*/

                    cosmosValues.stream().forEach(s -> {

                        String v = null;

                        try {
                           v = objectMapper.writeValueAsString(s);
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to deserialize JSON",e);
                        }
                        jedis.setex("User:"+s.getId(),18000,v);
                    });



                    UserProfile[] fMap = IntStream.range(0,userIds.length).mapToObj(i -> {
                        String _userId = userIds[i];

                        if (redisMap.containsKey(_userId)) {


                            return redisMap.get(_userId);

                        }

                        if (cosmosMap.containsKey(_userId)) {

                            return cosmosMap.get(_userId);
                        }

                        return new UserProfile(null,"Deleted User",Instant.now(),null,0);
                    }).toArray(UserProfile[]::new);


                    return fMap;
                }
            }

            return cachedResults.stream().map(v -> {

                try {
                    return objectMapper.readValue(v,UserProfile.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize JSON ", e);
                }
            }).toArray(UserProfile[]::new);
        }
    }

    public UserProfile getUserBySession(String session) {

        try (Jedis jedis = jedisPool.getResource()) {

            String v = jedis.get("session:"+session);
            jedis.expire("session:"+session,360);

            if (v == null || v.length() == 0)
            {
                try {
                    SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.id = @sessionId",
                            Arrays.asList(new SqlParameter("@sessionId",session)));
                    CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

                    //saved pages results of the query
                    CosmosPagedIterable<SessionData> results = container.queryItems(
                            querySpec,
                            options,
                            SessionData.class
                    );

                    if (results.iterator().hasNext()) {

                        SessionData sd = results.iterator().next();
                        String c = null;
                        try {
                            c = objectMapper.writeValueAsString(sd.getProfile());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Failed to deserialize JSON ", e);
                        }

                        jedis.setex("session:"+session,redisExpiration,c);

                        return sd.getProfile();
                    }

                } catch (Exception e) {
                    System.err.println("Exception "+e);
                }

                return null;
            }

            try {
                return objectMapper.readValue(v, UserProfile.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize JSON ", e);
            }

        }
    }

    public String generateSessionToken() {
        byte[] arr = new byte[32];
        secureRandom.nextBytes(arr);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(arr);
    }

    public boolean createUser(User user) {

        if (!verifyEmail(user.getEmail()))
        {
            container.createItem(user);
            return true;
        } else {
            return false;
        }

    }

    public String getSessionByUser(String userId) {

        SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.user = @userId",
                Arrays.asList(new SqlParameter("@userId",userId)));

        CosmosPagedIterable<SessionData> page = sessionContainer.queryItems(querySpec,new CosmosQueryRequestOptions(),SessionData.class);
        if (page.iterator().hasNext()) {
            SessionData sd = page.iterator().next();

            return sd.getUser();
        }

        return null;
    }

    public UserSessionData login(AuthData authData) {

        try {
            UserSessionData sessionData = null;

            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(query,
                    Arrays.asList(new SqlParameter("@email", authData.getEmail())));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<User> results = container.queryItems(
                    querySpec,
                    options,
                    User.class
            );

            UserProfile displayModel = null;
            if (results.iterator().hasNext()) {
                User model = results.iterator().next();

                if (verifyPassword(authData.getPassword(),model.getPassword())) {

                    displayModel = new UserProfile(model.getId(),model.getUsername(),model.getDateOfCreation(),model.getAvatar(),model.getPower());

                    try (Jedis jedis = jedisPool.getResource()) {

                        ObjectMapper mapper = new ObjectMapper();

                        String session = generateSessionToken();
                        sessionData = new UserSessionData(displayModel,session);

                        jedis.setex("session:"+ session ,redisExpiration,mapper.writeValueAsString(displayModel));

                        SessionData sd = new SessionData(session,displayModel);
                        sessionContainer.createItem(sd);
                    }
                }
            }

            return sessionData;
        } catch (CosmosException ex) {
            System.err.println("Cosmos Err: "+ex);
            return null;
        } catch (Exception ex) {
            System.err.println("Err: "+ex);
            return null;
        }


    }

    public int getSessionExpiration() {
        return cookieExpiration;
    }

    public void logout(String session) {

        if (session == null)
        return;


        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("session:"+session);
        }


        SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.id = @userId",
                Arrays.asList(new SqlParameter("@userId",session)));

        CosmosPagedIterable<SessionData> page = sessionContainer.queryItems(querySpec,new CosmosQueryRequestOptions(),SessionData.class);
        if (page.iterator().hasNext()) {
            SessionData sd = page.iterator().next();

            sessionContainer.deleteItem(sd, new CosmosItemRequestOptions());
        }
    }

    public boolean verifyEmail(String email) {
        try {
            //Query Cosmos DB for the given email
            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(query,
                    Arrays.asList(new SqlParameter("@email", email)));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<User> results = container.queryItems(
                    querySpec,
                    options,
                    User.class
            );
            return results.iterator().hasNext();
        } catch (CosmosException e) {
            // Handle Cosmos DB-specific errors (e.g., connection or query failure)
            return false;

        } catch (Exception e) {
            // Handle unexpected exceptions
            return false;
        }
    }

    public boolean verifyPassword(String password,String hashedPass) {
        return BCrypt.checkpw(password,hashedPass);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

}
