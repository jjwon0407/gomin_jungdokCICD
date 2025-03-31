package com.gomin_jungdok.gdgoc.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    /*private final UserRepository userRepository;

    @Transactional
    public User saveUser(String id, String password, String nickname, String googleEmail, String socialId, String socialType) {
        User user = User.builder()
                .id(id)
                .password(password)
                .nickname(nickname)
                .googleEmail(googleEmail)
                .socialId(socialId)
                .socialType(socialType)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }*/
}
