package redis.util;

import redis.clients.jedis.Jedis;

public class RedisClient {
    public static Jedis jedis = null;

    public static Jedis getClient() {
        if (jedis == null) {
            jedis = new Jedis(Constant.HOST, Constant.PORT);
        }
        return jedis;
    }
}
