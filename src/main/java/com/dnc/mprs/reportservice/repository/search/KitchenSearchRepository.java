package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.Kitchen;
import com.dnc.mprs.reportservice.repository.KitchenRepository;
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
 * Spring Data Elasticsearch repository for the {@link Kitchen} entity.
 */
public interface KitchenSearchRepository extends ElasticsearchRepository<Kitchen, Long>, KitchenSearchRepositoryInternal {}

interface KitchenSearchRepositoryInternal {
    Page<Kitchen> search(String query, Pageable pageable);

    Page<Kitchen> search(Query query);

    @Async
    void index(Kitchen entity);

    @Async
    void deleteFromIndexById(Long id);
}

class KitchenSearchRepositoryInternalImpl implements KitchenSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final KitchenRepository repository;

    KitchenSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, KitchenRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Kitchen> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Kitchen> search(Query query) {
        SearchHits<Kitchen> searchHits = elasticsearchTemplate.search(query, Kitchen.class);
        List<Kitchen> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Kitchen entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Kitchen.class);
    }
}
