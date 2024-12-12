package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.repository.ReportRepository;
import com.dnc.mprs.reportservice.repository.search.ReportSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Report save(Report report) {
        LOG.debug("Request to save Report : {}", report);
        report = reportRepository.save(report);
        reportSearchRepository.index(report);
        return report;
    }

    /**
     * Update a report.
     *
     * @param report the entity to save.
     * @return the persisted entity.
     */
    public Report update(Report report) {
        LOG.debug("Request to update Report : {}", report);
        report = reportRepository.save(report);
        reportSearchRepository.index(report);
        return report;
    }

    /**
     * Partially update a report.
     *
     * @param report the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Report> partialUpdate(Report report) {
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
                if (report.getAuthorId() != null) {
                    existingReport.setAuthorId(report.getAuthorId());
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
            .map(reportRepository::save)
            .map(savedReport -> {
                reportSearchRepository.index(savedReport);
                return savedReport;
            });
    }

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Report> findAll(Pageable pageable) {
        LOG.debug("Request to get all Reports");
        return reportRepository.findAll(pageable);
    }

    /**
     * Get one report by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Report> findOne(Long id) {
        LOG.debug("Request to get Report : {}", id);
        return reportRepository.findById(id);
    }

    /**
     * Delete the report by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Report : {}", id);
        reportRepository.deleteById(id);
        reportSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the report corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Report> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Reports for query {}", query);
        return reportSearchRepository.search(query, pageable);
    }
}
