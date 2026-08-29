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
    public int maxBearbeitungsZeit = 0; // NEU: Speichert die Start-Zeit für Prozentrechnung
    public boolean erstesFahrzeugDa = false;
    private StringBuilder lagemeldungHistorie = new StringBuilder();

    // Felder für die Einsatzakte
    public int patientenAnzahl = 0;
    public String patientenStatusText = "Keine Patienten betroffen";
    public String schadensObjekt = "";
    public ArrayList<String> einsatzProtokoll = new ArrayList<>();
    
    // NEU: Schalter für dynamische Meldungen
    private boolean meldung75 = false;
    private boolean meldung50 = false;
    private boolean meldung25 = false;

    public Einsatz(EinsatzVorlage v, String zeit) {
        this.vorlage = v;
        this.alarmUhrzeit = zeit;
        this.beschreibung = v.beschreibung;
        this.belohnungGeld = 500 + (int)(Math.random() * 1000);
        
        if (v.hatNachforderung && Math.random() * 100 < v.nachforderungProzent) {
            this.nachforderungTyp = v.nachforderungTyp;
        }
        
        this.bearbeitungsZeit = 300 + (int)(Math.random() * 301);
        this.maxBearbeitungsZeit = this.bearbeitungsZeit; // Startwert merken
        
        for(CustomMaterial cm : LogistikSimulator.customMaterials) {
            if(cm.einsatzStichworte.contains(v.stichwort) || cm.einsatzStichworte.isEmpty()) {
                if(Math.random() > 0.5) {
                    reqMaterial.put(cm.name, 1 + (int)(Math.random() * cm.maxVerbrauch));
                }
            }
        }
        
        lagemeldungHistorie.append("[").append(zeit).append("] Alarmierung: ").append(v.stichwort).append(" - ").append(v.beschreibung).append("\n");
        initEinsatzDetails();
    }
    
    private void initEinsatzDetails() {
        if (vorlage.art.equals("RD") || vorlage.art.equals("KTP")) {
            this.patientenAnzahl = 1 + (int)(Math.random() * 2);
            this.patientenStatusText = patientenAnzahl + "x Patient(en) versorgt / in Behandlung";
        } else if (vorlage.stichwort.startsWith("F2") || vorlage.stichwort.startsWith("F3")) {
            this.patientenAnzahl = (Math.random() < 0.4) ? (int)(1 + Math.random() * 3) : 0;
            this.patientenStatusText = (patientenAnzahl > 0) ? patientenAnzahl + "x Person(en) mit Rauchgasintoxikation" : "Keine Personenschäden";
        }

        if (beschreibung.contains("Dachstuhl") || vorlage.stichwort.contains("F2")) schadensObjekt = "Dachstuhl / Wohngebäude";
        else if (beschreibung.contains("BMA")) schadensObjekt = "Automatische Brandmeldeanlage";
        else if (beschreibung.contains("PKW") || beschreibung.contains("THL")) schadensObjekt = "Verkehrsunfall / Fahrzeuge";
        else if (vorlage.art.equals("FW")) schadensObjekt = "Unrat / Kleinbrand";
        else schadensObjekt = "Rettungsdiensteinsatzstelle";

        addProtokoll("Einsatz disponiert & Fahrzeuge alarmiert.");
    }

    public void addProtokoll(String eintrag) {
        String uhrzeit = LogistikSimulator.getUhrzeit();
        einsatzProtokoll.add("[" + uhrzeit + "] " + eintrag);
        
        // Damit es auch auf der rechten Seite direkt als "System-Nachricht" auftaucht:
        FunkManager.funkHistorie.add("[" + uhrzeit + "] EINSATZ UPDATE: " + eintrag + "\n");
        if (FunkManager.funkHistorie.size() > 8) FunkManager.funkHistorie.removeFirst();
    }

    public void fahrzeugAngekommen(Fahrzeug f, String uhrzeit) {
        if (!erstesFahrzeugDa) {
            erstesFahrzeugDa = true;
            lagemeldungAbgegeben = true;
            
            // Dynamische Eintreff-Meldung
            String lageText = "";
            if (vorlage.art.equals("FW")) lageText = "Erste Lage auf Sicht: " + schadensObjekt + " bestaetigt. Wir leiten die Erkundung ein.";
            else lageText = "Eingetroffen. Patient angetroffen, wir beginnen mit der Erstversorgung.";
            
            lagemeldungHistorie.append("[").append(uhrzeit).append("] 1. Lagemeldung: ").append(lageText).append("\n");
            addProtokoll(f.funkrufname + " funkt: " + lageText);
            
            if (!nachforderungTyp.isEmpty()) {
                lagemeldungHistorie.append("[").append(uhrzeit).append("] NACHFORDERUNG: Es werden weitere Kraefte benoetigt: ").append(nachforderungTyp).append("\n");
                addProtokoll("Kritische Lage! Nachforderung abgesetzt: " + nachforderungTyp);
            }
        } else {
            addProtokoll(f.funkrufname + " ist ebenfalls an der Einsatzstelle eingetroffen.");
        }
    }

    public void checkLagemeldung(int speed, String uhrzeit) {
        if (erstesFahrzeugDa && (nachforderungBedient || nachforderungTyp.isEmpty())) {
            bearbeitungsZeit -= (speed * 8); 
            
            // Fortschritt berechnen (1.0 = 100%, 0.0 = 0%)
            double fortschritt = (double) bearbeitungsZeit / maxBearbeitungsZeit;
            
            // DYNAMISCHE MELDUNGEN AUSLÖSEN
            if (fortschritt <= 0.75 && !meldung75) {
                meldung75 = true;
                if (vorlage.art.equals("FW")) addProtokoll("Erkundung abgeschlossen. Angriffstrupp geht zur Brandbekaempfung vor.");
                else addProtokoll("Vitalparameter des Patienten aufgenommen. Verdachtsdiagnose wird geprueft.");
            }
            else if (fortschritt <= 0.50 && !meldung50) {
                meldung50 = true;
                if (vorlage.art.equals("FW")) addProtokoll("Lagemeldung: Feuer unter Kontrolle! Keine weitere Ausbreitung.");
                else addProtokoll("Patient ist kreislaufstabil. Behandlung wird fortgesetzt.");
            }
            else if (fortschritt <= 0.25 && !meldung25) {
                meldung25 = true;
                if (vorlage.art.equals("FW")) addProtokoll("Letzte Glutnester werden abgeloescht. Nachloescharbeiten laufen.");
                else addProtokoll("Transportvorbereitungen laufen. Patient wird gleich verladen.");
            }
            
            if (bearbeitungsZeit <= 0 && !bereitZumLoeschen) {
                lagemeldungHistorie.append("[").append(uhrzeit).append("] Abschlussmeldung: Einsatz erfolgreich beendet. Fahrzeuge ruecken ab.\n");
                addProtokoll("Abschlussmeldung: Einsatzstelle uebergeben/beendet. Wir machen uns frei.");
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