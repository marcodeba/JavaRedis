package redis.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.CountDownLatch;

public class JedisPoolTest {
    private static JedisPoolConfig config;//Jedis客户端池配置

    private static JedisPool pool;//Jedis客户端池

    static {
        config = new JedisPoolConfig();
        //config.setMaxActive(60000);
        config.setMaxIdle(1000);
        //config.setMaxWait(10000);
        config.setTestOnBorrow(true);
        pool = new JedisPool(config, Constant.HOST, Constant.PORT);
    }

    /**
     * 单笔测试(用池)
     *
     * @param count
     */
    public static void testWithPool(int count) {
        for (int i = 0; i < count; i++) {
            Jedis jr = null;
            try {
                jr = pool.getResource();
                testOnce(jr);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jr != null) {
                    pool.close();
                }
            }
        }
    }

    public static void paiallelTestWithPool(int paiallel, int count) {
        //用该对象保证所线程都完成主线程才退出
        CountDownLatch cd = new CountDownLatch(paiallel);
        long start = System.currentTimeMillis();
        Thread[] ts = new Thread[paiallel];
        for (int i = 0; i < paiallel; i++) {
            ts[i] = new Thread(new WorkerWithPool(cd, count));
            ts[i].start();
        }

        try {
            cd.await();//等待所有子线程完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Pool useTime:" + (System.currentTimeMillis() - start));
        pool.destroy();
    }

    private static void testOnce(Jedis jr) {
        System.out.println(jr.incr("incrTest"));
    }

    public static void main(String[] args) {
        paiallelTestWithPool(100, 1000);
    }

    public static class WorkerWithPool implements Runnable {
        private CountDownLatch cd;
        private int count;

        public WorkerWithPool(CountDownLatch cd, int count) {
            this.cd = cd;
            this.count = count;
        }

        public void run() {
            try {
                testWithPool(this.count);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cd.countDown();
            }
        }
    }
}
