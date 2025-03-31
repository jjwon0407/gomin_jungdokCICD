package com.gomin_jungdok.gdgoc.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gomin_jungdok.gdgoc.post.PostCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostListDetailResponseDto {

    @Schema(example = "1")
    private Long id;

    @JsonProperty("isVoted")
    @Schema(example = "true")
    private boolean isVoted;

    @JsonProperty("isMine")
    @Schema(example = "false")
    private boolean isMine;

    @JsonProperty("isAi")
    @Schema(example = "false")
    private boolean isAi;

    @JsonProperty("category")
    @Schema(example = "일상")
    private String category;

    @Schema(example = "고민제목")
    private String title;

    @Schema(example = "선택지1")
    private String option1Content;

    @Schema(example = "선택지2")
    private String option2Content;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long option1Vote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long option2Vote;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String option1Percentage;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String option2Percentage;

    public PostListDetailResponseDto(
            Long id,
            boolean isVoted,
            boolean isMine,
            boolean isAi,
            String category,
            String title,
            String option1Content,
            String option2Content,
            Long option1Vote,
            Long option2Vote,
            String option1Percentage,
            String option2Percentage) {
        this.id = id;
        this.isVoted = isVoted;
        this.isMine = isMine;
        this.isAi = isAi;
        this.category = category;
        this.title = title;
        this.option1Content = option1Content;
        this.option2Content = option2Content;
        this.option1Vote = option1Vote;
        this.option2Vote = option2Vote;
        this.option1Percentage = option1Percentage;
        this.option2Percentage = option2Percentage;
    }
}