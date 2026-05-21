#!/bin/bash

# 分布式锁测试 - 使用curl同时发起多个请求
echo "========================================"
echo "分布式锁测试 - 5个并发请求"
echo "========================================"
echo ""

DEVICE_ID=1
BASE_URL="http://localhost:8082"

# 生成测试JWT token（简化版）
generate_token() {
    local user_id=$1
    local header=$(echo -n '{"alg":"HS256","typ":"JWT"}' | base64)
    local payload=$(echo -n "{\"sub\":\"${user_id}\",\"role\":\"flyer\"}" | base64)
    local signature=$(echo -n "${header}${payload}" | base64)
    echo "${header}.${payload}.${signature}"
}

echo "测试说明："
echo "- 同时发起5个租借请求到设备 ${DEVICE_ID}"
echo "- 预期：只有1个成功，其他被分布式锁拒绝"
echo ""

# 创建5个并发请求
for i in 1 2 3 4 5; do
    TOKEN=$(generate_token $i)
    (
        echo "[请求 $i] 飞手 $i 发起租借..."
        RESPONSE=$(curl -s -X POST "${BASE_URL}/device/rent/${DEVICE_ID}" \
            -H "Authorization: Bearer ${TOKEN}" \
            -H "Content-Type: application/json" \
            -w "\nHTTP_CODE:%{http_code}" 2>&1)

        HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE" | cut -d: -f2)
        BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE/d')

        if [[ "$BODY" == *"成功"* ]] || [[ "$BODY" == *"code\":200"* ]]; then
            echo "[请求 $i] ✅ 飞手 $i 租借成功!"
        elif [[ "$BODY" == *"设备正在被其他操作"* ]] || [[ "$BODY" == *"锁"* ]]; then
            echo "[请求 $i] ○ 飞手 $i 被分布式锁拒绝 (预期)"
        else
            echo "[请求 $i] ✗ 飞手 $i 失败 - HTTP:$HTTP_CODE - $BODY"
        fi
    ) &
done

# 等待所有后台任务完成
wait

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"
