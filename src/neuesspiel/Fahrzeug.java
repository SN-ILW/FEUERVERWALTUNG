package neuesspiel;
import java.awt.Color;
public class Fahrzeug {
    public String funkrufname;
    public String typ;
    public int status = 2; // 1=Frei, 2=Wache, 3=Anfahrt, 4=Ort, 6=Defekt, 7=Patient geladen, 8=Transport
    public int anfahrtsZeit = 0;
    public int originalAnfahrt = 0; 
    public Einsatz aktuellerEinsatz = null;
    public String ausfallGrund = "";
    public int reparaturDauer = 0;
    public int kilometer = 0;
    public int naechsteInspektion = 1000;
    public int reqEL=0, reqGF=0, reqNA=0, reqNFS=0, reqMA=0, reqTF=0, reqFüAs=0, reqRS=0, reqTM=0;
    public int ausrueckeVerzoegerung = 0;
    
    public Fahrzeug(String kennung, String typ) {
        this.funkrufname = kennung;
        this.typ = typ;
        if(typ.equals("RTW")){ reqNFS=1; reqRS=1; }
        else if(typ.equals("KTW")){ reqRS=2; }
        else if(typ.equals("NEF")){ reqNA=1; reqNFS=1; }
        else if(typ.equals("HLF")){ reqGF=1; reqMA=1; reqTF=2; reqTM=2; }
        else if(typ.equals("DLK")){ reqTF=1; reqMA=1; }
        else if(typ.equals("ELW")){ reqEL=1; reqFüAs=1; }
        else if(typ.equals("TLF")){ reqTF=1; reqMA=1; }
        else if(typ.equals("MTW")){ reqTM=1; }
    }
    
    // Fuege das oben zu den anderen Variablen in deiner Fahrzeug.java hinzu:
    public Color stempelFarbe = new Color(192, 57, 43); // Standardmaessig ein schickes Rot
    
    public void tick(int speed, String uhrzeit) {
        // --- NEU: AUSRÜCKEVERZÖGERUNG ---
        if (ausrueckeVerzoegerung > 0) {
            ausrueckeVerzoegerung -= speed;
            if (ausrueckeVerzoegerung <= 0) {
                ausrueckeVerzoegerung = 0;
                this.status = 3;
                if (aktuellerEinsatz != null) {
                    aktuellerEinsatz.addProtokoll("📻 " + this.funkrufname + " funkt: Status 3 (Einsatzuebernahme, Ausgerueckt)");
                }
            }
            return; // Fahrzeug fährt erst los, wenn es ausgerüstet ist!
        }

        // --- ANFAHRT & RÜCKFAHRT ---
        if (status == 3 || status == 8 || (status == 1 && anfahrtsZeit > 0)) {
            anfahrtsZeit -= speed;
            if (anfahrtsZeit <= 0) {
                anfahrtsZeit = 0;
                
                if (status == 3) {
                    status = 4;
                    if (aktuellerEinsatz != null) {
                        aktuellerEinsatz.addProtokoll("📻 " + this.funkrufname + " funkt: Status 4 (Eingetroffen an EST)");
                        aktuellerEinsatz.fahrzeugAngekommen(this, uhrzeit);
                    }
                } else if (status == 8) {
                    status = 1;
                    if (aktuellerEinsatz != null) {
                        aktuellerEinsatz.addProtokoll("📻 " + this.funkrufname + " funkt: Status 1 (Patient uebergeben, wieder frei)");
                    }
                } else if (status == 1) {
                    status = 2; // Wieder auf der Wache angekommen
                }
            }
        }

        // --- REPARATUR ---
        if (status == 6 && reparaturDauer > 0 && (ausfallGrund.equals("In Bearbeitung") || ausfallGrund.equals("Personalwechsel"))) {
            reparaturDauer -= speed;
            if (reparaturDauer <= 0) {
                reparaturDauer = 0;
                status = 2;
                ausfallGrund = "";
            }
        }
    }
}