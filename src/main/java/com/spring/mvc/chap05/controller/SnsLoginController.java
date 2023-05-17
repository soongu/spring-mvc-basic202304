package com.spring.mvc.chap05.controller;

import com.spring.mvc.chap05.service.SnsLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SnsLoginController {

    private final SnsLoginService snsLoginService;

    @Value("${sns.kakao.app-key}")
    private String kakaoAppKey;

    @Value("${sns.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @GetMapping("/kakao/login")
    public String kakaoLogin() {
        String requestUri = String.format("https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code", kakaoAppKey, kakaoRedirectUri);
        return "redirect:" + requestUri;
    }

    @GetMapping("/sns/kakao")
    public String snsKakao(String code, HttpSession session) {
        log.info("auth code: {}", code);
        snsLoginService.kakaoService(kakaoAppKey, kakaoRedirectUri, code, session);
        return "redirect:/";
    }
}
