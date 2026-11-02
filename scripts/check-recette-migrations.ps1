$ErrorActionPreference = 'Stop'
$root = Split-Path $PSScriptRoot -Parent
$session = Get-Content -LiteralPath (Join-Path $root 'backend/target/recette/session.json') -Raw | ConvertFrom-Json
if ($session.database -notmatch '^mef_integration_[a-f0-9]{32}$') { throw 'Only an isolated integration database is allowed.' }
$settings = @{}
Get-Content -LiteralPath (Join-Path $root 'backend/.env.local') | ForEach-Object {
    if ($_ -match '^(MEF_DB_URL|MEF_DB_USER|MEF_DB_PASSWORD)=(.*)$') { $settings[$Matches[1]] = $Matches[2] }
}
foreach ($name in @('MEF_DB_URL','MEF_DB_USER','MEF_DB_PASSWORD')) {
    $value = [Environment]::GetEnvironmentVariable($name)
    if ($value) { $settings[$name] = $value }
}
$source = [Uri]($settings['MEF_DB_URL'] -replace '^jdbc:', '')
if ($source.Host -notin @('localhost','127.0.0.1') -or $source.AbsolutePath -eq "/$($session.database)") { throw 'Unsafe migration test target.' }
$psql = 'C:/Program Files/PostgreSQL/15/bin/psql.exe'
$savedPassword = $env:PGPASSWORD
$savedEncoding = $env:PGCLIENTENCODING
try {
    $env:PGPASSWORD = $settings['MEF_DB_PASSWORD']; $env:PGCLIENTENCODING = 'UTF8'
    $tables = @('stagiaires','stagiaire_academique','stage_participant','rapports_activite','evaluations_finales','soutenances','alertes','fichiers','stage_participant_fichiers')
    # Use public catalog names, then fingerprint existing rows without exposing their contents.
    $available = & $psql -X -w -h $source.Host -p $source.Port -U $settings['MEF_DB_USER'] -d $session.database -At -v ON_ERROR_STOP=1 -c "SELECT tablename FROM pg_tables WHERE schemaname='public'"
    if ($LASTEXITCODE -ne 0) { throw 'Cannot read isolated schema.' }
    $tables = $tables | Where-Object { $_ -in $available }
    $before = @{}
    foreach ($table in $tables) {
        $before[$table] = & $psql -X -w -h $source.Host -p $source.Port -U $settings['MEF_DB_USER'] -d $session.database -At -v ON_ERROR_STOP=1 -c "SELECT count(*)::text || ':' || md5(COALESCE(string_agg(to_jsonb(t)::text,E'\n' ORDER BY id),'')) FROM $table t"
        if ($LASTEXITCODE -ne 0) { throw 'Cannot fingerprint isolated fixture rows.' }
    }
    foreach ($migration in @('V002__participation_integrity.sql')) {
        & $psql -X -w -h $source.Host -p $source.Port -U $settings['MEF_DB_USER'] -d $session.database -v ON_ERROR_STOP=1 -f (Join-Path $root "backend/migrations/$migration") | Out-Null
        if ($LASTEXITCODE -ne 0) { throw 'Migration replay failed.' }
    }
    $checks = @()
    foreach ($table in $tables) {
        $after = & $psql -X -w -h $source.Host -p $source.Port -U $settings['MEF_DB_USER'] -d $session.database -At -v ON_ERROR_STOP=1 -c "SELECT count(*)::text || ':' || md5(COALESCE(string_agg(to_jsonb(t)::text,E'\n' ORDER BY id),'')) FROM $table t"
        if ($LASTEXITCODE -ne 0 -or $before[$table] -ne $after) { throw 'Fixture rows changed during migration replay.' }
        $checks += @{ table=$table; rowsUnchanged=$true }
    }
    $result = @{ database=$session.database; replayPassed=$true; checks=$checks }
    [IO.File]::WriteAllText((Join-Path $root 'backend/target/recette/migrations-results.json'),($result | ConvertTo-Json -Depth 4),[Text.UTF8Encoding]::new($false))
    $result | ConvertTo-Json -Depth 4
} finally {
    $env:PGPASSWORD = $savedPassword; $env:PGCLIENTENCODING = $savedEncoding
}
