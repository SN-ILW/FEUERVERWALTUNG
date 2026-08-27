package neuesspiel;

public class EinsatzVorlage {
    public String art; // "FW", "RD" oder "KTP"
    public String stichwort;
    public String beschreibung;
    
    // Benoetigte Fahrzeuge
    public int reqRTW, reqNEF, reqKTW, reqHLF, reqDLK, reqELW, reqTLF, reqMTW;
    
    public boolean hatNachforderung;
    public int nachforderungProzent;
    public String nachforderungTyp; 
    public int minLevel;

    public EinsatzVorlage(String art, String stichwort, String beschreibung, 
                          int reqRTW, int reqNEF, int reqKTW, 
                          int reqHLF, int reqDLK, int reqELW, int reqTLF, int reqMTW,
                          boolean hatNachforderung, int nachforderungProzent, 
                          String nachforderungTyp, int minLevel) {
        this.art = art;
        this.stichwort = stichwort;
        this.beschreibung = beschreibung;
        this.reqRTW = reqRTW;
        this.reqNEF = reqNEF;
        this.reqKTW = reqKTW;
        this.reqHLF = reqHLF;
        this.reqDLK = reqDLK;
        this.reqELW = reqELW;
        this.reqTLF = reqTLF;
        this.reqMTW = reqMTW;
        this.hatNachforderung = hatNachforderung;
        this.nachforderungProzent = nachforderungProzent;
        this.nachforderungTyp = nachforderungTyp;
        this.minLevel = minLevel;
    }
}