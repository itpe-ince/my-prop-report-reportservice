package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import com.dnc.mprs.reportservice.repository.InfrastructureRepository;
import com.dnc.mprs.reportservice.service.InfrastructureService;
import com.dnc.mprs.reportservice.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

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
    public Mono<ResponseEntity<Infrastructure>> createInfrastructure(@Valid @RequestBody Infrastructure infrastructure)
        throws URISyntaxException {
        LOG.debug("REST request to save Infrastructure : {}", infrastructure);
        if (infrastructure.getId() != null) {
            throw new BadRequestAlertException("A new infrastructure cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return infrastructureService
            .save(infrastructure)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/infrastructures/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<Infrastructure>> updateInfrastructure(
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

        return infrastructureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return infrastructureService
                    .update(infrastructure)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
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
    public Mono<ResponseEntity<Infrastructure>> partialUpdateInfrastructure(
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

        return infrastructureRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Infrastructure> result = infrastructureService.partialUpdate(infrastructure);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /infrastructures} : get all the infrastructures.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of infrastructures in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Infrastructure>>> getAllInfrastructures(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Infrastructures");
        return infrastructureService
            .countAll()
            .zipWith(infrastructureService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /infrastructures/:id} : get the "id" infrastructure.
     *
     * @param id the id of the infrastructure to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the infrastructure, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Infrastructure>> getInfrastructure(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Infrastructure : {}", id);
        Mono<Infrastructure> infrastructure = infrastructureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(infrastructure);
    }

    /**
     * {@code DELETE  /infrastructures/:id} : delete the "id" infrastructure.
     *
     * @param id the id of the infrastructure to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteInfrastructure(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Infrastructure : {}", id);
        return infrastructureService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code SEARCH  /infrastructures/_search?query=:query} : search for the infrastructure corresponding
     * to the query.
     *
     * @param query the query of the infrastructure search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<Infrastructure>>> searchInfrastructures(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of Infrastructures for query {}", query);
        return infrastructureService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(infrastructureService.search(query, pageable)));
    }
}
