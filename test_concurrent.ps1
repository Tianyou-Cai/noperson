# Concurrent Distributed Lock Test
$baseUrl = "http://localhost:8082"
$deviceId = 15

Write-Host "Concurrent Distributed Lock Test"
Write-Host "================================"
Write-Host "Device ID: $deviceId"
Write-Host "Number of concurrent requests: 5"
Write-Host ""

# Create 5 concurrent requests
$jobs = @()
for ($i = 1; $i -le 5; $i++) {
    $flyerId = $i
    $body = "{`"deviceId`":$deviceId,`"flyerId`":$flyerId}"
    
    $jobs += Start-Job -ScriptBlock {
        param($url, $body, $flyerId)
        try {
            $response = Invoke-WebRequest -Uri $url -Method POST -Headers @{"Content-Type"="application/json"} -Body $body -UseBasicParsing
            return @{
                FlyerId = $flyerId
                Success = $true
                Message = $response.Content
            }
        } catch {
            return @{
                FlyerId = $flyerId
                Success = $false
                Message = $_.Exception.Message
            }
        }
    } -ArgumentList "$baseUrl/rental/test-lock", $body, $flyerId
}

Write-Host "All 5 requests sent simultaneously..."
Write-Host ""

# Wait for all jobs and collect results
$results = $jobs | Wait-Job | Receive-Job

# Display results
foreach ($result in $results) {
    if ($result.Success) {
        Write-Host "[Flyer $($result.FlyerId)] SUCCESS: $($result.Message)"
    } else {
        Write-Host "[Flyer $($result.FlyerId)] FAILED: $($result.Message)"
    }
}

# Cleanup jobs
Remove-Job -State Completed

Write-Host ""
Write-Host "Test completed!"