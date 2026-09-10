package com.idevel.notice.repository;

import com.idevel.notice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBoardIdOrderByCreatedAtAsc(Long boardId);

    long countByBoardId(Long boardId);

    // 게시글 삭제시 댓글 전체 삭제
    void deleteAllByBoardId(Long boardId);
}