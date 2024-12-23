package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
import com.dnc.mprs.reportservice.service.KitchenService;
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
    public Mono<ResponseEntity<Kitchen>> createKitchen(@Valid @RequestBody Kitchen kitchen) throws URISyntaxException {
        LOG.debug("REST request to save Kitchen : {}", kitchen);
        if (kitchen.getId() != null) {
            throw new BadRequestAlertException("A new kitchen cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return kitchenService
            .save(kitchen)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/kitchens/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<Kitchen>> updateKitchen(
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

        return kitchenRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return kitchenService
                    .update(kitchen)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
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
    public Mono<ResponseEntity<Kitchen>> partialUpdateKitchen(
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

        return kitchenRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Kitchen> result = kitchenService.partialUpdate(kitchen);

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
     * {@code GET  /kitchens} : get all the kitchens.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of kitchens in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Kitchen>>> getAllKitchens(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Kitchens");
        return kitchenService
            .countAll()
            .zipWith(kitchenService.findAll(pageable).collectList())
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
     * {@code GET  /kitchens/:id} : get the "id" kitchen.
     *
     * @param id the id of the kitchen to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the kitchen, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Kitchen>> getKitchen(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Kitchen : {}", id);
        Mono<Kitchen> kitchen = kitchenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(kitchen);
    }

    /**
     * {@code DELETE  /kitchens/:id} : delete the "id" kitchen.
     *
     * @param id the id of the kitchen to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteKitchen(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Kitchen : {}", id);
        return kitchenService
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
     * {@code SEARCH  /kitchens/_search?query=:query} : search for the kitchen corresponding
     * to the query.
     *
     * @param query the query of the kitchen search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<Kitchen>>> searchKitchens(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of Kitchens for query {}", query);
        return kitchenService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(kitchenService.search(query, pageable)));
    }
}
