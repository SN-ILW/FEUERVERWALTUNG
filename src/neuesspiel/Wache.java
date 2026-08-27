package neuesspiel;

import java.util.ArrayList;
import java.util.HashMap;

public class Wache {
    public String name;
    public String kennung;
    public ArrayList<Fahrzeug> fuhrpark = new ArrayList<>();
    public ArrayList<Personal> personalPool = new ArrayList<>();
    public HashMap<String, Integer> fahrzeugCounter = new HashMap<>();
    public HashMap<String, Integer> material = new HashMap<>();
    public java.util.ArrayList<WachenAusbau> ausbauten = new java.util.ArrayList<>();
    public Wache(String name, String kennung) {
        this.name = name;
        this.kennung = kennung;
        fahrzeugCounter.put("HLF", 0);
        fahrzeugCounter.put("RTW", 0);
        fahrzeugCounter.put("ELW", 0);
        fahrzeugCounter.put("DLK", 0);
        fahrzeugCounter.put("NEF", 0);
        fahrzeugCounter.put("KTW", 0);
    }

    public void addFahrzeug(Fahrzeug f) {
        fuhrpark.add(f);
        fuhrpark.sort((f1, f2) -> f1.funkrufname.compareTo(f2.funkrufname));
    }

    public String generiereFunkrufname(String typ) {
        int count = fahrzeugCounter.getOrDefault(typ, 0) + 1;
        fahrzeugCounter.put(typ, count);
        
        String typKennung = "83"; 
        if(typ.equals("HLF")) typKennung = "43";
        else if(typ.equals("DLK")) typKennung = "23";
        else if(typ.equals("ELW")) typKennung = "11";
        else if(typ.equals("NEF")) typKennung = "82";
        else if(typ.equals("KTW")) typKennung = "85";
        
        return kennung + "-" + typKennung + "-" + String.format("%02d", count);
    }

    public boolean hatMaterial(String matName, int menge) {
        return material.getOrDefault(matName, 0) >= menge;
    }

    public void abziehenMaterial(String matName, int menge) {
        if (hatMaterial(matName, menge)) {
            material.put(matName, material.get(matName) - menge);
        }
    }
}