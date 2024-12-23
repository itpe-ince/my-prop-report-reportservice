package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.EnvFactorAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.EnvFactor;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.EnvFactorRepository;
import com.dnc.mprs.reportservice.repository.search.EnvFactorSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link EnvFactorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EnvFactorResourceIT {

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
    private WebTestClient webTestClient;

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
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(EnvFactor.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        envFactor = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEnvFactor != null) {
            envFactorRepository.delete(insertedEnvFactor).block();
            envFactorSearchRepository.delete(insertedEnvFactor).block();
            insertedEnvFactor = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEnvFactor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        // Create the EnvFactor
        var returnedEnvFactor = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EnvFactor.class)
            .returnResult()
            .getResponseBody();

        // Validate the EnvFactor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEnvFactorUpdatableFieldsEquals(returnedEnvFactor, getPersistedEnvFactor(returnedEnvFactor));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEnvFactor = returnedEnvFactor;
    }

    @Test
    void createEnvFactorWithExistingId() throws Exception {
        // Create the EnvFactor with an existing ID
        envFactor.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEnvFactorNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        // set the field null
        envFactor.setEnvFactorName(null);

        // Create the EnvFactor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEnvFactors() {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();

        // Get all the envFactorList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(envFactor.getId().intValue()))
            .jsonPath("$.[*].envFactorName")
            .value(hasItem(DEFAULT_ENV_FACTOR_NAME))
            .jsonPath("$.[*].envFactorDistance")
            .value(hasItem(sameNumber(DEFAULT_ENV_FACTOR_DISTANCE)))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getEnvFactor() {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();

        // Get the envFactor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, envFactor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(envFactor.getId().intValue()))
            .jsonPath("$.envFactorName")
            .value(is(DEFAULT_ENV_FACTOR_NAME))
            .jsonPath("$.envFactorDistance")
            .value(is(sameNumber(DEFAULT_ENV_FACTOR_DISTANCE)))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingEnvFactor() {
        // Get the envFactor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEnvFactor() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        envFactorSearchRepository.save(envFactor).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());

        // Update the envFactor
        EnvFactor updatedEnvFactor = envFactorRepository.findById(envFactor.getId()).block();
        updatedEnvFactor.envFactorName(UPDATED_ENV_FACTOR_NAME).envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE).remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEnvFactor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedEnvFactor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnvFactorToMatchAllProperties(updatedEnvFactor);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<EnvFactor> envFactorSearchList = Streamable.of(envFactorSearchRepository.findAll().collectList().block()).toList();
                EnvFactor testEnvFactorSearch = envFactorSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertEnvFactorAllPropertiesEquals(testEnvFactorSearch, updatedEnvFactor);
                assertEnvFactorUpdatableFieldsEquals(testEnvFactorSearch, updatedEnvFactor);
            });
    }

    @Test
    void putNonExistingEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        envFactor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, envFactor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEnvFactorWithPatch() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envFactor using partial update
        EnvFactor partialUpdatedEnvFactor = new EnvFactor();
        partialUpdatedEnvFactor.setId(envFactor.getId());

        partialUpdatedEnvFactor
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnvFactor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEnvFactor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnvFactor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvFactorUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnvFactor, envFactor),
            getPersistedEnvFactor(envFactor)
        );
    }

    @Test
    void fullUpdateEnvFactorWithPatch() throws Exception {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the envFactor using partial update
        EnvFactor partialUpdatedEnvFactor = new EnvFactor();
        partialUpdatedEnvFactor.setId(envFactor.getId());

        partialUpdatedEnvFactor
            .envFactorName(UPDATED_ENV_FACTOR_NAME)
            .envFactorDistance(UPDATED_ENV_FACTOR_DISTANCE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEnvFactor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEnvFactor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EnvFactor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvFactorUpdatableFieldsEquals(partialUpdatedEnvFactor, getPersistedEnvFactor(partialUpdatedEnvFactor));
    }

    @Test
    void patchNonExistingEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        envFactor.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, envFactor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEnvFactor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        envFactor.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(envFactor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EnvFactor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEnvFactor() {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();
        envFactorRepository.save(envFactor).block();
        envFactorSearchRepository.save(envFactor).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the envFactor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, envFactor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(envFactorSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEnvFactor() {
        // Initialize the database
        insertedEnvFactor = envFactorRepository.save(envFactor).block();
        envFactorSearchRepository.save(envFactor).block();

        // Search the envFactor
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + envFactor.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(envFactor.getId().intValue()))
            .jsonPath("$.[*].envFactorName")
            .value(hasItem(DEFAULT_ENV_FACTOR_NAME))
            .jsonPath("$.[*].envFactorDistance")
            .value(hasItem(sameNumber(DEFAULT_ENV_FACTOR_DISTANCE)))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return envFactorRepository.count().block();
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
        return envFactorRepository.findById(envFactor.getId()).block();
    }

    protected void assertPersistedEnvFactorToMatchAllProperties(EnvFactor expectedEnvFactor) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEnvFactorAllPropertiesEquals(expectedEnvFactor, getPersistedEnvFactor(expectedEnvFactor));
        assertEnvFactorUpdatableFieldsEquals(expectedEnvFactor, getPersistedEnvFactor(expectedEnvFactor));
    }

    protected void assertPersistedEnvFactorToMatchUpdatableProperties(EnvFactor expectedEnvFactor) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEnvFactorAllUpdatablePropertiesEquals(expectedEnvFactor, getPersistedEnvFactor(expectedEnvFactor));
        assertEnvFactorUpdatableFieldsEquals(expectedEnvFactor, getPersistedEnvFactor(expectedEnvFactor));
    }
}
