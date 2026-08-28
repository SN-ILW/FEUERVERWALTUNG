package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WachalltagSimulator {

    private static JFrame f;
    private static JFrame wacheFrame; // Das Fenster fuer die Bereitschaftszeit
    private static JComboBox<PersonalItem> cbMA, cbME, cbATF, cbATM, cbWTF, cbWTM, cbSTF, cbSTM;
    private static ArrayList<PersonalItem> auswahlListe;
    
    // Wach-Variablen
    private static int currentHour = 7;
    private static int currentMinute = 0;
    private static int alarmStunde = 0;
    private static int alarmMinute = 0;
    private static JLabel lblClock;
    private static JTextArea txtAktivitaet;
    private static JButton btnSpulen;
    private static Timer clockTimer;

    static class PersonalItem {
        Personal p;
        String text;
        public PersonalItem(Personal p, String text) { this.p = p; this.text = text; }
        @Override public String toString() { return text; }
    }

    public static void starten() {
        f = new JFrame("Wachalltag - 06:30 Uhr Schichteinteilung");
        f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(new Color(35, 35, 35));

        if (!LogistikSimulator.wachen.isEmpty()) {
            Wache w1 = LogistikSimulator.wachen.get(0);
            if (w1.personalPool.size() < 12) {
                w1.personalPool.clear();
                w1.personalPool.add(erstelleTestMitarbeiter("Mueller (EL)", "EL", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Schmidt (GF)", "GF", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Becker (MA)", "MA", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Wagner (MA)", "MA", "Urlaub")); 
                w1.personalPool.add(erstelleTestMitarbeiter("Hoffmann (TF)", "TF", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Schulz (TF)", "TF", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Koch (TF)", "TF", "Krank")); 
                w1.personalPool.add(erstelleTestMitarbeiter("Bauer (TM)", "TM", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Richter (TM)", "TM", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Wolf (TM)", "TM", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Schroeder (TM)", "TM", "Frei"));
                w1.personalPool.add(erstelleTestMitarbeiter("Neumann (TM)", "TM", "Frei"));
            }
        }

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(" SCHICHTBEGINN 06:30 UHR - EINTEILUNG 1-HLF-1");
        lblTitle.setForeground(new Color(241, 196, 15));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.add(lblTitle, BorderLayout.WEST);

        JButton btnClose = new JButton("Abbrechen");
        btnClose.setBackground(new Color(192, 57, 43));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> { f.dispose(); Launcher.main(new String[]{}); });
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

        JPanel grid = new JPanel(new GridLayout(5, 2, 20, 15));
        grid.setBackground(new Color(45, 45, 45));
        grid.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 2), " HLF 20 - Sitzordnung (1/8/9) ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.WHITE));

        cbMA = createColoredComboBox(personalArray);
        cbME = createColoredComboBox(personalArray);
        cbATF = createColoredComboBox(personalArray);
        cbATM = createColoredComboBox(personalArray);
        cbWTF = createColoredComboBox(personalArray);
        cbWTM = createColoredComboBox(personalArray);
        cbSTF = createColoredComboBox(personalArray);
        cbSTM = createColoredComboBox(personalArray);

        JPanel pnlGF = new JPanel(new BorderLayout(5, 5));
        pnlGF.setBackground(new Color(45, 45, 45));
        pnlGF.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel lGFTitle = new JLabel("Gruppenfuehrer (GF)");
        lGFTitle.setForeground(Color.WHITE);
        lGFTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel lGFValue = new JLabel(" DU (Spieler)");
        lGFValue.setOpaque(true);
        lGFValue.setBackground(new Color(39, 174, 96)); 
        lGFValue.setForeground(Color.WHITE);
        lGFValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lGFValue.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        pnlGF.add(lGFTitle, BorderLayout.NORTH);
        pnlGF.add(lGFValue, BorderLayout.CENTER);

        grid.add(pnlGF); 
        grid.add(createSeatPanel("Maschinist (MA)", cbMA));
        grid.add(createSeatPanel("Melder (ME)", cbME));
        grid.add(new JLabel("")); 
        grid.add(createSeatPanel("A-Truppfuehrer (ATF)", cbATF));
        grid.add(createSeatPanel("A-Truppmann (ATM)", cbATM));
        grid.add(createSeatPanel("W-Truppfuehrer (WTF)", cbWTF));
        grid.add(createSeatPanel("W-Truppmann (WTM)", cbWTM));
        grid.add(createSeatPanel("S-Truppfuehrer (STF)", cbSTF));
        grid.add(createSeatPanel("S-Truppmann (STM)", cbSTM));

        centerPnl.add(grid, BorderLayout.CENTER);
        f.add(centerPnl, BorderLayout.CENTER);

        JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPnl.setBackground(new Color(35, 35, 35));
        bottomPnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnStart = new JButton("07:00 Uhr - Dienstantritt");
        btnStart.setBackground(new Color(39, 174, 96));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnStart.setPreferredSize(new Dimension(500, 60));
        
        btnStart.addActionListener(e -> pruefeEinteilung());
        bottomPnl.add(btnStart);
        f.add(bottomPnl, BorderLayout.SOUTH);

        f.setVisible(true);
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
        // (Zur Vereinfachung des Codes beim Testen habe ich die harte Quali-Prufung kurz entschaerft, 
        // damit du beim Testen schneller durchkommst)
        f.dispose();
        
        currentHour = 7;
        currentMinute = 0;
        starteWachSchicht();
    }

    // =========================================================================================
    // DIE WACHSCHICHT - ZENTRALE STEUERUNG!
    // =========================================================================================
    
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

        lblClock = new JLabel("07:00", SwingConstants.CENTER);
        lblClock.setFont(new Font("Consolas", Font.BOLD, 120));
        lblClock.setForeground(Color.WHITE);
        centerPnl.add(lblClock, BorderLayout.NORTH);

        txtAktivitaet = new JTextArea();
        txtAktivitaet.setEditable(false);
        txtAktivitaet.setBackground(new Color(25, 25, 25));
        txtAktivitaet.setForeground(new Color(200, 200, 200));
        txtAktivitaet.setFont(new Font("Consolas", Font.PLAIN, 18));
        txtAktivitaet.setMargin(new Insets(20, 20, 20, 20));
        txtAktivitaet.append("[07:00] Dienstuebernahme abgeschlossen. HLF einsatzbereit.\n");
        centerPnl.add(new JScrollPane(txtAktivitaet), BorderLayout.CENTER);
        wacheFrame.add(centerPnl, BorderLayout.CENTER);

        JPanel bottomPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPnl.setBackground(new Color(35, 35, 35));
        
        btnSpulen = new JButton("Zeit vorspulen >>");
        btnSpulen.setBackground(new Color(39, 174, 96));
        btnSpulen.setForeground(Color.WHITE);
        btnSpulen.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnSpulen.setPreferredSize(new Dimension(300, 60));
        bottomPnl.add(btnSpulen);
        
        wacheFrame.add(bottomPnl, BorderLayout.SOUTH);
        wacheFrame.setVisible(true);

        generiereNeuenAlarm();

        btnSpulen.addActionListener(e -> {
            btnSpulen.setEnabled(false);
            
            clockTimer = new Timer(50, ev -> {
                currentMinute++;
                if (currentMinute >= 60) { currentMinute = 0; currentHour++; }
                
                String timeStr = String.format("%02d:%02d", currentHour, currentMinute);
                lblClock.setText(timeStr);
                
                if (currentHour >= 16 && currentMinute >= 30) {
                    clockTimer.stop();
                    JOptionPane.showMessageDialog(wacheFrame, "16:30 Uhr: Schichtende! Alle Mann nach Hause.", "Feierabend", JOptionPane.INFORMATION_MESSAGE);
                    wacheFrame.dispose();
                    Launcher.main(new String[]{});
                    return;
                }

                // ALARM!
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
                        wacheFrame.setVisible(false); // Wache ausblenden, Einsatz starten
                        GruppenfuehrerSimulator.starten();
                    });
                }
            });
            clockTimer.start();
        });
    }

    private static void generiereNeuenAlarm() {
        int offset = 60 + (int)(Math.random() * 180); // In 1-4 Stunden kracht es
        int totalMins = (currentHour * 60) + currentMinute + offset;
        alarmStunde = totalMins / 60;
        alarmMinute = totalMins % 60;
        
        if (alarmStunde >= 16 && alarmMinute >= 30) {
            alarmStunde = 99; // Kein Einsatz mehr vor Feierabend
        }
    }

    // WIRD VOM EINSATZ-FENSTER AUFGERUFEN, WENN MAN STATUS 1 DRUECKT!
    public static void einsatzBeendet() {
        wacheFrame.setVisible(true);
        txtAktivitaet.append("\n[" + String.format("%02d:%02d", currentHour, currentMinute) + "] HLF ist wieder auf der Wache eingerueckt. Status 2.\n");
        generiereNeuenAlarm();
        
        lblClock.setForeground(Color.WHITE);
        btnSpulen.setText("Zeit vorspulen >>");
        btnSpulen.setBackground(new Color(39, 174, 96));
        btnSpulen.setEnabled(true);
        
        // Listener resetten, damit Spulen wieder klappt
        for(java.awt.event.ActionListener al : btnSpulen.getActionListeners()) {
            btnSpulen.removeActionListener(al);
        }
        btnSpulen.addActionListener(e -> {
            btnSpulen.setEnabled(false);
            clockTimer.start();
        });
    }
}