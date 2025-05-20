package com.personal.movierama_backend.security

import com.personal.movierama_backend.common.model.User
import com.personal.movierama_backend.common.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import spock.lang.Specification

class UserDetailsServiceImplSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def service = new UserDetailsServiceImpl(userRepository)

    def "should return UserDetails when user is found"() {
        given:
        def username = "john"
        def user = new User(username, "john@example.com", "hashed123")

        when:
        def result = service.loadUserByUsername(username)

        then:
        1 * userRepository.findByUsername(username) >> Optional.of(user)
        result == user
    }

    def "should throw UsernameNotFoundException when user is not found"() {
        given:
        def username = "missing_user"

        when:
        service.loadUserByUsername(username)

        then:
        1 * userRepository.findByUsername(username) >> Optional.empty()

        def ex = thrown(UsernameNotFoundException)
        ex.message == "User not found: missing_user"
    }
}

