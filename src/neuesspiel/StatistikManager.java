package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StatistikManager {

    public static void zeigeTagesZeitung(int tag, ArrayList<Einsatz> tagesStatistik, int abgelehnte, int tagesXP) {
        // Wir erstellen ein randloses Dialogfenster
        JDialog dialog = FensterManager.createFramelessDialog("Tagesabschluss - Schweriner Tagesblatt", 700, 600);
        
        // 1. Statistiken auswerten
        int geschafft = tagesStatistik.size();
        int patienten = 0;
        int feuerEinsaetze = 0;
        
        for(Einsatz e : tagesStatistik) {
            patienten += e.patientenAnzahl;
            if(e.vorlage.art.equals("FW")) feuerEinsaetze++;
        }
        
        // 2. Schlagzeile und Artikel basierend auf Leistung bestimmen
        String headline = "";
        String subtext = "";

        if (abgelehnte > 3) {
            headline = "LEITSTELLE IM CHAOS!";
            subtext = "Der Buergermeister zeigt sich besorgt: " + abgelehnte + " Notrufe blieben heute komplett unbeantwortet! 'Wir muessen dringend ueber das Budget und die Leitung der Feuerwehr sprechen', hieß es aus dem Rathaus. Ein dunkler Tag fuer die Sicherheit in unserer Stadt.";
        } else if (geschafft == 0) {
            headline = "RUHIGER TAG IN DER STADT";
            subtext = "Die Einsatzkraefte hatten einen ungewoehnlich entspannten Tag. Es wurden keine nennenswerten Zwischenfaelle gemeldet. Die Buerger genießen das ruhige Wetter.";
        } else if (feuerEinsaetze >= 3) {
            headline = "STADT IN FLAMMEN!";
            subtext = "Die Feuerwehr befand sich heute im Dauereinsatz. Mehrere Braende hielten die Stadt in Atem. Dank der großartigen und schnellen Leistung der Leitstelle konnte Schlimmeres verhindert werden. Insgesamt wurden " + patienten + " Personen medizinisch versorgt.";
        } else if (abgelehnte == 0 && geschafft > 5) {
            headline = "HELDEN DES ALLTAGS!";
            subtext = "Perfekte Organisation in der Einsatz-Zentrale: Alle " + geschafft + " anliegenden Einsaetze wurden souveraen abgearbeitet. Die Buerger danken den Frauen und Maennern von Feuerwehr und Rettungsdienst!";
        } else {
            headline = "SICHERHEIT GARANTIERT";
            subtext = "Ein routinierter Tag fuer unsere Einsatzkraefte neigt sich dem Ende zu. Insgesamt wurden " + geschafft + " Einsaetze professionell bearbeitet und " + patienten + " Patienten versorgt. Die Leitstelle hat alles im Griff.";
        }

        // 3. Zeitungs-Layout mit HTML & CSS bauen (ohne Emojis!)
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body { font-family: 'Times New Roman', Times, serif; background-color: #f4f1ea; color: #111; padding: 20px; text-align: center; }")
            .append(".header { border-bottom: 4px solid #111; border-top: 4px solid #111; padding: 15px 0; margin-bottom: 25px; }")
            .append(".title { font-size: 38px; font-weight: bold; text-transform: uppercase; letter-spacing: 3px; margin-bottom: 5px; }")
            .append(".date { font-size: 14px; color: #444; font-style: italic; }")
            .append(".headline { font-size: 28px; font-weight: bold; margin-bottom: 15px; color: #b71c1c; text-transform: uppercase; }") // Dunkelrote Schlagzeile
            .append(".article { font-size: 16px; line-height: 1.6; text-align: justify; margin: 0 30px; padding: 20px; border: 1px solid #ccc; background: #fff; box-shadow: 2px 2px 5px rgba(0,0,0,0.1); }")
            .append(".stats { margin-top: 25px; font-family: 'Consolas', monospace; font-size: 14px; text-align: left; padding: 15px; background: #e8e5df; border-left: 5px solid #111; display: inline-block; width: 80%; }")
            .append("</style></head><body>");

        String datum = LogistikSimulator.getDatumUndUhrzeit();

        // Header der Zeitung
        html.append("<div class='header'>")
            .append("<div class='title'>Schweriner Tagesblatt</div>")
            .append("<div class='date'>Abendausgabe zum Ende von Tag ").append(tag).append(" | ").append(datum).append("</div>")
            .append("</div>");

        // Schlagzeile und Text
        html.append("<div class='headline'>").append(headline).append("</div>");
        html.append("<div class='article'><b>Schwerin.</b> ").append(subtext).append("</div>");

        // Statistik-Kasten unten
        html.append("<div class='stats'>")
            .append("<b>OFFIZIELLE TAGES-STATISTIK DER LEITSTELLE:</b><br><br>")
            .append("Erfolgreiche Einsaetze: ").append(geschafft).append("<br>")
            .append("Versorgte Patienten: ").append(patienten).append("<br>")
            .append("Abgelehnte Notrufe: ").append(abgelehnte).append("<br><br>")
            .append("Erspielte Erfahrung (XP): +").append(tagesXP).append("<br>")
            .append("</div>");

        html.append("</body></html>");

        JEditorPane pane = new JEditorPane("text/html", html.toString());
        pane.setEditable(false);
        pane.setBorder(BorderFactory.createEmptyBorder());
        
        dialog.add(new JScrollPane(pane), BorderLayout.CENTER);

        // Schließen-Button unten
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(35, 35, 35));
        JButton btnClose = LogistikSimulator.createStyledButton("Zeitung weglegen & Naechsten Tag starten", new Color(39, 174, 96));
        btnClose.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnClose);

        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true); // Pausiert das Spiel, bis die Zeitung geschlossen wird
    }
}