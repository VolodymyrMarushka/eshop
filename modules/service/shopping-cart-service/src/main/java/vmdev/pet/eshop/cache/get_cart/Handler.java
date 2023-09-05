package vmdev.pet.eshop.cache.get_cart;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonMapper;
import com.github.hrytsenko.jsondata.JsonResources;
import com.github.hrytsenko.jsondata.springboot.error.ServiceException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Handler {

  private static final JsonMapper<Model.Cart> TO_CART = JsonMapper.create(
      JsonResources.readResource("/vmdev/pet/eshop/cache/get_cart/to-cart.mapper.json"), Model.Cart::new);

  Providers.ItemStore itemStore;
  Providers.ProductFabric productFabric;

  Model.Cart handle(String cartId) {
    List<Model.Item> items = itemStore.getCartItems(cartId);
    if (items.isEmpty()) {
      log.info("Cart with id '{}' not found or expired", cartId);
      throw new ServiceException.NotFound();
    }

    List<JsonBean> jsonBeans = productFabric.extractProducts(items);
    JsonBean data = new JsonBean()
        .putEntities("items", items)
        .putString("cartId", cartId)
        .putEntities("products", jsonBeans);
    return TO_CART.map(data);
  }

}
