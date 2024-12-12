package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.repository.BedroomRepository;
import com.dnc.mprs.reportservice.service.BedroomService;
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
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.Bedroom}.
 */
@RestController
@RequestMapping("/api/bedrooms")
public class BedroomResource {

    private static final Logger LOG = LoggerFactory.getLogger(BedroomResource.class);

    private static final String ENTITY_NAME = "reportserviceBedroom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BedroomService bedroomService;

    private final BedroomRepository bedroomRepository;

    public BedroomResource(BedroomService bedroomService, BedroomRepository bedroomRepository) {
        this.bedroomService = bedroomService;
        this.bedroomRepository = bedroomRepository;
    }

    /**
     * {@code POST  /bedrooms} : Create a new bedroom.
     *
     * @param bedroom the bedroom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bedroom, or with status {@code 400 (Bad Request)} if the bedroom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Bedroom> createBedroom(@Valid @RequestBody Bedroom bedroom) throws URISyntaxException {
        LOG.debug("REST request to save Bedroom : {}", bedroom);
        if (bedroom.getId() != null) {
            throw new BadRequestAlertException("A new bedroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bedroom = bedroomService.save(bedroom);
        return ResponseEntity.created(new URI("/api/bedrooms/" + bedroom.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bedroom.getId().toString()))
            .body(bedroom);
    }

    /**
     * {@code PUT  /bedrooms/:id} : Updates an existing bedroom.
     *
     * @param id the id of the bedroom to save.
     * @param bedroom the bedroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bedroom,
     * or with status {@code 400 (Bad Request)} if the bedroom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bedroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Bedroom> updateBedroom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Bedroom bedroom
    ) throws URISyntaxException {
        LOG.debug("REST request to update Bedroom : {}, {}", id, bedroom);
        if (bedroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bedroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bedroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bedroom = bedroomService.update(bedroom);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bedroom.getId().toString()))
            .body(bedroom);
    }

    /**
     * {@code PATCH  /bedrooms/:id} : Partial updates given fields of an existing bedroom, field will ignore if it is null
     *
     * @param id the id of the bedroom to save.
     * @param bedroom the bedroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bedroom,
     * or with status {@code 400 (Bad Request)} if the bedroom is not valid,
     * or with status {@code 404 (Not Found)} if the bedroom is not found,
     * or with status {@code 500 (Internal Server Error)} if the bedroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bedroom> partialUpdateBedroom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Bedroom bedroom
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Bedroom partially : {}, {}", id, bedroom);
        if (bedroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bedroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bedroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bedroom> result = bedroomService.partialUpdate(bedroom);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bedroom.getId().toString())
        );
    }

    /**
     * {@code GET  /bedrooms} : get all the bedrooms.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bedrooms in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Bedroom>> getAllBedrooms(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Bedrooms");
        Page<Bedroom> page = bedroomService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bedrooms/:id} : get the "id" bedroom.
     *
     * @param id the id of the bedroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bedroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bedroom> getBedroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bedroom : {}", id);
        Optional<Bedroom> bedroom = bedroomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bedroom);
    }

    /**
     * {@code DELETE  /bedrooms/:id} : delete the "id" bedroom.
     *
     * @param id the id of the bedroom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBedroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bedroom : {}", id);
        bedroomService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /bedrooms/_search?query=:query} : search for the bedroom corresponding
     * to the query.
     *
     * @param query the query of the bedroom search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<Bedroom>> searchBedrooms(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Bedrooms for query {}", query);
        try {
            Page<Bedroom> page = bedroomService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
