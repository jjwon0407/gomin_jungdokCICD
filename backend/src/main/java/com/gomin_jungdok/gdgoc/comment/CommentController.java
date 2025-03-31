package com.gomin_jungdok.gdgoc.comment;

import com.gomin_jungdok.gdgoc.comment.dto.CommentCreateDTO;
import com.gomin_jungdok.gdgoc.comment.dto.CommentListResponseDto;
import com.gomin_jungdok.gdgoc.post.dto.PostDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post/today")
@RequiredArgsConstructor
@Tag(name = "/api/post/today") // Swagger 그룹화
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{id}/comment")
    @Operation(summary = "댓글 작성")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentService.class)))
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody CommentCreateDTO commentCreateDTO, @PathVariable @Parameter(description = "게시물 ID", example = "1") long id) {
        commentService.createComment(commentCreateDTO, id);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("message", "댓글 작성 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/comment")
    @Operation(summary = "댓글 목록 조회")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = CommentListResponseDto.class))))
    public ResponseEntity<Map<String, Object>> Comment(@PathVariable @Parameter(description = "게시물 ID", example = "1") long id) {
        List<CommentListResponseDto> responseDto = commentService.getComments(id);

        //TODO 응답 반환하는 전용 함수 따로 만들어서 리팩토링 적용하기, 에러 핸들링 추가하기
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("message", "댓글 불러오기 성공");
        response.put("comments", responseDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
