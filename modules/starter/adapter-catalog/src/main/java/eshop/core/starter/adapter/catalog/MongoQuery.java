package eshop.core.starter.adapter.catalog;

import com.github.hrytsenko.jsondata.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.bson.Document;
import org.bson.conversions.Bson;

@UtilityClass
class MongoQuery {

  static final String ID = "_id";
  static final String VERSION = "_version";

  private static final Map<String, Function<Catalog.SortField, Bson>> SORTS;

  static {
    SORTS = new ConcurrentHashMap<>();
    SORTS.put("asc", field -> Sorts.ascending(field.getName()));
    SORTS.put("desc", field -> Sorts.descending(field.getName()));
  }

  private static final Map<String, Function<Catalog.SearchField, Bson>> FILTERS;

  static {
    FILTERS = new ConcurrentHashMap<>();
    FILTERS.put("eq", field -> Filters.eq(field.getName(), field.getValue()));
    FILTERS.put("neq", field -> Filters.ne(field.getName(), field.getValue()));
    FILTERS.put("gt", field -> Filters.gt(field.getName(), field.getValue()));
    FILTERS.put("lt", field -> Filters.lt(field.getName(), field.getValue()));
    FILTERS.put("in", field -> Filters.in(field.getName(), (Iterable<?>) field.getValue()));
    FILTERS.put("like", field -> Filters.regex(field.getName(), ".*" + field.getValue() + ".*"));
    FILTERS.put("empty", field -> Filters.exists(field.getName(), false));
    FILTERS.put("or", MongoQuery::or);
  }

  @SuppressWarnings("unchecked")
  private static Bson or(Catalog.SearchField field) {
    List<Bson> filters = ((List<Map<String, ?>>) field.getValue()).stream()
        .map(JsonParser.fromMapTo(Catalog.SearchField::new))
        .map(MongoQuery::filterBy)
        .collect(Collectors.toList());
    return Filters.or(filters);
  }

  static Bson filterById(String id) {
    return Filters.eq(ID, id);
  }

  static Bson filterByIdVersion(String id, Long version) {
    return Filters.and(Filters.eq(ID, id), Filters.eq(VERSION, version));
  }

  static Bson filterBy(Catalog.StoreQuery query) {
    List<Bson> filters = query.getSearchFields().stream()
        .map(MongoQuery::filterBy)
        .collect(Collectors.toList());
    if (filters.isEmpty()) {
      return Document.parse("{}");
    }
    return Filters.and(filters);
  }

  private static Bson filterBy(Catalog.SearchField field) {
    String operator = field.getOperator();
    if (!FILTERS.containsKey(operator)) {
      throw new IllegalArgumentException(String.format("Unknown operator %s for field %s", operator, field.getName()));
    }
    return FILTERS.get(operator).apply(field);
  }

  static Bson selectBy(Catalog.StoreQuery query) {
    List<Bson> projections = query.getSelectFields().stream()
        .map(MongoQuery::selectBy)
        .collect(Collectors.toList());
    projections.add(Projections.excludeId());
    List<String> excludeFieldNames = Optional.ofNullable(query.getExcludeFields()).orElseGet(List::of).stream()
        .map(Catalog.ExcludeField::getName)
        .collect(Collectors.toList());
    if (!excludeFieldNames.isEmpty()) {
      projections.add(Projections.exclude(excludeFieldNames));
    }
    return Projections.fields(projections);
  }

  private static Bson selectBy(Catalog.SelectField field) {
    return Projections.include(field.getName());
  }

  static Bson sortBy(List<Catalog.SortField> fields) {
    if (fields.isEmpty()) {
      return Document.parse("{}");
    }
    List<Bson> sorts = fields.stream()
        .map(MongoQuery::sortBy)
        .collect(Collectors.toList());
    return Sorts.orderBy(sorts);
  }

  private static Bson sortBy(Catalog.SortField field) {
    String order = field.getOrder();
    if (!SORTS.containsKey(order)) {
      throw new IllegalArgumentException(String.format("Unknown order %s for field %s", order, field.getName()));
    }
    return SORTS.get(order).apply(field);
  }

  static Bson updateBy(Map<String, ?> content) {
    List<Bson> updates = FlatStream.from(content)
        .map(entry -> entry.getValue() != null
            ? Updates.set(entry.getKey(), entry.getValue()) : Updates.unset(entry.getKey()))
        .collect(Collectors.toList());
    updates.add(Updates.set(VERSION, CatalogMongo.getVersion()));
    if (updates.isEmpty()) {
      return Document.parse("{}");
    }
    return Updates.combine(updates);
  }

  @UtilityClass
  static class FlatStream {

    private static Stream<Map.Entry<String, ?>> from(Map<String, ?> source) {
      return flatten(entry("", source));
    }

    @SuppressWarnings("unchecked")
    private static Stream<Map.Entry<String, ?>> flatten(Map.Entry<String, ?> parent) {
      if (!(parent.getValue() instanceof Map<?, ?>)) {
        return Stream.of(parent);
      }
      return ((Map<String, ?>) parent.getValue()).entrySet().stream()
          .map(child -> entry(merge(parent.getKey(), child.getKey()), child.getValue()))
          .flatMap(FlatStream::flatten);
    }

    private static AbstractMap.SimpleEntry<String, ?> entry(String key, Object value) {
      return new AbstractMap.SimpleEntry<>(key, value);
    }

    private static String merge(String parent, String child) {
      if (parent.isEmpty()) {
        return child;
      }
      return parent + "." + child;
    }

  }

}
