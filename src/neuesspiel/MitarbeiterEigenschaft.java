package neuesspiel;

public class MitarbeiterEigenschaft {
    public String name;
    public String beschreibung;
    public String typ; // z.B. "GESCHWINDIGKEIT", "IMMUN", "DIVA"
    public double effektWert; // Wie stark ist der Effekt (z.B. 1.1 fuer 10% mehr)

    public MitarbeiterEigenschaft(String name, String beschreibung, String typ, double effektWert) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.typ = typ;
        this.effektWert = effektWert;
    }
}