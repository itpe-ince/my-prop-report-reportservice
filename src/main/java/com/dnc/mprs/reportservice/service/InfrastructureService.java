package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.repository.InfrastructureRepository;
import com.dnc.mprs.reportservice.repository.search.InfrastructureSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Infrastructure> save(Infrastructure infrastructure) {
        LOG.debug("Request to save Infrastructure : {}", infrastructure);
        return infrastructureRepository.save(infrastructure).flatMap(infrastructureSearchRepository::save);
    }

    /**
     * Update a infrastructure.
     *
     * @param infrastructure the entity to save.
     * @return the persisted entity.
     */
    public Mono<Infrastructure> update(Infrastructure infrastructure) {
        LOG.debug("Request to update Infrastructure : {}", infrastructure);
        return infrastructureRepository.save(infrastructure).flatMap(infrastructureSearchRepository::save);
    }

    /**
     * Partially update a infrastructure.
     *
     * @param infrastructure the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Infrastructure> partialUpdate(Infrastructure infrastructure) {
        LOG.debug("Request to partially update Infrastructure : {}", infrastructure);

        return infrastructureRepository
            .findById(infrastructure.getId())
            .map(existingInfrastructure -> {
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
            .flatMap(infrastructureRepository::save)
            .flatMap(savedInfrastructure -> {
                infrastructureSearchRepository.save(savedInfrastructure);
                return Mono.just(savedInfrastructure);
            });
    }

    /**
     * Get all the infrastructures.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Infrastructure> findAll(Pageable pageable) {
        LOG.debug("Request to get all Infrastructures");
        return infrastructureRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of infrastructures available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return infrastructureRepository.count();
    }

    /**
     * Returns the number of infrastructures available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return infrastructureSearchRepository.count();
    }

    /**
     * Get one infrastructure by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Infrastructure> findOne(Long id) {
        LOG.debug("Request to get Infrastructure : {}", id);
        return infrastructureRepository.findById(id);
    }

    /**
     * Delete the infrastructure by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Infrastructure : {}", id);
        return infrastructureRepository.deleteById(id).then(infrastructureSearchRepository.deleteById(id));
    }

    /**
     * Search for the infrastructure corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Infrastructure> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Infrastructures for query {}", query);
        return infrastructureSearchRepository.search(query, pageable);
    }
}
