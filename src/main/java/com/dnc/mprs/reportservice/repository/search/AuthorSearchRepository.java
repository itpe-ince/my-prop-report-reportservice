package com.dnc.mprs.reportservice.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.dnc.mprs.reportservice.domain.Author;
import com.dnc.mprs.reportservice.repository.AuthorRepository;
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
 * Spring Data Elasticsearch repository for the {@link Author} entity.
 */
public interface AuthorSearchRepository extends ElasticsearchRepository<Author, Long>, AuthorSearchRepositoryInternal {}

interface AuthorSearchRepositoryInternal {
    Page<Author> search(String query, Pageable pageable);

    Page<Author> search(Query query);

    @Async
    void index(Author entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AuthorSearchRepositoryInternalImpl implements AuthorSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AuthorRepository repository;

    AuthorSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AuthorRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Author> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Author> search(Query query) {
        SearchHits<Author> searchHits = elasticsearchTemplate.search(query, Author.class);
        List<Author> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Author entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Author.class);
    }
}
