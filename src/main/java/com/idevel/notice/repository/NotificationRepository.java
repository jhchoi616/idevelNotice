package com.idevel.notice.repository;

import com.idevel.notice.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByMemberIdOrderByCreatedAtDesc(
            Long memberId,
            Pageable pageable
    );

    long countByMemberIdAndIsReadFalse(Long memberId);
}