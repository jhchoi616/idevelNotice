package com.idevel.notice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "member_oauth",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_member_oauth_provider_provider_id",
            columnNames = {"provider", "provider_id"}
        )
    }
)
@Getter
@NoArgsConstructor
public class MemberOAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 연결된 서비스 회원
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "member_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_member_oauth_member")
    )
    private Member member;

    /**
     * OAuth 제공자
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    /**
     * OAuth 제공자가 발급한 회원 고유 식별값
     */
    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public MemberOAuth(
            Member member,
            OAuthProvider provider,
            String providerId
    ) {
        this.member = member;
        this.provider = provider;
        this.providerId = providerId;
    }
}