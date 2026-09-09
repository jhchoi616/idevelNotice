package com.idevel.notice.repository;

import com.idevel.notice.entity.MemberOAuth;
import com.idevel.notice.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberOAuthRepository extends JpaRepository<MemberOAuth, Long> {

    /**
     * OAuth 로그인 시 회원 조회
     */
    Optional<MemberOAuth> findByProviderAndProviderId(
        OAuthProvider provider,
        String providerId
    );

    /**
     * 특정 회원이 해당 OAuth 계정을 이미 연결했는지 확인
     */
    boolean existsByMemberIdAndProvider(
        Long memberId,
        OAuthProvider provider
    );
}