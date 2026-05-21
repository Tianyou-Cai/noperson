# Test distributed lock API
$baseUrl = "http://localhost:8082"
$deviceId = 15

Write-Host "Distributed Lock Test"
Write-Host "====================="
Write-Host "Device ID: $deviceId"
Write-Host ""

# Test 1: First request
Write-Host "Test 1: Flyer 1 tries to rent device $deviceId"
$body1 = "{`"deviceId`":$deviceId,`"flyerId`":1}"
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/rental/test-lock" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body1 -UseBasicParsing
    Write-Host "Result: SUCCESS - $($response.Content)"
} catch {
    Write-Host "Result: FAILED - $($_.Exception.Message)"
}

Write-Host ""

# Test 2: Second request (same device, should fail if lock is held)
Write-Host "Test 2: Flyer 2 tries to rent device $deviceId (during Test 1 processing)"
$body2 = "{`"deviceId`":$deviceId,`"flyerId`":2}"
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/rental/test-lock" -Method POST -Headers @{"Content-Type"="application/json"} -Body $body2 -UseBasicParsing
    Write-Host "Result: SUCCESS - $($response.Content)"
} catch {
    Write-Host "Result: FAILED - $($_.Exception.Message)"
}

Write-Host ""
Write-Host "Test completed!"