package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.EnvFactor;
import com.dnc.mprs.reportservice.repository.EnvFactorRepository;
import com.dnc.mprs.reportservice.repository.search.EnvFactorSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.EnvFactor}.
 */
@Service
@Transactional
public class EnvFactorService {

    private static final Logger LOG = LoggerFactory.getLogger(EnvFactorService.class);

    private final EnvFactorRepository envFactorRepository;

    private final EnvFactorSearchRepository envFactorSearchRepository;

    public EnvFactorService(EnvFactorRepository envFactorRepository, EnvFactorSearchRepository envFactorSearchRepository) {
        this.envFactorRepository = envFactorRepository;
        this.envFactorSearchRepository = envFactorSearchRepository;
    }

    /**
     * Save a envFactor.
     *
     * @param envFactor the entity to save.
     * @return the persisted entity.
     */
    public Mono<EnvFactor> save(EnvFactor envFactor) {
        LOG.debug("Request to save EnvFactor : {}", envFactor);
        return envFactorRepository.save(envFactor).flatMap(envFactorSearchRepository::save);
    }

    /**
     * Update a envFactor.
     *
     * @param envFactor the entity to save.
     * @return the persisted entity.
     */
    public Mono<EnvFactor> update(EnvFactor envFactor) {
        LOG.debug("Request to update EnvFactor : {}", envFactor);
        return envFactorRepository.save(envFactor).flatMap(envFactorSearchRepository::save);
    }

    /**
     * Partially update a envFactor.
     *
     * @param envFactor the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<EnvFactor> partialUpdate(EnvFactor envFactor) {
        LOG.debug("Request to partially update EnvFactor : {}", envFactor);

        return envFactorRepository
            .findById(envFactor.getId())
            .map(existingEnvFactor -> {
                if (envFactor.getEnvFactorName() != null) {
                    existingEnvFactor.setEnvFactorName(envFactor.getEnvFactorName());
                }
                if (envFactor.getEnvFactorDistance() != null) {
                    existingEnvFactor.setEnvFactorDistance(envFactor.getEnvFactorDistance());
                }
                if (envFactor.getRemarks() != null) {
                    existingEnvFactor.setRemarks(envFactor.getRemarks());
                }

                return existingEnvFactor;
            })
            .flatMap(envFactorRepository::save)
            .flatMap(savedEnvFactor -> {
                envFactorSearchRepository.save(savedEnvFactor);
                return Mono.just(savedEnvFactor);
            });
    }

    /**
     * Get all the envFactors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EnvFactor> findAll(Pageable pageable) {
        LOG.debug("Request to get all EnvFactors");
        return envFactorRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of envFactors available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return envFactorRepository.count();
    }

    /**
     * Returns the number of envFactors available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return envFactorSearchRepository.count();
    }

    /**
     * Get one envFactor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<EnvFactor> findOne(Long id) {
        LOG.debug("Request to get EnvFactor : {}", id);
        return envFactorRepository.findById(id);
    }

    /**
     * Delete the envFactor by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EnvFactor : {}", id);
        return envFactorRepository.deleteById(id).then(envFactorSearchRepository.deleteById(id));
    }

    /**
     * Search for the envFactor corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EnvFactor> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of EnvFactors for query {}", query);
        return envFactorSearchRepository.search(query, pageable);
    }
}
