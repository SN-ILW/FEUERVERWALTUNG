package neuesspiel;

import java.util.ArrayList;



public class Personal {
    public String name;
    public ArrayList<String> qualifikationen = new ArrayList<>();
    public String status = "Bereit";
    public String geplanterStatus = "Bereit"; 
    public String zugewiesenesFahrzeug = "Keines";
    public String geplantesFahrzeug = "Keines";
    public java.util.ArrayList<MitarbeiterEigenschaft> eigenschaften = new java.util.ArrayList<>();
    public int urlaubStart = -1;
    public int urlaubEnd = -1;
    public int krankBis = -1;
    public double stundenLohn = 13.50; 
    public int abgelehnteForderungen = 0;
    public int schichtenMonat = 0;
    public int lehrgangDauerSec = 0;
    public String lehrgangThema = "";
    
    public boolean praeferenzGesendet = false;

    // --- NEU: Der 31-Tage Schichtplan ---
    public String[] planAktuellerMonat = new String[31];
    public String[] planNaechsterMonat = new String[31];

    public Personal(String name, String qual) {
        this.name = name;
        if (!qual.isEmpty()) {
            String[] parts = qual.split(",");
            for (String part : parts) {
                this.qualifikationen.add(part.trim());
            }
        }
        
        // Arrays beim Erstellen standardmaessig mit "Frei" fuellen
        for(int i = 0; i < 31; i++) {
            planAktuellerMonat[i] = "Frei";
            planNaechsterMonat[i] = "Frei";
        }
    }
    
    

    // Im Konstruktor von Personal.java kannst du den Stundenlohn je nach Qualifikation setzen:
    public void initStundenLohn() {
        // Stundenlohn festlegen
        if (qualifikationen.contains("NA") || qualifikationen.contains("EL")) {
            stundenLohn = 22.50;
        } else if (qualifikationen.contains("GF") || qualifikationen.contains("NFS")) {
            stundenLohn = 17.00;
        } else if (qualifikationen.contains("TF") || qualifikationen.contains("MA")) {
            stundenLohn = 15.00;
        } else {
            stundenLohn = 13.00; // Anwaerter / TM / RS
        }
    }
    
    public String getPersonalNummer() {
        return String.format("%04d", Math.abs(name.hashCode()) % 10000);
    }
}