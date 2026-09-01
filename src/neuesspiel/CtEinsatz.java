package neuesspiel;

import java.util.ArrayList;
import java.util.List;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public class CtEinsatz {
    
    public int einsatzId;
    public Coordinate ort;           // Wo auf der echten Karte ist das?
    public String stichwort;         // z.B. "F2 (Wohnungsbrand)"
    public String meldebild;         // z.B. "Brennt Küche, starke Rauchentwicklung"
    
    public List<String> alarmierteFahrzeuge; 
    
    // --- DAS TRIAGE-SYSTEM (PATIENTEN-STATUS) ---
    public boolean hatPatienten;
    public int patientenGesundheit;  // 100 = Stabil, 0 = Exitus (Game Over für diesen Einsatz)
    
    // Konstruktor, um einen neuen Einsatz zu erstellen
    public CtEinsatz(int einsatzId, Coordinate ort, String stichwort, String meldebild, boolean hatPatienten) {
        this.einsatzId = einsatzId;
        this.ort = ort;
        this.stichwort = stichwort;
        this.meldebild = meldebild;
        this.alarmierteFahrzeuge = new ArrayList<>();
        
        this.hatPatienten = hatPatienten;
        this.patientenGesundheit = 100; // Startet immer bei 100%
    }
    
    // Hilfsmethode, um ein alarmiertes Fahrzeug in die Liste einzutragen
    public void fzHinzufuegen(String fzName) {
        if (!alarmierteFahrzeuge.contains(fzName)) {
            alarmierteFahrzeuge.add(fzName);
        }
    }
    
    // Zieht dem Patienten Leben ab (Triage-Timer)
    public void verschlechtern(int schaden) {
        if (hatPatienten && patientenGesundheit > 0) {
            patientenGesundheit -= schaden;
            if (patientenGesundheit < 0) {
                patientenGesundheit = 0;
            }
        }
    }
    
    // Prüft, ob der Rettungsdienst zu spät kam
    public boolean isExitus() {
        return hatPatienten && patientenGesundheit <= 0;
    }
}