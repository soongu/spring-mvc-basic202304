package com.spring.mvc.chap05.service;

import com.spring.mvc.chap05.dto.request.SignUpRequestDTO;
import com.spring.mvc.chap05.dto.response.KaKaoUserInfoDTO;
import com.spring.mvc.chap05.dto.response.LoginUserResponseDTO;
import com.spring.mvc.chap05.entity.Auth;
import com.spring.mvc.chap05.entity.Member;
import com.spring.mvc.util.LoginUtil;
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

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnsLoginService {

    private final MemberService memberService;

    public void kakaoService(String appKey, String redirectUri, String code, HttpSession session) {

        String accessToken = getKakaoAccessToken(appKey, redirectUri, code);

        KaKaoUserInfoDTO kakaoProfile = getKakaoProfile(accessToken);
        log.info("profile info: {}",kakaoProfile);

        KaKaoUserInfoDTO.KaKaoAccount kaKaoAccount = kakaoProfile.getKaKaoAccount();

        // 회원가입 여부 확인
        Member member = memberService.getMember(kaKaoAccount.getEmail());

        // 회원가입 안 됨
        if (member == null) {
            SignUpRequestDTO dto = SignUpRequestDTO.builder()
                    .account(kaKaoAccount.getEmail())
                    .name(kaKaoAccount.getProfile().getNickname())
                    .password("1234")
                    .email(kaKaoAccount.getEmail())
                    .build();

            memberService.join(dto, kaKaoAccount.getProfile().getProfileImageUrl());
        }

        // 로그인 처리
        memberService.maintainLoginState(session, kaKaoAccount.getEmail());
        session.setAttribute("loginMethod", "SNS");

    }



    private String getKakaoAccessToken(String appKey, String redirectUri, String code) {
        // 토큰 요청하기
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", appKey);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<?> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<?> responseEntity = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, Map.class);

        Map<String, Object> responseData = (Map<String, Object>) responseEntity.getBody();
        log.info("responseData: {}", responseData);

        return (String) responseData.get("access_token");
    }

    private KaKaoUserInfoDTO getKakaoProfile(String accessToken) {

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<KaKaoUserInfoDTO> responseEntity = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, KaKaoUserInfoDTO.class);

        return responseEntity.getBody();
    }
}
