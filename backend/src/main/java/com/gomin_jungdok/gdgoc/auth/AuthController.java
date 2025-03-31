package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.auth.Dto.KakaoLoginResponse;
import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.jwt.AuthTokens;
import com.gomin_jungdok.gdgoc.jwt.JwtUtil;
import com.gomin_jungdok.gdgoc.jwt.kakaoLogoutRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "카카오 로그인")
public class AuthController {

    private final KakaoService kakaoService;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping("/kakao/login")
    @Operation(summary = "/api/auth/kakao/login")
    @ApiResponse(responseCode = "302",
            content = @Content(schema = @Schema(example = "{\"statusCode\": 302, \"message\": \"카카오 로그인\"}")))
    public ResponseEntity<Map<String, Object>> kakaoLogin() {

        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
                // + "&scope=email";  // 이메일 권한 요청

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", kakaoAuthUrl)
                .build();
    }

    @GetMapping("/kakao/callback")
    @Operation(summary = "api/auth/kakao/callback")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = KakaoLoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "토큰 생성 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"statusCode\": 401, \"message\": \"토큰 생성 실패\"}")))
    })
    public ResponseEntity<Map<String, Object>> kakaoCallback(
            @Parameter(description = "카카오에서 전달된 인증 코드", required = true)
            @RequestParam("code") String code) {

        String accessToken = kakaoService.getKakaoAccessToken(code);
        System.out.println("accessToken = " + accessToken);

        AuthTokens tokens = kakaoService.kakaoLogin(accessToken);

        Map<String, Object> response = new HashMap<>();

        if (tokens == null)
        {
            response.put("statusCode", 401);
            response.put("message", "토큰 생성 실패");
        }
        else {

            response.put("statusCode", 200);
            response.put("message", "로그인 성공");
            response.put("jwtToken", tokens);
        }

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken) // 헤더에 액세스 토큰 추가
                .body(response); // 바디에 유저 정보 포함

    }

    @GetMapping("/kakao/user")
    @Operation(summary = "api/auth/kakao/user",
            security = @SecurityRequirement(name = "BearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))),
            @ApiResponse(responseCode = "401", description = "토큰 생성 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 401, \"message\": \"토큰 생성 실패\"}")))
    })
    public UserInfoDto getUser(@RequestHeader("Authorization") String accessToken) throws Exception {

        System.out.println("accessToken = " + accessToken);
        UserInfoDto userInfo = kakaoService.getUserInfo(accessToken);
        System.out.println("userInfo = " + userInfo);
        return userInfo;
    }


    @PostMapping(value = "/kakao/refresh")
    @Operation(summary = "api/auth/kakao/refresh")
    @ApiResponse(responseCode = "200", description = "refresh token 발급 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = KakaoLoginResponse.class)))
    public ResponseEntity<Map<String, Object>> refresh(@RequestHeader ("Authorization") String refreshToken) {
        if (refreshToken.startsWith("Bearer ")) {
            System.out.println("refreshToken = " + refreshToken);
            refreshToken = refreshToken.substring(7); // "Bearer " 제거
        }
        System.out.println("refreshToken = " + refreshToken);

        AuthTokens newTokens = jwtUtil.refreshAccessToken(refreshToken);
        String accessToken = newTokens.getAccessToken();

        Map<String, Object> response = new HashMap<>();


        if (newTokens != null)
        {
            response.put("statusCode", 200);
            response.put("message", "로그인 성공");
            response.put("newTokens", newTokens);
        }

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken) // 헤더에 액세스 토큰 추가
                .body(response); // 바디에 유저 정보 포함
    }

    @PostMapping(value = "/kakao/logout")
    @Operation(summary = "api/auth/kakao/logout")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 200, \"message\": \"로그인 성공\"}"))),
            @ApiResponse(responseCode = "401", description = "토큰 생성 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 401, \"message\": \"토큰 생성 실패\"}"))),
            @ApiResponse(responseCode = "500", description = "로그아웃 실패",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"statusCode\": 500, \"message\": \"로그아웃 실패\"}")))
    })
    public ResponseEntity<Map<String, Object>> kakaoLogout(@RequestHeader ("Authorization") @Parameter(description = "jwt 토큰의 accessToken", example = "jwtAccessToken") String jwtAccessToken, @RequestBody @Parameter(description = "카카오에서 제공하는 accessToken", example = "kakaoAccessToken") kakaoLogoutRequestDto body) {

        // 본문에서 카카오 액세스 토큰 추출
        String kakaoAccessToken = body.getKakaoAccessToken();
        System.out.println("kakaoAccessToken = " + kakaoAccessToken);

        try {
            kakaoService.kakaoLogout(kakaoAccessToken);
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 200);
            response.put("message", "로그아웃 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("statusCode", 500);
            response.put("message", "로그아웃 실패 : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
