package io.github.mikeyfreake.myapp.repository.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import io.github.mikeyfreake.myapp.domain.User;

/**
 * Spring Data ElasticSearch repository for the User entity.
 */
public interface UserSearchRepository extends ElasticsearchRepository<User, Long> {
}
