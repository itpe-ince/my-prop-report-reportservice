package com.dnc.mprs.reportservice.service;

import com.dnc.mprs.reportservice.domain.Author;
import com.dnc.mprs.reportservice.repository.AuthorRepository;
import com.dnc.mprs.reportservice.repository.search.AuthorSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Author save(Author author) {
        LOG.debug("Request to save Author : {}", author);
        author = authorRepository.save(author);
        authorSearchRepository.index(author);
        return author;
    }

    /**
     * Update a author.
     *
     * @param author the entity to save.
     * @return the persisted entity.
     */
    public Author update(Author author) {
        LOG.debug("Request to update Author : {}", author);
        author = authorRepository.save(author);
        authorSearchRepository.index(author);
        return author;
    }

    /**
     * Partially update a author.
     *
     * @param author the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Author> partialUpdate(Author author) {
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
            .map(authorRepository::save)
            .map(savedAuthor -> {
                authorSearchRepository.index(savedAuthor);
                return savedAuthor;
            });
    }

    /**
     * Get all the authors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Author> findAll(Pageable pageable) {
        LOG.debug("Request to get all Authors");
        return authorRepository.findAll(pageable);
    }

    /**
     * Get one author by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Author> findOne(Long id) {
        LOG.debug("Request to get Author : {}", id);
        return authorRepository.findById(id);
    }

    /**
     * Delete the author by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Author : {}", id);
        authorRepository.deleteById(id);
        authorSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the author corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Author> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Authors for query {}", query);
        return authorSearchRepository.search(query, pageable);
    }
}
