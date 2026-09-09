package org.example.smart_hub.mcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SafeSqlValidator {

    private static final List<String> FORBIDDEN_KEYWORDS = Arrays.asList(
            "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "TRUNCATE",
            "CREATE", "GRANT", "REVOKE", "EXEC", "EXECUTE", "SHUTDOWN",
            "REPLACE", "LOCK", "CALL"
    );

    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LIMIT_PATTERN = Pattern.compile("\\bLIMIT\\s+(\\d+)", Pattern.CASE_INSENSITIVE);

    public record SqlValidationResult(
            boolean isSafe,
            String sanitizedQuery,
            String errorMessage
    ) {}

    public SqlValidationResult validateAndSanitize(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return new SqlValidationResult(false, null, "Câu lệnh SQL không được để trống.");
        }

        String trimmedSql = sql.trim();

        // 1. Must be a SELECT query
        if (!SELECT_PATTERN.matcher(trimmedSql).find()) {
            return new SqlValidationResult(false, null, "Quy định an toàn: Chỉ cho phép thực thi câu lệnh truy vấn dữ liệu (SELECT).");
        }

        // 2. Must not contain multiple statements separated by semicolon with dangerous commands
        String upperSql = trimmedSql.toUpperCase();
        for (String keyword : FORBIDDEN_KEYWORDS) {
            // Regex to check whole word
            String regex = "\\b" + keyword + "\\b";
            if (Pattern.compile(regex).matcher(upperSql).find()) {
                log.warn("Dangerous SQL keyword detected: {}", keyword);
                return new SqlValidationResult(false, null, "Phát hiện từ khóa nguy hiểm bị cấm: '" + keyword + "'. Thao tác bị chặn bởi Safe SQL Validator.");
            }
        }

        // 3. Remove trailing semicolon for sanitization
        if (trimmedSql.endsWith(";")) {
            trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1).trim();
        }

        // 4. Enforce LIMIT 100
        var limitMatcher = LIMIT_PATTERN.matcher(trimmedSql);
        if (limitMatcher.find()) {
            int limitValue = Integer.parseInt(limitMatcher.group(1));
            if (limitValue > 100) {
                trimmedSql = limitMatcher.replaceFirst("LIMIT 100");
                log.info("Query limit adjusted to maximum allowed: 100");
            }
        } else {
            trimmedSql = trimmedSql + " LIMIT 100";
        }

        return new SqlValidationResult(true, trimmedSql, null);
    }
}
