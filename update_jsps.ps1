$jsps = Get-ChildItem -Path "src\main\webapp" -Filter "*.jsp" -Recurse
foreach ($jsp in $jsps) {
    $content = Get-Content $jsp.FullName -Raw
    $content = $content -replace '<link rel="stylesheet" type="text/css" href="css/style.css">', '<link rel="stylesheet" type="text/css" href="css/style.css?v=2">`n    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">'
    Set-Content -Path $jsp.FullName -Value $content
}
