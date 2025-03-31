package com.gomin_jungdok.gdgoc.vote;

import com.gomin_jungdok.gdgoc.post.dto.PostListResponseDto;
import com.gomin_jungdok.gdgoc.vote.DTO.VoteResponseDTO;
import com.gomin_jungdok.gdgoc.vote.DTO.VoteRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@Tag(name = "/api/post/{id}/vote")
    public class VoteController {
    private final VoteService voteService;

    //    @PostMapping("/1L/vote")
//    public ResponseEntity<Map<String, Object>> vote(@RequestBody VoteRequestDTO voteRequest) {
    @PostMapping("/{id}/vote")
    @Operation(summary = "고민글 선택지 선택(투표)")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = VoteResponseDTO.class)))
    public ResponseEntity<Map<String, Object>> vote(@PathVariable Long id, @RequestBody VoteRequestDTO voteRequest) {

        // postid로 post 불러와서 투표 결과 가져옴
        // 투표 결과 수정(1 or 2에 +1)
        // 투표 결과 저장 후 리턴
        VoteResponseDTO result;
        try {
            result = voteService.vote(1L, id, voteRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("message", "서버 오류 발생"));
        }

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.CREATED.value());
        response.put("message", "고민글 투표 성공");
        response.put("data", result);


        return ResponseEntity.ok(response);
    }



}
