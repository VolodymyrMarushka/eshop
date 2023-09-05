package vmdev.pet.eshop.cache.add_item;

import com.github.hrytsenko.jsondata.JsonBean;
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

    void addItem(String key, Model.Item item) {
      itemCacheStore.addItem(key, new CashObject(item.getId(), item.as(JsonBean::new)));
    }

  }

}
