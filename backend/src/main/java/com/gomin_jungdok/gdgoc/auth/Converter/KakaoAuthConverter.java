package com.gomin_jungdok.gdgoc.auth.Converter;

import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.user.User;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class KakaoAuthConverter {

    public static User toUser(String email) {

        return User.builder()
                // .socialId(userInfo.getId())
                .nickname("익명")
                .socialId(email)
                .socialType("KAKAO")
                .createdAt(new Date())
                .build();
    }
}
