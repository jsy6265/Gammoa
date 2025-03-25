package com.bitc.fullstack405.securitytest.utill;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {
  private final String SECRET_KEY = "your-secret-key";
  private final long ACCESS_TOKEN_EXPIRATION  = 1000 * 60 * 60; // 1시간
  private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;  // 7일

  // 토큰 생성
  public String generateAccessToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  // 리프레시 토큰 생성
  public String generateRefreshToken(String username, HttpServletResponse response) {
    String refreshToken = Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();

    // 리프레시 토큰을 쿠키에 저장
    ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)  // JavaScript로 접근할 수 없도록 설정
        .secure(true)    // HTTPS에서만 쿠키 전송
        .path("/")       // 쿠키의 유효 경로 (기본적으로 전체 사이트)
        .maxAge(7 * 24 * 60 * 60) // 쿠키의 만료 시간을 설정 7일, 브라우저 닫아도 삭제 안됨
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return refreshToken;
  }

  // 토큰에서 유저 정보 추출
  public String getUsernameFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰 만료 시간 가져오기
  public long getExpiration(String token) {
    return Jwts.parser()
        .setSigningKey(SECRET_KEY)
        .parseClaimsJws(token)
        .getBody()
        .getExpiration()
        .getTime();
  }
}
