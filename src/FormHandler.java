// Fichier : FormHandler.java

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class FormHandler {

    private static final File dataFile = new File("C:\\Users\\maxim\\tmp\\ari\\usr\\local\\lib\\myweb\\data.txt");

    public static void handleForm(String method, String path, BufferedReader in, OutputStream out) throws IOException {
        Map<String, String> params = new HashMap<>();

        if (method.equals("GET")) {
            if (path.contains("?")) {
                String query = path.split("\\?", 2)[1];
                parseQuery(query, params);
            }
        } else if (method.equals("POST")) {
            String line;
            int contentLength = 0;
            while (!(line = in.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length: ")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }
            char[] body = new char[contentLength];
            in.read(body);
            parseQuery(new String(body), params);
        }

        String name = params.get("user_name");
        String mail = params.get("user_mail");

        if (name == null) {
            sendHtml(out, "<h1>Erreur : nom requis</h1>");
            return;
        }

        if (mail != null) {
            try (FileWriter fw = new FileWriter(dataFile, true)) {
                fw.write(name + "," + mail + "\n");
            }
            sendHtml(out, "<h1>Données enregistrées</h1><a href='/'>Accueil</a>");
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
                String line;
                boolean found = false;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(name)) {
                        sendHtml(out, "<h1>Utilisateur trouvé</h1><p>Nom: " + parts[0] + "<br>Email: " + parts[1] + "</p>");
                        found = true;
                        break;
                    }
                }
                if (!found) sendHtml(out, "<h1>Utilisateur non trouvé</h1>");
            }
        }
    }

    private static void parseQuery(String query, Map<String, String> params) throws UnsupportedEncodingException {
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            String key = URLDecoder.decode(kv[0], "UTF-8");
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : "";
            params.put(key, value);
        }
    }

    private static void sendHtml(OutputStream out, String content) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        writer.print("HTTP/1.1 200 OK\r\n");
        writer.print("Content-Type: text/html\r\n");
        writer.print("Content-Length: " + content.length() + "\r\n\r\n");
        writer.print("<html><body>" + content + "</body></html>");
        writer.flush();
    }
}