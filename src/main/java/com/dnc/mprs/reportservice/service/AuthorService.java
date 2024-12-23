package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Author;
import com.dnc.mprs.reportservice.repository.AuthorRepository;
import com.dnc.mprs.reportservice.repository.search.AuthorSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.dnc.mprs.reportservice.domain.Author}.
 */
@Service
@Transactional
public class AuthorService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;

    private final AuthorSearchRepository authorSearchRepository;

    public AuthorService(AuthorRepository authorRepository, AuthorSearchRepository authorSearchRepository) {
        this.authorRepository = authorRepository;
        this.authorSearchRepository = authorSearchRepository;
    }

    /**
     * Save a author.
     *
     * @param author the entity to save.
     * @return the persisted entity.
     */
    public Mono<Author> save(Author author) {
        LOG.debug("Request to save Author : {}", author);
        return authorRepository.save(author).flatMap(authorSearchRepository::save);
    }

    /**
     * Update a author.
     *
     * @param author the entity to save.
     * @return the persisted entity.
     */
    public Mono<Author> update(Author author) {
        LOG.debug("Request to update Author : {}", author);
        return authorRepository.save(author).flatMap(authorSearchRepository::save);
    }

    /**
     * Partially update a author.
     *
     * @param author the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Author> partialUpdate(Author author) {
        LOG.debug("Request to partially update Author : {}", author);

        return authorRepository
            .findById(author.getId())
            .map(existingAuthor -> {
                if (author.getName() != null) {
                    existingAuthor.setName(author.getName());
                }
                if (author.getContactInfo() != null) {
                    existingAuthor.setContactInfo(author.getContactInfo());
                }

                return existingAuthor;
            })
            .flatMap(authorRepository::save)
            .flatMap(savedAuthor -> {
                authorSearchRepository.save(savedAuthor);
                return Mono.just(savedAuthor);
            });
    }

    /**
     * Get all the authors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Author> findAll(Pageable pageable) {
        LOG.debug("Request to get all Authors");
        return authorRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of authors available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return authorRepository.count();
    }

    /**
     * Returns the number of authors available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return authorSearchRepository.count();
    }

    /**
     * Get one author by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Author> findOne(Long id) {
        LOG.debug("Request to get Author : {}", id);
        return authorRepository.findById(id);
    }

    /**
     * Delete the author by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Author : {}", id);
        return authorRepository.deleteById(id).then(authorSearchRepository.deleteById(id));
    }

    /**
     * Search for the author corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Author> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Authors for query {}", query);
        return authorSearchRepository.search(query, pageable);
    }
}
