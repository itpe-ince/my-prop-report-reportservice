package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.Entrance;
import com.dnc.mprs.reportservice.repository.EntranceRepository;
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
 * Spring Data Elasticsearch repository for the {@link Entrance} entity.
 */
public interface EntranceSearchRepository extends ElasticsearchRepository<Entrance, Long>, EntranceSearchRepositoryInternal {}

interface EntranceSearchRepositoryInternal {
    Page<Entrance> search(String query, Pageable pageable);

    Page<Entrance> search(Query query);

    @Async
    void index(Entrance entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EntranceSearchRepositoryInternalImpl implements EntranceSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EntranceRepository repository;

    EntranceSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EntranceRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Entrance> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Entrance> search(Query query) {
        SearchHits<Entrance> searchHits = elasticsearchTemplate.search(query, Entrance.class);
        List<Entrance> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Entrance entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Entrance.class);
    }
}
