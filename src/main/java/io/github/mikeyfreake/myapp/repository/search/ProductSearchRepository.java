package io.github.mikeyfreake.myapp.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import io.github.mikeyfreake.myapp.domain.Product;

/**
 * Spring Data ElasticSearch repository for the Product entity.
 */
public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {
}
