package io.github.mikeyfreake.myapp.service;

import io.github.mikeyfreake.myapp.domain.Sprint;
import io.github.mikeyfreake.myapp.repository.SprintRepository;
import io.github.mikeyfreake.myapp.repository.search.SprintSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Sprint.
 */
@Service
@Transactional
public class SprintService {

    private final Logger log = LoggerFactory.getLogger(SprintService.class);
    
    @Inject
    private SprintRepository sprintRepository;
    
    @Inject
    private SprintSearchRepository sprintSearchRepository;
    
    /**
     * Save a sprint.
     * 
     * @param sprint the entity to save
     * @return the persisted entity
     */
    public Sprint save(Sprint sprint) {
        log.debug("Request to save Sprint : {}", sprint);
        Sprint result = sprintRepository.save(sprint);
        sprintSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the sprints.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Sprint> findAll(Pageable pageable) {
        log.debug("Request to get all Sprints");
        Page<Sprint> result = sprintRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one sprint by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Sprint findOne(Long id) {
        log.debug("Request to get Sprint : {}", id);
        Sprint sprint = sprintRepository.findOne(id);
        return sprint;
    }

    /**
     *  Delete the  sprint by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Sprint : {}", id);
        sprintRepository.delete(id);
        sprintSearchRepository.delete(id);
    }

    /**
     * Search for the sprint corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Sprint> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sprints for query {}", query);
        return sprintSearchRepository.search(queryStringQuery(query), pageable);
    }
}
