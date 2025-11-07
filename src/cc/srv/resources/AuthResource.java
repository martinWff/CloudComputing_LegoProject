package cc.srv.resources;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import cc.srv.db.CosmosConnection;
import cc.srv.db.RedisConnection;
import cc.srv.db.dataconstructor.AuthModel;
import cc.srv.db.dataconstructor.UserModel;
import cc.srv.db.dataconstructor.UserProfile;
import cc.utils.EnvLoader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;

@Path("/auth")
public class AuthResource {

    private final CosmosContainer UsersCont = CosmosConnection.getDatabase().getContainer("Users");

    private static final int EXPIRATION = Integer.parseInt(EnvLoader.getVariable("session_expiration"));

    private static final SecureRandom random = new SecureRandom();

    private static String GenerateSessionToken() {
        byte[] arr = new byte[32];
        random.nextBytes(arr);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(arr);
    }

    public static UserProfile getUserFromToken(String tkn) {
        try (Jedis jedis = RedisConnection.getCachePool().getResource()) {

            Jsonb builder = JsonbBuilder.create();

            String v = jedis.get("session:"+tkn);
            jedis.expire("session:"+tkn,EXPIRATION);

            if (v == null || v.length() == 0)
                return null;

            UserProfile c =builder.fromJson(v, UserProfile.class);

            return c;
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(UserModel.PublicView.class)
    public Response login(AuthModel authModel) {

        try {

            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(query,
                    Arrays.asList(new SqlParameter("@email", authModel.getEmail())));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<UserModel> results = UsersCont.queryItems(
                    querySpec,
                    options,
                    UserModel.class
            );

            NewCookie cookie = null;
            UserProfile displayModel = null;
            if (results.iterator().hasNext()) {
                UserModel model = results.iterator().next();

                if (AuthModel.Verify(authModel.getPassword(),model.getPassword())) {

                    String tkn = GenerateSessionToken();

                    cookie = new NewCookie.Builder("Session")
                            .value(tkn)
                            .path("/")
                            .maxAge(EXPIRATION)
                            .sameSite(NewCookie.SameSite.LAX)
                            .build();

                    displayModel = new UserProfile(model.getId(),model.getUsername(),model.getDateOfCreation(),model.getAvatar(),model.getPower());

                    try (Jedis jedis = RedisConnection.getCachePool().getResource()) {

                        ObjectMapper mapper = new ObjectMapper();

                        jedis.setex("session:"+tkn,EXPIRATION,mapper.writeValueAsString(new UserProfile(displayModel.getId(),displayModel.getUsername(),displayModel.getDateOfCreation(), displayModel.getAvatar(),displayModel.getPower())));
                    }
                }
            }

            return Response.ok(displayModel).cookie(cookie).build();
        } catch (CosmosException ex) {
            System.err.println("Cosmos Err: "+ex);
            return null;
        } catch (Exception ex) {
            System.err.println("Err: "+ex);
            return null;
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @JsonView(UserModel.PublicView.class)
    public Response register(UserModel userModel) {

        try {

            if (verifyUser(userModel.getEmail()))
                return Response.ok("Email already in use").build();

            UserModel newUser = new UserModel(
                    UUID.randomUUID().toString(),
                    userModel.getUsername(),
                    userModel.getEmail(),
                    UserModel.Hashed(userModel.getPassword()),
                    Instant.now(),
                    null,
                    userModel.getIsDeleted(),
                    userModel.getPower()
            );

            UsersCont.createItem(newUser);


            String tkn = GenerateSessionToken();

            NewCookie cookie = new NewCookie.Builder("Session")
                    .value(tkn)
                    .path("/")
                    .maxAge(EXPIRATION)
                    .sameSite(NewCookie.SameSite.LAX)
                    .build();

            UserProfile displayModel = new UserProfile(newUser.getId(),newUser.getUsername(),newUser.getDateOfCreation(),newUser.getAvatar(),newUser.getPower());

            try (Jedis jedis = RedisConnection.getCachePool().getResource()) {

                ObjectMapper mapper = new ObjectMapper();

                jedis.setex("session:"+tkn,EXPIRATION,mapper.writeValueAsString(new UserProfile(displayModel.getId(),displayModel.getUsername(),displayModel.getDateOfCreation(), displayModel.getAvatar(),displayModel.getPower())));

            }

            return Response.ok(displayModel).cookie(cookie).build();
        } catch (CosmosException ex) {
            System.err.println("Cosmos Err: "+ex);
            return null;
        } catch (Exception ex) {
            System.err.println("Err: "+ex);
            return null;
        }


    }


    @GET
    @Path("/logout")
    public Response logout(@CookieParam("Session") String session) {

        if (session == null)
            return Response.ok().build();

        NewCookie cookie = new NewCookie.Builder("Session")
                .value(null)
                .path("/")
                .maxAge(0)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        try (Jedis jedis = RedisConnection.getCachePool().getResource()) {
            jedis.del("Session:"+session);
        }

        return Response.ok().cookie(cookie).build();
    }


    public boolean verifyUser(String email) {
        try {
            //Query Cosmos DB for the given email
            String query = "SELECT * FROM c WHERE c.email = @email";
            SqlQuerySpec querySpec = new SqlQuerySpec(query,
                    Arrays.asList(new SqlParameter("@email", email)));

            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<UserModel> results = UsersCont.queryItems(
                    querySpec,
                    options,
                    UserModel.class
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
}
