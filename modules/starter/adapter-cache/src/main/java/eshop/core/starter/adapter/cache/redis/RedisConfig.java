package eshop.core.starter.adapter.cache.redis;

import eshop.core.starter.adapter.cache.CacheFactory;
import eshop.core.starter.adapter.cache.model.CashObject;
import eshop.core.starter.adapter.cache.serializer.JsonBeanRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

  @Bean
  @Primary
  public static LettuceConnectionFactory redisConnectionFactory(
      @Value("${vmdev.pet.eshop.provider.cache-storage.redis.server.host}") String host,
      @Value("${vmdev.pet.eshop.provider.cache-storage.redis.server.port}") int port
  ) {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setPort(port);
    config.setHostName(host);
    return new LettuceConnectionFactory(config);
  }

  @Bean
  @DependsOn("redisConnectionFactory")
  public RedisTemplate<String, CashObject> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, CashObject> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new JsonBeanRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  public CacheFactory redisCacheFactory(RedisTemplate<String, CashObject> redisTemplate) {
    return new RedisFactory(redisTemplate);
  }

}
