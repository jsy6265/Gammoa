package com.bitc.fullstack405.securitytest.handler;

import com.bitc.fullstack405.securitytest.database.entity.BlackList;
import com.bitc.fullstack405.securitytest.service.LogoutService;
import com.bitc.fullstack405.securitytest.utill.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

  @Autowired
  private RedisTemplate<String, String> redisTemplate; // 블랙리스트 저장용
  @Autowired
  private JwtProvider jwtProvider;
  @Autowired
  private BlackListRepository blackListRepository;
  @Autowired
  private LogoutService logoutService;

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    String accessToken = extractToken(request);
    logoutService.logout(accessToken,response);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }
}
