package com.ecinema.app.controllers;

import com.ecinema.app.configs.InitializationConfig;
import com.ecinema.app.domain.dtos.MovieDto;
import com.ecinema.app.domain.dtos.ReviewDto;
import com.ecinema.app.domain.dtos.ScreeningDto;
import com.ecinema.app.services.MovieService;
import com.ecinema.app.services.ReviewService;
import com.ecinema.app.services.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RequiredArgsConstructor
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MovieInfoControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private ScreeningService screeningService;

    @MockBean
    private InitializationConfig config;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    void moviesPage()
            throws Exception {
        Page<MovieDto> pageOfDtos = new PageImpl<>(new ArrayList<>());
        given(movieService.findAll(any(PageRequest.class)))
                .willReturn(pageOfDtos);
        given(movieService.findAllByLikeTitle(anyString(), any(PageRequest.class)))
                .willReturn(pageOfDtos);
        mockMvc.perform(get("/movies"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(result -> model().attributeExists("movies"));
    }

    @Test
    void searchMovie()
            throws Exception {
        Page<MovieDto> pageOfDtos = new PageImpl<>(new ArrayList<>());
        given(movieService.findAllByLikeTitle(eq("dune"), any(PageRequest.class)))
                .willReturn(pageOfDtos);
        mockMvc.perform(get("/movies")
                                .param("search", "dune"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(result -> model().attribute(
                       "movies", is(Map.of(0, pageOfDtos.getContent()))));
    }

    @Test
    void movieInfoPage()
            throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(1L);
        given(movieService.findById(1L)).willReturn(movieDto);
        mockMvc.perform(get("/movie-info")
                                .param("id", String.valueOf(movieDto.getId())))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(result -> model().attribute("movie", movieDto));
    }

    @Test
    void movieReviewsPage()
            throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(1L);
        given(movieService.findByTitle("dune")).willReturn(movieDto);
        PageRequest pageRequest = PageRequest.of(0, 6);
        Page<ReviewDto> pageOfDtos = new PageImpl<>(new ArrayList<>());
        given(reviewService.findPageByMovieIdAndNotCensored(movieDto.getId(), pageRequest))
                .willReturn(pageOfDtos);
        mockMvc.perform(get("/movie-reviews")
                                .param("id", String.valueOf(movieDto.getId())))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(result -> model().attribute("reviews", pageOfDtos));
    }

    @Test
    void movieScreeningsPage()
            throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setId(1L);
        given(movieService.findByTitle("dune")).willReturn(movieDto);
        PageRequest pageRequest = PageRequest.of(0, 6);
        Page<ScreeningDto> pageOfDtos = new PageImpl<>(new ArrayList<>());
        given(screeningService.findPageByMovieId(1L, pageRequest))
                .willReturn(pageOfDtos);
        mockMvc.perform(get("/movie-screenings")
                                .param("id", String.valueOf(1L)))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(result -> model().attribute("screenings", pageOfDtos));
    }

}