package vmdev.pet.eshop.cache.get_cart;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonEntity;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Model {

  static class Item extends JsonEntity<Item> {

    String getId() {
      return getString("id");
    }

  }

  static class Cart extends JsonEntity<Cart> {

    static Model.Cart of(String cartId, List<JsonBean> items) {
      return new Cart()
          .putString("cart.id", cartId)
          .putEntities("items", items);
    }

  }

}
