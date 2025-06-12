// Fichier : HttpServer.java

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class HttpServer {
    public static void main(String[] args) {
        try {
            // Charger la config depuis myweb.conf
            Config config = new Config("C:\\Users\\maxim\\tmp\\ari\\etc\\myweb\\myweb.conf");
            int port = config.getPort();
            String root = config.getDocumentRoot();
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur lancé sur le port " + port);

            // Enregistrer le PID
            try (FileWriter fw = new FileWriter("C:\\Users\\maxim\\tmp\\ari\\var\\run\\myweb\\myweb.pid")) {
                fw.write(String.valueOf(ProcessHandle.current().pid()));
            }

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket, config, root)).start();
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
        }
    }

    public static void handleClient(Socket socket, Config config, String root) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                OutputStream out = socket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;
            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String path = tokenizer.nextToken();

            String ip = socket.getInetAddress().getHostAddress();
            if (!config.getSecurityManager().isAllowed(ip)) {
                Logger.logAccess(config.getAccessLog(), ip + " REJECTED " + path);
                sendResponse(out, 403, "Accès refusé", "text/plain");
                return;
            } else {
                Logger.logAccess(config.getAccessLog(), ip + " ACCEPTED " + path);
            }

            if (path.equals("/status")) {
                String status = StatusHandler.getStatus();
                sendResponse(out, 200, status, "text/plain");
                return;
            }

            if (path.startsWith("/programme")) {
                FormHandler.handleForm(method, path, in, out);
                return;
            }

            File file = new File(root + path);
            if (!file.exists()) {
                sendResponse(out, 404, "Fichier non trouvé", "text/plain");
                return;
            }

            if (file.isDirectory()) {
                if (config.isDirectoryIndexEnabled(file.getPath())) {
                    sendDirectoryListing(file, out);
                    return;
                } else {
                    sendResponse(out, 403, "Listing interdit", "text/plain");
                    return;
                }
            }

            String type = Files.probeContentType(file.toPath());
            byte[] data = Files.readAllBytes(file.toPath());

            if (type != null && (type.startsWith("image") || type.startsWith("audio") || type.startsWith("video"))) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
                    gzip.write(data);
                }
                byte[] compressed = baos.toByteArray();
                PrintWriter writer = new PrintWriter(out);
                writer.print("HTTP/1.1 200 OK\r\n");
                writer.print("Content-Type: " + type + "\r\n");
                writer.print("Content-Encoding: gzip\r\n");
                writer.print("Content-Length: " + compressed.length + "\r\n\r\n");
                writer.flush();
                out.write(compressed);
            } else {
                sendResponse(out, 200, new String(data), type);
            }

        } catch (IOException e) {
            Logger.logError(config.getErrorLog(), "Erreur: " + e.getMessage());
        }
    }

    public static void sendResponse(OutputStream out, int code, String body, String contentType) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        writer.print("HTTP/1.1 " + code + " OK\r\n");
        writer.print("Content-Type: " + contentType + "\r\n");
        writer.print("Content-Length: " + body.length() + "\r\n");
        writer.print("\r\n");
        writer.print(body);
        writer.flush();
    }

    public static void sendDirectoryListing(File dir, OutputStream out) throws IOException {
        StringBuilder html = new StringBuilder("<html><body><h1>Index of " + dir.getName() + "</h1><ul>");
        for (File file : dir.listFiles()) {
            html.append("<li><a href=\"" + file.getName() + "\">" + file.getName() + "</a></li>");
        }
        html.append("</ul></body></html>");
        sendResponse(out, 200, html.toString(), "text/html");
    }
}
