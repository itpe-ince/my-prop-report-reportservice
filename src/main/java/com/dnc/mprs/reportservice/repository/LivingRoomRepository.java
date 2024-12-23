package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.LivingRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the LivingRoom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LivingRoomRepository extends ReactiveCrudRepository<LivingRoom, Long>, LivingRoomRepositoryInternal {
    Flux<LivingRoom> findAllBy(Pageable pageable);

    @Query("SELECT * FROM living_room entity WHERE entity.report_id = :id")
    Flux<LivingRoom> findByReport(Long id);

    @Query("SELECT * FROM living_room entity WHERE entity.report_id IS NULL")
    Flux<LivingRoom> findAllWhereReportIsNull();

    @Override
    <S extends LivingRoom> Mono<S> save(S entity);

    @Override
    Flux<LivingRoom> findAll();

    @Override
    Mono<LivingRoom> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface LivingRoomRepositoryInternal {
    <S extends LivingRoom> Mono<S> save(S entity);

    Flux<LivingRoom> findAllBy(Pageable pageable);

    Flux<LivingRoom> findAll();

    Mono<LivingRoom> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<LivingRoom> findAllBy(Pageable pageable, Criteria criteria);
}
