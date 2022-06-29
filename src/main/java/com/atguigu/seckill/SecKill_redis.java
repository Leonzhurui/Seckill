package com.atguigu.seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;

public class SecKill_redis {

    // 秒杀过程
    public static boolean doSecKill(String uid, String prodId) {
        // 1.uid和prodid非空判断
        if (uid == null || prodId == null) {
            return false;
        }
        // 2.连接redis
//        Jedis jedis = new Jedis("192.168.1.102", 6379);
        // 通过连接池得到jedis对象
        JedisPool jedisPool = JedisPoolUtil.getJedisPoolInstance();
        Jedis jedis = jedisPool.getResource();

        // 3.拼接key
        // 3.1库存key
        String kcKey = "sk:" + prodId + ":qt";
        // 3.2秒杀成功用户key
        String userKey = "sk:" + prodId + ":user";


        // 监视库存
        jedis.watch(kcKey);

        // 4.获取库存，如果库存null，秒杀还没有开始
        String kc = jedis.get(kcKey);
        if (kc == null) {
            System.out.println("秒杀还没有开始，请等待");
            jedis.close();
            return false;
        }

        // 5.判断用户是否重复秒杀操作
        if (jedis.sismember(userKey, uid)) {
            System.out.println("已经秒杀成功了，不能重复秒杀");
            jedis.close();
            return false;
        }

        // 6.判断如果商品数量，库存数量小于1，秒杀结束

        if (Integer.parseInt(kc) <= 0) {
            System.out.println("秒杀已经结束了");
            jedis.close();
            return false;
        }

        // 7.秒杀过程
        // 使用事务
        Transaction multi = jedis.multi();

        // 组队操作
        multi.decr(kcKey);
        multi.sadd(userKey, uid);

        // 执行
        List<Object> results = multi.exec();
        if (results == null || results.size() == 0) {
            System.out.println("秒杀失败了...");
            jedis.close();
            return false;
        }

        // 7.1 库存-1
//        jedis.decr(kcKey);
        // 7.2 把秒杀陈功用户添加清单里面
//        jedis.sadd(userKey, uid);
        System.out.println("秒杀成功了...");
        jedis.close();
        return true;
    }


}
