package com.idevel.notice.service;

import com.idevel.notice.entity.MemberOAuth;
import com.idevel.notice.entity.OAuthProvider;
import com.idevel.notice.repository.MemberOAuthRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberOAuthService {

    private final MemberOAuthRepository memberOAuthRepository;

    public MemberOAuth findByProviderAndProviderId(
            OAuthProvider provider,
            String providerId
    ) {
        return memberOAuthRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                    HttpStatus.NOT_FOUND,"OAuth 계정을 찾을 수 없습니다.")
                );
    }

    public boolean existsByMemberIdAndProvider(
            Long memberId,
            OAuthProvider provider
    ) {
        return memberOAuthRepository
                .existsByMemberIdAndProvider(memberId, provider);
    }
}