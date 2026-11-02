$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$session = Get-Content -LiteralPath (Join-Path $root 'backend/target/recette/session.json') -Raw | ConvertFrom-Json
if ($session.database -notmatch '^mef_integration_[a-f0-9]{32}$') { throw 'Invalid recette manifest.' }
$started = [DateTime]::Parse($session.startedUtc).ToUniversalTime()
foreach ($entry in @(@($session.backendPid,'java'),@($session.frontendPid,'node'))) {
    $process = Get-Process -Id $entry[0] -ErrorAction SilentlyContinue
    if (!$process) { continue }
    if ($process.ProcessName -ne $entry[1] -or [Math]::Abs(($process.StartTime.ToUniversalTime()-$started).TotalSeconds) -gt 30) {
        throw 'Process identity changed; refusing to stop an unrelated process.'
    }
    Stop-Process -Id $process.Id
}
Write-Host 'Recette servers stopped. Test database and fixture documents retained.'
