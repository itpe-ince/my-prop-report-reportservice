package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.InfrastructureAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.domain.enumeration.InfraType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.InfrastructureRepository;
import com.dnc.mprs.reportservice.repository.search.InfrastructureSearchRepository;
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
 * Integration tests for the {@link InfrastructureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InfrastructureResourceIT {

    private static final Long DEFAULT_REPORT_ID = 1L;
    private static final Long UPDATED_REPORT_ID = 2L;

    private static final InfraType DEFAULT_INFRA_TYPE = InfraType.SCHOOL;
    private static final InfraType UPDATED_INFRA_TYPE = InfraType.TRANSPRT;

    private static final String DEFAULT_INFRA_NAME = "AAAAAAAAAA";
    private static final String UPDATED_INFRA_NAME = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_CONDITION_LEVEL = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_CONDITION_LEVEL = QualityStateType.MIDDLE;

    private static final Integer DEFAULT_INFRA_DISTANCE = 1;
    private static final Integer UPDATED_INFRA_DISTANCE = 2;

    private static final QualityStateType DEFAULT_INFRA_DISTANCE_UNIT = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_INFRA_DISTANCE_UNIT = QualityStateType.MIDDLE;

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/infrastructures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/infrastructures/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InfrastructureRepository infrastructureRepository;

    @Autowired
    private InfrastructureSearchRepository infrastructureSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInfrastructureMockMvc;

    private Infrastructure infrastructure;

    private Infrastructure insertedInfrastructure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Infrastructure createEntity() {
        return new Infrastructure()
            .reportId(DEFAULT_REPORT_ID)
            .infraType(DEFAULT_INFRA_TYPE)
            .infraName(DEFAULT_INFRA_NAME)
            .conditionLevel(DEFAULT_CONDITION_LEVEL)
            .infraDistance(DEFAULT_INFRA_DISTANCE)
            .infraDistanceUnit(DEFAULT_INFRA_DISTANCE_UNIT)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Infrastructure createUpdatedEntity() {
        return new Infrastructure()
            .reportId(UPDATED_REPORT_ID)
            .infraType(UPDATED_INFRA_TYPE)
            .infraName(UPDATED_INFRA_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .infraDistance(UPDATED_INFRA_DISTANCE)
            .infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    public void initTest() {
        infrastructure = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedInfrastructure != null) {
            infrastructureRepository.delete(insertedInfrastructure);
            infrastructureSearchRepository.delete(insertedInfrastructure);
            insertedInfrastructure = null;
        }
    }

    @Test
    @Transactional
    void createInfrastructure() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        // Create the Infrastructure
        var returnedInfrastructure = om.readValue(
            restInfrastructureMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Infrastructure.class
        );

        // Validate the Infrastructure in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInfrastructureUpdatableFieldsEquals(returnedInfrastructure, getPersistedInfrastructure(returnedInfrastructure));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedInfrastructure = returnedInfrastructure;
    }

    @Test
    @Transactional
    void createInfrastructureWithExistingId() throws Exception {
        // Create the Infrastructure with an existing ID
        infrastructure.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInfrastructureMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        // set the field null
        infrastructure.setReportId(null);

        // Create the Infrastructure, which fails.

        restInfrastructureMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkInfraTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        // set the field null
        infrastructure.setInfraType(null);

        // Create the Infrastructure, which fails.

        restInfrastructureMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkInfraNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        // set the field null
        infrastructure.setInfraName(null);

        // Create the Infrastructure, which fails.

        restInfrastructureMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConditionLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        // set the field null
        infrastructure.setConditionLevel(null);

        // Create the Infrastructure, which fails.

        restInfrastructureMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInfrastructures() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);

        // Get all the infrastructureList
        restInfrastructureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(infrastructure.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].infraType").value(hasItem(DEFAULT_INFRA_TYPE.toString())))
            .andExpect(jsonPath("$.[*].infraName").value(hasItem(DEFAULT_INFRA_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].infraDistance").value(hasItem(DEFAULT_INFRA_DISTANCE)))
            .andExpect(jsonPath("$.[*].infraDistanceUnit").value(hasItem(DEFAULT_INFRA_DISTANCE_UNIT.toString())))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getInfrastructure() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);

        // Get the infrastructure
        restInfrastructureMockMvc
            .perform(get(ENTITY_API_URL_ID, infrastructure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(infrastructure.getId().intValue()))
            .andExpect(jsonPath("$.reportId").value(DEFAULT_REPORT_ID.intValue()))
            .andExpect(jsonPath("$.infraType").value(DEFAULT_INFRA_TYPE.toString()))
            .andExpect(jsonPath("$.infraName").value(DEFAULT_INFRA_NAME))
            .andExpect(jsonPath("$.conditionLevel").value(DEFAULT_CONDITION_LEVEL.toString()))
            .andExpect(jsonPath("$.infraDistance").value(DEFAULT_INFRA_DISTANCE))
            .andExpect(jsonPath("$.infraDistanceUnit").value(DEFAULT_INFRA_DISTANCE_UNIT.toString()))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingInfrastructure() throws Exception {
        // Get the infrastructure
        restInfrastructureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInfrastructure() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        infrastructureSearchRepository.save(infrastructure);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());

        // Update the infrastructure
        Infrastructure updatedInfrastructure = infrastructureRepository.findById(infrastructure.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInfrastructure are not directly saved in db
        em.detach(updatedInfrastructure);
        updatedInfrastructure
            .reportId(UPDATED_REPORT_ID)
            .infraType(UPDATED_INFRA_TYPE)
            .infraName(UPDATED_INFRA_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .infraDistance(UPDATED_INFRA_DISTANCE)
            .infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT)
            .remarks(UPDATED_REMARKS);

        restInfrastructureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInfrastructure.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInfrastructure))
            )
            .andExpect(status().isOk());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInfrastructureToMatchAllProperties(updatedInfrastructure);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Infrastructure> infrastructureSearchList = Streamable.of(infrastructureSearchRepository.findAll()).toList();
                Infrastructure testInfrastructureSearch = infrastructureSearchList.get(searchDatabaseSizeAfter - 1);

                assertInfrastructureAllPropertiesEquals(testInfrastructureSearch, updatedInfrastructure);
            });
    }

    @Test
    @Transactional
    void putNonExistingInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        infrastructure.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInfrastructureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, infrastructure.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfrastructureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfrastructureMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(infrastructure)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInfrastructureWithPatch() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infrastructure using partial update
        Infrastructure partialUpdatedInfrastructure = new Infrastructure();
        partialUpdatedInfrastructure.setId(infrastructure.getId());

        partialUpdatedInfrastructure.infraName(UPDATED_INFRA_NAME).infraDistance(UPDATED_INFRA_DISTANCE).remarks(UPDATED_REMARKS);

        restInfrastructureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInfrastructure.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInfrastructure))
            )
            .andExpect(status().isOk());

        // Validate the Infrastructure in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfrastructureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInfrastructure, infrastructure),
            getPersistedInfrastructure(infrastructure)
        );
    }

    @Test
    @Transactional
    void fullUpdateInfrastructureWithPatch() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the infrastructure using partial update
        Infrastructure partialUpdatedInfrastructure = new Infrastructure();
        partialUpdatedInfrastructure.setId(infrastructure.getId());

        partialUpdatedInfrastructure
            .reportId(UPDATED_REPORT_ID)
            .infraType(UPDATED_INFRA_TYPE)
            .infraName(UPDATED_INFRA_NAME)
            .conditionLevel(UPDATED_CONDITION_LEVEL)
            .infraDistance(UPDATED_INFRA_DISTANCE)
            .infraDistanceUnit(UPDATED_INFRA_DISTANCE_UNIT)
            .remarks(UPDATED_REMARKS);

        restInfrastructureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInfrastructure.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInfrastructure))
            )
            .andExpect(status().isOk());

        // Validate the Infrastructure in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInfrastructureUpdatableFieldsEquals(partialUpdatedInfrastructure, getPersistedInfrastructure(partialUpdatedInfrastructure));
    }

    @Test
    @Transactional
    void patchNonExistingInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        infrastructure.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInfrastructureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, infrastructure.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfrastructureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isBadRequest());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInfrastructure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        infrastructure.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInfrastructureMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(infrastructure))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Infrastructure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInfrastructure() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);
        infrastructureRepository.save(infrastructure);
        infrastructureSearchRepository.save(infrastructure);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the infrastructure
        restInfrastructureMockMvc
            .perform(delete(ENTITY_API_URL_ID, infrastructure.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(infrastructureSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInfrastructure() throws Exception {
        // Initialize the database
        insertedInfrastructure = infrastructureRepository.saveAndFlush(infrastructure);
        infrastructureSearchRepository.save(infrastructure);

        // Search the infrastructure
        restInfrastructureMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + infrastructure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(infrastructure.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportId").value(hasItem(DEFAULT_REPORT_ID.intValue())))
            .andExpect(jsonPath("$.[*].infraType").value(hasItem(DEFAULT_INFRA_TYPE.toString())))
            .andExpect(jsonPath("$.[*].infraName").value(hasItem(DEFAULT_INFRA_NAME)))
            .andExpect(jsonPath("$.[*].conditionLevel").value(hasItem(DEFAULT_CONDITION_LEVEL.toString())))
            .andExpect(jsonPath("$.[*].infraDistance").value(hasItem(DEFAULT_INFRA_DISTANCE)))
            .andExpect(jsonPath("$.[*].infraDistanceUnit").value(hasItem(DEFAULT_INFRA_DISTANCE_UNIT.toString())))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    protected long getRepositoryCount() {
        return infrastructureRepository.count();
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

    protected Infrastructure getPersistedInfrastructure(Infrastructure infrastructure) {
        return infrastructureRepository.findById(infrastructure.getId()).orElseThrow();
    }

    protected void assertPersistedInfrastructureToMatchAllProperties(Infrastructure expectedInfrastructure) {
        assertInfrastructureAllPropertiesEquals(expectedInfrastructure, getPersistedInfrastructure(expectedInfrastructure));
    }

    protected void assertPersistedInfrastructureToMatchUpdatableProperties(Infrastructure expectedInfrastructure) {
        assertInfrastructureAllUpdatablePropertiesEquals(expectedInfrastructure, getPersistedInfrastructure(expectedInfrastructure));
    }
}
