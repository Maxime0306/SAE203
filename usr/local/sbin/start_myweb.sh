#!/bin/bash

# Fichier : start_myweb.sh

JAR_PATH="/tmp/usr/local/lib/myweb/server.jar"
CONF_PATH="/tmp/etc/myweb/myweb.conf"
LOG_PATH="/tmp/var/run/myweb/myweb.log"

if [ ! -f "$JAR_PATH" ]; then
    echo "Erreur : fichier $JAR_PATH introuvable"
    exit 1
fi

nohup java -cp "$JAR_PATH" HttpServer "$CONF_PATH" > "$LOG_PATH" 2>&1 &
echo "Serveur lancé avec PID $!"
echo $! > /tmp/var/run/myweb/myweb.pid