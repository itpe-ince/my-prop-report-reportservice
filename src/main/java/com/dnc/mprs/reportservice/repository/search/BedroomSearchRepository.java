package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.Bedroom;
import com.dnc.mprs.reportservice.repository.BedroomRepository;
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
 * Spring Data Elasticsearch repository for the {@link Bedroom} entity.
 */
public interface BedroomSearchRepository extends ElasticsearchRepository<Bedroom, Long>, BedroomSearchRepositoryInternal {}

interface BedroomSearchRepositoryInternal {
    Page<Bedroom> search(String query, Pageable pageable);

    Page<Bedroom> search(Query query);

    @Async
    void index(Bedroom entity);

    @Async
    void deleteFromIndexById(Long id);
}

class BedroomSearchRepositoryInternalImpl implements BedroomSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final BedroomRepository repository;

    BedroomSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, BedroomRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Bedroom> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Bedroom> search(Query query) {
        SearchHits<Bedroom> searchHits = elasticsearchTemplate.search(query, Bedroom.class);
        List<Bedroom> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Bedroom entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Bedroom.class);
    }
}
