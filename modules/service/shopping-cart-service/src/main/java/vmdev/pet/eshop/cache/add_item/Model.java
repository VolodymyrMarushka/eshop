package vmdev.pet.eshop.cache.add_item;

import com.github.hrytsenko.jsondata.JsonEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Model {

  static class Request extends JsonEntity<Request> {

    String getCartId() {
      return getString("cart.id");
    }

    Item getItem() {
      return getEntity("item", Item::new);
    }

  }

  static class Item extends JsonEntity<Model.Item> {

    String getId() {
      return getString("id");
    }

  }

}
