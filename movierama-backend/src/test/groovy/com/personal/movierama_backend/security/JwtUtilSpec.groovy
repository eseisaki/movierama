package com.personal.movierama_backend.security

import com.personal.movierama_backend.common.model.User
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.WeakKeyException
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Field

class JwtUtilSpec extends Specification {

    private static final String VALID_SECRET = "test-secret-must-be-at-least-32-bytes-long!!"
    private static final long VALID_EXPIRATION = 3600L // seconds

    private JwtUtil jwtUtil

    def setup() {
        jwtUtil = new JwtUtil()
        inject(jwtUtil, "secret", VALID_SECRET)
        inject(jwtUtil, "expiration", VALID_EXPIRATION)
        jwtUtil.init()
    }

    def "should generate a valid JWT token"() {
        given:
        def user = new User("john", "john@example.com", "pass123")

        when:
        def token = jwtUtil.generateToken(user)

        then:
        isWellFormedJwt(token)
    }

    def "should fail if secret is shorter than 32 bytes"() {
        given:
        def shortKeyJwtUtil = new JwtUtil()
        inject(shortKeyJwtUtil, "secret", "short-key")
        inject(shortKeyJwtUtil, "expiration", VALID_EXPIRATION)

        when:
        shortKeyJwtUtil.init()

        then:
        thrown(WeakKeyException)
    }

    def "should extract username from a real generated token"() {
        given:
        def user = new User("john_doe", "john@example.com", "pass")
        def token = jwtUtil.generateToken(user)

        when:
        def username = jwtUtil.extractUsername(token)

        then:
        username == "john_doe"
    }

    @Unroll
    def "should return #expected when user is #userUsername and token subject is #tokenSubject and token is #desc"() {
        given:
        def testJwtUtil = new JwtUtil()
        inject(testJwtUtil, "secret", VALID_SECRET)
        inject(testJwtUtil, "expiration", expiration)
        testJwtUtil.init()

        def user = new User(tokenSubject, "user@mail.com", "pass")
        def token = testJwtUtil.generateToken(user)

        def userDetails = Mock(UserDetails)
        userDetails.getUsername() >> userUsername

        expect:
        testJwtUtil.isTokenValid(token, userDetails) == expected

        where:
        tokenSubject | userUsername | expiration | expected | desc
        "john_doe"   | "john_doe"   | 3600L      | true     | "valid"
        "john_doe"   | "jane_doe"   | 3600L      | false    | "username mismatch"
    }

    def "should throw ExpiredJwtException for expired token"() {
        given:
        def expiredJwtUtil = new JwtUtil()
        inject(expiredJwtUtil, "secret", VALID_SECRET)
        inject(expiredJwtUtil, "expiration", -60L)
        expiredJwtUtil.init()

        def user = new User("john_doe", "john@example.com", "pass")
        def token = expiredJwtUtil.generateToken(user)

        def userDetails = Mock(UserDetails)
        userDetails.getUsername() >> "john_doe"

        when:
        expiredJwtUtil.isTokenValid(token, userDetails)

        then:
        thrown(ExpiredJwtException)
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
