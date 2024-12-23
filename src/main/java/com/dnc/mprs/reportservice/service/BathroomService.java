package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.repository.BathroomRepository;
import com.dnc.mprs.reportservice.repository.search.BathroomSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<Bathroom> save(Bathroom bathroom) {
        LOG.debug("Request to save Bathroom : {}", bathroom);
        return bathroomRepository.save(bathroom).flatMap(bathroomSearchRepository::save);
    }

    /**
     * Update a bathroom.
     *
     * @param bathroom the entity to save.
     * @return the persisted entity.
     */
    public Mono<Bathroom> update(Bathroom bathroom) {
        LOG.debug("Request to update Bathroom : {}", bathroom);
        return bathroomRepository.save(bathroom).flatMap(bathroomSearchRepository::save);
    }

    /**
     * Partially update a bathroom.
     *
     * @param bathroom the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Bathroom> partialUpdate(Bathroom bathroom) {
        LOG.debug("Request to partially update Bathroom : {}", bathroom);

        return bathroomRepository
            .findById(bathroom.getId())
            .map(existingBathroom -> {
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
            .flatMap(bathroomRepository::save)
            .flatMap(savedBathroom -> {
                bathroomSearchRepository.save(savedBathroom);
                return Mono.just(savedBathroom);
            });
    }

    /**
     * Get all the bathrooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Bathroom> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bathrooms");
        return bathroomRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of bathrooms available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return bathroomRepository.count();
    }

    /**
     * Returns the number of bathrooms available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return bathroomSearchRepository.count();
    }

    /**
     * Get one bathroom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Bathroom> findOne(Long id) {
        LOG.debug("Request to get Bathroom : {}", id);
        return bathroomRepository.findById(id);
    }

    /**
     * Delete the bathroom by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Bathroom : {}", id);
        return bathroomRepository.deleteById(id).then(bathroomSearchRepository.deleteById(id));
    }

    /**
     * Search for the bathroom corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Bathroom> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Bathrooms for query {}", query);
        return bathroomSearchRepository.search(query, pageable);
    }
}
