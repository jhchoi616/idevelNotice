package com.idevel.notice.service;

import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.Comment;
import com.idevel.notice.entity.Member;
import com.idevel.notice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
    }

    public long countByBoardId(Long boardId) {
        return commentRepository.countByBoardId(boardId);
    }

    @Transactional
    public Comment create(
            Board board,
            Member member,
            String content,
            boolean secret
    ) {
        Comment comment = new Comment(
                board,
                member,
                content,
                secret
        );

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment createReply(
            Board board,
            Member member,
            Long parentId,
            String content,
            boolean secret
    ) {
        Comment parent = findById(parentId);

        // 다른 게시글의 댓글을 부모로 지정하는 것 방지
        if (!parent.getBoard().getId().equals(board.getId())) {
            throw new IllegalArgumentException(
                    "해당 게시글의 댓글이 아닙니다."
            );
        }

        // 대댓글의 대댓글 방지
        if (parent.getParent() != null) {
            throw new IllegalStateException(
                    "대댓글에는 다시 답글을 작성할 수 없습니다."
            );
        }

        Comment reply = new Comment(
                board,
                member,
                parent,
                content,
                secret
        );

        return commentRepository.save(reply);
    }

    public Comment findById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "댓글을 찾을 수 없습니다."
                        )
                );
    }
// 댓글 삭제
    @Transactional
    public void deleteComment(
        Long commentId,
        String username
) {
    Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() ->
                    new IllegalArgumentException("존재하지 않는 댓글입니다.")
            );

    // 작성자 확인
    if (!comment.getMember().getUsername().equals(username)) {
        throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
    }

    commentRepository.delete(comment);
}

// 업데이트 코멘트
        @Transactional 
        public void updateComment(
                Long commentId,
                String content,
                String username
        ) {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("존재하지 않는 댓글입니다.")
                    );
                    

            // 작성자 확인
            if (!comment.getMember().getUsername().equals(username)) {
                throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
            }

            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
            }

            if (content.length() > 300) {
                throw new IllegalArgumentException(
                        "댓글은 300자까지 입력할 수 있습니다."
                );
            }

            comment.update(content);
        }

}