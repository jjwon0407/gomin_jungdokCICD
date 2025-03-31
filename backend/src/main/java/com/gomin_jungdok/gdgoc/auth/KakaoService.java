package com.gomin_jungdok.gdgoc.auth;

import com.gomin_jungdok.gdgoc.auth.Dto.KakaoTokenResponseDto;
import com.gomin_jungdok.gdgoc.auth.Dto.UserInfoDto;
import com.gomin_jungdok.gdgoc.jwt.AuthTokens;
import com.gomin_jungdok.gdgoc.jwt.AuthTokensGenerator;
import com.gomin_jungdok.gdgoc.jwt.JwtUtil;
import com.gomin_jungdok.gdgoc.user.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.MultiValueMap;
import java.util.Map;

import static com.gomin_jungdok.gdgoc.auth.Converter.KakaoAuthConverter.toUser;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final JwtUtil jwtTokenProvider;

    @Value("${kakao.client}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final KakaoRepository kakaoRepository;
    private final AuthTokensGenerator authTokensGenerator;


    // code를 getKakaoAccessToken 에 전달 -> accessToken받아오기

    public AuthTokens kakaoLogin(String accessToken) {

        UserInfoDto userInfo = getKakaoUserInfo(accessToken);

        Long userId = userInfo.getId();

        AuthTokens authTokens = authTokensGenerator.generate(userId.toString());

        return authTokens;
    }

    public String getKakaoAccessToken(String code) {
        String reqURL = "https://kauth.kakao.com/oauth/token";

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("인증 코드가 없습니다.");
        }

        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        // HttpBody 데이터를 받는 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            System.out.println("code = " + code);
            // Http 요청하기 - Post 방식으로 - 그리고 response 변수의 응답 받음.
            ResponseEntity<KakaoTokenResponseDto> response = rt.exchange(reqURL, HttpMethod.POST, kakaoTokenRequest, KakaoTokenResponseDto.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getAccessToken();
            } else {
                throw new RuntimeException("Failed to get Kakao access token.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while requesting Kakao access token: " + e.getMessage(), e);
        }
    }

    public UserInfoDto getKakaoUserInfo(String accessToken) {
        // 유저 정보 반환, if 신규 유저면 가입
        System.out.println("kakaoAccessToken = " + accessToken);

        // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap 타입으로 선언
        UserInfoDto userInfo = new UserInfoDto();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        RestTemplate rt = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 호출
        ResponseEntity<String> response = rt.exchange(reqURL, HttpMethod.GET, entity, String.class);
        String result = response.getBody();
        if (response.getStatusCode() == HttpStatus.OK && result != null) {
            JsonElement element = JsonParser.parseString(result);
            String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();

            System.out.println("email = " + email);

            User user = kakaoRepository.findBySocialId(email);

            if (user == null) {
                System.out.println("user is null");
                user = toUser(email);
                kakaoRepository.save(user);
            }

            userInfo.setId(user.getId());
            userInfo.setCreatedAt(user.getCreatedAt());
            userInfo.setNickname(user.getNickname());
            userInfo.setSocialType(user.getSocialType());
        }

        return userInfo;
    }

    public void kakaoLogout(String accessToken) {

        String reqURL = "https://kapi.kakao.com/v1/user/logout";

        RestTemplate rt = new RestTemplate();

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        System.out.println("headers = " + headers);
        System.out.println("entity = " + entity);


        try {
            System.out.println("POST 요청");
            ResponseEntity<Map> responseEntity = rt.exchange(reqURL, HttpMethod.POST, entity, Map.class);
            System.out.println("responseEntity = " + responseEntity);

            System.out.println("응답 코드 및 응답 본문 처리");
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("성공적으로 로그아웃 처리");
                Map<String, Object> responseBody = responseEntity.getBody();
                System.out.println("필요한 작업을 여기에 처리");
                System.out.println("로그아웃 성공: " + responseBody);
            } else {
                throw new RuntimeException("로그아웃 실패: " + responseEntity.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("카카오 로그아웃 요청 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public String createFirebaseCustomToken(UserInfoDto userInfo) throws Exception {

        UserRecord userRecord;
        String email = userInfo.getSocialId();

        // 이메일이 없으면 예외 처리
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일이 필요합니다.");
        }

        // 1. 사용자 정보로 파이어 베이스 유저정보 update, 사용자 정보가 있다면 userRecord에 유저 정보가 담긴다.
        try {
            // 이메일을 기반으로 Firebase 사용자 정보 가져오기
            userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            // 1-2. 사용자 정보가 없다면 > catch 구분에서 createUser로 사용자를 생성한다.
        } catch (FirebaseAuthException e) {
            // 사용자가 존재하지 않으면 새로 생성 (uid는 자동 생성됨)
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setEmailVerified(true) // 이메일 인증 여부 (필요하면 true)
                    .setDisplayName(userInfo.getNickname());

            userRecord = FirebaseAuth.getInstance().createUser(createRequest);

        }
        // 3. Firebase에서 생성된 uid를 가져옴
        String firebaseUid = userRecord.getUid();

        // 4. 해당 사용자의 DB 정보 업데이트 (uid 저장)
        User user = kakaoRepository.findByEmail(email);
        if (user != null) {
            user.setUid(firebaseUid); // Firebase의 uid 저장
            kakaoRepository.save(user);
        }

        // 5. Firebase Custom Token 생성 후 리턴
        return FirebaseAuth.getInstance().createCustomToken(firebaseUid);

    }

    public UserInfoDto getUserInfo(String accessToken) {
        System.out.println("accessToken = " + accessToken);
        accessToken = accessToken.substring(7);

        Long userId = Long.parseLong(jwtTokenProvider.validateAndGetUserId(accessToken));
        UserInfoDto userInfo = new UserInfoDto();
        User user = kakaoRepository.findById(userId).get();

        userInfo.setId(userId);
        userInfo.setCreatedAt(user.getCreatedAt());
        userInfo.setNickname(user.getNickname());
        userInfo.setSocialType(user.getSocialType());
        userInfo.setSocialId(user.getSocialId());

        return userInfo;


    }
}
