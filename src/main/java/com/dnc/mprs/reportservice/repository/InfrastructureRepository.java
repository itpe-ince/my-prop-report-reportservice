package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Infrastructure;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Infrastructure entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InfrastructureRepository extends ReactiveCrudRepository<Infrastructure, Long>, InfrastructureRepositoryInternal {
    Flux<Infrastructure> findAllBy(Pageable pageable);

    @Query("SELECT * FROM infrastructure entity WHERE entity.report_id = :id")
    Flux<Infrastructure> findByReport(Long id);

    @Query("SELECT * FROM infrastructure entity WHERE entity.report_id IS NULL")
    Flux<Infrastructure> findAllWhereReportIsNull();

    @Override
    <S extends Infrastructure> Mono<S> save(S entity);

    @Override
    Flux<Infrastructure> findAll();

    @Override
    Mono<Infrastructure> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface InfrastructureRepositoryInternal {
    <S extends Infrastructure> Mono<S> save(S entity);

    Flux<Infrastructure> findAllBy(Pageable pageable);

    Flux<Infrastructure> findAll();

    Mono<Infrastructure> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Infrastructure> findAllBy(Pageable pageable, Criteria criteria);
}
