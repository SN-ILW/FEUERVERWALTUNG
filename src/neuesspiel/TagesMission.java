package neuesspiel;

public class TagesMission {
    public String titel;
    public String beschreibung;
    public String typ; // z.B. "KEINE_HILFE", "FEUERWEHR_PROFI"
    public int zielWert;
    public int fortschritt;
    public int belohnungGeld;
    public int belohnungXp;
    public boolean abgeschlossen;

    public TagesMission(String titel, String beschreibung, String typ, int zielWert, int geld, int xp) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.typ = typ;
        this.zielWert = zielWert;
        this.belohnungGeld = geld;
        this.belohnungXp = xp;
        this.fortschritt = 0;
        this.abgeschlossen = false;
    }
}