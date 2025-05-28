./gradlew build

if ($LASTEXITCODE -ne 0) {
    Write-Error "Fail build gradle"
    exit 1
}

docker-compose -f template-local.yml build
docker-compose -f template-local.yml up -d

if ($LASTEXITCODE -eq 0) {
    Write-Host "Contenedores levantados correctamente."
} else {
    Write-Error "Hubo un error al levantar los contenedores."
}