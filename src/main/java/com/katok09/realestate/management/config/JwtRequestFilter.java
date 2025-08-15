package com.katok09.realestate.management.config;

import com.katok09.realestate.management.service.UserDetailsServiceImpl;
import com.katok09.realestate.management.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  private final UserDetailsServiceImpl userDetailsService;
  private final JwtUtil jwtUtil;

  public JwtRequestFilter(UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader("Authorization");

    String username = null;
    String jwtToken = null;

    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      try {
        username = jwtUtil.getUsernameFromToken(jwtToken);
      } catch (RuntimeException e) {
        logger.warn("JWTトークンの解析に失敗しました: " + e.getMessage());
      }
    } else {
      logger.debug("JWTトークンが存在しないか、Bearer形式ではありません");
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

      if (jwtUtil.validateToken(jwtToken, userDetails)) {

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        logger.debug("ユーザー認証成功: " + username);
      } else {
        logger.warn("JWTトークン検証に失敗しました: " + username);
      }
    }
    chain.doFilter(request, response);

  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

    String path = request.getRequestURI();

    return path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/guest-login") ||
        path.startsWith("/api/auth/register") ||
        path.startsWith("/swagger-ui/") ||
        path.startsWith("/v3/api-docs/") ||
        path.startsWith("/h2-console/");
  }

}
