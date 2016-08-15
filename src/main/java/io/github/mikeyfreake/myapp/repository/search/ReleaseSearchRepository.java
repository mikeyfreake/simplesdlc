package io.github.mikeyfreake.myapp.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import io.github.mikeyfreake.myapp.domain.Release;

/**
 * Spring Data ElasticSearch repository for the Release entity.
 */
public interface ReleaseSearchRepository extends ElasticsearchRepository<Release, Long> {
}
