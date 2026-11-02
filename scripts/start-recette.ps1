param(
    [Parameter(Mandatory=$true)]
    [ValidatePattern('^mef_integration_[a-f0-9]{32}$')][string]$Database
)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$settings = @{}
$localConfig = Join-Path $projectRoot 'backend/.env.local'
if (Test-Path -LiteralPath $localConfig) {
    Get-Content -LiteralPath $localConfig | ForEach-Object {
        if ($_ -match '^(MEF_DB_URL|MEF_DB_USER|MEF_DB_PASSWORD)=(.*)$') { $settings[$Matches[1]] = $Matches[2] }
    }
}
foreach ($name in @('MEF_DB_URL','MEF_DB_USER','MEF_DB_PASSWORD')) {
    $value = [Environment]::GetEnvironmentVariable($name)
    if ($value) { $settings[$name] = $value }
    if (!$settings[$name]) { throw 'Missing local PostgreSQL configuration.' }
}
$source = [Uri]($settings['MEF_DB_URL'] -replace '^jdbc:', '')
if ($source.Host -notin @('localhost','127.0.0.1') -or $source.AbsolutePath -eq "/$Database") { throw 'Only an independent local integration database is allowed.' }
foreach ($port in @(18080,15173)) {
    $probe = [Net.Sockets.TcpClient]::new()
    try { $probe.Connect('127.0.0.1',$port); throw "Recette port $port is already in use." }
    catch [Net.Sockets.SocketException] { } finally { $probe.Dispose() }
}
$uploadPath = [IO.Path]::GetFullPath((Join-Path $projectRoot "backend/target/integration-uploads/$Database"))
if (!(Test-Path -LiteralPath $uploadPath)) { throw 'Run isolated integration tests with -KeepUploads first.' }
$artifactRoot = Join-Path $projectRoot 'backend/target/recette'
[IO.Directory]::CreateDirectory($artifactRoot) | Out-Null
$environment = @{
    SPRING_DATASOURCE_URL="jdbc:postgresql://$($source.Host):$($source.Port)/$Database"
    SPRING_DATASOURCE_USERNAME=$settings['MEF_DB_USER']
    SPRING_DATASOURCE_PASSWORD=$settings['MEF_DB_PASSWORD']
    APP_UPLOAD_DIR=$uploadPath
    SERVER_ADDRESS='127.0.0.1'
    SERVER_PORT='18080'
    SPRING_JPA_HIBERNATE_DDL_AUTO='validate'
    SPRING_JPA_SHOW_SQL='false'
    LOGGING_LEVEL_ROOT='ERROR'
    VITE_API_URL='/api'
    API_PROXY_TARGET='http://127.0.0.1:18080'
}
$saved = @{}
$backendProcess = $null
try {
    foreach ($name in $environment.Keys) {
        $saved[$name] = [Environment]::GetEnvironmentVariable($name,'Process')
        [Environment]::SetEnvironmentVariable($name,$environment[$name],'Process')
    }
    $backendProcess = Start-Process -FilePath 'java.exe' -ArgumentList @('-jar','target/stagemanagement-0.0.1-SNAPSHOT.jar') -WorkingDirectory (Join-Path $projectRoot 'backend') -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $artifactRoot 'backend.stdout.log') -RedirectStandardError (Join-Path $artifactRoot 'backend.stderr.log')
    $frontendProcess = Start-Process -FilePath 'node.exe' -ArgumentList @('node_modules/vite/bin/vite.js','--host','127.0.0.1','--port','15173','--strictPort') -WorkingDirectory (Join-Path $projectRoot 'frontend') -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $artifactRoot 'frontend.stdout.log') -RedirectStandardError (Join-Path $artifactRoot 'frontend.stderr.log')
    $manifest = @{ database=$Database; backendPid=$backendProcess.Id; frontendPid=$frontendProcess.Id; url='http://127.0.0.1:15173'; startedUtc=[DateTime]::UtcNow.ToString('o') }
    [IO.File]::WriteAllText((Join-Path $artifactRoot 'session.json'),($manifest | ConvertTo-Json),[Text.UTF8Encoding]::new($false))
    $manifest | ConvertTo-Json
} finally {
    foreach ($name in $saved.Keys) { [Environment]::SetEnvironmentVariable($name,$saved[$name],'Process') }
}
