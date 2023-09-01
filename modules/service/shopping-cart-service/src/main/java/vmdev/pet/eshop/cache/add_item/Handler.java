package vmdev.pet.eshop.cache.add_item;

import com.github.hrytsenko.jsondata.JsonBean;
import eshop.core.starter.adapter.cache.model.CashObject;
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
    itemStore.addItem(request.getCartId(), new CashObject(item.getId(), item.as(JsonBean::new)));
  }

}
