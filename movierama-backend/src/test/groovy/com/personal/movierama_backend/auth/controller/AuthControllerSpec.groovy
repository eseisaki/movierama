package com.personal.movierama_backend.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.personal.movierama_backend.auth.dto.SignupRequest
import com.personal.movierama_backend.auth.serivce.AuthService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AuthControllerSpec extends Specification {

    MockMvc mockMvc
    AuthService authService = Mock()
    ObjectMapper objectMapper = new ObjectMapper()
    AuthController authController

    def setup() {
        authController = new AuthController(authService)
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build()
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

    def "should return 400 Bad Request when required fields are missing"() {
        given:
        def invalidRequestJson = '''{
        "username": "",
        "email": null,
        "password": ""
    }''' 

        when:
        def result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))

        then:
        result.andExpect(status().isBadRequest())
    }
}
