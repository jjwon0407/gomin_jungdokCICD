package com.gomin_jungdok.gdgoc.user.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;   //user의 id(1, 2, 3)
    private String nickname;
    private String email;
}
