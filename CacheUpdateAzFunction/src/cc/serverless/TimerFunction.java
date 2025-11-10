package cc.serverless;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import cc.utils.AuctionData;
import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

import cc.cache.RedisCache;
import redis.clients.jedis.Jedis;

/**
 * Azure Functions with Timer Trigger.
 */
public class TimerFunction {
    @FunctionName("periodic-compute")
    public void cosmosFunction( @TimerTrigger(name = "periodicSetTime", 
    								schedule = "0 0 6,18 * * *")
    				String timerInfo,
    				ExecutionContext context) {


		String endpoint = System.getenv("CosmosEndpoint");
		String key = System.getenv("CosmosKey");

		CosmosClient client = new CosmosClientBuilder()
				.endpoint(endpoint)
				.key(key)
				.consistencyLevel(ConsistencyLevel.EVENTUAL)
				.buildClient();

		CosmosContainer container = client.getDatabase("LegoDB").getContainer("Auctions");

		// Example: update a specific document
		try {

			//container.queryItems(new SqlQuerySpec("SELECT * FROM c WHERE c.isClosed = false", Arrays.asList(new SqlParameter("@"))))
			CosmosPagedIterable<AuctionData> iter = container.queryItems("SELECT * FROM c WHERE c.isClosed = false",new CosmosQueryRequestOptions(), AuctionData.class);

			Iterator<AuctionData> iterator = iter.iterator();


			Instant currentInstant = Instant.now();

			while (iterator.hasNext()) {
				{
					AuctionData data = iterator.next();


					Instant created = data.getCreatedAt();

					Duration d = Duration.ofDays(data.getEndsIn());
					Instant offseted = created.plus(d);

					if (offseted.isAfter(currentInstant)) {

						CosmosPatchOperations op = CosmosPatchOperations.create();
						op.replace("/isClosed", true);

						container.patchItem(data.getId(), new PartitionKey(data.getProduct()), op, AuctionData.class);
						context.getLogger().info("Updated document " + data.getId() + " in Cosmos DB.");


						try (Jedis jedis = RedisCache.getCachePool().getResource()) {
							jedis.del("auction:"+data.getId());
						}

					}
				}
			}

		} catch (Exception e) {
			context.getLogger().severe("Error updating Cosmos DB: " + e.getMessage());
		} finally {
			client.close();
		}

    }
}
