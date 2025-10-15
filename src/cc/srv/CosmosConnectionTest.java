package cc.srv;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/testdb")
public class CosmosConnectionTest {

    // Replace with your real values
    private static final String ENDPOINT = "https://Link_do_URI/";
    private static final String KEY = "TestKey";

    @GET
    @Path("/connection")
    public void ligma() {
        CosmosClient client = null;
        try {
            // Build the client
            client = new CosmosClientBuilder()
                    .endpoint(ENDPOINT)
                    .key(KEY).gatewayMode()
                    .consistencyLevel(ConsistencyLevel.EVENTUAL)
                    .buildClient();

            // Try listing databases to verify access
            System.out.println("✅ Connection successful! Listing databases:");

            client.readAllDatabases().forEach(db -> {
                System.out.println(" - " + db.getId());
            });

        } catch (Exception e) {
            System.err.println("❌ Failed to connect to Cosmos DB: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}
