package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.BathroomAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.BathroomRepository;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.search.BathroomSearchRepository;
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
 * Integration tests for the {@link BathroomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BathroomResourceIT {

    private static final String DEFAULT_BATHROOM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BATHROOM_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDTION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDTION_LEVEL = QualityStateType.MIDDLE;

    private static final BigDecimal DEFAULT_BATHROOM_SIZE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BATHROOM_SIZE = new BigDecimal(2);

    private static final QualityStateType DEFAULT_WATER_PRESSURE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_WATER_PRESSURE = QualityStateType.MIDDLE;

    private static final String DEFAULT_SHOWER_BOOTH_PRESENCE = "A";
    private static final String UPDATED_SHOWER_BOOTH_PRESENCE = "B";

    private static final String DEFAULT_BATHTUB_PRESENCE = "A";
    private static final String UPDATED_BATHTUB_PRESENCE = "B";

    private static final QualityStateType DEFAULT_FLOOR_AND_CEILING = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_FLOOR_AND_CEILING = QualityStateType.MIDDLE;

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bathrooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/bathrooms/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BathroomRepository bathroomRepository;

    @Autowired
    private BathroomSearchRepository bathroomSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Bathroom bathroom;

    private Bathroom insertedBathroom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bathroom createEntity() {
        return new Bathroom()
            .bathroomName(DEFAULT_BATHROOM_NAME)
            .condtionLevel(DEFAULT_CONDTION_LEVEL)
            .bathroomSize(DEFAULT_BATHROOM_SIZE)
            .waterPressure(DEFAULT_WATER_PRESSURE)
            .showerBoothPresence(DEFAULT_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(DEFAULT_BATHTUB_PRESENCE)
            .floorAndCeiling(DEFAULT_FLOOR_AND_CEILING)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bathroom createUpdatedEntity() {
        return new Bathroom()
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .bathroomSize(UPDATED_BATHROOM_SIZE)
            .waterPressure(UPDATED_WATER_PRESSURE)
            .showerBoothPresence(UPDATED_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Bathroom.class).block();
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
        bathroom = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBathroom != null) {
            bathroomRepository.delete(insertedBathroom).block();
            bathroomSearchRepository.delete(insertedBathroom).block();
            insertedBathroom = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBathroom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        // Create the Bathroom
        var returnedBathroom = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Bathroom.class)
            .returnResult()
            .getResponseBody();

        // Validate the Bathroom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBathroomUpdatableFieldsEquals(returnedBathroom, getPersistedBathroom(returnedBathroom));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBathroom = returnedBathroom;
    }

    @Test
    void createBathroomWithExistingId() throws Exception {
        // Create the Bathroom with an existing ID
        bathroom.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkBathroomNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        // set the field null
        bathroom.setBathroomName(null);

        // Create the Bathroom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCondtionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        // set the field null
        bathroom.setCondtionLevel(null);

        // Create the Bathroom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkWaterPressureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        // set the field null
        bathroom.setWaterPressure(null);

        // Create the Bathroom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFloorAndCeilingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        // set the field null
        bathroom.setFloorAndCeiling(null);

        // Create the Bathroom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBathrooms() {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();

        // Get all the bathroomList
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
            .value(hasItem(bathroom.getId().intValue()))
            .jsonPath("$.[*].bathroomName")
            .value(hasItem(DEFAULT_BATHROOM_NAME))
            .jsonPath("$.[*].condtionLevel")
            .value(hasItem(DEFAULT_CONDTION_LEVEL.toString()))
            .jsonPath("$.[*].bathroomSize")
            .value(hasItem(sameNumber(DEFAULT_BATHROOM_SIZE)))
            .jsonPath("$.[*].waterPressure")
            .value(hasItem(DEFAULT_WATER_PRESSURE.toString()))
            .jsonPath("$.[*].showerBoothPresence")
            .value(hasItem(DEFAULT_SHOWER_BOOTH_PRESENCE))
            .jsonPath("$.[*].bathtubPresence")
            .value(hasItem(DEFAULT_BATHTUB_PRESENCE))
            .jsonPath("$.[*].floorAndCeiling")
            .value(hasItem(DEFAULT_FLOOR_AND_CEILING.toString()))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getBathroom() {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();

        // Get the bathroom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bathroom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bathroom.getId().intValue()))
            .jsonPath("$.bathroomName")
            .value(is(DEFAULT_BATHROOM_NAME))
            .jsonPath("$.condtionLevel")
            .value(is(DEFAULT_CONDTION_LEVEL.toString()))
            .jsonPath("$.bathroomSize")
            .value(is(sameNumber(DEFAULT_BATHROOM_SIZE)))
            .jsonPath("$.waterPressure")
            .value(is(DEFAULT_WATER_PRESSURE.toString()))
            .jsonPath("$.showerBoothPresence")
            .value(is(DEFAULT_SHOWER_BOOTH_PRESENCE))
            .jsonPath("$.bathtubPresence")
            .value(is(DEFAULT_BATHTUB_PRESENCE))
            .jsonPath("$.floorAndCeiling")
            .value(is(DEFAULT_FLOOR_AND_CEILING.toString()))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingBathroom() {
        // Get the bathroom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBathroom() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        bathroomSearchRepository.save(bathroom).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());

        // Update the bathroom
        Bathroom updatedBathroom = bathroomRepository.findById(bathroom.getId()).block();
        updatedBathroom
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .bathroomSize(UPDATED_BATHROOM_SIZE)
            .waterPressure(UPDATED_WATER_PRESSURE)
            .showerBoothPresence(UPDATED_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBathroom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedBathroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBathroomToMatchAllProperties(updatedBathroom);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Bathroom> bathroomSearchList = Streamable.of(bathroomSearchRepository.findAll().collectList().block()).toList();
                Bathroom testBathroomSearch = bathroomSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertBathroomAllPropertiesEquals(testBathroomSearch, updatedBathroom);
                assertBathroomUpdatableFieldsEquals(testBathroomSearch, updatedBathroom);
            });
    }

    @Test
    void putNonExistingBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        bathroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bathroom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBathroomWithPatch() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bathroom using partial update
        Bathroom partialUpdatedBathroom = new Bathroom();
        partialUpdatedBathroom.setId(bathroom.getId());

        partialUpdatedBathroom
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBathroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBathroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bathroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBathroomUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBathroom, bathroom), getPersistedBathroom(bathroom));
    }

    @Test
    void fullUpdateBathroomWithPatch() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bathroom using partial update
        Bathroom partialUpdatedBathroom = new Bathroom();
        partialUpdatedBathroom.setId(bathroom.getId());

        partialUpdatedBathroom
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .bathroomSize(UPDATED_BATHROOM_SIZE)
            .waterPressure(UPDATED_WATER_PRESSURE)
            .showerBoothPresence(UPDATED_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBathroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBathroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bathroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBathroomUpdatableFieldsEquals(partialUpdatedBathroom, getPersistedBathroom(partialUpdatedBathroom));
    }

    @Test
    void patchNonExistingBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        bathroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bathroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bathroom))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBathroom() {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();
        bathroomRepository.save(bathroom).block();
        bathroomSearchRepository.save(bathroom).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bathroom
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bathroom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBathroom() {
        // Initialize the database
        insertedBathroom = bathroomRepository.save(bathroom).block();
        bathroomSearchRepository.save(bathroom).block();

        // Search the bathroom
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + bathroom.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(bathroom.getId().intValue()))
            .jsonPath("$.[*].bathroomName")
            .value(hasItem(DEFAULT_BATHROOM_NAME))
            .jsonPath("$.[*].condtionLevel")
            .value(hasItem(DEFAULT_CONDTION_LEVEL.toString()))
            .jsonPath("$.[*].bathroomSize")
            .value(hasItem(sameNumber(DEFAULT_BATHROOM_SIZE)))
            .jsonPath("$.[*].waterPressure")
            .value(hasItem(DEFAULT_WATER_PRESSURE.toString()))
            .jsonPath("$.[*].showerBoothPresence")
            .value(hasItem(DEFAULT_SHOWER_BOOTH_PRESENCE))
            .jsonPath("$.[*].bathtubPresence")
            .value(hasItem(DEFAULT_BATHTUB_PRESENCE))
            .jsonPath("$.[*].floorAndCeiling")
            .value(hasItem(DEFAULT_FLOOR_AND_CEILING.toString()))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return bathroomRepository.count().block();
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

    protected Bathroom getPersistedBathroom(Bathroom bathroom) {
        return bathroomRepository.findById(bathroom.getId()).block();
    }

    protected void assertPersistedBathroomToMatchAllProperties(Bathroom expectedBathroom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBathroomAllPropertiesEquals(expectedBathroom, getPersistedBathroom(expectedBathroom));
        assertBathroomUpdatableFieldsEquals(expectedBathroom, getPersistedBathroom(expectedBathroom));
    }

    protected void assertPersistedBathroomToMatchUpdatableProperties(Bathroom expectedBathroom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBathroomAllUpdatablePropertiesEquals(expectedBathroom, getPersistedBathroom(expectedBathroom));
        assertBathroomUpdatableFieldsEquals(expectedBathroom, getPersistedBathroom(expectedBathroom));
    }
}
