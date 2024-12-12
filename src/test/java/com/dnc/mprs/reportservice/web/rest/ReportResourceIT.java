package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.ReportAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.ReportRepository;
import com.dnc.mprs.reportservice.repository.search.ReportSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link ReportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ReportResourceIT {

    private static final String DEFAULT_REPORT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_REPORT_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_REPORT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REPORT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Long DEFAULT_AUTHOR_ID = 1L;
    private static final Long UPDATED_AUTHOR_ID = 2L;

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final QualityStateType DEFAULT_EXTERIOR_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_EXTERIOR_STATE = QualityStateType.MIDDLE;

    private static final Integer DEFAULT_CONSTRUCTION_YEAR = 1;
    private static final Integer UPDATED_CONSTRUCTION_YEAR = 2;

    private static final QualityStateType DEFAULT_MAINTENANCE_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_MAINTENANCE_STATE = QualityStateType.MIDDLE;

    private static final String DEFAULT_PARKING_FACILITY = "AAAAAAAAAA";
    private static final String UPDATED_PARKING_FACILITY = "BBBBBBBBBB";

    private static final Integer DEFAULT_PARKING_COUNT = 1;
    private static final Integer UPDATED_PARKING_COUNT = 2;

    private static final QualityStateType DEFAULT_ELEVATOR_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_ELEVATOR_STATE = QualityStateType.MIDDLE;

    private static final QualityStateType DEFAULT_NOISE_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_NOISE_STATE = QualityStateType.MIDDLE;

    private static final QualityStateType DEFAULT_HOMEPAD_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_HOMEPAD_STATE = QualityStateType.MIDDLE;

    private static final String DEFAULT_CCTV_YN = "A";
    private static final String UPDATED_CCTV_YN = "B";

    private static final QualityStateType DEFAULT_FIRE_SAFETY_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_FIRE_SAFETY_STATE = QualityStateType.MIDDLE;

    private static final QualityStateType DEFAULT_DOOR_SECURITY_STATE = QualityStateType.HIGH;
    private static final QualityStateType UPDATED_DOOR_SECURITY_STATE = QualityStateType.MIDDLE;

    private static final Integer DEFAULT_MAINTENANCE_FEE = 1;
    private static final Integer UPDATED_MAINTENANCE_FEE = 2;

    private static final String DEFAULT_REDEVELOPMENT_YN = "A";
    private static final String UPDATED_REDEVELOPMENT_YN = "B";

    private static final String DEFAULT_RENTAL_DEMAND = "AAAAAAAAAA";
    private static final String UPDATED_RENTAL_DEMAND = "BBBBBBBBBB";

    private static final String DEFAULT_COMMUNITY_RULES = "AAAAAAAAAA";
    private static final String UPDATED_COMMUNITY_RULES = "BBBBBBBBBB";

    private static final Long DEFAULT_COMPLEX_ID = 1L;
    private static final Long UPDATED_COMPLEX_ID = 2L;

    private static final String DEFAULT_COMPLEX_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPLEX_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_PROPERTY_ID = 1L;
    private static final Long UPDATED_PROPERTY_ID = 2L;

    private static final String DEFAULT_PROPERTY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_PROPERTY_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/reports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/reports/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportSearchRepository reportSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReportMockMvc;

    private Report report;

    private Report insertedReport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity() {
        return new Report()
            .reportTitle(DEFAULT_REPORT_TITLE)
            .reportDate(DEFAULT_REPORT_DATE)
            .authorId(DEFAULT_AUTHOR_ID)
            .summary(DEFAULT_SUMMARY)
            .exteriorState(DEFAULT_EXTERIOR_STATE)
            .constructionYear(DEFAULT_CONSTRUCTION_YEAR)
            .maintenanceState(DEFAULT_MAINTENANCE_STATE)
            .parkingFacility(DEFAULT_PARKING_FACILITY)
            .parkingCount(DEFAULT_PARKING_COUNT)
            .elevatorState(DEFAULT_ELEVATOR_STATE)
            .noiseState(DEFAULT_NOISE_STATE)
            .homepadState(DEFAULT_HOMEPAD_STATE)
            .cctvYn(DEFAULT_CCTV_YN)
            .fireSafetyState(DEFAULT_FIRE_SAFETY_STATE)
            .doorSecurityState(DEFAULT_DOOR_SECURITY_STATE)
            .maintenanceFee(DEFAULT_MAINTENANCE_FEE)
            .redevelopmentYn(DEFAULT_REDEVELOPMENT_YN)
            .rentalDemand(DEFAULT_RENTAL_DEMAND)
            .communityRules(DEFAULT_COMMUNITY_RULES)
            .complexId(DEFAULT_COMPLEX_ID)
            .complexName(DEFAULT_COMPLEX_NAME)
            .propertyId(DEFAULT_PROPERTY_ID)
            .propertyName(DEFAULT_PROPERTY_NAME)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createUpdatedEntity() {
        return new Report()
            .reportTitle(UPDATED_REPORT_TITLE)
            .reportDate(UPDATED_REPORT_DATE)
            .authorId(UPDATED_AUTHOR_ID)
            .summary(UPDATED_SUMMARY)
            .exteriorState(UPDATED_EXTERIOR_STATE)
            .constructionYear(UPDATED_CONSTRUCTION_YEAR)
            .maintenanceState(UPDATED_MAINTENANCE_STATE)
            .parkingFacility(UPDATED_PARKING_FACILITY)
            .parkingCount(UPDATED_PARKING_COUNT)
            .elevatorState(UPDATED_ELEVATOR_STATE)
            .noiseState(UPDATED_NOISE_STATE)
            .homepadState(UPDATED_HOMEPAD_STATE)
            .cctvYn(UPDATED_CCTV_YN)
            .fireSafetyState(UPDATED_FIRE_SAFETY_STATE)
            .doorSecurityState(UPDATED_DOOR_SECURITY_STATE)
            .maintenanceFee(UPDATED_MAINTENANCE_FEE)
            .redevelopmentYn(UPDATED_REDEVELOPMENT_YN)
            .rentalDemand(UPDATED_RENTAL_DEMAND)
            .communityRules(UPDATED_COMMUNITY_RULES)
            .complexId(UPDATED_COMPLEX_ID)
            .complexName(UPDATED_COMPLEX_NAME)
            .propertyId(UPDATED_PROPERTY_ID)
            .propertyName(UPDATED_PROPERTY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    public void initTest() {
        report = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReport != null) {
            reportRepository.delete(insertedReport);
            reportSearchRepository.delete(insertedReport);
            insertedReport = null;
        }
    }

    @Test
    @Transactional
    void createReport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // Create the Report
        var returnedReport = om.readValue(
            restReportMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Report.class
        );

        // Validate the Report in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReportUpdatableFieldsEquals(returnedReport, getPersistedReport(returnedReport));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedReport = returnedReport;
    }

    @Test
    @Transactional
    void createReportWithExistingId() throws Exception {
        // Create the Report with an existing ID
        report.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkReportTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setReportTitle(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkAuthorIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setAuthorId(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkExteriorStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setExteriorState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkMaintenanceStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setMaintenanceState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkElevatorStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setElevatorState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNoiseStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setNoiseState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkHomepadStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setHomepadState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkFireSafetyStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setFireSafetyState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkDoorSecurityStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setDoorSecurityState(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkComplexIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setComplexId(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkComplexNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setComplexName(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPropertyIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setPropertyId(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkPropertyNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setPropertyName(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        // set the field null
        report.setCreatedAt(null);

        // Create the Report, which fails.

        restReportMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllReports() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get all the reportList
        restReportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportTitle").value(hasItem(DEFAULT_REPORT_TITLE)))
            .andExpect(jsonPath("$.[*].reportDate").value(hasItem(DEFAULT_REPORT_DATE.toString())))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].exteriorState").value(hasItem(DEFAULT_EXTERIOR_STATE.toString())))
            .andExpect(jsonPath("$.[*].constructionYear").value(hasItem(DEFAULT_CONSTRUCTION_YEAR)))
            .andExpect(jsonPath("$.[*].maintenanceState").value(hasItem(DEFAULT_MAINTENANCE_STATE.toString())))
            .andExpect(jsonPath("$.[*].parkingFacility").value(hasItem(DEFAULT_PARKING_FACILITY)))
            .andExpect(jsonPath("$.[*].parkingCount").value(hasItem(DEFAULT_PARKING_COUNT)))
            .andExpect(jsonPath("$.[*].elevatorState").value(hasItem(DEFAULT_ELEVATOR_STATE.toString())))
            .andExpect(jsonPath("$.[*].noiseState").value(hasItem(DEFAULT_NOISE_STATE.toString())))
            .andExpect(jsonPath("$.[*].homepadState").value(hasItem(DEFAULT_HOMEPAD_STATE.toString())))
            .andExpect(jsonPath("$.[*].cctvYn").value(hasItem(DEFAULT_CCTV_YN)))
            .andExpect(jsonPath("$.[*].fireSafetyState").value(hasItem(DEFAULT_FIRE_SAFETY_STATE.toString())))
            .andExpect(jsonPath("$.[*].doorSecurityState").value(hasItem(DEFAULT_DOOR_SECURITY_STATE.toString())))
            .andExpect(jsonPath("$.[*].maintenanceFee").value(hasItem(DEFAULT_MAINTENANCE_FEE)))
            .andExpect(jsonPath("$.[*].redevelopmentYn").value(hasItem(DEFAULT_REDEVELOPMENT_YN)))
            .andExpect(jsonPath("$.[*].rentalDemand").value(hasItem(DEFAULT_RENTAL_DEMAND)))
            .andExpect(jsonPath("$.[*].communityRules").value(hasItem(DEFAULT_COMMUNITY_RULES)))
            .andExpect(jsonPath("$.[*].complexId").value(hasItem(DEFAULT_COMPLEX_ID.intValue())))
            .andExpect(jsonPath("$.[*].complexName").value(hasItem(DEFAULT_COMPLEX_NAME)))
            .andExpect(jsonPath("$.[*].propertyId").value(hasItem(DEFAULT_PROPERTY_ID.intValue())))
            .andExpect(jsonPath("$.[*].propertyName").value(hasItem(DEFAULT_PROPERTY_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc
            .perform(get(ENTITY_API_URL_ID, report.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(report.getId().intValue()))
            .andExpect(jsonPath("$.reportTitle").value(DEFAULT_REPORT_TITLE))
            .andExpect(jsonPath("$.reportDate").value(DEFAULT_REPORT_DATE.toString()))
            .andExpect(jsonPath("$.authorId").value(DEFAULT_AUTHOR_ID.intValue()))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.exteriorState").value(DEFAULT_EXTERIOR_STATE.toString()))
            .andExpect(jsonPath("$.constructionYear").value(DEFAULT_CONSTRUCTION_YEAR))
            .andExpect(jsonPath("$.maintenanceState").value(DEFAULT_MAINTENANCE_STATE.toString()))
            .andExpect(jsonPath("$.parkingFacility").value(DEFAULT_PARKING_FACILITY))
            .andExpect(jsonPath("$.parkingCount").value(DEFAULT_PARKING_COUNT))
            .andExpect(jsonPath("$.elevatorState").value(DEFAULT_ELEVATOR_STATE.toString()))
            .andExpect(jsonPath("$.noiseState").value(DEFAULT_NOISE_STATE.toString()))
            .andExpect(jsonPath("$.homepadState").value(DEFAULT_HOMEPAD_STATE.toString()))
            .andExpect(jsonPath("$.cctvYn").value(DEFAULT_CCTV_YN))
            .andExpect(jsonPath("$.fireSafetyState").value(DEFAULT_FIRE_SAFETY_STATE.toString()))
            .andExpect(jsonPath("$.doorSecurityState").value(DEFAULT_DOOR_SECURITY_STATE.toString()))
            .andExpect(jsonPath("$.maintenanceFee").value(DEFAULT_MAINTENANCE_FEE))
            .andExpect(jsonPath("$.redevelopmentYn").value(DEFAULT_REDEVELOPMENT_YN))
            .andExpect(jsonPath("$.rentalDemand").value(DEFAULT_RENTAL_DEMAND))
            .andExpect(jsonPath("$.communityRules").value(DEFAULT_COMMUNITY_RULES))
            .andExpect(jsonPath("$.complexId").value(DEFAULT_COMPLEX_ID.intValue()))
            .andExpect(jsonPath("$.complexName").value(DEFAULT_COMPLEX_NAME))
            .andExpect(jsonPath("$.propertyId").value(DEFAULT_PROPERTY_ID.intValue()))
            .andExpect(jsonPath("$.propertyName").value(DEFAULT_PROPERTY_NAME))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportSearchRepository.save(report);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReport are not directly saved in db
        em.detach(updatedReport);
        updatedReport
            .reportTitle(UPDATED_REPORT_TITLE)
            .reportDate(UPDATED_REPORT_DATE)
            .authorId(UPDATED_AUTHOR_ID)
            .summary(UPDATED_SUMMARY)
            .exteriorState(UPDATED_EXTERIOR_STATE)
            .constructionYear(UPDATED_CONSTRUCTION_YEAR)
            .maintenanceState(UPDATED_MAINTENANCE_STATE)
            .parkingFacility(UPDATED_PARKING_FACILITY)
            .parkingCount(UPDATED_PARKING_COUNT)
            .elevatorState(UPDATED_ELEVATOR_STATE)
            .noiseState(UPDATED_NOISE_STATE)
            .homepadState(UPDATED_HOMEPAD_STATE)
            .cctvYn(UPDATED_CCTV_YN)
            .fireSafetyState(UPDATED_FIRE_SAFETY_STATE)
            .doorSecurityState(UPDATED_DOOR_SECURITY_STATE)
            .maintenanceFee(UPDATED_MAINTENANCE_FEE)
            .redevelopmentYn(UPDATED_REDEVELOPMENT_YN)
            .rentalDemand(UPDATED_RENTAL_DEMAND)
            .communityRules(UPDATED_COMMUNITY_RULES)
            .complexId(UPDATED_COMPLEX_ID)
            .complexName(UPDATED_COMPLEX_NAME)
            .propertyId(UPDATED_PROPERTY_ID)
            .propertyName(UPDATED_PROPERTY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedReport.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedReport))
            )
            .andExpect(status().isOk());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReportToMatchAllProperties(updatedReport);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Report> reportSearchList = Streamable.of(reportSearchRepository.findAll()).toList();
                Report testReportSearch = reportSearchList.get(searchDatabaseSizeAfter - 1);

                assertReportAllPropertiesEquals(testReportSearch, updatedReport);
            });
    }

    @Test
    @Transactional
    void putNonExistingReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        report.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, report.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(report))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(report))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(report)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateReportWithPatch() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport
            .authorId(UPDATED_AUTHOR_ID)
            .summary(UPDATED_SUMMARY)
            .exteriorState(UPDATED_EXTERIOR_STATE)
            .constructionYear(UPDATED_CONSTRUCTION_YEAR)
            .parkingFacility(UPDATED_PARKING_FACILITY)
            .parkingCount(UPDATED_PARKING_COUNT)
            .elevatorState(UPDATED_ELEVATOR_STATE)
            .noiseState(UPDATED_NOISE_STATE)
            .doorSecurityState(UPDATED_DOOR_SECURITY_STATE)
            .redevelopmentYn(UPDATED_REDEVELOPMENT_YN)
            .rentalDemand(UPDATED_RENTAL_DEMAND)
            .complexName(UPDATED_COMPLEX_NAME)
            .propertyName(UPDATED_PROPERTY_NAME)
            .createdAt(UPDATED_CREATED_AT);

        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReport))
            )
            .andExpect(status().isOk());

        // Validate the Report in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReport, report), getPersistedReport(report));
    }

    @Test
    @Transactional
    void fullUpdateReportWithPatch() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport
            .reportTitle(UPDATED_REPORT_TITLE)
            .reportDate(UPDATED_REPORT_DATE)
            .authorId(UPDATED_AUTHOR_ID)
            .summary(UPDATED_SUMMARY)
            .exteriorState(UPDATED_EXTERIOR_STATE)
            .constructionYear(UPDATED_CONSTRUCTION_YEAR)
            .maintenanceState(UPDATED_MAINTENANCE_STATE)
            .parkingFacility(UPDATED_PARKING_FACILITY)
            .parkingCount(UPDATED_PARKING_COUNT)
            .elevatorState(UPDATED_ELEVATOR_STATE)
            .noiseState(UPDATED_NOISE_STATE)
            .homepadState(UPDATED_HOMEPAD_STATE)
            .cctvYn(UPDATED_CCTV_YN)
            .fireSafetyState(UPDATED_FIRE_SAFETY_STATE)
            .doorSecurityState(UPDATED_DOOR_SECURITY_STATE)
            .maintenanceFee(UPDATED_MAINTENANCE_FEE)
            .redevelopmentYn(UPDATED_REDEVELOPMENT_YN)
            .rentalDemand(UPDATED_RENTAL_DEMAND)
            .communityRules(UPDATED_COMMUNITY_RULES)
            .complexId(UPDATED_COMPLEX_ID)
            .complexName(UPDATED_COMPLEX_NAME)
            .propertyId(UPDATED_PROPERTY_ID)
            .propertyName(UPDATED_PROPERTY_NAME)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReport.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReport))
            )
            .andExpect(status().isOk());

        // Validate the Report in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportUpdatableFieldsEquals(partialUpdatedReport, getPersistedReport(partialUpdatedReport));
    }

    @Test
    @Transactional
    void patchNonExistingReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        report.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, report.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(report))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(report))
            )
            .andExpect(status().isBadRequest());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReportMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(report)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);
        reportRepository.save(report);
        reportSearchRepository.save(report);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the report
        restReportMockMvc
            .perform(delete(ENTITY_API_URL_ID, report.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.saveAndFlush(report);
        reportSearchRepository.save(report);

        // Search the report
        restReportMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + report.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
            .andExpect(jsonPath("$.[*].reportTitle").value(hasItem(DEFAULT_REPORT_TITLE)))
            .andExpect(jsonPath("$.[*].reportDate").value(hasItem(DEFAULT_REPORT_DATE.toString())))
            .andExpect(jsonPath("$.[*].authorId").value(hasItem(DEFAULT_AUTHOR_ID.intValue())))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].exteriorState").value(hasItem(DEFAULT_EXTERIOR_STATE.toString())))
            .andExpect(jsonPath("$.[*].constructionYear").value(hasItem(DEFAULT_CONSTRUCTION_YEAR)))
            .andExpect(jsonPath("$.[*].maintenanceState").value(hasItem(DEFAULT_MAINTENANCE_STATE.toString())))
            .andExpect(jsonPath("$.[*].parkingFacility").value(hasItem(DEFAULT_PARKING_FACILITY)))
            .andExpect(jsonPath("$.[*].parkingCount").value(hasItem(DEFAULT_PARKING_COUNT)))
            .andExpect(jsonPath("$.[*].elevatorState").value(hasItem(DEFAULT_ELEVATOR_STATE.toString())))
            .andExpect(jsonPath("$.[*].noiseState").value(hasItem(DEFAULT_NOISE_STATE.toString())))
            .andExpect(jsonPath("$.[*].homepadState").value(hasItem(DEFAULT_HOMEPAD_STATE.toString())))
            .andExpect(jsonPath("$.[*].cctvYn").value(hasItem(DEFAULT_CCTV_YN)))
            .andExpect(jsonPath("$.[*].fireSafetyState").value(hasItem(DEFAULT_FIRE_SAFETY_STATE.toString())))
            .andExpect(jsonPath("$.[*].doorSecurityState").value(hasItem(DEFAULT_DOOR_SECURITY_STATE.toString())))
            .andExpect(jsonPath("$.[*].maintenanceFee").value(hasItem(DEFAULT_MAINTENANCE_FEE)))
            .andExpect(jsonPath("$.[*].redevelopmentYn").value(hasItem(DEFAULT_REDEVELOPMENT_YN)))
            .andExpect(jsonPath("$.[*].rentalDemand").value(hasItem(DEFAULT_RENTAL_DEMAND)))
            .andExpect(jsonPath("$.[*].communityRules").value(hasItem(DEFAULT_COMMUNITY_RULES)))
            .andExpect(jsonPath("$.[*].complexId").value(hasItem(DEFAULT_COMPLEX_ID.intValue())))
            .andExpect(jsonPath("$.[*].complexName").value(hasItem(DEFAULT_COMPLEX_NAME)))
            .andExpect(jsonPath("$.[*].propertyId").value(hasItem(DEFAULT_PROPERTY_ID.intValue())))
            .andExpect(jsonPath("$.[*].propertyName").value(hasItem(DEFAULT_PROPERTY_NAME)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    protected long getRepositoryCount() {
        return reportRepository.count();
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

    protected Report getPersistedReport(Report report) {
        return reportRepository.findById(report.getId()).orElseThrow();
    }

    protected void assertPersistedReportToMatchAllProperties(Report expectedReport) {
        assertReportAllPropertiesEquals(expectedReport, getPersistedReport(expectedReport));
    }

    protected void assertPersistedReportToMatchUpdatableProperties(Report expectedReport) {
        assertReportAllUpdatablePropertiesEquals(expectedReport, getPersistedReport(expectedReport));
    }
}
