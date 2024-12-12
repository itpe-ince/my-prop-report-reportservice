package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.EnvFactor;
import com.dnc.mprs.reportservice.repository.EnvFactorRepository;
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
 * Spring Data Elasticsearch repository for the {@link EnvFactor} entity.
 */
public interface EnvFactorSearchRepository extends ElasticsearchRepository<EnvFactor, Long>, EnvFactorSearchRepositoryInternal {}

interface EnvFactorSearchRepositoryInternal {
    Page<EnvFactor> search(String query, Pageable pageable);

    Page<EnvFactor> search(Query query);

    @Async
    void index(EnvFactor entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EnvFactorSearchRepositoryInternalImpl implements EnvFactorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EnvFactorRepository repository;

    EnvFactorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EnvFactorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<EnvFactor> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<EnvFactor> search(Query query) {
        SearchHits<EnvFactor> searchHits = elasticsearchTemplate.search(query, EnvFactor.class);
        List<EnvFactor> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(EnvFactor entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), EnvFactor.class);
    }
}
