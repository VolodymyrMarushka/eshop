package vmdev.pet.eshop.cache.remove_cart;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Handler {

  Providers.ItemStore itemStore;

  void handle(String cartId) {
    log.info("Remove cart with id '{}'", cartId);
    itemStore.removeCart(cartId);
  }

}
