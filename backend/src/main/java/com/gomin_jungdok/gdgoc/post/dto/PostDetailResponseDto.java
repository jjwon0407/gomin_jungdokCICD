package com.gomin_jungdok.gdgoc.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gomin_jungdok.gdgoc.post.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;


@Getter
@Setter
public class PostDetailResponseDto {
    @JsonProperty("isVoted")
    private boolean isVoted;

    @JsonProperty("isMine")
    private boolean isMine;

    @JsonProperty("isAi")
    private boolean isAi;

    @JsonProperty("category")
    private String category;

    private String profileImage;
    private String writerNickname;
    private String createdAt;
    private String title;
    private String description;
    private Map<String, String> images;
    private String option1Content;
    private String option2Content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long option1Vote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long option2Vote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String option1Percentage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String option2Percentage;

    public PostDetailResponseDto(
            boolean isVoted,
            boolean isMine,
            boolean isAi,
            String category,
            String profileImage,
            String writerNickname,
            String createdAt,
            String title,
            String description,
            Map<String, String> images,
            String option1Content,
            String option2Content,
            Long option1Vote,
            Long option2Vote,
            String option1Percentage,
            String option2Percentage) {
        this.isVoted = isVoted;
        this.isMine = isMine;
        this.isAi = isAi;
        this.category = category;
        this.profileImage = profileImage;
        this.writerNickname = writerNickname;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.images = images;
        this.option1Content = option1Content;
        this.option2Content = option2Content;
        this.option1Vote = option1Vote;
        this.option2Vote = option2Vote;
        this.option1Percentage = option1Percentage;
        this.option2Percentage = option2Percentage;
    }
}