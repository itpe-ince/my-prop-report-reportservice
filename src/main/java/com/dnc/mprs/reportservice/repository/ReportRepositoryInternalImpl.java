package com.dnc.mprs.reportservice.repository;

import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.repository.rowmapper.AuthorRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Report entity.
 */
@SuppressWarnings("unused")
class ReportRepositoryInternalImpl extends SimpleR2dbcRepository<Report, Long> implements ReportRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AuthorRowMapper authorMapper;
    private final ReportRowMapper reportMapper;

    private static final Table entityTable = Table.aliased("report", EntityManager.ENTITY_ALIAS);
    private static final Table authorTable = Table.aliased("author", "author");

    public ReportRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AuthorRowMapper authorMapper,
        ReportRowMapper reportMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Report.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.authorMapper = authorMapper;
        this.reportMapper = reportMapper;
    }

    @Override
    public Flux<Report> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Report> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ReportSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AuthorSqlHelper.getColumns(authorTable, "author"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(authorTable)
            .on(Column.create("author_id", entityTable))
            .equals(Column.create("id", authorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Report.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Report> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Report> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Report process(Row row, RowMetadata metadata) {
        Report entity = reportMapper.apply(row, "e");
        entity.setAuthor(authorMapper.apply(row, "author"));
        return entity;
    }

    @Override
    public <S extends Report> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
