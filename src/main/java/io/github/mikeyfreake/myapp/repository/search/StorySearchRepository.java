package io.github.mikeyfreake.myapp.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import io.github.mikeyfreake.myapp.domain.Story;

/**
 * Spring Data ElasticSearch repository for the Story entity.
 */
public interface StorySearchRepository extends ElasticsearchRepository<Story, Long> {
}
