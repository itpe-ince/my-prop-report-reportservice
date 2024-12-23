package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.EnvFactor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EnvFactor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnvFactorRepository extends ReactiveCrudRepository<EnvFactor, Long>, EnvFactorRepositoryInternal {
    Flux<EnvFactor> findAllBy(Pageable pageable);

    @Query("SELECT * FROM env_factor entity WHERE entity.report_id = :id")
    Flux<EnvFactor> findByReport(Long id);

    @Query("SELECT * FROM env_factor entity WHERE entity.report_id IS NULL")
    Flux<EnvFactor> findAllWhereReportIsNull();

    @Override
    <S extends EnvFactor> Mono<S> save(S entity);

    @Override
    Flux<EnvFactor> findAll();

    @Override
    Mono<EnvFactor> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EnvFactorRepositoryInternal {
    <S extends EnvFactor> Mono<S> save(S entity);

    Flux<EnvFactor> findAllBy(Pageable pageable);

    Flux<EnvFactor> findAll();

    Mono<EnvFactor> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<EnvFactor> findAllBy(Pageable pageable, Criteria criteria);
}
