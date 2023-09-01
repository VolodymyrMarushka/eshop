package vmdev.pet.eshop.product.extract_products;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.springboot.web.ValidateRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(path = "/product/extract-products", produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidateRequest(value = "/vmdev/pet/eshop/product/extract_products/request_schema.json")
    public List<JsonBean> handle(@RequestBody Model.Request request) {
      return handler.handle(request);
    }

  }

}
