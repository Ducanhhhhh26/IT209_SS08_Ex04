package org.example.smart_hub.mcp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SafeSqlValidatorTest {

    private SafeSqlValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SafeSqlValidator();
    }

    @Test
    @DisplayName("Chấp nhận câu lệnh SELECT hợp lệ và tự động gán LIMIT 100")
    void testValidSelectQueryAppendsLimit() {
        String sql = "SELECT * FROM deliveries WHERE hub_code = 'HN-01'";
        SafeSqlValidator.SqlValidationResult result = validator.validateAndSanitize(sql);

        assertTrue(result.isSafe());
        assertNull(result.errorMessage());
        assertTrue(result.sanitizedQuery().toUpperCase().contains("LIMIT 100"));
    }

    @Test
    @DisplayName("Chấp nhận câu lệnh SELECT có LIMIT nhỏ hơn hoặc bằng 100")
    void testValidSelectWithCustomLimit() {
        String sql = "SELECT * FROM deliveries LIMIT 25";
        SafeSqlValidator.SqlValidationResult result = validator.validateAndSanitize(sql);

        assertTrue(result.isSafe());
        assertEquals("SELECT * FROM deliveries LIMIT 25", result.sanitizedQuery());
    }

    @Test
    @DisplayName("Ép hạ LIMIT xuống 100 nếu câu lệnh yêu cầu vượt quá 100")
    void testLimitExceededIsCapped() {
        String sql = "SELECT * FROM deliveries LIMIT 500";
        SafeSqlValidator.SqlValidationResult result = validator.validateAndSanitize(sql);

        assertTrue(result.isSafe());
        assertEquals("SELECT * FROM deliveries LIMIT 100", result.sanitizedQuery());
    }

    @Test
    @DisplayName("Chặn tuyệt đối câu lệnh DROP TABLE phá hoại cấu trúc CSDL")
    void testBlockDropTable() {
        String sql = "DROP TABLE deliveries";
        SafeSqlValidator.SqlValidationResult result = validator.validateAndSanitize(sql);

        assertFalse(result.isSafe());
        assertNotNull(result.errorMessage());
        assertTrue(result.errorMessage().contains("DROP") || result.errorMessage().contains("SELECT"));
    }

    @Test
    @DisplayName("Chặn tuyệt đối câu lệnh DELETE FROM xóa dữ liệu")
    void testBlockDeleteQuery() {
        String sql = "DELETE FROM incidents WHERE id = 1";
        SafeSqlValidator.SqlValidationResult result = validator.validateAndSanitize(sql);

        assertFalse(result.isSafe());
        assertNotNull(result.errorMessage());
    }

    @Test
    @DisplayName("Chặn tuyệt đối câu lệnh UPDATE và ALTER TABLE")
    void testBlockUpdateAndAlter() {
        String updateSql = "UPDATE deliveries SET status = 'DELIVERED'";
        assertFalse(validator.validateAndSanitize(updateSql).isSafe());

        String alterSql = "ALTER TABLE deliveries DROP COLUMN cod_amount";
        assertFalse(validator.validateAndSanitize(alterSql).isSafe());
    }

    @Test
    @DisplayName("Chặn câu lệnh SELECT lồng lệnh cấm (SQL Injection)")
    void testBlockInjectedCommands() {
        String injectedSql = "SELECT * FROM deliveries; DROP TABLE incidents;";
        SafeSqlValidator.SqlValidationResult result = validator.validateAndSanitize(injectedSql);

        assertFalse(result.isSafe());
    }
}
