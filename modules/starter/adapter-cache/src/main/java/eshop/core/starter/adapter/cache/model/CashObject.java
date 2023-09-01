package eshop.core.starter.adapter.cache.model;

import com.github.hrytsenko.jsondata.JsonBean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CashObject {

  String hashKey;
  JsonBean hashValue;

}
