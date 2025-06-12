#!/bin/bash

# Script : stop_myweb.sh

PID_FILE="/var/run/myweb/myweb.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    echo "Arrêt du serveur (PID $PID)"
    kill "$PID" && rm "$PID_FILE"
else
    echo "PID introuvable. Le serveur est-il lancé ?"
fi
