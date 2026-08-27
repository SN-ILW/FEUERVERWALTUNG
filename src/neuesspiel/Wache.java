package neuesspiel;

import java.util.ArrayList;
import java.util.HashMap;

public class Wache {
    public String name;
    public String kennung;
    public ArrayList<Fahrzeug> fuhrpark;
    public ArrayList<Personal> personalPool;
    public HashMap<String, Integer> material;
    
    // NEU: Hier speichert die Wache ihre eigenen, lokalen Gebaeude!
    public ArrayList<WachenAusbau> upgrades; 

    public Wache(String name, String kennung) {
        this.name = name;
        this.kennung = kennung;
        this.fuhrpark = new ArrayList<>();
        this.personalPool = new ArrayList<>();
        this.material = new HashMap<>();
        this.upgrades = new ArrayList<>(); // NEU: Initialisierung der Liste
    }

    public void addFahrzeug(Fahrzeug f) {
        fuhrpark.add(f);
    }

    public String generiereFunkrufname(String typ) {
        int anzahl = 1;
        for (Fahrzeug f : fuhrpark) {
            if (f.typ.equals(typ)) anzahl++;
        }
        return kennung + "/" + getTypKennziffer(typ) + "-" + anzahl;
    }

    private String getTypKennziffer(String typ) {
        switch (typ) {
            case "ELW": return "11";
            case "HLF": return "46";
            case "DLK": return "33";
            case "RTW": return "83";
            case "KTW": return "85";
            case "NEF": return "82";
            default: return "00";
        }
    }
    
    public boolean hatMaterial(String mName, int anzahl) {
        return material.getOrDefault(mName, 0) >= anzahl;
    }
}