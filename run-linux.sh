#!/bin/bash

set -e

./gradlew build --no-daemon
BUILD_EXIT_CODE=$?

if [ $BUILD_EXIT_CODE -ne 0 ]; then
  echo "Falló el build de Gradle."
  exit 1
fi

docker-compose -f template-local.yml build

docker-compose -f template-local.yml up -d
UP_EXIT_CODE=$?

if [ $UP_EXIT_CODE -eq 0 ]; then
  echo "Contenedores levantados correctamente."
else
  echo "Hubo un error al levantar los contenedores."
  exit 1
fi