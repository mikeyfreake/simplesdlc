package io.github.mikeyfreake.myapp.repository.search;

import io.github.mikeyfreake.myapp.domain.Release;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Release entity.
 */
public interface ReleaseSearchRepository extends ElasticsearchRepository<Release, Long> {
}
