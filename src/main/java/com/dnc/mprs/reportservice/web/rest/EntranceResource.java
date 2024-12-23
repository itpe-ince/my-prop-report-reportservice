package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.repository.EntranceRepository;
import com.dnc.mprs.reportservice.service.EntranceService;
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
    public Mono<ResponseEntity<Entrance>> createEntrance(@Valid @RequestBody Entrance entrance) throws URISyntaxException {
        LOG.debug("REST request to save Entrance : {}", entrance);
        if (entrance.getId() != null) {
            throw new BadRequestAlertException("A new entrance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return entranceService
            .save(entrance)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/entrances/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<Entrance>> updateEntrance(
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

        return entranceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return entranceService
                    .update(entrance)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
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
    public Mono<ResponseEntity<Entrance>> partialUpdateEntrance(
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

        return entranceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Entrance> result = entranceService.partialUpdate(entrance);

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
     * {@code GET  /entrances} : get all the entrances.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entrances in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Entrance>>> getAllEntrances(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Entrances");
        return entranceService
            .countAll()
            .zipWith(entranceService.findAll(pageable).collectList())
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
     * {@code GET  /entrances/:id} : get the "id" entrance.
     *
     * @param id the id of the entrance to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entrance, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Entrance>> getEntrance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Entrance : {}", id);
        Mono<Entrance> entrance = entranceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(entrance);
    }

    /**
     * {@code DELETE  /entrances/:id} : delete the "id" entrance.
     *
     * @param id the id of the entrance to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEntrance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Entrance : {}", id);
        return entranceService
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
     * {@code SEARCH  /entrances/_search?query=:query} : search for the entrance corresponding
     * to the query.
     *
     * @param query the query of the entrance search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<Entrance>>> searchEntrances(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of Entrances for query {}", query);
        return entranceService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(entranceService.search(query, pageable)));
    }
}
