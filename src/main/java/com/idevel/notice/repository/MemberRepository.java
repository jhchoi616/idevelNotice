package com.idevel.notice.repository;

import com.idevel.notice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(String username);

    Optional<Member> findByEmail(String email);
    // 중복검사
    boolean existsByUsername(String username);
    // 중복검사
    boolean existsByEmail(String email);
}