package neuesspiel;

public class MonatsZiel {
    public String titel;
    public String beschreibung;
    public String zielTyp; 
    public int zielWert;
    public int belohnungGeld;
    public boolean abgeschlossen;

    public MonatsZiel(String titel, String beschreibung, String zielTyp, int zielWert, int belohnungGeld) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.zielTyp = zielTyp;
        this.zielWert = zielWert;
        this.belohnungGeld = belohnungGeld;
        this.abgeschlossen = false;
    }
}