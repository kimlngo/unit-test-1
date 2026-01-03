package com.kimlngo.springmvc.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum GradeType {
    MATH("math"),
    SCIENCE("science"),
    HISTORY("history");

    private final String value;
    private static final Map<String, GradeType> GRADE_TYPE_MAP;

    static {
        Map<String, GradeType> map = new HashMap<>();
        for (var type : GradeType.values()) {
            map.put(type.value, type);
        }
        GRADE_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    GradeType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static GradeType findGradeType(String val) {
        return GRADE_TYPE_MAP.get(val);
    }
}
