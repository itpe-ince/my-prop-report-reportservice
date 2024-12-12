package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.repository.EntranceRepository;
import com.dnc.mprs.reportservice.service.EntranceService;
import com.dnc.mprs.reportservice.web.rest.errors.BadRequestAlertException;
import com.dnc.mprs.reportservice.web.rest.errors.ElasticsearchExceptionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.Entrance}.
 */
@RestController
@RequestMapping("/api/entrances")
public class EntranceResource {

    private static final Logger LOG = LoggerFactory.getLogger(EntranceResource.class);

    private static final String ENTITY_NAME = "reportserviceEntrance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntranceService entranceService;

    private final EntranceRepository entranceRepository;

    public EntranceResource(EntranceService entranceService, EntranceRepository entranceRepository) {
        this.entranceService = entranceService;
        this.entranceRepository = entranceRepository;
    }

    /**
     * {@code POST  /entrances} : Create a new entrance.
     *
     * @param entrance the entrance to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entrance, or with status {@code 400 (Bad Request)} if the entrance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Entrance> createEntrance(@Valid @RequestBody Entrance entrance) throws URISyntaxException {
        LOG.debug("REST request to save Entrance : {}", entrance);
        if (entrance.getId() != null) {
            throw new BadRequestAlertException("A new entrance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        entrance = entranceService.save(entrance);
        return ResponseEntity.created(new URI("/api/entrances/" + entrance.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, entrance.getId().toString()))
            .body(entrance);
    }

    /**
     * {@code PUT  /entrances/:id} : Updates an existing entrance.
     *
     * @param id the id of the entrance to save.
     * @param entrance the entrance to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entrance,
     * or with status {@code 400 (Bad Request)} if the entrance is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entrance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Entrance> updateEntrance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Entrance entrance
    ) throws URISyntaxException {
        LOG.debug("REST request to update Entrance : {}, {}", id, entrance);
        if (entrance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entrance.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entranceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        entrance = entranceService.update(entrance);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, entrance.getId().toString()))
            .body(entrance);
    }

    /**
     * {@code PATCH  /entrances/:id} : Partial updates given fields of an existing entrance, field will ignore if it is null
     *
     * @param id the id of the entrance to save.
     * @param entrance the entrance to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entrance,
     * or with status {@code 400 (Bad Request)} if the entrance is not valid,
     * or with status {@code 404 (Not Found)} if the entrance is not found,
     * or with status {@code 500 (Internal Server Error)} if the entrance couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Entrance> partialUpdateEntrance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Entrance entrance
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Entrance partially : {}, {}", id, entrance);
        if (entrance.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entrance.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entranceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Entrance> result = entranceService.partialUpdate(entrance);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, entrance.getId().toString())
        );
    }

    /**
     * {@code GET  /entrances} : get all the entrances.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entrances in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Entrance>> getAllEntrances(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Entrances");
        Page<Entrance> page = entranceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /entrances/:id} : get the "id" entrance.
     *
     * @param id the id of the entrance to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entrance, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Entrance> getEntrance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Entrance : {}", id);
        Optional<Entrance> entrance = entranceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(entrance);
    }

    /**
     * {@code DELETE  /entrances/:id} : delete the "id" entrance.
     *
     * @param id the id of the entrance to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntrance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Entrance : {}", id);
        entranceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /entrances/_search?query=:query} : search for the entrance corresponding
     * to the query.
     *
     * @param query the query of the entrance search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<Entrance>> searchEntrances(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Entrances for query {}", query);
        try {
            Page<Entrance> page = entranceService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
