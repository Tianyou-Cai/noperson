#!/bin/bash

# 分布式锁测试脚本
# 同时发起5个请求，测试是否只有一个成功

echo "========================================"
echo "分布式锁并发测试"
echo "========================================"
echo ""

BASE_URL="http://localhost:8082"
DEVICE_ID=1

echo "测试说明："
echo "- 设备ID: $DEVICE_ID"
echo "- 同时发起5个租借请求"
echo "- 预期：只有1个成功，其他被分布式锁拒绝"
echo ""

echo "开始测试..."
echo ""

# 同时发起5个请求
for i in 1 2 3 4 5; do
    echo "[请求 $i] 飞手 $i 发起租借..."
    curl -s -X POST "$BASE_URL/rental/create" \
        -H "Content-Type: application/json" \
        -d "{\"deviceId\":$DEVICE_ID,\"flyerId\":$i,\"rentalHours\":1}" \
        &
done

# 等待所有请求完成
wait

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"
