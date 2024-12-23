package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.BedroomAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.BedroomRepository;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.search.BedroomSearchRepository;
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
 * Integration tests for the {@link BedroomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BedroomResourceIT {

    private static final String DEFAULT_BEDROOM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BEDROOM_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDITION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDITION_LEVEL = QualityStateType.MIDDLE;

    private static final BigDecimal DEFAULT_ROOM_SIZE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ROOM_SIZE = new BigDecimal(2);

    private static final String DEFAULT_CLOSET_YN = "A";
    private static final String UPDATED_CLOSET_YN = "B";

    private static final String DEFAULT_AC_YN = "A";
    private static final String UPDATED_AC_YN = "B";

    private static final String DEFAULT_WINDOW_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_WINDOW_LOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_WINDOW_SIZE = "AAAAAAAAAA";
    private static final String UPDATED_WINDOW_SIZE = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/bedrooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/bedrooms/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BedroomRepository bedroomRepository;

    @Autowired
    private BedroomSearchRepository bedroomSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Bedroom bedroom;

    private Bedroom insertedBedroom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bedroom createEntity() {
        return new Bedroom()
            .bedroomName(DEFAULT_BEDROOM_NAME)
            .conditionLevel(DEFAULT_CONDITION_LEVEL)
            .roomSize(DEFAULT_ROOM_SIZE)
            .closetYn(DEFAULT_CLOSET_YN)
            .acYn(DEFAULT_AC_YN)
            .windowLocation(DEFAULT_WINDOW_LOCATION)
            .windowSize(DEFAULT_WINDOW_SIZE)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bedroom createUpdatedEntity() {
        return new Bedroom()
            .bedroomName(UPDATED_BEDROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .closetYn(UPDATED_CLOSET_YN)
            .acYn(UPDATED_AC_YN)
            .windowLocation(UPDATED_WINDOW_LOCATION)
            .windowSize(UPDATED_WINDOW_SIZE)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Bedroom.class).block();
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
        bedroom = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBedroom != null) {
            bedroomRepository.delete(insertedBedroom).block();
            bedroomSearchRepository.delete(insertedBedroom).block();
            insertedBedroom = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBedroom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        // Create the Bedroom
        var returnedBedroom = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Bedroom.class)
            .returnResult()
            .getResponseBody();

        // Validate the Bedroom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBedroomUpdatableFieldsEquals(returnedBedroom, getPersistedBedroom(returnedBedroom));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBedroom = returnedBedroom;
    }

    @Test
    void createBedroomWithExistingId() throws Exception {
        // Create the Bedroom with an existing ID
        bedroom.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkBedroomNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        // set the field null
        bedroom.setBedroomName(null);

        // Create the Bedroom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        // set the field null
        bedroom.setConditionLevel(null);

        // Create the Bedroom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllBedrooms() {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();

        // Get all the bedroomList
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
            .value(hasItem(bedroom.getId().intValue()))
            .jsonPath("$.[*].bedroomName")
            .value(hasItem(DEFAULT_BEDROOM_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].roomSize")
            .value(hasItem(sameNumber(DEFAULT_ROOM_SIZE)))
            .jsonPath("$.[*].closetYn")
            .value(hasItem(DEFAULT_CLOSET_YN))
            .jsonPath("$.[*].acYn")
            .value(hasItem(DEFAULT_AC_YN))
            .jsonPath("$.[*].windowLocation")
            .value(hasItem(DEFAULT_WINDOW_LOCATION))
            .jsonPath("$.[*].windowSize")
            .value(hasItem(DEFAULT_WINDOW_SIZE))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getBedroom() {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();

        // Get the bedroom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, bedroom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(bedroom.getId().intValue()))
            .jsonPath("$.bedroomName")
            .value(is(DEFAULT_BEDROOM_NAME))
            .jsonPath("$.conditionLevel")
            .value(is(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.roomSize")
            .value(is(sameNumber(DEFAULT_ROOM_SIZE)))
            .jsonPath("$.closetYn")
            .value(is(DEFAULT_CLOSET_YN))
            .jsonPath("$.acYn")
            .value(is(DEFAULT_AC_YN))
            .jsonPath("$.windowLocation")
            .value(is(DEFAULT_WINDOW_LOCATION))
            .jsonPath("$.windowSize")
            .value(is(DEFAULT_WINDOW_SIZE))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingBedroom() {
        // Get the bedroom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBedroom() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        bedroomSearchRepository.save(bedroom).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());

        // Update the bedroom
        Bedroom updatedBedroom = bedroomRepository.findById(bedroom.getId()).block();
        updatedBedroom
            .bedroomName(UPDATED_BEDROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .closetYn(UPDATED_CLOSET_YN)
            .acYn(UPDATED_AC_YN)
            .windowLocation(UPDATED_WINDOW_LOCATION)
            .windowSize(UPDATED_WINDOW_SIZE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBedroom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedBedroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBedroomToMatchAllProperties(updatedBedroom);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Bedroom> bedroomSearchList = Streamable.of(bedroomSearchRepository.findAll().collectList().block()).toList();
                Bedroom testBedroomSearch = bedroomSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertBedroomAllPropertiesEquals(testBedroomSearch, updatedBedroom);
                assertBedroomUpdatableFieldsEquals(testBedroomSearch, updatedBedroom);
            });
    }

    @Test
    void putNonExistingBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        bedroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bedroom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateBedroomWithPatch() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bedroom using partial update
        Bedroom partialUpdatedBedroom = new Bedroom();
        partialUpdatedBedroom.setId(bedroom.getId());

        partialUpdatedBedroom.conditionLevel(UPDATED_CONDITION_LEVEL).roomSize(UPDATED_ROOM_SIZE).remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBedroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBedroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bedroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBedroomUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBedroom, bedroom), getPersistedBedroom(bedroom));
    }

    @Test
    void fullUpdateBedroomWithPatch() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bedroom using partial update
        Bedroom partialUpdatedBedroom = new Bedroom();
        partialUpdatedBedroom.setId(bedroom.getId());

        partialUpdatedBedroom
            .bedroomName(UPDATED_BEDROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .closetYn(UPDATED_CLOSET_YN)
            .acYn(UPDATED_AC_YN)
            .windowLocation(UPDATED_WINDOW_LOCATION)
            .windowSize(UPDATED_WINDOW_SIZE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBedroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBedroom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Bedroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBedroomUpdatableFieldsEquals(partialUpdatedBedroom, getPersistedBedroom(partialUpdatedBedroom));
    }

    @Test
    void patchNonExistingBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        bedroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bedroom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(bedroom))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteBedroom() {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();
        bedroomRepository.save(bedroom).block();
        bedroomSearchRepository.save(bedroom).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bedroom
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, bedroom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchBedroom() {
        // Initialize the database
        insertedBedroom = bedroomRepository.save(bedroom).block();
        bedroomSearchRepository.save(bedroom).block();

        // Search the bedroom
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + bedroom.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(bedroom.getId().intValue()))
            .jsonPath("$.[*].bedroomName")
            .value(hasItem(DEFAULT_BEDROOM_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].roomSize")
            .value(hasItem(sameNumber(DEFAULT_ROOM_SIZE)))
            .jsonPath("$.[*].closetYn")
            .value(hasItem(DEFAULT_CLOSET_YN))
            .jsonPath("$.[*].acYn")
            .value(hasItem(DEFAULT_AC_YN))
            .jsonPath("$.[*].windowLocation")
            .value(hasItem(DEFAULT_WINDOW_LOCATION))
            .jsonPath("$.[*].windowSize")
            .value(hasItem(DEFAULT_WINDOW_SIZE))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return bedroomRepository.count().block();
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

    protected Bedroom getPersistedBedroom(Bedroom bedroom) {
        return bedroomRepository.findById(bedroom.getId()).block();
    }

    protected void assertPersistedBedroomToMatchAllProperties(Bedroom expectedBedroom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBedroomAllPropertiesEquals(expectedBedroom, getPersistedBedroom(expectedBedroom));
        assertBedroomUpdatableFieldsEquals(expectedBedroom, getPersistedBedroom(expectedBedroom));
    }

    protected void assertPersistedBedroomToMatchUpdatableProperties(Bedroom expectedBedroom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBedroomAllUpdatablePropertiesEquals(expectedBedroom, getPersistedBedroom(expectedBedroom));
        assertBedroomUpdatableFieldsEquals(expectedBedroom, getPersistedBedroom(expectedBedroom));
    }
}
