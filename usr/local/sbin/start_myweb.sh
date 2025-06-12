#!/bin/bash

# Fichier : start_myweb.sh

JAR_PATH="C:\Users\maxim\tmp\ari\var\run\myweb\server.jar"
CONF_PATH="C:\Users\maxim\tmp\ari\etc\myweb\myweb.conf"
LOG_PATH="C:\Users\maxim\tmp\ari\myweb_access.log"

if [ ! -f "$JAR_PATH" ]; then
    echo "Erreur : fichier $JAR_PATH introuvable"
fi

nohup java -cp "$JAR_PATH" HttpServer "$CONF_PATH" > "$LOG_PATH" 2>&1 &
echo "Serveur lancé avec PID $!"
echo $! > /tmp/var/run/myweb/myweb.pid