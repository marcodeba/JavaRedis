import redis.clients.jedis.Jedis;
import redis.util.RedisClient;

public class RedisTest {

	public static void main(String[] args) {
		
		Jedis jedis = null;
		try {
			jedis = RedisClient.getClient();
			String key = "mkey";
			jedis.set(key, "hello,redis!");
            String v = jedis.get(key);
            String k2 = "count";
            jedis.incr(k2);
            jedis.incr(k2);
            System.out.println(v);
            System.out.println(jedis.get(k2));
		} finally {
			if (jedis != null) {
				jedis.disconnect();
			}
		}
	}
}
