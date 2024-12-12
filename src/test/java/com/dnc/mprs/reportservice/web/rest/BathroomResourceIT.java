package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.BathroomAsserts.*;
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
import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.BathroomRepository;
import com.dnc.mprs.reportservice.repository.search.BathroomSearchRepository;
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
 * Integration tests for the {@link BathroomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BathroomResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

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
    private MockMvc restBathroomMockMvc;

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
            .reportId(DEFAULT_REPORT_ID)
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
            .reportId(UPDATED_REPORT_ID)
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .bathroomSize(UPDATED_BATHROOM_SIZE)
            .waterPressure(UPDATED_WATER_PRESSURE)
            .showerBoothPresence(UPDATED_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        bathroom = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBathroom != null) {
            bathroomRepository.delete(insertedBathroom);
            bathroomSearchRepository.delete(insertedBathroom);
            insertedBathroom = null;
        }
    }

    @Test
    @Transactional
    void createBathroom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        // Create the Bathroom
        var returnedBathroom = om.readValue(
            restBathroomMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Bathroom.class
        );

        // Validate the Bathroom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBathroomUpdatableFieldsEquals(returnedBathroom, getPersistedBathroom(returnedBathroom));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBathroom = returnedBathroom;
    }

    @Test
    @Transactional
    void createBathroomWithExistingId() throws Exception {
        // Create the Bathroom with an existing ID
        bathroom.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBathroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isBadRequest());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        // set the field null
        bathroom.setReportId(null);

        // Create the Bathroom, which fails.

        restBathroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBathroomNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        // set the field null
        bathroom.setBathroomName(null);

        // Create the Bathroom, which fails.

        restBathroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCondtionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        // set the field null
        bathroom.setCondtionLevel(null);

        // Create the Bathroom, which fails.

        restBathroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkWaterPressureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        // set the field null
        bathroom.setWaterPressure(null);

        // Create the Bathroom, which fails.

        restBathroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFloorAndCeilingIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        // set the field null
        bathroom.setFloorAndCeiling(null);

        // Create the Bathroom, which fails.

        restBathroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBathrooms() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);

        // Get all the bathroomList
        restBathroomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bathroom.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].bathroomName").value(hasItem(DEFAULT_BATHROOM_NAME)))
            .andExpect(jsonPath("$.[*].condtionLevel").value(hasItem(DEFAULT_CONDTION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].bathroomSize").value(hasItem(sameNumber(DEFAULT_BATHROOM_SIZE))))
            .andExpect(jsonPath("$.[*].waterPressure").value(hasItem(DEFAULT_WATER_PRESSURE.toString())))
            .andExpect(jsonPath("$.[*].showerBoothPresence").value(hasItem(DEFAULT_SHOWER_BOOTH_PRESENCE)))
            .andExpect(jsonPath("$.[*].bathtubPresence").value(hasItem(DEFAULT_BATHTUB_PRESENCE)))
            .andExpect(jsonPath("$.[*].floorAndCeiling").value(hasItem(DEFAULT_FLOOR_AND_CEILING.toString())))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getBathroom() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);

        // Get the bathroom
        restBathroomMockMvc
            .perform(get(ENTITY_API_URL_ID, bathroom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bathroom.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.bathroomName").value(DEFAULT_BATHROOM_NAME))
            .andExpect(jsonPath("$.condtionLevel").value(DEFAULT_CONDTION_LEVEL.toString()))
            .andExpect(jsonPath("$.bathroomSize").value(sameNumber(DEFAULT_BATHROOM_SIZE)))
            .andExpect(jsonPath("$.waterPressure").value(DEFAULT_WATER_PRESSURE.toString()))
            .andExpect(jsonPath("$.showerBoothPresence").value(DEFAULT_SHOWER_BOOTH_PRESENCE))
            .andExpect(jsonPath("$.bathtubPresence").value(DEFAULT_BATHTUB_PRESENCE))
            .andExpect(jsonPath("$.floorAndCeiling").value(DEFAULT_FLOOR_AND_CEILING.toString()))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingBathroom() throws Exception {
        // Get the bathroom
        restBathroomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBathroom() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        bathroomSearchRepository.save(bathroom);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());

        // Update the bathroom
        Bathroom updatedBathroom = bathroomRepository.findById(bathroom.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBathroom are not directly saved in db
        em.detach(updatedBathroom);
        updatedBathroom
            .reportId(UPDATED_REPORT_ID)
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .bathroomSize(UPDATED_BATHROOM_SIZE)
            .waterPressure(UPDATED_WATER_PRESSURE)
            .showerBoothPresence(UPDATED_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING)
            .remarks(UPDATED_REMARKS);

        restBathroomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBathroom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBathroom))
            )
            .andExpect(status().isOk());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBathroomToMatchAllProperties(updatedBathroom);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Bathroom> bathroomSearchList = Streamable.of(bathroomSearchRepository.findAll()).toList();
                Bathroom testBathroomSearch = bathroomSearchList.get(searchDatabaseSizeAfter - 1);

                assertBathroomAllPropertiesEquals(testBathroomSearch, updatedBathroom);
            });
    }

    @Test
    @Transactional
    void putNonExistingBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        bathroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBathroomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bathroom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bathroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBathroomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bathroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBathroomMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBathroomWithPatch() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bathroom using partial update
        Bathroom partialUpdatedBathroom = new Bathroom();
        partialUpdatedBathroom.setId(bathroom.getId());

        partialUpdatedBathroom
            .reportId(UPDATED_REPORT_ID)
            .bathroomName(UPDATED_BATHROOM_NAME)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .remarks(UPDATED_REMARKS);

        restBathroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBathroom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBathroom))
            )
            .andExpect(status().isOk());

        // Validate the Bathroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBathroomUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBathroom, bathroom), getPersistedBathroom(bathroom));
    }

    @Test
    @Transactional
    void fullUpdateBathroomWithPatch() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bathroom using partial update
        Bathroom partialUpdatedBathroom = new Bathroom();
        partialUpdatedBathroom.setId(bathroom.getId());

        partialUpdatedBathroom
            .reportId(UPDATED_REPORT_ID)
            .bathroomName(UPDATED_BATHROOM_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .bathroomSize(UPDATED_BATHROOM_SIZE)
            .waterPressure(UPDATED_WATER_PRESSURE)
            .showerBoothPresence(UPDATED_SHOWER_BOOTH_PRESENCE)
            .bathtubPresence(UPDATED_BATHTUB_PRESENCE)
            .floorAndCeiling(UPDATED_FLOOR_AND_CEILING)
            .remarks(UPDATED_REMARKS);

        restBathroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBathroom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBathroom))
            )
            .andExpect(status().isOk());

        // Validate the Bathroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBathroomUpdatableFieldsEquals(partialUpdatedBathroom, getPersistedBathroom(partialUpdatedBathroom));
    }

    @Test
    @Transactional
    void patchNonExistingBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        bathroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBathroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bathroom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bathroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBathroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bathroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBathroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        bathroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBathroomMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bathroom)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bathroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBathroom() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);
        bathroomRepository.save(bathroom);
        bathroomSearchRepository.save(bathroom);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bathroom
        restBathroomMockMvc
            .perform(delete(ENTITY_API_URL_ID, bathroom.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bathroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBathroom() throws Exception {
        // Initialize the database
        insertedBathroom = bathroomRepository.saveAndFlush(bathroom);
        bathroomSearchRepository.save(bathroom);

        // Search the bathroom
        restBathroomMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + bathroom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bathroom.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].bathroomName").value(hasItem(DEFAULT_BATHROOM_NAME)))
            .andExpect(jsonPath("$.[*].condtionLevel").value(hasItem(DEFAULT_CONDTION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].bathroomSize").value(hasItem(sameNumber(DEFAULT_BATHROOM_SIZE))))
            .andExpect(jsonPath("$.[*].waterPressure").value(hasItem(DEFAULT_WATER_PRESSURE.toString())))
            .andExpect(jsonPath("$.[*].showerBoothPresence").value(hasItem(DEFAULT_SHOWER_BOOTH_PRESENCE)))
            .andExpect(jsonPath("$.[*].bathtubPresence").value(hasItem(DEFAULT_BATHTUB_PRESENCE)))
            .andExpect(jsonPath("$.[*].floorAndCeiling").value(hasItem(DEFAULT_FLOOR_AND_CEILING.toString())))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return bathroomRepository.count();
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
        return bathroomRepository.findById(bathroom.getId()).orElseThrow();
    }

    protected void assertPersistedBathroomToMatchAllProperties(Bathroom expectedBathroom) {
        assertBathroomAllPropertiesEquals(expectedBathroom, getPersistedBathroom(expectedBathroom));
    }

    protected void assertPersistedBathroomToMatchUpdatableProperties(Bathroom expectedBathroom) {
        assertBathroomAllUpdatablePropertiesEquals(expectedBathroom, getPersistedBathroom(expectedBathroom));
    }
}
