package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.repository.InfrastructureRepository;
import com.dnc.mprs.reportservice.repository.search.InfrastructureSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Infrastructure}.
 */
@Service
@Transactional
public class InfrastructureService {

    private static final Logger LOG = LoggerFactory.getLogger(InfrastructureService.class);

    private final InfrastructureRepository infrastructureRepository;

    private final InfrastructureSearchRepository infrastructureSearchRepository;

    public InfrastructureService(
        InfrastructureRepository infrastructureRepository,
        InfrastructureSearchRepository infrastructureSearchRepository
    ) {
        this.infrastructureRepository = infrastructureRepository;
        this.infrastructureSearchRepository = infrastructureSearchRepository;
    }

    /**
     * Save a infrastructure.
     *
     * @param infrastructure the entity to save.
     * @return the persisted entity.
     */
    public Infrastructure save(Infrastructure infrastructure) {
        LOG.debug("Request to save Infrastructure : {}", infrastructure);
        infrastructure = infrastructureRepository.save(infrastructure);
        infrastructureSearchRepository.index(infrastructure);
        return infrastructure;
    }

    /**
     * Update a infrastructure.
     *
     * @param infrastructure the entity to save.
     * @return the persisted entity.
     */
    public Infrastructure update(Infrastructure infrastructure) {
        LOG.debug("Request to update Infrastructure : {}", infrastructure);
        infrastructure = infrastructureRepository.save(infrastructure);
        infrastructureSearchRepository.index(infrastructure);
        return infrastructure;
    }

    /**
     * Partially update a infrastructure.
     *
     * @param infrastructure the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Infrastructure> partialUpdate(Infrastructure infrastructure) {
        LOG.debug("Request to partially update Infrastructure : {}", infrastructure);

        return infrastructureRepository
            .findById(infrastructure.getId())
            .map(existingInfrastructure -> {
                if (infrastructure.getReportId() != null) {
                    existingInfrastructure.setReportId(infrastructure.getReportId());
                }
                if (infrastructure.getInfraType() != null) {
                    existingInfrastructure.setInfraType(infrastructure.getInfraType());
                }
                if (infrastructure.getInfraName() != null) {
                    existingInfrastructure.setInfraName(infrastructure.getInfraName());
                }
                if (infrastructure.getConditionLevel() != null) {
                    existingInfrastructure.setConditionLevel(infrastructure.getConditionLevel());
                }
                if (infrastructure.getInfraDistance() != null) {
                    existingInfrastructure.setInfraDistance(infrastructure.getInfraDistance());
                }
                if (infrastructure.getInfraDistanceUnit() != null) {
                    existingInfrastructure.setInfraDistanceUnit(infrastructure.getInfraDistanceUnit());
                }
                if (infrastructure.getRemarks() != null) {
                    existingInfrastructure.setRemarks(infrastructure.getRemarks());
                }

                return existingInfrastructure;
            })
            .map(infrastructureRepository::save)
            .map(savedInfrastructure -> {
                infrastructureSearchRepository.index(savedInfrastructure);
                return savedInfrastructure;
            });
    }

    /**
     * Get all the infrastructures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Infrastructure> findAll(Pageable pageable) {
        LOG.debug("Request to get all Infrastructures");
        return infrastructureRepository.findAll(pageable);
    }

    /**
     * Get one infrastructure by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Infrastructure> findOne(Long id) {
        LOG.debug("Request to get Infrastructure : {}", id);
        return infrastructureRepository.findById(id);
    }

    /**
     * Delete the infrastructure by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Infrastructure : {}", id);
        infrastructureRepository.deleteById(id);
        infrastructureSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the infrastructure corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Infrastructure> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Infrastructures for query {}", query);
        return infrastructureSearchRepository.search(query, pageable);
    }
}
