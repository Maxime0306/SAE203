// Fichier : Config.java

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

public class Config {
    private int port = 80;
    private String documentRoot = ".";
    private String accessLog = "tmp/access.log";
    private String errorLog = "tmp/error.log";
    private SecurityManager security;
    private java.util.Map<String, Boolean> indexDirs = new java.util.HashMap<>();

    public Config(String filename) {
        try {
            File file = new File(filename);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            Element root = doc.getDocumentElement();

            String portStr = getTagValue("port", root);
            if (portStr != null) port = Integer.parseInt(portStr);

            String docRoot = getTagValue("DocumentRoot", root);
            if (docRoot != null) documentRoot = docRoot;

            NodeList dirs = root.getElementsByTagName("Directory");
            for (int i = 0; i < dirs.getLength(); i++) {
                Element dir = (Element) dirs.item(i);
                String path = dir.getAttribute("Chemin");
                String options = getTagValue("Options", dir);
                if (options != null && options.equals("Indexes")) {
                    indexDirs.put(path, true);
                }
            }

            accessLog = getTagValue("acceslog", root);
            errorLog = getTagValue("errorlog", root);

            Node securityNode = root.getElementsByTagName("security").item(0);
            if (securityNode != null && securityNode instanceof Element) {
                security = new SecurityManager((Element) securityNode);
            } else {
                security = new SecurityManager();
            }
        } catch (Exception e) {
            System.err.println("Erreur lecture configuration: " + e.getMessage());
            security = new SecurityManager();
        }
    }

    /**
     * Retourne la valeur d'un tag sous forme de String.
     * @param tag Le nom du tag.
     * @param e L'élément contenant le tag.'
     * @return La valeur du tag sous forme de String.
     */
    private String getTagValue(String tag, Element e) {
        NodeList nodes = e.getElementsByTagName(tag);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }

    public int getPort() { return port; }
    public String getDocumentRoot() { return documentRoot; }
    public String getAccessLog() { return accessLog; }
    public String getErrorLog() { return errorLog; }
    public SecurityManager getSecurityManager() { return security; }

    /**
     * Retourne vrai si l'indexation des dossiers est activée pour le chemin spécifié.
     * @param path Le chemin du dossier.
     * @return Vrai si l'indexation des dossiers est activée pour le chemin spécifié.'
     */
    public boolean isDirectoryIndexEnabled(String path) {
        return indexDirs.getOrDefault(path, false);
    }
}