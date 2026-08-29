package neuesspiel;

import javax.swing.*;
import java.awt.*;

public class PersonalManager {

    // Wird jeden Abend um 19 Uhr ausgefuehrt
    public static void tagesAuswertung() {
        if (!LogistikSimulator.cfgZufriedenheit) return;

        StringBuilder sb = new StringBuilder();
        boolean jemandGekuendigt = false;

        for (Wache w : LogistikSimulator.wachen) {
            // Wir gehen die Liste rueckwaerts durch, damit wir sicher loeschen koennen
            for (int i = w.personalPool.size() - 1; i >= 0; i--) {
                Personal p = w.personalPool.get(i);
                
                // Zu viele Schichten? = Stress
                if (p.schichtenMonat > 20) {
                    p.zufriedenheit -= 10;
                } else if (p.schichtenMonat > 15) {
                    p.zufriedenheit -= 5;
                } else if (p.schichtenMonat < 10 && p.zufriedenheit < 100) {
                    p.zufriedenheit += 5; // Erholung
                }

                // Hatte die Person Urlaub? = Happy
                if (p.status.equals("Urlaub")) {
                    p.zufriedenheit += 10;
                }

                // Darf nicht ueber 100 gehen
                if (p.zufriedenheit > 100) p.zufriedenheit = 100;

                // Kuendigungsgrund erreicht?
                if (p.zufriedenheit <= 0) {
                    p.zufriedenheit = 0;
                    sb.append("- ").append(p.name).append(" (Wache: ").append(w.name).append(")\n");
                    w.personalPool.remove(i);
                    jemandGekuendigt = true;
                }
            }
        }

        // Benachrichtigung an den Spieler, falls Leute hingeworfen haben
        if (jemandGekuendigt) {
            LogistikSimulator.postfach.add(new Email(
                "Personalabteilung", 
                "Massen-Kuendigung!", 
                "Folgende Mitarbeiter haben wegen unzumutbarer Arbeitsbedingungen (Zufriedenheit 0%) fristlos gekuendigt und ihre Sachen gepackt:\n\n" + sb.toString() + "\nBitte achte besser auf den Schichtplan!", 
                "Info", null, LogistikSimulator.tag, LogistikSimulator.tag
            ));
        }
    }

    // Bestrafung, wenn man im Postfach einen Urlaub ablehnt
    public static void urlaubAbgelehnt(Personal p) {
        if (!LogistikSimulator.cfgZufriedenheit) return;
        p.zufriedenheit -= 20;
        if(p.zufriedenheit < 0) p.zufriedenheit = 0;
    }

    // Oeffnet ein komplett neues Menue nur fuer Akten und Kuendigungen
    public static void oeffneVerwaltung() {
        JDialog d = FensterManager.createFramelessDialog("Personal-Akten & Kuendigungen", 650, 500);
        d.setLayout(new BorderLayout(10, 10));
        
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(new Color(35, 35, 35));

        for (Wache w : LogistikSimulator.wachen) {
            JLabel wLabel = new JLabel("=== " + w.name + " ===");
            wLabel.setForeground(new Color(241, 196, 15));
            wLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            wLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlMain.add(Box.createRigidArea(new Dimension(0, 10)));
            pnlMain.add(wLabel);
            pnlMain.add(Box.createRigidArea(new Dimension(0, 10)));

            for (Personal p : w.personalPool) {
                JPanel pnlPers = new JPanel(new BorderLayout());
                pnlPers.setBackground(new Color(45, 45, 45));
                pnlPers.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                pnlPers.setMaximumSize(new Dimension(600, 45));

                String qualis = String.join(", ", p.qualifikationen);
                JLabel lblName = new JLabel("  " + p.name + " [" + qualis + "] - Schichten: " + p.schichtenMonat);
                lblName.setForeground(Color.WHITE);
                lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                JProgressBar pbZufriedenheit = new JProgressBar(0, 100);
                pbZufriedenheit.setValue(p.zufriedenheit);
                pbZufriedenheit.setStringPainted(true);
                pbZufriedenheit.setString("Zufriedenheit: " + p.zufriedenheit + "%");
                
                // Farbe der Leiste je nach Stimmung
                if(p.zufriedenheit < 30) pbZufriedenheit.setForeground(new Color(192, 57, 43)); // Rot
                else if(p.zufriedenheit < 70) pbZufriedenheit.setForeground(new Color(243, 156, 18)); // Orange
                else pbZufriedenheit.setForeground(new Color(39, 174, 96)); // Gruen

                JButton btnFire = LogistikSimulator.createStyledButton("Kuendigen", new Color(192, 57, 43));
                btnFire.addActionListener(e -> {
                    int wahl = JOptionPane.showConfirmDialog(d, "Willst du " + p.name + " wirklich fristlos entlassen?\nEr/Sie verlaesst die Wache sofort.", "Kuendigung", JOptionPane.YES_NO_OPTION);
                    if (wahl == JOptionPane.YES_OPTION) {
                        w.personalPool.remove(p);
                        d.dispose();
                        oeffneVerwaltung(); // Fenster aktualisieren
                        LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                    }
                });

                JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                pnlRight.setOpaque(false);
                pnlRight.add(pbZufriedenheit);
                pnlRight.add(btnFire);

                pnlPers.add(lblName, BorderLayout.WEST);
                pnlPers.add(pnlRight, BorderLayout.EAST);
                pnlMain.add(pnlPers);
                pnlMain.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        JScrollPane scroll = new JScrollPane(pnlMain);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(new Color(35, 35, 35));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        d.add(scroll, BorderLayout.CENTER);

        JButton btnClose = LogistikSimulator.createStyledButton("Schliessen", new Color(108, 122, 137));
        btnClose.addActionListener(e -> d.dispose());
        
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBottom.setBackground(new Color(35, 35, 35));
        pnlBottom.add(btnClose);
        d.add(pnlBottom, BorderLayout.SOUTH);

        d.setVisible(true);
    }
}