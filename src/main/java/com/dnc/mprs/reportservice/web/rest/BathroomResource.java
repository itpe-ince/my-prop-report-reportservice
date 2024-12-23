package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.repository.BathroomRepository;
import com.dnc.mprs.reportservice.service.BathroomService;
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
    public Mono<ResponseEntity<Bathroom>> createBathroom(@Valid @RequestBody Bathroom bathroom) throws URISyntaxException {
        LOG.debug("REST request to save Bathroom : {}", bathroom);
        if (bathroom.getId() != null) {
            throw new BadRequestAlertException("A new bathroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return bathroomService
            .save(bathroom)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/bathrooms/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<Bathroom>> updateBathroom(
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

        return bathroomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return bathroomService
                    .update(bathroom)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
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
    public Mono<ResponseEntity<Bathroom>> partialUpdateBathroom(
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

        return bathroomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Bathroom> result = bathroomService.partialUpdate(bathroom);

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
     * {@code GET  /bathrooms} : get all the bathrooms.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bathrooms in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Bathroom>>> getAllBathrooms(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Bathrooms");
        return bathroomService
            .countAll()
            .zipWith(bathroomService.findAll(pageable).collectList())
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
     * {@code GET  /bathrooms/:id} : get the "id" bathroom.
     *
     * @param id the id of the bathroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bathroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Bathroom>> getBathroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bathroom : {}", id);
        Mono<Bathroom> bathroom = bathroomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bathroom);
    }

    /**
     * {@code DELETE  /bathrooms/:id} : delete the "id" bathroom.
     *
     * @param id the id of the bathroom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBathroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bathroom : {}", id);
        return bathroomService
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
     * {@code SEARCH  /bathrooms/_search?query=:query} : search for the bathroom corresponding
     * to the query.
     *
     * @param query the query of the bathroom search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<Bathroom>>> searchBathrooms(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of Bathrooms for query {}", query);
        return bathroomService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(bathroomService.search(query, pageable)));
    }
}
