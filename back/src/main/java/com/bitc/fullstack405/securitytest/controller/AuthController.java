package com.bitc.fullstack405.securitytest.controller;

import com.bitc.fullstack405.securitytest.database.dto.*;
import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import com.bitc.fullstack405.securitytest.service.AuthService;
import com.bitc.fullstack405.securitytest.service.UserService;
import com.bitc.fullstack405.securitytest.utill.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;
  private final UserService userService;
  
  // 로그인
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, HttpServletResponse response) {
    try{
    Authentication authentication = authenticationManager.authenticate(
        // 사용자 데이터 시큐리티에서 검증하기 위해 토큰 생성해서 authenticationManager 한테 보냄
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    // 있으면 컨텍스트에 저장
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // 성공 시 토큰 + 유저 id 리턴
    UserEntity user = userRepository.findByUsername(request.getUsername());
    // 액세스 토큰, 리프레시 토큰 발급
    String accessToken = jwtProvider.generateAccessToken(user.getUsername());
    String refreshToken = jwtProvider.generateRefreshToken(user.getUsername(),response);

    return ResponseEntity.ok(new AuthResponse(accessToken,refreshToken,user.getUsername()));
    }catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new AuthResponse(null, null,"Invalid username or password"));
    }
  }
  // 리프레시 토큰으로 액세스 토큰 재발급
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@RequestBody String refreshToken) {
    try {
      // 리프레시 토큰 검증
      if (!jwtProvider.validateToken(refreshToken)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new AuthResponse(null, null,"Invalid refresh token"));
      }

      // 리프레시 토큰에서 사용자 이름 가져오기
      String username = jwtProvider.getUsernameFromToken(refreshToken);

      // 새로운 액세스 토큰 발급
      String accessToken = jwtProvider.generateAccessToken(username);

      // 응답
      return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, username));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new AuthResponse(null, null,"Error during token refresh"));
    }
  }
  
  // 회원가입
  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest req) {
    UserResponse res = authService.register(req);
    return ResponseEntity.ok(res);
  }

  // 회원정보 수정
  @PutMapping("/{id}")
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest req) throws BadRequestException {
    UserResponse updateUser = userService.updateUser(id, req);
    return ResponseEntity.ok(updateUser);
  }

  // 회원 탈퇴

}
