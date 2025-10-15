package cc.srv;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/Database")

@ApplicationScoped //it creates one instance of this class on start up for the whole web app
public class CosmosConnection{

    // Replace with your real values (to do: find way to use the real link without hardcoding it EX: azure app configurator)
    
    //store the database thats actually gonna be used from cosmosDB
     private static CosmosDatabase db;

    //creating a client var to store 1 connection to be used between multiple users
    private static CosmosClient client;
    public static synchronized CosmosClient GetDBClient()
    {
        if(client == null)
        {
            client = new CosmosClientBuilder()
                    .endpoint(ENDPOINT)
                    .key(KEY)
                    .gatewayMode()
                    .buildClient();
        }
        return client;
    }

    public static synchronized CosmosDatabase getDatabase(String test) {
        if (db == null) {
            String dbName = System.getenv("COSMOS_DATABASE");
            //db = GetDBClient().getDatabase(dbName);
            db = GetDBClient().getDatabase(test);
        }
        return db;
    }


    //initialization function
    @PostConstruct
    public void init()
    {
        System.out.println("starting cosmosDB client connection creation");

        client = GetDBClient();
        db = getDatabase("LegoDB"); //change this later to environment variables

        //.out.println(client + "\n" + db + "\n");
    }


    @GET
    @Path("/TestDBConnection")
    public void TestConn() {
        CosmosClient tempclient = null;
        try {
            // Build the client
            client = new CosmosClientBuilder()
                    .endpoint(ENDPOINT)
                    .key(KEY)
                    .gatewayMode()
                    .consistencyLevel(ConsistencyLevel.EVENTUAL)
                    .buildClient();

            // Try listing databases to verify access
            System.out.println("✅ Connection successful! Listing databases:");

            client.readAllDatabases().forEach(dbases -> {
                System.out.println(" - " + dbases.getId());
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Cosmos DB: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (tempclient != null) {
                tempclient.close();
            }
        }
    }
}
