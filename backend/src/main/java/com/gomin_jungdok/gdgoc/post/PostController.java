package com.gomin_jungdok.gdgoc.post;

import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostWriteRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Tag(name = "/api/post")
public class PostController {
    private final PostService postService;

    //고민글 작성 api
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "고민 글 작성")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "multipart/form-data",
        schema = @Schema(example = "{\"statusCode\": 201, \"message\": \"고민글 작성 완료\"}")))
    public ResponseEntity<Map<String, Object>> createPost(@ModelAttribute PostWriteRequestDto requestDto) throws IOException {
        postService.createPost(requestDto);
        
        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "고민글 작성 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //고민글 상세보기 api
    @GetMapping("/{id}")
    @Operation(summary = "고민 글 상세보기")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PostDetailResponseDto.class)))
    public ResponseEntity<Map<String, Object>> getPostDetail(@PathVariable Long id) {
        PostDetailResponseDto responseDto = postService.getPostDetail(id);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "고민글 상세보기 반환 성공");
        response.put("data", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //고민글 리스트 불러오기 api
    @GetMapping()
    @Operation(summary = "고민 글 리스트 불러오기")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = PostListResponseDto.class)))
    public ResponseEntity<Map<String, Object>> getPost(
            @Parameter(description = "가져올 게시글 개수 (기본값: 10)")
            @RequestParam(name = "size", defaultValue = "10") int size,

            @Parameter(description = "마지막 게시글 ID (페이징 처리용)")
            @RequestParam(name = "last-id", required = false) Long lastId,

            @Parameter(description = "고민글 카테고리 필터링")
            @RequestParam(name = "category", required = false) List<String> category) {

        PostListResponseDto responseDto = postService.getPosts(size, lastId, category);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "고민글 리스트 반환 성공");
        response.put("data", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 오늘의 고민 선정
    @GetMapping("/today")
    @Operation(summary = "오늘의 고민 선정")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = TodayPostsDTO.class)))
    public ResponseEntity<List<TodayPostsDTO>> getTodayPost() {
        List<TodayPostsDTO> todayPosts = postService.getTodayPost();
        return ResponseEntity.ok(todayPosts);
    }

    // 오늘의 고민 게시글 상세보기
    @GetMapping("/today/{post_id}")
    @Operation(summary = "오늘의 고민 게시글 상세보기")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = PostDetailDTO.class)))
    public ResponseEntity<PostDetailDTO> getTodayPostDetail(@PathVariable long post_id) {
        PostDetailDTO postDetail = postService.getPostDetail(post_id);
        return ResponseEntity.ok(postDetail);
    }
}
