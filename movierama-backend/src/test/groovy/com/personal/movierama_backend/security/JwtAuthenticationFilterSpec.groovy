package com.personal.movierama_backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import spock.lang.Specification
import spock.lang.Unroll

class JwtAuthenticationFilterSpec extends Specification {

    def jwtUtil = Mock(JwtUtil)
    def userDetailsService = Mock(UserDetailsService)
    def filter = new JwtAuthenticationFilter(jwtUtil, userDetailsService)

    def request = Mock(HttpServletRequest)
    def response = Mock(HttpServletResponse)
    def filterChain = Mock(FilterChain)

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    @Unroll
    def "should skip filter when Authorization header is '#header'"() {
        given:
        request.getHeader("Authorization") >> header

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        SecurityContextHolder.context.authentication == null

        where:
        header << [null, "InvalidToken"]
    }

    def "should authenticate and set context for valid token"() {
        given:
        def token = "valid.jwt.token"
        def username = "user@example.com"
        def userDetails = new User(username, "password", [])

        request.getHeader("Authorization") >> "Bearer $token"
        jwtUtil.extractUsername(token) >> username
        userDetailsService.loadUserByUsername(username) >> userDetails
        jwtUtil.isTokenValid(token, userDetails) >> true

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        with(SecurityContextHolder.context.authentication) {
            it instanceof UsernamePasswordAuthenticationToken
            principal == userDetails
        }
    }

    def "should skip filter if username is null"() {
        given:
        def token = "some.jwt.token"
        request.getHeader("Authorization") >> "Bearer $token"
        jwtUtil.extractUsername(token) >> null

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        0 * userDetailsService.loadUserByUsername(_)
    }

    def "should skip if user already authenticated"() {
        given:
        def token = "token"
        def username = "user@example.com"
        def existingAuth = new UsernamePasswordAuthenticationToken(username, null, [])

        request.getHeader("Authorization") >> "Bearer $token"
        jwtUtil.extractUsername(token) >> username
        SecurityContextHolder.context.setAuthentication(existingAuth)

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        0 * userDetailsService._
    }

    def "should skip if token is invalid"() {
        given:
        def token = "invalid.jwt.token"
        def username = "user@example.com"
        def userDetails = new User(username, "password", [])

        request.getHeader("Authorization") >> "Bearer $token"
        jwtUtil.extractUsername(token) >> username
        userDetailsService.loadUserByUsername(username) >> userDetails
        jwtUtil.isTokenValid(token, userDetails) >> false

        when:
        filter.doFilterInternal(request, response, filterChain)

        then:
        1 * filterChain.doFilter(request, response)
        SecurityContextHolder.context.authentication == null
    }
}

