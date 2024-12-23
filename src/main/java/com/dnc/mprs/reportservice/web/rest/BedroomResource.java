package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.repository.BedroomRepository;
import com.dnc.mprs.reportservice.service.BedroomService;
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
    public Mono<ResponseEntity<Bedroom>> createBedroom(@Valid @RequestBody Bedroom bedroom) throws URISyntaxException {
        LOG.debug("REST request to save Bedroom : {}", bedroom);
        if (bedroom.getId() != null) {
            throw new BadRequestAlertException("A new bedroom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return bedroomService
            .save(bedroom)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/bedrooms/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
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
    public Mono<ResponseEntity<Bedroom>> updateBedroom(
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

        return bedroomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return bedroomService
                    .update(bedroom)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
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
    public Mono<ResponseEntity<Bedroom>> partialUpdateBedroom(
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

        return bedroomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Bedroom> result = bedroomService.partialUpdate(bedroom);

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
     * {@code GET  /bedrooms} : get all the bedrooms.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bedrooms in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Bedroom>>> getAllBedrooms(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of Bedrooms");
        return bedroomService
            .countAll()
            .zipWith(bedroomService.findAll(pageable).collectList())
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
     * {@code GET  /bedrooms/:id} : get the "id" bedroom.
     *
     * @param id the id of the bedroom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bedroom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Bedroom>> getBedroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Bedroom : {}", id);
        Mono<Bedroom> bedroom = bedroomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bedroom);
    }

    /**
     * {@code DELETE  /bedrooms/:id} : delete the "id" bedroom.
     *
     * @param id the id of the bedroom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBedroom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Bedroom : {}", id);
        return bedroomService
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
     * {@code SEARCH  /bedrooms/_search?query=:query} : search for the bedroom corresponding
     * to the query.
     *
     * @param query the query of the bedroom search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<Bedroom>>> searchBedrooms(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of Bedrooms for query {}", query);
        return bedroomService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(bedroomService.search(query, pageable)));
    }
}
