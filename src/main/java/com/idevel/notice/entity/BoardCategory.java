package com.idevel.notice.entity;

import java.util.Arrays;

public enum BoardCategory {

    FREE("free", "자유게시판"),
    QUESTION("question", "질문게시판"),
    INFO("info", "정보게시판"),
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
        }
    
}
