package ca.n4dev.aegaeon.api.repository;

import ca.n4dev.aegaeon.api.model.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AuthorityRepository.java
 * TODO(rguillemette) Add description
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 20 - 2018
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    /**
     * Find authority by code
     * @param pCode The code
     * @return An authority or null.
     */
    Authority findByCode(String pCode);
}
