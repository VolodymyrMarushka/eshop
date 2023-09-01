package vmdev.pet.eshop.cache.remove_item;

import com.github.hrytsenko.jsondata.springboot.web.ValidateRequest;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @DeleteMapping(path = "/cart/remove-item", produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidateRequest("/vmdev/pet/eshop/cache/remove_item/request_schema.json")
    public void handle(@RequestBody Model.Request request) {
      handler.handle(request);
    }

  }

}
