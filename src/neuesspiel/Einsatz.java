package neuesspiel;

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
    }
    
    public void fahrzeugAngekommen(Fahrzeug f, String uhrzeit) {
        if (!erstesFahrzeugDa) {
            erstesFahrzeugDa = true;
            lagemeldungAbgegeben = true;
            lagemeldungHistorie.append("[").append(uhrzeit).append("] 1. Lagemeldung: Erstes Fahrzeug eingetroffen. Einsatz wird bearbeitet...\n");
            if (!nachforderungTyp.isEmpty()) {
                lagemeldungHistorie.append("[").append(uhrzeit).append("] NACHFORDERUNG: Es werden weitere Kraefte benoetigt: ").append(nachforderungTyp).append("\n");
            }
        }
    }

    public void checkLagemeldung(int speed, String uhrzeit) {
        if (erstesFahrzeugDa && (nachforderungBedient || nachforderungTyp.isEmpty())) {
            bearbeitungsZeit -= (speed * 8); 
            
            if (bearbeitungsZeit <= 0 && !bereitZumLoeschen) {
                lagemeldungHistorie.append("[").append(uhrzeit).append("] Abschlussmeldung: Einsatz erfolgreich beendet. Fahrzeuge ruecken ab.\n");
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