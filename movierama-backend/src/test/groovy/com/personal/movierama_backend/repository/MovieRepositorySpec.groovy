package com.personal.movierama_backend.repository

import com.personal.movierama_backend.MovieramaBackendApplication
import com.personal.movierama_backend.model.Movie
import com.personal.movierama_backend.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@DataJpaTest
@ContextConfiguration(classes = MovieramaBackendApplication)
class MovieRepositorySpec extends Specification {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private MovieRepository movieRepository

    private User testUser

    def setup() {
        testUser = userRepository.save(new User(
                username: "eftychia",
                email: "efti@example.com",
                password: "secret123"
        ))
    }

    def "should save and retrieve movies by user ID"() {
        given:
        def movie = new Movie(
                title: "Inception",
                description: "Mind-bending sci-fi",
                user: testUser
        )
        movieRepository.save(movie)

        when:
        def results = movieRepository.findByUser(testUser)

        then:
        results.size() == 1
        results[0].title == "Inception"
        results[0].user.username == "eftychia"
    }

    def "should return empty list for user with no movies"() {
        given:
        def anotherUser = userRepository.save(new User(
                username: "newbie",
                email: "new@example.com",
                password: "pass"
        ))

        expect:
        movieRepository.findByUser(anotherUser).isEmpty()
    }
}
