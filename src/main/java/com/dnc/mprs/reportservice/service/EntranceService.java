package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.repository.EntranceRepository;
import com.dnc.mprs.reportservice.repository.search.EntranceSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Entrance}.
 */
@Service
@Transactional
public class EntranceService {

    private static final Logger LOG = LoggerFactory.getLogger(EntranceService.class);

    private final EntranceRepository entranceRepository;

    private final EntranceSearchRepository entranceSearchRepository;

    public EntranceService(EntranceRepository entranceRepository, EntranceSearchRepository entranceSearchRepository) {
        this.entranceRepository = entranceRepository;
        this.entranceSearchRepository = entranceSearchRepository;
    }

    /**
     * Save a entrance.
     *
     * @param entrance the entity to save.
     * @return the persisted entity.
     */
    public Entrance save(Entrance entrance) {
        LOG.debug("Request to save Entrance : {}", entrance);
        entrance = entranceRepository.save(entrance);
        entranceSearchRepository.index(entrance);
        return entrance;
    }

    /**
     * Update a entrance.
     *
     * @param entrance the entity to save.
     * @return the persisted entity.
     */
    public Entrance update(Entrance entrance) {
        LOG.debug("Request to update Entrance : {}", entrance);
        entrance = entranceRepository.save(entrance);
        entranceSearchRepository.index(entrance);
        return entrance;
    }

    /**
     * Partially update a entrance.
     *
     * @param entrance the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Entrance> partialUpdate(Entrance entrance) {
        LOG.debug("Request to partially update Entrance : {}", entrance);

        return entranceRepository
            .findById(entrance.getId())
            .map(existingEntrance -> {
                if (entrance.getReportId() != null) {
                    existingEntrance.setReportId(entrance.getReportId());
                }
                if (entrance.getEntranceName() != null) {
                    existingEntrance.setEntranceName(entrance.getEntranceName());
                }
                if (entrance.getCondtionLevel() != null) {
                    existingEntrance.setCondtionLevel(entrance.getCondtionLevel());
                }
                if (entrance.getEntranceSize() != null) {
                    existingEntrance.setEntranceSize(entrance.getEntranceSize());
                }
                if (entrance.getShoeRackSize() != null) {
                    existingEntrance.setShoeRackSize(entrance.getShoeRackSize());
                }
                if (entrance.getPantryPresence() != null) {
                    existingEntrance.setPantryPresence(entrance.getPantryPresence());
                }
                if (entrance.getRemarks() != null) {
                    existingEntrance.setRemarks(entrance.getRemarks());
                }

                return existingEntrance;
            })
            .map(entranceRepository::save)
            .map(savedEntrance -> {
                entranceSearchRepository.index(savedEntrance);
                return savedEntrance;
            });
    }

    /**
     * Get all the entrances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Entrance> findAll(Pageable pageable) {
        LOG.debug("Request to get all Entrances");
        return entranceRepository.findAll(pageable);
    }

    /**
     * Get one entrance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Entrance> findOne(Long id) {
        LOG.debug("Request to get Entrance : {}", id);
        return entranceRepository.findById(id);
    }

    /**
     * Delete the entrance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Entrance : {}", id);
        entranceRepository.deleteById(id);
        entranceSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the entrance corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Entrance> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Entrances for query {}", query);
        return entranceSearchRepository.search(query, pageable);
    }
}
