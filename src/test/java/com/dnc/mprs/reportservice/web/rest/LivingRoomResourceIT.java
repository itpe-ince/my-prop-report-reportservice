package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.LivingRoomAsserts.*;
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
import com.dnc.mprs.reportservice.domain.LivingRoom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.LivingRoomRepository;
import com.dnc.mprs.reportservice.repository.search.LivingRoomSearchRepository;
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
 * Integration tests for the {@link LivingRoomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LivingRoomResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

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
    private MockMvc restLivingRoomMockMvc;

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
            .reportId(DEFAULT_REPORT_ID)
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
            .reportId(UPDATED_REPORT_ID)
            .livingRoomName(UPDATED_LIVING_ROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        livingRoom = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedLivingRoom != null) {
            livingRoomRepository.delete(insertedLivingRoom);
            livingRoomSearchRepository.delete(insertedLivingRoom);
            insertedLivingRoom = null;
        }
    }

    @Test
    @Transactional
    void createLivingRoom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        // Create the LivingRoom
        var returnedLivingRoom = om.readValue(
            restLivingRoomMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LivingRoom.class
        );

        // Validate the LivingRoom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLivingRoomUpdatableFieldsEquals(returnedLivingRoom, getPersistedLivingRoom(returnedLivingRoom));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedLivingRoom = returnedLivingRoom;
    }

    @Test
    @Transactional
    void createLivingRoomWithExistingId() throws Exception {
        // Create the LivingRoom with an existing ID
        livingRoom.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivingRoomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom)))
            .andExpect(status().isBadRequest());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        // set the field null
        livingRoom.setReportId(null);

        // Create the LivingRoom, which fails.

        restLivingRoomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkLivingRoomNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        // set the field null
        livingRoom.setLivingRoomName(null);

        // Create the LivingRoom, which fails.

        restLivingRoomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        // set the field null
        livingRoom.setConditionLevel(null);

        // Create the LivingRoom, which fails.

        restLivingRoomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWallStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        // set the field null
        livingRoom.setWallState(null);

        // Create the LivingRoom, which fails.

        restLivingRoomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllLivingRooms() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);

        // Get all the livingRoomList
        restLivingRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livingRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].livingRoomName").value(hasItem(DEFAULT_LIVING_ROOM_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].roomSize").value(hasItem(sameNumber(DEFAULT_ROOM_SIZE))))
            .andExpect(jsonPath("$.[*].wallState").value(hasItem(DEFAULT_WALL_STATE.toString())))
            .andExpect(jsonPath("$.[*].floorMaterial").value(hasItem(DEFAULT_FLOOR_MATERIAL)))
            .andExpect(jsonPath("$.[*].sunlight").value(hasItem(DEFAULT_SUNLIGHT)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getLivingRoom() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);

        // Get the livingRoom
        restLivingRoomMockMvc
            .perform(get(ENTITY_API_URL_ID, livingRoom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(livingRoom.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.livingRoomName").value(DEFAULT_LIVING_ROOM_NAME))
            .andExpect(jsonPath("$.conditionLevel").value(DEFAULT_CONDITION_LEVEL.toString()))
            .andExpect(jsonPath("$.roomSize").value(sameNumber(DEFAULT_ROOM_SIZE)))
            .andExpect(jsonPath("$.wallState").value(DEFAULT_WALL_STATE.toString()))
            .andExpect(jsonPath("$.floorMaterial").value(DEFAULT_FLOOR_MATERIAL))
            .andExpect(jsonPath("$.sunlight").value(DEFAULT_SUNLIGHT))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingLivingRoom() throws Exception {
        // Get the livingRoom
        restLivingRoomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLivingRoom() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        livingRoomSearchRepository.save(livingRoom);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());

        // Update the livingRoom
        LivingRoom updatedLivingRoom = livingRoomRepository.findById(livingRoom.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLivingRoom are not directly saved in db
        em.detach(updatedLivingRoom);
        updatedLivingRoom
            .reportId(UPDATED_REPORT_ID)
            .livingRoomName(UPDATED_LIVING_ROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);

        restLivingRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLivingRoom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLivingRoom))
            )
            .andExpect(status().isOk());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLivingRoomToMatchAllProperties(updatedLivingRoom);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<LivingRoom> livingRoomSearchList = Streamable.of(livingRoomSearchRepository.findAll()).toList();
                LivingRoom testLivingRoomSearch = livingRoomSearchList.get(searchDatabaseSizeAfter - 1);

                assertLivingRoomAllPropertiesEquals(testLivingRoomSearch, updatedLivingRoom);
            });
    }

    @Test
    @Transactional
    void putNonExistingLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        livingRoom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivingRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livingRoom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(livingRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivingRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(livingRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivingRoomMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(livingRoom)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateLivingRoomWithPatch() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livingRoom using partial update
        LivingRoom partialUpdatedLivingRoom = new LivingRoom();
        partialUpdatedLivingRoom.setId(livingRoom.getId());

        partialUpdatedLivingRoom
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT);

        restLivingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivingRoom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLivingRoom))
            )
            .andExpect(status().isOk());

        // Validate the LivingRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivingRoomUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLivingRoom, livingRoom),
            getPersistedLivingRoom(livingRoom)
        );
    }

    @Test
    @Transactional
    void fullUpdateLivingRoomWithPatch() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the livingRoom using partial update
        LivingRoom partialUpdatedLivingRoom = new LivingRoom();
        partialUpdatedLivingRoom.setId(livingRoom.getId());

        partialUpdatedLivingRoom
            .reportId(UPDATED_REPORT_ID)
            .livingRoomName(UPDATED_LIVING_ROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .wallState(UPDATED_WALL_STATE)
            .floorMaterial(UPDATED_FLOOR_MATERIAL)
            .sunlight(UPDATED_SUNLIGHT)
            .remarks(UPDATED_REMARKS);

        restLivingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivingRoom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLivingRoom))
            )
            .andExpect(status().isOk());

        // Validate the LivingRoom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLivingRoomUpdatableFieldsEquals(partialUpdatedLivingRoom, getPersistedLivingRoom(partialUpdatedLivingRoom));
    }

    @Test
    @Transactional
    void patchNonExistingLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        livingRoom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, livingRoom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(livingRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(livingRoom))
            )
            .andExpect(status().isBadRequest());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLivingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        livingRoom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivingRoomMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(livingRoom))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LivingRoom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteLivingRoom() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);
        livingRoomRepository.save(livingRoom);
        livingRoomSearchRepository.save(livingRoom);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the livingRoom
        restLivingRoomMockMvc
            .perform(delete(ENTITY_API_URL_ID, livingRoom.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(livingRoomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchLivingRoom() throws Exception {
        // Initialize the database
        insertedLivingRoom = livingRoomRepository.saveAndFlush(livingRoom);
        livingRoomSearchRepository.save(livingRoom);

        // Search the livingRoom
        restLivingRoomMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + livingRoom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livingRoom.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].livingRoomName").value(hasItem(DEFAULT_LIVING_ROOM_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].roomSize").value(hasItem(sameNumber(DEFAULT_ROOM_SIZE))))
            .andExpect(jsonPath("$.[*].wallState").value(hasItem(DEFAULT_WALL_STATE.toString())))
            .andExpect(jsonPath("$.[*].floorMaterial").value(hasItem(DEFAULT_FLOOR_MATERIAL)))
            .andExpect(jsonPath("$.[*].sunlight").value(hasItem(DEFAULT_SUNLIGHT)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return livingRoomRepository.count();
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
        return livingRoomRepository.findById(livingRoom.getId()).orElseThrow();
    }

    protected void assertPersistedLivingRoomToMatchAllProperties(LivingRoom expectedLivingRoom) {
        assertLivingRoomAllPropertiesEquals(expectedLivingRoom, getPersistedLivingRoom(expectedLivingRoom));
    }

    protected void assertPersistedLivingRoomToMatchUpdatableProperties(LivingRoom expectedLivingRoom) {
        assertLivingRoomAllUpdatablePropertiesEquals(expectedLivingRoom, getPersistedLivingRoom(expectedLivingRoom));
    }
}
