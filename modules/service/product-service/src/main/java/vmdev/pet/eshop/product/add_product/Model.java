package vmdev.pet.eshop.product.add_product;

import com.github.hrytsenko.jsondata.JsonEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Model {

  static class Request extends JsonEntity<Request> {
  }

  static class Product extends JsonEntity<Product> {

    String getId() {
      return getString("id");
    }

  }

}
