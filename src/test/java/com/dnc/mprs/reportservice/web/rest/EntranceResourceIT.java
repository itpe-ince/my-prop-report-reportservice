package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.EntranceAsserts.*;
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
import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.EntranceRepository;
import com.dnc.mprs.reportservice.repository.search.EntranceSearchRepository;
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
 * Integration tests for the {@link EntranceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EntranceResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

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
    private MockMvc restEntranceMockMvc;

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
            .reportId(DEFAULT_REPORT_ID)
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
            .reportId(UPDATED_REPORT_ID)
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        entrance = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntrance != null) {
            entranceRepository.delete(insertedEntrance);
            entranceSearchRepository.delete(insertedEntrance);
            insertedEntrance = null;
        }
    }

    @Test
    @Transactional
    void createEntrance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        // Create the Entrance
        var returnedEntrance = om.readValue(
            restEntranceMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrance)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Entrance.class
        );

        // Validate the Entrance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEntranceUpdatableFieldsEquals(returnedEntrance, getPersistedEntrance(returnedEntrance));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedEntrance = returnedEntrance;
    }

    @Test
    @Transactional
    void createEntranceWithExistingId() throws Exception {
        // Create the Entrance with an existing ID
        entrance.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntranceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrance)))
            .andExpect(status().isBadRequest());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        // set the field null
        entrance.setReportId(null);

        // Create the Entrance, which fails.

        restEntranceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrance)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkEntranceNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        // set the field null
        entrance.setEntranceName(null);

        // Create the Entrance, which fails.

        restEntranceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrance)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCondtionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        // set the field null
        entrance.setCondtionLevel(null);

        // Create the Entrance, which fails.

        restEntranceMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrance)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllEntrances() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);

        // Get all the entranceList
        restEntranceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entrance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].entranceName").value(hasItem(DEFAULT_ENTRANCE_NAME)))
            .andExpect(jsonPath("$.[*].condtionLevel").value(hasItem(DEFAULT_CONDTION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].entranceSize").value(hasItem(sameNumber(DEFAULT_ENTRANCE_SIZE))))
            .andExpect(jsonPath("$.[*].shoeRackSize").value(hasItem(sameNumber(DEFAULT_SHOE_RACK_SIZE))))
            .andExpect(jsonPath("$.[*].pantryPresence").value(hasItem(DEFAULT_PANTRY_PRESENCE)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getEntrance() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);

        // Get the entrance
        restEntranceMockMvc
            .perform(get(ENTITY_API_URL_ID, entrance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(entrance.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.entranceName").value(DEFAULT_ENTRANCE_NAME))
            .andExpect(jsonPath("$.condtionLevel").value(DEFAULT_CONDTION_LEVEL.toString()))
            .andExpect(jsonPath("$.entranceSize").value(sameNumber(DEFAULT_ENTRANCE_SIZE)))
            .andExpect(jsonPath("$.shoeRackSize").value(sameNumber(DEFAULT_SHOE_RACK_SIZE)))
            .andExpect(jsonPath("$.pantryPresence").value(DEFAULT_PANTRY_PRESENCE))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingEntrance() throws Exception {
        // Get the entrance
        restEntranceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEntrance() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        entranceSearchRepository.save(entrance);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());

        // Update the entrance
        Entrance updatedEntrance = entranceRepository.findById(entrance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEntrance are not directly saved in db
        em.detach(updatedEntrance);
        updatedEntrance
            .reportId(UPDATED_REPORT_ID)
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE)
            .remarks(UPDATED_REMARKS);

        restEntranceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEntrance.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEntrance))
            )
            .andExpect(status().isOk());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntranceToMatchAllProperties(updatedEntrance);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Entrance> entranceSearchList = Streamable.of(entranceSearchRepository.findAll()).toList();
                Entrance testEntranceSearch = entranceSearchList.get(searchDatabaseSizeAfter - 1);

                assertEntranceAllPropertiesEquals(testEntranceSearch, updatedEntrance);
            });
    }

    @Test
    @Transactional
    void putNonExistingEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        entrance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntranceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, entrance.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entrance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntranceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entrance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntranceMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entrance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateEntranceWithPatch() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entrance using partial update
        Entrance partialUpdatedEntrance = new Entrance();
        partialUpdatedEntrance.setId(entrance.getId());

        partialUpdatedEntrance
            .reportId(UPDATED_REPORT_ID)
            .entranceName(UPDATED_ENTRANCE_NAME)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE);

        restEntranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntrance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntrance))
            )
            .andExpect(status().isOk());

        // Validate the Entrance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntranceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEntrance, entrance), getPersistedEntrance(entrance));
    }

    @Test
    @Transactional
    void fullUpdateEntranceWithPatch() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entrance using partial update
        Entrance partialUpdatedEntrance = new Entrance();
        partialUpdatedEntrance.setId(entrance.getId());

        partialUpdatedEntrance
            .reportId(UPDATED_REPORT_ID)
            .entranceName(UPDATED_ENTRANCE_NAME)
            .condtionLevel(UPDATED_CONDTION_LEVEL)
            .entranceSize(UPDATED_ENTRANCE_SIZE)
            .shoeRackSize(UPDATED_SHOE_RACK_SIZE)
            .pantryPresence(UPDATED_PANTRY_PRESENCE)
            .remarks(UPDATED_REMARKS);

        restEntranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntrance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntrance))
            )
            .andExpect(status().isOk());

        // Validate the Entrance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntranceUpdatableFieldsEquals(partialUpdatedEntrance, getPersistedEntrance(partialUpdatedEntrance));
    }

    @Test
    @Transactional
    void patchNonExistingEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        entrance.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, entrance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entrance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntranceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entrance))
            )
            .andExpect(status().isBadRequest());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEntrance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        entrance.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntranceMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entrance)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Entrance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteEntrance() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);
        entranceRepository.save(entrance);
        entranceSearchRepository.save(entrance);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the entrance
        restEntranceMockMvc
            .perform(delete(ENTITY_API_URL_ID, entrance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(entranceSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchEntrance() throws Exception {
        // Initialize the database
        insertedEntrance = entranceRepository.saveAndFlush(entrance);
        entranceSearchRepository.save(entrance);

        // Search the entrance
        restEntranceMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + entrance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entrance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].entranceName").value(hasItem(DEFAULT_ENTRANCE_NAME)))
            .andExpect(jsonPath("$.[*].condtionLevel").value(hasItem(DEFAULT_CONDTION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].entranceSize").value(hasItem(sameNumber(DEFAULT_ENTRANCE_SIZE))))
            .andExpect(jsonPath("$.[*].shoeRackSize").value(hasItem(sameNumber(DEFAULT_SHOE_RACK_SIZE))))
            .andExpect(jsonPath("$.[*].pantryPresence").value(hasItem(DEFAULT_PANTRY_PRESENCE)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return entranceRepository.count();
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
        return entranceRepository.findById(entrance.getId()).orElseThrow();
    }

    protected void assertPersistedEntranceToMatchAllProperties(Entrance expectedEntrance) {
        assertEntranceAllPropertiesEquals(expectedEntrance, getPersistedEntrance(expectedEntrance));
    }

    protected void assertPersistedEntranceToMatchUpdatableProperties(Entrance expectedEntrance) {
        assertEntranceAllUpdatablePropertiesEquals(expectedEntrance, getPersistedEntrance(expectedEntrance));
    }
}
