# Удаление существующих SSH туннелей
Get-Process ssh | Stop-Process -Force

# Создаем SSH-туннели
Start-Process -NoNewWindow -FilePath "powershell" -ArgumentList "-Command", "ssh -L 5434:127.0.0.1:5434 root@noxly.ru -N"
Start-Process -NoNewWindow -FilePath "powershell" -ArgumentList "-Command", "ssh -L 6380:127.0.0.1:6380 root@noxly.ru -N"
# Даем время на установку туннелей
Start-Sleep -Seconds 2

# Проверка туннелей
$connection1 = Test-NetConnection -ComputerName localhost -Port 5434
$connection2 = Test-NetConnection -ComputerName localhost -Port 6380

if (-not$connection2.TcpTestSucceeded)
{
    Write-Host "ПРЕДУПРЕЖДЕНИЕ: TCP connect to (::1 : 5434) failed"
}
else
{
    Write-Host "SSH туннель для postgresauthservice успешно установлен."
}

if (-not$connection1.TcpTestSucceeded)
{
    Write-Host "ПРЕДУПРЕЖДЕНИЕ: TCP connect to (::1 : 5435) failed"
}
else
{
    Write-Host "SSH туннель для postgresauthservice успешно установлен."
}