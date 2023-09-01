package eshop.core.starter.adapter.cache.redis;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonParser;
import eshop.core.starter.adapter.cache.CacheClient;
import eshop.core.starter.adapter.cache.model.CashObject;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    log.info("Put '{}' into cache", extendKey(key));

    byte[] rawKey = extendKey(key).getBytes();
    byte[] rawHashKey = cacheObj.getHashKey().getBytes();
    byte[] rawHashValue = cacheObj.getHashValue().asString().getBytes();
    redisTemplate.execute((connection) -> {
      connection.hSet(rawKey, rawHashKey, rawHashValue);
      connection.expire(rawKey, ttl.toSeconds());
      return null;
    }, true);
  }

  @Override
  public List<CashObject> getCartItems(String key) {
    Assert.notNull(key, "non null key required");

    log.info("Get '{}' from cache", extendKey(key));
    Map<byte[], byte[]> rawCart = redisTemplate.execute(connection ->
        connection.hGetAll(extendKey(key).getBytes()), true);
    if (rawCart == null || rawCart.isEmpty()) {
      return Collections.emptyList();
    }

    return rawCart.entrySet().stream()
        .map(entry -> {
          String hashKey = new String(entry.getKey());
          String hashValue = new String(entry.getValue());
          return new CashObject(hashKey, JsonParser.stringToEntity(hashValue, JsonBean::new));
        }).collect(Collectors.toList());
  }

  public void removeCart(String key) {
    Assert.notNull(key, "non null key required");
    // ToDo check if cart exist

    log.info("Remove cart '{}'", extendKey(key));
    redisTemplate.execute(connection ->
        connection.del(extendKey(key).getBytes()), true);
  }

  @Override
  public void removeItem(String key, String hashKey) {
    Assert.notNull(key, "non null key required");
    //ToDo check if item exist

    log.info("Remove item '{}'", hashKey);
    redisTemplate.execute(connection ->
        connection.hDel(extendKey(key).getBytes(), hashKey.getBytes()), true);
  }


  private String extendKey(String key) {
    return String.format("%s:%s", namespace, key);
  }

}

