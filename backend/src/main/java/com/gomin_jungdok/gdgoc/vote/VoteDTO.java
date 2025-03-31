package com.gomin_jungdok.gdgoc.vote;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor  // 기본 생성자 (필수)
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
public class VoteDTO {
    private int option_id;
    private int post_id;
    private int vote_user;
}
