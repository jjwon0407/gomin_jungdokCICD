package com.gomin_jungdok.gdgoc.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class kakaoLogoutRequestDto {
    private String kakaoAccessToken;
}
