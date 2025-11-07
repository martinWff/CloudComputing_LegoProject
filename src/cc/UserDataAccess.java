package cc;

import cc.srv.db.CosmosConnection;
import cc.srv.db.RedisConnection;
import cc.srv.db.dataconstructor.UserModel;
import cc.srv.db.dataconstructor.UserProfile;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UserDataAccess {


    private static final CosmosContainer UsersCont = CosmosConnection.getDatabase().getContainer("Users");

    public static final UserProfile DeletedUser = new UserProfile("DeletedUser","Deleted User", Instant.now(),null);

    public static UserProfile[] retrieveUserProfiles(String[] userIds) {

        if (userIds == null)
            return null;

        if (userIds.length == 0)
            return new UserProfile[0];


        try (Jedis jedis = RedisConnection.getCachePool().getResource()) {


            String[] redisArray = Arrays.stream(userIds).map(s -> "USER:"+s ).toArray(String[]::new);

            Jsonb builder = JsonbBuilder.create();

            List<String> cachedResults = jedis.mget(redisArray);

             List<AbstractMap.SimpleImmutableEntry<String,String>> mapAsList = IntStream.range(0, cachedResults.size())
                    .mapToObj(i -> new AbstractMap.SimpleImmutableEntry<String,String>(userIds[i],cachedResults.get(i)))
                    .collect(Collectors.toList());


            Map<String, UserProfile> redisMap = mapAsList.stream().filter(s -> s.getValue() != null).collect(Collectors.toMap(s -> s.getKey(), s-> builder.fromJson(s.getValue(),UserProfile.class) ));

            String[] remainingUserIds = mapAsList.stream().filter(o -> o.getValue() == null).map(s -> s.getKey()).toArray(String[]::new);

            if (remainingUserIds.length > 0) {

                SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.isDeleted = false AND ARRAY_CONTAINS(@userIds,c.id)",
                        Arrays.asList(new SqlParameter("@userIds",remainingUserIds)));
                CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

                //saved pages results of the query
                CosmosPagedIterable<UserProfile> results = UsersCont.queryItems(
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

                    cosmosValues.stream().forEach(s -> jedis.setex("User:"+s.getId(),18000,builder.toJson(s)));


                    // jedis.mset(redisArray);


                    UserProfile[] fMap = IntStream.range(0,userIds.length).mapToObj(i -> {
                        String _userId = userIds[i];

                        if (redisMap.containsKey(_userId)) {

                            return redisMap.get(_userId);

                        }

                        if (cosmosMap.containsKey(_userId)) {
                            return cosmosMap.get(_userId);
                        }

                        return null;
                    }).toArray(UserProfile[]::new);

                    return fMap;
                }
            }

            return cachedResults.stream().map(v -> builder.fromJson(v,UserProfile.class)).toArray(UserProfile[]::new);
        }
    }

    public static UserProfile retrieveUserProfile(String userId) {
        try (Jedis jedis = RedisConnection.getCachePool().getResource()) {

            Jsonb builder = JsonbBuilder.create();

            String v = jedis.get("user:"+userId);

            if (v == null) {


                SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE id = @userId",
                        Arrays.asList(new SqlParameter("@userId",userId)));
                CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

                //saved pages results of the query
                CosmosPagedIterable<UserModel> results = UsersCont.queryItems(
                        querySpec,
                        options,
                        UserModel.class
                );

                if (results.iterator().hasNext()) {
                    UserProfile profile = results.iterator().next();

                    jedis.setex("user:"+userId,3600,builder.toJson(profile));

                    return profile;
                }

            } else {

                return builder.fromJson(v, UserProfile.class);

            }
        }

        return null;
    }
}
