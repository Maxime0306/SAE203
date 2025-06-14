// Fichier : SecurityManager.java

import org.w3c.dom.*;
import java.util.*;

public class SecurityManager {
    private List<String> accepts = new ArrayList<>();
    private List<String> rejects = new ArrayList<>();
    private String defaultPolicy = "accept";
    private boolean acceptFirst = true;

    public SecurityManager() {}

    /**
     * Constructeur de la classe SecurityManager.
     * @param securityElement L'élément <security> du fichier de configuration.
     */
    public SecurityManager(Element securityElement) {
        NodeList children = securityElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) node;
            switch (el.getTagName()) {
                case "accept": accepts.add(el.getTextContent().trim()); break;
                case "reject": rejects.add(el.getTextContent().trim()); break;
                case "default": defaultPolicy = el.getTextContent().trim(); break;
                case "order":
                    NodeList ordre = el.getChildNodes();
                    for (int j = 0; j < ordre.getLength(); j++) {
                        if (ordre.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            String tag = ordre.item(j).getNodeName();
                            String val = ordre.item(j).getTextContent().trim();
                            if (tag.equals("first")) acceptFirst = val.equals("accept");
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Retourne vrai si l'IP est autorisée ou non.
     * @param ip L'IP à vérifier.
     * @return Vrai si l'IP est autorisée ou non.'
     */
    public boolean isAllowed(String ip) {
        if (acceptFirst) {
            if (matches(ip, accepts)) return true;
            if (matches(ip, rejects)) return false;
        } else {
            if (matches(ip, rejects)) return false;
            if (matches(ip, accepts)) return true;
        }
        return defaultPolicy.equals("accept");
    }

    /**
     * Vérifie si une IP est dans une liste de règles.
     * @param ip L'IP à vérifier.
     * @param rules La liste de règles.
     * @return
     */
    private boolean matches(String ip, List<String> rules) {
        for (String rule : rules) {
            if (rule.contains("/")) {
                String[] parts = rule.split("/");
                String base = parts[0];
                int mask = Integer.parseInt(parts[1]);
                if (inSameSubnet(ip, base, mask)) return true;
            } else if (rule.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si une IP est dans le même sous-réseau que une autre.
     * @param ip1 L'IP à vérifier.
     * @param ip2 L'IP de base.
     * @param mask La masque de sous-réseau.
     * @return Vrai si les deux IP sont dans le même sous-réseau.
     */
    private boolean inSameSubnet(String ip1, String ip2, int mask) {
        String[] a1 = ip1.split("\\.");
        String[] a2 = ip2.split("\\.");
        int b1 = 0, b2 = 0;
        for (int i = 0; i < 4; i++) {
            b1 = (b1 << 8) + Integer.parseInt(a1[i]);
            b2 = (b2 << 8) + Integer.parseInt(a2[i]);
        }
        int m = ~((1 << (32 - mask)) - 1);
        return (b1 & m) == (b2 & m);
    }
}