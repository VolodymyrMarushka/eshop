package vmdev.pet.eshop.product.add_product;

import eshop.core.starter.adapter.catalog.Catalog;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@UtilityClass
public class Providers {

  @Component
  @Slf4j
  @AllArgsConstructor
  static class ProductCatalog {

    Catalog.Manager productCatalog;

    void create(Model.Product product) {
      productCatalog.create(product.getId(), product);
    }

  }

}
