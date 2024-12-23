package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.EntranceAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.EntranceRepository;
import com.dnc.mprs.reportservice.repository.search.EntranceSearchRepository;
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
 * Integration tests for the {@link EntranceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EntranceResourceIT {

    private static final String DEFAULT_ENTRANCE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTRANCE_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDTION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDTION_LEVEL = QualityStateType.MIDDLE;

    private static final BigDecimal DEFAULT_ENTRANCE_SIZE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ENTRANCE_SIZE = new BigDecimal(2);

    private static final BigDecimal DEFAULT_SHOE_RACK_SIZE = new BigDecimal(1);
    private static final BigDecimal UPDATED_SHOE_RACK_SIZE = new BigDecimal(2);

    private static final String DEFAULT_PANTRY_PRESENCE = "A";
    private static final String UPDATED_PANTRY_PRESENCE = "B";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/entrances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/entrances/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntranceRepository entranceRepository;

    @Autowired
    private EntranceSearchRepository entranceSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Entrance entrance;

    private Entrance insertedEntrance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Entrance createEntity() {
        return new Entrance()
            .entranceName(DEFAULT_ENTRANCE_NAME)
            .condtionLevel(DEFAULT_CONDTION_LEVEL)
            .entranceSize(DEFAULT_ENTRANCE_SIZE)
            .shoeRackSize(DEFAULT_SHOE_RACK_SIZE)
            .pantryPresence(DEFAULT_PANTRY_PRESENCE)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Entrance createUpdatedEntity() {
        return new Entrance()
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Entrance.class).block();
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
        entrance = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntrance != null) {
            entranceRepository.delete(insertedEntrance).block();
            entranceSearchRepository.delete(insertedEntrance).block();
            insertedEntrance = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEntrance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        // Create the Entrance
        var returnedEntrance = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Entrance.class)
            .returnResult()
            .getResponseBody();

        // Validate the Entrance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEntranceUpdatableFieldsEquals(returnedEntrance, getPersistedEntrance(returnedEntrance));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEntrance = returnedEntrance;
    }

    @Test
    void createEntranceWithExistingId() throws Exception {
        // Create the Entrance with an existing ID
        entrance.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkEntranceNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        // set the field null
        entrance.setEntranceName(null);

        // Create the Entrance, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCondtionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        // set the field null
        entrance.setCondtionLevel(null);

        // Create the Entrance, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllEntrances() {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();

        // Get all the entranceList
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
            .value(hasItem(entrance.getId().intValue()))
            .jsonPath("$.[*].entranceName")
            .value(hasItem(DEFAULT_ENTRANCE_NAME))
            .jsonPath("$.[*].condtionLevel")
            .value(hasItem(DEFAULT_CONDTION_LEVEL.toString()))
            .jsonPath("$.[*].entranceSize")
            .value(hasItem(sameNumber(DEFAULT_ENTRANCE_SIZE)))
            .jsonPath("$.[*].shoeRackSize")
            .value(hasItem(sameNumber(DEFAULT_SHOE_RACK_SIZE)))
            .jsonPath("$.[*].pantryPresence")
            .value(hasItem(DEFAULT_PANTRY_PRESENCE))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getEntrance() {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();

        // Get the entrance
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, entrance.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(entrance.getId().intValue()))
            .jsonPath("$.entranceName")
            .value(is(DEFAULT_ENTRANCE_NAME))
            .jsonPath("$.condtionLevel")
            .value(is(DEFAULT_CONDTION_LEVEL.toString()))
            .jsonPath("$.entranceSize")
            .value(is(sameNumber(DEFAULT_ENTRANCE_SIZE)))
            .jsonPath("$.shoeRackSize")
            .value(is(sameNumber(DEFAULT_SHOE_RACK_SIZE)))
            .jsonPath("$.pantryPresence")
            .value(is(DEFAULT_PANTRY_PRESENCE))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingEntrance() {
        // Get the entrance
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEntrance() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        entranceSearchRepository.save(entrance).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());

        // Update the entrance
        Entrance updatedEntrance = entranceRepository.findById(entrance.getId()).block();
        updatedEntrance
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEntrance.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedEntrance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntranceToMatchAllProperties(updatedEntrance);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Entrance> entranceSearchList = Streamable.of(entranceSearchRepository.findAll().collectList().block()).toList();
                Entrance testEntranceSearch = entranceSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertEntranceAllPropertiesEquals(testEntranceSearch, updatedEntrance);
                assertEntranceUpdatableFieldsEquals(testEntranceSearch, updatedEntrance);
            });
    }

    @Test
    void putNonExistingEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        entrance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, entrance.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateEntranceWithPatch() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entrance using partial update
        Entrance partialUpdatedEntrance = new Entrance();
        partialUpdatedEntrance.setId(entrance.getId());

        partialUpdatedEntrance
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEntrance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEntrance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Entrance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntranceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEntrance, entrance), getPersistedEntrance(entrance));
    }

    @Test
    void fullUpdateEntranceWithPatch() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entrance using partial update
        Entrance partialUpdatedEntrance = new Entrance();
        partialUpdatedEntrance.setId(entrance.getId());

        partialUpdatedEntrance
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEntrance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEntrance))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Entrance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntranceUpdatableFieldsEquals(partialUpdatedEntrance, getPersistedEntrance(partialUpdatedEntrance));
    }

    @Test
    void patchNonExistingEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        entrance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, entrance.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(entrance))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteEntrance() {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();
        entranceRepository.save(entrance).block();
        entranceSearchRepository.save(entrance).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the entrance
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, entrance.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchEntrance() {
        // Initialize the database
        insertedEntrance = entranceRepository.save(entrance).block();
        entranceSearchRepository.save(entrance).block();

        // Search the entrance
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + entrance.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(entrance.getId().intValue()))
            .jsonPath("$.[*].entranceName")
            .value(hasItem(DEFAULT_ENTRANCE_NAME))
            .jsonPath("$.[*].condtionLevel")
            .value(hasItem(DEFAULT_CONDTION_LEVEL.toString()))
            .jsonPath("$.[*].entranceSize")
            .value(hasItem(sameNumber(DEFAULT_ENTRANCE_SIZE)))
            .jsonPath("$.[*].shoeRackSize")
            .value(hasItem(sameNumber(DEFAULT_SHOE_RACK_SIZE)))
            .jsonPath("$.[*].pantryPresence")
            .value(hasItem(DEFAULT_PANTRY_PRESENCE))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return entranceRepository.count().block();
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

    protected Entrance getPersistedEntrance(Entrance entrance) {
        return entranceRepository.findById(entrance.getId()).block();
    }

    protected void assertPersistedEntranceToMatchAllProperties(Entrance expectedEntrance) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEntranceAllPropertiesEquals(expectedEntrance, getPersistedEntrance(expectedEntrance));
        assertEntranceUpdatableFieldsEquals(expectedEntrance, getPersistedEntrance(expectedEntrance));
    }

    protected void assertPersistedEntranceToMatchUpdatableProperties(Entrance expectedEntrance) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEntranceAllUpdatablePropertiesEquals(expectedEntrance, getPersistedEntrance(expectedEntrance));
        assertEntranceUpdatableFieldsEquals(expectedEntrance, getPersistedEntrance(expectedEntrance));
    }
}
