package com.dnc.mprs.reportservice.web.rest;

import com.dnc.mprs.reportservice.domain.LivingRoom;
import com.dnc.mprs.reportservice.repository.LivingRoomRepository;
import com.dnc.mprs.reportservice.service.LivingRoomService;
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
 * REST controller for managing {@link com.dnc.mprs.reportservice.domain.LivingRoom}.
 */
@RestController
@RequestMapping("/api/living-rooms")
public class LivingRoomResource {

    private static final Logger LOG = LoggerFactory.getLogger(LivingRoomResource.class);

    private static final String ENTITY_NAME = "reportserviceLivingRoom";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LivingRoomService livingRoomService;

    private final LivingRoomRepository livingRoomRepository;

    public LivingRoomResource(LivingRoomService livingRoomService, LivingRoomRepository livingRoomRepository) {
        this.livingRoomService = livingRoomService;
        this.livingRoomRepository = livingRoomRepository;
    }

    /**
     * {@code POST  /living-rooms} : Create a new livingRoom.
     *
     * @param livingRoom the livingRoom to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livingRoom, or with status {@code 400 (Bad Request)} if the livingRoom has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<LivingRoom>> createLivingRoom(@Valid @RequestBody LivingRoom livingRoom) throws URISyntaxException {
        LOG.debug("REST request to save LivingRoom : {}", livingRoom);
        if (livingRoom.getId() != null) {
            throw new BadRequestAlertException("A new livingRoom cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return livingRoomService
            .save(livingRoom)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/living-rooms/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /living-rooms/:id} : Updates an existing livingRoom.
     *
     * @param id the id of the livingRoom to save.
     * @param livingRoom the livingRoom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livingRoom,
     * or with status {@code 400 (Bad Request)} if the livingRoom is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livingRoom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<LivingRoom>> updateLivingRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LivingRoom livingRoom
    ) throws URISyntaxException {
        LOG.debug("REST request to update LivingRoom : {}, {}", id, livingRoom);
        if (livingRoom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livingRoom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return livingRoomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return livingRoomService
                    .update(livingRoom)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /living-rooms/:id} : Partial updates given fields of an existing livingRoom, field will ignore if it is null
     *
     * @param id the id of the livingRoom to save.
     * @param livingRoom the livingRoom to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livingRoom,
     * or with status {@code 400 (Bad Request)} if the livingRoom is not valid,
     * or with status {@code 404 (Not Found)} if the livingRoom is not found,
     * or with status {@code 500 (Internal Server Error)} if the livingRoom couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<LivingRoom>> partialUpdateLivingRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LivingRoom livingRoom
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LivingRoom partially : {}, {}", id, livingRoom);
        if (livingRoom.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livingRoom.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return livingRoomRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<LivingRoom> result = livingRoomService.partialUpdate(livingRoom);

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
     * {@code GET  /living-rooms} : get all the livingRooms.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of livingRooms in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<LivingRoom>>> getAllLivingRooms(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get a page of LivingRooms");
        return livingRoomService
            .countAll()
            .zipWith(livingRoomService.findAll(pageable).collectList())
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
     * {@code GET  /living-rooms/:id} : get the "id" livingRoom.
     *
     * @param id the id of the livingRoom to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livingRoom, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<LivingRoom>> getLivingRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LivingRoom : {}", id);
        Mono<LivingRoom> livingRoom = livingRoomService.findOne(id);
        return ResponseUtil.wrapOrNotFound(livingRoom);
    }

    /**
     * {@code DELETE  /living-rooms/:id} : delete the "id" livingRoom.
     *
     * @param id the id of the livingRoom to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteLivingRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LivingRoom : {}", id);
        return livingRoomService
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
     * {@code SEARCH  /living-rooms/_search?query=:query} : search for the livingRoom corresponding
     * to the query.
     *
     * @param query the query of the livingRoom search.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public Mono<ResponseEntity<Flux<LivingRoom>>> searchLivingRooms(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to search for a page of LivingRooms for query {}", query);
        return livingRoomService
            .searchCount()
            .map(total -> new PageImpl<>(new ArrayList<>(), pageable, total))
            .map(page ->
                PaginationUtil.generatePaginationHttpHeaders(
                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                    page
                )
            )
            .map(headers -> ResponseEntity.ok().headers(headers).body(livingRoomService.search(query, pageable)));
    }
}
