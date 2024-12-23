package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Kitchen;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Kitchen entity.
 */
@SuppressWarnings("unused")
@Repository
public interface KitchenRepository extends ReactiveCrudRepository<Kitchen, Long>, KitchenRepositoryInternal {
    Flux<Kitchen> findAllBy(Pageable pageable);

    @Query("SELECT * FROM kitchen entity WHERE entity.report_id = :id")
    Flux<Kitchen> findByReport(Long id);

    @Query("SELECT * FROM kitchen entity WHERE entity.report_id IS NULL")
    Flux<Kitchen> findAllWhereReportIsNull();

    @Override
    <S extends Kitchen> Mono<S> save(S entity);

    @Override
    Flux<Kitchen> findAll();

    @Override
    Mono<Kitchen> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface KitchenRepositoryInternal {
    <S extends Kitchen> Mono<S> save(S entity);

    Flux<Kitchen> findAllBy(Pageable pageable);

    Flux<Kitchen> findAll();

    Mono<Kitchen> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Kitchen> findAllBy(Pageable pageable, Criteria criteria);
}
