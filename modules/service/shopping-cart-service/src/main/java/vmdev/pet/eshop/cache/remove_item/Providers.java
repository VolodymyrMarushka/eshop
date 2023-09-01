package vmdev.pet.eshop.cache.remove_item;

import eshop.core.starter.adapter.cache.CacheClient;
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

    void removeItem(Model.Request request) {
      itemCacheStore.removeItem(request.getCartId(), request.getItemId());
    }

  }

}
