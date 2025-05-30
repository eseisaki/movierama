package com.personal.movierama_backend.common.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import spock.lang.Specification

class GlobalExceptionHandlerSpec extends Specification {

    def handler = new GlobalExceptionHandler()

    def "should handle UserAlreadyExistsException with 409 status"() {
        given:
        def ex = new UserAlreadyExistsException("User already exists")

        when:
        ResponseEntity<Map<String, String>> response = handler.handleUserExists(ex)

        then:
        response.statusCode == HttpStatus.CONFLICT
        response.body["error"] == "User already exists"
    }

    def "should handle IllegalArgumentException with 400 status"() {
        given:
        def ex = new IllegalArgumentException("Bad input")

        when:
        def response = handler.handleIllegalArgument(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body["error"] == "Bad input"
    }

    def "should handle generic Exception with 500 status"() {
        given:
        def ex = new RuntimeException("Oops")

        when:
        def response = handler.handleAll(ex)

        then:
        response.statusCode == HttpStatus.INTERNAL_SERVER_ERROR
        response.body["error"] == "An unexpected error occurred"
    }

    def "should handle MethodArgumentNotValidException with 400 and field errors"() {
        given:
        def bindingResult = Mock(BindingResult)
        def ex = new MethodArgumentNotValidException(null, bindingResult)

        and:
        bindingResult.getFieldErrors() >> [
                new FieldError("object", "username", "must not be blank"),
                new FieldError("object", "email", "must be a valid email")
        ]

        when:
        def response = handler.handleValidation(ex)

        then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body["username"] == "must not be blank"
        response.body["email"] == "must be a valid email"
    }
}
