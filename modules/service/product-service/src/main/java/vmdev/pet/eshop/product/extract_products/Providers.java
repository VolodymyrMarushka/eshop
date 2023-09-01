package vmdev.pet.eshop.product.extract_products;

import eshop.core.starter.adapter.catalog.Catalog;
import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonMapper;
import com.github.hrytsenko.jsondata.JsonResources;
import java.util.List;
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

    private static final JsonMapper<Catalog.StoreQuery> REQUEST_TO_STORE_QUERY = JsonMapper.create(
        JsonResources.readResource("/vmdev/pet/eshop/product/extract_products/request-to-find-query.mapper.json"), Catalog.StoreQuery::new);

    Catalog.Manager productCatalog;

    List<JsonBean> find(Model.Request request) {
      return productCatalog.find(REQUEST_TO_STORE_QUERY.map(request)).getResults();

    }

  }

}
