package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.LivingRoomAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.LivingRoom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.LivingRoomRepository;
import com.dnc.mprs.reportservice.repository.search.LivingRoomSearchRepository;
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
 * Integration tests for the {@link LivingRoomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class LivingRoomResourceIT {

    private static final String DEFAULT_LIVING_ROOM_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LIVING_ROOM_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDITION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDITION_LEVEL = QualityStateType.MIDDLE;

    private static final BigDecimal DEFAULT_ROOM_SIZE = new BigDecimal(1);
    private static final BigDecimal UPDATED_ROOM_SIZE = new BigDecimal(2);

    private static final QualityStateType DEFAULT_WALL_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_WALL_STATE = QualityStateType.MIDDLE;

    private static final String DEFAULT_FLOOR_MATERIAL = "AAAAAAAAAA";
    private static final String UPDATED_FLOOR_MATERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_SUNLIGHT = "AAAAAAAAAA";
    private static final String UPDATED_SUNLIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/living-rooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/living-rooms/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LivingRoomRepository livingRoomRepository;

    @Autowired
    private LivingRoomSearchRepository livingRoomSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private LivingRoom livingRoom;

    private LivingRoom insertedLivingRoom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LivingRoom createEntity() {
        return new LivingRoom()
            .livingRoomName(DEFAULT_LIVING_ROOM_NAME)
            .conditionLevel(DEFAULT_CONDITION_LEVEL)
            .roomSize(DEFAULT_ROOM_SIZE)
            .wallState(DEFAULT_WALL_STATE)
            .floorMaterial(DEFAULT_FLOOR_MATERIAL)
            .sunlight(DEFAULT_SUNLIGHT)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LivingRoom createUpdatedEntity() {
        return new LivingRoom()
            .livingRoomName(UPDATED_LIVING_ROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(LivingRoom.class).block();
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
        livingRoom = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedLivingRoom != null) {
            livingRoomRepository.delete(insertedLivingRoom).block();
            livingRoomSearchRepository.delete(insertedLivingRoom).block();
            insertedLivingRoom = null;
        }
        deleteEntities(em);
    }

    @Test
    void createLivingRoom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        // Create the LivingRoom
        var returnedLivingRoom = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(LivingRoom.class)
            .returnResult()
            .getResponseBody();

        // Validate the LivingRoom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLivingRoomUpdatableFieldsEquals(returnedLivingRoom, getPersistedLivingRoom(returnedLivingRoom));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedLivingRoom = returnedLivingRoom;
    }

    @Test
    void createLivingRoomWithExistingId() throws Exception {
        // Create the LivingRoom with an existing ID
        livingRoom.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkLivingRoomNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        // set the field null
        livingRoom.setLivingRoomName(null);

        // Create the LivingRoom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        // set the field null
        livingRoom.setConditionLevel(null);

        // Create the LivingRoom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkWallStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        // set the field null
        livingRoom.setWallState(null);

        // Create the LivingRoom, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllLivingRooms() {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();

        // Get all the livingRoomList
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
            .value(hasItem(livingRoom.getId().intValue()))
            .jsonPath("$.[*].livingRoomName")
            .value(hasItem(DEFAULT_LIVING_ROOM_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].roomSize")
            .value(hasItem(sameNumber(DEFAULT_ROOM_SIZE)))
            .jsonPath("$.[*].wallState")
            .value(hasItem(DEFAULT_WALL_STATE.toString()))
            .jsonPath("$.[*].floorMaterial")
            .value(hasItem(DEFAULT_FLOOR_MATERIAL))
            .jsonPath("$.[*].sunlight")
            .value(hasItem(DEFAULT_SUNLIGHT))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    @Test
    void getLivingRoom() {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();

        // Get the livingRoom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, livingRoom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(livingRoom.getId().intValue()))
            .jsonPath("$.livingRoomName")
            .value(is(DEFAULT_LIVING_ROOM_NAME))
            .jsonPath("$.conditionLevel")
            .value(is(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.roomSize")
            .value(is(sameNumber(DEFAULT_ROOM_SIZE)))
            .jsonPath("$.wallState")
            .value(is(DEFAULT_WALL_STATE.toString()))
            .jsonPath("$.floorMaterial")
            .value(is(DEFAULT_FLOOR_MATERIAL))
            .jsonPath("$.sunlight")
            .value(is(DEFAULT_SUNLIGHT))
            .jsonPath("$.remarks")
            .value(is(DEFAULT_REMARKS));
    }

    @Test
    void getNonExistingLivingRoom() {
        // Get the livingRoom
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingLivingRoom() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        livingRoomSearchRepository.save(livingRoom).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());

        // Update the livingRoom
        LivingRoom updatedLivingRoom = livingRoomRepository.findById(livingRoom.getId()).block();
        updatedLivingRoom
            .livingRoomName(UPDATED_LIVING_ROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedLivingRoom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedLivingRoom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLivingRoomToMatchAllProperties(updatedLivingRoom);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<LivingRoom> livingRoomSearchList = Streamable.of(livingRoomSearchRepository.findAll().collectList().block()).toList();
                LivingRoom testLivingRoomSearch = livingRoomSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertLivingRoomAllPropertiesEquals(testLivingRoomSearch, updatedLivingRoom);
                assertLivingRoomUpdatableFieldsEquals(testLivingRoomSearch, updatedLivingRoom);
            });
    }

    @Test
    void putNonExistingLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        livingRoom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, livingRoom.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateLivingRoomWithPatch() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livingRoom using partial update
        LivingRoom partialUpdatedLivingRoom = new LivingRoom();
        partialUpdatedLivingRoom.setId(livingRoom.getId());

        partialUpdatedLivingRoom
            .roomSize(UPDATED_ROOM_SIZE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLivingRoom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLivingRoom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LivingRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivingRoomUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLivingRoom, livingRoom),
            getPersistedLivingRoom(livingRoom)
        );
    }

    @Test
    void fullUpdateLivingRoomWithPatch() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livingRoom using partial update
        LivingRoom partialUpdatedLivingRoom = new LivingRoom();
        partialUpdatedLivingRoom.setId(livingRoom.getId());

        partialUpdatedLivingRoom
            .livingRoomName(UPDATED_LIVING_ROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLivingRoom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedLivingRoom))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the LivingRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivingRoomUpdatableFieldsEquals(partialUpdatedLivingRoom, getPersistedLivingRoom(partialUpdatedLivingRoom));
    }

    @Test
    void patchNonExistingLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        livingRoom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, livingRoom.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(livingRoom))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteLivingRoom() {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();
        livingRoomRepository.save(livingRoom).block();
        livingRoomSearchRepository.save(livingRoom).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the livingRoom
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, livingRoom.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchLivingRoom() {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.save(livingRoom).block();
        livingRoomSearchRepository.save(livingRoom).block();

        // Search the livingRoom
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + livingRoom.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(livingRoom.getId().intValue()))
            .jsonPath("$.[*].livingRoomName")
            .value(hasItem(DEFAULT_LIVING_ROOM_NAME))
            .jsonPath("$.[*].conditionLevel")
            .value(hasItem(DEFAULT_CONDITION_LEVEL.toString()))
            .jsonPath("$.[*].roomSize")
            .value(hasItem(sameNumber(DEFAULT_ROOM_SIZE)))
            .jsonPath("$.[*].wallState")
            .value(hasItem(DEFAULT_WALL_STATE.toString()))
            .jsonPath("$.[*].floorMaterial")
            .value(hasItem(DEFAULT_FLOOR_MATERIAL))
            .jsonPath("$.[*].sunlight")
            .value(hasItem(DEFAULT_SUNLIGHT))
            .jsonPath("$.[*].remarks")
            .value(hasItem(DEFAULT_REMARKS));
    }

    protected long getRepositoryCount() {
        return livingRoomRepository.count().block();
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

    protected LivingRoom getPersistedLivingRoom(LivingRoom livingRoom) {
        return livingRoomRepository.findById(livingRoom.getId()).block();
    }

    protected void assertPersistedLivingRoomToMatchAllProperties(LivingRoom expectedLivingRoom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLivingRoomAllPropertiesEquals(expectedLivingRoom, getPersistedLivingRoom(expectedLivingRoom));
        assertLivingRoomUpdatableFieldsEquals(expectedLivingRoom, getPersistedLivingRoom(expectedLivingRoom));
    }

    protected void assertPersistedLivingRoomToMatchUpdatableProperties(LivingRoom expectedLivingRoom) {
        // Test fails because reactive api returns an empty object instead of null
        // assertLivingRoomAllUpdatablePropertiesEquals(expectedLivingRoom, getPersistedLivingRoom(expectedLivingRoom));
        assertLivingRoomUpdatableFieldsEquals(expectedLivingRoom, getPersistedLivingRoom(expectedLivingRoom));
    }
}
