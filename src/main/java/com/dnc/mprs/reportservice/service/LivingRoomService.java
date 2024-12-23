package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.LivingRoom;
import com.dnc.mprs.reportservice.repository.LivingRoomRepository;
import com.dnc.mprs.reportservice.repository.search.LivingRoomSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.LivingRoom}.
 */
@Service
@Transactional
public class LivingRoomService {

    private static final Logger LOG = LoggerFactory.getLogger(LivingRoomService.class);

    private final LivingRoomRepository livingRoomRepository;

    private final LivingRoomSearchRepository livingRoomSearchRepository;

    public LivingRoomService(LivingRoomRepository livingRoomRepository, LivingRoomSearchRepository livingRoomSearchRepository) {
        this.livingRoomRepository = livingRoomRepository;
        this.livingRoomSearchRepository = livingRoomSearchRepository;
    }

    /**
     * Save a livingRoom.
     *
     * @param livingRoom the entity to save.
     * @return the persisted entity.
     */
    public Mono<LivingRoom> save(LivingRoom livingRoom) {
        LOG.debug("Request to save LivingRoom : {}", livingRoom);
        return livingRoomRepository.save(livingRoom).flatMap(livingRoomSearchRepository::save);
    }

    /**
     * Update a livingRoom.
     *
     * @param livingRoom the entity to save.
     * @return the persisted entity.
     */
    public Mono<LivingRoom> update(LivingRoom livingRoom) {
        LOG.debug("Request to update LivingRoom : {}", livingRoom);
        return livingRoomRepository.save(livingRoom).flatMap(livingRoomSearchRepository::save);
    }

    /**
     * Partially update a livingRoom.
     *
     * @param livingRoom the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<LivingRoom> partialUpdate(LivingRoom livingRoom) {
        LOG.debug("Request to partially update LivingRoom : {}", livingRoom);

        return livingRoomRepository
            .findById(livingRoom.getId())
            .map(existingLivingRoom -> {
                if (livingRoom.getLivingRoomName() != null) {
                    existingLivingRoom.setLivingRoomName(livingRoom.getLivingRoomName());
                }
                if (livingRoom.getConditionLevel() != null) {
                    existingLivingRoom.setConditionLevel(livingRoom.getConditionLevel());
                }
                if (livingRoom.getRoomSize() != null) {
                    existingLivingRoom.setRoomSize(livingRoom.getRoomSize());
                }
                if (livingRoom.getWallState() != null) {
                    existingLivingRoom.setWallState(livingRoom.getWallState());
                }
                if (livingRoom.getFloorMaterial() != null) {
                    existingLivingRoom.setFloorMaterial(livingRoom.getFloorMaterial());
                }
                if (livingRoom.getSunlight() != null) {
                    existingLivingRoom.setSunlight(livingRoom.getSunlight());
                }
                if (livingRoom.getRemarks() != null) {
                    existingLivingRoom.setRemarks(livingRoom.getRemarks());
                }

                return existingLivingRoom;
            })
            .flatMap(livingRoomRepository::save)
            .flatMap(savedLivingRoom -> {
                livingRoomSearchRepository.save(savedLivingRoom);
                return Mono.just(savedLivingRoom);
            });
    }

    /**
     * Get all the livingRooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<LivingRoom> findAll(Pageable pageable) {
        LOG.debug("Request to get all LivingRooms");
        return livingRoomRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of livingRooms available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return livingRoomRepository.count();
    }

    /**
     * Returns the number of livingRooms available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return livingRoomSearchRepository.count();
    }

    /**
     * Get one livingRoom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<LivingRoom> findOne(Long id) {
        LOG.debug("Request to get LivingRoom : {}", id);
        return livingRoomRepository.findById(id);
    }

    /**
     * Delete the livingRoom by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete LivingRoom : {}", id);
        return livingRoomRepository.deleteById(id).then(livingRoomSearchRepository.deleteById(id));
    }

    /**
     * Search for the livingRoom corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<LivingRoom> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of LivingRooms for query {}", query);
        return livingRoomSearchRepository.search(query, pageable);
    }
}
