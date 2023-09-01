package vmdev.pet.eshop.product.extract_products;

import com.github.hrytsenko.jsondata.JsonBean;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Handler {

  Providers.ProductCatalog productCatalog;

  List<JsonBean> handle(Model.Request request) {
    log.info("Extract products '{}'", request);
    return productCatalog.find(request);
  }

}
