package com.cty.nopersonfinally.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式锁测试程序 - 使用JJWT生成真实token
 */
public class DistributedLockWithJJWTTest {

    private static final String BASE_URL = "http://localhost:8082";
    private static final int THREAD_COUNT = 10;
    private static final Long TEST_DEVICE_ID = 1L;
    private static final String JWT_SECRET = "defaultSecretKey12345678901234567890";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/noperson?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger lockFailCount = new AtomicInteger(0);
    private static final AtomicInteger otherFailCount = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("分布式锁并发测试 - 100个飞手同时租借同一设备");
        System.out.println("========================================\n");

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        // 分配飞手ID: 使用1-100的连续ID
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            final Long flyerId = (long) (i + 1); // 飞手ID从1开始

            executor.submit(() -> {
                try {
                    testRentDevice(threadId, flyerId, TEST_DEVICE_ID);
                } catch (Exception e) {
                    System.err.println("[线程" + threadId + "] 异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(30, TimeUnit.SECONDS); // 等待最多30秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        long duration = System.currentTimeMillis() - startTime;

        // 输出结果
        System.out.println("\n========================================");
        System.out.println("测试结果汇总");
        System.out.println("========================================");
        System.out.println("总并发请求数: " + THREAD_COUNT);
        System.out.println("成功租借数: " + successCount.get());
        System.out.println("分布式锁拒绝数: " + lockFailCount.get());
        System.out.println("其他失败数: " + otherFailCount.get());
        System.out.println("总耗时: " + duration + "ms");
        System.out.println("========================================\n");

        if (successCount.get() == 1) {
            System.out.println("🎉 测试通过！分布式锁生效，只有1个飞手成功租借到设备");
            System.out.println("其他 " + lockFailCount.get() + " 个请求被分布式锁正确拒绝");
        } else if (successCount.get() == 0) {
            System.out.println("⚠️ 所有请求都失败了，请检查：");
            System.out.println("1. 设备是否存在且可租借");
            System.out.println("2. 飞手用户是否存在");
            System.out.println("3. 后端服务是否正常运行");
        } else {
            System.out.println("❌ 测试失败！" + successCount.get() + " 个请求成功了");
            System.out.println("分布式锁可能未生效！");
        }
    }

    /**
     * 生成有效的JWT token（使用JJWT库，与后端一致）
     */
    private static String generateJwtToken(Long userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + 86400000L); // 24小时

        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", "flyer")
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static void testRentDevice(int threadId, Long flyerId, Long deviceId) {
        String token = generateJwtToken(flyerId);

        System.out.println("[线程" + threadId + "] 飞手" + flyerId + " 开始请求...");

        try {
            // 发送HTTP请求
            java.net.URL url = new java.net.URL(BASE_URL + "/device/rent/" + deviceId);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setDoOutput(true);

            String postData = "{}";
            conn.getOutputStream().write(postData.getBytes());
            conn.getOutputStream().flush();
            conn.getOutputStream().close();

            int responseCode = conn.getResponseCode();
            java.io.InputStream inputStream = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
            String response = new String(inputStream.readAllBytes());

            System.out.println("[线程" + threadId + "] 飞手" + flyerId + " 收到响应码: " + responseCode);
            System.out.println("[线程" + threadId + "] 响应内容: " + response.substring(0, Math.min(150, response.length())));

            if (responseCode == 200 && (response.contains("\"code\":200") || response.contains("成功"))) {
                successCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ✅ 飞手" + flyerId + " 租借成功!");
            } else if (response.contains("设备正在被其他操作占用") || response.contains("锁")) {
                lockFailCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ○ 飞手" + flyerId + " 被锁拒绝 (预期行为)");
            } else if (response.contains("code")) {
                otherFailCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ✗ 飞手" + flyerId + " 业务失败");
            } else {
                otherFailCount.incrementAndGet();
                System.out.println("[线程" + threadId + "] ✗ 飞手" + flyerId + " 响应码: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            otherFailCount.incrementAndGet();
            System.err.println("[线程" + threadId + "] 异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
