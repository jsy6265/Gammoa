package com.bitc.fullstack405.securitytest.database.repository;

import com.bitc.fullstack405.securitytest.database.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  UserEntity findByUsername(String username);

  boolean existsByUsername(String username);
}
