<#
.\run-with-h2-stop.ps1

Stops services started by `run-with-h2.ps1` by reading PIDs from `run-with-h2.pids.txt`.
If that file is missing, the script will attempt to stop any Java processes that were started in the repository directory.

Usage:
  .\run-with-h2-stop.ps1
#>

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location $scriptDir

$pidsFile = Join-Path $scriptDir 'run-with-h2.pids.txt'

if (Test-Path $pidsFile) {
    $lines = Get-Content $pidsFile | Where-Object { $_ -match ':' }
    foreach ($line in $lines) {
        $parts = $line -split ':'
        if ($parts.Length -ge 2) {
            $procId = [int]$parts[1]
            try {
                Stop-Process -Id $procId -Force -ErrorAction Stop
                Write-Host ("Stopped PID {0}" -f $procId)
            } catch {
                Write-Warning ("Failed to stop PID {0}: {1}" -f $procId, $_.Exception.Message)
            }
        }
    }
    Remove-Item $pidsFile -ErrorAction SilentlyContinue
    Write-Host "Stopped processes listed in $pidsFile"
} else {
    Write-Warning "PID file not found: $pidsFile. Attempting to stop java processes started under repo path."
    # Best-effort: stop java processes whose cwd is inside repo
    Get-Process -Name java -ErrorAction SilentlyContinue | ForEach-Object {
        try {
            $procId = $_.Id
            # Cannot directly get process cwd reliably, so prompt the user
            Write-Host ("Found java PID {0} — stopping (force)..." -f $procId)
            Stop-Process -Id $procId -Force -ErrorAction SilentlyContinue
        } catch {
            Write-Warning ("Could not stop java PID {0}: {1}" -f $procId, $_.Exception.Message)
        }
    }
    Write-Host "Attempted to stop java processes. Please verify manually if any services are still running."
}
