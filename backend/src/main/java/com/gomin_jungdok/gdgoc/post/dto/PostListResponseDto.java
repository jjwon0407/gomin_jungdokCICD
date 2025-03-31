package com.gomin_jungdok.gdgoc.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Getter
@Setter
public class PostListResponseDto {

    @Schema(description = "요청한 게시글 개수", example = "10")
    private int size;

    @Schema(description = "게시글 목록")
    private List<PostListDetailResponseDto> posts;

    public PostListResponseDto(int size, List<PostListDetailResponseDto> postListDetails) {
        this.size = size;
        this.posts = postListDetails;
    }
}