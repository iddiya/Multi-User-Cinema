package com.ecinema.app.repositories;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ModeratorRepositoryTest {

    @Autowired
    private ModeratorRepository moderatorRepository;

    @AfterEach
    void tearDown() {
        moderatorRepository.deleteAll();
    }

}