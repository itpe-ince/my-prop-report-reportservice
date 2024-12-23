package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Entrance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Entrance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntranceRepository extends ReactiveCrudRepository<Entrance, Long>, EntranceRepositoryInternal {
    Flux<Entrance> findAllBy(Pageable pageable);

    @Query("SELECT * FROM entrance entity WHERE entity.report_id = :id")
    Flux<Entrance> findByReport(Long id);

    @Query("SELECT * FROM entrance entity WHERE entity.report_id IS NULL")
    Flux<Entrance> findAllWhereReportIsNull();

    @Override
    <S extends Entrance> Mono<S> save(S entity);

    @Override
    Flux<Entrance> findAll();

    @Override
    Mono<Entrance> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EntranceRepositoryInternal {
    <S extends Entrance> Mono<S> save(S entity);

    Flux<Entrance> findAllBy(Pageable pageable);

    Flux<Entrance> findAll();

    Mono<Entrance> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Entrance> findAllBy(Pageable pageable, Criteria criteria);
}
