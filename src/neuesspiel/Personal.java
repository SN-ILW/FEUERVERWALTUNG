package neuesspiel;

import java.util.ArrayList;

public class Personal {
    public String name;
    public ArrayList<String> qualifikationen = new ArrayList<>();
    public String status = "Bereit";
    public String geplanterStatus = "Bereit"; 
    public String zugewiesenesFahrzeug = "Keines";
    public String geplantesFahrzeug = "Keines";
    
    public int urlaubStart = -1;
    public int urlaubEnd = -1;
    public int krankBis = -1;
    
    public int schichtenMonat = 0;
    public int lehrgangDauerSec = 0;
    public String lehrgangThema = "";
    
    public boolean praeferenzGesendet = false;

    public Personal(String name, String qual) {
        this.name = name;
        if (!qual.isEmpty()) {
            String[] parts = qual.split(",");
            for (String part : parts) {
                this.qualifikationen.add(part.trim());
            }
        }
    }
    
    public String getPersonalNummer() {
        return String.format("%04d", Math.abs(name.hashCode()) % 10000);
    }
}