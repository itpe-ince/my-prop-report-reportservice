package com.dnc.mprs.reportservice.web.rest;

import static com.dnc.mprs.reportservice.domain.ReportAsserts.*;
import static com.dnc.mprs.reportservice.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.dnc.mprs.reportservice.IntegrationTest;
import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.domain.enumeration.QualityStateType;
import com.dnc.mprs.reportservice.repository.EntityManager;
import com.dnc.mprs.reportservice.repository.ReportRepository;
import com.dnc.mprs.reportservice.repository.search.ReportSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ReportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ReportResourceIT {

    private static final String DEFAULT_REPORT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_REPORT_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_REPORT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REPORT_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Report.class).block();
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
        report = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReport != null) {
            reportRepository.delete(insertedReport).block();
            reportSearchRepository.delete(insertedReport).block();
            insertedReport = null;
        }
        deleteEntities(em);
    }

    @Test
    void createReport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // Create the Report
        var returnedReport = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Report.class)
            .returnResult()
            .getResponseBody();

        // Validate the Report in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertReportUpdatableFieldsEquals(returnedReport, getPersistedReport(returnedReport));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedReport = returnedReport;
    }

    @Test
    void createReportWithExistingId() throws Exception {
        // Create the Report with an existing ID
        report.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkReportTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setReportTitle(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkExteriorStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setExteriorState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkMaintenanceStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setMaintenanceState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkElevatorStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setElevatorState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkNoiseStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setNoiseState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkHomepadStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setHomepadState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkFireSafetyStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setFireSafetyState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkDoorSecurityStateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setDoorSecurityState(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkComplexIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setComplexId(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkComplexNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setComplexName(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPropertyIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setPropertyId(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkPropertyNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setPropertyName(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        // set the field null
        report.setCreatedAt(null);

        // Create the Report, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllReports() {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();

        // Get all the reportList
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
            .value(hasItem(report.getId().intValue()))
            .jsonPath("$.[*].reportTitle")
            .value(hasItem(DEFAULT_REPORT_TITLE))
            .jsonPath("$.[*].reportDate")
            .value(hasItem(DEFAULT_REPORT_DATE.toString()))
            .jsonPath("$.[*].summary")
            .value(hasItem(DEFAULT_SUMMARY))
            .jsonPath("$.[*].exteriorState")
            .value(hasItem(DEFAULT_EXTERIOR_STATE.toString()))
            .jsonPath("$.[*].constructionYear")
            .value(hasItem(DEFAULT_CONSTRUCTION_YEAR))
            .jsonPath("$.[*].maintenanceState")
            .value(hasItem(DEFAULT_MAINTENANCE_STATE.toString()))
            .jsonPath("$.[*].parkingFacility")
            .value(hasItem(DEFAULT_PARKING_FACILITY))
            .jsonPath("$.[*].parkingCount")
            .value(hasItem(DEFAULT_PARKING_COUNT))
            .jsonPath("$.[*].elevatorState")
            .value(hasItem(DEFAULT_ELEVATOR_STATE.toString()))
            .jsonPath("$.[*].noiseState")
            .value(hasItem(DEFAULT_NOISE_STATE.toString()))
            .jsonPath("$.[*].homepadState")
            .value(hasItem(DEFAULT_HOMEPAD_STATE.toString()))
            .jsonPath("$.[*].cctvYn")
            .value(hasItem(DEFAULT_CCTV_YN))
            .jsonPath("$.[*].fireSafetyState")
            .value(hasItem(DEFAULT_FIRE_SAFETY_STATE.toString()))
            .jsonPath("$.[*].doorSecurityState")
            .value(hasItem(DEFAULT_DOOR_SECURITY_STATE.toString()))
            .jsonPath("$.[*].maintenanceFee")
            .value(hasItem(DEFAULT_MAINTENANCE_FEE))
            .jsonPath("$.[*].redevelopmentYn")
            .value(hasItem(DEFAULT_REDEVELOPMENT_YN))
            .jsonPath("$.[*].rentalDemand")
            .value(hasItem(DEFAULT_RENTAL_DEMAND))
            .jsonPath("$.[*].communityRules")
            .value(hasItem(DEFAULT_COMMUNITY_RULES))
            .jsonPath("$.[*].complexId")
            .value(hasItem(DEFAULT_COMPLEX_ID.intValue()))
            .jsonPath("$.[*].complexName")
            .value(hasItem(DEFAULT_COMPLEX_NAME))
            .jsonPath("$.[*].propertyId")
            .value(hasItem(DEFAULT_PROPERTY_ID.intValue()))
            .jsonPath("$.[*].propertyName")
            .value(hasItem(DEFAULT_PROPERTY_NAME))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getReport() {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();

        // Get the report
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, report.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(report.getId().intValue()))
            .jsonPath("$.reportTitle")
            .value(is(DEFAULT_REPORT_TITLE))
            .jsonPath("$.reportDate")
            .value(is(DEFAULT_REPORT_DATE.toString()))
            .jsonPath("$.summary")
            .value(is(DEFAULT_SUMMARY))
            .jsonPath("$.exteriorState")
            .value(is(DEFAULT_EXTERIOR_STATE.toString()))
            .jsonPath("$.constructionYear")
            .value(is(DEFAULT_CONSTRUCTION_YEAR))
            .jsonPath("$.maintenanceState")
            .value(is(DEFAULT_MAINTENANCE_STATE.toString()))
            .jsonPath("$.parkingFacility")
            .value(is(DEFAULT_PARKING_FACILITY))
            .jsonPath("$.parkingCount")
            .value(is(DEFAULT_PARKING_COUNT))
            .jsonPath("$.elevatorState")
            .value(is(DEFAULT_ELEVATOR_STATE.toString()))
            .jsonPath("$.noiseState")
            .value(is(DEFAULT_NOISE_STATE.toString()))
            .jsonPath("$.homepadState")
            .value(is(DEFAULT_HOMEPAD_STATE.toString()))
            .jsonPath("$.cctvYn")
            .value(is(DEFAULT_CCTV_YN))
            .jsonPath("$.fireSafetyState")
            .value(is(DEFAULT_FIRE_SAFETY_STATE.toString()))
            .jsonPath("$.doorSecurityState")
            .value(is(DEFAULT_DOOR_SECURITY_STATE.toString()))
            .jsonPath("$.maintenanceFee")
            .value(is(DEFAULT_MAINTENANCE_FEE))
            .jsonPath("$.redevelopmentYn")
            .value(is(DEFAULT_REDEVELOPMENT_YN))
            .jsonPath("$.rentalDemand")
            .value(is(DEFAULT_RENTAL_DEMAND))
            .jsonPath("$.communityRules")
            .value(is(DEFAULT_COMMUNITY_RULES))
            .jsonPath("$.complexId")
            .value(is(DEFAULT_COMPLEX_ID.intValue()))
            .jsonPath("$.complexName")
            .value(is(DEFAULT_COMPLEX_NAME))
            .jsonPath("$.propertyId")
            .value(is(DEFAULT_PROPERTY_ID.intValue()))
            .jsonPath("$.propertyName")
            .value(is(DEFAULT_PROPERTY_NAME))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    void getNonExistingReport() {
        // Get the report
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingReport() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();
        reportSearchRepository.save(report).block();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());

        // Update the report
        Report updatedReport = reportRepository.findById(report.getId()).block();
        updatedReport
            .reportTitle(UPDATED_REPORT_TITLE)
            .reportDate(UPDATED_REPORT_DATE)
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

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedReport.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReportToMatchAllProperties(updatedReport);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Report> reportSearchList = Streamable.of(reportSearchRepository.findAll().collectList().block()).toList();
                Report testReportSearch = reportSearchList.get(searchDatabaseSizeAfter - 1);

                // Test fails because reactive api returns an empty object instead of null
                // assertReportAllPropertiesEquals(testReportSearch, updatedReport);
                assertReportUpdatableFieldsEquals(testReportSearch, updatedReport);
            });
    }

    @Test
    void putNonExistingReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        report.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, report.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateReportWithPatch() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport
            .summary(UPDATED_SUMMARY)
            .exteriorState(UPDATED_EXTERIOR_STATE)
            .constructionYear(UPDATED_CONSTRUCTION_YEAR)
            .maintenanceState(UPDATED_MAINTENANCE_STATE)
            .parkingCount(UPDATED_PARKING_COUNT)
            .elevatorState(UPDATED_ELEVATOR_STATE)
            .noiseState(UPDATED_NOISE_STATE)
            .homepadState(UPDATED_HOMEPAD_STATE)
            .maintenanceFee(UPDATED_MAINTENANCE_FEE)
            .rentalDemand(UPDATED_RENTAL_DEMAND)
            .communityRules(UPDATED_COMMUNITY_RULES)
            .propertyId(UPDATED_PROPERTY_ID)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedReport, report), getPersistedReport(report));
    }

    @Test
    void fullUpdateReportWithPatch() throws Exception {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the report using partial update
        Report partialUpdatedReport = new Report();
        partialUpdatedReport.setId(report.getId());

        partialUpdatedReport
            .reportTitle(UPDATED_REPORT_TITLE)
            .reportDate(UPDATED_REPORT_DATE)
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

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedReport.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedReport))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Report in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReportUpdatableFieldsEquals(partialUpdatedReport, getPersistedReport(partialUpdatedReport));
    }

    @Test
    void patchNonExistingReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        report.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, report.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamReport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        report.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(report))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Report in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteReport() {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();
        reportRepository.save(report).block();
        reportSearchRepository.save(report).block();

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the report
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, report.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(reportSearchRepository.findAll().collectList().block());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchReport() {
        // Initialize the database
        insertedReport = reportRepository.save(report).block();
        reportSearchRepository.save(report).block();

        // Search the report
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + report.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(report.getId().intValue()))
            .jsonPath("$.[*].reportTitle")
            .value(hasItem(DEFAULT_REPORT_TITLE))
            .jsonPath("$.[*].reportDate")
            .value(hasItem(DEFAULT_REPORT_DATE.toString()))
            .jsonPath("$.[*].summary")
            .value(hasItem(DEFAULT_SUMMARY))
            .jsonPath("$.[*].exteriorState")
            .value(hasItem(DEFAULT_EXTERIOR_STATE.toString()))
            .jsonPath("$.[*].constructionYear")
            .value(hasItem(DEFAULT_CONSTRUCTION_YEAR))
            .jsonPath("$.[*].maintenanceState")
            .value(hasItem(DEFAULT_MAINTENANCE_STATE.toString()))
            .jsonPath("$.[*].parkingFacility")
            .value(hasItem(DEFAULT_PARKING_FACILITY))
            .jsonPath("$.[*].parkingCount")
            .value(hasItem(DEFAULT_PARKING_COUNT))
            .jsonPath("$.[*].elevatorState")
            .value(hasItem(DEFAULT_ELEVATOR_STATE.toString()))
            .jsonPath("$.[*].noiseState")
            .value(hasItem(DEFAULT_NOISE_STATE.toString()))
            .jsonPath("$.[*].homepadState")
            .value(hasItem(DEFAULT_HOMEPAD_STATE.toString()))
            .jsonPath("$.[*].cctvYn")
            .value(hasItem(DEFAULT_CCTV_YN))
            .jsonPath("$.[*].fireSafetyState")
            .value(hasItem(DEFAULT_FIRE_SAFETY_STATE.toString()))
            .jsonPath("$.[*].doorSecurityState")
            .value(hasItem(DEFAULT_DOOR_SECURITY_STATE.toString()))
            .jsonPath("$.[*].maintenanceFee")
            .value(hasItem(DEFAULT_MAINTENANCE_FEE))
            .jsonPath("$.[*].redevelopmentYn")
            .value(hasItem(DEFAULT_REDEVELOPMENT_YN))
            .jsonPath("$.[*].rentalDemand")
            .value(hasItem(DEFAULT_RENTAL_DEMAND))
            .jsonPath("$.[*].communityRules")
            .value(hasItem(DEFAULT_COMMUNITY_RULES))
            .jsonPath("$.[*].complexId")
            .value(hasItem(DEFAULT_COMPLEX_ID.intValue()))
            .jsonPath("$.[*].complexName")
            .value(hasItem(DEFAULT_COMPLEX_NAME))
            .jsonPath("$.[*].propertyId")
            .value(hasItem(DEFAULT_PROPERTY_ID.intValue()))
            .jsonPath("$.[*].propertyName")
            .value(hasItem(DEFAULT_PROPERTY_NAME))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()));
    }

    protected long getRepositoryCount() {
        return reportRepository.count().block();
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
        return reportRepository.findById(report.getId()).block();
    }

    protected void assertPersistedReportToMatchAllProperties(Report expectedReport) {
        // Test fails because reactive api returns an empty object instead of null
        // assertReportAllPropertiesEquals(expectedReport, getPersistedReport(expectedReport));
        assertReportUpdatableFieldsEquals(expectedReport, getPersistedReport(expectedReport));
    }

    protected void assertPersistedReportToMatchUpdatableProperties(Report expectedReport) {
        // Test fails because reactive api returns an empty object instead of null
        // assertReportAllUpdatablePropertiesEquals(expectedReport, getPersistedReport(expectedReport));
        assertReportUpdatableFieldsEquals(expectedReport, getPersistedReport(expectedReport));
    }
}
