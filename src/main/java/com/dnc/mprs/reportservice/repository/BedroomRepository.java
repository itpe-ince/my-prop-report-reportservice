package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Bedroom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Bedroom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BedroomRepository extends ReactiveCrudRepository<Bedroom, Long>, BedroomRepositoryInternal {
    Flux<Bedroom> findAllBy(Pageable pageable);

    @Query("SELECT * FROM bedroom entity WHERE entity.report_id = :id")
    Flux<Bedroom> findByReport(Long id);

    @Query("SELECT * FROM bedroom entity WHERE entity.report_id IS NULL")
    Flux<Bedroom> findAllWhereReportIsNull();

    @Override
    <S extends Bedroom> Mono<S> save(S entity);

    @Override
    Flux<Bedroom> findAll();

    @Override
    Mono<Bedroom> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BedroomRepositoryInternal {
    <S extends Bedroom> Mono<S> save(S entity);

    Flux<Bedroom> findAllBy(Pageable pageable);

    Flux<Bedroom> findAll();

    Mono<Bedroom> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Bedroom> findAllBy(Pageable pageable, Criteria criteria);
}
