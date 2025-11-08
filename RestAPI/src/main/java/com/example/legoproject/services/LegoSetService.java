package com.example.legoproject.services;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.legoproject.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.ZAddParams;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class LegoSetService {

    private final CosmosContainer container;
    private final CosmosContainer commentContainer;

    private final UserService userService;

    private final JedisPool jedisPool;

    @Autowired
    private ObjectMapper oMapper;

    @Autowired
    public LegoSetService(CosmosDatabase db, UserService us, JedisPool pool) {
        this.container = db.getContainer("LegoSet");
        this.commentContainer = db.getContainer("Comments");
        this.userService = us;
        this.jedisPool = pool;
    }

    public Comment postComment(UserProfile user,String content,String legoSet) {

        if (user == null)
            return null;

        try (Jedis jedis = jedisPool.getResource()) {

            Comment cacheComment = new Comment(UUID.randomUUID().toString(), user, content, Instant.now());

            String comm = null;
            try {
                comm =oMapper.writeValueAsString(cacheComment);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize JSON ", e);
            }

            jedis.lpushx("Comments:"+legoSet,comm);

        }


        SqlQuerySpec querySpec = new SqlQuerySpec("SELECT * FROM c WHERE c.id = @id",
                Arrays.asList(new SqlParameter("@id", legoSet)));
        CosmosPagedIterable<LegoSet> legoSets = container.queryItems(querySpec,new CosmosQueryRequestOptions(), LegoSet.class);

        if (legoSets.iterator().hasNext()) {
            CommentData comment = new CommentData(UUID.randomUUID().toString(), legoSet, user.getId(), content, Instant.now());

            commentContainer.createItem(comment);

            return new Comment(comment.getId(),user,content,Instant.now());

        } else {
            return null;
        }

    }

    public List<Comment> listComments(String legoSet,Instant before) {



        try {


            if (before == null) {
                try (Jedis jedis = jedisPool.getResource()) {

                   // List<String> cachedComments = jedis.zrange("Comments:" + legoSet, 0, -1);

                    List<String> cachedComments = jedis.lrange("Comments:"+legoSet,0,25);

                    if (cachedComments.size() > 0) {
                        List<Comment> comments = cachedComments.stream().map(c -> {

                            Comment com = null;
                            try {
                                com = oMapper.readValue(c, Comment.class);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to deserialize JSON ", e);
                            }

                            return com;
                        }).toList();

                        return comments;
                    }
                }
            }

            SqlQuerySpec commentQuerySpec;

            if (before != null) {
                long ts = before.getEpochSecond();

                commentQuerySpec = new SqlQuerySpec("SELECT TOP @lim * FROM c WHERE c.productId = @productId AND c.timestamp < @timestamp ORDER BY c.timestamp DESC", Arrays.asList(new SqlParameter("@productId",legoSet),new SqlParameter("@timestamp",ts), new SqlParameter("@lim",25)));

            } else {
                commentQuerySpec = new SqlQuerySpec("SELECT TOP @lim * FROM c WHERE c.productId = @productId ORDER BY c.timestamp DESC", Arrays.asList(new SqlParameter("@productId",legoSet), new SqlParameter("@lim",25)));
            }


            CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

            CosmosPagedIterable<CommentData> commentResults = commentContainer.queryItems(commentQuerySpec,options, CommentData.class);

            Set<String> userIds = commentResults.stream()
                    .map(CommentData::getAuthor)
                    .collect(Collectors.toSet());

            UserProfile[] payload = userService.getUserProfiles(userIds.toArray(String[]::new));

            Map<String,UserProfile> usersById = Arrays.stream(payload).collect(Collectors.toMap(u -> u.getId(), u -> u));

           List<Comment> result = commentResults.stream()
                    .map(commentModel -> {

                        UserProfile userProfile = usersById.getOrDefault(commentModel.getAuthor(),new UserProfile(null,"Deleted User",Instant.now(),null,0));
                        return new Comment(commentModel.getId(),userProfile,commentModel.getContent(), commentModel.getTimestamp());
                    })
                    .collect(Collectors.toList());

           if (before == null && result.size() > 0) {
               try (Jedis jedis = jedisPool.getResource()) {


                   String[] cachedList = result.stream().map(c -> {
                       try {

                           return oMapper.writeValueAsString(c);
                       } catch (Exception e) {
                           throw new RuntimeException("Failed to deserialize JSON ", e);
                       }
                   }).toArray(String[]::new);

                   jedis.rpush("Comments:"+legoSet,cachedList);
                   jedis.expire("Comments:"+legoSet,3600);


               }

           }


            return result;

        } catch (CosmosException e) {
            System.out.println("Cosmos Err: "+e);

            return null;
        }
        catch (Exception e) {
            System.out.println("Err: "+e);

            return null;
        }

    }

    public LegoSet createLegoSet(LegoSet m) {

        LegoSet createdModel = new LegoSet(UUID.randomUUID().toString(),m.getName(),m.getDescription(),m.getYearOfProduction(),m.getPhotos());
        try {

            container.createItem(createdModel);

            return createdModel;

        } catch (CosmosException e) {
            System.err.println("Cosmos DB error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error: "+e.getMessage());

            return null;
        }


    }

}
