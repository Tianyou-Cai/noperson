package com.cty.nopersonfinally.test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量生成测试飞手数据的工具类
 */
public class TestDataGenerator {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/noperson?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password_here"; // 修改为你的数据库密码

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("开始生成测试飞手数据...");
        System.out.println("========================================");

        // 生成100个测试飞手
        List<Long> flyerIds = generateTestFlyers(100);

        System.out.println("\n========================================");
        System.out.println("测试数据生成完成！");
        System.out.println("生成的飞手ID列表：");
        System.out.println(flyerIds);
        System.out.println("========================================");

        System.out.println("\n你可以使用这些飞手ID来运行并发测试");
        System.out.println("将这些ID填入DeviceRentConcurrencyTest中");
    }

    /**
     * 批量生成测试飞手
     */
    private static List<Long> generateTestFlyers(int count) {
        List<Long> generatedIds = new ArrayList<>();
        String sql = "INSERT INTO sys_user (username, password, phone, role_type, audit_status, status, balance) VALUES (?, ?, ?, 2, 1, 1, 1000)";

        // BCrypt加密的密码 "password123"
        String encryptedPassword = "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "flyer_test_" + i);
                pstmt.setString(2, encryptedPassword);
                pstmt.setString(3, "1390000" + String.format("%04d", i));
                pstmt.executeUpdate();

                // 获取自动生成的ID
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedIds.add(rs.getLong(1));
                    }
                }
            }

            conn.commit();
            System.out.println("成功插入 " + count + " 个测试飞手用户");

        } catch (SQLException e) {
            System.err.println("数据库操作失败：" + e.getMessage());
            e.printStackTrace();
        }

        return generatedIds;
    }
}
