package com.gomin_jungdok.gdgoc.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum PostCategory {
    DAILY("일상"),
    LOVE("연애"),
    CAREER("진로"),
    RELATIONSHIP("인간관계"),
    WORK("사회생활"),
    ETC("기타");  // "기타" 추가

    private final String value;

    public static PostCategory fromValue(String value) {
        return Arrays.stream(PostCategory.values())
                .filter(c -> c.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리: " + value));
    }
}
