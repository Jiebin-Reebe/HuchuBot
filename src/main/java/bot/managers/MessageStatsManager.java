package bot.managers;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MessageStatsManager {
    private final Connection conn;

    public MessageStatsManager(String dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS message_stats (
                    user_id TEXT PRIMARY KEY,
                    message_count INTEGER NOT NULL DEFAULT 0
                );
            """);

            try {
                stmt.executeUpdate("ALTER TABLE message_stats ADD COLUMN voice_minutes INTEGER NOT NULL DEFAULT 0;");
            } catch (SQLException e) {
                if (!e.getMessage().contains("duplicate column name")) {
                    throw e;
                }
            }
        }
    }

    // 메시지 수 1 증가
    public void addMessage(String userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("""
            INSERT INTO message_stats(user_id, message_count, voice_minutes)
            VALUES (?, 1, 0)
            ON CONFLICT(user_id)
            DO UPDATE SET message_count = message_count + 1;
        """);
        stmt.setString(1, userId);
        stmt.executeUpdate();
        stmt.close();
    }

    // 음성 채팅 분수 누적
    public void addVoiceMinutes(String userId, int minutes) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("""
            INSERT INTO message_stats(user_id, message_count, voice_minutes)
            VALUES (?, 0, ?)
            ON CONFLICT(user_id)
            DO UPDATE SET voice_minutes = voice_minutes + excluded.voice_minutes;
        """);
        stmt.setString(1, userId);
        stmt.setInt(2, minutes);
        stmt.executeUpdate();
        stmt.close();
    }

    // 메시지 수와 음성 분 가져오기
    public Map<String, Integer> getUserStats(String userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT message_count, voice_minutes FROM message_stats WHERE user_id = ?");
        stmt.setString(1, userId);
        ResultSet rs = stmt.executeQuery();
        Map<String, Integer> result = new HashMap<>();
        if (rs.next()) {
            result.put("message_count", rs.getInt("message_count"));
            result.put("voice_minutes", rs.getInt("voice_minutes"));
        } else {
            result.put("message_count", 0);
            result.put("voice_minutes", 0);
        }
        rs.close();
        stmt.close();
        return result;
    }

    // 전체 데이터 조회 (리더보드용)
    public Map<String, int[]> getAllUserStats() throws SQLException {
        Map<String, int[]> result = new HashMap<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT user_id, message_count, voice_minutes FROM message_stats");
        while (rs.next()) {
            result.put(rs.getString("user_id"),
                    new int[]{rs.getInt("message_count"), rs.getInt("voice_minutes")});
        }
        rs.close();
        stmt.close();
        return result;
    }

    public void close() throws SQLException {
        conn.close();
    }
}
