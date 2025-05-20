package com.personal.movierama_backend.movie.repository;

import com.personal.movierama_backend.movie.model.Movie;
import com.personal.movierama_backend.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findByUser(User user);
}
