package com.ecinema.app.repositories;

import com.ecinema.app.domain.entities.Movie;
import com.ecinema.app.services.MovieService;
import com.ecinema.app.util.UtilMethods;
import com.ecinema.app.domain.enums.MovieCategory;
import com.ecinema.app.domain.enums.MsrbRating;
import com.ecinema.app.domain.objects.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static com.ecinema.app.domain.enums.MovieCategory.*;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findAllPagination() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Movie movie = new Movie();
            movie.setTitle("title" + i);
            movies.add(movie);
            movieRepository.save(movie);
        }
        List<Movie> control = movies.subList(0, 5);
        // when
        Pageable pageable = PageRequest.of(0, 5);
        Page<Movie> page = movieRepository.findAll(pageable);
        List<Movie> test = page.getContent();
        // then
        for (int i = 0; i < 5; i++) {
            Movie movie1 = control.get(i);
            Movie movie2 = test.get(i);
            assertEquals(movie1.getId(), movie2.getId());
            assertEquals(movie1.getTitle(), movie2.getTitle());
        }
    }

    @Test
    void findAllLikeTitlePagination() {
        // given
        for (int i = 0; i < 30; i++) {
            Movie movie = new Movie();
            movie.setId((long) i + 1);
            movie.setSearchTitle(i < 15 ? "TITLE" : "DUMMY");
            movieRepository.save(movie);
        }
        // when
        Pageable pageable =  PageRequest.of(0, 15);
        Page<Movie> page = movieRepository.findBySearchTitleContaining("T", pageable);
        List<Movie> test = page.getContent();
        // then
        assertEquals(15, test.size());
        for (Movie movie : test) {
            assertTrue(movie.getSearchTitle().contains("T"));
        }
    }

    @Test
    void findAllLikeTitle() {
        // given
        for (int i = 0; i < 30; i++) {
            Movie movie = new Movie();
            movie.setId((long) i + 1);
            movie.setSearchTitle(i < 15 ? "TITLE" + i : "DUMMY");
            movieRepository.save(movie);
        }
        // when
        List<Movie> test = movieRepository.findBySearchTitleContaining("T");
        // then
        assertEquals(15, test.size());
        for (Movie movie : test) {
            assertTrue(movie.getSearchTitle().contains("T"));
        }
    }

    @Test
    void findAllByMsrbRating() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie();
            int j = i % MsrbRating.values().length;
            movie.setMsrbRating(MsrbRating.values()[j]);
            movieRepository.save(movie);
            movies.add(movie);
        }
        List<Movie> control = movies.stream()
                .filter(movie -> movie.getMsrbRating().equals(MsrbRating.PG))
                .collect(Collectors.toList());
        // when
        List<Movie> test = movieRepository.findAllByMsrbRating(MsrbRating.PG);
        // then
        assertEquals(5, control.size());
        assertEquals(control.size(), test.size());
        assertEquals(control, test);
    }

    @Test
    void findAllByMoviesCategoriesContainsOne() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Movie movie = new Movie();
            int j = i % MovieCategory.values().length;
            movie.getMovieCategories().add(MovieCategory.values()[j]);
            movieRepository.save(movie);
            movies.add(movie);
        }
        List<Movie> control = movies.stream()
                .filter(movie -> movie.getMovieCategories().contains(MovieCategory.ROMANCE))
                .collect(Collectors.toList());
        // when
        List<Movie> test = movieRepository.findAllByMoviesCategoriesContains(MovieCategory.ROMANCE);
        // then
        assertEquals(control, test);
    }

    @Test
    void findAllByMoviesCategoriesContainsSet() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Movie movie = new Movie();
            movie.setTitle(UUID.randomUUID().toString());
            int j = i % MovieCategory.values().length;
            while (j >= 0) {
                movie.getMovieCategories().add(MovieCategory.values()[j]);
                j--;
            }
            movieRepository.save(movie);
            movies.add(movie);
        }
        List<Movie> control = movies.stream()
                .filter(movie -> {
                    Set<MovieCategory> movieCategories = movie.getMovieCategories();
                    return movieCategories.contains(HORROR) ||
                            movieCategories.contains(DARK) ||
                            movieCategories.contains(EPIC) ||
                            movieCategories.contains(RAUNCHY);

                }).collect(Collectors.toList());
        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getTitle);
        control.sort(movieComparator);
        // when
        List<Movie> test = movieRepository.findAllByMovieCategoriesContainsSet(
                new HashSet<>() {{
                    add(HORROR);
                    add(DARK);
                    add(EPIC);
                    add(RAUNCHY);
                }});
        test.sort(movieComparator);
        // then
        assertEquals(control, test);
    }

    @Test
    void findAllOrderByReleaseDateAscending() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie();
            movie.setReleaseDate(UtilMethods.randomDate());
            movieRepository.save(movie);
            movies.add(movie);
        }
        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getReleaseDate);
        movies.sort(movieComparator);
        // when
        List<Movie> test = movieRepository.findAllOrderByReleaseDateAscending();
        // then
        assertEquals(movies, test);
    }

    @Test
    void findAllOrderByReleaseDateDescending() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie();
            movie.setReleaseDate(UtilMethods.randomDate());
            movieRepository.save(movie);
            movies.add(movie);
        }
        Comparator<Movie> movieComparator = (o1, o2) -> o2.getReleaseDate().compareTo(o1.getReleaseDate());
        movies.sort(movieComparator);
        // when
        List<Movie> test = movieRepository.findAllOrderByReleaseDateDescending();
        // then
        assertEquals(movies, test);
    }

    @Test
    void findAllOrderByDurationAscending() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie();
            movie.setDuration(Duration.randomDuration());
            movieRepository.save(movie);
            movies.add(movie);
        }
        Comparator<Movie> movieComparator = Comparator.comparing(Movie::getDuration);
        movies.sort(movieComparator);
        // when
        List<Movie> test = movieRepository.findAllOrderByDurationAscending();
        // then
        assertEquals(movies.size(), test.size());
        for (int i = 0; i < movies.size(); i++) {
            assertTrue(movies.get(i).getDuration().compareTo(test.get(i).getDuration()) <= 0);
        }
    }

    @Test
    void findAllOrderByDurationDescending() {
        // given
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Movie movie = new Movie();
            movie.setDuration(Duration.randomDuration());
            movieRepository.save(movie);
            movies.add(movie);
        }
        Comparator<Movie> movieComparator = (o1, o2) -> o2.getDuration().compareTo(o1.getDuration());
        movies.sort(movieComparator);
        // when
        List<Movie> test = movieRepository.findAllOrderByDurationDescending();
        // then
        assertEquals(movies.size(), test.size());
        for (int i = 0; i < movies.size(); i++) {
            assertTrue(movies.get(i).getDuration().compareTo(test.get(i).getDuration()) >= 0);
        }
    }

    @Test
    void existsByTitle() {
        // given
        Movie movie = new Movie();
        movie.setSearchTitle("TESTMOVIE");
        movieRepository.save(movie);
        // when
        String test1Str = MovieService.convertTitleToSearchTitle("Test Movie");
        String test2Str = MovieService.convertTitleToSearchTitle("Test movie");
        String test3Str = MovieService.convertTitleToSearchTitle("teStM oVi E");
        String test4Str = MovieService.convertTitleToSearchTitle("   t E sT M   O vI e");
        String test5Str = MovieService.convertTitleToSearchTitle("false");
        boolean test1 = movieRepository.existsBySearchTitle(test1Str);
        boolean test2 = movieRepository.existsBySearchTitle(test2Str);
        boolean test3 = movieRepository.existsBySearchTitle(test3Str);
        boolean test4 = movieRepository.existsBySearchTitle(test4Str);
        boolean test5 = movieRepository.existsBySearchTitle(test5Str);
        // then
        assertEquals("TESTMOVIE", test1Str);
        assertEquals("TESTMOVIE", test2Str);
        assertEquals("TESTMOVIE", test3Str);
        assertEquals("TESTMOVIE", test4Str);
        assertNotEquals("TESTMOVIE", test5Str);
        assertTrue(test1);
        assertTrue(test2);
        assertTrue(test3);
        assertTrue(test4);
        assertFalse(test5);
    }

}