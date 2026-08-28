package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GruppenfuehrerSimulator {

    private static JFrame f;
    private static JTextArea txtLog;
    private static JPanel actionPanel;
    
    private static int phase = 0; 
    private static boolean atPaAngelegt = false;
    private static boolean wasserversorgungSteht = false;
    private static boolean verteilerGesteckt = false;
    private static boolean rtwNachalarmiert = false;

    public static void starten() {
        f = new JFrame("Einsatzleiter (HLF)");
        f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(new Color(35, 35, 35));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(" EINSATZFUEHRUNG: 1-HLF-1 (Gruppenfuehrer)");
        lblTitle.setForeground(new Color(243, 156, 18));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.add(lblTitle, BorderLayout.WEST);

        JButton btnClose = new JButton("Zurueck zum Hauptmenue");
        btnClose.setBackground(new Color(192, 57, 43));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> {
            f.dispose();
            Launcher.main(new String[]{}); 
        });
        titleBar.add(btnClose, BorderLayout.EAST);
        f.add(titleBar, BorderLayout.NORTH);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(25, 25, 25));
        txtLog.setForeground(new Color(0, 255, 0)); 
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 20)); 
        txtLog.setMargin(new Insets(20, 20, 20, 20));
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 2), " Funk- & Einsatzprotokoll ", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));
        scrollLog.setBackground(new Color(35, 35, 35));
        
        JPanel fmsWrapper = new JPanel(new BorderLayout());
        fmsWrapper.setBackground(new Color(35, 35, 35));
        
        JPanel fmsPanel = new JPanel(new BorderLayout(5, 5));
        fmsPanel.setBackground(new Color(45, 45, 45));
        fmsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 2), " FMS ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));
        fmsPanel.setPreferredSize(new Dimension(300, 350)); 
        
        JPanel grid1to9 = new JPanel(new GridLayout(3, 3, 8, 8));
        grid1to9.setBackground(new Color(45, 45, 45));
        grid1to9.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Color[] fmsColors = {
            null, 
            new Color(0, 255, 0),       
            new Color(255, 255, 0),     
            new Color(255, 165, 0),     
            new Color(255, 153, 153),   
            new Color(255, 0, 0),       
            new Color(150, 150, 150),   
            new Color(200, 200, 255),   
            new Color(255, 200, 200),   
            new Color(255, 255, 200)    
        };

        for (int i = 1; i <= 9; i++) {
            JButton btnFMS = createFmsButton(String.valueOf(i), fmsColors[i]);
            final int status = i;
            btnFMS.addActionListener(e -> handleFmsClick(status));
            grid1to9.add(btnFMS);
        }

        JButton btnFms0 = createFmsButton("0", new Color(180, 0, 0)); 
        btnFms0.setPreferredSize(new Dimension(0, 60));
        btnFms0.addActionListener(e -> handleFmsClick(0));
        
        JPanel bottomFmsContainer = new JPanel(new BorderLayout());
        bottomFmsContainer.setBackground(new Color(45, 45, 45));
        bottomFmsContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomFmsContainer.add(btnFms0, BorderLayout.CENTER);

        fmsPanel.add(grid1to9, BorderLayout.CENTER);
        fmsPanel.add(bottomFmsContainer, BorderLayout.SOUTH);
        
        fmsWrapper.add(fmsPanel, BorderLayout.NORTH); 

        JPanel centerWrapper = new JPanel(new BorderLayout(30, 0)); 
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80)); 
        centerWrapper.setBackground(new Color(35, 35, 35));
        centerWrapper.add(scrollLog, BorderLayout.CENTER);
        centerWrapper.add(fmsWrapper, BorderLayout.EAST); 
        f.add(centerWrapper, BorderLayout.CENTER);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionPanel.setBackground(new Color(45, 45, 45));
        actionPanel.setPreferredSize(new Dimension(f.getWidth(), 180)); 
        f.add(actionPanel, BorderLayout.SOUTH);

        phase = 0;
        atPaAngelegt = false;
        wasserversorgungSteht = false;
        verteilerGesteckt = false;
        rtwNachalarmiert = false;

        log("LEITSTELLE: Alarm fuer 1-HLF-1. Stichwort: F1 - Unklare Rauchentwicklung.");
        log("SYSTEM: Besatzung sammelt sich am Fahrzeug. Bitte in Kuerze Status 3 druecken.");
        baueButtons();

        Timer t = new Timer(3000, e -> {
            phase = 1;
            baueButtons();
        });
        t.setRepeats(false);
        t.start();

        f.setVisible(true);
    }

    private static void log(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        txtLog.append("[" + time + "] " + msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private static JButton createFmsButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(new Color(40, 40, 40)); 
        if(text.equals("5") || text.equals("0")) b.setForeground(Color.WHITE); 
        b.setFont(new Font("Segoe UI", Font.BOLD, 38)); 
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createRaisedBevelBorder()); 
        return b;
    }

    private static JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(320, 60)); 
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static void handleFmsClick(int status) {
        if (status == 3 && phase == 1) {
            log("1-HLF-1: Status 3 (via FMS).");
            log("LEITSTELLE: Verstanden 1-HLF-1. Anfahrt.");
            phase = 2;
            baueButtons();
            
            Timer t = new Timer(5000, ev -> {
                phase = 3;
                baueButtons();
            });
            t.setRepeats(false);
            t.start();
        } 
        else if (status == 4 && phase == 3) {
            log("1-HLF-1: Status 4 (via FMS).");
            log("LEITSTELLE: Verstanden.");
            phase = 4;
            baueButtons();
        }
        else if (status == 5) {
            log("1-HLF-1: SPRECHWUNSCH (Status 5)!");
            log("LEITSTELLE: 1-HLF-1 kommen.");
        }
        else {
            log("1-HLF-1: Status " + status);
        }
    }

    private static void baueButtons() {
        actionPanel.removeAll();

        if (phase == 0) {
            JButton bWait = createBtn("Warte auf Mannschaft...", Color.GRAY);
            bWait.setEnabled(false);
            actionPanel.add(bWait);
        } 
        else if (phase == 1) {
            JButton btnS3 = createBtn("Status 3 (Ausruecken)", new Color(241, 196, 15)); 
            btnS3.setForeground(Color.BLACK);
            btnS3.addActionListener(e -> handleFmsClick(3));
            actionPanel.add(btnS3);
        }
        else if (phase == 2) {
            JButton bFahrt = createBtn("Fahrzeug auf Anfahrt...", Color.GRAY);
            bFahrt.setEnabled(false);
            actionPanel.add(bFahrt);
        }
        else if (phase == 3) {
            JButton btnS4 = createBtn("Status 4 (Eingetroffen)", new Color(211, 84, 0)); 
            btnS4.addActionListener(e -> handleFmsClick(4));
            actionPanel.add(btnS4);
        }
        else if (phase == 4) {
            JButton btnErkunden = createBtn("Lage erkunden", new Color(41, 128, 185));
            btnErkunden.addActionListener(e -> {
                log("GF ERKUNDUNG: Schwarzer Rauch aus dem Kuechenfenster! Vor der Tuer liegt eine bewusstlose Person mit Rauchgasvergiftung!");
                phase = 5;
                baueButtons();
            });
            actionPanel.add(btnErkunden);
        }
        else if (phase == 5) {
            if (!rtwNachalarmiert) {
                JButton btnNachalarm = createBtn("Leitstelle: RTW & NEF nachfordern", new Color(142, 68, 173));
                btnNachalarm.addActionListener(e -> {
                    rtwNachalarmiert = true;
                    log("1-HLF-1: Wir haben hier eine bewusstlose Person! Benoetigen dringend RTW und NEF!");
                    log("LEITSTELLE: Verstanden 1-HLF-1, Rettungsdienst ist alarmiert und auf Anfahrt.");
                    baueButtons();
                });
                actionPanel.add(btnNachalarm);
            }

            if (!atPaAngelegt) {
                JButton btnAT_PA = createBtn("A-Trupp: PA anlegen & Retten", new Color(192, 57, 43));
                btnAT_PA.addActionListener(e -> {
                    atPaAngelegt = true;
                    log("BEFEHL: Angriffstrupp unter Atemschutz ausruesten und Crash-Rettung der Person!");
                    log("A-TRUPP: Verstanden, legen PA an und ziehen Person aus dem Gefahrenbereich.");
                    baueButtons();
                });
                actionPanel.add(btnAT_PA);
            } else if (!verteilerGesteckt) {
                JButton btnAT_C = createBtn("A-Trupp: Verteiler & C-Rohr", new Color(192, 57, 43));
                btnAT_C.addActionListener(e -> {
                    if(!wasserversorgungSteht) {
                        JOptionPane.showMessageDialog(f, "Achtung: Der Wassertrupp hat noch keine Wasserversorgung zum Fahrzeug aufgebaut! Das C-Rohr ist trocken!", "Taktischer Fehler", JOptionPane.WARNING_MESSAGE);
                    }
                    verteilerGesteckt = true;
                    log("BEFEHL: Angriffstrupp, Person an RTW uebergeben. Dann mit C-Rohr zum Verteiler vor!");
                    log("A-TRUPP: Verstanden. Gehen mit C-Rohr vor.");
                    baueButtons();
                });
                actionPanel.add(btnAT_C);
            } else {
                JButton btnAT_Angriff = createBtn("A-Trupp: Innenangriff!", new Color(192, 57, 43));
                btnAT_Angriff.addActionListener(e -> {
                    if(!wasserversorgungSteht) {
                        log("A-TRUPP: FEHLER! Wir haben kein Wasser am Strahlrohr! Brechen Angriff ab!");
                        return;
                    }
                    log("BEFEHL: Angriffstrupp zur Brandbekaempfung in die Kueche vor!");
                    log("A-TRUPP: Wasser marsch! Gehen in Brandraum vor.");
                    btnAT_Angriff.setEnabled(false);
                    
                    Timer t = new Timer(6000, ev -> {
                        log("A-TRUPP: Feuer aus! Beginnen mit Nachloescharbeiten.");
                        if(rtwNachalarmiert) {
                            log("LEITSTELLE: RTW vermeldet Status 8, Patient auf dem Weg ins Krankenhaus.");
                        } else {
                            log("SYSTEM: ACHTUNG! Du hast den RTW vergessen! Patient geht es sehr schlecht.");
                        }
                        JOptionPane.showMessageDialog(f, "Einsatz beendet!\nKlicke oben rechts auf 'Zurueck zum Hauptmenue'.");
                    });
                    t.setRepeats(false);
                    t.start();
                });
                actionPanel.add(btnAT_Angriff);
            }

            if (!wasserversorgungSteht) {
                JButton btnWT_Wasser = createBtn("W-Trupp: Wasserversorgung", new Color(41, 128, 185));
                btnWT_Wasser.addActionListener(e -> {
                    wasserversorgungSteht = true;
                    log("BEFEHL: Wassertrupp, Wasserversorgung vom Unterflurhydranten zum HLF!");
                    log("W-TRUPP: Verstanden. Setzen Standrohr und legen B-Leitung.");
                    baueButtons();
                });
                actionPanel.add(btnWT_Wasser);
            } else {
                JButton btnWT_Sichern = createBtn("W-Trupp: Sicherungstrupp", new Color(41, 128, 185));
                btnWT_Sichern.addActionListener(e -> {
                    log("BEFEHL: Wassertrupp als Sicherungstrupp mit PA ausruesten.");
                    log("W-TRUPP: Verstanden, Si-Trupp steht bereit.");
                    btnWT_Sichern.setEnabled(false);
                });
                actionPanel.add(btnWT_Sichern);
            }
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }
}