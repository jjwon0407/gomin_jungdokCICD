package com.gomin_jungdok.gdgoc.post.dto;

import com.gomin_jungdok.gdgoc.post.PostCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PostWriteRequestDto {
    private String title;
    private String description;
    private String category;
    private String option1;
    private String option2;
    private List<MultipartFile> images;
}