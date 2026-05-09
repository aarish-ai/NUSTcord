$files = Get-ChildItem -Path "src\main\webapp" -Filter "*.jsp" -Recurse
foreach ($f in $files) {
    $content = Get-Content $f.FullName -Raw
    $content = $content.Replace("``n    <link", "`n    <link")
    Set-Content -Path $f.FullName -Value $content -NoNewline
}
