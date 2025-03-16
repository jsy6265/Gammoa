package com.bitc.fullstack405.securitytest.service;

import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import com.bitc.fullstack405.securitytest.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

// security 사용자 인증 서비스
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Autowired
  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // 1. username으로 데이터베이스에서 유저 정보 조회
    UserEntity user = userRepository.findByUsername(username);

    // 2. 조회된 UserEntity 객체를 Spring Security에서 사용할 수 있도록 변환
    return new org.springframework.security.core.userdetails.User(
        user.getUsername(),   // 로그인 ID
        user.getPassword(),   // 암호화된 비밀번호 (BCrypt로 저장된 값)
        new ArrayList<>()     // 권한 (현재는 빈 리스트)
    );
  }

}
