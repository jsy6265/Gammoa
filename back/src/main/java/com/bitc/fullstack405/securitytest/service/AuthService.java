package com.bitc.fullstack405.securitytest.service;

import com.bitc.fullstack405.securitytest.database.dto.RegisterRequest;
import com.bitc.fullstack405.securitytest.database.dto.UserResponse;
import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import com.bitc.fullstack405.securitytest.utill.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 회원가입, 로그인, 토큰 발급 처리 서비스
@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtProvider jwtProvider;

  // 회원 등록
  public UserResponse register(RegisterRequest req){
    if(userRepository.existsByUsername(req.getUsername())){
      throw new IllegalArgumentException("Username is already in use");
    }
    UserEntity user = new UserEntity(
        null,
        req.getUsername(),
        passwordEncoder.encode(req.getPassword()),
        req.getNickname(),
        req.getDeveloper_yn()
    );
    UserEntity saveUser = userRepository.save(user);

    // 저장한 유저 데이터 리턴
    return UserResponse.registerUser(saveUser);
  }


}
