package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
import com.dnc.mprs.reportservice.repository.search.KitchenSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Kitchen save(Kitchen kitchen) {
        LOG.debug("Request to save Kitchen : {}", kitchen);
        kitchen = kitchenRepository.save(kitchen);
        kitchenSearchRepository.index(kitchen);
        return kitchen;
    }

    /**
     * Update a kitchen.
     *
     * @param kitchen the entity to save.
     * @return the persisted entity.
     */
    public Kitchen update(Kitchen kitchen) {
        LOG.debug("Request to update Kitchen : {}", kitchen);
        kitchen = kitchenRepository.save(kitchen);
        kitchenSearchRepository.index(kitchen);
        return kitchen;
    }

    /**
     * Partially update a kitchen.
     *
     * @param kitchen the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Kitchen> partialUpdate(Kitchen kitchen) {
        LOG.debug("Request to partially update Kitchen : {}", kitchen);

        return kitchenRepository
            .findById(kitchen.getId())
            .map(existingKitchen -> {
                if (kitchen.getReportId() != null) {
                    existingKitchen.setReportId(kitchen.getReportId());
                }
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
            .map(kitchenRepository::save)
            .map(savedKitchen -> {
                kitchenSearchRepository.index(savedKitchen);
                return savedKitchen;
            });
    }

    /**
     * Get all the kitchens.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Kitchen> findAll(Pageable pageable) {
        LOG.debug("Request to get all Kitchens");
        return kitchenRepository.findAll(pageable);
    }

    /**
     * Get one kitchen by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Kitchen> findOne(Long id) {
        LOG.debug("Request to get Kitchen : {}", id);
        return kitchenRepository.findById(id);
    }

    /**
     * Delete the kitchen by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Kitchen : {}", id);
        kitchenRepository.deleteById(id);
        kitchenSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the kitchen corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Kitchen> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Kitchens for query {}", query);
        return kitchenSearchRepository.search(query, pageable);
    }
}
