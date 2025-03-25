package com.bitc.fullstack405.securitytest.handler;

import com.bitc.fullstack405.securitytest.database.entity.BlackList;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlackListRepository extends CrudRepository<BlackList, Long> {
  Optional<BlackList> findByAccessToken(String accessToken);
}
