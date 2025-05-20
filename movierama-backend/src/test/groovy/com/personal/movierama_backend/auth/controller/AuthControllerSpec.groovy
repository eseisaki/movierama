package com.personal.movierama_backend.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.personal.movierama_backend.auth.dto.LoginRequest
import com.personal.movierama_backend.auth.dto.SignupRequest
import com.personal.movierama_backend.auth.serivce.AuthService
import com.personal.movierama_backend.common.exception.GlobalExceptionHandler
import com.personal.movierama_backend.common.exception.UserAlreadyExistsException
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.test.web.servlet.MockMvc
import static org.hamcrest.Matchers.containsString
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerSpec extends Specification {

    ObjectMapper objectMapper
    MockMvc mockMvc
    AuthService authService = Mock()
    AuthController authController

    def setup() {
        authController = new AuthController(authService)
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build()

        objectMapper = new ObjectMapper().findAndRegisterModules()

    }

    def "should return token when signup is successful"() {
        given:
        def request = new SignupRequest("john_doe", "john@example.com", "securePassword123")
        def expectedToken = "mock-jwt-token"

        when:
        def result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        1 * authService.registerUser(_ as SignupRequest) >> expectedToken

        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.token').value(expectedToken))
    }

    def "should return 400 when signup request body is null"() {
        when:
        def result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error').value("Invalid or missing request body"))
    }

    def "should return 400 Bad Request when required fields are missing"() {
        given:
        def invalidRequest = new SignupRequest("", "", "")

        when:
        def result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.username').exists())
                .andExpect(jsonPath('$.email').exists())
                .andExpect(jsonPath('$.password', containsString("Password")))
    }


    def "should return 409 Conflict when username already exists"() {
        given:
        def request = new SignupRequest("existing_user", "email@example.com", "validPass123")

        when:
        def result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        1 * authService.registerUser(_ as SignupRequest) >> {
            throw new UserAlreadyExistsException("Username '" + request.username() + "' is already taken.")
        }

        result.andExpect(status().isConflict())
                .andExpect(jsonPath('$.error').value("Username '" + request.username() + "' is already taken."))
    }


    def "should return token when login is successful"() {
        given:
        def request = new LoginRequest("john_doe", "securePassword123")
        def expectedToken = "mock-jwt-token"

        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        1 * authService.authenticateUser(_ as LoginRequest) >> expectedToken

        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.token').value(expectedToken))
    }

    def "should return 400 when login request body is null"() {
        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))

        then:
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.error').value("Invalid or missing request body"))
    }

    def "should return 400 Bad Request when required fields are missing or invalid"() {
        given:
        def invalidRequestJson = '''{
        "username": "",
        "email": null,
        "password": ""
    }'''

        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))

        then:
        result.andExpect(status().isBadRequest())
    }

    def "should return 401 Unauthorized when credentials are invalid"() {
        given:
        def request = new LoginRequest("john_doe", "wrongPassword")

        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        1 * authService.authenticateUser(_ as LoginRequest) >> {
            throw new BadCredentialsException("Bad credentials")
        }

        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath('$.error').value("Unauthorized"))
                .andExpect(jsonPath('$.message').value("Invalid username or password"))
    }

    def "should return 401 Unauthorized when user is not found"() {
        given:
        def request = new LoginRequest("unknown_user", "anyPassword")

        when:
        def result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        1 * authService.authenticateUser(_ as LoginRequest) >> {
            throw new UsernameNotFoundException("User not found")
        }

        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath('$.error').value("Unauthorized"))
                .andExpect(jsonPath('$.message').value("Invalid username or password"))
    }
}
