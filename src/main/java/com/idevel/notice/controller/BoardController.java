package com.idevel.notice.controller;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.idevel.notice.dto.BoardListDto;
import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardCategory;
import com.idevel.notice.entity.Member;
import com.idevel.notice.repository.BoardFileRepository;
import com.idevel.notice.security.CustomUserDetails;
import com.idevel.notice.service.BoardService;
import com.idevel.notice.service.CommentService;



@Controller 
@RequestMapping("/board")
@RequiredArgsConstructor 
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final BoardFileRepository boardFileRepository;
    @GetMapping("/write")
    public String boardWrite() {
        return "board/write";
    }

    @GetMapping("/{category}")
    public String board(
        @PathVariable("category") String category,
         @PageableDefault ( size = 10, sort = "createdAt", direction = Sort.Direction.DESC ) Pageable pageable, 
        Model model
    ) {
        
        BoardCategory boardCategory = BoardCategory.from(category);
        Page<BoardListDto> boardPage =boardService.findBoards(boardCategory, pageable);
        System.out.println("조회 카테고리 = "+category);
        System.out.println("조회 카테고리2 = "+boardCategory);
        model.addAttribute("category", boardCategory);
        model.addAttribute("boards",boardPage.getContent());
        model.addAttribute("page",boardPage);


        return "board/list";
    }

    @GetMapping("/{category}/{id}")
    public String detail(
            @PathVariable("category") String category,
            @PathVariable("id") Long id,
            Model model
    ) {

        BoardCategory boardCategory;

    try {
        boardCategory = BoardCategory.valueOf(category.toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("존재하지 않는 게시판입니다.");
    }

    Board board = boardService.findByIdAndIncreaseViewCount(id);

    if (board.getCategory() != boardCategory) {
        throw new IllegalArgumentException("잘못된 게시글 주소입니다.");
    }

    model.addAttribute("board", board);
    model.addAttribute("comments", commentService.findByBoardId(id));

    model.addAttribute(
            "files",
            boardFileRepository.findByBoardIdOrderBySortOrderAsc(id)
    );


        return "board/detail";
    }

    @PostMapping("/write")
    public String write(
            @RequestParam("category")
            BoardCategory category,

            @RequestParam("title")
            String title,

            @RequestParam("content")
            String content,

            @RequestParam(value = "files", required = false)
            MultipartFile[] files,

            Authentication authentication
    ) {

        Board board = boardService.write(
                category,
                title,
                content,
                authentication.getName(),
                files
        );
        System.out.println(board);

        return "redirect:/board/" + category.name().toLowerCase();
    }
    

@PostMapping("/{category}/{id}/comment")
public String writeComment(
        @PathVariable("category") String category,
        @PathVariable("id") Long id,
        @RequestParam("content") String content,
        Authentication authentication
) {
CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
Member member = userDetails.getMember();

    System.out.println("시큐리티 내용 확인용도 : "+member.getEmail());
    Board board = boardService.findById(id);
    commentService.create(
            board,
            member,
            content,
            false
    );

    return "redirect:/board/" + category.toLowerCase() + "/" + id;
}


@GetMapping("/{category}/{id}/edit")
public String editForm(
        @PathVariable("category") String category,
        @PathVariable("id") Long id,
        Authentication authentication,
        Model model
) {
    Board board = boardService.findById(id);

    if (!board.getCategory().name().equalsIgnoreCase(category)) {
        throw new IllegalArgumentException("잘못된 게시글 주소입니다.");
    }

    if (!board.getWriter().getUsername()
            .equals(authentication.getName())) {
        throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    model.addAttribute("board", board);
    model.addAttribute("category", board.getCategory().name());
    // 자료실인 경우 첨부파일도 넣기
    if (board.getCategory() == BoardCategory.DATA) {
        model.addAttribute(
            "files",
            boardFileRepository.findByBoardIdOrderBySortOrderAsc(id)
        );
    }
    return "board/write";
}

@PostMapping("/{id}/edit")
public String update(
        @PathVariable("id") Long id,
        @RequestParam("category") BoardCategory category,
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value = "files", required = false) MultipartFile[] files,
        @RequestParam(value = "deletedFileIds", required = false) List<Long> deletedFileIds,
        Authentication authentication
) {

    boardService.update(
            category,
            id,
            title,
            content,
            authentication.getName(),
            files,
            deletedFileIds
    );

    return "redirect:/board/"
            + category.getValue()
            + "/"
            + id;
}


@PostMapping("/{category}/{id}/delete")
public String delete(
        @PathVariable("category") String category,
        @PathVariable("id") Long id,
        Authentication authentication,
        RedirectAttributes redirectAttributes
) {
try{
System.out.println("게시글 삭제까지 옴?");
System.out.println("삭제에 사용하는 ID : " + id);
System.out.println("삭제에 사용하는 유저 name : "+authentication.getName());
    boardService.delete(
        id,
        authentication.getName()
    );
}catch(Exception e){
    System.out.println("게시글 삭제 시 예외임 : "+e.getMessage());
    redirectAttributes.addFlashAttribute("error", e.getMessage());
}

    return "redirect:/board/"
            + category.toLowerCase();
}

// 댓글 수정
@PostMapping("/{category}/{id}/comment/{commentId}/edit")
public String updateComment(
        @PathVariable("category") String category,
        @PathVariable("id") Long id,
        @PathVariable("commentId") Long commentId,
        @RequestParam("content") String content,
        Authentication authentication,RedirectAttributes redirectAttributes
) {
    // System.out.println("댓글 수정란까지 옴? 어디를 못 찾은거지 : "+content);
    
    try {
        commentService.updateComment(
            commentId,
            content,
            authentication.getName()
    );
    } catch (Exception e) {
        // 여기서 예외메시지 찍히는거 찾기
        System.out.println("댓글 수정 시 예외임 : "+e.getMessage());
        redirectAttributes.addFlashAttribute("error", e.getMessage());
    }

    return "redirect:/board/"
            + category.toLowerCase()
            + "/"
            + id;
}

// 댓글 삭제
@PostMapping("/{category}/{id}/comment/{commentId}/delete")
public String deleteComment(
        @PathVariable("category") String category,
        @PathVariable("id") Long id,
        @PathVariable("commentId") Long commentId,
        Authentication authentication
) {
    commentService.deleteComment(
            commentId,
            authentication.getName()
    );

    return "redirect:/board/"
            + category.toLowerCase()
            + "/"
            + id;
}

}
