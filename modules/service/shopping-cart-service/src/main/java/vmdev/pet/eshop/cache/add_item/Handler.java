package vmdev.pet.eshop.cache.add_item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Handler {

  Providers.ItemStore itemStore;

  void handle(Model.Request request) {
    Model.Item item = request.getItem();
    log.info("Add item '{}' to cache", item);
    itemStore.addItem(request.getCartId(), item);
  }

}
