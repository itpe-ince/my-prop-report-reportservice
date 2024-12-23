package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Bathroom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Bathroom entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BathroomRepository extends ReactiveCrudRepository<Bathroom, Long>, BathroomRepositoryInternal {
    Flux<Bathroom> findAllBy(Pageable pageable);

    @Query("SELECT * FROM bathroom entity WHERE entity.report_id = :id")
    Flux<Bathroom> findByReport(Long id);

    @Query("SELECT * FROM bathroom entity WHERE entity.report_id IS NULL")
    Flux<Bathroom> findAllWhereReportIsNull();

    @Override
    <S extends Bathroom> Mono<S> save(S entity);

    @Override
    Flux<Bathroom> findAll();

    @Override
    Mono<Bathroom> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface BathroomRepositoryInternal {
    <S extends Bathroom> Mono<S> save(S entity);

    Flux<Bathroom> findAllBy(Pageable pageable);

    Flux<Bathroom> findAll();

    Mono<Bathroom> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Bathroom> findAllBy(Pageable pageable, Criteria criteria);
}
