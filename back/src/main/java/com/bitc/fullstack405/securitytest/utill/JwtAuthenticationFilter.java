package com.bitc.fullstack405.securitytest.utill;

import com.bitc.fullstack405.securitytest.handler.BlackListRepository;
import com.bitc.fullstack405.securitytest.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final UserDetailsServiceImpl userDetailsService;
  private final BlackListRepository blackListRepository;


  @Autowired
  public JwtAuthenticationFilter(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService, BlackListRepository blackListRepository) {
    this.jwtProvider = jwtProvider;
    this.userDetailsService = userDetailsService;
    this.blackListRepository = blackListRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String token = resolveToken(request);

    // 블랙리스트 확인
    if (blackListRepository.findByAccessToken(token).isPresent()) {
      System.out.println("블랙리스트된 토큰입니다! 요청을 차단합니다.");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 반환
      response.getWriter().write("This token is blacklisted.");
      return;
    }


    // 토큰 유효하면 security context에 저장
    if (token != null && jwtProvider.validateToken(token)) {
      String username = jwtProvider.getUsernameFromToken(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
