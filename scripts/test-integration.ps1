$ErrorActionPreference = 'Stop'
# Create a uniquely named empty test database; never restore over the configured database.
$projectRoot = Split-Path $PSScriptRoot -Parent
$settings = @{}
Get-Content -LiteralPath (Join-Path $projectRoot 'backend/src/main/resources/application.properties') | ForEach-Object {
    if ($_ -match '^spring\.datasource\.(url|username|password)=(.*)$') { $settings[$Matches[1]] = $Matches[2].Trim() }
}
$sourceUrl = [Uri]($settings['url'] -replace '^jdbc:', '')
if ($sourceUrl.Host -notin @('localhost', '127.0.0.1')) { throw 'Only a local PostgreSQL server is allowed by this test launcher.' }
$testDatabase = 'mef_integration_' + [Guid]::NewGuid().ToString('N')
if ($testDatabase -notmatch '^mef_integration_[a-f0-9]{32}$' -or $sourceUrl.AbsolutePath -eq "/$testDatabase") { throw 'Invalid test database name.' }
$pgExecutable = 'C:/Program Files/PostgreSQL/15/bin/psql.exe'
$savedEnvironment = @{}
foreach ($name in @('PGPASSWORD','PGCLIENTENCODING','MEF_TEST_DB_URL','MEF_TEST_DB_USER','MEF_TEST_DB_PASSWORD','SPRING_DATASOURCE_URL','SPRING_DATASOURCE_USERNAME','SPRING_DATASOURCE_PASSWORD')) { $savedEnvironment[$name] = [Environment]::GetEnvironmentVariable($name, 'Process') }
$created = $false
$testExit = 1
try {
    $env:PGPASSWORD = $settings['password']
    $env:PGCLIENTENCODING = 'UTF8'
    & $pgExecutable -X -w -h $sourceUrl.Host -p $sourceUrl.Port -U $settings['username'] -d postgres -v ON_ERROR_STOP=1 -c "CREATE DATABASE $testDatabase" | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'Cannot create the isolated test database.' }
    $created = $true
    $schema = [IO.File]::ReadAllText((Join-Path $projectRoot 'conception/Conception2.sql'))
    $schema = $schema.Substring($schema.IndexOf('CREATE TABLE types_statut('))
    if ($schema -match '(?im)^\s*(DROP DATABASE|CREATE DATABASE|\\c\s)') { throw 'Database-level statements must not enter the test schema.' }
    $schemaFile = Join-Path $projectRoot 'backend/target/integration-schema.sql'
    [IO.Directory]::CreateDirectory((Split-Path $schemaFile -Parent)) | Out-Null
    [IO.File]::WriteAllText($schemaFile, $schema, [Text.UTF8Encoding]::new($false))
    & $pgExecutable -X -w -h $sourceUrl.Host -p $sourceUrl.Port -U $settings['username'] -d $testDatabase -v ON_ERROR_STOP=1 -f $schemaFile | Out-Null
    if ($LASTEXITCODE -ne 0) { throw 'Cannot initialize the isolated test schema.' }
    $env:MEF_TEST_DB_URL = "jdbc:postgresql://$($sourceUrl.Host):$($sourceUrl.Port)/$testDatabase"
    $env:MEF_TEST_DB_USER = $settings['username']
    $env:MEF_TEST_DB_PASSWORD = $settings['password']
    $env:SPRING_DATASOURCE_URL = $env:MEF_TEST_DB_URL
    $env:SPRING_DATASOURCE_USERNAME = $env:MEF_TEST_DB_USER
    $env:SPRING_DATASOURCE_PASSWORD = $env:MEF_TEST_DB_PASSWORD
    Push-Location (Join-Path $projectRoot 'backend')
    try {
        & mvn.cmd -o test
        $testExit = $LASTEXITCODE
    } finally { Pop-Location }
} finally {
    if ($created) {
        # The name is generated and checked above; only this test database is dropped.
        & $pgExecutable -X -w -h $sourceUrl.Host -p $sourceUrl.Port -U $settings['username'] -d postgres -v ON_ERROR_STOP=1 -c "DROP DATABASE $testDatabase WITH (FORCE)" | Out-Null
        if ($LASTEXITCODE -ne 0) { Write-Warning "Cleanup failed for $testDatabase"; $testExit = 1 }
    }
    foreach ($name in $savedEnvironment.Keys) { [Environment]::SetEnvironmentVariable($name, $savedEnvironment[$name], 'Process') }
    $uploadRoot = [IO.Path]::GetFullPath((Join-Path $projectRoot 'backend/target/integration-uploads'))
    $uploadDirectory = [IO.Path]::GetFullPath((Join-Path $uploadRoot $testDatabase))
    if (!$uploadDirectory.StartsWith($uploadRoot + [IO.Path]::DirectorySeparatorChar) -or (Split-Path $uploadDirectory -Leaf) -ne $testDatabase) { throw 'Unsafe upload cleanup path.' }
    if (Test-Path -LiteralPath $uploadDirectory) { Remove-Item -LiteralPath $uploadDirectory -Recurse -Force }
}
exit $testExit
