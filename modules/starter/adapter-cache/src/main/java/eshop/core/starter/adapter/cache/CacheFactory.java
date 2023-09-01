package eshop.core.starter.adapter.cache;

import java.time.Duration;

public interface CacheFactory {

  CacheClient createClient(String keyspace, Duration ttl);

}
