package com.idevel.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_username",
            columnNames = "username"
        ),
        @UniqueConstraint(
            name = "uk_member_email",
            columnNames = "email"
        )
    }
)
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 일반 로그인 아이디
     * OAuth 회원은 처음 생성될 때 NULL일 수 있음
     */
    @Column(unique = true)
    private String username;

    /**
     * 일반 로그인 비밀번호
     * OAuth 회원은 처음 생성될 때 NULL일 수 있음
     */
    private String password;

    /**
     * 서비스에서 사용하는 닉네임
     */
    @Column(nullable = false, length = 30)
    private String nickname;

    /**
     * 회원 이메일
     * 일반 회원 / OAuth 회원 모두 동일 이메일은 허용하지 않음
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if (this.role == null) {
            this.role = Role.USER;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Member(
        String username,
        String password,
        String nickname,
        String email
) {
    this.username = username;
    this.password = password;
    this.nickname = nickname;
    this.email = email;
}
}