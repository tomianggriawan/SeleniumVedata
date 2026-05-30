$html = Get-Content -Raw -Path page_source_dashboard.html
[regex]::Matches($html, 'href="([^"]+)"') | ForEach-Object {
    $_.Groups[1].Value
}
