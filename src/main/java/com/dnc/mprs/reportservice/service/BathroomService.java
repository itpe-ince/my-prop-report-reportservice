package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.repository.BathroomRepository;
import com.dnc.mprs.reportservice.repository.search.BathroomSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Bathroom}.
 */
@Service
@Transactional
public class BathroomService {

    private static final Logger LOG = LoggerFactory.getLogger(BathroomService.class);

    private final BathroomRepository bathroomRepository;

    private final BathroomSearchRepository bathroomSearchRepository;

    public BathroomService(BathroomRepository bathroomRepository, BathroomSearchRepository bathroomSearchRepository) {
        this.bathroomRepository = bathroomRepository;
        this.bathroomSearchRepository = bathroomSearchRepository;
    }

    /**
     * Save a bathroom.
     *
     * @param bathroom the entity to save.
     * @return the persisted entity.
     */
    public Bathroom save(Bathroom bathroom) {
        LOG.debug("Request to save Bathroom : {}", bathroom);
        bathroom = bathroomRepository.save(bathroom);
        bathroomSearchRepository.index(bathroom);
        return bathroom;
    }

    /**
     * Update a bathroom.
     *
     * @param bathroom the entity to save.
     * @return the persisted entity.
     */
    public Bathroom update(Bathroom bathroom) {
        LOG.debug("Request to update Bathroom : {}", bathroom);
        bathroom = bathroomRepository.save(bathroom);
        bathroomSearchRepository.index(bathroom);
        return bathroom;
    }

    /**
     * Partially update a bathroom.
     *
     * @param bathroom the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Bathroom> partialUpdate(Bathroom bathroom) {
        LOG.debug("Request to partially update Bathroom : {}", bathroom);

        return bathroomRepository
            .findById(bathroom.getId())
            .map(existingBathroom -> {
                if (bathroom.getReportId() != null) {
                    existingBathroom.setReportId(bathroom.getReportId());
                }
                if (bathroom.getBathroomName() != null) {
                    existingBathroom.setBathroomName(bathroom.getBathroomName());
                }
                if (bathroom.getCondtionLevel() != null) {
                    existingBathroom.setCondtionLevel(bathroom.getCondtionLevel());
                }
                if (bathroom.getBathroomSize() != null) {
                    existingBathroom.setBathroomSize(bathroom.getBathroomSize());
                }
                if (bathroom.getWaterPressure() != null) {
                    existingBathroom.setWaterPressure(bathroom.getWaterPressure());
                }
                if (bathroom.getShowerBoothPresence() != null) {
                    existingBathroom.setShowerBoothPresence(bathroom.getShowerBoothPresence());
                }
                if (bathroom.getBathtubPresence() != null) {
                    existingBathroom.setBathtubPresence(bathroom.getBathtubPresence());
                }
                if (bathroom.getFloorAndCeiling() != null) {
                    existingBathroom.setFloorAndCeiling(bathroom.getFloorAndCeiling());
                }
                if (bathroom.getRemarks() != null) {
                    existingBathroom.setRemarks(bathroom.getRemarks());
                }

                return existingBathroom;
            })
            .map(bathroomRepository::save)
            .map(savedBathroom -> {
                bathroomSearchRepository.index(savedBathroom);
                return savedBathroom;
            });
    }

    /**
     * Get all the bathrooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Bathroom> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bathrooms");
        return bathroomRepository.findAll(pageable);
    }

    /**
     * Get one bathroom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Bathroom> findOne(Long id) {
        LOG.debug("Request to get Bathroom : {}", id);
        return bathroomRepository.findById(id);
    }

    /**
     * Delete the bathroom by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Bathroom : {}", id);
        bathroomRepository.deleteById(id);
        bathroomSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the bathroom corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Bathroom> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Bathrooms for query {}", query);
        return bathroomSearchRepository.search(query, pageable);
    }
}
