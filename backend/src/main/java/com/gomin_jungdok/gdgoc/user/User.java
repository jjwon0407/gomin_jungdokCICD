package com.gomin_jungdok.gdgoc.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Date; 

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "id")
    private String userId;

    @Column(name = "password")
    private String password;

    //firebase 사용 시 필요한 변수값
    @Column(name = "uid", nullable = false)
    private String uid;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Column(nullable = false)
    private String nickname;

    @Column(name = "google_email")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "social_type", nullable = false)
    private String socialType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @Column(name = "profile_image")
    private String profileImage;
}
