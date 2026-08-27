package neuesspiel;

public class Vertrag {
    public String auftraggeber;
    public String beschreibung;
    public String zielEinsatzArt; // z.B. "KTP"
    public int zielMenge;
    public int aktuelleMenge;
    public int belohnungProTag;
    public int strafeBeiFehlschlag;

    public Vertrag(String auftraggeber, String beschreibung, String zielEinsatzArt, int zielMenge, int belohnung, int strafe) {
        this.auftraggeber = auftraggeber;
        this.beschreibung = beschreibung;
        this.zielEinsatzArt = zielEinsatzArt;
        this.zielMenge = zielMenge;
        this.belohnungProTag = belohnung;
        this.strafeBeiFehlschlag = strafe;
        this.aktuelleMenge = 0;
    }
}