package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Bathroom;
import com.dnc.mprs.reportservice.repository.rowmapper.BathroomRowMapper;
import com.dnc.mprs.reportservice.repository.rowmapper.ReportRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Bathroom entity.
 */
@SuppressWarnings("unused")
class BathroomRepositoryInternalImpl extends SimpleR2dbcRepository<Bathroom, Long> implements BathroomRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ReportRowMapper reportMapper;
    private final BathroomRowMapper bathroomMapper;

    private static final Table entityTable = Table.aliased("bathroom", EntityManager.ENTITY_ALIAS);
    private static final Table reportTable = Table.aliased("report", "report");

    public BathroomRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ReportRowMapper reportMapper,
        BathroomRowMapper bathroomMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Bathroom.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.reportMapper = reportMapper;
        this.bathroomMapper = bathroomMapper;
    }

    @Override
    public Flux<Bathroom> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Bathroom> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BathroomSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ReportSqlHelper.getColumns(reportTable, "report"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(reportTable)
            .on(Column.create("report_id", entityTable))
            .equals(Column.create("id", reportTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Bathroom.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Bathroom> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Bathroom> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Bathroom process(Row row, RowMetadata metadata) {
        Bathroom entity = bathroomMapper.apply(row, "e");
        entity.setReport(reportMapper.apply(row, "report"));
        return entity;
    }

    @Override
    public <S extends Bathroom> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
