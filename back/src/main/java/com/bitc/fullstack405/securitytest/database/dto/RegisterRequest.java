package com.bitc.fullstack405.securitytest.database.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
  // 회원 가입 시 필요한 정보
  private  String username;
  private String password;
  private String nickname;

  private String developer_yn;
}
