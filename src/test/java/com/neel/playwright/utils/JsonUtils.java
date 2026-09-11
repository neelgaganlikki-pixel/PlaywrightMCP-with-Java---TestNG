package com.neel.playwright.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static void writeEmployeeData(EmployeeTestData data) throws IOException {
        Path output = Path.of("test_data", "user_data1.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, toJson(data.asJson(), 0) + System.lineSeparator(), StandardCharsets.UTF_8);
    }

    private static String toJson(Object value, int level) {
        if (value instanceof Map<?, ?> map) {
            StringBuilder json = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append(System.lineSeparator()).append("  ".repeat(level + 1));
                json.append('"').append(escape(entry.getKey().toString())).append("\": ");
                json.append(toJson(entry.getValue(), level + 1));
                first = false;
            }
            if (!map.isEmpty()) {
                json.append(System.lineSeparator()).append("  ".repeat(level));
            }
            return json.append("}").toString();
        }
        if (value instanceof Boolean) {
            return value.toString();
        }
        return "\"" + escape(value.toString()) + "\"";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}