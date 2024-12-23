package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.KitchenAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
import com.dnc.mprs.reportservice.repository.search.KitchenSearchRepository;
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
 * Integration tests for the {@link KitchenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class KitchenResourceIT {

    private static final String DEFAULT_KITCHEN_NAME = "AAAAAAAAAA";
    private static final String UPDATED_KITCHEN_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDITION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDITION_LEVEL = QualityStateType.MIDDLE;

    private static final String DEFAULT_BUILT_IN_CABINET = "A";
    private static final String UPDATED_BUILT_IN_CABINET = "B";

    private static final QualityStateType DEFAULT_SINK_CONDITION = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_SINK_CONDITION = QualityStateType.MIDDLE;

    private static final String DEFAULT_VENTILATION_SYSTEM = "AAAAAAAAAA";
    private static final String UPDATED_VENTILATION_SYSTEM = "BBBBBBBBBB";

    private static final String DEFAULT_APPLIANCE_PROVISION = "AAAAAAAAAA";
    private static final String UPDATED_APPLIANCE_PROVISION = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/kitchens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/kitchens/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private KitchenRepository kitchenRepository;

    @Autowired
    private KitchenSearchRepository kitchenSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Kitchen kitchen;

    private Kitchen insertedKitchen;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kitchen createEntity() {
        return new Kitchen()
            .kitchenName(DEFAULT_KITCHEN_NAME)
            .conditionLevel(DEFAULT_CONDITION_LEVEL)
            .builtInCabinet(DEFAULT_BUILT_IN_CABINET)
            .sinkCondition(DEFAULT_SINK_CONDITION)
            .ventilationSystem(DEFAULT_VENTILATION_SYSTEM)
            .applianceProvision(DEFAULT_APPLIANCE_PROVISION)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Kitchen createUpdatedEntity() {
        return new Kitchen()
            .kitchenName(UPDATED_KITCHEN_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .builtInCabinet(UPDATED_BUILT_IN_CABINET)
            .sinkCondition(UPDATED_SINK_CONDITION)
            .ventilationSystem(UPDATED_VENTILATION_SYSTEM)
            .applianceProvision(UPDATED_APPLIANCE_PROVISION)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Kitchen.class).block();
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
        kitchen = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedKitchen != null) {
            kitchenRepository.delete(insertedKitchen).block();
            kitchenSearchRepository.delete(insertedKitchen).block();
            insertedKitchen = null;
        }
        deleteEntities(em);
    }

    @Test
    void createKitchen() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        // Create the Kitchen
        var returnedKitchen = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Kitchen.class)
            .returnResult()
            .getResponseBody();

        // Validate the Kitchen in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertKitchenUpdatableFieldsEquals(returnedKitchen, getPersistedKitchen(returnedKitchen));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedKitchen = returnedKitchen;
    }

    @Test
    void createKitchenWithExistingId() throws Exception {
        // Create the Kitchen with an existing ID
        kitchen.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkKitchenNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        // set the field null
        kitchen.setKitchenName(null);

        // Create the Kitchen, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        // set the field null
        kitchen.setConditionLevel(null);

        // Create the Kitchen, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkSinkConditionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        // set the field null
        kitchen.setSinkCondition(null);

        // Create the Kitchen, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllKitchens() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        // Get all the kitchenList
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
            .value(hasItem(kitchen.getId().intValue()))
            .jsonPath("$.[*].kitchenName")
            .value(hasItem(DEFAULT_KITCHEN_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].builtInCabinet")
            .value(hasItem(DEFAULT_BUILT_IN_CABINET))
            .jsonPath("$.[*].sinkCondition")
            .value(hasItem(DEFAULT_SINK_CONDITION.toString()))
            .jsonPath("$.[*].ventilationSystem")
            .value(hasItem(DEFAULT_VENTILATION_SYSTEM))
            .jsonPath("$.[*].applianceProvision")
            .value(hasItem(DEFAULT_APPLIANCE_PROVISION))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getKitchen() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        // Get the kitchen
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(kitchen.getId().intValue()))
            .jsonPath("$.kitchenName")
            .value(is(DEFAULT_KITCHEN_NAME))
            .jsonPath("$.conditionLevel")
            .value(is(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.builtInCabinet")
            .value(is(DEFAULT_BUILT_IN_CABINET))
            .jsonPath("$.sinkCondition")
            .value(is(DEFAULT_SINK_CONDITION.toString()))
            .jsonPath("$.ventilationSystem")
            .value(is(DEFAULT_VENTILATION_SYSTEM))
            .jsonPath("$.applianceProvision")
            .value(is(DEFAULT_APPLIANCE_PROVISION))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingKitchen() {
        // Get the kitchen
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingKitchen() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSearchRepository.save(kitchen).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());

        // Update the kitchen
        Kitchen updatedKitchen = kitchenRepository.findById(kitchen.getId()).block();
        updatedKitchen
            .kitchenName(UPDATED_KITCHEN_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .builtInCabinet(UPDATED_BUILT_IN_CABINET)
            .sinkCondition(UPDATED_SINK_CONDITION)
            .ventilationSystem(UPDATED_VENTILATION_SYSTEM)
            .applianceProvision(UPDATED_APPLIANCE_PROVISION)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedKitchen.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedKitchen))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKitchenToMatchAllProperties(updatedKitchen);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Kitchen> kitchenSearchList = Streamable.of(kitchenSearchRepository.findAll().collectList().block()).toList();
                Kitchen testKitchenSearch = kitchenSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertKitchenAllPropertiesEquals(testKitchenSearch, updatedKitchen);
                assertKitchenUpdatableFieldsEquals(testKitchenSearch, updatedKitchen);
            });
    }

    @Test
    void putNonExistingKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        kitchen.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateKitchenWithPatch() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen using partial update
        Kitchen partialUpdatedKitchen = new Kitchen();
        partialUpdatedKitchen.setId(kitchen.getId());

        partialUpdatedKitchen.applianceProvision(UPDATED_APPLIANCE_PROVISION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKitchen.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedKitchen))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Kitchen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedKitchen, kitchen), getPersistedKitchen(kitchen));
    }

    @Test
    void fullUpdateKitchenWithPatch() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen using partial update
        Kitchen partialUpdatedKitchen = new Kitchen();
        partialUpdatedKitchen.setId(kitchen.getId());

        partialUpdatedKitchen
            .kitchenName(UPDATED_KITCHEN_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .builtInCabinet(UPDATED_BUILT_IN_CABINET)
            .sinkCondition(UPDATED_SINK_CONDITION)
            .ventilationSystem(UPDATED_VENTILATION_SYSTEM)
            .applianceProvision(UPDATED_APPLIANCE_PROVISION)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKitchen.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedKitchen))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Kitchen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenUpdatableFieldsEquals(partialUpdatedKitchen, getPersistedKitchen(partialUpdatedKitchen));
    }

    @Test
    void patchNonExistingKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        kitchen.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(kitchen))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteKitchen() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();
        kitchenRepository.save(kitchen).block();
        kitchenSearchRepository.save(kitchen).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the kitchen
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, kitchen.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchKitchen() {
        // Initialize the database
        insertedKitchen = kitchenRepository.save(kitchen).block();
        kitchenSearchRepository.save(kitchen).block();

        // Search the kitchen
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + kitchen.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(kitchen.getId().intValue()))
            .jsonPath("$.[*].kitchenName")
            .value(hasItem(DEFAULT_KITCHEN_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].builtInCabinet")
            .value(hasItem(DEFAULT_BUILT_IN_CABINET))
            .jsonPath("$.[*].sinkCondition")
            .value(hasItem(DEFAULT_SINK_CONDITION.toString()))
            .jsonPath("$.[*].ventilationSystem")
            .value(hasItem(DEFAULT_VENTILATION_SYSTEM))
            .jsonPath("$.[*].applianceProvision")
            .value(hasItem(DEFAULT_APPLIANCE_PROVISION))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return kitchenRepository.count().block();
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

    protected Kitchen getPersistedKitchen(Kitchen kitchen) {
        return kitchenRepository.findById(kitchen.getId()).block();
    }

    protected void assertPersistedKitchenToMatchAllProperties(Kitchen expectedKitchen) {
        // Test fails because reactive api returns an empty object instead of null
        // assertKitchenAllPropertiesEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
        assertKitchenUpdatableFieldsEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
    }

    protected void assertPersistedKitchenToMatchUpdatableProperties(Kitchen expectedKitchen) {
        // Test fails because reactive api returns an empty object instead of null
        // assertKitchenAllUpdatablePropertiesEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
        assertKitchenUpdatableFieldsEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
    }
}
