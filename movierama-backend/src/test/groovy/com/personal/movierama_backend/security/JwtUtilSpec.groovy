package com.personal.movierama_backend.security

import com.personal.movierama_backend.common.model.User
import io.jsonwebtoken.security.WeakKeyException
import spock.lang.Specification

import java.lang.reflect.Field

class JwtUtilSpec extends Specification {

    private JwtUtil jwtUtil

    def setup(){
        jwtUtil = new JwtUtil()
    }

    def "should generate a valid JWT token"() {
        given:
        inject(jwtUtil, "secret", "test-secret-must-be-at-least-32-bytes-long!!")
        inject(jwtUtil, "expiration", 3600000L)

        def user = new User("john", "john@example.com", "pass123")

        when:
        def token = jwtUtil.generateToken(user)

        then:
        isWellFormedJwt(token)
    }

    def "should fail if secret is shorter than 32 bytes"() {
        given:
        inject(jwtUtil, "secret", "short-key") // too short
        inject(jwtUtil, "expiration", 3600000L)

        when:
        jwtUtil.generateToken(new User("john", "john@example.com", "pass123"))

        then:
        thrown(WeakKeyException)
    }

    private static boolean isWellFormedJwt(String token) {
        return token != null && token.split("\\.").size() == 3
    }

    private static void inject(Object target, String fieldName, Object value) {
        Field field = target.getClass().getDeclaredField(fieldName)
        field.setAccessible(true)
        field.set(target, value)
    }
}
