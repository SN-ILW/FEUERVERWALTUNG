package neuesspiel;

public class Event {
    public String name;
    public String beschreibung;
    public int dauerTage;
    public boolean aktiv = true;

    // Multiplikatoren fuer bestimmte Einsatzarten waehrend des Events
    public double chanceR1 = 1.0;
    public double chanceH1 = 1.0;
    
    // Globale Notruf-Raten-Erhoehung waehrend des Events (z.B. 1.5 fuer 50% mehr Einsaetze allgemein)
    public double globalRateMultiplier = 1.0;

    public Event(String name, String beschreibung, int dauerTage, double chanceR1, double chanceH1, double globalRateMultiplier) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.dauerTage = dauerTage;
        this.chanceR1 = chanceR1;
        this.chanceH1 = chanceH1;
        this.globalRateMultiplier = globalRateMultiplier;
    }
}
