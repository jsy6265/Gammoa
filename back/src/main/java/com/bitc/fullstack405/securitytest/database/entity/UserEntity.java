package com.bitc.fullstack405.securitytest.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="GAMMOA_MEMBER_MAS")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="MEMBER_ID",nullable = false)
  private Long id;

  @Column(name="MEMBER_LOGIN_ID", nullable = false, unique = true)
  private String username;

  @Column(name="MEMBER_LOGIN_PASS",nullable = false)
  private String password;

  @Column(name="MEMBER_NICKNAME",nullable = false)
  private String nickname;

  @Column(name="DEVELOPER_YN", nullable = false)
  @ColumnDefault("'N'")
  private String developer_yn;

  @Column(name="CREATE_DATE",nullable = false, updatable = false)
  private LocalDateTime create_date;

  @Column(name="UPDATE_DATE",nullable = false)
  private LocalDateTime update_date;

  public UserEntity(Long id, String username, String password, String nickname, String developer_yn) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.developer_yn = developer_yn;
  }

  // 생성될 때 현재 시간 저장
  @PrePersist
  protected void onCreate(){
    this.create_date = LocalDateTime.now();
    this.update_date = LocalDateTime.now();
  }

  // 업데이트 될 떄 현재 시간 갱신
  @PreUpdate
  protected void onUpdate(){
    this.update_date = LocalDateTime.now();
  }

}
