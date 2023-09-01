package vmdev.pet.eshop.product.add_product;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Handler {

  Providers.ProductCatalog productCatalog;

  void handle(Model.Request request) {
    log.info("Add product '{}'", request);
    productCatalog.create(request.as(Model.Product::new));
  }

}
