package neuesspiel;

public class EinsatzVorlage {
    public String art; // "FW" oder "RD" oder "KTP"
    public String stichwort;
    public String beschreibung;
    
    public int reqRTW, reqNEF, reqKTW, reqHLF, reqDLK, reqELW;
    
    public boolean hatNachforderung;
    public int nachforderungProzent;
    public String nachforderungTyp;
    public int minLevel;

    public EinsatzVorlage(String art, String sw, String desc, int rtw, int nef, int ktw, int hlf, int dlk, int elw, boolean hatNach, int nachProz, String nachTyp, int minLevel) {
        this.art = art;
        this.stichwort = sw;
        this.beschreibung = desc;
        this.reqRTW = rtw;
        this.reqNEF = nef;
        this.reqKTW = ktw;
        this.reqHLF = hlf;
        this.reqDLK = dlk;
        this.reqELW = elw;
        this.hatNachforderung = hatNach;
        this.nachforderungProzent = nachProz;
        this.nachforderungTyp = nachTyp;
        this.minLevel = minLevel;
    }
}