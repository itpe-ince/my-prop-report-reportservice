package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.repository.BathroomRepository;
import com.dnc.mprs.reportservice.service.BathroomService;
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
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.Bathroom}.
 */
@RestController
@RequestMapping("/api/bathrooms")
public class BathroomResource {

    private static final Logger LOG = LoggerFactory.getLogger(BathroomResource.class);

    private static final String ENTITY_NAME = "reportserviceBathroom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BathroomService bathroomService;

    private final BathroomRepository bathroomRepository;

    public BathroomResource(BathroomService bathroomService, BathroomRepository bathroomRepository) {
        this.bathroomService = bathroomService;
        this.bathroomRepository = bathroomRepository;
    }

    /**
     * {@code POST  /bathrooms} : Create a new bathroom.
     *
     * @param bathroom the bathroom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new bathroom, or with status {@code 400 (Bad Request)} if the bathroom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Bathroom> createBathroom(@Valid @RequestBody Bathroom bathroom) throws URISyntaxException {
        LOG.debug("REST request to save Bathroom : {}", bathroom);
        if (bathroom.getId() != null) {
            throw new BadRequestAlertException("A new bathroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bathroom = bathroomService.save(bathroom);
        return ResponseEntity.created(new URI("/api/bathrooms/" + bathroom.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bathroom.getId().toString()))
            .body(bathroom);
    }

    /**
     * {@code PUT  /bathrooms/:id} : Updates an existing bathroom.
     *
     * @param id the id of the bathroom to save.
     * @param bathroom the bathroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bathroom,
     * or with status {@code 400 (Bad Request)} if the bathroom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bathroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Bathroom> updateBathroom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Bathroom bathroom
    ) throws URISyntaxException {
        LOG.debug("REST request to update Bathroom : {}, {}", id, bathroom);
        if (bathroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bathroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bathroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        bathroom = bathroomService.update(bathroom);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bathroom.getId().toString()))
            .body(bathroom);
    }

    /**
     * {@code PATCH  /bathrooms/:id} : Partial updates given fields of an existing bathroom, field will ignore if it is null
     *
     * @param id the id of the bathroom to save.
     * @param bathroom the bathroom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bathroom,
     * or with status {@code 400 (Bad Request)} if the bathroom is not valid,
     * or with status {@code 404 (Not Found)} if the bathroom is not found,
     * or with status {@code 500 (Internal Server Error)} if the bathroom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Bathroom> partialUpdateBathroom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Bathroom bathroom
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Bathroom partially : {}, {}", id, bathroom);
        if (bathroom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, bathroom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bathroomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Bathroom> result = bathroomService.partialUpdate(bathroom);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bathroom.getId().toString())
        );
    }

    /**
     * {@code GET  /bathrooms} : get all the bathrooms.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bathrooms in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Bathroom>> getAllBathrooms(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Bathrooms");
        Page<Bathroom> page = bathroomService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /bathrooms/:id} : get the "id" bathroom.
     *
     * @param id the id of the bathroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bathroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Bathroom> getBathroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bathroom : {}", id);
        Optional<Bathroom> bathroom = bathroomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bathroom);
    }

    /**
     * {@code DELETE  /bathrooms/:id} : delete the "id" bathroom.
     *
     * @param id the id of the bathroom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBathroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bathroom : {}", id);
        bathroomService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /bathrooms/_search?query=:query} : search for the bathroom corresponding
     * to the query.
     *
     * @param query the query of the bathroom search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<Bathroom>> searchBathrooms(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Bathrooms for query {}", query);
        try {
            Page<Bathroom> page = bathroomService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
