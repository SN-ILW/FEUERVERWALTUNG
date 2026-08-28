package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WachalltagSimulator {

    private static JFrame f;
    public static JFrame wacheFrame; 
    private static JComboBox<PersonalItem> cbMA, cbGF, cbATF, cbATM, cbWTF, cbWTM;
    private static ArrayList<PersonalItem> auswahlListe;
    
    // Die Staffel-Besatzung (6 Personen: MA, GF, ATF, ATM, WTF, WTM)
    private static Personal[] besatzung = new Personal[6];
    private static boolean schichtLaeuft = false;
    
    // Wach-Variablen (jetzt public, damit der Einsatz die Zeit weiterlaufen lassen kann!)
    public static int currentHour = 6;
    public static int currentMinute = 30;
    
    private static int alarmStunde = 0;
    private static int alarmMinute = 0;
    public static JLabel lblClock;
    public static JTextArea txtAktivitaet;
    private static JButton btnSpulen;
    private static Timer clockTimer;

    static class PersonalItem {
        Personal p;
        String text;
        public PersonalItem(Personal p, String text) { this.p = p; this.text = text; }
        @Override public String toString() { return text; }
    }

    public static void starten() {
        f = new JFrame(schichtLaeuft ? "Personal-Einteilung aendern" : "Wachalltag - 06:30 Uhr Schichteinteilung");
        f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(new Color(35, 35, 35));

        // Test-Mitarbeiter falls Pool leer
        if (!LogistikSimulator.wachen.isEmpty()) {
            Wache w1 = LogistikSimulator.wachen.get(0);
            if (w1.personalPool.size() < 6) {
                w1.personalPool.clear();
                w1.personalPool.add(erstelleTestMitarbeiter("Mueller (EL)", "EL", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Schmidt (GF)", "GF", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Becker (MA)", "MA", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Hoffmann (TF)", "TF", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Schulz (TF)", "TF", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Bauer (TM)", "TM", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Richter (TM)", "TM", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Wolf (TM)", "TM", "Frei"));
            }
        }

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(schichtLaeuft ? " BESATZUNG VON 1-HLF-1 AENDERN" : " SCHICHTBEGINN 06:30 UHR - EINTEILUNG STAFFEL (1/5)");
        lblTitle.setForeground(new Color(241, 196, 15));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.add(lblTitle, BorderLayout.WEST);

        JButton btnClose = new JButton(schichtLaeuft ? "Zurueck zur Wache" : "Abbrechen");
        btnClose.setBackground(new Color(192, 57, 43));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> { 
            f.dispose(); 
            if(schichtLaeuft) wacheFrame.setVisible(true); 
            else Launcher.main(new String[]{}); 
        });
        titleBar.add(btnClose, BorderLayout.EAST);
        f.add(titleBar, BorderLayout.NORTH);

        auswahlListe = new ArrayList<>();
        auswahlListe.add(new PersonalItem(null, "--- Nicht besetzt ---"));
        
        if (!LogistikSimulator.wachen.isEmpty()) {
            for (Personal p : LogistikSimulator.wachen.get(0).personalPool) {
                String qualString = String.join(", ", p.qualifikationen);
                String label = p.name + " (" + qualString + ")";
                auswahlListe.add(new PersonalItem(p, label));
            }
        }
        PersonalItem[] personalArray = auswahlListe.toArray(new PersonalItem[0]);

        JPanel centerPnl = new JPanel(new BorderLayout());
        centerPnl.setBackground(new Color(35, 35, 35));
        centerPnl.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JPanel grid = new JPanel(new GridLayout(3, 2, 20, 15)); // 3 Zeilen, 2 Spalten fuer Staffel
        grid.setBackground(new Color(45, 45, 45));
        grid.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 2), " HLF 20 - Staffel-Besatzung ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.WHITE));

        // AUTO-FILL LOGIK (Sucht automatisch verfuegbares Personal)
        ArrayList<Personal> bereitsEingeteilt = new ArrayList<>();
        if (!schichtLaeuft && !LogistikSimulator.wachen.isEmpty()) {
            ArrayList<Personal> pool = LogistikSimulator.wachen.get(0).personalPool;
            besatzung[0] = findeFreiesPersonal("MA", pool, bereitsEingeteilt);
            besatzung[1] = findeFreiesPersonal("GF", pool, bereitsEingeteilt);
            besatzung[2] = findeFreiesPersonal("TF", pool, bereitsEingeteilt);
            besatzung[3] = findeFreiesPersonal("TM", pool, bereitsEingeteilt);
            besatzung[4] = findeFreiesPersonal("TF", pool, bereitsEingeteilt);
            besatzung[5] = findeFreiesPersonal("TM", pool, bereitsEingeteilt);
        }

        cbMA = createColoredComboBox(personalArray); setCbSelection(cbMA, besatzung[0]);
        cbGF = createColoredComboBox(personalArray); setCbSelection(cbGF, besatzung[1]);
        cbATF = createColoredComboBox(personalArray); setCbSelection(cbATF, besatzung[2]);
        cbATM = createColoredComboBox(personalArray); setCbSelection(cbATM, besatzung[3]);
        cbWTF = createColoredComboBox(personalArray); setCbSelection(cbWTF, besatzung[4]);
        cbWTM = createColoredComboBox(personalArray); setCbSelection(cbWTM, besatzung[5]);

        grid.add(createSeatPanel("Maschinist (MA)", cbMA));
        grid.add(createSeatPanel("Gruppenfuehrer (GF)", cbGF));
        grid.add(createSeatPanel("A-Truppfuehrer (ATF)", cbATF));
        grid.add(createSeatPanel("A-Truppmann (ATM)", cbATM));
        grid.add(createSeatPanel("W-Truppfuehrer (WTF)", cbWTF));
        grid.add(createSeatPanel("W-Truppmann (WTM)", cbWTM));

        centerPnl.add(grid, BorderLayout.CENTER);
        f.add(centerPnl, BorderLayout.CENTER);

        JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPnl.setBackground(new Color(35, 35, 35));
        bottomPnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnStart = new JButton(schichtLaeuft ? "Aenderungen speichern & Fortsetzen" : "07:00 Uhr - Dienstantritt");
        btnStart.setBackground(new Color(39, 174, 96));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStart.setPreferredSize(new Dimension(500, 60));
        
        btnStart.addActionListener(e -> pruefeEinteilung());
        bottomPnl.add(btnStart);
        f.add(bottomPnl, BorderLayout.SOUTH);

        f.setVisible(true);
    }

    private static Personal findeFreiesPersonal(String req, ArrayList<Personal> pool, ArrayList<Personal> ignorieren) {
        for (Personal p : pool) {
            if (p.status.equals("Frei") && !ignorieren.contains(p) && LogistikSimulator.personErfuellt(p, req)) {
                ignorieren.add(p);
                return p;
            }
        }
        return null;
    }

    private static JComboBox<PersonalItem> createColoredComboBox(PersonalItem[] items) {
        JComboBox<PersonalItem> cb = new JComboBox<>(items);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PersonalItem && ((PersonalItem) value).p != null) {
                    Personal p = ((PersonalItem) value).p;
                    if (p.status.equals("Krank") || p.krankBis != -1) {
                        label.setBackground(new Color(192, 57, 43)); label.setForeground(Color.WHITE);
                    } else if (p.status.equals("Urlaub") || p.urlaubStart != -1) {
                        label.setBackground(new Color(211, 84, 0)); label.setForeground(Color.WHITE);
                    } else if (!isSelected) {
                        label.setBackground(new Color(45, 45, 45)); label.setForeground(Color.WHITE);
                    }
                }
                return label;
            }
        });
        return cb;
    }

    private static void setCbSelection(JComboBox<PersonalItem> cb, Personal p) {
        if (p == null) return;
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (cb.getItemAt(i).p == p) {
                cb.setSelectedIndex(i);
                break;
            }
        }
    }

    private static Personal erstelleTestMitarbeiter(String name, String quali, String status) {
        Personal p = new Personal(name, "Anwaerter");
        p.qualifikationen.clear(); p.qualifikationen.add(quali); p.status = status;
        if(status.equals("Krank")) p.krankBis = 100;
        if(status.equals("Urlaub")) p.urlaubStart = 100;
        return p;
    }

    private static JPanel createSeatPanel(String title, JComboBox<PersonalItem> cb) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(45, 45, 45));
        p.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel l = new JLabel(title);
        l.setForeground(Color.WHITE); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.NORTH); p.add(cb, BorderLayout.CENTER);
        return p;
    }

    private static void pruefeEinteilung() {
        besatzung[0] = ((PersonalItem)cbMA.getSelectedItem()).p;
        besatzung[1] = ((PersonalItem)cbGF.getSelectedItem()).p;
        besatzung[2] = ((PersonalItem)cbATF.getSelectedItem()).p;
        besatzung[3] = ((PersonalItem)cbATM.getSelectedItem()).p;
        besatzung[4] = ((PersonalItem)cbWTF.getSelectedItem()).p;
        besatzung[5] = ((PersonalItem)cbWTM.getSelectedItem()).p;

        f.dispose();
        
        if (!schichtLaeuft) {
            schichtLaeuft = true;
            currentHour = 7;
            currentMinute = 0;
            starteWachSchicht();
        } else {
            wacheFrame.setVisible(true);
            txtAktivitaet.append("\n[" + String.format("%02d:%02d", currentHour, currentMinute) + "] HLF-Fahrzeugeinteilung wurde manuell geaendert.\n");
        }
    }
    
    private static void starteWachSchicht() {
        wacheFrame = new JFrame("Wachalltag");
        wacheFrame.setUndecorated(true);
        wacheFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        wacheFrame.setLayout(new BorderLayout());
        wacheFrame.getContentPane().setBackground(new Color(35, 35, 35));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(" WACHALLTAG: BEREITSCHAFT AUF DER WACHE");
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.add(lblTitle, BorderLayout.WEST);
        
        JButton btnClose = new JButton("Schicht Abbrechen");
        btnClose.setBackground(new Color(192, 57, 43));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> { wacheFrame.dispose(); Launcher.main(new String[]{}); });
        titleBar.add(btnClose, BorderLayout.EAST);
        wacheFrame.add(titleBar, BorderLayout.NORTH);

        JPanel centerPnl = new JPanel(new BorderLayout(20, 20));
        centerPnl.setBackground(new Color(35, 35, 35));
        centerPnl.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        lblClock = new JLabel(String.format("%02d:%02d", currentHour, currentMinute), SwingConstants.CENTER);
        lblClock.setFont(new Font("Consolas", Font.BOLD, 120));
        lblClock.setForeground(Color.WHITE);
        centerPnl.add(lblClock, BorderLayout.NORTH);

        txtAktivitaet = new JTextArea();
        txtAktivitaet.setEditable(false);
        txtAktivitaet.setBackground(new Color(25, 25, 25));
        txtAktivitaet.setForeground(new Color(200, 200, 200));
        txtAktivitaet.setFont(new Font("Consolas", Font.PLAIN, 18));
        txtAktivitaet.setMargin(new Insets(20, 20, 20, 20));
        txtAktivitaet.append("[07:00] Dienstuebernahme abgeschlossen. HLF einsatzbereit (Status 2).\n");
        centerPnl.add(new JScrollPane(txtAktivitaet), BorderLayout.CENTER);
        wacheFrame.add(centerPnl, BorderLayout.CENTER);

        JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPnl.setBackground(new Color(35, 35, 35));
        
        JButton btnCrewAendern = new JButton("Fahrzeugbesatzung aendern");
        btnCrewAendern.setBackground(new Color(41, 128, 185));
        btnCrewAendern.setForeground(Color.WHITE);
        btnCrewAendern.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnCrewAendern.setPreferredSize(new Dimension(350, 60));
        bottomPnl.add(btnCrewAendern);

        btnSpulen = new JButton("Zeit vorspulen >>");
        btnSpulen.setBackground(new Color(39, 174, 96));
        btnSpulen.setForeground(Color.WHITE);
        btnSpulen.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnSpulen.setPreferredSize(new Dimension(300, 60));
        bottomPnl.add(btnSpulen);
        
        wacheFrame.add(bottomPnl, BorderLayout.SOUTH);
        wacheFrame.setVisible(true);

        generiereNeuenAlarm();

        btnCrewAendern.addActionListener(e -> {
            if (clockTimer != null) clockTimer.stop();
            btnSpulen.setEnabled(true);
            wacheFrame.setVisible(false);
            starten(); 
        });

        btnSpulen.addActionListener(e -> {
            btnSpulen.setEnabled(false);
            btnCrewAendern.setEnabled(false); 
            
            clockTimer = new Timer(50, ev -> {
                currentMinute++;
                if (currentMinute >= 60) { currentMinute = 0; currentHour++; }
                
                String timeStr = String.format("%02d:%02d", currentHour, currentMinute);
                lblClock.setText(timeStr);
                
                if (currentHour >= 16 && currentMinute >= 30) {
                    clockTimer.stop();
                    JOptionPane.showMessageDialog(wacheFrame, "16:30 Uhr: Schichtende! Alle Mann nach Hause.", "Feierabend", JOptionPane.INFORMATION_MESSAGE);
                    wacheFrame.dispose();
                    schichtLaeuft = false;
                    Launcher.main(new String[]{});
                    return;
                }

                if (currentHour == alarmStunde && currentMinute == alarmMinute) {
                    clockTimer.stop();
                    lblClock.setForeground(new Color(192, 57, 43)); 
                    txtAktivitaet.append("\n!!! " + timeStr + " - ALARM ALARM ALARM !!!\n");
                    txtAktivitaet.append("Einsatz fuer 1-HLF-1! Bitte zum Fahrzeug begeben!\n");
                    
                    btnSpulen.setText("ALARM! Zum Einsatzort >>");
                    btnSpulen.setBackground(new Color(192, 57, 43));
                    btnSpulen.setEnabled(true);
                    
                    for(java.awt.event.ActionListener al : btnSpulen.getActionListeners()) {
                        btnSpulen.removeActionListener(al);
                    }
                    btnSpulen.addActionListener(alarmEvent -> {
                        wacheFrame.setVisible(false); 
                        GruppenfuehrerSimulator.starten();
                    });
                }
            });
            clockTimer.start();
        });
    }

    private static void generiereNeuenAlarm() {
        int offset = 60 + (int)(Math.random() * 180); 
        int totalMins = (currentHour * 60) + currentMinute + offset;
        alarmStunde = totalMins / 60;
        alarmMinute = totalMins % 60;
        
        if (alarmStunde >= 16 && alarmMinute >= 30) {
            alarmStunde = 99; 
        }
    }

    public static void einsatzBeendet() {
        wacheFrame.setVisible(true);
        txtAktivitaet.append("\n[" + String.format("%02d:%02d", currentHour, currentMinute) + "] HLF ist auf der Wache eingetroffen. Status 2 (Einsatzbereit auf Wache).\n");
        generiereNeuenAlarm();
        
        lblClock.setForeground(Color.WHITE);
        lblClock.setText(String.format("%02d:%02d", currentHour, currentMinute));
        btnSpulen.setText("Zeit vorspulen >>");
        btnSpulen.setBackground(new Color(39, 174, 96));
        btnSpulen.setEnabled(true);
        
        JPanel bottomPnl = (JPanel) wacheFrame.getContentPane().getComponent(2);
        bottomPnl.getComponent(0).setEnabled(true); 
        
        for(java.awt.event.ActionListener al : btnSpulen.getActionListeners()) {
            btnSpulen.removeActionListener(al);
        }
        btnSpulen.addActionListener(e -> {
            btnSpulen.setEnabled(false);
            bottomPnl.getComponent(0).setEnabled(false); 
            clockTimer.start();
        });
    }
}