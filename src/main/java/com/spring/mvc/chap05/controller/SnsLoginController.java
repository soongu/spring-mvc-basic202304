package com.spring.mvc.chap05.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class SnsLoginController {

    @Value("${sns.kakao.app-key}")
    // 카카오 app key
    private String kakaoAppKey;

    @Value("${sns.kakao.redirect-uri}")
    // 카카오 redirect uri
    private String kakaoRedirectURI;

    // 카카오 인가 코드 발급 요청
    @GetMapping("/kakao/login")
    public String kakaoLogin() {
        String requestUri = String.format("https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code", kakaoAppKey, kakaoRedirectURI);
        return "redirect:" + requestUri;
    }
}
