package com.spring.mvc.chap05.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter
@ToString
@NoArgsConstructor
public class KaKaoUserInfoDTO {

    private long id;

    @JsonProperty("kakao_account")
    private KaKaoAccount kaKaoAccount;

    @Setter @Getter
    @ToString
    public static class KaKaoAccount {

        private Profile profile;
        private String email;

        @Setter @Getter
        @ToString
        public static class Profile {

            private String nickname;
            @JsonProperty("profile_image_url")
            private String profileImageUrl;
        }
    }
}
