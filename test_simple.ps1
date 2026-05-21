# 简单的分布式锁测试
# 手动运行多次来测试

$baseUrl = "http://localhost:8082"
$deviceId = 1

# 测试1: 第一个请求
Write-Host "测试1: 飞手1租借设备$deviceId"
$body1 = '{"deviceId":1,"flyerId":1,"rentalHours":1}'
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/rental/create" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body1 -UseBasicParsing
    Write-Host "结果: 成功 - $($response.Content)"
} catch {
    Write-Host "结果: 失败 - $($_.Exception.Message)"
}

Write-Host ""

# 测试2: 第二个请求（同一设备）
Write-Host "测试2: 飞手2租借设备$deviceId"
$body2 = '{"deviceId":1,"flyerId":2,"rentalHours":1}'
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/rental/create" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body2 -UseBasicParsing
    Write-Host "结果: 成功 - $($response.Content)"
} catch {
    Write-Host "结果: 失败 - $($_.Exception.Message)"
}

Write-Host ""

Write-Host "测试完成！"
