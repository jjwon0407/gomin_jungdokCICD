package com.gomin_jungdok.gdgoc.post;

import java.util.List;

import com.gomin_jungdok.gdgoc.vote.VoteResultDTO;
import lombok.*;

@Getter
@NoArgsConstructor  // 기본 생성자 (필수)
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
public class PostDetailDTO {
    private long post_id;
    private String post_title;
    private String post_desc;
    private String category;
    private List<String> imageUrls;
    private List<VoteResultDTO> voteResults; //투표 결과 (퍼센트)
    private long totalVoteCount; ///전체 투표수
    private long commentCount; //댓글 수
}
