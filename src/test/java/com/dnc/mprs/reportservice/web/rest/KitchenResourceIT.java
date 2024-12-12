package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.KitchenAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
import com.dnc.mprs.reportservice.repository.search.KitchenSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link KitchenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class KitchenResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

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
    private MockMvc restKitchenMockMvc;

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
            .reportId(DEFAULT_REPORT_ID)
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
            .reportId(UPDATED_REPORT_ID)
            .kitchenName(UPDATED_KITCHEN_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .builtInCabinet(UPDATED_BUILT_IN_CABINET)
            .sinkCondition(UPDATED_SINK_CONDITION)
            .ventilationSystem(UPDATED_VENTILATION_SYSTEM)
            .applianceProvision(UPDATED_APPLIANCE_PROVISION)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        kitchen = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedKitchen != null) {
            kitchenRepository.delete(insertedKitchen);
            kitchenSearchRepository.delete(insertedKitchen);
            insertedKitchen = null;
        }
    }

    @Test
    @Transactional
    void createKitchen() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        // Create the Kitchen
        var returnedKitchen = om.readValue(
            restKitchenMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Kitchen.class
        );

        // Validate the Kitchen in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertKitchenUpdatableFieldsEquals(returnedKitchen, getPersistedKitchen(returnedKitchen));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedKitchen = returnedKitchen;
    }

    @Test
    @Transactional
    void createKitchenWithExistingId() throws Exception {
        // Create the Kitchen with an existing ID
        kitchen.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restKitchenMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isBadRequest());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        // set the field null
        kitchen.setReportId(null);

        // Create the Kitchen, which fails.

        restKitchenMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkKitchenNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        // set the field null
        kitchen.setKitchenName(null);

        // Create the Kitchen, which fails.

        restKitchenMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        // set the field null
        kitchen.setConditionLevel(null);

        // Create the Kitchen, which fails.

        restKitchenMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkSinkConditionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        // set the field null
        kitchen.setSinkCondition(null);

        // Create the Kitchen, which fails.

        restKitchenMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllKitchens() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);

        // Get all the kitchenList
        restKitchenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kitchen.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].kitchenName").value(hasItem(DEFAULT_KITCHEN_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].builtInCabinet").value(hasItem(DEFAULT_BUILT_IN_CABINET)))
            .andExpect(jsonPath("$.[*].sinkCondition").value(hasItem(DEFAULT_SINK_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].ventilationSystem").value(hasItem(DEFAULT_VENTILATION_SYSTEM)))
            .andExpect(jsonPath("$.[*].applianceProvision").value(hasItem(DEFAULT_APPLIANCE_PROVISION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getKitchen() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);

        // Get the kitchen
        restKitchenMockMvc
            .perform(get(ENTITY_API_URL_ID, kitchen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(kitchen.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.kitchenName").value(DEFAULT_KITCHEN_NAME))
            .andExpect(jsonPath("$.conditionLevel").value(DEFAULT_CONDITION_LEVEL.toString()))
            .andExpect(jsonPath("$.builtInCabinet").value(DEFAULT_BUILT_IN_CABINET))
            .andExpect(jsonPath("$.sinkCondition").value(DEFAULT_SINK_CONDITION.toString()))
            .andExpect(jsonPath("$.ventilationSystem").value(DEFAULT_VENTILATION_SYSTEM))
            .andExpect(jsonPath("$.applianceProvision").value(DEFAULT_APPLIANCE_PROVISION))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingKitchen() throws Exception {
        // Get the kitchen
        restKitchenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingKitchen() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        kitchenSearchRepository.save(kitchen);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());

        // Update the kitchen
        Kitchen updatedKitchen = kitchenRepository.findById(kitchen.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedKitchen are not directly saved in db
        em.detach(updatedKitchen);
        updatedKitchen
            .reportId(UPDATED_REPORT_ID)
            .kitchenName(UPDATED_KITCHEN_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .builtInCabinet(UPDATED_BUILT_IN_CABINET)
            .sinkCondition(UPDATED_SINK_CONDITION)
            .ventilationSystem(UPDATED_VENTILATION_SYSTEM)
            .applianceProvision(UPDATED_APPLIANCE_PROVISION)
            .remarks(UPDATED_REMARKS);

        restKitchenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedKitchen.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedKitchen))
            )
            .andExpect(status().isOk());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedKitchenToMatchAllProperties(updatedKitchen);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Kitchen> kitchenSearchList = Streamable.of(kitchenSearchRepository.findAll()).toList();
                Kitchen testKitchenSearch = kitchenSearchList.get(searchDatabaseSizeAfter - 1);

                assertKitchenAllPropertiesEquals(testKitchenSearch, updatedKitchen);
            });
    }

    @Test
    @Transactional
    void putNonExistingKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        kitchen.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKitchenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, kitchen.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kitchen))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(kitchen))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateKitchenWithPatch() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen using partial update
        Kitchen partialUpdatedKitchen = new Kitchen();
        partialUpdatedKitchen.setId(kitchen.getId());

        partialUpdatedKitchen.ventilationSystem(UPDATED_VENTILATION_SYSTEM);

        restKitchenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKitchen.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKitchen))
            )
            .andExpect(status().isOk());

        // Validate the Kitchen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedKitchen, kitchen), getPersistedKitchen(kitchen));
    }

    @Test
    @Transactional
    void fullUpdateKitchenWithPatch() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the kitchen using partial update
        Kitchen partialUpdatedKitchen = new Kitchen();
        partialUpdatedKitchen.setId(kitchen.getId());

        partialUpdatedKitchen
            .reportId(UPDATED_REPORT_ID)
            .kitchenName(UPDATED_KITCHEN_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .builtInCabinet(UPDATED_BUILT_IN_CABINET)
            .sinkCondition(UPDATED_SINK_CONDITION)
            .ventilationSystem(UPDATED_VENTILATION_SYSTEM)
            .applianceProvision(UPDATED_APPLIANCE_PROVISION)
            .remarks(UPDATED_REMARKS);

        restKitchenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedKitchen.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedKitchen))
            )
            .andExpect(status().isOk());

        // Validate the Kitchen in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertKitchenUpdatableFieldsEquals(partialUpdatedKitchen, getPersistedKitchen(partialUpdatedKitchen));
    }

    @Test
    @Transactional
    void patchNonExistingKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        kitchen.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restKitchenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, kitchen.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(kitchen))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(kitchen))
            )
            .andExpect(status().isBadRequest());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamKitchen() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        kitchen.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restKitchenMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(kitchen)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Kitchen in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteKitchen() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);
        kitchenRepository.save(kitchen);
        kitchenSearchRepository.save(kitchen);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the kitchen
        restKitchenMockMvc
            .perform(delete(ENTITY_API_URL_ID, kitchen.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(kitchenSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchKitchen() throws Exception {
        // Initialize the database
        insertedKitchen = kitchenRepository.saveAndFlush(kitchen);
        kitchenSearchRepository.save(kitchen);

        // Search the kitchen
        restKitchenMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + kitchen.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(kitchen.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].kitchenName").value(hasItem(DEFAULT_KITCHEN_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].builtInCabinet").value(hasItem(DEFAULT_BUILT_IN_CABINET)))
            .andExpect(jsonPath("$.[*].sinkCondition").value(hasItem(DEFAULT_SINK_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].ventilationSystem").value(hasItem(DEFAULT_VENTILATION_SYSTEM)))
            .andExpect(jsonPath("$.[*].applianceProvision").value(hasItem(DEFAULT_APPLIANCE_PROVISION)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return kitchenRepository.count();
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
        return kitchenRepository.findById(kitchen.getId()).orElseThrow();
    }

    protected void assertPersistedKitchenToMatchAllProperties(Kitchen expectedKitchen) {
        assertKitchenAllPropertiesEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
    }

    protected void assertPersistedKitchenToMatchUpdatableProperties(Kitchen expectedKitchen) {
        assertKitchenAllUpdatablePropertiesEquals(expectedKitchen, getPersistedKitchen(expectedKitchen));
    }
}
