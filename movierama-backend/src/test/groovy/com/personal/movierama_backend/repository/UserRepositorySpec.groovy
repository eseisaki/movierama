package com.personal.movierama_backend.repository

import com.personal.movierama_backend.MovieramaBackendApplication
import com.personal.movierama_backend.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification



@DataJpaTest
@ContextConfiguration(classes = MovieramaBackendApplication)
class UserRepositorySpec extends Specification {

    @Autowired
    private UserRepository userRepository

    def "should save and retrieve user by username()"(){
        given:
        def user = new User(username: "eftychia", email: "efti@example.com", password: "secret123")

        when:
        userRepository.save(user)
        def result = userRepository.findByUsername("eftychia")

        then:
        result.isPresent()
        result.get().email == "efti@example.com"
    }

    def "should return empty for non-existing username"() {
        expect:
        userRepository.findByUsername("ghost").isEmpty()
    }
}
