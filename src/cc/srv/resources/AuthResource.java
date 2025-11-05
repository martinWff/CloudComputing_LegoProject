package cc.srv.resources;

import cc.srv.db.CosmosConnection;
import cc.srv.db.RedisConnection;
import cc.srv.db.dataconstructor.AuthModel;
import cc.srv.db.dataconstructor.UserModel;
import cc.utils.EnvLoader;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.GetExParams;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

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

    public static UserModel getUserFromToken(String tkn) {
        try (Jedis jedis = RedisConnection.getCachePool().getResource()) {

            Jsonb builder = JsonbBuilder.create();

            GetExParams p = new GetExParams();
            p.ex(EXPIRATION);
            String v = jedis.getEx("session:"+tkn,p);

            return builder.fromJson(v, UserModel.class);

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
            UserModel displayModel = null;
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

                    displayModel = new UserModel(model.getId(),model.getUsername(),null,null,model.getDateOfCreation(),model.getStatus());

                    try (Jedis jedis = RedisConnection.getCachePool().getResource()) {

                        ObjectMapper mapper = new ObjectMapper();

                        jedis.setex("session:"+tkn,EXPIRATION,mapper.writeValueAsString(displayModel));

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

        System.out.println(userModel.getId());
        System.out.println(userModel.getUsername());
        System.out.println(userModel.getEmail());
        System.out.println(userModel.getPassword());
        try {


            UserModel newUser = new UserModel(
                    userModel.getUsername(),
                    userModel.getEmail(),
                    userModel.getPassword(),
                    userModel.getStatus()
            );

            UsersCont.createItem(newUser);

            UserModel displayModel = new UserModel(newUser.getId(),newUser.getUsername(),null,null,newUser.getDateOfCreation(),newUser.getStatus());

            NewCookie cookie = new NewCookie.Builder("Session")
                    .value("user2")
                    .path("/")
                    .maxAge(360)
                    .sameSite(NewCookie.SameSite.LAX)
                    .build();


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

        NewCookie cookie = new NewCookie.Builder("Session")
                .value(null)
                .path("/")
                .maxAge(0)
                .sameSite(NewCookie.SameSite.LAX)
                .build();


        return Response.ok().cookie(cookie).build();


    }
}
