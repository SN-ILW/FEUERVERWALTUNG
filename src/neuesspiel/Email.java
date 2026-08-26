package neuesspiel;

public class Email {
    public String absender;
    public String betreff;
    public String text;
    public String typ; // "Krank", "Urlaub", "Info", "Anwaerter", "Lehrgang_Anfrage", "Vorwissen"
    public boolean gelesen = false;
    public Personal person;
    public int startTag;
    public int endTag;

    public Email(String absender, String betreff, String text, String typ, Personal person, int startTag, int endTag) {
        this.absender = absender;
        this.betreff = betreff;
        this.text = text;
        this.typ = typ;
        this.person = person;
        this.startTag = startTag;
        this.endTag = endTag;
    }
}