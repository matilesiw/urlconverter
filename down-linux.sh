#!/bin/bash
docker-compose -f template-local.yml down

if [ $? -eq 0 ]; then
  echo "Contenedores detenidos correctamente."
else
  echo "Hubo un error al detener los contenedores."
  exit 1
fi