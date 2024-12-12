package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.repository.InfrastructureRepository;
import com.dnc.mprs.reportservice.service.InfrastructureService;
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
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.Infrastructure}.
 */
@RestController
@RequestMapping("/api/infrastructures")
public class InfrastructureResource {

    private static final Logger LOG = LoggerFactory.getLogger(InfrastructureResource.class);

    private static final String ENTITY_NAME = "reportserviceInfrastructure";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InfrastructureService infrastructureService;

    private final InfrastructureRepository infrastructureRepository;

    public InfrastructureResource(InfrastructureService infrastructureService, InfrastructureRepository infrastructureRepository) {
        this.infrastructureService = infrastructureService;
        this.infrastructureRepository = infrastructureRepository;
    }

    /**
     * {@code POST  /infrastructures} : Create a new infrastructure.
     *
     * @param infrastructure the infrastructure to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new infrastructure, or with status {@code 400 (Bad Request)} if the infrastructure has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Infrastructure> createInfrastructure(@Valid @RequestBody Infrastructure infrastructure)
        throws URISyntaxException {
        LOG.debug("REST request to save Infrastructure : {}", infrastructure);
        if (infrastructure.getId() != null) {
            throw new BadRequestAlertException("A new infrastructure cannot already have an ID", ENTITY_NAME, "idexists");
        }
        infrastructure = infrastructureService.save(infrastructure);
        return ResponseEntity.created(new URI("/api/infrastructures/" + infrastructure.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, infrastructure.getId().toString()))
            .body(infrastructure);
    }

    /**
     * {@code PUT  /infrastructures/:id} : Updates an existing infrastructure.
     *
     * @param id the id of the infrastructure to save.
     * @param infrastructure the infrastructure to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated infrastructure,
     * or with status {@code 400 (Bad Request)} if the infrastructure is not valid,
     * or with status {@code 500 (Internal Server Error)} if the infrastructure couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Infrastructure> updateInfrastructure(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Infrastructure infrastructure
    ) throws URISyntaxException {
        LOG.debug("REST request to update Infrastructure : {}, {}", id, infrastructure);
        if (infrastructure.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, infrastructure.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!infrastructureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        infrastructure = infrastructureService.update(infrastructure);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, infrastructure.getId().toString()))
            .body(infrastructure);
    }

    /**
     * {@code PATCH  /infrastructures/:id} : Partial updates given fields of an existing infrastructure, field will ignore if it is null
     *
     * @param id the id of the infrastructure to save.
     * @param infrastructure the infrastructure to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated infrastructure,
     * or with status {@code 400 (Bad Request)} if the infrastructure is not valid,
     * or with status {@code 404 (Not Found)} if the infrastructure is not found,
     * or with status {@code 500 (Internal Server Error)} if the infrastructure couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Infrastructure> partialUpdateInfrastructure(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Infrastructure infrastructure
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Infrastructure partially : {}, {}", id, infrastructure);
        if (infrastructure.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, infrastructure.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!infrastructureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Infrastructure> result = infrastructureService.partialUpdate(infrastructure);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, infrastructure.getId().toString())
        );
    }

    /**
     * {@code GET  /infrastructures} : get all the infrastructures.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of infrastructures in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Infrastructure>> getAllInfrastructures(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Infrastructures");
        Page<Infrastructure> page = infrastructureService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /infrastructures/:id} : get the "id" infrastructure.
     *
     * @param id the id of the infrastructure to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the infrastructure, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Infrastructure> getInfrastructure(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Infrastructure : {}", id);
        Optional<Infrastructure> infrastructure = infrastructureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(infrastructure);
    }

    /**
     * {@code DELETE  /infrastructures/:id} : delete the "id" infrastructure.
     *
     * @param id the id of the infrastructure to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInfrastructure(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Infrastructure : {}", id);
        infrastructureService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /infrastructures/_search?query=:query} : search for the infrastructure corresponding
     * to the query.
     *
     * @param query the query of the infrastructure search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<Infrastructure>> searchInfrastructures(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Infrastructures for query {}", query);
        try {
            Page<Infrastructure> page = infrastructureService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
