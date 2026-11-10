package sopt.collaboration.aladin.domain.book.entity;

import java.util.Arrays;

public enum Grade {
    BEST(BookCondition.EXCELLENT),
    GOOD(BookCondition.GOOD),
    NORMAL(BookCondition.FAIR);

    private final BookCondition condition;

    Grade(BookCondition condition) {
        this.condition = condition;
    }

    public BookCondition toCondition() {
        return condition;
    }

    public static Grade from(BookCondition condition) {
        return Arrays.stream(values())
                .filter(grade -> grade.condition == condition)
                .findFirst()
                .orElseThrow();
    }
}
