package vmdev.pet.eshop.cache.add_item;

import eshop.core.starter.adapter.cache.CacheClient;
import eshop.core.starter.adapter.cache.model.CashObject;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@UtilityClass
public class Providers {

  @Component
  @Slf4j
  @AllArgsConstructor
  static class ItemStore {

    CacheClient itemCacheStore;

    void addItem(String key, CashObject cashObject) {
      itemCacheStore.addItem(key, cashObject);
    }

  }

}
