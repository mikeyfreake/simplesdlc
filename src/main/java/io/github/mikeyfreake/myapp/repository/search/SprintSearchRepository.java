package io.github.mikeyfreake.myapp.repository.search;

import io.github.mikeyfreake.myapp.domain.Sprint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Sprint entity.
 */
public interface SprintSearchRepository extends ElasticsearchRepository<Sprint, Long> {
}
