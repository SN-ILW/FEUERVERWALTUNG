package neuesspiel;

public class VertragVorlage {
    public String auftraggeber;
    public String beschreibung;
    public String zielEinsatzArt; // "KTP", "R1", etc.
    public int zielMenge;
    public int belohnungProTag;
    public int strafeBeiFehlschlag;

    public VertragVorlage(String auftraggeber, String beschreibung, String zielEinsatzArt, int zielMenge, int belohnung, int strafe) {
        this.auftraggeber = auftraggeber;
        this.beschreibung = beschreibung;
        this.zielEinsatzArt = zielEinsatzArt;
        this.zielMenge = zielMenge;
        this.belohnungProTag = belohnung;
        this.strafeBeiFehlschlag = strafe;
    }
}