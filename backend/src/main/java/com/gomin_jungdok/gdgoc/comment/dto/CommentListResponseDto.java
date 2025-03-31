package com.gomin_jungdok.gdgoc.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class CommentListResponseDto {

    @Schema(example = "1")
    private Long id;

    @JsonProperty("isMine")
    @Schema(example = "true")
    private boolean isMine;

    @Schema(example = "유저 프로필 이미지")
    private String profileImage;

    @Schema(example = "닉네임")
    private String nickname;

    @Schema(example = "댓글 내용")
    private String description;

    @Schema(example = "대댓글 일 경우 부모 댓글 ID")
    private Long parentCommentId;

    @Schema(example = "댓글 작성 일시")
    private Date createdAt;

    @Schema(example = "댓글 계층 순서")
    private int hierarchy;

    public CommentListResponseDto(
            Long id,
            boolean isMine,
            String profileImage,
            String nickname,
            String description,
            Long parentCommentId,
            Date createdAt,
            Integer hierarchy) {
        this.id = id;
        this.isMine = isMine;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.description = description;
        this.parentCommentId = parentCommentId;
        this.createdAt = createdAt;
        this.hierarchy = hierarchy;
    }
}