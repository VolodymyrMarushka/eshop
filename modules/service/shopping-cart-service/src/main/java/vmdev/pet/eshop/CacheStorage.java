package vmdev.pet.eshop;

import eshop.core.starter.adapter.cache.CacheClient;
import eshop.core.starter.adapter.cache.CacheFactory;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheStorage {


  @Bean
  public CacheClient itemCacheStore(CacheFactory cacheFactory,
      @Value("${vmdev.pet.eshop.provider.cache-storage.redis.keyspace.ttl}") Duration ttl) {
    return cacheFactory.createClient("cart", ttl);
  }


}
