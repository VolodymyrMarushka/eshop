package vmdev.pet.eshop.cache.remove_cart;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@UtilityClass
public class Trigger {

  @RestController
  @Slf4j
  @RequestMapping(path = "/api/v1")
  @AllArgsConstructor
  static class Web {

    Handler handler;

    @DeleteMapping(path = "/cart/{cartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void handle(@PathVariable String cartId) {
      handler.handle(cartId);
    }

  }

}
