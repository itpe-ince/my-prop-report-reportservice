package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.EnvFactor;
import com.dnc.mprs.reportservice.repository.EnvFactorRepository;
import com.dnc.mprs.reportservice.service.EnvFactorService;
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
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.EnvFactor}.
 */
@RestController
@RequestMapping("/api/env-factors")
public class EnvFactorResource {

    private static final Logger LOG = LoggerFactory.getLogger(EnvFactorResource.class);

    private static final String ENTITY_NAME = "reportserviceEnvFactor";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnvFactorService envFactorService;

    private final EnvFactorRepository envFactorRepository;

    public EnvFactorResource(EnvFactorService envFactorService, EnvFactorRepository envFactorRepository) {
        this.envFactorService = envFactorService;
        this.envFactorRepository = envFactorRepository;
    }

    /**
     * {@code POST  /env-factors} : Create a new envFactor.
     *
     * @param envFactor the envFactor to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new envFactor, or with status {@code 400 (Bad Request)} if the envFactor has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EnvFactor> createEnvFactor(@Valid @RequestBody EnvFactor envFactor) throws URISyntaxException {
        LOG.debug("REST request to save EnvFactor : {}", envFactor);
        if (envFactor.getId() != null) {
            throw new BadRequestAlertException("A new envFactor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        envFactor = envFactorService.save(envFactor);
        return ResponseEntity.created(new URI("/api/env-factors/" + envFactor.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, envFactor.getId().toString()))
            .body(envFactor);
    }

    /**
     * {@code PUT  /env-factors/:id} : Updates an existing envFactor.
     *
     * @param id the id of the envFactor to save.
     * @param envFactor the envFactor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envFactor,
     * or with status {@code 400 (Bad Request)} if the envFactor is not valid,
     * or with status {@code 500 (Internal Server Error)} if the envFactor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EnvFactor> updateEnvFactor(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EnvFactor envFactor
    ) throws URISyntaxException {
        LOG.debug("REST request to update EnvFactor : {}, {}", id, envFactor);
        if (envFactor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, envFactor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!envFactorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        envFactor = envFactorService.update(envFactor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, envFactor.getId().toString()))
            .body(envFactor);
    }

    /**
     * {@code PATCH  /env-factors/:id} : Partial updates given fields of an existing envFactor, field will ignore if it is null
     *
     * @param id the id of the envFactor to save.
     * @param envFactor the envFactor to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated envFactor,
     * or with status {@code 400 (Bad Request)} if the envFactor is not valid,
     * or with status {@code 404 (Not Found)} if the envFactor is not found,
     * or with status {@code 500 (Internal Server Error)} if the envFactor couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EnvFactor> partialUpdateEnvFactor(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EnvFactor envFactor
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EnvFactor partially : {}, {}", id, envFactor);
        if (envFactor.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, envFactor.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!envFactorRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EnvFactor> result = envFactorService.partialUpdate(envFactor);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, envFactor.getId().toString())
        );
    }

    /**
     * {@code GET  /env-factors} : get all the envFactors.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of envFactors in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EnvFactor>> getAllEnvFactors(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of EnvFactors");
        Page<EnvFactor> page = envFactorService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /env-factors/:id} : get the "id" envFactor.
     *
     * @param id the id of the envFactor to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the envFactor, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnvFactor> getEnvFactor(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EnvFactor : {}", id);
        Optional<EnvFactor> envFactor = envFactorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(envFactor);
    }

    /**
     * {@code DELETE  /env-factors/:id} : delete the "id" envFactor.
     *
     * @param id the id of the envFactor to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvFactor(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EnvFactor : {}", id);
        envFactorService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /env-factors/_search?query=:query} : search for the envFactor corresponding
     * to the query.
     *
     * @param query the query of the envFactor search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<EnvFactor>> searchEnvFactors(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of EnvFactors for query {}", query);
        try {
            Page<EnvFactor> page = envFactorService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
