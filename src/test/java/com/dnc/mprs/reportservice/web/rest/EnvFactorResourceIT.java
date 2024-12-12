package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.EnvFactorAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.EnvFactor;
import com.dnc.mprs.reportservice.repository.EnvFactorRepository;
import com.dnc.mprs.reportservice.repository.search.EnvFactorSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EnvFactorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnvFactorResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

    private static final String DEFAULT_ENV_FACTOR_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENV_FACTOR_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_ENV_FACTOR_DISTANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ENV_FACTOR_DISTANCE = new BigDecimal(2);

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/env-factors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/env-factors/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnvFactorRepository envFactorRepository;

    @Autowired
    private EnvFactorSearchRepository envFactorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvFactorMockMvc;

    private EnvFactor envFactor;

    private EnvFactor insertedEnvFactor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvFactor createEntity() {
        return new EnvFactor()
            .reportId(DEFAULT_REPORT_ID)
            .envFactorName(DEFAULT_ENV_FACTOR_NAME)
            .envFactorDistance(DEFAULT_ENV_FACTOR_DISTANCE)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvFactor createUpdatedEntity() {
        return new EnvFactor()
            .reportId(UPDATED_REPORT_ID)
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        envFactor = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEnvFactor != null) {
            envFactorRepository.delete(insertedEnvFactor);
            envFactorSearchRepository.delete(insertedEnvFactor);
            insertedEnvFactor = null;
        }
    }

    @Test
    @Transactional
    void createEnvFactor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        // Create the EnvFactor
        var returnedEnvFactor = om.readValue(
            restEnvFactorMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envFactor)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnvFactor.class
        );

        // Validate the EnvFactor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEnvFactorUpdatableFieldsEquals(returnedEnvFactor, getPersistedEnvFactor(returnedEnvFactor));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEnvFactor = returnedEnvFactor;
    }

    @Test
    @Transactional
    void createEnvFactorWithExistingId() throws Exception {
        // Create the EnvFactor with an existing ID
        envFactor.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvFactorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envFactor)))
            .andExpect(status().isBadRequest());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        // set the field null
        envFactor.setReportId(null);

        // Create the EnvFactor, which fails.

        restEnvFactorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envFactor)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEnvFactorNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        // set the field null
        envFactor.setEnvFactorName(null);

        // Create the EnvFactor, which fails.

        restEnvFactorMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envFactor)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEnvFactors() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);

        // Get all the envFactorList
        restEnvFactorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(envFactor.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].envFactorName").value(hasItem(DEFAULT_ENV_FACTOR_NAME)))
            .andExpect(jsonPath("$.[*].envFactorDistance").value(hasItem(sameNumber(DEFAULT_ENV_FACTOR_DISTANCE))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getEnvFactor() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);

        // Get the envFactor
        restEnvFactorMockMvc
            .perform(get(ENTITY_API_URL_ID, envFactor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(envFactor.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.envFactorName").value(DEFAULT_ENV_FACTOR_NAME))
            .andExpect(jsonPath("$.envFactorDistance").value(sameNumber(DEFAULT_ENV_FACTOR_DISTANCE)))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingEnvFactor() throws Exception {
        // Get the envFactor
        restEnvFactorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnvFactor() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        envFactorSearchRepository.save(envFactor);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());

        // Update the envFactor
        EnvFactor updatedEnvFactor = envFactorRepository.findById(envFactor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnvFactor are not directly saved in db
        em.detach(updatedEnvFactor);
        updatedEnvFactor
            .reportId(UPDATED_REPORT_ID)
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);

        restEnvFactorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEnvFactor.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEnvFactor))
            )
            .andExpect(status().isOk());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnvFactorToMatchAllProperties(updatedEnvFactor);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EnvFactor> envFactorSearchList = Streamable.of(envFactorSearchRepository.findAll()).toList();
                EnvFactor testEnvFactorSearch = envFactorSearchList.get(searchDatabaseSizeAfter - 1);

                assertEnvFactorAllPropertiesEquals(testEnvFactorSearch, updatedEnvFactor);
            });
    }

    @Test
    @Transactional
    void putNonExistingEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        envFactor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvFactorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, envFactor.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(envFactor))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvFactorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(envFactor))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvFactorMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(envFactor)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEnvFactorWithPatch() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envFactor using partial update
        EnvFactor partialUpdatedEnvFactor = new EnvFactor();
        partialUpdatedEnvFactor.setId(envFactor.getId());

        partialUpdatedEnvFactor
            .reportId(UPDATED_REPORT_ID)
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);

        restEnvFactorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvFactor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvFactor))
            )
            .andExpect(status().isOk());

        // Validate the EnvFactor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvFactorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnvFactor, envFactor),
            getPersistedEnvFactor(envFactor)
        );
    }

    @Test
    @Transactional
    void fullUpdateEnvFactorWithPatch() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envFactor using partial update
        EnvFactor partialUpdatedEnvFactor = new EnvFactor();
        partialUpdatedEnvFactor.setId(envFactor.getId());

        partialUpdatedEnvFactor
            .reportId(UPDATED_REPORT_ID)
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);

        restEnvFactorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvFactor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvFactor))
            )
            .andExpect(status().isOk());

        // Validate the EnvFactor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvFactorUpdatableFieldsEquals(partialUpdatedEnvFactor, getPersistedEnvFactor(partialUpdatedEnvFactor));
    }

    @Test
    @Transactional
    void patchNonExistingEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        envFactor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvFactorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, envFactor.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(envFactor))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvFactorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(envFactor))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvFactorMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(envFactor))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEnvFactor() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);
        envFactorRepository.save(envFactor);
        envFactorSearchRepository.save(envFactor);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the envFactor
        restEnvFactorMockMvc
            .perform(delete(ENTITY_API_URL_ID, envFactor.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEnvFactor() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.saveAndFlush(envFactor);
        envFactorSearchRepository.save(envFactor);

        // Search the envFactor
        restEnvFactorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + envFactor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(envFactor.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].envFactorName").value(hasItem(DEFAULT_ENV_FACTOR_NAME)))
            .andExpect(jsonPath("$.[*].envFactorDistance").value(hasItem(sameNumber(DEFAULT_ENV_FACTOR_DISTANCE))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return envFactorRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected EnvFactor getPersistedEnvFactor(EnvFactor envFactor) {
        return envFactorRepository.findById(envFactor.getId()).orElseThrow();
    }

    protected void assertPersistedEnvFactorToMatchAllProperties(EnvFactor expectedEnvFactor) {
        assertEnvFactorAllPropertiesEquals(expectedEnvFactor, getPersistedEnvFactor(expectedEnvFactor));
    }

    protected void assertPersistedEnvFactorToMatchUpdatableProperties(EnvFactor expectedEnvFactor) {
        assertEnvFactorAllUpdatablePropertiesEquals(expectedEnvFactor, getPersistedEnvFactor(expectedEnvFactor));
    }
}
