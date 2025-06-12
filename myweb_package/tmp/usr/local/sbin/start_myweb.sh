#!/bin/bash

# Script : start_myweb.sh

JAR_PATH="/usr/local/lib/myweb/server.jar"
CONF_PATH="/etc/myweb/myweb.conf"
LOG_PATH="/usr/local/lib/myweb/myweb_access.log"
PID_PATH="/var/run/myweb/myweb.pid"

if [ ! -f "$JAR_PATH" ]; then
    echo "Erreur : fichier $JAR_PATH introuvable"
    exit 1
fi

nohup java -cp "$JAR_PATH" HttpServer "$CONF_PATH" > "$LOG_PATH" 2>&1 &
echo $! > "$PID_PATH"
echo "Serveur lancé avec PID $!"

