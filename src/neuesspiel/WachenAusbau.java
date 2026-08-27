package neuesspiel;

public class WachenAusbau {
    public String id;
    public String name;
    public String beschreibung;
    public int kosten;
    public boolean freigeschaltet;

    public WachenAusbau(String id, String name, String beschreibung, int kosten) {
        this.id = id;
        this.name = name;
        this.beschreibung = beschreibung;
        this.kosten = kosten;
        this.freigeschaltet = false;
    }
}