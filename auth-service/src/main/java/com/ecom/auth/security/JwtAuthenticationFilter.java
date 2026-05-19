package com.ecom.auth.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecom.auth.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security filter for JWT token validation and authentication.
 *
 * Intercepts HTTP requests to extract and validate JWT tokens from the Authorization header.
 * On successful validation, sets the user in the Spring Security context.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Processes incoming HTTP requests to extract and validate JWT tokens.
	 *
	 * Looks for Bearer tokens in the Authorization header and validates them.
	 * If valid, sets the authenticated user in the Security context.
	 *
	 * @param request the HTTP request
	 * @param response the HTTP response
	 * @param filterChain the filter chain to continue processing
	 * @throws ServletException if a servlet error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			String authHeader = request.getHeader("Authorization");

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				if (jwtUtil.validateToken(token)) {
					String email = jwtUtil.extractEmail(token);

					UsernamePasswordAuthenticationToken authentication =
							new UsernamePasswordAuthenticationToken(email, null, null);
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot set user authentication: " + ex.getMessage(), ex);
		}

		filterChain.doFilter(request, response);
	}
}
