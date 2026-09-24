param(
    [Parameter(Mandatory=$true)][string]$baseDir,
    [Parameter(Mandatory=$true)][string]$version
)

$ErrorActionPreference = "Stop"

docker buildx build --platform linux/amd64,linux/arm64 -t "vitalegi/cosucce-fe:${version}" -t "vitalegi/cosucce-fe:latest" --push "$baseDir/frontend"

Write-Host "Immagini pubblicate: cosucce-fe:${version} (+ :latest)"
Write-Host "Aggiorna COSUCCE_FE_IMAGE_TAG in deploy/env/prod.env sul Raspberry, poi lancia update-raspberry.sh"
