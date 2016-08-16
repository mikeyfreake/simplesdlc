package io.github.mikeyfreake.myapp.web.rest;

import io.github.mikeyfreake.myapp.SimplesdlcApp;
import io.github.mikeyfreake.myapp.domain.Story;
import io.github.mikeyfreake.myapp.repository.StoryRepository;
import io.github.mikeyfreake.myapp.repository.search.StorySearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.github.mikeyfreake.myapp.domain.enumeration.StoryClassification;
import io.github.mikeyfreake.myapp.domain.enumeration.StoryState;
import io.github.mikeyfreake.myapp.domain.enumeration.StoryUrgency;
import io.github.mikeyfreake.myapp.domain.enumeration.StoryPriority;

/**
 * Test class for the StoryResource REST controller.
 *
 * @see StoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SimplesdlcApp.class)
@WebAppConfiguration
@IntegrationTest
public class StoryResourceIntTest {

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";
    private static final String DEFAULT_ACCEPTANCE_CRITERIA = "AAAAA";
    private static final String UPDATED_ACCEPTANCE_CRITERIA = "BBBBB";

    private static final StoryClassification DEFAULT_CLASSIFICATION = StoryClassification.Project;
    private static final StoryClassification UPDATED_CLASSIFICATION = StoryClassification.Initiative;

    private static final Integer DEFAULT_EFFORT_POINTS = 1;
    private static final Integer UPDATED_EFFORT_POINTS = 2;

    private static final StoryState DEFAULT_STATE = StoryState.AwaitingInfo;
    private static final StoryState UPDATED_STATE = StoryState.Draft;

    private static final StoryUrgency DEFAULT_URGENCY = StoryUrgency.Critical;
    private static final StoryUrgency UPDATED_URGENCY = StoryUrgency.High;

    private static final StoryPriority DEFAULT_PRIORITY = StoryPriority.Critical;
    private static final StoryPriority UPDATED_PRIORITY = StoryPriority.High;

    @Inject
    private StoryRepository storyRepository;

    @Inject
    private StorySearchRepository storySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStoryMockMvc;

    private Story story;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StoryResource storyResource = new StoryResource();
        ReflectionTestUtils.setField(storyResource, "storySearchRepository", storySearchRepository);
        ReflectionTestUtils.setField(storyResource, "storyRepository", storyRepository);
        this.restStoryMockMvc = MockMvcBuilders.standaloneSetup(storyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        storySearchRepository.deleteAll();
        story = new Story();
        story.setShortDescription(DEFAULT_SHORT_DESCRIPTION);
        story.setDescription(DEFAULT_DESCRIPTION);
        story.setAcceptanceCriteria(DEFAULT_ACCEPTANCE_CRITERIA);
        story.setClassification(DEFAULT_CLASSIFICATION);
        story.setEffortPoints(DEFAULT_EFFORT_POINTS);
        story.setState(DEFAULT_STATE);
        story.setUrgency(DEFAULT_URGENCY);
        story.setPriority(DEFAULT_PRIORITY);
    }

    @Test
    @Transactional
    public void createStory() throws Exception {
        int databaseSizeBeforeCreate = storyRepository.findAll().size();

        // Create the Story

        restStoryMockMvc.perform(post("/api/stories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(story)))
                .andExpect(status().isCreated());

        // Validate the Story in the database
        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeCreate + 1);
        Story testStory = stories.get(stories.size() - 1);
        assertThat(testStory.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testStory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStory.getAcceptanceCriteria()).isEqualTo(DEFAULT_ACCEPTANCE_CRITERIA);
        assertThat(testStory.getClassification()).isEqualTo(DEFAULT_CLASSIFICATION);
        assertThat(testStory.getEffortPoints()).isEqualTo(DEFAULT_EFFORT_POINTS);
        assertThat(testStory.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testStory.getUrgency()).isEqualTo(DEFAULT_URGENCY);
        assertThat(testStory.getPriority()).isEqualTo(DEFAULT_PRIORITY);

        // Validate the Story in ElasticSearch
        Story storyEs = storySearchRepository.findOne(testStory.getId());
        assertThat(storyEs).isEqualToComparingFieldByField(testStory);
    }

    @Test
    @Transactional
    public void checkShortDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = storyRepository.findAll().size();
        // set the field null
        story.setShortDescription(null);

        // Create the Story, which fails.

        restStoryMockMvc.perform(post("/api/stories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(story)))
                .andExpect(status().isBadRequest());

        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStories() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

        // Get all the stories
        restStoryMockMvc.perform(get("/api/stories?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(story.getId().intValue())))
                .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].acceptanceCriteria").value(hasItem(DEFAULT_ACCEPTANCE_CRITERIA.toString())))
                .andExpect(jsonPath("$.[*].classification").value(hasItem(DEFAULT_CLASSIFICATION.toString())))
                .andExpect(jsonPath("$.[*].effortPoints").value(hasItem(DEFAULT_EFFORT_POINTS)))
                .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
                .andExpect(jsonPath("$.[*].urgency").value(hasItem(DEFAULT_URGENCY.toString())))
                .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())));
    }

    @Test
    @Transactional
    public void getStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);

        // Get the story
        restStoryMockMvc.perform(get("/api/stories/{id}", story.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(story.getId().intValue()))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.acceptanceCriteria").value(DEFAULT_ACCEPTANCE_CRITERIA.toString()))
            .andExpect(jsonPath("$.classification").value(DEFAULT_CLASSIFICATION.toString()))
            .andExpect(jsonPath("$.effortPoints").value(DEFAULT_EFFORT_POINTS))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.urgency").value(DEFAULT_URGENCY.toString()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStory() throws Exception {
        // Get the story
        restStoryMockMvc.perform(get("/api/stories/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);
        storySearchRepository.save(story);
        int databaseSizeBeforeUpdate = storyRepository.findAll().size();

        // Update the story
        Story updatedStory = new Story();
        updatedStory.setId(story.getId());
        updatedStory.setShortDescription(UPDATED_SHORT_DESCRIPTION);
        updatedStory.setDescription(UPDATED_DESCRIPTION);
        updatedStory.setAcceptanceCriteria(UPDATED_ACCEPTANCE_CRITERIA);
        updatedStory.setClassification(UPDATED_CLASSIFICATION);
        updatedStory.setEffortPoints(UPDATED_EFFORT_POINTS);
        updatedStory.setState(UPDATED_STATE);
        updatedStory.setUrgency(UPDATED_URGENCY);
        updatedStory.setPriority(UPDATED_PRIORITY);

        restStoryMockMvc.perform(put("/api/stories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStory)))
                .andExpect(status().isOk());

        // Validate the Story in the database
        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeUpdate);
        Story testStory = stories.get(stories.size() - 1);
        assertThat(testStory.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testStory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStory.getAcceptanceCriteria()).isEqualTo(UPDATED_ACCEPTANCE_CRITERIA);
        assertThat(testStory.getClassification()).isEqualTo(UPDATED_CLASSIFICATION);
        assertThat(testStory.getEffortPoints()).isEqualTo(UPDATED_EFFORT_POINTS);
        assertThat(testStory.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testStory.getUrgency()).isEqualTo(UPDATED_URGENCY);
        assertThat(testStory.getPriority()).isEqualTo(UPDATED_PRIORITY);

        // Validate the Story in ElasticSearch
        Story storyEs = storySearchRepository.findOne(testStory.getId());
        assertThat(storyEs).isEqualToComparingFieldByField(testStory);
    }

    @Test
    @Transactional
    public void deleteStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);
        storySearchRepository.save(story);
        int databaseSizeBeforeDelete = storyRepository.findAll().size();

        // Get the story
        restStoryMockMvc.perform(delete("/api/stories/{id}", story.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean storyExistsInEs = storySearchRepository.exists(story.getId());
        assertThat(storyExistsInEs).isFalse();

        // Validate the database is empty
        List<Story> stories = storyRepository.findAll();
        assertThat(stories).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStory() throws Exception {
        // Initialize the database
        storyRepository.saveAndFlush(story);
        storySearchRepository.save(story);

        // Search the story
        restStoryMockMvc.perform(get("/api/_search/stories?query=id:" + story.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(story.getId().intValue())))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].acceptanceCriteria").value(hasItem(DEFAULT_ACCEPTANCE_CRITERIA.toString())))
            .andExpect(jsonPath("$.[*].classification").value(hasItem(DEFAULT_CLASSIFICATION.toString())))
            .andExpect(jsonPath("$.[*].effortPoints").value(hasItem(DEFAULT_EFFORT_POINTS)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].urgency").value(hasItem(DEFAULT_URGENCY.toString())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY.toString())));
    }
}
