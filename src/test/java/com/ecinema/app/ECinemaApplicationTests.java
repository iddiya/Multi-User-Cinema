package com.ecinema.app;

import com.ecinema.app.configs.InitializationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ECinemaApplicationContextTest {

    @MockBean
    private InitializationConfig config;

    @Test
    public void loadContext() {}

}
