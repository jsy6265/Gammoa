package com.bitc.fullstack405.securitytest.handler;

import com.bitc.fullstack405.securitytest.database.entity.BlackList;
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

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    String accessToken = extractToken(request);

    if (accessToken != null && jwtProvider.validateToken(accessToken)) {
      // accessToken Redis에 블랙리스트로 등록
      long accessTokenExpiration = jwtProvider.getExpiration(accessToken);

      // 블랙리스트 객체 생성 후 redis 저장
      BlackList blackList = BlackList.builder()
          .accessToken(accessToken)
          .expiration(accessTokenExpiration)
          .build();
      blackListRepository.save(blackList);

      destroyRefreshToken(response);
    }
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

  // 쿠키에 있는 리프레시 토큰 값 제거
  private void destroyRefreshToken(HttpServletResponse response) {

    ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
        .maxAge(0)
        .path("/")
        .secure(true)
        .sameSite("None")
        .httpOnly(true)
        .build();

    response.setHeader("Set-Cookie", cookie.toString());
  }
}
