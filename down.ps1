docker-compose -f template-local.yml down

if ($LASTEXITCODE -eq 0) {
    Write-Host "Contenedores detenidos correctamente."
} else {
    Write-Error "Hubo un error al detener los contenedores."
}