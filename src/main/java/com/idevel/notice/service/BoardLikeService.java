package com.idevel.notice.service;

import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardLike;
import com.idevel.notice.entity.Member;
import com.idevel.notice.repository.BoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardLikeService {

    private final BoardLikeRepository boardLikeRepository;

    public boolean isLiked(Long boardId, Long memberId) {
        return boardLikeRepository.existsByBoardIdAndMemberId(
                boardId,
                memberId
        );
    }

    public long countLikes(Long boardId) {
        return boardLikeRepository.countByBoardId(boardId);
    }

    @Transactional
    public void like(Board board, Member member) {

        if (boardLikeRepository.existsByBoardIdAndMemberId(
                board.getId(),
                member.getId()
        )) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        BoardLike boardLike = new BoardLike(
                board,
                member
        );

        boardLikeRepository.save(boardLike);
    }

    @Transactional
    public void unlike(Board board, Member member) {

        BoardLike boardLike = boardLikeRepository
                .findByBoardIdAndMemberId(
                        board.getId(),
                        member.getId()
                )
                .orElseThrow(() ->
                        new IllegalStateException("좋아요를 누르지 않았습니다.")
                );

        boardLikeRepository.delete(boardLike);
    }
}