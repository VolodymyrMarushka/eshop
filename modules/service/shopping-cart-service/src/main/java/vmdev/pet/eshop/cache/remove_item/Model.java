package vmdev.pet.eshop.cache.remove_item;

import com.github.hrytsenko.jsondata.JsonEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Model {

  static class Request extends JsonEntity<Request> {

    String getCartId() {
      return getString("cart.id");
    }

    String getItemId() {
      return getString("item.id");
    }

  }

}
