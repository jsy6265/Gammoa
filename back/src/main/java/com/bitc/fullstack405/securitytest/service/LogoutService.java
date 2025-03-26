package com.bitc.fullstack405.securitytest.service;

import com.bitc.fullstack405.securitytest.database.entity.BlackList;
import com.bitc.fullstack405.securitytest.handler.BlackListRepository;
import com.bitc.fullstack405.securitytest.utill.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

  private final JwtProvider jwtProvider;
  private final BlackListRepository blackListRepository;
  private final RedisTemplate<String, String> redisTemplate;

  @Transactional
  public void logout(String accessToken, HttpServletResponse response) {
    if (accessToken != null && jwtProvider.validateToken(accessToken)) {
      long expiration = jwtProvider.getExpiration(accessToken);

      // 액세스 토큰 블랙리스트 추가
      BlackList blackList = BlackList.builder()
          .accessToken(accessToken)
          .expiration(expiration)
          .build();
      blackListRepository.save(blackList);

      // 쿠키에서 리프레시 토큰 삭제
      destroyRefreshToken(response);
    }
  }

  // 리프레시 토큰 삭제 (쿠키에서 제거)
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