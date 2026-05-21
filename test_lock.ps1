# PowerShell 分布式锁测试脚本
# 同时发起5个租借请求，测试分布式锁是否生效

Write-Host "========================================"
Write-Host "分布式锁并发测试"
Write-Host "========================================"
Write-Host ""

$baseUrl = "http://localhost:8082"
$deviceId = 1

Write-Host "测试说明："
Write-Host "- 设备ID: $deviceId"
Write-Host "- 同时发起5个租借请求"
Write-Host "- 预期：只有1个成功，其他被分布式锁拒绝"
Write-Host ""

Write-Host "开始测试..."
Write-Host ""

# 创建5个并发请求
$tasks = @()
for ($i = 1; $i -le 5; $i++) {
    $flyerId = $i
    $body = @{
        deviceId     = $deviceId
        flyerId      = $flyerId
        rentalHours  = 1
    } | ConvertTo-Json
    
    $tasks += Start-Job -ScriptBlock {
        param($url, $body)
        try {
            $response = Invoke-WebRequest -Uri $url -Method POST -Headers @{"Content-Type"="application/json"} -Body $body -UseBasicParsing
            return @{
                FlyerId = $args[2]
                Success = $true
                StatusCode = $response.StatusCode
                Content = $response.Content
            }
        } catch {
            return @{
                FlyerId = $args[2]
                Success = $false
                ErrorMessage = $_.Exception.Message
            }
        }
    } -ArgumentList "$baseUrl/rental/create", $body, $flyerId
}

# 等待所有任务完成并获取结果
$results = $tasks | Wait-Job | Receive-Job

# 输出结果
foreach ($result in $results) {
    if ($result.Success) {
        Write-Host "[飞手 $($result.FlyerId)] ✅ 响应码: $($result.StatusCode), 内容: $($result.Content)"
    } else {
        Write-Host "[飞手 $($result.FlyerId)] ✗ 失败: $($result.ErrorMessage)"
    }
}

Write-Host ""
Write-Host "========================================"
Write-Host "测试完成"
Write-Host "========================================"

# 清理作业
Remove-Job -State Completed
