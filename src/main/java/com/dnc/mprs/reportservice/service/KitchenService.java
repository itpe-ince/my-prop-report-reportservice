package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
import com.dnc.mprs.reportservice.repository.search.KitchenSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Kitchen}.
 */
@Service
@Transactional
public class KitchenService {

    private static final Logger LOG = LoggerFactory.getLogger(KitchenService.class);

    private final KitchenRepository kitchenRepository;

    private final KitchenSearchRepository kitchenSearchRepository;

    public KitchenService(KitchenRepository kitchenRepository, KitchenSearchRepository kitchenSearchRepository) {
        this.kitchenRepository = kitchenRepository;
        this.kitchenSearchRepository = kitchenSearchRepository;
    }

    /**
     * Save a kitchen.
     *
     * @param kitchen the entity to save.
     * @return the persisted entity.
     */
    public Mono<Kitchen> save(Kitchen kitchen) {
        LOG.debug("Request to save Kitchen : {}", kitchen);
        return kitchenRepository.save(kitchen).flatMap(kitchenSearchRepository::save);
    }

    /**
     * Update a kitchen.
     *
     * @param kitchen the entity to save.
     * @return the persisted entity.
     */
    public Mono<Kitchen> update(Kitchen kitchen) {
        LOG.debug("Request to update Kitchen : {}", kitchen);
        return kitchenRepository.save(kitchen).flatMap(kitchenSearchRepository::save);
    }

    /**
     * Partially update a kitchen.
     *
     * @param kitchen the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Kitchen> partialUpdate(Kitchen kitchen) {
        LOG.debug("Request to partially update Kitchen : {}", kitchen);

        return kitchenRepository
            .findById(kitchen.getId())
            .map(existingKitchen -> {
                if (kitchen.getKitchenName() != null) {
                    existingKitchen.setKitchenName(kitchen.getKitchenName());
                }
                if (kitchen.getConditionLevel() != null) {
                    existingKitchen.setConditionLevel(kitchen.getConditionLevel());
                }
                if (kitchen.getBuiltInCabinet() != null) {
                    existingKitchen.setBuiltInCabinet(kitchen.getBuiltInCabinet());
                }
                if (kitchen.getSinkCondition() != null) {
                    existingKitchen.setSinkCondition(kitchen.getSinkCondition());
                }
                if (kitchen.getVentilationSystem() != null) {
                    existingKitchen.setVentilationSystem(kitchen.getVentilationSystem());
                }
                if (kitchen.getApplianceProvision() != null) {
                    existingKitchen.setApplianceProvision(kitchen.getApplianceProvision());
                }
                if (kitchen.getRemarks() != null) {
                    existingKitchen.setRemarks(kitchen.getRemarks());
                }

                return existingKitchen;
            })
            .flatMap(kitchenRepository::save)
            .flatMap(savedKitchen -> {
                kitchenSearchRepository.save(savedKitchen);
                return Mono.just(savedKitchen);
            });
    }

    /**
     * Get all the kitchens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Kitchen> findAll(Pageable pageable) {
        LOG.debug("Request to get all Kitchens");
        return kitchenRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of kitchens available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return kitchenRepository.count();
    }

    /**
     * Returns the number of kitchens available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return kitchenSearchRepository.count();
    }

    /**
     * Get one kitchen by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Kitchen> findOne(Long id) {
        LOG.debug("Request to get Kitchen : {}", id);
        return kitchenRepository.findById(id);
    }

    /**
     * Delete the kitchen by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Kitchen : {}", id);
        return kitchenRepository.deleteById(id).then(kitchenSearchRepository.deleteById(id));
    }

    /**
     * Search for the kitchen corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Kitchen> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Kitchens for query {}", query);
        return kitchenSearchRepository.search(query, pageable);
    }
}
