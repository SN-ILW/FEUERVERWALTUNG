package neuesspiel;

public class MailGenerator {

    private static String generiereAbsender(String name) {
        return name.replace(" ", ".").toLowerCase() + "@sn-ilw.de";
    }

    public static Email generiereUrlaubsantrag(Personal p, int startTag, int endTag) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag,\n\n"
                    + "Mit dieser Mail beantrage ich, " + p.name + " (" + p.getPersonalNummer() + "), Urlaub vom " 
                    + LogistikSimulator.getDatumString(startTag) + " bis " + LogistikSimulator.getShortDatumString(endTag) + ".\n\n"
                    + "Ich bitte den Urlaub einzutragen.\n\n"
                    + "MfG\n" + p.name;
        return new Email(absender, "Urlaubsantrag", text, "Urlaub", p, startTag, endTag);
    }

    public static Email generiereKrankmeldung(Personal p, int tag, int krankBis) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag,\n\n"
                    + "Mit dieser Mail beantrage ich, " + p.name + " (" + p.getPersonalNummer() + "), eine Krankschreibung vom " 
                    + LogistikSimulator.getDatumString(tag) + " bis " + LogistikSimulator.getShortDatumString(krankBis) + ".\n\n"
                    + "Ich bitte den Ausfall einzutragen.\n\n"
                    + "MfG\n" + p.name;
        return new Email(absender, "Krankmeldung", text, "Krank", p, tag, krankBis);
    }

    public static Email generiereKrankVerlaengerung(Personal p, int tag, int neuesEnde) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag,\n\n"
                    + "leider bin ich weiterhin krank. Mit dieser Mail beantrage ich, " + p.name + " (" + p.getPersonalNummer() + "), die Verlaengerung meiner Krankschreibung bis zum " 
                    + LogistikSimulator.getShortDatumString(neuesEnde) + ".\n\n"
                    + "Ich bitte den Ausfall einzutragen.\n\n"
                    + "MfG\n" + p.name;
        return new Email(absender, "Verlaengerung Krankmeldung", text, "Krank", p, tag, neuesEnde);
    }

    public static Email generiereUrlaubVerlaengerung(Personal p, int tag, int neuesEnde) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag,\n\n"
                    + "Mit dieser Mail beantrage ich, " + p.name + " (" + p.getPersonalNummer() + "), eine Verlaengerung meines Urlaubs vom " 
                    + LogistikSimulator.getDatumString(tag) + " bis zum " + LogistikSimulator.getShortDatumString(neuesEnde) + ".\n\n"
                    + "Ich bitte den Urlaub einzutragen.\n\n"
                    + "MfG\n" + p.name;
        return new Email(absender, "Urlaubsverlaengerung", text, "Urlaub", p, tag, neuesEnde);
    }
    
    public static Email generiereAnwaerterWahl(Personal p, int tag, String praeferenz) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag,\n\n"
                    + "ich habe nun meine erste Schicht als Anwaerter beendet. Mir hat der Bereich " + praeferenz + " besonders gut gefallen.\n\n"
                    + "Ich wuerde mich freuen, wenn Sie mich fuer diesen Bereich uebernehmen.\n\n"
                    + "MfG\n" + p.name;
        return new Email(absender, "Uebernahme nach Anwaerter-Schicht", text, "Anwaerter", p, tag, tag);
    }

    public static Email generiereLehrgangsAnfrage(Personal p, int tag, String wunschLehrgang, int kosten) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag Chef,\n\n"
                    + "ich arbeite nun schon eine Weile im aktuellen Bereich und wuerde mich gerne weiterbilden.\n"
                    + "Koennte ich den Lehrgang zum " + wunschLehrgang + " besuchen?\n"
                    + "Die Kosten wuerden sich auf " + kosten + " EURO belaufen.\n\n"
                    + "MfG\n" + p.name;
        Email m = new Email(absender, "Anfrage auf Weiterbildung", text, "Lehrgang_Anfrage", p, tag, tag);
        m.text += "##" + wunschLehrgang + "##" + kosten; 
        return m;
    }

    public static Email generiereVorwissen(Personal p, int tag, String vorhandenerLehrgang) {
        String absender = generiereAbsender(p.name);
        String text = "Guten Tag,\n\n"
                    + "ich wurde heute frisch eingestellt. Zur Info: Ich habe in der Vergangenheit bereits den Lehrgang zum " + vorhandenerLehrgang + " erfolgreich abgeschlossen.\n\n"
                    + "Koennen Sie mir diesen anerkennen?\n\n"
                    + "MfG\n" + p.name;
        Email m = new Email(absender, "Anerkennung Vorwissen", text, "Vorwissen", p, tag, tag);
        m.text += "##" + vorhandenerLehrgang; 
        return m;
    }
}