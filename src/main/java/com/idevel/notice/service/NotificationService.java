package com.idevel.notice.service;

import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.Comment;
import com.idevel.notice.entity.Member;
import com.idevel.notice.entity.Notification;
import com.idevel.notice.entity.NotificationType;
import com.idevel.notice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> findNotifications(
            Long memberId,
            Pageable pageable
    ) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(
                memberId,
                pageable
        );
    }

    public long countUnread(Long memberId) {
        return notificationRepository.countByMemberIdAndIsReadFalse(
                memberId
        );
    }

    @Transactional
    public Notification create(
            Member member,
            NotificationType type,
            String message,
            Board board,
            Comment comment
    ) {
        Notification notification = new Notification(
                member,
                type,
                message,
                board,
                comment
        );

        return notificationRepository.save(notification);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = findById(notificationId);

        notification.markAsRead();
    }

    public Notification findById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "알림을 찾을 수 없습니다."
                        )
                );
    }
}