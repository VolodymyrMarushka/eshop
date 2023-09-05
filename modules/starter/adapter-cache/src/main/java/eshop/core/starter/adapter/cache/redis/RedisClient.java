package eshop.core.starter.adapter.cache.redis;

import com.github.hrytsenko.jsondata.JsonBean;
import eshop.core.starter.adapter.cache.CacheClient;
import eshop.core.starter.adapter.cache.model.CashObject;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

@AllArgsConstructor
@Slf4j
public class RedisClient implements CacheClient {

  String namespace;
  Duration ttl;
  RedisTemplate<String, CashObject> redisTemplate;

  @Override
  public void addItem(String key, CashObject cacheObj) {
    log.info("Put '{}' into cache", key);
    redisTemplate.opsForHash().put(extendKey(key), cacheObj.getHashKey(), cacheObj.getHashValue());
    redisTemplate.expire(extendKey(key), ttl);
  }

  @Override
  public List<CashObject> getCartItems(String key) {
    Assert.notNull(key, "non null key required");
    HashOperations<String, String, JsonBean> hashOperations = redisTemplate.opsForHash();

    log.info("Get '{}' from cache", extendKey(key));
    return hashOperations.entries(extendKey(key)).entrySet().stream()
        .map(entry -> new CashObject(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  public void removeCart(String key) {
    log.info("Remove cart '{}'", extendKey(key));
    redisTemplate.delete(extendKey(key));
  }

  @Override
  public void removeItem(String key, String hashKey) {
    log.info("Remove item '{}'", hashKey);
    redisTemplate.opsForHash().delete(extendKey(key), hashKey);
  }


  private String extendKey(String key) {
    return String.format("%s:%s", namespace, key);
  }

}

