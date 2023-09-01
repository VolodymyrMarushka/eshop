package vmdev.pet.eshop.cache.get_cart;

import com.github.hrytsenko.jsondata.JsonBean;
import eshop.core.starter.adapter.cache.CacheClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@UtilityClass
public class Providers {

  @Component
  @Slf4j
  @AllArgsConstructor
  static class ItemStore {

    CacheClient itemCacheStore;

    List<Model.Item> getCartItems(String cartId) {
      return itemCacheStore.getCartItems(cartId).stream()
          .map(cashObj -> cashObj.getHashValue().as(Model.Item::new))
          .collect(Collectors.toList());
    }

  }

  @Component
  @Slf4j
  @AllArgsConstructor
  static class ProductFabric {

    Providers.ProductFabric.Client client;

    List<JsonBean> extractProducts(List<Model.Item> items) {
      List<String> itemIds = items.stream().map(Model.Item::getId).collect(Collectors.toList());
      JsonBean request = new JsonBean()
          .putList("products", itemIds);
      return client.extractProducts(request);
    }

    @FeignClient(name = "${vmdev.pet.eshop.product-fabric.http.client.name}",
        url = "${vmdev.pet.eshop.product-fabric.http.client.url}")
    interface Client {

      @PostMapping(value = "/product/extract-products", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
      List<JsonBean> extractProducts(JsonBean request);

    }

  }

}
