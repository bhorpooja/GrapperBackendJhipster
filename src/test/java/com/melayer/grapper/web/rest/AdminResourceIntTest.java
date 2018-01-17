package com.melayer.grapper.web.rest;

import com.melayer.grapper.GrapperApp;

import com.melayer.grapper.domain.Admin;
import com.melayer.grapper.repository.AdminRepository;
import com.melayer.grapper.repository.search.AdminSearchRepository;
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
 * Test class for the AdminResource REST controller.
 *
 * @see AdminResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GrapperApp.class)
public class AdminResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL_ID = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL_ID = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE_NO = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE_NO = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminSearchRepository adminSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restAdminMockMvc;

    private Admin admin;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AdminResource adminResource = new AdminResource(adminRepository, adminSearchRepository);
        this.restAdminMockMvc = MockMvcBuilders.standaloneSetup(adminResource)
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
    public static Admin createEntity() {
        Admin admin = new Admin()
            .name(DEFAULT_NAME)
            .emailId(DEFAULT_EMAIL_ID)
            .mobileNo(DEFAULT_MOBILE_NO)
            .password(DEFAULT_PASSWORD);
        return admin;
    }

    @Before
    public void initTest() {
        adminRepository.deleteAll();
        adminSearchRepository.deleteAll();
        admin = createEntity();
    }

    @Test
    public void createAdmin() throws Exception {
        int databaseSizeBeforeCreate = adminRepository.findAll().size();

        // Create the Admin
        restAdminMockMvc.perform(post("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isCreated());

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeCreate + 1);
        Admin testAdmin = adminList.get(adminList.size() - 1);
        assertThat(testAdmin.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAdmin.getEmailId()).isEqualTo(DEFAULT_EMAIL_ID);
        assertThat(testAdmin.getMobileNo()).isEqualTo(DEFAULT_MOBILE_NO);
        assertThat(testAdmin.getPassword()).isEqualTo(DEFAULT_PASSWORD);

        // Validate the Admin in Elasticsearch
        Admin adminEs = adminSearchRepository.findOne(testAdmin.getId());
        assertThat(adminEs).isEqualToIgnoringGivenFields(testAdmin);
    }

    @Test
    public void createAdminWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = adminRepository.findAll().size();

        // Create the Admin with an existing ID
        admin.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdminMockMvc.perform(post("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isBadRequest());

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminRepository.findAll().size();
        // set the field null
        admin.setName(null);

        // Create the Admin, which fails.

        restAdminMockMvc.perform(post("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isBadRequest());

        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkEmailIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminRepository.findAll().size();
        // set the field null
        admin.setEmailId(null);

        // Create the Admin, which fails.

        restAdminMockMvc.perform(post("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isBadRequest());

        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkMobileNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminRepository.findAll().size();
        // set the field null
        admin.setMobileNo(null);

        // Create the Admin, which fails.

        restAdminMockMvc.perform(post("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isBadRequest());

        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = adminRepository.findAll().size();
        // set the field null
        admin.setPassword(null);

        // Create the Admin, which fails.

        restAdminMockMvc.perform(post("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isBadRequest());

        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllAdmins() throws Exception {
        // Initialize the database
        adminRepository.save(admin);

        // Get all the adminList
        restAdminMockMvc.perform(get("/api/admins?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(admin.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].emailId").value(hasItem(DEFAULT_EMAIL_ID.toString())))
            .andExpect(jsonPath("$.[*].mobileNo").value(hasItem(DEFAULT_MOBILE_NO.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())));
    }

    @Test
    public void getAdmin() throws Exception {
        // Initialize the database
        adminRepository.save(admin);

        // Get the admin
        restAdminMockMvc.perform(get("/api/admins/{id}", admin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(admin.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.emailId").value(DEFAULT_EMAIL_ID.toString()))
            .andExpect(jsonPath("$.mobileNo").value(DEFAULT_MOBILE_NO.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()));
    }

    @Test
    public void getNonExistingAdmin() throws Exception {
        // Get the admin
        restAdminMockMvc.perform(get("/api/admins/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateAdmin() throws Exception {
        // Initialize the database
        adminRepository.save(admin);
        adminSearchRepository.save(admin);
        int databaseSizeBeforeUpdate = adminRepository.findAll().size();

        // Update the admin
        Admin updatedAdmin = adminRepository.findOne(admin.getId());
        updatedAdmin
            .name(UPDATED_NAME)
            .emailId(UPDATED_EMAIL_ID)
            .mobileNo(UPDATED_MOBILE_NO)
            .password(UPDATED_PASSWORD);

        restAdminMockMvc.perform(put("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAdmin)))
            .andExpect(status().isOk());

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate);
        Admin testAdmin = adminList.get(adminList.size() - 1);
        assertThat(testAdmin.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAdmin.getEmailId()).isEqualTo(UPDATED_EMAIL_ID);
        assertThat(testAdmin.getMobileNo()).isEqualTo(UPDATED_MOBILE_NO);
        assertThat(testAdmin.getPassword()).isEqualTo(UPDATED_PASSWORD);

        // Validate the Admin in Elasticsearch
        Admin adminEs = adminSearchRepository.findOne(testAdmin.getId());
        assertThat(adminEs).isEqualToIgnoringGivenFields(testAdmin);
    }

    @Test
    public void updateNonExistingAdmin() throws Exception {
        int databaseSizeBeforeUpdate = adminRepository.findAll().size();

        // Create the Admin

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAdminMockMvc.perform(put("/api/admins")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(admin)))
            .andExpect(status().isCreated());

        // Validate the Admin in the database
        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteAdmin() throws Exception {
        // Initialize the database
        adminRepository.save(admin);
        adminSearchRepository.save(admin);
        int databaseSizeBeforeDelete = adminRepository.findAll().size();

        // Get the admin
        restAdminMockMvc.perform(delete("/api/admins/{id}", admin.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean adminExistsInEs = adminSearchRepository.exists(admin.getId());
        assertThat(adminExistsInEs).isFalse();

        // Validate the database is empty
        List<Admin> adminList = adminRepository.findAll();
        assertThat(adminList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void searchAdmin() throws Exception {
        // Initialize the database
        adminRepository.save(admin);
        adminSearchRepository.save(admin);

        // Search the admin
        restAdminMockMvc.perform(get("/api/_search/admins?query=id:" + admin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(admin.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].emailId").value(hasItem(DEFAULT_EMAIL_ID.toString())))
            .andExpect(jsonPath("$.[*].mobileNo").value(hasItem(DEFAULT_MOBILE_NO.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Admin.class);
        Admin admin1 = new Admin();
        admin1.setId("id1");
        Admin admin2 = new Admin();
        admin2.setId(admin1.getId());
        assertThat(admin1).isEqualTo(admin2);
        admin2.setId("id2");
        assertThat(admin1).isNotEqualTo(admin2);
        admin1.setId(null);
        assertThat(admin1).isNotEqualTo(admin2);
    }
}
