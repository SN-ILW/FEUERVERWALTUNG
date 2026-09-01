package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public class CalltakerSimulator {
    public static JEditorPane txtStatusListe; 
    
    public static JFrame frame;
    public static JTextArea txtTelefonDialog;
    public static JTextArea txtFunk;
    public static JButton btnAnnehmen, btnAuflegen;
    public static double einsatzFaktor = 1.0;
    public static NotrufDialogKI.CtNotruf aktuellerAnruf = null;
    public static Timer klingelTimer;
    public static JDialog abfrageFenster;
    public static MapPanel pnlMapContainer; 

    public static HashMap<String, Integer> fahrzeugStatus = new HashMap<>();
    private static String letzterStatusHTML = "";

    // --- MODERNE FARBPALETTE ---
    private static final Color BG_MAIN = new Color(18, 18, 18);
    private static final Color BG_PANEL = new Color(30, 30, 30);
    private static final Color BG_TEXT = new Color(20, 20, 20);
    private static final Color ACCENT_GREEN = new Color(46, 204, 113);
    private static final Color ACCENT_BLUE = new Color(52, 152, 219);
    private static final Color ACCENT_YELLOW = new Color(241, 196, 15);
    private static final Color BORDER_COLOR = new Color(50, 50, 50);

    private static boolean checkInternet() {
        try {
            java.net.URL url = new java.net.URL("http://www.google.com");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000); 
            conn.connect();
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false; 
        }
    }

    public static void starten() {
        JWindow loadingWindow = new JWindow();
        loadingWindow.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        loadingWindow.setLocation(0, 0);
        
        JPanel pnlLoad = new JPanel(new GridBagLayout());
        pnlLoad.setBackground(BG_MAIN); 
        
        JPanel pnlBox = new JPanel();
        pnlBox.setLayout(new BoxLayout(pnlBox, BoxLayout.Y_AXIS));
        pnlBox.setBackground(BG_PANEL);
        pnlBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_YELLOW, 2),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));

        JLabel lblTitle = new JLabel("LEITSTELLE SCHWERIN - SYSTEMSTART");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ACCENT_YELLOW);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblStatus = new JLabel("Schritt 1/2: Prüfe Netzwerkverbindung...");
        lblStatus.setFont(new Font("Consolas", Font.PLAIN, 16));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true); 
        progress.setPreferredSize(new Dimension(350, 15));
        progress.setMaximumSize(new Dimension(350, 15));
        progress.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnBackToMenu = LogistikSimulator.createStyledButton("ZURÜCK ZUM HAUPTMENÜ", new Color(192, 57, 43));
        btnBackToMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBackToMenu.setVisible(false); 

        pnlBox.add(lblTitle);
        pnlBox.add(Box.createVerticalStrut(25));
        pnlBox.add(lblStatus);
        pnlBox.add(Box.createVerticalStrut(20));
        pnlBox.add(progress);
        pnlBox.add(btnBackToMenu);

        pnlLoad.add(pnlBox);
        loadingWindow.add(pnlLoad);
        loadingWindow.setVisible(true);

        new Thread(() -> {
            try {
                Thread.sleep(800); 
                if (!checkInternet()) {
                    SwingUtilities.invokeLater(() -> {
                        progress.setVisible(false);
                        lblStatus.setForeground(new Color(231, 76, 60)); 
                        lblStatus.setText("FEHLER: Keine Netzwerkverbindung gefunden!");
                        btnBackToMenu.setVisible(true);
                    });
                    return; 
                }
                SwingUtilities.invokeLater(() -> lblStatus.setText("Schritt 2/2: Lade Schweriner Realdaten aus OpenStreetMap..."));
                NotrufDialogKI.ladeAdressenOnline();
                Thread.sleep(500); 
                SwingUtilities.invokeLater(() -> {
                    loadingWindow.dispose();
                    baueHauptGUI(); 
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();

        btnBackToMenu.addActionListener(e -> {
            loadingWindow.dispose();
            Launcher.main(new String[]{}); 
        });
    }

    private static void baueHauptGUI() {
        frame = new JFrame("Calltaker-Modus (Disponent)");
        frame.setUndecorated(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG_MAIN);

        // --- TOP BAR ---
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(25, 25, 25)); // Etwas dunkler als Panel, heller als Main
        pnlTop.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR), // Feiner Strich unten
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        JLabel lblTitle = new JLabel("LEITSTELLE SCHWERIN - DISPONENTEN-PLATZ 1");
        lblTitle.setForeground(ACCENT_YELLOW);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTop.add(lblTitle, BorderLayout.WEST);

        JPanel pnlTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        pnlTopRight.setBackground(new Color(25, 25, 25));
        pnlTopRight.setOpaque(false);

        JLabel lblSlider = new JLabel("Einsatz-Rate: 1.0x");
        lblSlider.setForeground(Color.WHITE);
        lblSlider.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JSlider sliderRate = new JSlider(1, 10, 10);
        sliderRate.setBackground(new Color(25, 25, 25));
        sliderRate.setFocusable(false);
        sliderRate.addChangeListener(e -> {
            einsatzFaktor = sliderRate.getValue() / 10.0;
            lblSlider.setText("Einsatz-Rate: " + einsatzFaktor + "x");
        });

        JButton btnExit = LogistikSimulator.createStyledButton("X Modus Beenden", new Color(192, 57, 43));
        btnExit.addActionListener(e -> {
            if (klingelTimer != null) klingelTimer.stop();
            if (abfrageFenster != null) abfrageFenster.dispose();
            frame.dispose();
            Launcher.main(new String[]{}); 
        });

        pnlTopRight.add(lblSlider);
        pnlTopRight.add(sliderRate);
        pnlTopRight.add(btnExit);
        pnlTop.add(pnlTopRight, BorderLayout.EAST);

        pnlMapContainer = new MapPanel();

        // --- TELEFON PANEL (MODERN) ---
        JPanel pnlTelefon = new JPanel(new BorderLayout(0, 10)); // Abstand nur vertikal
        pnlTelefon.setBackground(BG_PANEL);
        pnlTelefon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Akzent-Überschrift (Grün)
        JLabel lblTelefonTitle = new JLabel(" TELEFON & NOTRUFABFRAGE");
        lblTelefonTitle.setForeground(Color.WHITE);
        lblTelefonTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTelefonTitle.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_GREEN));
        pnlTelefon.add(lblTelefonTitle, BorderLayout.NORTH);

        txtTelefonDialog = new JTextArea("System: Warte auf eingehenden Notruf...");
        txtTelefonDialog.setEditable(false);
        txtTelefonDialog.setBackground(BG_TEXT);
        txtTelefonDialog.setForeground(ACCENT_GREEN); 
        txtTelefonDialog.setFont(new Font("Consolas", Font.PLAIN, 15));
        txtTelefonDialog.setLineWrap(true);
        txtTelefonDialog.setWrapStyleWord(true);
        txtTelefonDialog.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollDialog = new JScrollPane(txtTelefonDialog);
        scrollDialog.setBorder(BorderFactory.createEmptyBorder()); // Rahmenlos!
        pnlTelefon.add(scrollDialog, BorderLayout.CENTER);

        JPanel pnlTelefonBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTelefonBtns.setBackground(BG_PANEL);
        btnAnnehmen = LogistikSimulator.createStyledButton("ANRUF ANNEHMEN", new Color(39, 174, 96));
        btnAuflegen = LogistikSimulator.createStyledButton("BEENDEN / ABLEHNEN", new Color(192, 57, 43));
        btnAnnehmen.setEnabled(false);
        btnAuflegen.setEnabled(false);
        pnlTelefonBtns.add(btnAnnehmen);
        pnlTelefonBtns.add(btnAuflegen);
        pnlTelefon.add(pnlTelefonBtns, BorderLayout.SOUTH);

        // --- FUNK PANEL (MODERN) ---
        JPanel pnlFunk = new JPanel(new BorderLayout(0, 10));
        pnlFunk.setBackground(BG_PANEL);
        pnlFunk.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Akzent-Überschrift (Blau)
        JLabel lblFunkTitle = new JLabel(" FUNK & EINSATZ-STATUS");
        lblFunkTitle.setForeground(Color.WHITE);
        lblFunkTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFunkTitle.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_BLUE));
        pnlFunk.add(lblFunkTitle, BorderLayout.NORTH);

        JPanel pnlFunkSplit = new JPanel(new BorderLayout(10, 0)); // Abstand zwischen Funk und Status
        pnlFunkSplit.setBackground(BG_PANEL);

        txtFunk = new JTextArea("System: Funkkanal geoffnet.\n");
        txtFunk.setEditable(false);
        txtFunk.setBackground(BG_TEXT);
        txtFunk.setForeground(ACCENT_BLUE); 
        txtFunk.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtFunk.setMargin(new Insets(10, 10, 10, 10));
        txtFunk.setLineWrap(true);
        txtFunk.setWrapStyleWord(true);
        JScrollPane scrollFunk = new JScrollPane(txtFunk);
        scrollFunk.setBorder(BorderFactory.createEmptyBorder()); // Rahmenlos!
        pnlFunkSplit.add(scrollFunk, BorderLayout.CENTER);

        txtStatusListe = new JEditorPane();
        txtStatusListe.setContentType("text/html");
        txtStatusListe.setEditable(false);
        txtStatusListe.setBackground(BG_TEXT);
        JScrollPane scrollStatus = new JScrollPane(txtStatusListe);
        scrollStatus.setPreferredSize(new Dimension(260, 0)); 
        scrollStatus.setBorder(BorderFactory.createEmptyBorder()); // Rahmenlos!
        pnlFunkSplit.add(scrollStatus, BorderLayout.EAST);
        
        pnlFunk.add(pnlFunkSplit, BorderLayout.CENTER);

        // --- SCHLANKE SPLITPANES ---
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTelefon, pnlFunk);
        rightSplit.setDividerSize(4); // Sehr schlank
        rightSplit.setBorder(null);
        rightSplit.setBackground(BG_MAIN);
        rightSplit.setResizeWeight(0.45); 
        
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlMapContainer, rightSplit);
        mainSplit.setDividerSize(4); // Sehr schlank
        mainSplit.setBorder(null);
        mainSplit.setBackground(BG_MAIN);
        mainSplit.setResizeWeight(0.65); 

        frame.add(pnlTop, BorderLayout.NORTH);
        frame.add(mainSplit, BorderLayout.CENTER);
        frame.setVisible(true);
        ausgabeTelefon("System hochgefahren. Modus aktiv. Warte auf Anrufer...");

        klingelTimer = new Timer(5000, e -> {
            if (aktuellerAnruf == null && Math.random() < (0.3 * einsatzFaktor)) { 
                aktuellerAnruf = NotrufDialogKI.generiereNotruf();
                ausgabeTelefon("\n[!!!] EINGEHENDER NOTRUF [!!!]");
                LogistikSimulator.playSound("notruf.wav");
                btnAnnehmen.setEnabled(true);
                btnAnnehmen.setBackground(new Color(39, 174, 96));
            }
        });
        klingelTimer.start();

        btnAnnehmen.addActionListener(e -> {
            if (aktuellerAnruf != null) {
                btnAnnehmen.setEnabled(false);
                btnAnnehmen.setBackground(new Color(60, 60, 60));
                btnAuflegen.setEnabled(true);
                ausgabeTelefon("\n--- VERBINDUNG HERGESTELLT ---");
                ausgabeTelefon("Disponent: Notruf Feuerwehr und Rettungsdienst, wo genau ist der Notfallort?");
                
                Timer t = new Timer(1500, ev -> {
                    ausgabeTelefon("Anrufer: " + aktuellerAnruf.textIntro);
                    ausgabeTelefon("Anrufer: " + aktuellerAnruf.textWo);
                    oeffneAbfrageFenster();
                });
                t.setRepeats(false);
                t.start();
            }
        });

        btnAuflegen.addActionListener(e -> {
            ausgabeTelefon("\n--- VERBINDUNG BEENDET ---");
            aktuellerAnruf = null;
            btnAuflegen.setEnabled(false);
            btnAnnehmen.setEnabled(false);
            if (abfrageFenster != null && abfrageFenster.isVisible()) abfrageFenster.dispose();
        });

        Timer statusTimer = new Timer(1000, e -> updateStatusListe());
        statusTimer.start();
        updateStatusListe(); 
    }

    public static void oeffneAbfrageFenster() {
        if (abfrageFenster != null) abfrageFenster.dispose(); 
        abfrageFenster = new JDialog(frame, "Notrufabfrage", false); 
        abfrageFenster.setUndecorated(true);
        abfrageFenster.setSize(350, 300);
        abfrageFenster.setLocationRelativeTo(frame); 
        
        JPanel content = new JPanel(new GridLayout(6, 1, 10, 10));
        content.setBackground(BG_PANEL);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1), // Feiner Rahmen
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitle = new JLabel(" W-FRAGEN STELLEN");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_GREEN));
        content.add(lblTitle);

        JButton btnWas = LogistikSimulator.createStyledButton("Was genau ist passiert?", new Color(41, 128, 185));
        JButton btnVerletzte = LogistikSimulator.createStyledButton("Gibt es Verletzte?", new Color(41, 128, 185));
        JButton btnName = LogistikSimulator.createStyledButton("Wie lautet Ihr Name?", new Color(41, 128, 185));
        JButton btnAlarm = LogistikSimulator.createStyledButton("FAHRZEUGE ALARMIEREN", new Color(192, 57, 43));

        btnWas.addActionListener(e -> {
            ausgabeTelefon("\nDisponent: Was genau ist passiert?");
            aktuellerAnruf.frageWasGestellt = true; 
            btnWas.setEnabled(false); 
            Timer t = new Timer(1000, ev -> ausgabeTelefon("Anrufer: " + aktuellerAnruf.textWas));
            t.setRepeats(false); t.start();
        });

        btnVerletzte.addActionListener(e -> {
            ausgabeTelefon("\nDisponent: Gibt es Verletzte oder Personen in Gefahr?");
            aktuellerAnruf.frageVerletzteGestellt = true; 
            btnVerletzte.setEnabled(false);
            Timer t = new Timer(1000, ev -> ausgabeTelefon("Anrufer: " + aktuellerAnruf.textVerletzte));
            t.setRepeats(false); t.start();
        });

        btnName.addActionListener(e -> {
            ausgabeTelefon("\nDisponent: Wie lautet Ihr Name?");
            aktuellerAnruf.frageNameGestellt = true; 
            btnName.setEnabled(false);
            Timer t = new Timer(1000, ev -> ausgabeTelefon("Anrufer: Ich heiße " + aktuellerAnruf.anruferName + "."));
            t.setRepeats(false); t.start();
        });

        btnAlarm.addActionListener(e -> {
            ausgabeTelefon("\nDisponent: Verstanden. Bleiben Sie ruhig, die Einsatzkräfte sind auf dem Weg!");
            abfrageFenster.dispose();
            oeffneAlarmierungsFenster(aktuellerAnruf);
        });

        final Point[] dragPoint = new Point[1];
        content.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { dragPoint[0] = e.getPoint(); }});
        content.addMouseMotionListener(new MouseMotionAdapter() { public void mouseDragged(MouseEvent e) { abfrageFenster.setLocation(abfrageFenster.getLocation().x + e.getX() - dragPoint[0].x, abfrageFenster.getLocation().y + e.getY() - dragPoint[0].y); }});
        content.add(btnWas); content.add(btnVerletzte); content.add(btnName); content.add(btnAlarm);
        abfrageFenster.add(content);
        abfrageFenster.setVisible(true);
    }

    public static void oeffneAlarmierungsFenster(NotrufDialogKI.CtNotruf anruf) {
        JDialog dispoFenster = new JDialog(frame, "Einsatz anlegen & Alarmieren", false);
        dispoFenster.setUndecorated(true);
        dispoFenster.setSize(800, 550); 
        dispoFenster.setLocationRelativeTo(frame);
        
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setBackground(BG_MAIN);
        mainContent.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // --- INFO PANEL ---
        JPanel pnlDatenWrap = new JPanel(new BorderLayout(0, 10));
        pnlDatenWrap.setBackground(BG_MAIN);
        
        JLabel lblDatenTitle = new JLabel(" EINSATZ-INFORMATIONEN");
        lblDatenTitle.setForeground(Color.WHITE);
        lblDatenTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblDatenTitle.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_YELLOW));
        pnlDatenWrap.add(lblDatenTitle, BorderLayout.NORTH);
        
        JPanel pnlDaten = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlDaten.setBackground(BG_PANEL);
        pnlDaten.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        class FormField {
            void add(String label, String value) {
                JLabel l = new JLabel(label); l.setForeground(Color.LIGHT_GRAY); l.setFont(new Font("Segoe UI", Font.BOLD, 13));
                JTextField t = new JTextField(value); t.setEditable(false); t.setBackground(BG_TEXT); t.setForeground(Color.WHITE);
                t.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                pnlDaten.add(l); pnlDaten.add(t);
            }
        }
        FormField form = new FormField();
        form.add("Einsatzort:", anruf.strasse);
        form.add("Meldebild:", anruf.frageWasGestellt ? anruf.kurzBeschreibung : "??? (NICHT ERFRAGT)");
        form.add("Patienten/Verletzte:", anruf.frageVerletzteGestellt ? "Info liegt vor (Siehe Chat)" : "??? (UNGEKLAERT)");
        form.add("Mitteiler Name:", anruf.frageNameGestellt ? anruf.anruferName : "UNBEKANNT");
        pnlDatenWrap.add(pnlDaten, BorderLayout.CENTER);

        // --- DISPO PANEL ---
        JPanel pnlAlarmWrap = new JPanel(new BorderLayout(0, 10));
        pnlAlarmWrap.setBackground(BG_MAIN);
        
        JLabel lblAlarmTitle = new JLabel(" KRÄFTEDISPOSITION");
        lblAlarmTitle.setForeground(Color.WHITE);
        lblAlarmTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAlarmTitle.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_YELLOW));
        pnlAlarmWrap.add(lblAlarmTitle, BorderLayout.NORTH);

        JPanel pnlAlarmInner = new JPanel(new BorderLayout(0, 10));
        pnlAlarmInner.setBackground(BG_PANEL);
        pnlAlarmInner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlTopAlarm = new JPanel(new BorderLayout(10, 0));
        pnlTopAlarm.setBackground(BG_PANEL);
        JLabel lblStichwort = new JLabel("Stichwort:");
        lblStichwort.setForeground(Color.LIGHT_GRAY);
        JComboBox<String> cbStichwort = new JComboBox<>(new String[]{"Bitte waehlen...", "F1 (Kleinfeuer)", "F2 (Wohnungsbrand)", "F3 (Grossbrand)", "H1 (Hilfeleistung)", "R1 (Rettungswagen)", "R2 (Notarzt Einsatz)"});
        cbStichwort.setBackground(BG_TEXT); cbStichwort.setForeground(Color.WHITE);
        pnlTopAlarm.add(lblStichwort, BorderLayout.WEST); 
        pnlTopAlarm.add(cbStichwort, BorderLayout.CENTER);
        pnlAlarmInner.add(pnlTopAlarm, BorderLayout.NORTH);

        JPanel pnlFahrzeuge = new JPanel();
        pnlFahrzeuge.setLayout(new BoxLayout(pnlFahrzeuge, BoxLayout.Y_AXIS));
        pnlFahrzeuge.setBackground(BG_PANEL);
        
        List<JCheckBox> alleFahrzeugBoxen = new ArrayList<>();
        
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "BF Nord", new String[]{"HLF", "DLK", "KEF", "RTW 1", "RTW 2", "RTW 3", "NEF"});
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "BF Sued", new String[]{"ELW", "HLF 1", "HLF 2", "DLK 1", "DLK 2", "RW", "RTW 1", "RTW 2", "RTW 3", "NEF"});
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "FF Mitte", new String[]{"HLF 1", "HLF 2", "ELW", "ELW-2", "MTW"});
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "FF Schlossgarten", new String[]{"HLF 1", "HLF 2", "MTW"});
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "FF Warnitz", new String[]{"HLF", "MTW"});
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "FF Wuestmark", new String[]{"HLF", "MTW"});
        baueWachenBlock(pnlFahrzeuge, alleFahrzeugBoxen, "FF Wickendorf", new String[]{"HLF", "TLF-Wald", "MTW"});

        JScrollPane scrollFahrzeuge = new JScrollPane(pnlFahrzeuge);
        scrollFahrzeuge.setBorder(BorderFactory.createEmptyBorder());
        scrollFahrzeuge.getVerticalScrollBar().setUnitIncrement(16);
        pnlAlarmInner.add(scrollFahrzeuge, BorderLayout.CENTER);
        
        pnlAlarmWrap.add(pnlAlarmInner, BorderLayout.CENTER);

        // --- BOTTOM BUTTONS ---
        JPanel pnlBottomBtns = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlBottomBtns.setBackground(BG_MAIN);
        
        JButton btnAbbrechen = LogistikSimulator.createStyledButton("ABBRECHEN", BORDER_COLOR);
        btnAbbrechen.addActionListener(e -> dispoFenster.dispose());
        
        JButton btnAusloesen = LogistikSimulator.createStyledButton("ALARM AUSLÖSEN", new Color(192, 57, 43));
        btnAusloesen.setPreferredSize(new Dimension(0, 50));
        
        pnlBottomBtns.add(btnAbbrechen);
        pnlBottomBtns.add(btnAusloesen);

        btnAusloesen.addActionListener(e -> {
            if (cbStichwort.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(dispoFenster, "Du musst ein Stichwort waehlen!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            ArrayList<String> alarmierteFahrzeuge = new ArrayList<>();
            for (JCheckBox cb : alleFahrzeugBoxen) {
                if (cb.isSelected()) alarmierteFahrzeuge.add(cb.getActionCommand()); 
            }
            if (alarmierteFahrzeuge.isEmpty()) {
                JOptionPane.showMessageDialog(dispoFenster, "Du musst mindestens ein Fahrzeug auswaehlen!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String stichwort = (String) cbStichwort.getSelectedItem();
            ausgabeFunk("LEITSTELLE: ALARM fuer Einsatz '" + stichwort + "' in der " + anruf.strasse + "!");
            ausgabeFunk("ALARMIERT WURDEN:\n  - " + String.join("\n  - ", alarmierteFahrzeuge));
            
            int eId = pnlMapContainer.erstelleEinsatz(anruf.koordinate);

            for (String fz : alarmierteFahrzeuge) {
                String wachenName = fz.substring(0, fz.indexOf(" (")); 
                int currentStatus = fahrzeugStatus.getOrDefault(fz, 2);
                int ruestZeitSec;
                if (currentStatus == 1) {
                    ruestZeitSec = 3; 
                } else {
                    int maxZeit = (wachenName.startsWith("BF")) ? 45 : 95;
                    ruestZeitSec = 10 + (int)(Math.random() * (maxZeit - 10)); 
                }
                int fahrZeitSec = 45 + (int)(Math.random() * 90); 
                
                fahrzeugStatus.put(fz, 10);
                
                Coordinate startPos = pnlMapContainer.getFahrzeugPosition(fz);
                if (startPos == null) startPos = pnlMapContainer.wachenKoords.get(wachenName);
                
                boolean startVisible = (currentStatus == 1);
                pnlMapContainer.sendeFahrzeug(fz, startPos, anruf.koordinate, ruestZeitSec, fahrZeitSec, false, startVisible);
                
                Timer ausrueckeTimer = new Timer(ruestZeitSec * 1000, ev -> {
                    fahrzeugStatus.put(fz, 3);
                    ausgabeFunk(fz + " funkt: Status 3 (Auf Anfahrt zur Einsatzstelle)");
                });
                ausrueckeTimer.setRepeats(false); 
                ausrueckeTimer.start();
                
                Timer ankunftTimer = new Timer((ruestZeitSec + fahrZeitSec) * 1000, ev -> {
                    ausgabeFunk(fz + " funkt: Status 4 (Ankunft an Einsatzstelle)");
                    fahrzeugStatus.put(fz, 4); 
                    pnlMapContainer.fahrzeugAngekommen(eId, fz); 
                    
                    int einsatzDauer = 45 + (int)(Math.random() * 45); 
                    Timer status1Timer = new Timer(einsatzDauer * 1000, endEv -> {
                        if(fahrzeugStatus.getOrDefault(fz, 2) != 4) return; 
                        
                        ausgabeFunk(fz + " funkt: Status 1 (Einsatzbereit auf Funk, Rueckfahrt)");
                        fahrzeugStatus.put(fz, 1); 
                        pnlMapContainer.fahrzeugFrei(eId, fz); 
                        
                        int fahrZeitSecRueck = 45 + (int)(Math.random() * 90);
                        Coordinate zielPos = pnlMapContainer.wachenKoords.get(wachenName);
                        pnlMapContainer.sendeFahrzeug(fz, anruf.koordinate, zielPos, 0, fahrZeitSecRueck, true, true);
                        
                        Timer status2Timer = new Timer(fahrZeitSecRueck * 1000, s2Ev -> {
                            if (fahrzeugStatus.getOrDefault(fz, 2) == 1) { 
                                ausgabeFunk(fz + " funkt: Status 2 (Einsatzbereit auf Wache)");
                                fahrzeugStatus.put(fz, 2);
                                pnlMapContainer.entferneFahrzeug(fz);
                            }
                        });
                        status2Timer.setRepeats(false);
                        status2Timer.start();
                    });
                    status1Timer.setRepeats(false);
                    status1Timer.start();
                });
                ankunftTimer.setRepeats(false); 
                ankunftTimer.start();
            }
            dispoFenster.dispose();
            btnAuflegen.doClick(); 
        });

        mainContent.add(pnlDatenWrap, BorderLayout.NORTH);
        mainContent.add(pnlAlarmWrap, BorderLayout.CENTER);
        mainContent.add(pnlBottomBtns, BorderLayout.SOUTH);
        
        dispoFenster.add(mainContent);
        dispoFenster.setVisible(true);
    }

    private static void baueWachenBlock(JPanel parent, List<JCheckBox> boxListe, String wachenName, String[] fahrzeuge) {
        JLabel lbl = new JLabel(wachenName.toUpperCase());
        lbl.setForeground(ACCENT_YELLOW);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        parent.add(lbl);
        
        JPanel grid = new JPanel(new GridLayout(0, 2, 2, 2));
        grid.setBackground(BG_PANEL);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        for (String fz : fahrzeuge) {
            String fullId = wachenName + " (" + fz + ")";
            int status = fahrzeugStatus.getOrDefault(fullId, 2); 

            JCheckBox cb = new JCheckBox(fz);
            cb.setActionCommand(fullId); 
            cb.setBackground(BG_PANEL);
            cb.setFocusPainted(false);
            cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            
            if (status == 1 || status == 2) {
                cb.setForeground(Color.WHITE);
                if (status == 1) cb.setText(fz + " (S1)");
            } else {
                cb.setForeground(Color.GRAY);
                cb.setEnabled(false);
                cb.setText(fz + " (S" + (status==10?"C":status) + ")");
            }
            
            boxListe.add(cb);
            grid.add(cb);
        }
        parent.add(grid);
    }

    public static void ausgabeTelefon(String text) {
        txtTelefonDialog.append(text + "\n");
        txtTelefonDialog.setCaretPosition(txtTelefonDialog.getDocument().getLength()); 
    }

    public static void ausgabeFunk(String text) {
        txtFunk.append("> " + text + "\n");
        txtFunk.setCaretPosition(txtFunk.getDocument().getLength());
    }
    
    public static void updateStatusListe() {
        if (txtStatusListe == null) return;
        StringBuilder sb = new StringBuilder();
        
        // UPDATE: Hintergrundfarbe auf modernes #141414 (sehr dunkel) angepasst, ohne Scrollbalken-Geflacker
        sb.append("<html><body style='font-family: Consolas; font-size: 11px; color: #F1C40F; background-color: #141414; margin: 0px; padding: 5px;'>");
        
        String[] wachen = { "BF Nord", "BF Sued", "FF Mitte", "FF Schlossgarten", "FF Warnitz", "FF Wuestmark", "FF Wickendorf" };
        String[][] fahrzeuge = {
            {"HLF", "DLK", "KEF", "RTW 1", "RTW 2", "RTW 3", "NEF"},
            {"ELW", "HLF 1", "HLF 2", "DLK 1", "DLK 2", "RW", "RTW 1", "RTW 2", "RTW 3", "NEF"},
            {"HLF 1", "HLF 2", "ELW", "ELW-2", "MTW"},
            {"HLF 1", "HLF 2", "MTW"},
            {"HLF", "MTW"},
            {"HLF", "MTW"},
            {"HLF", "TLF-Wald", "MTW"}
        };
        
        for (int i = 0; i < wachen.length; i++) {
            sb.append("<b style='color: white;'>=== ").append(wachen[i].toUpperCase()).append(" ===</b><br>");
            for (String fz : fahrzeuge[i]) {
                String fullName = wachen[i] + " (" + fz + ")";
                int status = fahrzeugStatus.getOrDefault(fullName, 2); 
                if (status == 10) {
                    sb.append("<span style='color: #E74C3C;'>(C | ").append(fz).append(")</span><br>");
                } else {
                    sb.append("(S").append(status).append(" | ").append(fz).append(")<br>");
                }
            }
            sb.append("<br>");
        }
        sb.append("</body></html>");
        
        String neuerHTML = sb.toString();
        if (!neuerHTML.equals(letzterStatusHTML)) {
            txtStatusListe.setText(neuerHTML);
            letzterStatusHTML = neuerHTML;
        }
    }
}