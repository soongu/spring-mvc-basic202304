package com.spring.mvc.chap05.service;

import com.spring.mvc.chap05.dto.request.SignUpRequestDTO;
import com.spring.mvc.chap05.dto.sns.KakaoUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnsLoginService {

    private final MemberService memberService;

    // 카카오 로그인 처리
    public void kakaoService(Map<String, String> requestMap) {
        // 인가코드를 통해 토큰 발급받기
        String accessToken = getKakaoAccessToken(requestMap);
        log.info("token : {}", accessToken);

        // 토큰을 통해 사용자 정보 가져오기
        KakaoUserDTO dto = getKakaoUserInfo(accessToken);

        // 사용자 정보를 통해 우리 서비스 회원가입 진행
        memberService.join(
                SignUpRequestDTO.builder()
                        .account(dto.getKakaoAccount().getEmail())
                        .email(dto.getKakaoAccount().getEmail())
                        .name(dto.getKakaoAccount().getProfile().getNickname())
                        .password("9999")
                        .build(),
                dto.getKakaoAccount().getProfile().getProfileImageUrl()
        );
    }

    private KakaoUserDTO getKakaoUserInfo(String accessToken) {

        String requestUri = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        // 요청 보내기
        RestTemplate template = new RestTemplate();
        ResponseEntity<KakaoUserDTO> responseEntity = template.exchange(
                requestUri,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                KakaoUserDTO.class
        );

        // 응답 바디 읽기
        KakaoUserDTO responseData = responseEntity.getBody();
        log.info("user profile: {}", responseData);

        return responseData;

    }

    private String getKakaoAccessToken(Map<String, String> requestMap) {

        log.info("requestMap : {}", requestMap);

        // 요청 uri
        String requestUri = "https://kauth.kakao.com/oauth/token";

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", requestMap.get("appkey"));
        params.add("redirect_uri", requestMap.get("redirect"));
        params.add("code", requestMap.get("code"));

        // 카카오 서버로 post 통신
        RestTemplate template = new RestTemplate();

        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);

        // 통신을 보내면서 응답데이터를 리턴
        // param1: 요청 url
        // param2: 요청 메서드
        // param3: 헤더와 요청파라미터정보 엔터티
        // param4: 응답데이터를 받을 객체의 타입 (ex: dto, map)
        ResponseEntity<Map> responseEntity = template.exchange(requestUri, HttpMethod.POST, requestEntity, Map.class);

        // 응답데이터에서 필요한 정보를 가져오기
        Map<String, Object> responseData = (Map<String, Object>) responseEntity.getBody();
        log.info("토큰 요청 응답데이터: {}", responseData);

        return (String) responseData.get("access_token");
    }


}
