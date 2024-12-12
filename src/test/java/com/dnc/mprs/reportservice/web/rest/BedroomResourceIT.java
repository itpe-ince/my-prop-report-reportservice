package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.BedroomAsserts.*;
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
import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.BedroomRepository;
import com.dnc.mprs.reportservice.repository.search.BedroomSearchRepository;
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
 * Integration tests for the {@link BedroomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BedroomResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

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
    private MockMvc restBedroomMockMvc;

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
            .reportId(DEFAULT_REPORT_ID)
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
            .reportId(UPDATED_REPORT_ID)
            .bedroomName(UPDATED_BEDROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .closetYn(UPDATED_CLOSET_YN)
            .acYn(UPDATED_AC_YN)
            .windowLocation(UPDATED_WINDOW_LOCATION)
            .windowSize(UPDATED_WINDOW_SIZE)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        bedroom = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBedroom != null) {
            bedroomRepository.delete(insertedBedroom);
            bedroomSearchRepository.delete(insertedBedroom);
            insertedBedroom = null;
        }
    }

    @Test
    @Transactional
    void createBedroom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        // Create the Bedroom
        var returnedBedroom = om.readValue(
            restBedroomMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bedroom)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Bedroom.class
        );

        // Validate the Bedroom in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBedroomUpdatableFieldsEquals(returnedBedroom, getPersistedBedroom(returnedBedroom));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedBedroom = returnedBedroom;
    }

    @Test
    @Transactional
    void createBedroomWithExistingId() throws Exception {
        // Create the Bedroom with an existing ID
        bedroom.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restBedroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bedroom)))
            .andExpect(status().isBadRequest());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        // set the field null
        bedroom.setReportId(null);

        // Create the Bedroom, which fails.

        restBedroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bedroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkBedroomNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        // set the field null
        bedroom.setBedroomName(null);

        // Create the Bedroom, which fails.

        restBedroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bedroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        // set the field null
        bedroom.setConditionLevel(null);

        // Create the Bedroom, which fails.

        restBedroomMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bedroom)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllBedrooms() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);

        // Get all the bedroomList
        restBedroomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bedroom.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].bedroomName").value(hasItem(DEFAULT_BEDROOM_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].roomSize").value(hasItem(sameNumber(DEFAULT_ROOM_SIZE))))
            .andExpect(jsonPath("$.[*].closetYn").value(hasItem(DEFAULT_CLOSET_YN)))
            .andExpect(jsonPath("$.[*].acYn").value(hasItem(DEFAULT_AC_YN)))
            .andExpect(jsonPath("$.[*].windowLocation").value(hasItem(DEFAULT_WINDOW_LOCATION)))
            .andExpect(jsonPath("$.[*].windowSize").value(hasItem(DEFAULT_WINDOW_SIZE)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getBedroom() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);

        // Get the bedroom
        restBedroomMockMvc
            .perform(get(ENTITY_API_URL_ID, bedroom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bedroom.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.bedroomName").value(DEFAULT_BEDROOM_NAME))
            .andExpect(jsonPath("$.conditionLevel").value(DEFAULT_CONDITION_LEVEL.toString()))
            .andExpect(jsonPath("$.roomSize").value(sameNumber(DEFAULT_ROOM_SIZE)))
            .andExpect(jsonPath("$.closetYn").value(DEFAULT_CLOSET_YN))
            .andExpect(jsonPath("$.acYn").value(DEFAULT_AC_YN))
            .andExpect(jsonPath("$.windowLocation").value(DEFAULT_WINDOW_LOCATION))
            .andExpect(jsonPath("$.windowSize").value(DEFAULT_WINDOW_SIZE))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingBedroom() throws Exception {
        // Get the bedroom
        restBedroomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBedroom() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        bedroomSearchRepository.save(bedroom);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());

        // Update the bedroom
        Bedroom updatedBedroom = bedroomRepository.findById(bedroom.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBedroom are not directly saved in db
        em.detach(updatedBedroom);
        updatedBedroom
            .reportId(UPDATED_REPORT_ID)
            .bedroomName(UPDATED_BEDROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .closetYn(UPDATED_CLOSET_YN)
            .acYn(UPDATED_AC_YN)
            .windowLocation(UPDATED_WINDOW_LOCATION)
            .windowSize(UPDATED_WINDOW_SIZE)
            .remarks(UPDATED_REMARKS);

        restBedroomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBedroom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBedroom))
            )
            .andExpect(status().isOk());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBedroomToMatchAllProperties(updatedBedroom);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Bedroom> bedroomSearchList = Streamable.of(bedroomSearchRepository.findAll()).toList();
                Bedroom testBedroomSearch = bedroomSearchList.get(searchDatabaseSizeAfter - 1);

                assertBedroomAllPropertiesEquals(testBedroomSearch, updatedBedroom);
            });
    }

    @Test
    @Transactional
    void putNonExistingBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        bedroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBedroomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bedroom.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bedroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBedroomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bedroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBedroomMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bedroom)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateBedroomWithPatch() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bedroom using partial update
        Bedroom partialUpdatedBedroom = new Bedroom();
        partialUpdatedBedroom.setId(bedroom.getId());

        partialUpdatedBedroom.bedroomName(UPDATED_BEDROOM_NAME).conditionLevel(UPDATED_CONDITION_LEVEL).windowSize(UPDATED_WINDOW_SIZE);

        restBedroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBedroom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBedroom))
            )
            .andExpect(status().isOk());

        // Validate the Bedroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBedroomUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBedroom, bedroom), getPersistedBedroom(bedroom));
    }

    @Test
    @Transactional
    void fullUpdateBedroomWithPatch() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bedroom using partial update
        Bedroom partialUpdatedBedroom = new Bedroom();
        partialUpdatedBedroom.setId(bedroom.getId());

        partialUpdatedBedroom
            .reportId(UPDATED_REPORT_ID)
            .bedroomName(UPDATED_BEDROOM_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .roomSize(UPDATED_ROOM_SIZE)
            .closetYn(UPDATED_CLOSET_YN)
            .acYn(UPDATED_AC_YN)
            .windowLocation(UPDATED_WINDOW_LOCATION)
            .windowSize(UPDATED_WINDOW_SIZE)
            .remarks(UPDATED_REMARKS);

        restBedroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBedroom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBedroom))
            )
            .andExpect(status().isOk());

        // Validate the Bedroom in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBedroomUpdatableFieldsEquals(partialUpdatedBedroom, getPersistedBedroom(partialUpdatedBedroom));
    }

    @Test
    @Transactional
    void patchNonExistingBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        bedroom.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBedroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bedroom.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bedroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBedroomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bedroom))
            )
            .andExpect(status().isBadRequest());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBedroom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        bedroom.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBedroomMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bedroom)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Bedroom in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteBedroom() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);
        bedroomRepository.save(bedroom);
        bedroomSearchRepository.save(bedroom);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the bedroom
        restBedroomMockMvc
            .perform(delete(ENTITY_API_URL_ID, bedroom.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(bedroomSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchBedroom() throws Exception {
        // Initialize the database
        insertedBedroom = bedroomRepository.saveAndFlush(bedroom);
        bedroomSearchRepository.save(bedroom);

        // Search the bedroom
        restBedroomMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + bedroom.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bedroom.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].bedroomName").value(hasItem(DEFAULT_BEDROOM_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].roomSize").value(hasItem(sameNumber(DEFAULT_ROOM_SIZE))))
            .andExpect(jsonPath("$.[*].closetYn").value(hasItem(DEFAULT_CLOSET_YN)))
            .andExpect(jsonPath("$.[*].acYn").value(hasItem(DEFAULT_AC_YN)))
            .andExpect(jsonPath("$.[*].windowLocation").value(hasItem(DEFAULT_WINDOW_LOCATION)))
            .andExpect(jsonPath("$.[*].windowSize").value(hasItem(DEFAULT_WINDOW_SIZE)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return bedroomRepository.count();
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
        return bedroomRepository.findById(bedroom.getId()).orElseThrow();
    }

    protected void assertPersistedBedroomToMatchAllProperties(Bedroom expectedBedroom) {
        assertBedroomAllPropertiesEquals(expectedBedroom, getPersistedBedroom(expectedBedroom));
    }

    protected void assertPersistedBedroomToMatchUpdatableProperties(Bedroom expectedBedroom) {
        assertBedroomAllUpdatablePropertiesEquals(expectedBedroom, getPersistedBedroom(expectedBedroom));
    }
}
