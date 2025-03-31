package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.user.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KakaoRepository extends JpaRepository<User, Long> {
    User findByEmail(@Email(message = "유효한 이메일 형식이 아닙니다.") String email);

    @Query("SELECT p FROM User p WHERE p.id = :userId")
    Optional<User> findById(Long userId);

    User findBySocialId(@Email(message = "유효한 이메일 형식이 아닙니다.") String email);
}
