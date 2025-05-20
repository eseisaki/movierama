package com.personal.movierama_backend.auth.serivce

import com.personal.movierama_backend.auth.dto.LoginRequest
import com.personal.movierama_backend.auth.dto.SignupRequest
import com.personal.movierama_backend.common.exception.UserAlreadyExistsException
import com.personal.movierama_backend.common.model.User
import com.personal.movierama_backend.common.repository.UserRepository
import com.personal.movierama_backend.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class AuthServiceImplSpec extends Specification {

    UserRepository userRepository
    AuthenticationManager authenticationManager
    PasswordEncoder passwordEncoder
    JwtUtil jwtUtil

    AuthService authService

    def setup() {
        authenticationManager = Mock()
        userRepository = Mock()
        passwordEncoder = Mock()
        jwtUtil = Mock()

        authService = new AuthServiceImpl(authenticationManager, userRepository, passwordEncoder, jwtUtil)
    }

    def "should register new user and return JWT"() {
        given:
        def request = new SignupRequest("john", "john@example.com", "plain123")
        def hashedPassword = "hashed123"
        def expectedToken = "jwt.token.value"

        when:
        def token = authService.registerUser(request)

        then:
        1 * userRepository.existsByUsername("john") >> false
        1 * userRepository.existsByEmail("john@example.com") >> false
        1 * passwordEncoder.encode("plain123") >> hashedPassword
        1 * userRepository.save({ User u -> u.username == "john" && u.email == "john@example.com" && u.password == hashedPassword
        }) >> new User("john", "john@example.com", hashedPassword)
        1 * jwtUtil.generateToken(_ as User) >> expectedToken

        token == expectedToken
    }

    def "should throw exception if #field already exists"() {
        given:
        def request = new SignupRequest("john", "john@mail.com", "pass123")

        and:
        userRepository.existsByUsername("john") >> usernameExists
        userRepository.existsByEmail("john@mail.com") >> emailExists

        when:
        authService.registerUser(request)

        then:
        thrown(UserAlreadyExistsException)

        where:
        field      | usernameExists | emailExists
        "username" | true           | false
        "email"    | false          | true
    }

    def "should authenticate user and return JWT token"() {
        given:
        def request = new LoginRequest("john", "plain123")
        def user = new User("john", "john@example.com", "hashed123")
        def expectedToken = "jwt.token.value"

        def authentication = Mock(Authentication) {
            getPrincipal() >> user
        }

        when:
        def token = authService.authenticateUser(request)

        then:
        1 * authenticationManager.authenticate({ UsernamePasswordAuthenticationToken t ->
            t.principal == "john" && t.credentials == "plain123"
        }) >> authentication

        1 * jwtUtil.generateToken(user) >> expectedToken

        token == expectedToken
    }


    def "should throw BadCredentialsException when authentication fails"() {
        given:
        def request = new LoginRequest("john", "wrongpass")

        when:
        authService.authenticateUser(request)

        then:
        1 * authenticationManager.authenticate(_) >> {
            throw new BadCredentialsException("Bad credentials")
        }

        thrown(BadCredentialsException)
    }

}
