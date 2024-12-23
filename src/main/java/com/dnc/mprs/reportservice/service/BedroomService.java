package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.repository.BedroomRepository;
import com.dnc.mprs.reportservice.repository.search.BedroomSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Bedroom}.
 */
@Service
@Transactional
public class BedroomService {

    private static final Logger LOG = LoggerFactory.getLogger(BedroomService.class);

    private final BedroomRepository bedroomRepository;

    private final BedroomSearchRepository bedroomSearchRepository;

    public BedroomService(BedroomRepository bedroomRepository, BedroomSearchRepository bedroomSearchRepository) {
        this.bedroomRepository = bedroomRepository;
        this.bedroomSearchRepository = bedroomSearchRepository;
    }

    /**
     * Save a bedroom.
     *
     * @param bedroom the entity to save.
     * @return the persisted entity.
     */
    public Mono<Bedroom> save(Bedroom bedroom) {
        LOG.debug("Request to save Bedroom : {}", bedroom);
        return bedroomRepository.save(bedroom).flatMap(bedroomSearchRepository::save);
    }

    /**
     * Update a bedroom.
     *
     * @param bedroom the entity to save.
     * @return the persisted entity.
     */
    public Mono<Bedroom> update(Bedroom bedroom) {
        LOG.debug("Request to update Bedroom : {}", bedroom);
        return bedroomRepository.save(bedroom).flatMap(bedroomSearchRepository::save);
    }

    /**
     * Partially update a bedroom.
     *
     * @param bedroom the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Bedroom> partialUpdate(Bedroom bedroom) {
        LOG.debug("Request to partially update Bedroom : {}", bedroom);

        return bedroomRepository
            .findById(bedroom.getId())
            .map(existingBedroom -> {
                if (bedroom.getBedroomName() != null) {
                    existingBedroom.setBedroomName(bedroom.getBedroomName());
                }
                if (bedroom.getConditionLevel() != null) {
                    existingBedroom.setConditionLevel(bedroom.getConditionLevel());
                }
                if (bedroom.getRoomSize() != null) {
                    existingBedroom.setRoomSize(bedroom.getRoomSize());
                }
                if (bedroom.getClosetYn() != null) {
                    existingBedroom.setClosetYn(bedroom.getClosetYn());
                }
                if (bedroom.getAcYn() != null) {
                    existingBedroom.setAcYn(bedroom.getAcYn());
                }
                if (bedroom.getWindowLocation() != null) {
                    existingBedroom.setWindowLocation(bedroom.getWindowLocation());
                }
                if (bedroom.getWindowSize() != null) {
                    existingBedroom.setWindowSize(bedroom.getWindowSize());
                }
                if (bedroom.getRemarks() != null) {
                    existingBedroom.setRemarks(bedroom.getRemarks());
                }

                return existingBedroom;
            })
            .flatMap(bedroomRepository::save)
            .flatMap(savedBedroom -> {
                bedroomSearchRepository.save(savedBedroom);
                return Mono.just(savedBedroom);
            });
    }

    /**
     * Get all the bedrooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Bedroom> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bedrooms");
        return bedroomRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of bedrooms available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return bedroomRepository.count();
    }

    /**
     * Returns the number of bedrooms available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return bedroomSearchRepository.count();
    }

    /**
     * Get one bedroom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Bedroom> findOne(Long id) {
        LOG.debug("Request to get Bedroom : {}", id);
        return bedroomRepository.findById(id);
    }

    /**
     * Delete the bedroom by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Bedroom : {}", id);
        return bedroomRepository.deleteById(id).then(bedroomSearchRepository.deleteById(id));
    }

    /**
     * Search for the bedroom corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Bedroom> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Bedrooms for query {}", query);
        return bedroomSearchRepository.search(query, pageable);
    }
}
