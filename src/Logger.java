// Fichier : Logger.java

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void logAccess(String filePath, String message) {
        log(filePath, "[ACCESS] " + message);
    }

    public static void logError(String filePath, String message) {
        log(filePath, "[ERROR] " + message);
    }

    /**
     * Ecrit un message dans un fichier de log.
     * @param filePath Le chemin du fichier de log.
     * @param message Le message à écrire.
     */
    private static void log(String filePath, String message) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            String timestamp = LocalDateTime.now().format(formatter);
            out.println(timestamp + " " + message);

        } catch (IOException e) {
            System.err.println("Erreur d'écriture dans le fichier de log: " + e.getMessage());
        }
    }
}