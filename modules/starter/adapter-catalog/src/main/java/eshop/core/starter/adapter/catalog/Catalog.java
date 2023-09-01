package eshop.core.starter.adapter.catalog;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonEntity;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.springframework.cloud.sleuth.annotation.NewSpan;

@UtilityClass
public class Catalog {

  public interface Manager {

    @NewSpan("/store/create")
    void create(String key, JsonEntity<?> object);

    @NewSpan("/store/find")
    SearchResult find(StoreQuery query);

    @NewSpan("/store/scan")
    Stream<JsonBean> scan(Catalog.StoreQuery query);

    @NewSpan("/store/update-by-key")
    void update(String key, JsonEntity<?> object);

    @NewSpan("/store/update-by-key-version")
    void update(String key, Long version, JsonEntity<?> object);

    @NewSpan("/store/update-by-query")
    void update(StoreQuery query, JsonEntity<?> object);

    @NewSpan("/store/save")
    void save(String key, JsonEntity<?> object);

    @NewSpan("/store/delete")
    void delete(StoreQuery query);

    void doInTransaction(Runnable runnable);

  }

  public static class SearchResult extends JsonEntity<SearchResult> {

    public static SearchResult of(String total, List<JsonBean> results) {
      return new SearchResult()
          .putString("total", total)
          .putEntities("results", results);
    }

    public String getTotal() {
      return getString("total");
    }

    public List<JsonBean> getResults() {
      return getEntities("results", JsonBean::new);
    }

  }

  public static class StoreQuery extends JsonEntity<StoreQuery> {

    List<SearchField> getSearchFields() {
      return getEntities("searchFields", SearchField::new);
    }

    List<SortField> getSortFields() {
      return getEntities("sortFields", SortField::new);
    }

    List<SelectField> getSelectFields() {
      return getEntities("selectFields", SelectField::new);
    }

    String getOffset() {
      return getString("offset");
    }

    String getLimit() {
      return getString("limit");
    }

    List<ExcludeField> getExcludeFields() {
      return getEntities("excludeFields", ExcludeField::new);
    }

  }

  public static class SearchField extends JsonEntity<SearchField> {

    String getName() {
      return getString("name");
    }

    Object getValue() {
      return getObject("value");
    }

    String getOperator() {
      return getString("operator");
    }

  }

  public static class SortField extends JsonEntity<SortField> {

    String getName() {
      return getString("name");
    }

    String getOrder() {
      return getString("order");
    }

  }

  public static class SelectField extends JsonEntity<SelectField> {

    String getName() {
      return getString("name");
    }

  }

  public static class ExcludeField extends JsonEntity<ExcludeField> {

    String getName() {
      return getString("name");
    }

  }

}
