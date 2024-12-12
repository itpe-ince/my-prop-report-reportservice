package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.Report;
import com.dnc.mprs.reportservice.repository.ReportRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Report} entity.
 */
public interface ReportSearchRepository extends ElasticsearchRepository<Report, Long>, ReportSearchRepositoryInternal {}

interface ReportSearchRepositoryInternal {
    Page<Report> search(String query, Pageable pageable);

    Page<Report> search(Query query);

    @Async
    void index(Report entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ReportSearchRepositoryInternalImpl implements ReportSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ReportRepository repository;

    ReportSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ReportRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Report> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Report> search(Query query) {
        SearchHits<Report> searchHits = elasticsearchTemplate.search(query, Report.class);
        List<Report> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Report entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Report.class);
    }
}
