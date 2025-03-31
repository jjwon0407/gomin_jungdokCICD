package com.gomin_jungdok.gdgoc.auth.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    @Schema(example = "1L")
    private Long id;
    @Schema(example = "user@example.com")
    private String socialId;
    @Schema(example = "KAKAO")
    private String socialType;
    @Schema(example = "익명")
    private String nickname;
    @Schema(example = "2025-03-20 00:49:18.0")
    private Date createdAt;
//    private String profileImage;


}
