package neuesspiel;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NotrufDialogKI {

    public static class CtNotruf {
        public String anruferName;
        public String strasse;
        public Coordinate koordinate;
        public String stichwort;
        public String kurzBeschreibung;
        public boolean istPanisch;
        public String textIntro, textWo, textWas, textVerletzte;
        public boolean frageWasGestellt = false, frageVerletzteGestellt = false, frageNameGestellt = false;
    }

    public static class EinsatzTyp {
        public String stichwort, name;
        public boolean immerPanisch;
        public String[] introTexte, wasTexte, verletzteTexte;

        public EinsatzTyp(String stichwort, String name, boolean immerPanisch, String[] introTexte, String[] wasTexte, String[] verletzteTexte) {
            this.stichwort = stichwort; this.name = name; this.immerPanisch = immerPanisch;
            this.introTexte = introTexte; this.wasTexte = wasTexte; this.verletzteTexte = verletzteTexte;
        }
    }

    // NEU: Ein simples Objekt für die heruntergeladenen Online-Adressen
    public static class EchteAdresse {
        String adresseKomplett;
        Coordinate koordinate;
        public EchteAdresse(String a, Coordinate c) { adresseKomplett = a; koordinate = c; }
    }

    private static Random rand = new Random();
    private static String[] vornamen = {"Max", "Lisa", "Herr", "Frau", "Peter", "Anna", "Tom", "Julia", "Stefan", "Marie"};
    private static String[] nachnamen = {"Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Schröder", "Wagner"};
    
    // HIER LANDEN DIE ONLINE-ADRESSEN DRIN!
    public static List<EchteAdresse> adressPool = new ArrayList<>();
    public static List<EinsatzTyp> einsatzKatalog = new ArrayList<>();

    static {
        einsatzKatalog.add(new EinsatzTyp("F1", "Mülleimerbrand", false,
            new String[]{"Guten Tag, ich möchte ein Feuer melden.", "Hier brennt ein Mülleimer!"},
            new String[]{"Der Papiereimer brennt lichterloh.", "Jemand hat Müll angezündet, das qualmt stark."},
            new String[]{"Nein, hier steht niemand direkt dran.", "Zum Glück nicht."}
        ));
        einsatzKatalog.add(new EinsatzTyp("F2", "Wohnungsbrand", true,
            new String[]{"FEUERWEHR!!! ES BRENNT!!", "Hilfe, kommen Sie schnell!"},
            new String[]{"Aus dem Fenster schlagen Flammen!", "Der ganze Hausflur ist voller schwarzem Rauch!"},
            new String[]{"Ich weiß es nicht! Da ist noch jemand drin!", "Ja, Leute schreien am Fenster!"}
        ));
        einsatzKatalog.add(new EinsatzTyp("H1", "Baum auf Straße", false,
            new String[]{"Moin, hier liegt ein Baum im Weg.", "Hallo, die Straße ist blockiert."},
            new String[]{"Ein dicker Ast ist abgebrochen und blockiert die halbe Fahrbahn.", "Ein Baum ist umgestürzt."},
            new String[]{"Nein, Auto hat auch nichts abbekommen.", "Niemand verletzt."}
        ));
        einsatzKatalog.add(new EinsatzTyp("R2", "Verdacht Herzinfarkt", true,
            new String[]{"Rettungsdienst?! Oh mein Gott, machen Sie schnell!!", "Brauche dringend einen Arzt!"},
            new String[]{"Mein Mann ist zusammengeklappt! Er hält sich die Brust!", "Mein Kollege kriegt keine Luft mehr."},
            new String[]{"Ja, ein Patient. Ihm geht es sehr schlecht!", "Ja, er ist nicht mehr ansprechbar!"}
        ));
        einsatzKatalog.add(new EinsatzTyp("R1", "Schnittverletzung", false,
            new String[]{"Guten Tag, mein Mann hat sich an der Hand verletzt."},
            new String[]{"Mein Mann wollte einen Schrank zusammenbauen.", "Mein Mann hat die Holzlatte kürzen wollen."},
            new String[]{"Ja, eine Person, sie blutet stark!", "Ja, er ist ansprechbar!"}
        ));
        einsatzKatalog.add(new EinsatzTyp("H1", "Klein Tier in Not", false,
            new String[]{"Guten Tag, mein Hund Steckt mit der Pfote im Gulli fest"},
            new String[]{"Ich war gassi und auf einmal Jauelte mein Hund auf, da steckte er schon im Gulli fest.", "Ich habe einen Knohen ins Gulli gekickt damit mein Hund den nicht bekommt, was soll ich sagen er wollte den Knochen."},
            new String[]{"Nein, nur mein Hund, aber der ist Lieb.", "Nein, aber mein Hündchen steckt fest Machen sie doch was!"}
        ));
        einsatzKatalog.add(new EinsatzTyp("F1", "PKW-Brannt", true,
            new String[]{"Guten Tag, mein Auto es.... es brennt lichter Loh."},
            new String[]{"Meine Frau war gerade noch einkaufen, und jetzt brennt die Karre, volle Kanne, sie hat bestimmt geraucht.", "Mein Mann hat die Gasflaschen aus dem Baumarkt geholt... Beeilen sie sich, das Knallt ganz doll!.",
                "Wir wollten gerade in den Urlaub fahren auf einmal ist alles voller Rauch... beeilen sie sich."},
            new String[]{"Nein, aber mein Auto ist wohl Kaputt!", "Ja, er sitz noch im Auto!"}
        ));
    }

    // ==========================================
    // NEU: LÄDT ECHTE ADRESSEN AUS DEM INTERNET
    // ==========================================
    public static void ladeAdressenOnline() {
        try {
            System.out.println("Lade Adressen von Overpass API...");
            String query = "[out:json];node[\"addr:street\"][\"addr:housenumber\"](53.56,11.35,53.68,11.48);out 4000;";
            String urlStr = "https://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(query, "UTF-8");
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000); // 8 Sekunden (Server ist manchmal langsam)
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "LeitstellenSimulatorSchwerin/1.0");
            
            if (conn.getResponseCode() != 200) {
                System.out.println("Server antwortet mit Fehler-Code: " + conn.getResponseCode());
                return;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            
            String json = sb.toString();
            
            // Zerschneidet den Text anhand von Regex (ignoriert Leerzeichen)
            String[] nodes = json.split("\"type\"\\s*:\\s*\"node\"");
            
            for(int i = 1; i < nodes.length; i++) {
                String node = nodes[i];
                try {
                    String latStr = safeExtract(node, "lat", false);
                    String lonStr = safeExtract(node, "lon", false);
                    String street = safeExtract(node, "addr:street", true);
                    String hnr = safeExtract(node, "addr:housenumber", true);
                    
                    if(latStr != null && lonStr != null && street != null && hnr != null) {
                        double lat = Double.parseDouble(latStr);
                        double lon = Double.parseDouble(lonStr);
                        adressPool.add(new EchteAdresse(street + " " + hnr, new Coordinate(lat, lon)));
                    }
                } catch(Exception e) { /* Einzelnen Fehler ignorieren */ }
            }
            
            Collections.shuffle(adressPool);
            System.out.println("Erfolgreich " + adressPool.size() + " reale Schweriner Adressen geladen!");
            
        } catch (Exception e) {
            System.out.println("Fehler beim Adressen laden: " + e.getMessage());
        }
    }

    // NEUE HILFSMETHODE: Kugelsicherer Parser, dem Leerzeichen im JSON egal sind!
    private static String safeExtract(String text, String key, boolean isString) {
        String search = "\"" + key + "\"";
        int idx = text.indexOf(search);
        if (idx == -1) return null;
        idx += search.length();
        
        // Ueberspringt alle Leerzeichen und Doppelpunkte
        while (idx < text.length() && (text.charAt(idx) == ' ' || text.charAt(idx) == ':')) {
            idx++;
        }
        
        if (isString) {
            if (text.charAt(idx) == '"') {
                idx++;
                int end = text.indexOf("\"", idx);
                if (end != -1) return text.substring(idx, end);
            }
        } else {
            // Für Zahlen (lat/lon)
            int end = idx;
            while (end < text.length() && (Character.isDigit(text.charAt(end)) || text.charAt(end) == '.' || text.charAt(end) == '-')) {
                end++;
            }
            return text.substring(idx, end);
        }
        return null;
    }

    private static String extractString(String text, String startTag, String endTag) {
        int start = text.indexOf(startTag);
        if(start == -1) return null;
        start += startTag.length();
        int end = text.indexOf(endTag, start);
        if(end == -1) return null;
        return text.substring(start, end).trim();
    }

    // ==========================================
    // NOTRUF GENERIEREN
    // ==========================================
    public static CtNotruf generiereNotruf() {
        CtNotruf n = new CtNotruf();
        n.anruferName = vornamen[rand.nextInt(vornamen.length)] + " " + nachnamen[rand.nextInt(nachnamen.length)];
        
        // --- DIE MAGIE: Wir bedienen uns einfach aus dem heruntergeladenen Pool! ---
        if (!adressPool.isEmpty()) {
            EchteAdresse realeAdresse = adressPool.get(rand.nextInt(adressPool.size()));
            n.strasse = realeAdresse.adresseKomplett;
            n.koordinate = realeAdresse.koordinate;
        } else {
            // Fallback (sollte dank Internet-Check nicht passieren)
            n.strasse = "Ersatzstraße 1";
            n.koordinate = new Coordinate(53.6333, 11.4166); 
        }
        
        EinsatzTyp typ = einsatzKatalog.get(rand.nextInt(einsatzKatalog.size()));
        n.stichwort = typ.stichwort;
        n.kurzBeschreibung = typ.name;
        n.istPanisch = typ.immerPanisch || (rand.nextDouble() > 0.7); 
        
        n.textIntro = typ.introTexte[rand.nextInt(typ.introTexte.length)];
        n.textWas = typ.wasTexte[rand.nextInt(typ.wasTexte.length)];
        n.textVerletzte = typ.verletzteTexte[rand.nextInt(typ.verletzteTexte.length)];
        
        if (n.istPanisch) {
            n.textIntro = n.textIntro.toUpperCase(); 
            n.textWo = "Hier, in der " + n.strasse + "!!! Bitte beeilen Sie sich!!!";
        } else {
            n.textWo = "Das ist in der " + n.strasse + ".";
        }
        
        return n;
    }
}