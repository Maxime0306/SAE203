// Fichier : StatusHandler.java

import java.io.*;
import java.lang.management.ManagementFactory;

public class StatusHandler {

    public static String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("État du serveur:\n");

        // Mémoire libre
        long freeMem = Runtime.getRuntime().freeMemory();
        sb.append("Mémoire libre: ").append(freeMem / 1024).append(" Ko\n");

        // Espace disque libre
        File root = new File("C:\\");  // Utilisation de C:\ pour Windows
        long freeDisk = root.getFreeSpace();
        sb.append("Espace disque libre: ").append(freeDisk / (1024 * 1024)).append(" Mo\n");

        // Nombre de processus (alternative compatible avec Windows)
        try {
            int nbProc = ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors();
            sb.append("Nombre de processeurs disponibles: ").append(nbProc).append("\n");
        } catch (Exception e) {
            sb.append("Erreur récupération information processeurs\n");
        }

        // Nombre d'utilisateurs (alternative pour Windows)
        try {
            // Sur Windows, nous ne pouvons pas utiliser la commande 'who'
            // Utilisons une alternative simple
            sb.append("Utilisateurs connectés: information non disponible sur Windows\n");
        } catch (Exception e) {
            sb.append("Erreur récupération utilisateurs\n");
        }

        return sb.toString();
    }
}