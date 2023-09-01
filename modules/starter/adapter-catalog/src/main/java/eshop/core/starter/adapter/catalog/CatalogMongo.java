package eshop.core.starter.adapter.catalog;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonEntity;
import com.github.hrytsenko.jsondata.JsonParser;
import com.mongodb.MongoException;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;

@Slf4j
@AllArgsConstructor
public class CatalogMongo implements Catalog.Manager, AutoCloseable {

  MongoClient client;
  Collection collection;

  public static CatalogMongo create(Properties properties) {
    MongoClient mongoClient = MongoClients.create(properties.getUri());
    MongoCollection<Document> mongoCollection = mongoClient
        .getDatabase(properties.getDb())
        .getCollection(properties.getCollection());
    Collection collection = Collection.create(mongoCollection);
    return new CatalogMongo(mongoClient, collection);
  }

  @Override
  public void create(String key, JsonEntity<?> object) {
    log.info("Create object '{}'", key);
    Document document = asMongoDocument(key, object);
    document.put(MongoQuery.VERSION, getVersion());
    collection.insertOne(document);
  }

  @Override
  public Catalog.SearchResult find(Catalog.StoreQuery query) {
    log.info("Find objects by query '{}'", query);
    Bson bsonQuery = MongoQuery.filterBy(query);
    FindIterable<Document> results = collection.find(bsonQuery)
        .sort(MongoQuery.sortBy(query.getSortFields()))
        .projection(MongoQuery.selectBy(query))
        .skip(Integer.parseInt(query.getOffset() == null ? "0" : query.getOffset()))
        .limit(Integer.parseInt(query.getLimit() == null ? "0" : query.getLimit()));
    long totalCount = collection.countDocuments(bsonQuery);
    return prepareResult(totalCount, results);
  }

  @Override
  public Stream<JsonBean> scan(Catalog.StoreQuery query) {
    log.info("Find objects by query '{}'", query);
    FindIterable<Document> results = collection.find(MongoQuery.filterBy(query))
        .sort(MongoQuery.sortBy(query.getSortFields()))
        .projection(MongoQuery.selectBy(query))
        .skip(Integer.parseInt(query.getOffset() == null ? "0" : query.getOffset()))
        .limit(Integer.parseInt(query.getLimit() == null ? "0" : query.getLimit()));
    return StreamSupport.stream(results.spliterator(), false)
        .map(document -> JsonParser.mapToEntity(document, JsonBean::new));
  }

  @Override
  public void update(String key, JsonEntity<?> object) {
    log.info("Update object '{}'", key);
    boolean result = updateOne(MongoQuery.filterById(key), object, false);

    if (!result) {
      throw new MongoException("Object not found");
    }
  }

  @Override
  public void update(String key, Long version, JsonEntity<?> object) {
    log.info("Update object '{}' '{}'", key, version);
    updateOne(MongoQuery.filterByIdVersion(key, version), object, false);
  }

  @Override
  public void update(Catalog.StoreQuery query, JsonEntity<?> object) {
    log.info("Update object by query '{}'", query);
    boolean result = updateOne(MongoQuery.filterBy(query), object, false);

    if (!result) {
      throw new MongoException("Object not found");
    }
  }

  @Override
  public void save(String key, JsonEntity<?> object) {
    log.info("Save object '{}'", key);
    replaceOne(MongoQuery.filterById(key), asMongoDocument(key, object), true);
  }

  @Override
  public void delete(Catalog.StoreQuery query) {
    log.info("Delete object '{}'", query);
    deleteMany(MongoQuery.filterBy(query));
  }

  private boolean updateOne(Bson query, JsonEntity<?> object, boolean upsert) {
    Bson bson = MongoQuery.updateBy(object.asMap());
    UpdateOptions updateOptions = new UpdateOptions().upsert(upsert);
    UpdateResult result = collection.updateOne(query, bson, updateOptions);

    return result.getMatchedCount() == 1 || Objects.nonNull(result.getUpsertedId());
  }

  private void replaceOne(Bson query, Document document, boolean upsert) {
    document.put(MongoQuery.VERSION, getVersion());
    ReplaceOptions replaceOptions = new ReplaceOptions().upsert(upsert);
    UpdateResult result = collection.replaceOne(query, document, replaceOptions);

    if (result.getMatchedCount() != 1 && result.getUpsertedId() == null) {
      throw new MongoException("Object not found");
    }
  }

  private void deleteMany(Bson query) {
    collection.deleteMany(query);
  }

  @Override
  public void doInTransaction(Runnable runnable) {
    log.info("Do in transaction");
    ClientSession session = client.startSession();
    Collection.clientSession.set(session);
    try {
      session.startTransaction();
      runnable.run();
      session.commitTransaction();
    } catch (Exception ex) {
      log.error("Transaction error", ex);
      session.abortTransaction();
      throw ex;
    } finally {
      session.close();
      Collection.clientSession.remove();
    }
  }

  private Catalog.SearchResult prepareResult(long totalCount, MongoIterable<Document> searchResponse) {
    List<JsonBean> results = StreamSupport.stream(searchResponse.spliterator(), false)
        .map(document -> JsonParser.mapToEntity(document, JsonBean::new))
        .collect(Collectors.toList());

    return Catalog.SearchResult.of(String.valueOf(totalCount), results);
  }

  private Document asMongoDocument(String key, JsonEntity<?> object) {
    Document document = new Document()
        .append(MongoQuery.ID, key);
    document.putAll(object.asMap());
    return document;
  }

  public AggregateIterable<Document> execute(List<? extends Bson> pipeline) {
    return collection.aggregate(pipeline);
  }

  @Override
  public void close() {
    client.close();
  }

  @SuppressWarnings("DefaultAnnotationParam")
  @Data
  @FieldDefaults(makeFinal = false)
  public static class Properties {

    String uri;
    String db;
    String collection;

  }

  @AllArgsConstructor
  static class Collection {

    MongoCollection<Document> collection;
    static ThreadLocal<ClientSession> clientSession = new ThreadLocal<>();

    static Collection create(MongoCollection<Document> collection) {
      return new Collection(collection);
    }

    FindIterable<Document> find(Bson bson) {
      return collection.find(bson);
    }

    Long countDocuments(Bson bson) {
      return collection.countDocuments(bson);
    }

    InsertOneResult insertOne(Document document) {
      return Optional.ofNullable(clientSession.get())
          .map(session -> collection.insertOne(session, document))
          .orElseGet(() -> collection.insertOne(document));
    }

    UpdateResult updateOne(Bson query, Bson bson, UpdateOptions updateOptions) {
      return Optional.ofNullable(clientSession.get())
          .map(session -> collection.updateOne(session, query, bson, updateOptions))
          .orElseGet(() -> collection.updateOne(query, bson, updateOptions));
    }

    UpdateResult replaceOne(Bson query, Document document, ReplaceOptions replaceOptions) {
      return Optional.ofNullable(clientSession.get())
          .map(session -> collection.replaceOne(session, query, document, replaceOptions))
          .orElseGet(() -> collection.replaceOne(query, document, replaceOptions));
    }

    DeleteResult deleteMany(Bson query) {
      return Optional.ofNullable(clientSession.get())
          .map(session -> collection.deleteMany(session, query))
          .orElseGet(() -> collection.deleteMany(query));
    }

    AggregateIterable<Document> aggregate(List<? extends Bson> pipeline) {
      return Optional.ofNullable(clientSession.get())
              .map(session -> collection.aggregate(session, pipeline))
              .orElseGet(() -> collection.aggregate(pipeline));
    }

  }

  static long getVersion() {
    return System.nanoTime();
  }

}
