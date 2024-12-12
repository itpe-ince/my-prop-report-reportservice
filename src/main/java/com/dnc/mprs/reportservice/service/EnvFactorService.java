package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.EnvFactor;
import com.dnc.mprs.reportservice.repository.EnvFactorRepository;
import com.dnc.mprs.reportservice.repository.search.EnvFactorSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public EnvFactor save(EnvFactor envFactor) {
        LOG.debug("Request to save EnvFactor : {}", envFactor);
        envFactor = envFactorRepository.save(envFactor);
        envFactorSearchRepository.index(envFactor);
        return envFactor;
    }

    /**
     * Update a envFactor.
     *
     * @param envFactor the entity to save.
     * @return the persisted entity.
     */
    public EnvFactor update(EnvFactor envFactor) {
        LOG.debug("Request to update EnvFactor : {}", envFactor);
        envFactor = envFactorRepository.save(envFactor);
        envFactorSearchRepository.index(envFactor);
        return envFactor;
    }

    /**
     * Partially update a envFactor.
     *
     * @param envFactor the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EnvFactor> partialUpdate(EnvFactor envFactor) {
        LOG.debug("Request to partially update EnvFactor : {}", envFactor);

        return envFactorRepository
            .findById(envFactor.getId())
            .map(existingEnvFactor -> {
                if (envFactor.getReportId() != null) {
                    existingEnvFactor.setReportId(envFactor.getReportId());
                }
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
            .map(envFactorRepository::save)
            .map(savedEnvFactor -> {
                envFactorSearchRepository.index(savedEnvFactor);
                return savedEnvFactor;
            });
    }

    /**
     * Get all the envFactors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EnvFactor> findAll(Pageable pageable) {
        LOG.debug("Request to get all EnvFactors");
        return envFactorRepository.findAll(pageable);
    }

    /**
     * Get one envFactor by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EnvFactor> findOne(Long id) {
        LOG.debug("Request to get EnvFactor : {}", id);
        return envFactorRepository.findById(id);
    }

    /**
     * Delete the envFactor by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EnvFactor : {}", id);
        envFactorRepository.deleteById(id);
        envFactorSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the envFactor corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EnvFactor> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of EnvFactors for query {}", query);
        return envFactorSearchRepository.search(query, pageable);
    }
}
