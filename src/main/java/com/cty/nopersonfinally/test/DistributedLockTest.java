package com.cty.nopersonfinally.test;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式锁测试程序
 */
public class DistributedLockTest {

    private static final String BASE_URL = "http://localhost:8082";
    private static final int THREAD_COUNT = 10; // 测试10个并发
    private static final Long TEST_DEVICE_ID = 1L; // 设备ID
    private static final String DB_URL = "jdbc:mysql://localhost:3306/noperson?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456"; // 修改为你的密码

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger lockFailCount = new AtomicInteger(0);
    private static final AtomicInteger otherFailCount = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("分布式锁测试开始");
        System.out.println("========================================\n");

        // 获取可用的飞手ID
        String flyerIds = getAvailableFlyerIds();
        if (flyerIds == null || flyerIds.isEmpty()) {
            System.out.println("错误：数据库中没有找到飞手用户，请先创建测试数据");
            return;
        }
        System.out.println("使用飞手IDs: " + flyerIds + "\n");

        // 等待后端服务启动
        System.out.println("等待服务启动...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        // 分配飞手ID给各线程
        String[] flyerIdArray = flyerIds.split(",");
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            final Long flyerId = Long.parseLong(flyerIdArray[i % flyerIdArray.length].trim());

            executor.submit(() -> {
                try {
                    testRentDevice(threadId, flyerId, TEST_DEVICE_ID);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        long duration = System.currentTimeMillis() - startTime;

        // 输出结果
        System.out.println("\n========================================");
        System.out.println("测试结果");
        System.out.println("========================================");
        System.out.println("总并发请求数: " + THREAD_COUNT);
        System.out.println("成功租借数: " + successCount.get());
        System.out.println("分布式锁拒绝数: " + lockFailCount.get());
        System.out.println("其他失败数: " + otherFailCount.get());
        System.out.println("总耗时: " + duration + "ms");
        System.out.println("========================================\n");

        if (successCount.get() == 1) {
            System.out.println("✅ 测试通过！分布式锁生效，只有一个请求成功");
        } else if (successCount.get() == 0) {
            System.out.println("⚠️ 所有请求都失败了");
        } else {
            System.out.println("❌ 测试失败！" + successCount.get() + "个请求成功了");
        }
    }

    private static String getAvailableFlyerIds() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT user_id FROM sys_user WHERE role_type = 2 AND status = 1 LIMIT 10";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(rs.getLong("user_id"));
            }
        } catch (SQLException e) {
            System.err.println("数据库查询失败: " + e.getMessage());
            return null;
        }
        return sb.toString();
    }

    private static void testRentDevice(int threadId, Long flyerId, Long deviceId) {
        System.out.println("[线程" + threadId + "] 飞手" + flyerId + " 尝试租借设备" + deviceId);

        try {
            // 发送HTTP请求
            java.net.URL url = new java.net.URL(BASE_URL + "/device/rent/" + deviceId);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + generateTestToken(flyerId));
            conn.setDoOutput(true);
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();
            String response = new String(conn.getInputStream().readAllBytes());

            if (responseCode == 200 && response.contains("\"code\":200")) {
                successCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ✅ 飞手" + flyerId + " 租借成功!");
            } else if (response.contains("设备正在被其他操作占用") || response.contains("锁")) {
                lockFailCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ○ 飞手" + flyerId + " 被分布式锁拒绝");
            } else {
                otherFailCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ✗ 飞手" + flyerId + " 失败: " + response.substring(0, Math.min(100, response.length())));
            }

            conn.disconnect();
        } catch (Exception e) {
            otherFailCount.incrementAndGet();
            System.err.println("[线程" + threadId + "] 异常: " + e.getMessage());
        }
    }

    private static String generateTestToken(Long userId) {
        // 简化版token生成，仅用于测试
        // 格式: header.payload.signature (base64)
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"" + userId + "\",\"role\":\"flyer\"}";

        String headerBase64 = java.util.Base64.getEncoder().encodeToString(header.getBytes());
        String payloadBase64 = java.util.Base64.getEncoder().encodeToString(payload.getBytes());
        String signature = java.util.Base64.getEncoder().encodeToString((headerBase64 + payloadBase64).getBytes());

        return headerBase64 + "." + payloadBase64 + "." + signature;
    }
}
