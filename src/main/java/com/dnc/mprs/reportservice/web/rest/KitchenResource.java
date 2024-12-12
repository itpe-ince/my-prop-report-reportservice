package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
import com.dnc.mprs.reportservice.service.KitchenService;
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
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.Kitchen}.
 */
@RestController
@RequestMapping("/api/kitchens")
public class KitchenResource {

    private static final Logger LOG = LoggerFactory.getLogger(KitchenResource.class);

    private static final String ENTITY_NAME = "reportserviceKitchen";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KitchenService kitchenService;

    private final KitchenRepository kitchenRepository;

    public KitchenResource(KitchenService kitchenService, KitchenRepository kitchenRepository) {
        this.kitchenService = kitchenService;
        this.kitchenRepository = kitchenRepository;
    }

    /**
     * {@code POST  /kitchens} : Create a new kitchen.
     *
     * @param kitchen the kitchen to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new kitchen, or with status {@code 400 (Bad Request)} if the kitchen has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Kitchen> createKitchen(@Valid @RequestBody Kitchen kitchen) throws URISyntaxException {
        LOG.debug("REST request to save Kitchen : {}", kitchen);
        if (kitchen.getId() != null) {
            throw new BadRequestAlertException("A new kitchen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        kitchen = kitchenService.save(kitchen);
        return ResponseEntity.created(new URI("/api/kitchens/" + kitchen.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, kitchen.getId().toString()))
            .body(kitchen);
    }

    /**
     * {@code PUT  /kitchens/:id} : Updates an existing kitchen.
     *
     * @param id the id of the kitchen to save.
     * @param kitchen the kitchen to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kitchen,
     * or with status {@code 400 (Bad Request)} if the kitchen is not valid,
     * or with status {@code 500 (Internal Server Error)} if the kitchen couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Kitchen> updateKitchen(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Kitchen kitchen
    ) throws URISyntaxException {
        LOG.debug("REST request to update Kitchen : {}, {}", id, kitchen);
        if (kitchen.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kitchen.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!kitchenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        kitchen = kitchenService.update(kitchen);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, kitchen.getId().toString()))
            .body(kitchen);
    }

    /**
     * {@code PATCH  /kitchens/:id} : Partial updates given fields of an existing kitchen, field will ignore if it is null
     *
     * @param id the id of the kitchen to save.
     * @param kitchen the kitchen to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated kitchen,
     * or with status {@code 400 (Bad Request)} if the kitchen is not valid,
     * or with status {@code 404 (Not Found)} if the kitchen is not found,
     * or with status {@code 500 (Internal Server Error)} if the kitchen couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Kitchen> partialUpdateKitchen(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Kitchen kitchen
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Kitchen partially : {}, {}", id, kitchen);
        if (kitchen.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, kitchen.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!kitchenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Kitchen> result = kitchenService.partialUpdate(kitchen);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, kitchen.getId().toString())
        );
    }

    /**
     * {@code GET  /kitchens} : get all the kitchens.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of kitchens in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Kitchen>> getAllKitchens(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Kitchens");
        Page<Kitchen> page = kitchenService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /kitchens/:id} : get the "id" kitchen.
     *
     * @param id the id of the kitchen to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the kitchen, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Kitchen> getKitchen(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Kitchen : {}", id);
        Optional<Kitchen> kitchen = kitchenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(kitchen);
    }

    /**
     * {@code DELETE  /kitchens/:id} : delete the "id" kitchen.
     *
     * @param id the id of the kitchen to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKitchen(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Kitchen : {}", id);
        kitchenService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /kitchens/_search?query=:query} : search for the kitchen corresponding
     * to the query.
     *
     * @param query the query of the kitchen search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<Kitchen>> searchKitchens(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Kitchens for query {}", query);
        try {
            Page<Kitchen> page = kitchenService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
