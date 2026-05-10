package it.thesis.springboot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum FileType {
    AVERAGE("average"),
    P95("p95"),
    P99("p99"),
    SOAK("soak"),
    SPIKE("spike"),
    STRESS("stress");

    private final String type;

    public static FileType findFileType(String source) {
        return Arrays.stream(FileType.values())
                .filter(ft -> ft.getType().equalsIgnoreCase(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown file type: " + source));
    }
}