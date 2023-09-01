package eshop.core.starter.adapter.cache.redis;

import eshop.core.starter.adapter.cache.CacheClient;
import eshop.core.starter.adapter.cache.CacheFactory;
import eshop.core.starter.adapter.cache.model.CashObject;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@AllArgsConstructor
@RequiredArgsConstructor
public class RedisFactory implements CacheFactory {

  RedisTemplate<String, CashObject> redisTemplate;


  @Override
  public CacheClient createClient(String keyspace, Duration ttl) {
    return new RedisClient(keyspace, ttl, redisTemplate);
  }

}
