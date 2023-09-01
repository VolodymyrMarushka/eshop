package vmdev.pet.eshop.product;

import eshop.core.starter.adapter.catalog.Catalog;
import eshop.core.starter.adapter.catalog.CatalogMongo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductCatalog {

  @Bean
  public Catalog.Manager productCatalog(CatalogMongo.Properties productCatalogProperties) {
    return CatalogMongo.create(productCatalogProperties);
  }

  @Bean
  @ConfigurationProperties(prefix = "vmdev.pet.eshop.provider.product-catalog.mongo")
  public CatalogMongo.Properties productCatalogProperties() {
    return new CatalogMongo.Properties();
  }

}
