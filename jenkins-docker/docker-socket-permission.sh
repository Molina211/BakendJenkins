#!/bin/bash

if [ -e /var/run/docker.sock ]; then
  echo "Ajustando permisos para docker.sock"
  sudo chmod 666 /var/run/docker.sock
else
  echo "No se encontró el socket de Docker en /var/run/docker.sock"
fi

echo "Usuario actual: $(whoami)"
echo "Grupos del usuario jenkins: $(groups jenkins)"
echo "Permisos del socket Docker: $(ls -la /var/run/docker.sock)"
docker info || echo "Error al conectar con Docker"
