package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Admin;
import org.springframework.stereotype.Repository;

/** The interface Admin repository. */
@Repository
public interface AdminRepository extends UserAuthorityRepository<Admin> {}
