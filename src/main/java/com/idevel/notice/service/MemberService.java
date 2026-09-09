package com.idevel.notice.service;

import com.idevel.notice.dto.MemberSignupRequest;
import com.idevel.notice.entity.Member;
import com.idevel.notice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );
    }

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("회원을 찾을 수 없습니다.")
                );
    }

    public boolean existsByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public Member signup(MemberSignupRequest request) {

        System.out.println("username = " + request.getUsername());
    System.out.println("password = " + request.getPassword());
    System.out.println("nickname = " + request.getNickname());
    System.out.println("email = " + request.getEmail());
    

        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        Member member = new Member(
                request.getUsername(),
                encodedPassword,
                request.getNickname(),
                request.getEmail()
        );

        return memberRepository.save(member);
    }
}