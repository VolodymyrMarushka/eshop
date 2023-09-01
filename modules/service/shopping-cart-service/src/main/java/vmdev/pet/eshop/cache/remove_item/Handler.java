package vmdev.pet.eshop.cache.remove_item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Handler {

  Providers.ItemStore itemStore;

  void handle(Model.Request request) {
    log.info("Remove item '{}'", request.getItemId());
    itemStore.removeItem(request);
  }

}
