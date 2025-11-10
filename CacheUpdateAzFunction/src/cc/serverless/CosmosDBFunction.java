package cc.serverless;

import java.util.*;

import cc.utils.SessionData;
import cc.utils.UserProfile;
import com.azure.core.util.paging.ContinuablePagedIterable;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.CosmosDBTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

import cc.cache.RedisCache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

/**
 * Azure Functions with Timer Trigger.
 */
public class CosmosDBFunction {

	private static final String redisUrl = System.getenv("RedisUrl"); // redis://:password@host:6380
	private static final JedisPooled jedis = new JedisPooled(redisUrl);
	private static final ObjectMapper mapper = new ObjectMapper();
    @FunctionName("cosmosDBSessionUpdater")
    public void run(@CosmosDBTrigger(name = "cosmosSessionUpdater",
    										databaseName = "LegoDB",
    										containerName = "Users",
											leaseContainerName="leases",
    										createLeaseContainerIfNotExists = true,
    										connection = "AzureCosmosDBConnection")
        							List<UserProfile> users,
        							final ExecutionContext context ) {


		List<Map<String, Object>> sessionEntries = new ArrayList<>();

		try {

			CosmosClient client = new CosmosClientBuilder()
					.endpoint(System.getenv("CosmosEndpoint"))
					.key(System.getenv("CosmosKey"))
					.buildClient();

			CosmosContainer container = client.getDatabase("LegoDB").getContainer("Sessions");

			Set<String> userIdSets = new HashSet<String>();

			Map<String,Object> map = new HashMap<>();

			for (UserProfile user : users) {

				String userId = user.getId();


				userIdSets.add(userId);

				map.put(userId,user);
			}

			String[] userIdsArray = userIdSets.toArray(String[]::new);

			SqlQuerySpec spec = new SqlQuerySpec("SELECT * FROM c WHERE ARRAY_CONTAINS(@array,c.user)", Arrays.asList(new SqlParameter("@array",userIdsArray)));

			CosmosPagedIterable sessionsIterab = container.queryItems(spec,new CosmosQueryRequestOptions(), SessionData.class);
			Iterator<SessionData> iterator = sessionsIterab.iterator();

			while (iterator.hasNext()) {
				SessionData p = iterator.next();

				UserProfile profile = (UserProfile) map.get(p.getUser());

				if (profile != null) {
					CosmosPatchOperations op = CosmosPatchOperations.create();
					op.replace("/profile", map.get(p.getUser()));

					container.patchItem(p.getId(), new PartitionKey(p.getUser()), op, SessionData.class);
					jedis.setex("session:"+p.getId(), 120, mapper.writeValueAsString(profile));

					context.getLogger().info("Cached user " + p.getUser() + " session ("+p.getId()+") in Redis.");
				}
			}


			// Write all audit entries to the UserChanges container

			context.getLogger().info("Updated " + sessionEntries.size() + " documents to Sessions container.");

		} catch (Exception e) {
			context.getLogger().severe("Error processing Cosmos DB changes: " + e.getMessage());
		}
    }

}
