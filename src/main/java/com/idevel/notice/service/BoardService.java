package com.idevel.notice.service;

import com.idevel.notice.dto.BoardListDto;
import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardCategory;
import com.idevel.notice.entity.Member;
import com.idevel.notice.repository.BoardRepository;
import com.idevel.notice.repository.CommentRepository;
import com.idevel.notice.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileService boardFileService;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;


    public Page<BoardListDto> findBoards( BoardCategory category, Pageable pageable ) {

         Page<Board> boards =
            boardRepository.findByCategoryOrderByCreatedAtDesc(
                category,
                pageable
        );

        return boards.map(board -> {

        int commentCount =
                (int) commentRepository.countByBoardId(board.getId());

        return new BoardListDto(
                board,
                commentCount
        );
    });


    }

    public Page<Board> searchBoards(
            BoardCategory category,
            String keyword,
            Pageable pageable
    ) {
        return boardRepository.search(
                category,
                keyword,
                pageable
        );
    }

    public Page<Board> findPopularBoards(Pageable pageable) {
        List<BoardCategory> categories = List.of(
                BoardCategory.FREE,
                BoardCategory.QUESTION,
                BoardCategory.INFO
        );

        return boardRepository.findByCategoryInOrderByViewCountDescCreatedAtDesc(
                categories,
                pageable
        );
    }

    public Board findById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() ->
                        new IllegalArgumentException("게시글을 찾을 수 없습니다.")
                );
    }

    @Transactional
    public Board findByIdAndIncreaseViewCount(Long boardId) {
        Board board = findById(boardId);
        board.increaseViewCount();

        return board;
    }

    @Transactional 
   public Board write(
            BoardCategory category,
            String title,
            String content,
            String username,
            MultipartFile[] files
    ) {

        /*
         * 작성자 조회
         */
        Member writer = memberRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 회원입니다.")
                );


        /*
         * 게시글 생성
         */
        Board board = new Board(
                title,
                content,
                writer,
                category
        );


        /*
         * 게시글 저장
         */
        Board savedBoard = boardRepository.save(board);


        /*
         * 자료실인 경우에만 파일 처리
         */
        if (category == BoardCategory.DATA
                && files != null) {
                    boardFileService.saveFiles(
                    savedBoard,
                    files
            );
// 여기는 파일리스트 있을 때 파일리스트 돌면서 파일들 꺼내서 검증인듯
            // for (int i = 0; i < files.length; i++) { 

            //     MultipartFile file = files[i];

            //     if (file == null || file.isEmpty()) {
            //         continue;
            //     }

            //     // 파일 검증 및 저장
            //     // BoardFile 생성
            //     // boardFileRepository.save(...)
            // }
        }
        return savedBoard;
    }
}