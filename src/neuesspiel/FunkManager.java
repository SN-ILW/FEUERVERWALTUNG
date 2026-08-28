package neuesspiel;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;

public class FunkManager {

    private static Timer kiTimer;
    private static Random rand = new Random();
    
    // Speichert die letzten 8 Funksprueche fuer das Hauptfenster
    public static LinkedList<String> funkHistorie = new LinkedList<>();

    private static String[] nachbarn = {
        "Parchim", "Ludwigslust", "Leezen", "Pampow", 
        "Klein Rogahn", "Groß Rogahn", "Muehlen Eichsen", "Grevesmuehlen"
    };
    private static String[] fahrzeugeFW = {"HLF 20", "LF 16/12", "TLF 4000", "DLK 23/12", "TSF-W", "ELW 1"};
    private static String[] fahrzeugeRD = {"RTW", "KTW", "NEF"};
    private static String[] stati = {
        "Status 1 (Frei auf Funk)", "Status 2 (Einsatzbereit auf Wache)", "Status 3 (Anfahrt zum Einsatzort)", 
        "Status 4 (Eingetroffen an EST)", "Status 6 (Ausser Dienst / Defekt)", "Status 8 (Am Krankenhaus an)"
    };
    private static String[] sonderMeldungen = {
        "Christoph 34: Sind gelandet, Notarzt ist beim Patienten.",
        "Polizei Schwerin: Wir benoetigen einen Abschlepper zur Einsatzstelle.",
        "Rettung Schwerin 10-82-1: Pol zur Einsatzstelle Unklare Todesursache.",
        "Polizei Autobahn: Vollsperrung eingerichtet!",
        "Christoph 34: Status 1, fliegen zurueck zum Klinikum."
    };

    public static void init() {
        if (kiTimer == null) {
            funkHistorie.add("SYSTEM: Funkueberwachung LST_FW/RD_01 aktiv.\n");
            kiTimer = new Timer(15000, e -> {
                int delay = 15000 + rand.nextInt(20000);
                kiTimer.setDelay(delay);
                generiereKiFunk();
            });
            kiTimer.start();
        }
    }

    public static void logMessage(String absender, String nachricht) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String text = "[" + time + "] " + absender + ": " + nachricht + "\n";
        
        funkHistorie.add(text);
        if (funkHistorie.size() > 8) {
            funkHistorie.removeFirst(); // Hält die Liste auf 8 Zeilen begrenzt
        }
        
        // Aktualisiert sofort das Hauptfenster, wenn ein neuer Funkspruch reinkommt!
        LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
    }

    private static void generiereKiFunk() {
        // Generiert nur KI-Funk, wenn es in den Einstellungen erlaubt ist!
        if (!LogistikSimulator.cfgKiFunk) return; 

        if (rand.nextDouble() > 0.85) {
            logMessage("FUNK", sonderMeldungen[rand.nextInt(sonderMeldungen.length)]);
        } else {
            String wache = nachbarn[rand.nextInt(nachbarn.length)];
            boolean istRettungsdienst = rand.nextBoolean();
            String fz = istRettungsdienst ? fahrzeugeRD[rand.nextInt(fahrzeugeRD.length)] : fahrzeugeFW[rand.nextInt(fahrzeugeFW.length)];
            String status = stati[rand.nextInt(stati.length)];
            String fzKenner = (istRettungsdienst ? "Rettung " : "Florian ") + wache + " " + fz;
            
            if(status.contains("Status 1")) {
                logMessage(fzKenner, "Status 1, sind wieder einsatzbereit.");
            } else {
                logMessage(fzKenner, "Drueckt " + status);
            }
        }
    }
}