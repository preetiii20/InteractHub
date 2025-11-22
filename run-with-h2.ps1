<#
.\run-with-h2.ps1

Starts all backend microservices in this repository using the H2 profile.
Place this script at the repository root and run it from PowerShell (pwsh.exe).

What it does:
- Starts each module's `mvnw.cmd spring-boot:run -Dspring.profiles.active=h2` in a new process.
- Writes started process PIDs to `run-with-h2.pids.txt` in the repo root so they can be stopped later.

Usage:
  # from repo root
  .\run-with-h2.ps1

Notes:
- Requires Windows PowerShell (pwsh) and Maven wrapper files (`mvnw.cmd`) present in each module.
- Output will indicate PIDs for each started service. Logs appear in the spawned process windows.
#>

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

$services = @(
    @{ name = 'admin-service'; path = 'backend-microservices\admin-service'; port = 8081 },
    @{ name = 'chat'; path = 'backend-microservices\chat'; port = 8085 },
    @{ name = 'manager-service'; path = 'backend-microservices\manager'; port = 8083 },
    @{ name = 'notification-service'; path = 'backend-microservices\notify'; port = 8090 }
)

$pidsFile = Join-Path $scriptDir 'run-with-h2.pids.txt'
if (Test-Path $pidsFile) { Remove-Item $pidsFile -ErrorAction SilentlyContinue }

Write-Host "Starting services with H2 profile...`n"

foreach ($s in $services) {
    $wd = Join-Path $scriptDir $s.path
    $mvnw = Join-Path $wd 'mvnw.cmd'
    if (-not (Test-Path $wd)) {
        Write-Warning "Path not found: $wd — skipping $($s.name)"
        continue
    }
    if (-not (Test-Path $mvnw)) {
        Write-Warning "Maven wrapper not found: $mvnw — skipping $($s.name)"
        continue
    }

    $args = @('spring-boot:run','-Dspring.profiles.active=h2')
    Write-Host "Starting $($s.name) (port $($s.port))..."

    # Start in a new window so logs are visible; capture process object
    $proc = Start-Process -FilePath $mvnw -ArgumentList $args -WorkingDirectory $wd -PassThru

    if ($proc) {
        "{0}:{1}" -f $($s.name), $proc.Id | Out-File -FilePath $pidsFile -Append -Encoding utf8
        Write-Host "Started $($s.name) with PID $($proc.Id)"
    } else {
        Write-Warning "Failed to start $($s.name)"
    }
}

Write-Host "`nAll start commands issued. PID list saved to: $pidsFile"
Write-Host "To stop the services run: .\run-with-h2-stop.ps1"
