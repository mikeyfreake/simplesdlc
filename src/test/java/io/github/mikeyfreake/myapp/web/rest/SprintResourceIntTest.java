package io.github.mikeyfreake.myapp.web.rest;

import io.github.mikeyfreake.myapp.SimplesdlcApp;
import io.github.mikeyfreake.myapp.domain.Sprint;
import io.github.mikeyfreake.myapp.repository.SprintRepository;
import io.github.mikeyfreake.myapp.service.SprintService;
import io.github.mikeyfreake.myapp.repository.search.SprintSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.mikeyfreake.myapp.domain.enumeration.SprintState;

/**
 * Test class for the SprintResource REST controller.
 *
 * @see SprintResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SimplesdlcApp.class)
@WebAppConfiguration
@IntegrationTest
public class SprintResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final SprintState DEFAULT_STATE = SprintState.Draft;
    private static final SprintState UPDATED_STATE = SprintState.Planning;

    private static final ZonedDateTime DEFAULT_PLANNED_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_PLANNED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_PLANNED_START_DATE_STR = dateTimeFormatter.format(DEFAULT_PLANNED_START_DATE);

    private static final ZonedDateTime DEFAULT_PLANNED_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_PLANNED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_PLANNED_END_DATE_STR = dateTimeFormatter.format(DEFAULT_PLANNED_END_DATE);

    @Inject
    private SprintRepository sprintRepository;

    @Inject
    private SprintService sprintService;

    @Inject
    private SprintSearchRepository sprintSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSprintMockMvc;

    private Sprint sprint;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SprintResource sprintResource = new SprintResource();
        ReflectionTestUtils.setField(sprintResource, "sprintService", sprintService);
        this.restSprintMockMvc = MockMvcBuilders.standaloneSetup(sprintResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        sprintSearchRepository.deleteAll();
        sprint = new Sprint();
        sprint.setShortDescription(DEFAULT_SHORT_DESCRIPTION);
        sprint.setDescription(DEFAULT_DESCRIPTION);
        sprint.setState(DEFAULT_STATE);
        sprint.setPlannedStartDate(DEFAULT_PLANNED_START_DATE);
        sprint.setPlannedEndDate(DEFAULT_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    public void createSprint() throws Exception {
        int databaseSizeBeforeCreate = sprintRepository.findAll().size();

        // Create the Sprint

        restSprintMockMvc.perform(post("/api/sprints")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sprint)))
                .andExpect(status().isCreated());

        // Validate the Sprint in the database
        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeCreate + 1);
        Sprint testSprint = sprints.get(sprints.size() - 1);
        assertThat(testSprint.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testSprint.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSprint.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testSprint.getPlannedStartDate()).isEqualTo(DEFAULT_PLANNED_START_DATE);
        assertThat(testSprint.getPlannedEndDate()).isEqualTo(DEFAULT_PLANNED_END_DATE);

        // Validate the Sprint in ElasticSearch
        Sprint sprintEs = sprintSearchRepository.findOne(testSprint.getId());
        assertThat(sprintEs).isEqualToComparingFieldByField(testSprint);
    }

    @Test
    @Transactional
    public void checkShortDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = sprintRepository.findAll().size();
        // set the field null
        sprint.setShortDescription(null);

        // Create the Sprint, which fails.

        restSprintMockMvc.perform(post("/api/sprints")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sprint)))
                .andExpect(status().isBadRequest());

        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPlannedStartDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = sprintRepository.findAll().size();
        // set the field null
        sprint.setPlannedStartDate(null);

        // Create the Sprint, which fails.

        restSprintMockMvc.perform(post("/api/sprints")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sprint)))
                .andExpect(status().isBadRequest());

        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPlannedEndDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = sprintRepository.findAll().size();
        // set the field null
        sprint.setPlannedEndDate(null);

        // Create the Sprint, which fails.

        restSprintMockMvc.perform(post("/api/sprints")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sprint)))
                .andExpect(status().isBadRequest());

        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSprints() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);

        // Get all the sprints
        restSprintMockMvc.perform(get("/api/sprints?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sprint.getId().intValue())))
                .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
                .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE_STR)))
                .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE_STR)));
    }

    @Test
    @Transactional
    public void getSprint() throws Exception {
        // Initialize the database
        sprintRepository.saveAndFlush(sprint);

        // Get the sprint
        restSprintMockMvc.perform(get("/api/sprints/{id}", sprint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sprint.getId().intValue()))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.plannedStartDate").value(DEFAULT_PLANNED_START_DATE_STR))
            .andExpect(jsonPath("$.plannedEndDate").value(DEFAULT_PLANNED_END_DATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingSprint() throws Exception {
        // Get the sprint
        restSprintMockMvc.perform(get("/api/sprints/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSprint() throws Exception {
        // Initialize the database
        sprintService.save(sprint);

        int databaseSizeBeforeUpdate = sprintRepository.findAll().size();

        // Update the sprint
        Sprint updatedSprint = new Sprint();
        updatedSprint.setId(sprint.getId());
        updatedSprint.setShortDescription(UPDATED_SHORT_DESCRIPTION);
        updatedSprint.setDescription(UPDATED_DESCRIPTION);
        updatedSprint.setState(UPDATED_STATE);
        updatedSprint.setPlannedStartDate(UPDATED_PLANNED_START_DATE);
        updatedSprint.setPlannedEndDate(UPDATED_PLANNED_END_DATE);

        restSprintMockMvc.perform(put("/api/sprints")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSprint)))
                .andExpect(status().isOk());

        // Validate the Sprint in the database
        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeUpdate);
        Sprint testSprint = sprints.get(sprints.size() - 1);
        assertThat(testSprint.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testSprint.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSprint.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testSprint.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testSprint.getPlannedEndDate()).isEqualTo(UPDATED_PLANNED_END_DATE);

        // Validate the Sprint in ElasticSearch
        Sprint sprintEs = sprintSearchRepository.findOne(testSprint.getId());
        assertThat(sprintEs).isEqualToComparingFieldByField(testSprint);
    }

    @Test
    @Transactional
    public void deleteSprint() throws Exception {
        // Initialize the database
        sprintService.save(sprint);

        int databaseSizeBeforeDelete = sprintRepository.findAll().size();

        // Get the sprint
        restSprintMockMvc.perform(delete("/api/sprints/{id}", sprint.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean sprintExistsInEs = sprintSearchRepository.exists(sprint.getId());
        assertThat(sprintExistsInEs).isFalse();

        // Validate the database is empty
        List<Sprint> sprints = sprintRepository.findAll();
        assertThat(sprints).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSprint() throws Exception {
        // Initialize the database
        sprintService.save(sprint);

        // Search the sprint
        restSprintMockMvc.perform(get("/api/_search/sprints?query=id:" + sprint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sprint.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE_STR)))
            .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE_STR)));
    }
}
