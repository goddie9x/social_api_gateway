package com.godsocial.ApiGateway.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    public void setValue(String key, String value){
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }
    public String getValue(String key){
        ValueOperations<String,String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }
    public void delete(String key){
        redisTemplate.delete(key);
    }
}
