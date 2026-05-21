#!/bin/bash

# 测试分布式锁的简单脚本
# 同时发起5个租借请求，看是否只有一个成功

echo "========================================"
echo "设备租借并发测试 - 分布式锁验证"
echo "========================================"
echo ""

DEVICE_ID=1
BASE_URL="http://localhost:8082"

# 生成一个简单的测试token（模拟飞手1）
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6ImZseWVyIiwiaWF0IjoxNzA2NzQ0MDAwLCJleHAiOjE3MDY4MzA0MDB9.xxxxxxxxxxxxxxxx"

echo "测试说明："
echo "- 同时发起5个租借请求"
echo "- 设备ID: $DEVICE_ID"
echo "- 只有1个请求应该成功，其他应该被分布式锁拒绝"
echo ""

echo "开始测试..."
echo ""

# 同时发起5个请求
for i in 1 2 3 4 5; do
    echo "请求 $i - 飞手ID: $i"
    curl -X POST "${BASE_URL}/device/rent/${DEVICE_ID}" \
         -H "Authorization: Bearer ${TOKEN}" \
         -H "Content-Type: application/json" \
         -s | grep -o '"code":[0-9]*' &
done

wait

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"
