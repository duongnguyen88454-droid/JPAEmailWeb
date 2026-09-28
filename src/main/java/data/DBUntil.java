package data;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBUntil {

    // Dùng lazy initialization để tránh crash class khi kết nối DB thất bại lúc khởi động
    private static EntityManagerFactory emf = null;

    public static synchronized EntityManagerFactory getEmFactory() {
        if (emf == null) {
            emf = createEmf();
        }
        return emf;
    }

    private static EntityManagerFactory createEmf() {
        try {
            // Đọc biến môi trường khi deploy trên Render
            String dbUrl      = System.getenv("DB_URL");
            String dbUser     = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");

            if (dbUrl != null && dbUser != null && dbPassword != null) {
                // Override persistence.xml bằng biến môi trường (khi deploy)
                Map<String, String> props = new HashMap<>();
                props.put("javax.persistence.jdbc.url",      dbUrl);
                props.put("javax.persistence.jdbc.user",     dbUser);
                props.put("javax.persistence.jdbc.password", dbPassword);
                System.out.println("DBUntil: Using DB from environment variables.");
                return Persistence.createEntityManagerFactory("emailListPU", props);
            }

            // Fallback: dùng cấu hình trong persistence.xml (khi chạy local)
            System.out.println("DBUntil: Using DB from persistence.xml.");
            return Persistence.createEntityManagerFactory("emailListPU");

        } catch (Exception e) {
            System.err.println("DBUntil ERROR: Could not create EntityManagerFactory!");
            System.err.println("Cause: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static void closeStatement(Statement s) {
        try {
            if (s != null) s.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void closePreparedStatement(Statement ps) {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
