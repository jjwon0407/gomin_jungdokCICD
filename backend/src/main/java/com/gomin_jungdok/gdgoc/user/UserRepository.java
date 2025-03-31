package com.gomin_jungdok.gdgoc.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialIdOrUid(String socialId, String uid);

    User findBySocialId(String socialId);
}
