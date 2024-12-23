package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.Infrastructure;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Infrastructure} entity.
 */
public interface InfrastructureSearchRepository
    extends ReactiveElasticsearchRepository<Infrastructure, Long>, InfrastructureSearchRepositoryInternal {}

interface InfrastructureSearchRepositoryInternal {
    Flux<Infrastructure> search(String query, Pageable pageable);

    Flux<Infrastructure> search(Query query);
}

class InfrastructureSearchRepositoryInternalImpl implements InfrastructureSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    InfrastructureSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Infrastructure> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Infrastructure> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Infrastructure.class).map(SearchHit::getContent);
    }
}
