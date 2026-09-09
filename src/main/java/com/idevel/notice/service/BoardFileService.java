package com.idevel.notice.service;

import com.idevel.notice.entity.Board;
import com.idevel.notice.entity.BoardFile;
import com.idevel.notice.repository.BoardFileRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardFileService {

    private final BoardFileRepository boardFileRepository;

     private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_TOTAL_SIZE = 20 * 1024 * 1024;

    private static final String[] ALLOWED_TYPES = {
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    };

    /*
     * 서버에 실제 파일이 저장될 위치
     *
     * EC2에서도 프로젝트 외부의 uploads 디렉터리를 사용하는 방식
     */
    // private final Path uploadPath = Paths.get("uploads", "board");
    @Value ("${file.upload.path}")
    private String uploadPath;

    /**
     * 게시글의 파일들을 저장
     */
    public void saveFiles(
            Board board,
            MultipartFile[] files
    ) {
    Path uploadDirectory = Paths.get(uploadPath);
        if (files == null || files.length == 0) {
            return;
        }

        long totalSize = 0;

        /*
         * 전체 용량 검증
         */
        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException(
                        "파일당 최대 10MB까지 업로드할 수 있습니다."
                );
            }

            totalSize += file.getSize();
        }

        if (totalSize > MAX_TOTAL_SIZE) {
            throw new IllegalArgumentException(
                    "전체 파일 용량은 20MB를 초과할 수 없습니다."
            );
        }


        /*
         * 업로드 디렉터리 생성
         */
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "파일 저장 폴더를 생성할 수 없습니다.",
                    e
            );
        }


        /*
         * 실제 파일 저장
         */
        int sortOrder = 0;

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                continue;
            }

            validateImage(file);

            String originalFileName =
                    file.getOriginalFilename();

            String extension =
                    getExtension(originalFileName);

            String storedFileName =
                    UUID.randomUUID() + extension;

            Path targetPath = uploadDirectory.resolve(storedFileName);

            try {

                file.transferTo(targetPath);

            } catch (IOException e) {

                throw new RuntimeException(
                        "파일 저장에 실패했습니다.",
                        e
                );
            }


            /*
             * DB에 파일 정보 저장
             */
            BoardFile boardFile = new BoardFile(
                    board,
                    originalFileName,
                    storedFileName,
                    targetPath.toString(),
                    file.getSize(),
                    file.getContentType(),
                    sortOrder++
            );

            boardFileRepository.save(boardFile);
        }
    }


    /**
     * 이미지 파일 검증
     */
    private void validateImage(MultipartFile file) {

        String contentType =
                file.getContentType();

        boolean allowed = false;

        for (String type : ALLOWED_TYPES) {

            if (type.equalsIgnoreCase(contentType)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new IllegalArgumentException(
                    "JPG, JPEG, PNG, GIF, WEBP 이미지만 업로드할 수 있습니다."
            );
        }
    }


    /**
     * 확장자 추출
     */
    private String getExtension(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "파일 이름이 올바르지 않습니다."
            );
        }

        int index = fileName.lastIndexOf('.');

        if (index == -1) {
            throw new IllegalArgumentException(
                    "확장자가 없는 파일은 업로드할 수 없습니다."
            );
        }

        return fileName.substring(index).toLowerCase();
    }



    public List<BoardFile> findByBoardId(Long boardId) {
        return boardFileRepository.findByBoardIdOrderBySortOrderAsc(boardId);
    }

    public BoardFile findById(Long fileId) {
        return boardFileRepository.findById(fileId)
                .orElseThrow(() ->
                        new IllegalArgumentException("첨부파일을 찾을 수 없습니다.")
                );
    }

    @Transactional
    public void delete(Long fileId) {
        BoardFile boardFile = findById(fileId);

        boardFileRepository.delete(boardFile);
    }
}