package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.LivingRoom;
import com.dnc.mprs.reportservice.repository.LivingRoomRepository;
import com.dnc.mprs.reportservice.repository.search.LivingRoomSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public LivingRoom save(LivingRoom livingRoom) {
        LOG.debug("Request to save LivingRoom : {}", livingRoom);
        livingRoom = livingRoomRepository.save(livingRoom);
        livingRoomSearchRepository.index(livingRoom);
        return livingRoom;
    }

    /**
     * Update a livingRoom.
     *
     * @param livingRoom the entity to save.
     * @return the persisted entity.
     */
    public LivingRoom update(LivingRoom livingRoom) {
        LOG.debug("Request to update LivingRoom : {}", livingRoom);
        livingRoom = livingRoomRepository.save(livingRoom);
        livingRoomSearchRepository.index(livingRoom);
        return livingRoom;
    }

    /**
     * Partially update a livingRoom.
     *
     * @param livingRoom the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LivingRoom> partialUpdate(LivingRoom livingRoom) {
        LOG.debug("Request to partially update LivingRoom : {}", livingRoom);

        return livingRoomRepository
            .findById(livingRoom.getId())
            .map(existingLivingRoom -> {
                if (livingRoom.getReportId() != null) {
                    existingLivingRoom.setReportId(livingRoom.getReportId());
                }
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
            .map(livingRoomRepository::save)
            .map(savedLivingRoom -> {
                livingRoomSearchRepository.index(savedLivingRoom);
                return savedLivingRoom;
            });
    }

    /**
     * Get all the livingRooms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LivingRoom> findAll(Pageable pageable) {
        LOG.debug("Request to get all LivingRooms");
        return livingRoomRepository.findAll(pageable);
    }

    /**
     * Get one livingRoom by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LivingRoom> findOne(Long id) {
        LOG.debug("Request to get LivingRoom : {}", id);
        return livingRoomRepository.findById(id);
    }

    /**
     * Delete the livingRoom by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LivingRoom : {}", id);
        livingRoomRepository.deleteById(id);
        livingRoomSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the livingRoom corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LivingRoom> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of LivingRooms for query {}", query);
        return livingRoomSearchRepository.search(query, pageable);
    }
}
