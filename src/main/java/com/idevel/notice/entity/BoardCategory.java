package com.idevel.notice.entity;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public enum BoardCategory {

    FREE("free", "자유게시판"),
    QUESTION("question", "질문게시판"),
    INFO("info", "정보게시판"),
    POPULAR("popular", "인기글"),
    DATA("data", "자료실");

        private final String value;
    private final String displayName;

    BoardCategory(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
    public static BoardCategory from(String value) {
        return Arrays.stream(values())
                .filter(category -> category.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() ->  new ResponseStatusException( HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 입니다." ));
        }
    
}
