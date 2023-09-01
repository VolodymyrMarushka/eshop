package vmdev.pet.eshop.cache.get_cart;

import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping(path = "/cart/{cartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Model.Cart handle(@PathVariable("cartId") int cartId) {
      return handler.handle(String.valueOf(cartId));
    }

  }

}
