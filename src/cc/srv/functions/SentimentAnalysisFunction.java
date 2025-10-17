package cc.srv.functions;
import cc.utils.EnvLoader;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.azure.ai.textanalytics.TextAnalyticsClient;
import com.azure.ai.textanalytics.TextAnalyticsClientBuilder;
import com.azure.ai.textanalytics.models.DocumentSentiment;
import com.azure.ai.textanalytics.models.SentimentConfidenceScores;
import com.azure.core.credential.AzureKeyCredential;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

public class SentimentAnalysisFunction {
    private final ObjectMapper mapper = new ObjectMapper();
    private TextAnalyticsClient textAnalyticsClient;
    private JedisPool redisPool;

    public SentimentAnalysisFunction() {
        String endpoint = EnvLoader.GetTextAnalyticEndpoint();
        String apiKey = EnvLoader.GetTextAnalyticKey();
        String redisHost = "localhost"; // To be externalized

        this.redisPool = new JedisPool(buildPoolConfig(), redisHost, 6379);
        this.textAnalyticsClient = new TextAnalyticsClientBuilder()
                .credential(new AzureKeyCredential(apiKey))
                .endpoint(endpoint)
                .buildClient();
    }

    @FunctionName("AnalyzeSentiment")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION, route = "sentiment/{language?}")
                    HttpRequestMessage<Optional<String>> request,
            @BindingName("language") String language,
            final ExecutionContext context) {

        context.getLogger().info("Sentiment analysis with Azure AI and Redis Cache");

        try {
            String comment = extractComment(request);
            if (comment.isEmpty()) {
                return createResponse(request, HttpStatus.BAD_REQUEST, "Comment is required");
            }

            String lang = (language != null) ? language : "en";
            String cacheKey = generateCacheKey(comment, lang);

            SentimentResult cachedResult = getFromCache(cacheKey);
            if (cachedResult != null) {
                context.getLogger().info("Result retrieved from cache");
                return createResponse(request, HttpStatus.OK, cachedResult);
            }

            SentimentResult result = analyzeWithAzureAI(comment);
            storeInCache(cacheKey, result);
            return createResponse(request, HttpStatus.OK, result);

        } catch (Exception e) {
            context.getLogger().severe("Error: " + e.getMessage());
            return createResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Analysis error: " + e.getMessage());
        }
    }

    private String extractComment(HttpRequestMessage<Optional<String>> request) throws Exception {
        String requestBody = request.getBody().orElse("");
        Map<String, String> body = mapper.readValue(requestBody, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        return body.getOrDefault("comment", "").trim();
    }

    private HttpResponseMessage createResponse(HttpRequestMessage<?> request, HttpStatus status, Object responseBody) {
        try {
            return request.createResponseBuilder(status)
                    .body(mapper.writeValueAsString(responseBody))
                    .header("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Critical error\"}")
                    .build();
        }
    }

    public SentimentResult analyzeWithAzureAI(String comment) {
        DocumentSentiment documentSentiment = textAnalyticsClient.analyzeSentiment(comment);
        SentimentConfidenceScores scores = documentSentiment.getConfidenceScores();
        double score = scores.getPositive();
        double confidence = Math.max(Math.max(scores.getPositive(), scores.getNegative()), scores.getNeutral());
        return new SentimentResult(documentSentiment.getSentiment().toString(), score, confidence);
    }

    private SentimentResult getFromCache(String cacheKey) {
        try (Jedis jedis = redisPool.getResource()) {
            String cachedValue = jedis.get(cacheKey);
            return cachedValue != null ? mapper.readValue(cachedValue, SentimentResult.class) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void storeInCache(String cacheKey, SentimentResult result) {
        try (Jedis jedis = redisPool.getResource()) {
            jedis.setex(cacheKey, 3600, mapper.writeValueAsString(result));
        } catch (Exception e) {
            // Handle cache error
        }
    }

    private String generateCacheKey(String comment, String language) {
        return language + ":" + Integer.toHexString(comment.hashCode());
    }

    private JedisPoolConfig buildPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        return poolConfig;
    }

    public static class SentimentResult {
        public String sentiment;
        public double score;
        public double confidence;

        public SentimentResult(String sentiment, double score, double confidence) {
            this.sentiment = sentiment;
            this.score = score;
            this.confidence = confidence;
        }
    }
}
