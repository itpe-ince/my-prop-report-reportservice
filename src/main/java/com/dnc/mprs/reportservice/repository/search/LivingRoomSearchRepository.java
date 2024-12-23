package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.LivingRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link LivingRoom} entity.
 */
public interface LivingRoomSearchRepository extends ReactiveElasticsearchRepository<LivingRoom, Long>, LivingRoomSearchRepositoryInternal {}

interface LivingRoomSearchRepositoryInternal {
    Flux<LivingRoom> search(String query, Pageable pageable);

    Flux<LivingRoom> search(Query query);
}

class LivingRoomSearchRepositoryInternalImpl implements LivingRoomSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    LivingRoomSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<LivingRoom> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<LivingRoom> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, LivingRoom.class).map(SearchHit::getContent);
    }
}
