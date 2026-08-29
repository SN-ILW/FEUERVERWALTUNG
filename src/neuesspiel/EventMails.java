package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class EventMails {

    private static Random rand = new Random();

    public static class EventMail {
        public String absender;
        public String betreff;
        public String nachricht;
        public int tag;           // Absoluter Spieltag (z.B. Tag 45)
        public int startSlot;     // Welcher Zeitslot (0 = 07:00, 4 = 09:00 etc.)
        public int dauerSlots;    // Wie viele halbe Stunden?
        public int verguetung;    // Wie viel Geld gibts dafuer?
        public String eventName;  // Kurzer Text fuer den Kalender
    }

    public static EventMail generiereZufaelligeAnfrage() {
        EventMail mail = new EventMail();
        // Das Event findet 2 bis 45 Tage in der Zukunft statt!
        mail.tag = LogistikSimulator.tag + 2 + rand.nextInt(40); 
        String echtesDatum = LogistikSimulator.getShortDatumString(mail.tag);
        
        int typ = rand.nextInt(3); 
        
        if (typ == 0) {
            // KTP = Kurz und punktuell
            mail.absender = "Transport@Helios-Schwerin.de (Weitergeleitet durch Fahrdienst@sn-ilw.de)";
            mail.betreff = "Anforderung KTP (Stichwort: KTP)";
            mail.startSlot = rand.nextInt(15); 
            mail.dauerSlots = 2 + rand.nextInt(3); 
            mail.verguetung = 150 + rand.nextInt(200);
            mail.eventName = "KTP Helios";
            
            mail.nachricht = "Guten Tag Leitstelle,\n\n"
                    + "fuer den " + echtesDatum + " benoetigen wir einen Krankentransportwagen (KTW oder RTW) "
                    + "fuer eine Verlegung von unserer Station 4 in die Reha-Klinik Leezen.\n"
                    + "Die Abholung sollte gegen " + berechneUhrzeit(mail.startSlot) + " Uhr erfolgen.\n\n"
                    + "Verguetung gemaess Katalog: " + mail.verguetung + " EUR.\n\n"
                    + "Bitte um kurze Bestaetigung der Uebernahme.\n\n"
                    + "Mit freundlichen Gruessen\nHelios Zentrales Belegungsmanagement";
                    
        } else if (typ == 1) {
            // Schulen / Kindergaerten = Gesamte Schicht
            String[] schulen = {"Werner-von-Siemens-Schule.de", "Grundschule-Lankow.de", "Kita-Sonnenschein.de"};
            mail.absender = "Sekretariat@" + schulen[rand.nextInt(schulen.length)];
            mail.betreff = "Anfrage: Brandschutzerziehung / Fahrzeugschau";
            mail.startSlot = 0; // Ganze Schicht (Start um 07:00 Uhr)
            mail.dauerSlots = 25; // 25 Slots = 12.5 Stunden (07:00 bis 19:30 Uhr)
            mail.verguetung = 800 + rand.nextInt(500);
            mail.eventName = "Event (Schule)";
            
            mail.nachricht = "Guten Tag liebe Feuerwehr Schwerin,\n\n"
                    + "wir veranstalten am " + echtesDatum + " unseren jaehrlichen Projekttag. "
                    + "Wir wuerden mit den Kindern gerne das Thema Feuerwehr & Rettungsdienst behandeln.\n\n"
                    + "Waere es moeglich, dass Sie an diesem Tag fuer die gesamte Schicht (07:00 bis 19:00 Uhr) mit einem "
                    + "HLF oder RTW bei uns vorbeikommen, damit alle Klassen die Ausruestung bestaunen koennen?\n"
                    + "Wir haben ein Budget von " + mail.verguetung + " EUR aus der Foerderkasse dafuer vorgesehen.\n\n"
                    + "Wir freuen uns auf Ihre Rueckmeldung!\n\n"
                    + "Mit freundlichen Gruessen\nDas Schulsekretariat";
                    
        } else {
            // Betriebe / Firmen = Gesamte Schicht
            String[] firmen = {"SchoellerAlibert.de", "Nestle-Werk.de", "Stadtwerke-SN.de", "Pampow-Logistik.de"};
            mail.absender = "Verwaltung@" + firmen[rand.nextInt(firmen.length)];
            mail.betreff = "Brandsicherheitswache / Erste Hilfe Auffrischung";
            mail.startSlot = 0; // Ganze Schicht (Start um 07:00 Uhr)
            mail.dauerSlots = 25; // 25 Slots = 12.5 Stunden
            mail.verguetung = 1500 + rand.nextInt(1000);
            mail.eventName = "Event (Firma)";
            
            mail.nachricht = "Sehr geehrte Damen und Herren,\n\n"
                    + "am " + echtesDatum + " benoetigen wir aufgrund von ganztägigen Wartungsarbeiten an unserer "
                    + "Sprinkleranlage eine offizielle Brandsicherheitswache inkl. Erste-Hilfe-Auffrischung fuer unsere Schichtleiter.\n\n"
                    + "Wir benoetigen ein Einsatzfahrzeug (HLF oder RTW) fuer die gesamte Schicht (07:00 bis 19:00 Uhr) "
                    + "auf unserem Werksgelaende.\n"
                    + "Fuer diesen Service zahlen wir " + mail.verguetung + " EUR an die Wache.\n\n"
                    + "Bitte teilen Sie uns mit, ob Sie den Termin wahrnehmen koennen oder einen Ersatztermin vorschlagen.\n\n"
                    + "Mit freundlichen Gruessen\nDie Werksleitung";
        }
        
        return mail;
    }

    private static String berechneUhrzeit(int slot) {
        int stunde = 7 + (slot / 2);
        String minute = (slot % 2 == 0) ? "00" : "30";
        return String.format("%02d:%s", stunde, minute);
    }

    public static void zeigeMailAn(EventMail mail) {
        JDialog d = new JDialog(LogistikSimulator.frame, "Neue Terminanfrage", true);
        d.setSize(600, 450);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout(10, 10));
        
        JTextArea txt = new JTextArea("Von: " + mail.absender + "\n"
                + "Betreff: " + mail.betreff + "\n"
                + "--------------------------------------------------------\n\n"
                + mail.nachricht);
        txt.setEditable(false);
        txt.setMargin(new Insets(15, 15, 15, 15));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        d.add(new JScrollPane(txt), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAnnehmen = new JButton("Annehmen (" + mail.verguetung + " EUR)");
        btnAnnehmen.setBackground(new Color(39, 174, 96)); btnAnnehmen.setForeground(Color.WHITE);
        
        JButton btnAblehnen = new JButton("Ablehnen");
        btnAblehnen.setBackground(new Color(192, 57, 43)); btnAblehnen.setForeground(Color.WHITE);
        
        JButton btnVerschieben = new JButton("Termin vorschlagen");
        btnVerschieben.setBackground(new Color(241, 196, 15)); btnVerschieben.setForeground(Color.BLACK);
        
        btnAnnehmen.addActionListener(e -> {
            Terminkalender.autoEintragen(mail.tag, mail.startSlot, mail.dauerSlots, "RESERVIERT: " + mail.eventName);
            LogistikSimulator.budget += mail.verguetung; 
            JOptionPane.showMessageDialog(d, "Termin bestaetigt und in den Kalender eingetragen.\n" + mail.verguetung + " EUR wurden verbucht!");
            d.dispose();
        });
        
        btnAblehnen.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "Termin wurde abgelehnt.");
            d.dispose();
        });
        
        btnVerschieben.addActionListener(e -> {
            int tageSpäter = 1 + rand.nextInt(3);
            JOptionPane.showMessageDialog(d, "Gegenvorschlag gesendet. Die Antwort dauert voraussichtlich " + tageSpäter + " Tag(e).");
            d.dispose();
        });
        
        btnPanel.add(btnAnnehmen);
        btnPanel.add(btnVerschieben);
        btnPanel.add(btnAblehnen);
        
        d.add(btnPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
    public static void zeigeGehaltsVerhandlung(Personal p, double forderung, Email mail) {
        JDialog d = new JDialog(LogistikSimulator.frame, "Gehaltsverhandlung: " + p.name, true);
        d.setSize(600, 400);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout(10, 10));
        
        JTextArea txt = new JTextArea("Absender: " + p.name + " (" + String.join(", ", p.qualifikationen) + ")\n"
                + "Betreff: Antrag auf Anpassung der Vergütung\n"
                + "--------------------------------------------------------\n\n"
                + "Hallo Leitstelle,\n\n"
                + "ich bin nun seit geraumer Zeit auf der Wache tätig und habe bereits " + p.schichtenMonat + " Schichten diesen Monat absolviert.\n"
                + "Mein aktueller Stundenlohn liegt bei " + String.format("%.2f", p.stundenLohn) + " EUR.\n\n"
                + "Aufgrund meiner Leistungen beantrage ich eine Anpassung meines Stundenlohns auf " + String.format("%.2f", forderung) + " EUR.\n\n"
                + "Mit freundlichen Grüßen,\n" + p.name);
        txt.setEditable(false);
        txt.setMargin(new Insets(15, 15, 15, 15));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        d.add(new JScrollPane(txt), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnGenehmigen = new JButton("Genehmigen (" + String.format("%.2f", forderung) + " €/h)");
        btnGenehmigen.setBackground(new Color(39, 174, 96)); btnGenehmigen.setForeground(Color.WHITE);
        
        JButton btnGegenangebot = new JButton("Gegenangebot machen");
        btnGegenangebot.setBackground(new Color(241, 196, 15)); btnGegenangebot.setForeground(Color.BLACK);
        
        JButton btnAblehnen = new JButton("Ablehnen");
        btnAblehnen.setBackground(new Color(192, 57, 43)); btnAblehnen.setForeground(Color.WHITE);
        
        btnGenehmigen.addActionListener(e -> {
            p.stundenLohn = forderung;
            p.abgelehnteForderungen = 0;
            JOptionPane.showMessageDialog(d, p.name + " freut sich über die Gehaltserhöhung auf " + String.format("%.2f", forderung) + " €/h!");
            LogistikSimulator.postfach.remove(mail);
            LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
            d.dispose();
        });
        
        btnGegenangebot.addActionListener(e -> {
            double kompromiss = p.stundenLohn + ((forderung - p.stundenLohn) / 2.0);
            String eingabe = JOptionPane.showInputDialog(d, "Dein Gegenangebot als Stundenlohn (in €):", String.format("%.2f", kompromiss).replace(",", "."));
            if (eingabe != null) {
                try {
                    double gebot = Double.parseDouble(eingabe.replace(",", "."));
                    if (gebot > p.stundenLohn && gebot < forderung) {
                        p.stundenLohn = gebot;
                        JOptionPane.showMessageDialog(d, "Einigung erzielt! Neuer Stundenlohn: " + String.format("%.2f", gebot) + " €/h.");
                        LogistikSimulator.postfach.remove(mail);
                        LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                        d.dispose();
                    } else {
                        JOptionPane.showMessageDialog(d, "Ungültiges Gegenangebot!");
                    }
                } catch(Exception ex) { JOptionPane.showMessageDialog(d, "Zahlenformat ungültig!"); }
            }
        });
        
        btnAblehnen.addActionListener(e -> {
            p.abgelehnteForderungen++;
            if (p.abgelehnteForderungen >= 2) {
                JOptionPane.showMessageDialog(d, "Achtung: " + p.name + " ist sehr unzufrieden mit der erneuten Ablehnung!", "Unzufriedenheit", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(d, "Gehaltserhöhung abgelehnt.");
            }
            LogistikSimulator.postfach.remove(mail);
            LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
            d.dispose();
        });
        
        btnPanel.add(btnGenehmigen);
        btnPanel.add(btnGegenangebot);
        btnPanel.add(btnAblehnen);
        
        d.add(btnPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
}