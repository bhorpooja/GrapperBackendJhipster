package com.melayer.grapper.web.rest;

import com.melayer.grapper.GrapperApp;

import com.melayer.grapper.domain.App;
import com.melayer.grapper.repository.AppRepository;
import com.melayer.grapper.repository.search.AppSearchRepository;
import com.melayer.grapper.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.melayer.grapper.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AppResource REST controller.
 *
 * @see AppResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrapperApp.class)
public class AppResourceIntTest {

    private static final String DEFAULT_APP_NAME = "AAAAAAAAAA";
    private static final String UPDATED_APP_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_OFFER = 1;
    private static final Integer UPDATED_OFFER = 2;

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_DATE = "AAAAAAAAAA";
    private static final String UPDATED_DATE = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private AppSearchRepository appSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restAppMockMvc;

    private App app;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AppResource appResource = new AppResource(appRepository, appSearchRepository);
        this.restAppMockMvc = MockMvcBuilders.standaloneSetup(appResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static App createEntity() {
        App app = new App()
            .appName(DEFAULT_APP_NAME)
            .url(DEFAULT_URL)
            .offer(DEFAULT_OFFER)
            .category(DEFAULT_CATEGORY)
            .date(DEFAULT_DATE)
            .image(DEFAULT_IMAGE);
        return app;
    }

    @Before
    public void initTest() {
        appRepository.deleteAll();
        appSearchRepository.deleteAll();
        app = createEntity();
    }

    @Test
    public void createApp() throws Exception {
        int databaseSizeBeforeCreate = appRepository.findAll().size();

        // Create the App
        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isCreated());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeCreate + 1);
        App testApp = appList.get(appList.size() - 1);
        assertThat(testApp.getAppName()).isEqualTo(DEFAULT_APP_NAME);
        assertThat(testApp.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testApp.getOffer()).isEqualTo(DEFAULT_OFFER);
        assertThat(testApp.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testApp.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testApp.getImage()).isEqualTo(DEFAULT_IMAGE);

        // Validate the App in Elasticsearch
        App appEs = appSearchRepository.findOne(testApp.getId());
        assertThat(appEs).isEqualToIgnoringGivenFields(testApp);
    }

    @Test
    public void createAppWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = appRepository.findAll().size();

        // Create the App with an existing ID
        app.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isBadRequest());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkAppNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appRepository.findAll().size();
        // set the field null
        app.setAppName(null);

        // Create the App, which fails.

        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isBadRequest());

        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = appRepository.findAll().size();
        // set the field null
        app.setUrl(null);

        // Create the App, which fails.

        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isBadRequest());

        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkOfferIsRequired() throws Exception {
        int databaseSizeBeforeTest = appRepository.findAll().size();
        // set the field null
        app.setOffer(null);

        // Create the App, which fails.

        restAppMockMvc.perform(post("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isBadRequest());

        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllApps() throws Exception {
        // Initialize the database
        appRepository.save(app);

        // Get all the appList
        restAppMockMvc.perform(get("/api/apps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId())))
            .andExpect(jsonPath("$.[*].appName").value(hasItem(DEFAULT_APP_NAME.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].offer").value(hasItem(DEFAULT_OFFER)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE.toString())));
    }

    @Test
    public void getApp() throws Exception {
        // Initialize the database
        appRepository.save(app);

        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", app.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(app.getId()))
            .andExpect(jsonPath("$.appName").value(DEFAULT_APP_NAME.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.offer").value(DEFAULT_OFFER))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE.toString()));
    }

    @Test
    public void getNonExistingApp() throws Exception {
        // Get the app
        restAppMockMvc.perform(get("/api/apps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateApp() throws Exception {
        // Initialize the database
        appRepository.save(app);
        appSearchRepository.save(app);
        int databaseSizeBeforeUpdate = appRepository.findAll().size();

        // Update the app
        App updatedApp = appRepository.findOne(app.getId());
        updatedApp
            .appName(UPDATED_APP_NAME)
            .url(UPDATED_URL)
            .offer(UPDATED_OFFER)
            .category(UPDATED_CATEGORY)
            .date(UPDATED_DATE)
            .image(UPDATED_IMAGE);

        restAppMockMvc.perform(put("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedApp)))
            .andExpect(status().isOk());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeUpdate);
        App testApp = appList.get(appList.size() - 1);
        assertThat(testApp.getAppName()).isEqualTo(UPDATED_APP_NAME);
        assertThat(testApp.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testApp.getOffer()).isEqualTo(UPDATED_OFFER);
        assertThat(testApp.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testApp.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testApp.getImage()).isEqualTo(UPDATED_IMAGE);

        // Validate the App in Elasticsearch
        App appEs = appSearchRepository.findOne(testApp.getId());
        assertThat(appEs).isEqualToIgnoringGivenFields(testApp);
    }

    @Test
    public void updateNonExistingApp() throws Exception {
        int databaseSizeBeforeUpdate = appRepository.findAll().size();

        // Create the App

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAppMockMvc.perform(put("/api/apps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(app)))
            .andExpect(status().isCreated());

        // Validate the App in the database
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteApp() throws Exception {
        // Initialize the database
        appRepository.save(app);
        appSearchRepository.save(app);
        int databaseSizeBeforeDelete = appRepository.findAll().size();

        // Get the app
        restAppMockMvc.perform(delete("/api/apps/{id}", app.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean appExistsInEs = appSearchRepository.exists(app.getId());
        assertThat(appExistsInEs).isFalse();

        // Validate the database is empty
        List<App> appList = appRepository.findAll();
        assertThat(appList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void searchApp() throws Exception {
        // Initialize the database
        appRepository.save(app);
        appSearchRepository.save(app);

        // Search the app
        restAppMockMvc.perform(get("/api/_search/apps?query=id:" + app.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(app.getId())))
            .andExpect(jsonPath("$.[*].appName").value(hasItem(DEFAULT_APP_NAME.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].offer").value(hasItem(DEFAULT_OFFER)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(App.class);
        App app1 = new App();
        app1.setId("id1");
        App app2 = new App();
        app2.setId(app1.getId());
        assertThat(app1).isEqualTo(app2);
        app2.setId("id2");
        assertThat(app1).isNotEqualTo(app2);
        app1.setId(null);
        assertThat(app1).isNotEqualTo(app2);
    }
}
