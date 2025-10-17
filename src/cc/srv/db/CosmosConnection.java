package cc.srv.db;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosDatabase;

import cc.utils.EnvLoader;
import jakarta.enterprise.context.ApplicationScoped;

//@Path("/db")

@ApplicationScoped //it creates one instance of this class on start up for the whole web app
public class CosmosConnection{

    // Replace with your real values (to do: find way to use the real link without hardcoding it EX: azure app configurator)
    private static final String ENDPOINT = EnvLoader.GetDBEndpoint();
    private static final String KEY = EnvLoader.GetDBKeys();

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

    public static synchronized CosmosDatabase getDatabase() {
        if (db == null) {
            db = GetDBClient().getDatabase(EnvLoader.GetDBName());
            
        }
        return db;
    }

    //initialization function
    //@PostConstruct
    public static void dbInit()
    {
        System.out.println("starting cosmosDB client connection creation");

        client = GetDBClient();
        db = getDatabase();

        System.out.println(client + "client \n" + db + " database \n");
    }

    public static void TestConn() {
        CosmosClient tempclient = null;
        try {
            // Build the client
            tempclient = new CosmosClientBuilder()
                    .endpoint(ENDPOINT)
                    .key(KEY)
                    .gatewayMode()
                    .consistencyLevel(ConsistencyLevel.EVENTUAL)
                    .buildClient();

            // Try listing databases to verify access
            System.out.println("✅ Connection successful! Listing databases:");

            tempclient.readAllDatabases().forEach(dbases -> {
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
