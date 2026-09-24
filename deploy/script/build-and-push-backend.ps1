param(
    [Parameter(Mandatory=$true)][string]$baseDir,
    [Parameter(Mandatory=$true)][string]$version
)

$ErrorActionPreference = "Stop"

Push-Location "$baseDir/backend"
mvn clean verify -DskipTests
Pop-Location

docker buildx build --platform linux/amd64,linux/arm64 -t "vitalegi/cosucce-be:${version}" -t "vitalegi/cosucce-be:latest" --push "$baseDir/backend" -f "$baseDir/backend/Dockerfile"

Write-Host "Immagini pubblicate: cosucce-be:${version} (+ :latest)"
Write-Host "Aggiorna COSUCCE_BE_IMAGE_TAG in deploy/env/prod.env sul Raspberry, poi lancia update-raspberry.sh"
