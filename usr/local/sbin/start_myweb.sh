#!/bin/bash

# Fichier : start_myweb.sh

JAR_PATH="var\run\myweb\server.jar"
CONF_PATH="etc\myweb\myweb.conf"
LOG_PATH="myweb_access.log"

if [ ! -f "$JAR_PATH" ]; then
    echo "Erreur : fichier $JAR_PATH introuvable"
fi

nohup java -cp "$JAR_PATH" HttpServer "$CONF_PATH" > "$LOG_PATH" 2>&1 &
echo "Serveur lancé avec PID $!"
echo $! > C:\Users\maxim\tmp\ari\var\run\myweb\myweb.pid