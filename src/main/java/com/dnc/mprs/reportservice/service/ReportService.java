package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.repository.ReportRepository;
import com.dnc.mprs.reportservice.repository.search.ReportSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Report}.
 */
@Service
@Transactional
public class ReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository reportRepository;

    private final ReportSearchRepository reportSearchRepository;

    public ReportService(ReportRepository reportRepository, ReportSearchRepository reportSearchRepository) {
        this.reportRepository = reportRepository;
        this.reportSearchRepository = reportSearchRepository;
    }

    /**
     * Save a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    public Mono<Report> save(Report report) {
        LOG.debug("Request to save Report : {}", report);
        return reportRepository.save(report).flatMap(reportSearchRepository::save);
    }

    /**
     * Update a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    public Mono<Report> update(Report report) {
        LOG.debug("Request to update Report : {}", report);
        return reportRepository.save(report).flatMap(reportSearchRepository::save);
    }

    /**
     * Partially update a report.
     *
     * @param report the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Report> partialUpdate(Report report) {
        LOG.debug("Request to partially update Report : {}", report);

        return reportRepository
            .findById(report.getId())
            .map(existingReport -> {
                if (report.getReportTitle() != null) {
                    existingReport.setReportTitle(report.getReportTitle());
                }
                if (report.getReportDate() != null) {
                    existingReport.setReportDate(report.getReportDate());
                }
                if (report.getSummary() != null) {
                    existingReport.setSummary(report.getSummary());
                }
                if (report.getExteriorState() != null) {
                    existingReport.setExteriorState(report.getExteriorState());
                }
                if (report.getConstructionYear() != null) {
                    existingReport.setConstructionYear(report.getConstructionYear());
                }
                if (report.getMaintenanceState() != null) {
                    existingReport.setMaintenanceState(report.getMaintenanceState());
                }
                if (report.getParkingFacility() != null) {
                    existingReport.setParkingFacility(report.getParkingFacility());
                }
                if (report.getParkingCount() != null) {
                    existingReport.setParkingCount(report.getParkingCount());
                }
                if (report.getElevatorState() != null) {
                    existingReport.setElevatorState(report.getElevatorState());
                }
                if (report.getNoiseState() != null) {
                    existingReport.setNoiseState(report.getNoiseState());
                }
                if (report.getHomepadState() != null) {
                    existingReport.setHomepadState(report.getHomepadState());
                }
                if (report.getCctvYn() != null) {
                    existingReport.setCctvYn(report.getCctvYn());
                }
                if (report.getFireSafetyState() != null) {
                    existingReport.setFireSafetyState(report.getFireSafetyState());
                }
                if (report.getDoorSecurityState() != null) {
                    existingReport.setDoorSecurityState(report.getDoorSecurityState());
                }
                if (report.getMaintenanceFee() != null) {
                    existingReport.setMaintenanceFee(report.getMaintenanceFee());
                }
                if (report.getRedevelopmentYn() != null) {
                    existingReport.setRedevelopmentYn(report.getRedevelopmentYn());
                }
                if (report.getRentalDemand() != null) {
                    existingReport.setRentalDemand(report.getRentalDemand());
                }
                if (report.getCommunityRules() != null) {
                    existingReport.setCommunityRules(report.getCommunityRules());
                }
                if (report.getComplexId() != null) {
                    existingReport.setComplexId(report.getComplexId());
                }
                if (report.getComplexName() != null) {
                    existingReport.setComplexName(report.getComplexName());
                }
                if (report.getPropertyId() != null) {
                    existingReport.setPropertyId(report.getPropertyId());
                }
                if (report.getPropertyName() != null) {
                    existingReport.setPropertyName(report.getPropertyName());
                }
                if (report.getCreatedAt() != null) {
                    existingReport.setCreatedAt(report.getCreatedAt());
                }
                if (report.getUpdatedAt() != null) {
                    existingReport.setUpdatedAt(report.getUpdatedAt());
                }

                return existingReport;
            })
            .flatMap(reportRepository::save)
            .flatMap(savedReport -> {
                reportSearchRepository.save(savedReport);
                return Mono.just(savedReport);
            });
    }

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Report> findAll(Pageable pageable) {
        LOG.debug("Request to get all Reports");
        return reportRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of reports available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return reportRepository.count();
    }

    /**
     * Returns the number of reports available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return reportSearchRepository.count();
    }

    /**
     * Get one report by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Report> findOne(Long id) {
        LOG.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Report : {}", id);
        return reportRepository.deleteById(id).then(reportSearchRepository.deleteById(id));
    }

    /**
     * Search for the report corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Report> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Reports for query {}", query);
        return reportSearchRepository.search(query, pageable);
    }
}
