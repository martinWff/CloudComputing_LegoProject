package cc.srv.db;

import cc.utils.EnvLoader;
import jakarta.enterprise.context.ApplicationScoped;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@ApplicationScoped
public class RedisConnection {

    private static final String HOSTNAME = EnvLoader.getVariable("redis_hostname");

    private static final String KEY = EnvLoader.getVariable("redis_key");

    private static JedisPool pool;

    public synchronized static JedisPool getCachePool() {
        if (pool != null)
            return pool;

        final JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);

        poolConfig.setBlockWhenExhausted(true);

        pool = new JedisPool(poolConfig,HOSTNAME,6380,1000,KEY,true);

        return pool;

    }


}
