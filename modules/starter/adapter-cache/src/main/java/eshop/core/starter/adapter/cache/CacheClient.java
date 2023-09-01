package eshop.core.starter.adapter.cache;

import eshop.core.starter.adapter.cache.model.CashObject;
import java.util.List;

public interface CacheClient {

  void addItem(String key, CashObject cashObject);

  List<CashObject> getCartItems(String key);

  void removeItem(String key, String hashKey);

  void removeCart(String key);

}
