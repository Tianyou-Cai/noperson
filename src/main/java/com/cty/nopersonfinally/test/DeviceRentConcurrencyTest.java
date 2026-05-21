package com.cty.nopersonfinally.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class DeviceRentConcurrencyTest {

    private static final String BASE_URL = "http://localhost:8082";
    private static final int THREAD_COUNT = 100; // 测试100个并发飞手
    private static final Long TEST_DEVICE_ID = 1L; // 测试设备ID
    private static final String JWT_SECRET = "defaultSecretKey12345678901234567890"; // 与后端配置一致
    private static final String FLYER_ROLE = "flyer"; // 飞手角色

    private static final AtomicInteger successCount = new AtomicInteger(0);
    private static final AtomicInteger failCount = new AtomicInteger(0);
    private static final AtomicInteger lockFailedCount = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("设备租借并发测试开始");
        System.out.println("========================================");
        System.out.println("测试参数：");
        System.out.println("- 并发线程数: " + THREAD_COUNT);
        System.out.println("- 测试设备ID: " + TEST_DEVICE_ID);
        System.out.println("- API地址: " + BASE_URL);
        System.out.println("========================================\n");

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1); // 用于确保所有线程同时开始
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        // 提交所有任务
        for (int i = 1; i <= THREAD_COUNT; i++) {
            final int flyerId = 1000 + i; // 飞手ID从1001开始
            executor.submit(() -> {
                try {
                    // 等待主线程发出开始信号
                    startLatch.await();

                    // 执行测试
                    testRentDevice(flyerId, TEST_DEVICE_ID);

                } catch (Exception e) {
                    System.err.println("飞手 " + flyerId + " 测试异常: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 模拟准备时间
        System.out.println("准备开始测试，3秒后所有线程同时发起请求...\n");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 发出开始信号，所有线程同时开始
        System.out.println("所有线程开始执行租借操作！\n");
        startLatch.countDown();

        // 等待所有线程完成
        try {
            doneLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 输出测试结果
        printResults(duration);
    }

    private static void testRentDevice(int flyerId, Long deviceId) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] 飞手 " + flyerId + " 发起租借请求，设备ID: " + deviceId);

        try {
            // 为每个飞手生成token
            String token = generateJwtToken((long) flyerId, FLYER_ROLE);

            // 创建HTTP客户端
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // 构建请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/device/rent/" + deviceId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(30))
                    .build();

            // 发送请求
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 处理响应
            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode == 200 && responseBody.contains("\"code\":200")) {
                successCount.incrementAndGet();
                System.out.println("[" + threadName + "] ✓ 飞手 " + flyerId + " 租借成功！响应: " + responseBody);
            } else if (responseBody.contains("设备正在被其他操作占用")) {
                lockFailedCount.incrementAndGet();
                System.out.println("[" + threadName + "] ○ 飞手 " + flyerId + " 获取锁失败（预期行为）: " + responseBody);
            } else {
                failCount.incrementAndGet();
                System.out.println("[" + threadName + "] ✗ 飞手 " + flyerId + " 租借失败，状态码: " + statusCode + ", 响应: " + responseBody);
            }

        } catch (Exception e) {
            failCount.incrementAndGet();
            System.err.println("[" + threadName + "] ✗ 飞手 " + flyerId + " 请求异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成JWT token（使用JJWT库，与后端一致）
     */
    private static String generateJwtToken(Long userId, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + 86400000L); // 24小时有效期

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    private static void printResults(long duration) {
        System.out.println("\n========================================");
        System.out.println("测试结果汇总");
        System.out.println("========================================");
        System.out.println("总并发请求数: " + THREAD_COUNT);
        System.out.println("成功租借数: " + successCount.get());
        System.out.println("失败请求数: " + failCount.get());
        System.out.println("获取锁失败数（被拒）: " + lockFailedCount.get());
        System.out.println("总耗时: " + duration + " ms");
        System.out.println("平均响应时间: " + (duration / (double) THREAD_COUNT) + " ms");
        System.out.println("========================================");

        // 验证分布式锁效果
        System.out.println("\n【分布式锁效果验证】");
        if (successCount.get() == 1) {
            System.out.println("✅ 测试通过！分布式锁生效，只有1个飞手成功租借设备");
        } else if (successCount.get() == 0) {
            System.out.println("⚠️ 所有请求都被拒绝，请检查：1) 设备是否存在 2) Token是否有效");
        } else {
            System.out.println("❌ 测试失败！有 " + successCount.get() + " 个飞手同时租借成功，分布式锁未生效！");
        }

        if (lockFailedCount.get() > 0) {
            System.out.println("📝 有 " + lockFailedCount.get() + " 个请求因获取锁失败而被拒绝，说明锁机制正在工作");
        }

        System.out.println("========================================");
    }
}
