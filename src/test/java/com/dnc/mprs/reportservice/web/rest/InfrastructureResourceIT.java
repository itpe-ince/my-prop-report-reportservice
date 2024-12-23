package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.InfrastructureAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.domain.enumeration.InfraType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.InfrastructureRepository;
import com.dnc.mprs.reportservice.repository.search.InfrastructureSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link InfrastructureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class InfrastructureResourceIT {

    private static final InfraType DEFAULT_INFRA_TYPE = InfraType.SCHOOL;
    private static final InfraType UPDATED_INFRA_TYPE = InfraType.TRANSPRT;

    private static final String DEFAULT_INFRA_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INFRA_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDITION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDITION_LEVEL = QualityStateType.MIDDLE;

    private static final Integer DEFAULT_INFRA_DISTANCE = 1;
    private static final Integer UPDATED_INFRA_DISTANCE = 2;

    private static final QualityStateType DEFAULT_INFRA_DISTANCE_UNIT = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_INFRA_DISTANCE_UNIT = QualityStateType.MIDDLE;

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/infrastructures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/infrastructures/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InfrastructureRepository infrastructureRepository;

    @Autowired
    private InfrastructureSearchRepository infrastructureSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Infrastructure infrastructure;

    private Infrastructure insertedInfrastructure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Infrastructure createEntity() {
        return new Infrastructure()
            .infraType(DEFAULT_INFRA_TYPE)
            .infraName(DEFAULT_INFRA_NAME)
            .conditionLevel(DEFAULT_CONDITION_LEVEL)
            .infraDistance(DEFAULT_INFRA_DISTANCE)
            .infraDistanceUnit(DEFAULT_INFRA_DISTANCE_UNIT)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Infrastructure createUpdatedEntity() {
        return new Infrastructure()
            .infraType(UPDATED_INFRA_TYPE)
            .infraName(UPDATED_INFRA_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .infraDistance(UPDATED_INFRA_DISTANCE)
            .infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Infrastructure.class).block();
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
        infrastructure = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInfrastructure != null) {
            infrastructureRepository.delete(insertedInfrastructure).block();
            infrastructureSearchRepository.delete(insertedInfrastructure).block();
            insertedInfrastructure = null;
        }
        deleteEntities(em);
    }

    @Test
    void createInfrastructure() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        // Create the Infrastructure
        var returnedInfrastructure = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Infrastructure.class)
            .returnResult()
            .getResponseBody();

        // Validate the Infrastructure in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInfrastructureUpdatableFieldsEquals(returnedInfrastructure, getPersistedInfrastructure(returnedInfrastructure));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInfrastructure = returnedInfrastructure;
    }

    @Test
    void createInfrastructureWithExistingId() throws Exception {
        // Create the Infrastructure with an existing ID
        infrastructure.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkInfraTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        // set the field null
        infrastructure.setInfraType(null);

        // Create the Infrastructure, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkInfraNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        // set the field null
        infrastructure.setInfraName(null);

        // Create the Infrastructure, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        // set the field null
        infrastructure.setConditionLevel(null);

        // Create the Infrastructure, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllInfrastructures() {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();

        // Get all the infrastructureList
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
            .value(hasItem(infrastructure.getId().intValue()))
            .jsonPath("$.[*].infraType")
            .value(hasItem(DEFAULT_INFRA_TYPE.toString()))
            .jsonPath("$.[*].infraName")
            .value(hasItem(DEFAULT_INFRA_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].infraDistance")
            .value(hasItem(DEFAULT_INFRA_DISTANCE))
            .jsonPath("$.[*].infraDistanceUnit")
            .value(hasItem(DEFAULT_INFRA_DISTANCE_UNIT.toString()))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getInfrastructure() {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();

        // Get the infrastructure
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, infrastructure.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(infrastructure.getId().intValue()))
            .jsonPath("$.infraType")
            .value(is(DEFAULT_INFRA_TYPE.toString()))
            .jsonPath("$.infraName")
            .value(is(DEFAULT_INFRA_NAME))
            .jsonPath("$.conditionLevel")
            .value(is(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.infraDistance")
            .value(is(DEFAULT_INFRA_DISTANCE))
            .jsonPath("$.infraDistanceUnit")
            .value(is(DEFAULT_INFRA_DISTANCE_UNIT.toString()))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingInfrastructure() {
        // Get the infrastructure
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingInfrastructure() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        infrastructureSearchRepository.save(infrastructure).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());

        // Update the infrastructure
        Infrastructure updatedInfrastructure = infrastructureRepository.findById(infrastructure.getId()).block();
        updatedInfrastructure
            .infraType(UPDATED_INFRA_TYPE)
            .infraName(UPDATED_INFRA_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .infraDistance(UPDATED_INFRA_DISTANCE)
            .infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedInfrastructure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedInfrastructure))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInfrastructureToMatchAllProperties(updatedInfrastructure);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Infrastructure> infrastructureSearchList = Streamable.of(
                    infrastructureSearchRepository.findAll().collectList().block()
                ).toList();
                Infrastructure testInfrastructureSearch = infrastructureSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertInfrastructureAllPropertiesEquals(testInfrastructureSearch, updatedInfrastructure);
                assertInfrastructureUpdatableFieldsEquals(testInfrastructureSearch, updatedInfrastructure);
            });
    }

    @Test
    void putNonExistingInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        infrastructure.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, infrastructure.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateInfrastructureWithPatch() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infrastructure using partial update
        Infrastructure partialUpdatedInfrastructure = new Infrastructure();
        partialUpdatedInfrastructure.setId(infrastructure.getId());

        partialUpdatedInfrastructure.conditionLevel(UPDATED_CONDITION_LEVEL).infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInfrastructure.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInfrastructure))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Infrastructure in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfrastructureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInfrastructure, infrastructure),
            getPersistedInfrastructure(infrastructure)
        );
    }

    @Test
    void fullUpdateInfrastructureWithPatch() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infrastructure using partial update
        Infrastructure partialUpdatedInfrastructure = new Infrastructure();
        partialUpdatedInfrastructure.setId(infrastructure.getId());

        partialUpdatedInfrastructure
            .infraType(UPDATED_INFRA_TYPE)
            .infraName(UPDATED_INFRA_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .infraDistance(UPDATED_INFRA_DISTANCE)
            .infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedInfrastructure.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedInfrastructure))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Infrastructure in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfrastructureUpdatableFieldsEquals(partialUpdatedInfrastructure, getPersistedInfrastructure(partialUpdatedInfrastructure));
    }

    @Test
    void patchNonExistingInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        infrastructure.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, infrastructure.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(infrastructure))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteInfrastructure() {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();
        infrastructureRepository.save(infrastructure).block();
        infrastructureSearchRepository.save(infrastructure).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the infrastructure
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, infrastructure.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchInfrastructure() {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.save(infrastructure).block();
        infrastructureSearchRepository.save(infrastructure).block();

        // Search the infrastructure
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + infrastructure.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(infrastructure.getId().intValue()))
            .jsonPath("$.[*].infraType")
            .value(hasItem(DEFAULT_INFRA_TYPE.toString()))
            .jsonPath("$.[*].infraName")
            .value(hasItem(DEFAULT_INFRA_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].infraDistance")
            .value(hasItem(DEFAULT_INFRA_DISTANCE))
            .jsonPath("$.[*].infraDistanceUnit")
            .value(hasItem(DEFAULT_INFRA_DISTANCE_UNIT.toString()))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return infrastructureRepository.count().block();
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

    protected Infrastructure getPersistedInfrastructure(Infrastructure infrastructure) {
        return infrastructureRepository.findById(infrastructure.getId()).block();
    }

    protected void assertPersistedInfrastructureToMatchAllProperties(Infrastructure expectedInfrastructure) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInfrastructureAllPropertiesEquals(expectedInfrastructure, getPersistedInfrastructure(expectedInfrastructure));
        assertInfrastructureUpdatableFieldsEquals(expectedInfrastructure, getPersistedInfrastructure(expectedInfrastructure));
    }

    protected void assertPersistedInfrastructureToMatchUpdatableProperties(Infrastructure expectedInfrastructure) {
        // Test fails because reactive api returns an empty object instead of null
        // assertInfrastructureAllUpdatablePropertiesEquals(expectedInfrastructure, getPersistedInfrastructure(expectedInfrastructure));
        assertInfrastructureUpdatableFieldsEquals(expectedInfrastructure, getPersistedInfrastructure(expectedInfrastructure));
    }
}
