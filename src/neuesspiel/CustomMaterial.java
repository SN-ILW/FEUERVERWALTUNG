package neuesspiel;

import java.util.ArrayList;

public class CustomMaterial {
    public String name;
    public ArrayList<String> fahrzeuge;
    public int maxVerbrauch;
    public ArrayList<String> einsatzStichworte;
    public int preis;
    public int bestellMenge;
    public int warnSchwelle;

    public CustomMaterial(String name, ArrayList<String> fahrzeuge, int maxVerbrauch, ArrayList<String> einsatzStichworte, int preis, int bestellMenge, int warnSchwelle) {
        this.name = name;
        this.fahrzeuge = fahrzeuge;
        this.maxVerbrauch = maxVerbrauch;
        this.einsatzStichworte = einsatzStichworte;
        this.preis = preis;
        this.bestellMenge = bestellMenge;
        this.warnSchwelle = warnSchwelle;
    }
}