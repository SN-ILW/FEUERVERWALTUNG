package neuesspiel;

public class Bestellung {
    public String materialName;
    public int menge;
    public int dauerSec;

    public Bestellung(String mat, int menge, int dauer) {
        this.materialName = mat;
        this.menge = menge;
        this.dauerSec = dauer;
    }
}