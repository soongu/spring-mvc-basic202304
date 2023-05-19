package com.spring.mvc.chap05.dto.request;

import com.spring.mvc.chap05.entity.LoginMethod;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpRequestDTO {

    @NotBlank
    @Size(min = 4, max = 14)
    private String account;
    @NotBlank
    private String password;
    @NotBlank
    @Size(min = 2, max = 6)
    private String name;
    @NotBlank
    @Email
    private String email;

    private MultipartFile profileImage;
    private LoginMethod loginMethod;
}
