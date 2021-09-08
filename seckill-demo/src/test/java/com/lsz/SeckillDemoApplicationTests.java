package com.lsz;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillDemoApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript redisScript;


    @Test
    public void lock(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 120, TimeUnit.SECONDS);
        if(isLock){
            valueOperations.set("name","xxxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(result);
        }else {
            System.out.println("有线程正在使用，请稍后。。。");
        }
    }

    @Test
    public int[][] merge() {
        System.out.println(new int [0][2]);
        return new int [0][2];
    }

}
