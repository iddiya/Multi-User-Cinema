package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Moderator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Moderator role def repository.
 */
@Repository
public interface ModeratorRepository extends UserAuthorityRepository<Moderator> {

    /**
     * Find all ids list.
     *
     * @return the list
     */
    @Query("SELECT m.id FROM Moderator m")
    List<Long> findAllIds();

}
