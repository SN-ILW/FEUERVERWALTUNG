package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GruppenfuehrerSimulator {

    private static JFrame f;
    private static JTextArea txtLog;
    private static JPanel actionPanel;
    private static JPanel crewPanel; 
    
    // FMS und SoSi Buttons
    private static JButton btnBlaulicht;
    private static JButton btnHorn;
    
    private static int phase = 0; 
    private static int einsatzTyp = 0; 
    private static boolean sprechwunschAktiv = false;
    
    // Taktische Erkundungs-Variablen
    private static boolean lageErkundet = false;
    private static boolean gfAbgesessen = false; 
    private static boolean alleAbgesessen = false; 
    
    private static boolean atPaAngelegt = false;
    private static boolean wasserversorgungSteht = false;
    private static boolean verteilerGesteckt = false;
    
    // Nachalarmierungs-Variablen
    private static boolean rtwNachalarmiert = false;
    private static boolean hlfNachalarmiert = false;
    private static boolean dlkNachalarmiert = false;
    private static boolean zweitesHlfDa = false;
    private static boolean dlkDa = false;
    private static boolean rtwDa = false;
    
    // Loeschangriff Fortschritt
    private static int loeschFortschritt = 0;
    private static Timer angriffTimer;
    
    private static boolean blaulichtAktiv = false;
    private static boolean hornAktiv = false;
    
    private static int anfahrtsDauerMs = 0; 
    
    private static boolean[] sitzPlatz = new boolean[6];
    private static int eingestiegenePersonen = 0;
    
    private static JLabel lblUhrzeit;
    private static Timer einsatzUhrTimer;
    
    // NEU: Globale Variablen fuer die perfekte Ladebalken-Animation
    private static JPanel pnlRoad;
    private static float fahrtProgress = 0.0f; // 0.0 = Start, 1.0 = Ziel

    public static void starten() {
        f = new JFrame("Einsatzleiter (HLF)");
        f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setLayout(new BorderLayout());
        f.getContentPane().setBackground(new Color(35, 35, 35));

        einsatzTyp = (Math.random() > 0.5) ? 0 : 1; 
        
        // Laengere realistische Anfahrt! (20 bis 35 Sekunden)
        anfahrtsDauerMs = 20000 + (int)(Math.random() * 15000);
        
        blaulichtAktiv = false; hornAktiv = false;
        lageErkundet = false; gfAbgesessen = false; alleAbgesessen = false;
        
        rtwNachalarmiert = false; hlfNachalarmiert = false; dlkNachalarmiert = false;
        zweitesHlfDa = false; dlkDa = false; rtwDa = false;
        loeschFortschritt = 0;
        
        eingestiegenePersonen = 0;
        for(int i = 0; i < 6; i++) sitzPlatz[i] = false; 

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel(" EINSATZFUEHRUNG: 1-HLF-1 (Gruppenfuehrer)");
        lblTitle.setForeground(new Color(243, 156, 18));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.add(lblTitle, BorderLayout.WEST);
        
        lblUhrzeit = new JLabel(String.format("Einsatzzeit: %02d:%02d Uhr ", WachalltagSimulator.currentHour, WachalltagSimulator.currentMinute));
        lblUhrzeit.setForeground(Color.WHITE);
        lblUhrzeit.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleBar.add(lblUhrzeit, BorderLayout.EAST);
        
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
        
        JPanel eastWrapper = new JPanel(new BorderLayout(0, 15));
        eastWrapper.setBackground(new Color(35, 35, 35));
        eastWrapper.setPreferredSize(new Dimension(350, 0));
        
        JPanel fmsPanel = new JPanel(new BorderLayout(5, 5));
        fmsPanel.setBackground(new Color(45, 45, 45));
        fmsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 2), " FMS & Sondersignal ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE));
        
        JPanel grid1to9 = new JPanel(new GridLayout(3, 3, 8, 8));
        grid1to9.setBackground(new Color(45, 45, 45));
        grid1to9.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Color[] fmsColors = { null, new Color(0, 255, 0), new Color(255, 255, 0), new Color(255, 165, 0), new Color(255, 153, 153), new Color(255, 0, 0), new Color(150, 150, 150), new Color(200, 200, 255), new Color(255, 200, 200), new Color(255, 255, 200) };

        for (int i = 1; i <= 9; i++) {
            JButton btnFMS = createFmsButton(String.valueOf(i), fmsColors[i]);
            final int status = i;
            btnFMS.addActionListener(e -> handleFmsClick(status));
            grid1to9.add(btnFMS);
        }

        JButton btnFms0 = createFmsButton("0", new Color(180, 0, 0)); 
        btnFms0.setPreferredSize(new Dimension(0, 60));
        btnFms0.addActionListener(e -> handleFmsClick(0));
        
        JPanel sosiPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        sosiPanel.setBackground(new Color(45, 45, 45));
        
        btnBlaulicht = new JButton("Blaulicht");
        btnBlaulicht.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnBlaulicht.setFocusPainted(false);
        btnBlaulicht.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBlaulicht.setPreferredSize(new Dimension(0, 50));
        btnBlaulicht.addActionListener(e -> toggleBlaulicht());

        btnHorn = new JButton("Horn");
        btnHorn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnHorn.setFocusPainted(false);
        btnHorn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHorn.addActionListener(e -> toggleHorn());

        updateSoSiButtons(); 
        sosiPanel.add(btnBlaulicht); sosiPanel.add(btnHorn);
        
        JPanel bottomFmsContainer = new JPanel(new BorderLayout(0, 10));
        bottomFmsContainer.setBackground(new Color(45, 45, 45));
        bottomFmsContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomFmsContainer.add(btnFms0, BorderLayout.NORTH);
        bottomFmsContainer.add(sosiPanel, BorderLayout.SOUTH);

        fmsPanel.add(grid1to9, BorderLayout.CENTER);
        fmsPanel.add(bottomFmsContainer, BorderLayout.SOUTH);
        
        crewPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        crewPanel.setBackground(Color.WHITE);
        crewPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.RED, 15),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        updateCrewUI();

        eastWrapper.add(fmsPanel, BorderLayout.NORTH);
        eastWrapper.add(crewPanel, BorderLayout.CENTER);

        JPanel centerWrapper = new JPanel(new BorderLayout(30, 0)); 
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80)); 
        centerWrapper.setBackground(new Color(35, 35, 35));
        centerWrapper.add(scrollLog, BorderLayout.CENTER);
        centerWrapper.add(eastWrapper, BorderLayout.EAST); 
        f.add(centerWrapper, BorderLayout.CENTER);

        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        actionPanel.setBackground(new Color(45, 45, 45));
        actionPanel.setPreferredSize(new Dimension(f.getWidth(), 350)); 
        f.add(actionPanel, BorderLayout.SOUTH);

        phase = 0;
        sprechwunschAktiv = false;

        String stichwort = (einsatzTyp == 0) ? "F1 - Unklare Rauchentwicklung" : "BMA - Ausloesung Brandmeldeanlage";
        log("LEITSTELLE: Alarm fuer 1-HLF-1. Stichwort: " + stichwort);
        
        einsatzUhrTimer = new Timer(15000, e -> {
            WachalltagSimulator.currentMinute++;
            if(WachalltagSimulator.currentMinute >= 60) {
                WachalltagSimulator.currentMinute = 0; WachalltagSimulator.currentHour++;
            }
            lblUhrzeit.setText(String.format("Einsatzzeit: %02d:%02d Uhr ", WachalltagSimulator.currentHour, WachalltagSimulator.currentMinute));
        });
        einsatzUhrTimer.start();
        
        phase = 1;
        baueButtons();

        Timer einsteigeTimer = new Timer(600, e -> {
            for(int i = 0; i < 6; i++) {
                if(!sitzPlatz[i]) {
                    sitzPlatz[i] = true;
                    eingestiegenePersonen++;
                    updateCrewUI();
                    if(alleEingestiegen()) {
                        log("SYSTEM: Staffel ist vollzaehlig aufgesessen! Bitte Status 3 druecken.");
                        baueButtons();
                        ((Timer)e.getSource()).stop();
                    }
                    break;
                }
            }
        });
        einsteigeTimer.start();

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
        b.setFocusPainted(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createRaisedBevelBorder()); 
        return b;
    }

    private static JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        b.setFocusPainted(false); b.setPreferredSize(new Dimension(340, 60)); 
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static JLabel createSeat(String text, boolean besetzt, Color hell, Color dunkel) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(besetzt ? hell : dunkel);
        l.setForeground(besetzt ? Color.BLACK : Color.GRAY);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        return l;
    }

    private static void updateCrewUI() {
        crewPanel.removeAll();
        crewPanel.add(createSeat("MA", sitzPlatz[0], new Color(255, 204, 0), new Color(100, 100, 0)));
        crewPanel.add(createSeat("GF", sitzPlatz[1], new Color(0, 255, 0), new Color(0, 100, 0)));
        crewPanel.add(createSeat("AF", sitzPlatz[2], new Color(255, 0, 0), new Color(100, 0, 0)));
        crewPanel.add(createSeat("AM", sitzPlatz[3], new Color(255, 0, 0), new Color(100, 0, 0)));
        crewPanel.add(createSeat("WF", sitzPlatz[4], new Color(0, 0, 255), new Color(0, 0, 100)));
        crewPanel.add(createSeat("WM", sitzPlatz[5], new Color(0, 0, 255), new Color(0, 0, 100)));
        crewPanel.revalidate(); crewPanel.repaint();
    }
    
    private static boolean alleEingestiegen() {
        for(boolean b : sitzPlatz) if(!b) return false;
        return true;
    }

    private static void toggleBlaulicht() { blaulichtAktiv = !blaulichtAktiv; updateSoSiButtons(); }
    private static void toggleHorn() { hornAktiv = !hornAktiv; updateSoSiButtons(); }

    private static void updateSoSiButtons() {
        if (blaulichtAktiv) { btnBlaulicht.setBackground(Color.BLUE); btnBlaulicht.setForeground(Color.WHITE); } 
        else { btnBlaulicht.setBackground(Color.WHITE); btnBlaulicht.setForeground(Color.BLACK); }

        if (hornAktiv) { btnHorn.setBackground(Color.GREEN); btnHorn.setForeground(Color.BLACK); } 
        else { btnHorn.setBackground(Color.WHITE); btnHorn.setForeground(Color.BLACK); }
    }
    
    private static void nachalarmieren(String fahrzeug) {
        log("1-HLF-1: Leitstelle von 1-HLF-1, wir benoetigen ein(e) " + fahrzeug + " zur Einsatzstelle!");
        
        boolean eigeneWache = Math.random() > 0.4; 
        int delay = eigeneWache ? anfahrtsDauerMs : (int)(anfahrtsDauerMs * (1.5 + Math.random())); 
        
        String herkunft = eigeneWache ? "unserer Wache" : "einer Nachbarwache";
        log("LEITSTELLE: Verstanden. " + fahrzeug + " von " + herkunft + " wurde alarmiert.");
        
        Timer t = new Timer(delay, ev -> {
            log("SYSTEM: Ein(e) " + fahrzeug + " (Status 4) ist an der Einsatzstelle eingetroffen!");
            if (fahrzeug.equals("Weiteres HLF")) zweitesHlfDa = true;
            if (fahrzeug.equals("Drehleiter (DLK)")) dlkDa = true;
            if (fahrzeug.equals("RTW & NEF")) rtwDa = true;
        });
        t.setRepeats(false); t.start();
        
        if (fahrzeug.equals("Weiteres HLF")) hlfNachalarmiert = true;
        if (fahrzeug.equals("Drehleiter (DLK)")) dlkNachalarmiert = true;
        if (fahrzeug.equals("RTW & NEF")) rtwNachalarmiert = true;
        
        sprechwunschAktiv = false;
        baueButtons();
    }

    // --- NEU: ZUVERLAESSIGE LADEBALKEN ANIMATION (MIT SPIEGELUNG) ---
    private static void startFahrtAnimation(int dauer, Runnable onComplete) {
        long start = System.currentTimeMillis();
        fahrtProgress = 0.0f;
        
        Timer anim = new Timer(30, e -> {
            long now = System.currentTimeMillis();
            fahrtProgress = Math.min(1.0f, (float)(now - start) / dauer);
            
            // Repaint erzwingen, damit das Fahrzeug vorwaerts wandert
            if (pnlRoad != null) pnlRoad.repaint();
            
            if (fahrtProgress >= 1.0f) {
                ((Timer)e.getSource()).stop();
                onComplete.run();
            }
        });
        anim.start();
    }

    private static void handleFmsClick(int status) {
        if (status == 3 && phase == 1) {
            if (!alleEingestiegen()) { log("SYSTEM: Das Fahrzeug ist noch nicht voll besetzt! Bitte warten..."); return; }
            log("1-HLF-1: Status 3.");
            log("LEITSTELLE: Verstanden 1-HLF-1. Anfahrt.");
            phase = 2; baueButtons();
            
            startFahrtAnimation(anfahrtsDauerMs, () -> {
                phase = 3; log("SYSTEM: Einsatzort in Sichtweite. Bitte Status 4 druecken."); baueButtons();
            });
        } 
        else if (status == 4 && phase == 3) {
            log("1-HLF-1: Status 4. Eingetroffen.");
            log("LEITSTELLE: Verstanden.");
            phase = 4; baueButtons();
        }
        else if (status == 5 && phase >= 4 && phase < 8) {
            log("1-HLF-1: SPRECHWUNSCH (Status 5).");
            log("LEITSTELLE: Hier Leitstelle, 1-HLF-1 kommen.");
            sprechwunschAktiv = true; baueButtons();
        }
        else if (status == 1 && phase == 6) {
            if (!alleEingestiegen()) { log("SYSTEM: Du kannst nicht ohne deine Mannschaft losfahren! Lass sie erst aufsitzen."); return; }
            log("1-HLF-1: Status 1. Sind wieder einsatzbereit auf Funk und treten die Rueckfahrt an.");
            log("LEITSTELLE: Verstanden, gute Rueckfahrt.");
            phase = 7; baueButtons();
            
            int doppelteDauer = anfahrtsDauerMs * 2;
            startFahrtAnimation(doppelteDauer, () -> {
                phase = 8; log("SYSTEM: Wache erreicht! Bitte druecke Status 2 (Einsatzbereit auf Wache)."); baueButtons();
            });
        }
        else if (status == 2 && phase == 8) {
            log("1-HLF-1: Status 2.");
            log("LEITSTELLE: Verstanden.");
            if (einsatzUhrTimer != null) einsatzUhrTimer.stop();
            f.dispose(); 
            WachalltagSimulator.einsatzBeendet(); 
        }
        else {
            log("1-HLF-1: FMS Status " + status + " gedrueckt.");
        }
    }

    private static void baueButtons() {
        actionPanel.removeAll();

        if (phase == 0 || phase == 1 || phase == 3) {
            if (phase == 1) {
                JLabel lblHint = new JLabel(!alleEingestiegen() ? "Mannschaft sitzt auf..." : "Fahrzeug voll besetzt! Status 3 druecken ->");
                lblHint.setForeground(Color.WHITE); lblHint.setFont(new Font("Segoe UI", Font.BOLD, 22)); actionPanel.add(lblHint);
            } else {
                JLabel lblHint = new JLabel("Befinden uns auf Anfahrt! Bitte FMS und SoSi nutzen.");
                lblHint.setForeground(Color.WHITE); lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 22)); actionPanel.add(lblHint);
            }
        }
        // --- DAS IST DAS NEUE ZEICHEN-PANEL FUER DAS FAHRZEUG ---
        else if (phase == 2 || phase == 7) {
            pnlRoad = new JPanel() {
                ImageIcon iconHinfahrt;
                ImageIcon iconRueckfahrt;
                {
                    // WICHTIG: TRAGE HIER DEINE ECHTEN DATEINAMEN EIN!
                    String nameHinfahrt = "A.png"; 
                    String nameRueckfahrt = "R.png"; 
                    
                    // Sucht im src/neuesspiel/ Ordner
                    java.net.URL urlHin = GruppenfuehrerSimulator.class.getResource(nameHinfahrt);
                    if (urlHin != null) iconHinfahrt = new ImageIcon(urlHin);
                    else iconHinfahrt = new ImageIcon(nameHinfahrt); // Sucht im Hauptordner
                    
                    java.net.URL urlRueck = GruppenfuehrerSimulator.class.getResource(nameRueckfahrt);
                    if (urlRueck != null) iconRueckfahrt = new ImageIcon(urlRueck);
                    else iconRueckfahrt = new ImageIcon(nameRueckfahrt);
                    
                    // DEBUG: Zeigt in der NetBeans-Konsole an, wenn das Bild fehlt!
                    if(iconHinfahrt.getIconWidth() <= 0) System.out.println("FEHLER: Bild '" + nameHinfahrt + "' nicht gefunden!");
                    if(iconRueckfahrt.getIconWidth() <= 0) System.out.println("FEHLER: Bild '" + nameRueckfahrt + "' nicht gefunden!");
                }
                
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    int w = getWidth();
                    int imgH = 90;
                    int imgW = 250; 
                    
                    ImageIcon currentIcon = (phase == 7) ? iconRueckfahrt : iconHinfahrt;
                    
                    if (currentIcon.getIconWidth() > 0) {
                        imgW = (int)((double)currentIcon.getIconWidth() / currentIcon.getIconHeight() * imgH);
                    }
                    
                    int max_x = w - imgW;
                    if (max_x < 0) max_x = 0;
                    
                    int x = phase == 2 ? (int)(max_x * fahrtProgress) : (int)(max_x * (1.0f - fahrtProgress));
                    
                    if (currentIcon.getIconWidth() > 0) {
                        g.drawImage(currentIcon.getImage(), x, 10, imgW, imgH, this);
                    } else {
                        // Der rote Kasten (Fallback)
                        g.setColor(new Color(192, 57, 43));
                        g.fillRect(x, 10, imgW, imgH);
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Segoe UI", Font.BOLD, 24));
                        g.drawString("BILD FEHLT", x + 10, 60);
                    }
                }
            };
            pnlRoad.setPreferredSize(new Dimension(800, 120));
            pnlRoad.setBackground(new Color(60, 60, 60));
            pnlRoad.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
            
            JLabel lblRoadHint = new JLabel(phase == 2 ? "Fahrt zum Einsatzort (Status 3)..." : "Rueckfahrt zur Wache (Status 1)...");
            lblRoadHint.setForeground(Color.WHITE);
            lblRoadHint.setFont(new Font("Segoe UI", Font.BOLD, 22));
            
            JPanel container = new JPanel(new BorderLayout(0, 10));
            container.setOpaque(false);
            container.add(lblRoadHint, BorderLayout.NORTH);
            container.add(pnlRoad, BorderLayout.CENTER);
            
            actionPanel.add(container);
        }
        else if (phase == 4) {
            if (!gfAbgesessen && !alleAbgesessen) {
                JButton btnGfErkunden = createBtn("Erkundung (nur GF & AF absitzen)", new Color(241, 196, 15));
                btnGfErkunden.setForeground(Color.BLACK);
                btnGfErkunden.addActionListener(e -> {
                    sitzPlatz[1] = false; sitzPlatz[2] = false; gfAbgesessen = true;
                    eingestiegenePersonen -= 2; updateCrewUI();
                    log("GF: Angriffstruppfuehrer und ich gehen zur Erkundung vor. Der Rest wartet auf Befehle!");
                    baueButtons();
                });
                actionPanel.add(btnGfErkunden);
                
                JButton btnAlleAbsitzen = createBtn("Komplette Mannschaft absitzen!", new Color(230, 126, 34));
                btnAlleAbsitzen.addActionListener(e -> {
                    for(int i = 1; i < 6; i++) { if(sitzPlatz[i]) { sitzPlatz[i] = false; eingestiegenePersonen--; } }
                    alleAbgesessen = true; gfAbgesessen = true; updateCrewUI();
                    log("GF: Absitzen! Sammeln am Fahrzeugheck!");
                    baueButtons();
                });
                actionPanel.add(btnAlleAbsitzen);
            } 
            else if (!lageErkundet) {
                JButton btnErkunden = createBtn("Lage melden (Erkundung beendet)", new Color(41, 128, 185));
                btnErkunden.addActionListener(e -> {
                    lageErkundet = true;
                    if (einsatzTyp == 0) log("GF ERKUNDUNG: Schwarzer Rauch aus dem Kuechenfenster! Brand bestaetigt!");
                    else log("GF ERKUNDUNG: BMA hat im Foyer ausgeloest. Handfeuermelder gedrueckt. Kein Rauch/Feuer.");
                    phase = 5; baueButtons();
                });
                actionPanel.add(btnErkunden);
            }
        }
        else if (phase == 5) {
            if (sprechwunschAktiv) {
                if (!hlfNachalarmiert) {
                    JButton btnNachHlf = createBtn("Weiteres HLF anfordern", new Color(142, 68, 173));
                    btnNachHlf.addActionListener(e -> nachalarmieren("Weiteres HLF")); actionPanel.add(btnNachHlf);
                }
                if (!dlkNachalarmiert) {
                    JButton btnNachDlk = createBtn("Drehleiter anfordern", new Color(142, 68, 173));
                    btnNachDlk.addActionListener(e -> nachalarmieren("Drehleiter (DLK)")); actionPanel.add(btnNachDlk);
                }
                if (!rtwNachalarmiert) {
                    JButton btnNachRett = createBtn("RTW & NEF anfordern", new Color(142, 68, 173));
                    btnNachRett.addActionListener(e -> nachalarmieren("RTW & NEF")); actionPanel.add(btnNachRett);
                }
                
                JButton btnZurueck = createBtn("Funkverkehr beenden", Color.DARK_GRAY);
                btnZurueck.addActionListener(e -> { sprechwunschAktiv = false; baueButtons(); });
                actionPanel.add(btnZurueck);
            } 
            else {
                if (einsatzTyp == 0) {
                    if (!alleAbgesessen) {
                        JButton btnRestAbsitzen = createBtn("Restliche Mannschaft absitzen!", new Color(230, 126, 34));
                        btnRestAbsitzen.addActionListener(e -> {
                            for(int i = 1; i < 6; i++) { if(sitzPlatz[i]) { sitzPlatz[i] = false; eingestiegenePersonen--; } }
                            alleAbgesessen = true; updateCrewUI();
                            log("GF: Brand bestaetigt! Komplette Staffel absitzen, wir bauen einen Loeschangriff auf!");
                            baueButtons();
                        });
                        actionPanel.add(btnRestAbsitzen);
                    } else {
                        if (!atPaAngelegt) {
                            JButton btnAT_PA = createBtn("A-Trupp: Retten!", new Color(192, 57, 43));
                            btnAT_PA.addActionListener(e -> { atPaAngelegt = true; log("A-TRUPP: Verstanden, gehen zur Menschenrettung vor."); baueButtons(); });
                            actionPanel.add(btnAT_PA);
                        } else if (!verteilerGesteckt) {
                            JButton btnAT_C = createBtn("A-Trupp: Verteiler & C-Rohr", new Color(192, 57, 43));
                            btnAT_C.addActionListener(e -> { verteilerGesteckt = true; log("A-TRUPP: Verstanden. Gehen mit C-Rohr vor."); baueButtons(); });
                            actionPanel.add(btnAT_C);
                        } else {
                            JButton btnAT_Angriff = createBtn("A-Trupp: Innenangriff!", new Color(192, 57, 43));
                            btnAT_Angriff.addActionListener(e -> {
                                if(!wasserversorgungSteht) { log("A-TRUPP: FEHLER! Kein Wasser am Strahlrohr!"); return; }
                                log("A-TRUPP: Wasser marsch! Gehen in Brandraum vor.");
                                btnAT_Angriff.setEnabled(false);
                                
                                angriffTimer = new Timer(1000, ev -> {
                                    int plus = zweitesHlfDa ? 3 : 2; 
                                    loeschFortschritt += plus;
                                    btnAT_Angriff.setText("Loeschangriff laeuft... " + Math.min(100, loeschFortschritt) + "%");
                                    
                                    if (loeschFortschritt >= 100) {
                                        ((Timer)ev.getSource()).stop();
                                        log("A-TRUPP: Feuer aus! Nachloescharbeiten beendet.");
                                        log("SYSTEM: Einsatz abgewickelt! Bitte Mannschaft aufsitzen lassen.");
                                        phase = 6; baueButtons();
                                    }
                                });
                                angriffTimer.start();
                            });
                            if(angriffTimer != null && angriffTimer.isRunning()) {
                                btnAT_Angriff.setEnabled(false);
                                btnAT_Angriff.setText("Loeschangriff laeuft... " + Math.min(100, loeschFortschritt) + "%");
                            }
                            actionPanel.add(btnAT_Angriff);
                        }

                        if (!wasserversorgungSteht) {
                            JButton btnWT_Wasser = createBtn("W-Trupp: Wasserversorgung", new Color(41, 128, 185));
                            btnWT_Wasser.addActionListener(e -> { wasserversorgungSteht = true; log("W-TRUPP: Verstanden. Bauen Wasserversorgung vom Hydranten auf."); baueButtons(); });
                            actionPanel.add(btnWT_Wasser);
                        }
                    }
                } 
                else {
                    JButton btnBma = createBtn("BMA zurueckstellen", new Color(39, 174, 96));
                    btnBma.addActionListener(e -> {
                        log("GF: Anlage zurueckgestellt und an Betreiber uebergeben. Fehlalarm.");
                        log("SYSTEM: Einsatz abgewickelt! Bitte aufsitzen, um die Rueckfahrt (Status 1) anzutreten.");
                        phase = 6; baueButtons();
                    });
                    actionPanel.add(btnBma);
                }
            }
        }
        else if (phase == 6) {
            if (!alleEingestiegen()) {
                JButton btnAufsitzen = createBtn("Aufsitzen befehlen!", new Color(230, 126, 34));
                btnAufsitzen.addActionListener(e -> {
                    btnAufsitzen.setEnabled(false);
                    btnAufsitzen.setText("Mannschaft sitzt auf...");
                    log("GF: Zum Abmarsch fertig! Aufsitzen!");
                    
                    Timer aufSitzTimer = new Timer(600, ev -> {
                        for(int i = 1; i < 6; i++) { 
                            if(!sitzPlatz[i]) {
                                sitzPlatz[i] = true; eingestiegenePersonen++; updateCrewUI();
                                if(alleEingestiegen()) {
                                    ((Timer)ev.getSource()).stop();
                                    log("SYSTEM: Mannschaft ist vollzaehlig auf dem Fahrzeug. Status 1 (Rueckfahrt) kann gedrueckt werden.");
                                    baueButtons();
                                }
                                break;
                            }
                        }
                    });
                    aufSitzTimer.start();
                });
                actionPanel.add(btnAufsitzen);
            } else {
                JLabel lblHint = new JLabel("Einsatz beendet. Bitte Status 1 druecken fuer die Rueckfahrt!");
                lblHint.setForeground(new Color(39, 174, 96)); lblHint.setFont(new Font("Segoe UI", Font.BOLD, 18)); actionPanel.add(lblHint);
            }
        }
        else if (phase == 8) {
            JLabel lblHint = new JLabel("An Wache eingetroffen. Bitte Status 2 druecken!");
            lblHint.setForeground(new Color(241, 196, 15)); lblHint.setFont(new Font("Segoe UI", Font.BOLD, 22)); actionPanel.add(lblHint);
        }

        actionPanel.revalidate(); actionPanel.repaint();
    }
}