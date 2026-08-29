package neuesspiel;

import java.util.ArrayList;
import java.util.HashMap;

public class Einsatz {
    public EinsatzVorlage vorlage;
    public String alarmUhrzeit;
    public String beschreibung;
    public HashMap<String, Integer> reqMaterial = new HashMap<>();
    
    public int xpBelohnung = 0;
    public int belohnungGeld = 0;
    
    public boolean bereitZumLoeschen = false;
    public boolean lagemeldungAbgegeben = false;
    public boolean nachforderungBedient = false;
    public String nachforderungTyp = "";
    
    public int bearbeitungsZeit = 0;
    public boolean erstesFahrzeugDa = false;
    private StringBuilder lagemeldungHistorie = new StringBuilder();

    // --- NEUE FELDER FÜR DIE EINSATZAKTE ---
    public int patientenAnzahl = 0;
    public String patientenStatusText = "Keine Patienten betroffen";
    public String schadensObjekt = "";
    public ArrayList<String> einsatzProtokoll = new ArrayList<>();

    public Einsatz(EinsatzVorlage v, String zeit) {
        this.vorlage = v;
        this.alarmUhrzeit = zeit;
        this.beschreibung = v.beschreibung;
        this.belohnungGeld = 500 + (int)(Math.random() * 1000);
        
        if (v.hatNachforderung && Math.random() * 100 < v.nachforderungProzent) {
            this.nachforderungTyp = v.nachforderungTyp;
        }
        
        this.bearbeitungsZeit = 300 + (int)(Math.random() * 301);
        
        for(CustomMaterial cm : LogistikSimulator.customMaterials) {
            if(cm.einsatzStichworte.contains(v.stichwort) || cm.einsatzStichworte.isEmpty()) {
                if(Math.random() > 0.5) {
                    reqMaterial.put(cm.name, 1 + (int)(Math.random() * cm.maxVerbrauch));
                }
            }
        }
        
        lagemeldungHistorie.append("[").append(zeit).append("] Alarmierung: ").append(v.stichwort).append(" - ").append(v.beschreibung).append("\n");
        
        // Initialisiere die detaillierte Einsatzakte
        initEinsatzDetails();
    }
    
    private void initEinsatzDetails() {
        // Patientenanzahl je nach Stichwort zufällig bestimmen
        if (vorlage.art.equals("RD") || vorlage.art.equals("KTP")) {
            this.patientenAnzahl = 1 + (int)(Math.random() * 2);
            this.patientenStatusText = patientenAnzahl + "x Patient(en) versorgt / in Behandlung";
        } else if (vorlage.stichwort.startsWith("F2") || vorlage.stichwort.startsWith("F3")) {
            this.patientenAnzahl = (Math.random() < 0.4) ? (int)(1 + Math.random() * 3) : 0;
            this.patientenStatusText = (patientenAnzahl > 0) 
                ? patientenAnzahl + "x Person(en) mit Rauchgasintoxikation" 
                : "Keine Personenschäden";
        }

        // Schadensobjekt je nach Einsatz bestimmen
        if (beschreibung.contains("Dachstuhl") || vorlage.stichwort.contains("F2")) schadensObjekt = "Dachstuhl / Wohngebäude";
        else if (beschreibung.contains("BMA")) schadensObjekt = "Automatische Brandmeldeanlage";
        else if (beschreibung.contains("PKW") || beschreibung.contains("THL")) schadensObjekt = "Verkehrsunfall / Fahrzeuge";
        else if (vorlage.art.equals("FW")) schadensObjekt = "Unrat / Kleinbrand";
        else schadensObjekt = "Rettungsdiensteinsatzstelle";

        // Initialer Log in der Akte
        addProtokoll("Einsatz disponiert & Fahrzeuge alarmiert.");
    }

    // Fügt dem privaten Einsatz-Protokoll der Akte einen Funkspruch hinzu
    public void addProtokoll(String eintrag) {
        einsatzProtokoll.add("[" + LogistikSimulator.getUhrzeit() + "] " + eintrag);
    }

    public void fahrzeugAngekommen(Fahrzeug f, String uhrzeit) {
        if (!erstesFahrzeugDa) {
            erstesFahrzeugDa = true;
            lagemeldungAbgegeben = true;
            lagemeldungHistorie.append("[").append(uhrzeit).append("] 1. Lagemeldung: Erstes Fahrzeug eingetroffen. Einsatz wird bearbeitet...\n");
            
            addProtokoll(f.funkrufname + " ist eingetroffen. Erste Lagemeldung abgesetzt.");
            
            if (!nachforderungTyp.isEmpty()) {
                lagemeldungHistorie.append("[").append(uhrzeit).append("] NACHFORDERUNG: Es werden weitere Kraefte benoetigt: ").append(nachforderungTyp).append("\n");
                addProtokoll("Nachforderung abgesetzt! Benötigt: " + nachforderungTyp);
            }
        } else {
            addProtokoll(f.funkrufname + " ist an der Einsatzstelle eingetroffen.");
        }
    }

    public void checkLagemeldung(int speed, String uhrzeit) {
        if (erstesFahrzeugDa && (nachforderungBedient || nachforderungTyp.isEmpty())) {
            bearbeitungsZeit -= (speed * 8); 
            
            if (bearbeitungsZeit <= 0 && !bereitZumLoeschen) {
                lagemeldungHistorie.append("[").append(uhrzeit).append("] Abschlussmeldung: Einsatz erfolgreich beendet. Fahrzeuge ruecken ab.\n");
                addProtokoll("Abschlussmeldung: Einsatz beendet. Fahrzeuge rücken ab.");
                bereitZumLoeschen = true;
            }
        }
    }
    
    public String getLagemeldungText() {
        String txt = lagemeldungHistorie.toString();
        if (erstesFahrzeugDa && !bereitZumLoeschen && (nachforderungBedient || nachforderungTyp.isEmpty())) {
            txt += "\n(Einsatzbearbeitung: noch " + (bearbeitungsZeit/8) + " Spiel-Minuten)";
        } else if (erstesFahrzeugDa && !nachforderungBedient && !nachforderungTyp.isEmpty()) {
            txt += "\n(Wartet auf Eintreffen der Nachforderung!)";
        }
        return txt;
    }
}