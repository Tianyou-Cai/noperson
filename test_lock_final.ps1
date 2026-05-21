# Test distributed lock with correct device ID
$baseUrl = "http://localhost:8082"
$deviceId = 15  # Use an existing device ID

Write-Host "Test: Distributed Lock for Device $deviceId"
Write-Host "=============================================="
Write-Host ""

# Test 1: First request
Write-Host "Test 1: Flyer 1 rents device $deviceId"
$body1 = "{`"deviceId`":$deviceId,`"flyerId`":1,`"rentalHours`":1}"
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/rental/create" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body1 -UseBasicParsing
    Write-Host "Result: SUCCESS - $($response.Content)"
} catch {
    Write-Host "Result: FAILED - $($_.Exception.Message)"
}

Write-Host ""

# Test 2: Second request (same device, should be blocked by lock)
Write-Host "Test 2: Flyer 2 rents device $deviceId"
$body2 = "{`"deviceId`":$deviceId,`"flyerId`":2,`"rentalHours`":1}"
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/rental/create" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body2 -UseBasicParsing
    Write-Host "Result: SUCCESS - $($response.Content)"
} catch {
    Write-Host "Result: FAILED - $($_.Exception.Message)"
}

Write-Host ""
Write-Host "Test completed!"