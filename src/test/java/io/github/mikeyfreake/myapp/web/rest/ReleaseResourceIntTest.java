package io.github.mikeyfreake.myapp.web.rest;

import io.github.mikeyfreake.myapp.SimplesdlcApp;
import io.github.mikeyfreake.myapp.domain.Release;
import io.github.mikeyfreake.myapp.repository.ReleaseRepository;
import io.github.mikeyfreake.myapp.repository.search.ReleaseSearchRepository;

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

import io.github.mikeyfreake.myapp.domain.enumeration.ReleaseState;

/**
 * Test class for the ReleaseResource REST controller.
 *
 * @see ReleaseResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SimplesdlcApp.class)
@WebAppConfiguration
@IntegrationTest
public class ReleaseResourceIntTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));


    private static final ReleaseState DEFAULT_STATE = ReleaseState.Draft;
    private static final ReleaseState UPDATED_STATE = ReleaseState.Planning;

    private static final ZonedDateTime DEFAULT_PLANNED_START_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_PLANNED_START_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_PLANNED_START_DATE_STR = dateTimeFormatter.format(DEFAULT_PLANNED_START_DATE);

    private static final ZonedDateTime DEFAULT_PLANNED_END_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_PLANNED_END_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_PLANNED_END_DATE_STR = dateTimeFormatter.format(DEFAULT_PLANNED_END_DATE);
    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private ReleaseRepository releaseRepository;

    @Inject
    private ReleaseSearchRepository releaseSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restReleaseMockMvc;

    private Release release;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReleaseResource releaseResource = new ReleaseResource();
        ReflectionTestUtils.setField(releaseResource, "releaseSearchRepository", releaseSearchRepository);
        ReflectionTestUtils.setField(releaseResource, "releaseRepository", releaseRepository);
        this.restReleaseMockMvc = MockMvcBuilders.standaloneSetup(releaseResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        releaseSearchRepository.deleteAll();
        release = new Release();
        release.setState(DEFAULT_STATE);
        release.setPlannedStartDate(DEFAULT_PLANNED_START_DATE);
        release.setPlannedEndDate(DEFAULT_PLANNED_END_DATE);
        release.setShortDescription(DEFAULT_SHORT_DESCRIPTION);
        release.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createRelease() throws Exception {
        int databaseSizeBeforeCreate = releaseRepository.findAll().size();

        // Create the Release

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isCreated());

        // Validate the Release in the database
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeCreate + 1);
        Release testRelease = releases.get(releases.size() - 1);
        assertThat(testRelease.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testRelease.getPlannedStartDate()).isEqualTo(DEFAULT_PLANNED_START_DATE);
        assertThat(testRelease.getPlannedEndDate()).isEqualTo(DEFAULT_PLANNED_END_DATE);
        assertThat(testRelease.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testRelease.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Release in ElasticSearch
        Release releaseEs = releaseSearchRepository.findOne(testRelease.getId());
        assertThat(releaseEs).isEqualToComparingFieldByField(testRelease);
    }

    @Test
    @Transactional
    public void checkStateIsRequired() throws Exception {
        int databaseSizeBeforeTest = releaseRepository.findAll().size();
        // set the field null
        release.setState(null);

        // Create the Release, which fails.

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isBadRequest());

        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkShortDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = releaseRepository.findAll().size();
        // set the field null
        release.setShortDescription(null);

        // Create the Release, which fails.

        restReleaseMockMvc.perform(post("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(release)))
                .andExpect(status().isBadRequest());

        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllReleases() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);

        // Get all the releases
        restReleaseMockMvc.perform(get("/api/releases?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(release.getId().intValue())))
                .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
                .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE_STR)))
                .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE_STR)))
                .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);

        // Get the release
        restReleaseMockMvc.perform(get("/api/releases/{id}", release.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(release.getId().intValue()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.plannedStartDate").value(DEFAULT_PLANNED_START_DATE_STR))
            .andExpect(jsonPath("$.plannedEndDate").value(DEFAULT_PLANNED_END_DATE_STR))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRelease() throws Exception {
        // Get the release
        restReleaseMockMvc.perform(get("/api/releases/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        releaseSearchRepository.save(release);
        int databaseSizeBeforeUpdate = releaseRepository.findAll().size();

        // Update the release
        Release updatedRelease = new Release();
        updatedRelease.setId(release.getId());
        updatedRelease.setState(UPDATED_STATE);
        updatedRelease.setPlannedStartDate(UPDATED_PLANNED_START_DATE);
        updatedRelease.setPlannedEndDate(UPDATED_PLANNED_END_DATE);
        updatedRelease.setShortDescription(UPDATED_SHORT_DESCRIPTION);
        updatedRelease.setDescription(UPDATED_DESCRIPTION);

        restReleaseMockMvc.perform(put("/api/releases")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRelease)))
                .andExpect(status().isOk());

        // Validate the Release in the database
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeUpdate);
        Release testRelease = releases.get(releases.size() - 1);
        assertThat(testRelease.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testRelease.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testRelease.getPlannedEndDate()).isEqualTo(UPDATED_PLANNED_END_DATE);
        assertThat(testRelease.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testRelease.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Release in ElasticSearch
        Release releaseEs = releaseSearchRepository.findOne(testRelease.getId());
        assertThat(releaseEs).isEqualToComparingFieldByField(testRelease);
    }

    @Test
    @Transactional
    public void deleteRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        releaseSearchRepository.save(release);
        int databaseSizeBeforeDelete = releaseRepository.findAll().size();

        // Get the release
        restReleaseMockMvc.perform(delete("/api/releases/{id}", release.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean releaseExistsInEs = releaseSearchRepository.exists(release.getId());
        assertThat(releaseExistsInEs).isFalse();

        // Validate the database is empty
        List<Release> releases = releaseRepository.findAll();
        assertThat(releases).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRelease() throws Exception {
        // Initialize the database
        releaseRepository.saveAndFlush(release);
        releaseSearchRepository.save(release);

        // Search the release
        restReleaseMockMvc.perform(get("/api/_search/releases?query=id:" + release.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(release.getId().intValue())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE_STR)))
            .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE_STR)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
