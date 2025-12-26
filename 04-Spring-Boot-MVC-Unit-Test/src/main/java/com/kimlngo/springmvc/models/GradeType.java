package com.kimlngo.springmvc.models;

public enum GradeType {
    MATH("math"),
    SCIENCE("science"),
    HISTORY("history");

    private final String value;

    GradeType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
