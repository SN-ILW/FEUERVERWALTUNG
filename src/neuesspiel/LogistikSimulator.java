package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class LogistikSimulator {
    
    public static JFrame frame;
    public static JTextArea txtStatus, txtEinsatz;
    public static JButton btnPause, btnPlay, btnFastForward;
    public static JLabel topUhrzeitLabel, playerInfoLabel, notrufLabel;
    public static JPanel notrufPanel;
    public static JEditorPane fmsBoard;
    
    public static TagesMission aktuelleMission = null; 
    public static JButton btnPostfach, btnTagBeenden;
    public static int aktuellerKredit = 0;
    public static int taeglicheKreditRate = 0;
    public static int budget = 25000;
    public static int xp = 0;
    public static int level = 1;
    public static int tag = 1;
    public static int inGameSekunden = 7 * 3600; 
    public static int speed = 1; 
    public static boolean cfgLogistikAktiv = true;
    public static JButton btnLog;
    public static boolean cfgKrankentransport = true;
    public static boolean cfgBeschaedigung = true;
    public static boolean cfgKrankheit = true;
    public static boolean cfgAutoTransfer = false;
    
    public static ArrayList<VertragVorlage> vertragsVorlagen = new ArrayList<>();
    public static ArrayList<Vertrag> aktiveVertraege = new ArrayList<>();
    public static HashMap<Personal, Wache> verliehenesPersonal = new HashMap<>(); 
    
    public static boolean cfgSoundNotruf = true;
    public static boolean cfgSoundStatus6 = true;
    public static boolean cfgSoundStatus7 = true;
    
    public static int volNotruf = 100;
    public static int volStatus6 = 100;
    public static int volStatus7 = 100;
    
    public static int abgelehnteEinsaetzeHeute = 0;

    // Alte globale techWerkstatt und techRuheraum wurden hier entfernt!
    public static boolean techGrossabnehmer = false;
    public static int lehrerStufe = 0; 
    public static int calltakerStufe = 0;
    public static double notrufRate = 1.0; 

    public static boolean klinik1Abgemeldet = false;
    public static boolean klinik2Abgemeldet = false;
    public static boolean klinikCrivitzAbgemeldet = false;
    public static boolean klinikLeezenAbgemeldet = false;
    public static boolean klinikHagenowAbgemeldet = false;

    public static boolean techKlinikCrivitz = false;
    public static boolean techKlinikLeezen = false;
    public static boolean techKlinikHagenow = false;

    public static ArrayList<Wache> wachen = new ArrayList<>();
    public static ArrayList<Einsatz> aktiveEinsaetze = new ArrayList<>();
    public static ArrayList<Einsatz> tagesStatistik = new ArrayList<>();
    public static Einsatz aktuellerNotruf = null;

    public static HashMap<String, Integer> hauptlager = new HashMap<>();
    public static ArrayList<Bestellung> lieferungen = new ArrayList<>();
    public static ArrayList<Email> postfach = new ArrayList<>();
    
    public static ArrayList<EinsatzVorlage> vorlagenPool = new ArrayList<>();
    public static ArrayList<CustomMaterial> customMaterials = new ArrayList<>();
    
    public static Event aktuellesEvent = null;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", new Color(35, 35, 35));
            UIManager.put("OptionPane.background", new Color(35, 35, 35));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("CheckBox.background", new Color(35, 35, 35));
            UIManager.put("CheckBox.foreground", Color.WHITE);
            UIManager.put("ComboBox.background", new Color(60, 60, 60));
            UIManager.put("ComboBox.foreground", Color.WHITE);
            UIManager.put("TextField.background", new Color(60, 60, 60));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("Spinner.background", new Color(60, 60, 60));
            UIManager.put("Spinner.foreground", Color.WHITE);
            UIManager.put("List.background", new Color(43, 43, 43));
            UIManager.put("List.foreground", Color.WHITE);
            UIManager.put("Button.background", new Color(60, 60, 60));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("TabbedPane.background", new Color(45, 45, 45));
            UIManager.put("TabbedPane.foreground", Color.WHITE);
            UIManager.put("TabbedPane.selected", new Color(60, 60, 60));
            UIManager.put("TabbedPane.contentAreaColor", new Color(35, 35, 35));
            UIManager.put("Table.background", new Color(43, 43, 43));
            UIManager.put("Table.foreground", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(20, 30, 48));
            UIManager.put("TableHeader.foreground", Color.WHITE);
        } catch (Exception e) {}
        
        if (!new File("savegame.properties").exists()) initStandardDaten();
        else if (!SpeicherManager.laden("savegame.properties")) initStandardDaten();
        
        frame = new JFrame("FeuerwehrVerwaltung");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                beendenMitSpeichern();
            }
        });
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLayout(new BorderLayout());
        
        Color bgDark = new Color(35, 35, 35);
        frame.getContentPane().setBackground(bgDark); 

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setBackground(new Color(25, 25, 25));
        
        JPanel pnlTopLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTopLeft.setBackground(new Color(25, 25, 25));
        
        topUhrzeitLabel = new JLabel(" ", SwingConstants.LEFT); 
        topUhrzeitLabel.setForeground(Color.WHITE); 
        topUhrzeitLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTopLeft.add(topUhrzeitLabel);
        pnlTop.add(pnlTopLeft, BorderLayout.WEST);
        
        JPanel pnlTime = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTime.setBackground(new Color(25, 25, 25));
        
        JLabel lblRate = new JLabel("Notruf-Rate: ");
        lblRate.setForeground(Color.WHITE);
        
        JComboBox<String> cbRate = new JComboBox<>(new String[]{"0.10x", "0.25x", "0.5x", "0.75x", "1.0x", "1.5x", "2.0x"});
        cbRate.setSelectedItem(notrufRate + "x");
        if(cbRate.getSelectedIndex() == -1) cbRate.setSelectedIndex(4); 
        cbRate.addActionListener(e -> {
            String val = (String) cbRate.getSelectedItem();
            notrufRate = Double.parseDouble(val.replace("x", ""));
        });
        
        btnPause = createStyledButton("Pause", new Color(60, 63, 65)); 
        btnPlay = createStyledButton("Play", new Color(39, 174, 96)); 
        btnFastForward = createStyledButton(">> 5x Spulen", new Color(60, 63, 65));
        JButton btnExit = createStyledButton("X Beenden", new Color(192, 57, 43));
        
        btnPause.addActionListener(e -> setSpeed(0));
        btnPlay.addActionListener(e -> setSpeed(1));
        btnFastForward.addActionListener(e -> setSpeed(3));
        btnExit.addActionListener(e -> beendenMitSpeichern()); 
        
        pnlTime.add(lblRate); pnlTime.add(cbRate);
        pnlTime.add(btnPause); pnlTime.add(btnPlay); pnlTime.add(btnFastForward); pnlTime.add(btnExit);
        pnlTop.add(pnlTime, BorderLayout.EAST);
        
        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 0, 0));
        pnlCenter.setBackground(bgDark);
        
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY)); 
        
        JPanel pnlPlayerInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlPlayerInfo.setBackground(new Color(40, 40, 40));
        pnlPlayerInfo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        playerInfoLabel = new JLabel(" ");
        playerInfoLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        playerInfoLabel.setForeground(Color.WHITE);
        pnlPlayerInfo.add(playerInfoLabel);
        pnlLeft.add(pnlPlayerInfo, BorderLayout.NORTH);
        
        fmsBoard = new JEditorPane("text/html", "");
        fmsBoard.setEditable(false);
        fmsBoard.setBackground(bgDark);
        
        fmsBoard.addHyperlinkListener(e -> {
            if(e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                String desc = e.getDescription();
                if(desc.startsWith("FZG:")) {
                    String funkname = desc.substring(4);
                    for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if(f.funkrufname.equals(funkname) && f.aktuellerEinsatz != null) { FensterManager.oeffneEinsatzDetails(f.aktuellerEinsatz); break; }
                } else if(desc.startsWith("HOSP:")) {
                    String funkname = desc.substring(5);
                    for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if(f.funkrufname.equals(funkname)) { FensterManager.oeffneKrankenhausWahl(f); break; }
                }
            }
        });
        
        JScrollPane scrollFms = new JScrollPane(fmsBoard);
        scrollFms.setBorder(BorderFactory.createEmptyBorder());
        pnlLeft.add(scrollFms, BorderLayout.CENTER);
        
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.GRAY));
        
        JPanel topRightWrapper = new JPanel(new BorderLayout());
        topRightWrapper.setBackground(new Color(40, 40, 40));
        topRightWrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        
        notrufPanel = new JPanel(new BorderLayout());
        notrufPanel.setBackground(new Color(40, 40, 40)); 
        notrufLabel = new JLabel("Kein Notruf anliegend.", SwingConstants.CENTER);
        notrufLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        notrufLabel.setForeground(Color.WHITE);
        notrufLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        notrufPanel.add(notrufLabel, BorderLayout.CENTER);
        
        topRightWrapper.add(notrufPanel, BorderLayout.CENTER);
        pnlRight.add(topRightWrapper, BorderLayout.NORTH);
        
        txtEinsatz = new JTextArea(); 
        txtEinsatz.setEditable(false); 
        txtEinsatz.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtEinsatz.setBackground(bgDark); 
        txtEinsatz.setForeground(Color.WHITE);
        txtEinsatz.setLineWrap(true);
        txtEinsatz.setWrapStyleWord(true);
        
        JScrollPane scrollEinsatz = new JScrollPane(txtEinsatz);
        scrollEinsatz.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
        pnlRight.add(scrollEinsatz, BorderLayout.CENTER);
        
        pnlCenter.add(pnlLeft); 
        pnlCenter.add(pnlRight);
        
        JPanel pnlBottom = new JPanel(new GridLayout(3, 4, 5, 5));
        pnlBottom.setBackground(bgDark);
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton btnDisp = createStyledButton("Notruf Disponieren", new Color(39, 174, 96));
        JButton btnNach = createStyledButton("Nachforderungen bedienen", new Color(211, 84, 0));
        JButton btnAblehnen = createStyledButton("Notruf Ablehnen (-250 XP)", new Color(192, 57, 43));
        btnPostfach = createStyledButton("Postfach (0)", new Color(108, 122, 137));
        
        JButton btnPers = createStyledButton("Personalwesen", new Color(41, 128, 185));
        btnLog = createStyledButton("Lager & Logistik", new Color(22, 160, 133));
        JButton btnFuhr = createStyledButton("Fuhrpark & Werkstatt", new Color(192, 57, 43));
        JButton btnBau = createStyledButton("Wachen & Gebaeude", new Color(142, 68, 173));
        
        JButton btnSys = createStyledButton("System & Editor", new Color(127, 140, 141));
        JButton btnKlinik = createStyledButton("Kliniken & Betten", new Color(230, 126, 34)); 
        
        JButton btnBank = createStyledButton("Bank & Finanzen", new Color(241, 196, 15));
        btnBank.setForeground(Color.BLACK);
        
        btnTagBeenden = createStyledButton("TAG BEENDEN (ab 19 Uhr)", new Color(142, 68, 173));
        btnTagBeenden.setEnabled(false);

        btnDisp.addActionListener(e -> disponiereNotruf());
        btnNach.addActionListener(e -> FensterManager.oeffneNachforderungMenu());
        btnAblehnen.addActionListener(e -> { 
            if(aktuellerNotruf != null) { 
                aktuellerNotruf = null; xp -= 250; abgelehnteEinsaetzeHeute++;
                uiAktualisieren(getUhrzeit()); 
            } 
        });
        btnPostfach.addActionListener(e -> FensterManager.oeffnePostfach());
        btnPers.addActionListener(e -> FensterManager.oeffnePersonalHauptmenu());
        btnLog.addActionListener(e -> FensterManager.oeffneLogistikHauptmenu());
        btnFuhr.addActionListener(e -> FensterManager.oeffneFuhrparkHauptmenu());
        btnBau.addActionListener(e -> FensterManager.oeffneWachenAusbau());
        btnSys.addActionListener(e -> FensterManager.oeffneSystemHauptmenu());
        btnKlinik.addActionListener(e -> FensterManager.oeffneBettenUebersicht());
        btnBank.addActionListener(e -> FensterManager.oeffneBank());
        btnTagBeenden.addActionListener(e -> {
            if(!hatGenugGeplantesPersonal()) {
                int wahl = JOptionPane.showConfirmDialog(frame, "Fuer den naechsten Tag ist nicht ausreichend Personal geplant!\nEinige Fahrzeuge werden ausfallen.\nTrotzdem den Tag beenden?", "Schichtplan unvollstaendig", JOptionPane.YES_NO_OPTION);
                if(wahl != JOptionPane.YES_OPTION) return;
            }
            tagesWechsel();
        });
        
        pnlBottom.add(btnDisp); pnlBottom.add(btnNach); pnlBottom.add(btnAblehnen); pnlBottom.add(btnPostfach);
        pnlBottom.add(btnPers); pnlBottom.add(btnLog); pnlBottom.add(btnFuhr); pnlBottom.add(btnBau);
        pnlBottom.add(btnSys); pnlBottom.add(btnKlinik); pnlBottom.add(btnBank); pnlBottom.add(btnTagBeenden);
        
        frame.add(pnlTop, BorderLayout.NORTH);
        frame.add(pnlCenter, BorderLayout.CENTER);
        frame.add(pnlBottom, BorderLayout.SOUTH);
        
        frame.setVisible(true);

        new Timer(1000, e -> {
            if (speed > 0) {
                inGameSekunden += (speed * 10);
                String uhrzeit = getUhrzeit();
                
                if (inGameSekunden % 900 == 0) {
                    klinik1Abgemeldet = Math.random() < 0.35; 
                    if(techKlinikCrivitz) klinikCrivitzAbgemeldet = Math.random() < 0.15;
                    if(techKlinikLeezen) klinikLeezenAbgemeldet = Math.random() < 0.10;
                    if(techKlinikHagenow) klinikHagenowAbgemeldet = Math.random() < 0.05;
                }

                for (int i = lieferungen.size() - 1; i >= 0; i--) {
                    Bestellung b = lieferungen.get(i);
                    b.dauerSec -= speed;
                    if (b.dauerSec <= 0) {
                        hauptlager.put(b.materialName, hauptlager.getOrDefault(b.materialName, 0) + b.menge);
                        lieferungen.remove(i);
                    }
                }
                
                if (cfgAutoTransfer && inGameSekunden % 1200 == 0) {
                    for(Wache w : wachen) {
                        for(CustomMaterial cm : customMaterials) {
                            if(hauptlager.getOrDefault(cm.name, 0) >= 10 && w.material.getOrDefault(cm.name, 0) <= cm.warnSchwelle) {
                                hauptlager.put(cm.name, hauptlager.get(cm.name) - 10);
                                w.material.put(cm.name, w.material.get(cm.name) + 10);
                            }
                        }
                    }
                }

                for(Wache w : wachen) {
                    for(Personal p : w.personalPool) {
                        if (p.status.equals("Lehrgang") && p.lehrgangDauerSec > 0) {
                            p.lehrgangDauerSec -= speed;
                            if (p.lehrgangDauerSec <= 0) {
                                p.status = "Frei";
                                p.qualifikationen.add(p.lehrgangThema);
                                postfach.add(new Email("Leitstelle", "Lehrgang bestanden", "Mitarbeiter " + p.name + " ist zurueck und hat den Lehrgang ("+p.lehrgangThema+") erfolgreich beendet.", "Info", p, -1, -1));
                                p.lehrgangDauerSec = 0;
                            }
                        }
                    }
                    for(Fahrzeug f : w.fuhrpark) {
                        f.tick(speed, uhrzeit);
                    }
                }

                for (int i = aktiveEinsaetze.size() - 1; i >= 0; i--) {
                    Einsatz ein = aktiveEinsaetze.get(i);
                    ein.checkLagemeldung(speed, uhrzeit);
                    
                    if (ein.bereitZumLoeschen) {
                        boolean ressourcenDa = true;
                        if (!ein.reqMaterial.isEmpty()) {
                            for(Wache w : wachen) {
                                for(String m : ein.reqMaterial.keySet()) {
                                    if(w.material.getOrDefault(m, 0) >= ein.reqMaterial.get(m)) {
                                        w.material.put(m, w.material.get(m) - ein.reqMaterial.get(m));
                                    } else ressourcenDa = false;
                                }
                            }
                        }
                        
                        if (ressourcenDa || ein.reqMaterial.isEmpty()) {
                            xp += ein.xpBelohnung;
                            budget += ein.belohnungGeld;
                            tagesStatistik.add(ein);
                            checkLevelUp();
                            
                            if(aktuelleMission != null && !aktuelleMission.abgeschlossen) {
                                if(aktuelleMission.typ.equals("ALLE") || aktuelleMission.typ.equals(ein.vorlage.art)) {
                                    aktuelleMission.fortschritt++;
                                    if(aktuelleMission.fortschritt >= aktuelleMission.zielWert) {
                                        aktuelleMission.abgeschlossen = true;
                                        budget += aktuelleMission.belohnungGeld;
                                        xp += aktuelleMission.belohnungXp;
                                        postfach.add(0, new Email("Leitstelle", "Tagesmission erfuellt!", "Du hast die heutige Mission ('" + aktuelleMission.titel + "') geschafft!\nBelohnung: " + aktuelleMission.belohnungGeld + " EUR und " + aktuelleMission.belohnungXp + " XP.", "Info", null, -1, -1));
                                    }
                                }
                            }
                            
                            for(Wache w : wachen) {
                                for(Fahrzeug f : w.fuhrpark) {
                                    if (f.aktuellerEinsatz == ein) { 
                                        f.aktuellerEinsatz = null;
                                        
                                        if(f.typ.equals("RTW")) {
                                            if(calltakerStufe >= 2 && Math.random() > 0.5) {
                                                f.status = 8;
                                                f.anfahrtsZeit = 60; f.originalAnfahrt = 60;
                                            } else {
                                                f.status = 7; 
                                                playSound("status7.wav");
                                            }
                                        } else {
                                            f.status = 1; 
                                        }
                                        
                                        if (cfgBeschaedigung) {
                                            double baseChance = 0.05;
                                            if (f.kilometer >= f.naechsteInspektion) {
                                                baseChance += 0.15; 
                                            }
                                            double damageChance = baseChance * Math.pow(0.95, level - 1); 
                                            
                                            for(Wache wCheck : wachen) {
                                                for(Personal pCheck : wCheck.personalPool) {
                                                    if(pCheck.zugewiesenesFahrzeug.equals(f.funkrufname)) {
                                                        for(MitarbeiterEigenschaft eig : pCheck.eigenschaften) {
                                                            if(eig.typ.equals("TECHNIK")) damageChance *= eig.effektWert;
                                                        }
                                                    }
                                                }
                                            }
                                            if (Math.random() < damageChance) {
                                                f.status = 6; 
                                                f.ausfallGrund = "Beschadigung"; 
                                                playSound("status6.wav");
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            postfach.add(new Email("Leitstelle", "Materialmangel bei " + ein.vorlage.stichwort, "Der Einsatz konnte nicht sauber beendet werden. XP & Geld verfallen.", "Info", null, -1, -1));
                            for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if (f.aktuellerEinsatz == ein) { f.aktuellerEinsatz = null; f.status = 1; }
                        }
                        aktiveEinsaetze.remove(i);
                    }
                }
                
                double eventGlobalMutiplier = aktuellesEvent != null ? aktuellesEvent.globalRateMultiplier : 1.0;
                boolean leitstelleGeoeffnet = inGameSekunden < (19 * 3600);
                
                if (leitstelleGeoeffnet && aktuellerNotruf == null && inGameSekunden % (1200 / speed) == 0 && Math.random() < (0.6 * notrufRate * eventGlobalMutiplier)) {
                    generiereNotruf(uhrzeit);
                    
                    if (aktuellerNotruf != null) {
                        playSound("notruf.wav");
                    }
                    
                    if (calltakerStufe > 0 && aktuellerNotruf != null) {
                        int maxCalltakerLvl = (calltakerStufe == 1) ? 2 : 4; 
                        
                        if(aktuellerNotruf.vorlage.minLevel <= maxCalltakerLvl && !aktuellerNotruf.vorlage.hatNachforderung) {
                            String fzStr = ""; boolean hasVehicle = true;
                            if(aktuellerNotruf.vorlage.reqRTW > 0) { fzStr = "RTW"; if(getFreiesFahrzeug(fzStr) == null) hasVehicle = false; }
                            if(aktuellerNotruf.vorlage.reqKTW > 0) { 
                                if(getFreiesFahrzeug("KTW") != null) {
                                    fzStr = "KTW";
                                } else if(getFreiesFahrzeug("RTW") != null) {
                                    fzStr = "RTW"; 
                                } else {
                                    hasVehicle = false; 
                                }
                            }
                            if(aktuellerNotruf.vorlage.reqHLF > 0) { fzStr = "HLF"; if(getFreiesFahrzeug(fzStr) == null) hasVehicle = false; }
                            
                            boolean matsDa = true;
                            for(String m : aktuellerNotruf.reqMaterial.keySet()) {
                                boolean foundM = false;
                                for(Wache wc : wachen) if(wc.hatMaterial(m, aktuellerNotruf.reqMaterial.get(m))) foundM = true;
                                if(!foundM) matsDa = false;
                            }
                            
                            if (hasVehicle && matsDa && !fzStr.isEmpty()) {
                                Fahrzeug f = getFreiesFahrzeug(fzStr);
                                int bTime = 30;
                                for(Wache wc : wachen) for(Personal p : wc.personalPool) if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) bTime = 60;
                                int mult = isRushHour() ? 3 : 1;
                                
                                f.status = 3; f.aktuellerEinsatz = aktuellerNotruf; 
                                f.anfahrtsZeit = (int)(bTime * mult * getSpeedMultiplier(f));
                                aktuellerNotruf.xpBelohnung = 25 * aktuellerNotruf.vorlage.minLevel;
                                aktiveEinsaetze.add(aktuellerNotruf);
                                aktuellerNotruf = null;
                            }
                        }
                    }
                }

                if (btnLog != null) btnLog.setVisible(cfgLogistikAktiv); 
                uiAktualisieren(uhrzeit);
            }
        }).start();
        
        uiAktualisieren(getUhrzeit());
    }

    public static void beendenMitSpeichern() {
        setSpeed(0);
        int wahl = JOptionPane.showOptionDialog(frame, 
                "Moechtest du das Spiel vor dem Beenden speichern?", 
                "Spiel beenden", 
                JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                new Object[]{"Ja (Speichern & Beenden)", "Nein (Ohne Speichern beenden)", "Abbrechen"}, 
                "Ja (Speichern & Beenden)");

        if (wahl == JOptionPane.YES_OPTION) {
            SpeicherManager.speichern("savegame.properties");
            System.exit(0);
        } else if (wahl == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    public static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); 
        return btn;
    }

    public static void setSpeed(int s) {
        speed = s;
        btnPause.setBackground(s == 0 ? new Color(243, 156, 18) : new Color(60, 63, 65));
        btnPlay.setBackground(s == 1 ? new Color(39, 174, 96) : new Color(60, 63, 65));
        btnFastForward.setBackground(s == 3 ? new Color(243, 156, 18) : new Color(60, 63, 65));
    }

    public static String getUhrzeit() {
        int std = (inGameSekunden / 3600) % 24;
        int min = (inGameSekunden / 60) % 60;
        return String.format("%02d:%02d", std, min);
    }
    
    public static boolean isRushHour() {
        int std = (inGameSekunden / 3600) % 24;
        return (std >= 7 && std <= 9) || (std >= 16 && std <= 18);
    }
    
    public static LocalDate getCurrentDate() {
        return LocalDate.of(2026, 6, 1).plusDays(tag - 1);
    }
    
    public static String getDatumUndUhrzeit() {
        return getCurrentDate().format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", java.util.Locale.GERMAN));
    }
    
    public static String getDatumString(int tagNum) {
        return LocalDate.of(2026, 6, 1).plusDays(tagNum - 1).format(DateTimeFormatter.ofPattern("EEEE, 'dem' dd.MM.yyyy", java.util.Locale.GERMAN));
    }
    
    public static String getShortDatumString(int tagNum) {
        return LocalDate.of(2026, 6, 1).plusDays(tagNum - 1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static void uiAktualisieren(String zeit) {
        String rushHourText = isRushHour() ? " | RUSH-HOUR (Verkehr blockiert)" : "";
        String eventText = aktuellesEvent != null ? " | EVENT: " + aktuellesEvent.name : "";
        topUhrzeitLabel.setText(getDatumUndUhrzeit() + " " + zeit + " Uhr" + rushHourText + eventText + " | Leitstelle & Logistik");
        
        String missionText = "";
        if(aktuelleMission != null) {
            String status = aktuelleMission.abgeschlossen ? "(ERFUELLT!)" : "(" + aktuelleMission.fortschritt + "/" + aktuelleMission.zielWert + ")";
            missionText = "  |  Mission: " + aktuelleMission.titel + " " + status;
        }
        playerInfoLabel.setText("Level: " + level + " | XP: " + xp + " / " + getRequiredXpForLevel(level) + " | Budget: " + budget + " \u20AC" + missionText);
        
        long unread = postfach.stream().filter(m -> !m.gelesen).count();
        btnPostfach.setText("Postfach (" + unread + ")");
        if(unread > 0) btnPostfach.setBackground(new Color(230, 126, 34));
        else btnPostfach.setBackground(new Color(108, 122, 137));

        if (inGameSekunden >= 19*3600) {
            btnTagBeenden.setEnabled(true);
            btnTagBeenden.setBackground(new Color(211, 84, 0));
        } else {
            btnTagBeenden.setEnabled(false);
            btnTagBeenden.setBackground(new Color(100, 100, 100));
        }

        StringBuilder fms = new StringBuilder();
        fms.append("<html><body style='color:#a9b7c6; font-family:Consolas, sans-serif; font-size:12px; margin:5px;'>");
        
        boolean hasWarnings = false;
        StringBuilder warnings = new StringBuilder();
        
        if (cfgLogistikAktiv) {
            for(CustomMaterial cm : customMaterials) {
                int hLag = hauptlager.getOrDefault(cm.name, 0);
                if(hLag <= (cm.warnSchwelle * 5)) {
                    hasWarnings = true;
                    warnings.append("- Hauptlager: ").append(cm.name).append(" fast leer (").append(hLag).append(")<br>");
                }
            }
            
            for(Wache w : wachen) {
                for(CustomMaterial cm : customMaterials) {
                    int wLag = w.material.getOrDefault(cm.name, 0);
                    if(wLag <= cm.warnSchwelle) {
                        hasWarnings = true;
                        warnings.append("- ").append(w.name).append(": ").append(cm.name).append(" fast leer (").append(wLag).append(")<br>");
                    }
                }
            }
            
            if(hasWarnings) {
                fms.append("<div style='color:#e74c3c; margin-bottom:10px;'><b>MATERIAL-WARNUNGEN:</b><br>").append(warnings.toString()).append("</div>");
            }
        }

        for(Wache w : wachen) {
            fms.append("<b style='color:#ffffff;'>=== ").append(w.name).append(" (").append(w.kennung).append(") ===</b><br>");
            for(Fahrzeug f : w.fuhrpark) {
                String color = "#ffffff";
                if(f.status == 1) color = "#3498db";
                else if(f.status == 2) color = "#2ecc71";
                else if(f.status == 3 || f.status == 4) color = "#e67e22";
                else if(f.status == 6) color = "#e74c3c";
                else if(f.status == 7 || f.status == 8) color = "#9b59b6";
                
                fms.append("<span style='color:#ffffff;'>[").append(f.typ).append("]</span> <b>").append(f.funkrufname).append("</b> | Status: <b style='color:").append(color).append(";'>").append(f.status).append("</b> ");
                
                if(f.status == 3) fms.append("-> Anfahrt: ").append(f.anfahrtsZeit/speed).append("s");
                else if(f.status == 1 && f.anfahrtsZeit > 0) fms.append("-> Rueckfahrt: ").append(f.anfahrtsZeit/speed).append("s");
                else if(f.status == 8) fms.append("-> Auf dem Weg zur Klinik: ").append(f.anfahrtsZeit/speed).append("s");
                else if(f.status == 4 && f.aktuellerEinsatz != null) fms.append("-> <a href='FZG:").append(f.funkrufname).append("' style='color:#f1c40f; text-decoration:underline;'>Am Einsatzort (Akte oeffnen)</a>");
                else if(f.status == 7) fms.append("-> <a href='HOSP:").append(f.funkrufname).append("' style='color:#9b59b6; text-decoration:underline;'>Patient geladen (Zielklinik waehlen)</a>");
                else if(f.status == 6) {
                    fms.append("-> <span style='color:#e74c3c;'>GRUND: ").append(f.ausfallGrund).append("</span>");
                    if (f.ausfallGrund.equals("Personalwechsel")) {
                        fms.append(" (Wartezeit: ").append(f.reparaturDauer/speed).append("s)");
                    } else if (f.ausfallGrund.equals("Beschadigung")) {
                        fms.append(" (Wartet auf Werkstatt)");
                    } 
                    else if (f.ausfallGrund.equals("Wartet auf Reparatur") || f.ausfallGrund.equals("In Bearbeitung")) {
                        fms.append(" (In Werkstatt fuer ").append(f.reparaturDauer/speed).append(" Sec.)");
                    }
                }
                fms.append("<br>");
            }
            fms.append("<br>");
        }
        fms.append("</body></html>");
        fmsBoard.setText(fms.toString());

        if (aktuellerNotruf != null) {
            notrufPanel.setBackground(new Color(192, 57, 43));
            notrufLabel.setText("!!! NOTRUF: " + aktuellerNotruf.beschreibung + " !!!");
        } else {
            notrufPanel.setBackground(new Color(40, 40, 40));
            notrufLabel.setText("Kein Notruf anliegend.");
        }

        StringBuilder lage = new StringBuilder();
        if(inGameSekunden >= 19*3600) lage.append("Leitstelle geschlossen.\n\n");
        lage.append("=== LAUFENDE EINSAETZE ===\n");
        if (aktiveEinsaetze.isEmpty()) lage.append("Aktuell keine Einsaetze.\n");
        for (Einsatz e : aktiveEinsaetze) {
            lage.append("Einsatz: ").append(e.beschreibung).append(" (Alarm: ").append(e.alarmUhrzeit).append(")\n");
            lage.append(e.getLagemeldungText()).append("\n\n");
        }
        txtEinsatz.setText(lage.toString());
    }

    public static void generiereNotruf(String uhrzeit) {
        ArrayList<EinsatzVorlage> moegliche = new ArrayList<>();
        for (EinsatzVorlage v : vorlagenPool) {
            if (v.minLevel <= level) {
                if (v.art.equals("KTP") && !cfgKrankentransport) continue;
                
                int gewichtung = 1;
                if (aktuellesEvent != null) {
                    if (v.stichwort.startsWith("R1") || v.stichwort.startsWith("R2")) gewichtung = (int)(aktuellesEvent.chanceR1 * 10);
                    else if (v.stichwort.startsWith("H1")) gewichtung = (int)(aktuellesEvent.chanceH1 * 10);
                    else gewichtung = 10;
                }
                for(int i = 0; i < gewichtung; i++) {
                    moegliche.add(v);
                }
            }
        }
        if (!moegliche.isEmpty()) {
            EinsatzVorlage v = moegliche.get((int) (Math.random() * moegliche.size()));
            aktuellerNotruf = new Einsatz(v, uhrzeit);
        }
    }

    public static void disponiereNotruf() {
        if (aktuellerNotruf == null) return;
        
        int missingELW = aktuellerNotruf.vorlage.reqELW - sucheFahrzeuge("ELW", aktuellerNotruf.vorlage.reqELW, null);
        int missingHLF = aktuellerNotruf.vorlage.reqHLF - sucheFahrzeuge("HLF", aktuellerNotruf.vorlage.reqHLF, null);
        int missingDLK = aktuellerNotruf.vorlage.reqDLK - sucheFahrzeuge("DLK", aktuellerNotruf.vorlage.reqDLK, null);
        int missingNEF = aktuellerNotruf.vorlage.reqNEF - sucheFahrzeuge("NEF", aktuellerNotruf.vorlage.reqNEF, null);
        
        int reqKTW = aktuellerNotruf.vorlage.reqKTW;
        int reqRTW = aktuellerNotruf.vorlage.reqRTW;
        int foundKTW = sucheFahrzeuge("KTW", reqKTW, null);
        int missingKTW = reqKTW - foundKTW;
        
        int neededRTW = reqRTW + missingKTW; 
        int foundRTW = sucheFahrzeuge("RTW", neededRTW, null);
        int missingFinalRTW = neededRTW - foundRTW;
        
        int totalMissing = Math.max(0, missingELW) + Math.max(0, missingHLF) + Math.max(0, missingDLK) + Math.max(0, missingFinalRTW) + Math.max(0, missingNEF);

        boolean matsDa = true;
        if (cfgLogistikAktiv) {
            for(String m : aktuellerNotruf.reqMaterial.keySet()) {
                boolean found = false;
                for(Wache w : wachen) {
                    if(w.hatMaterial(m, aktuellerNotruf.reqMaterial.get(m))) found = true;
                }
                if(!found) matsDa = false;
            }
        }
        
        if (!matsDa) {
            JOptionPane.showMessageDialog(frame, "Nicht genug Material (" + aktuellerNotruf.reqMaterial.keySet().iterator().next() + ") auf den Wachen!", "Material fehlt", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ueberlandHilfeAktiv = false;
        if (totalMissing > 0) {
            int wahl = JOptionPane.showConfirmDialog(frame, "Dir fehlen " + totalMissing + " Fahrzeuge!\nSoll der Landkreis aushelfen? (" + (totalMissing*500) + " EURO)", "Ueberlandhilfe", JOptionPane.YES_NO_OPTION);
            if (wahl == JOptionPane.YES_OPTION) {
                if (budget >= (totalMissing*500)) {
                    budget -= (totalMissing*500);
                    ueberlandHilfeAktiv = true;
                } else {
                    JOptionPane.showMessageDialog(frame, "Zu wenig Geld!", "Fehler", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                return;
            }
        }
        
        ArrayList<Fahrzeug> fzListe = new ArrayList<>();
        sucheFahrzeuge("ELW", aktuellerNotruf.vorlage.reqELW, fzListe);
        sucheFahrzeuge("HLF", aktuellerNotruf.vorlage.reqHLF, fzListe);
        sucheFahrzeuge("DLK", aktuellerNotruf.vorlage.reqDLK, fzListe);
        sucheFahrzeuge("NEF", aktuellerNotruf.vorlage.reqNEF, fzListe);
        
        int assignedKTW = sucheFahrzeuge("KTW", reqKTW, fzListe);
        sucheFahrzeuge("RTW", reqRTW + (reqKTW - assignedKTW), fzListe); 
        
        int xpBel = 0;
        int multiplier = isRushHour() ? 3 : 1;
        
        for (Fahrzeug f : fzListe) {
            int baseTime = 30; 
            for(Wache wCheck : wachen) {
                for(Personal p : wCheck.personalPool) { 
                    if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) { 
                        baseTime = 60; break; 
                    } 
                }
            }
            f.status = 3; 
            f.anfahrtsZeit = (int)(baseTime * multiplier * getSpeedMultiplier(f)); 
            f.aktuellerEinsatz = aktuellerNotruf;
            xpBel += 25;
        }
        
        if (!fzListe.isEmpty() || ueberlandHilfeAktiv) {
            aktuellerNotruf.xpBelohnung = xpBel * aktuellerNotruf.vorlage.minLevel;
            aktiveEinsaetze.add(aktuellerNotruf);
        }
        
        aktuellerNotruf = null;
        uiAktualisieren(getUhrzeit());
    }

    public static int sucheFahrzeuge(String typ, int anzahl, ArrayList<Fahrzeug> liste) {
        if (anzahl <= 0) return 0;
        int fehlt = anzahl;
        for (Wache w : wachen) {
            for (Fahrzeug f : w.fuhrpark) {
                if (f.typ.equals(typ) && (f.status == 1 || f.status == 2)) {
                    if (liste != null) liste.add(f);
                    fehlt--;
                    if (fehlt == 0) return anzahl;
                }
            }
        }
        return anzahl - fehlt;
    }
    
    public static Fahrzeug getFreiesFahrzeug(String typ) {
        for (Wache w : wachen) {
            for (Fahrzeug f : w.fuhrpark) {
                if (f.typ.equals(typ) && (f.status == 1 || f.status == 2)) {
                    return f;
                }
            }
        }
        return null;
    }

    public static void kaufFahrzeug(Wache w, String typ, int preis) {
        if (budget >= preis) {
            budget -= preis;
            Fahrzeug f = new Fahrzeug(w.generiereFunkrufname(typ), typ);
            w.addFahrzeug(f);
            f.status = 6; f.ausfallGrund = "Personal fehlt";
            uiAktualisieren(getUhrzeit());
        } else {
            JOptionPane.showMessageDialog(frame, "Nicht genug Budget!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void fahrzeugeReparieren() {
        ArrayList<Fahrzeug> defekt = new ArrayList<>();
        for (Wache w : wachen) for (Fahrzeug f : w.fuhrpark) if (f.status == 6 && f.ausfallGrund.equals("Beschadigung")) defekt.add(f);
        if (defekt.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine beschadigten Fahrzeuge!"); return; }
        
        String[] namen = new String[defekt.size()]; for(int i=0; i<defekt.size(); i++) namen[i] = defekt.get(i).funkrufname;
        String wahl = (String) JOptionPane.showInputDialog(frame, "Welches Fahrzeug reparieren?", "Werkstatt", JOptionPane.QUESTION_MESSAGE, null, namen, namen[0]);
        
        if (wahl != null) {
            Fahrzeug targetF = null; Wache targetW = null;
            for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if(f.funkrufname.equals(wahl)) { targetF = f; targetW = w; break; }
            
            boolean hatLokaleWerkstatt = false;
            if(targetW != null && targetW.upgrades != null) {
                for(WachenAusbau wa : targetW.upgrades) if(wa.id.equals("werkstatt")) hatLokaleWerkstatt = true;
            }
            
            int kosten = hatLokaleWerkstatt ? 500 : 1000;
            if (budget >= kosten) {
                budget -= kosten;
                targetF.ausfallGrund = "Wartet auf Reparatur"; 
                targetF.reparaturDauer = 300; 
                uiAktualisieren(getUhrzeit());
            } else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget (" + kosten + " EUR)!"); }
        }
    }

    public static void behebeStatus6(String grund, int kosten, int dauer) {
        ArrayList<Fahrzeug> defekt = new ArrayList<>();
        for (Wache w : wachen) for (Fahrzeug f : w.fuhrpark) if (f.status == 6 && f.ausfallGrund.equals(grund)) defekt.add(f);
        if (defekt.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine passenden Fahrzeuge!"); return; }
        
        String[] namen = new String[defekt.size()]; for(int i=0; i<defekt.size(); i++) namen[i] = defekt.get(i).funkrufname;
        String wahl = (String) JOptionPane.showInputDialog(frame, "Welches Fahrzeug bearbeiten?", "Aktion", JOptionPane.QUESTION_MESSAGE, null, namen, namen[0]);
        
        if (wahl != null) {
            if (budget >= kosten) {
                budget -= kosten;
                for (Fahrzeug f : defekt) if (f.funkrufname.equals(wahl)) {
                    f.ausfallGrund = "In Bearbeitung"; 
                    f.reparaturDauer = dauer;
                    break;
                }
                uiAktualisieren(getUhrzeit());
            } else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget!"); }
        }
    }

    public static void leihkraftAnfordern() {
        if (budget >= 250) {
            boolean helped = false;
            for(Wache w : wachen) {
                for(Fahrzeug f : w.fuhrpark) {
                    if (f.status == 6 && f.ausfallGrund.equals("Personal fehlt")) {
                        ArrayList<String> reqs = getRequiredRoles(f);
                        for(Personal p : w.personalPool) {
                            if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && !p.status.equals("Krank") && !p.status.equals("Urlaub") && !p.status.equals("Lehrgang")) {
                                for(int i=0; i<reqs.size(); i++) {
                                    if(personErfuellt(p, reqs.get(i))) { reqs.remove(i); break; }
                                }
                            }
                        }
                        if(!reqs.isEmpty()) {
                            String missingRole = reqs.get(0);
                            budget -= 250;
                            Personal leih = new Personal("Leihkraft (" + missingRole + ")", missingRole);
                            leih.zugewiesenesFahrzeug = f.funkrufname; leih.geplantesFahrzeug = "Keines";
                            w.personalPool.add(leih); helped = true;
                            SpeicherManager.speichern("savegame.properties");
                            if(hatGenugPersonal(f)) { 
                                f.status = 6; 
                                f.ausfallGrund = "Personalwechsel"; 
                                f.reparaturDauer = 30; 
                            }
                            break;
                        }
                    }
                }
                if(helped) break;
            }
            if(!helped) JOptionPane.showMessageDialog(frame, "Aktuell fehlt keinem Fahrzeug Personal!");
        } else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget (250 EURO)!", "Fehler", JOptionPane.ERROR_MESSAGE); }
    }

    public static void personalEinstellen() {
        if(wachen.isEmpty()){ JOptionPane.showMessageDialog(frame, "Du hast noch keine Wache!"); return; }
        if(budget >= 500) {
            String[] wNamen = new String[wachen.size()];
            for(int i=0; i<wachen.size(); i++) wNamen[i] = wachen.get(i).name;
            String wahl = (String) JOptionPane.showInputDialog(frame, "Fuer welche Wache?", "Einstellen", JOptionPane.QUESTION_MESSAGE, null, wNamen, wNamen[0]);
            if(wahl != null) {
                Wache target = null; for(Wache w : wachen) if(w.name.equals(wahl)) target = w;
                
                String[] vornamen = {"Max", "Anna", "Lisa", "Paul", "Tom", "Julia", "Felix", "Marie", "Leon", "Lena", "Lukas", "Laura"};
                String[] nachnamen = {"Muller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz"};
                String neu = vornamen[(int)(Math.random()*vornamen.length)] + " " + nachnamen[(int)(Math.random()*nachnamen.length)];
                
                Personal potenziell = new Personal(neu, "Anwaerter");
                
                MitarbeiterEigenschaft[] pool = {
                    new MitarbeiterEigenschaft("Bleifuss", "Faehrt 15% schneller", "SPEED", 0.85),
                    new MitarbeiterEigenschaft("Vorsichtig", "Faehrt 15% langsamer", "SPEED", 1.15),
                    new MitarbeiterEigenschaft("Eisern", "Wird kaum krank", "GESUNDHEIT", 0.2),
                    new MitarbeiterEigenschaft("Anfaellig", "Wird haeufig krank", "GESUNDHEIT", 1.8),
                    new MitarbeiterEigenschaft("Mechaniker", "Fahrzeug geht seltener kaputt", "TECHNIK", 0.5)
                };
                
                int anzahlTraits = (Math.random() > 0.7) ? 2 : 1; 
                for(int i=0; i<anzahlTraits; i++) {
                    MitarbeiterEigenschaft gewaehlt = pool[(int)(Math.random() * pool.length)];
                    boolean hatSchon = false;
                    for(MitarbeiterEigenschaft e : potenziell.eigenschaften) if(e.name.equals(gewaehlt.name)) hatSchon = true;
                    if(!hatSchon) potenziell.eigenschaften.add(gewaehlt);
                }
                
                target.personalPool.add(potenziell);
                budget -= 500;
                
                if (Math.random() > 0.6) {
                    String[] vorwissen = {"TM", "RS", "GF"};
                    String w = vorwissen[(int)(Math.random() * vorwissen.length)];
                    postfach.add(MailGenerator.generiereVorwissen(potenziell, tag, w));
                }
                
                StringBuilder traitText = new StringBuilder();
                for(MitarbeiterEigenschaft e : potenziell.eigenschaften) traitText.append("\n- ").append(e.name).append(" (").append(e.beschreibung).append(")");
                
                JOptionPane.showMessageDialog(frame, neu + " wurde als Anwaerter auf " + target.name + " eingestellt!\n\nBesondere Eigenschaften:" + traitText.toString());
                SpeicherManager.speichern("savegame.properties"); uiAktualisieren(getUhrzeit());
            }
        } else { JOptionPane.showMessageDialog(frame, "Zu wenig Budget (500 EURO benoetigt)!", "Fehler", JOptionPane.ERROR_MESSAGE); }
    }

    public static void tagesWechsel() {
        setSpeed(0);
        int tagesXP = 0;
        for (Einsatz e : tagesStatistik) tagesXP += e.xpBelohnung;
        
        if (tagesStatistik.size() > 0 && abgelehnteEinsaetzeHeute == 0) {
            budget += 1000;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== TAGESABSCHLUSS TAG ").append(tag).append(" ===\n\n");
        
        if (!aktiveVertraege.isEmpty()) {
            sb.append("=== VERTRAGSABRECHNUNG ===\n");
            for (Vertrag v : aktiveVertraege) {
                if (v.aktuelleMenge >= v.zielMenge) {
                    budget += v.belohnungProTag;
                    sb.append("✅ ").append(v.auftraggeber).append(" erfuellt! (+").append(v.belohnungProTag).append(" EUR)\n");
                } else {
                    budget -= v.strafeBeiFehlschlag;
                    sb.append("❌ ").append(v.auftraggeber).append(" verfehlt! (-").append(v.strafeBeiFehlschlag).append(" EUR)\n");
                }
                v.aktuelleMenge = 0; 
            }
            sb.append("\n");
        }
        
        if (aktuellerKredit > 0) {
            if (budget >= taeglicheKreditRate) {
                budget -= taeglicheKreditRate;
                aktuellerKredit -= taeglicheKreditRate;
                sb.append("Kreditrate abgebucht: -").append(taeglicheKreditRate).append(" EUR\nRestschuld: ").append(Math.max(0, aktuellerKredit)).append(" EUR\n\n");
                if (aktuellerKredit <= 0) {
                    aktuellerKredit = 0; taeglicheKreditRate = 0;
                    postfach.add(new Email("Bank", "Kredit abbezahlt", "Ihr Kredit wurde erfolgreich und vollstaendig getilgt. Wir bedanken uns fuer die Zusammenarbeit.", "Info", null, -1, -1));
                }
            } else {
                aktuellerKredit += (taeglicheKreditRate / 2); 
                sb.append("⚠️ KREDITRATE KONNTE NICHT ABGEBUCHT WERDEN!\nStrafzinsen berechnet.\n\n");
            }
        }
        
        if (aktuellesEvent != null) {
            aktuellesEvent.dauerTage--;
            if (aktuellesEvent.dauerTage <= 0) {
                postfach.add(new Email("Leitstelle", "Event Beendet", "Das Event '" + aktuellesEvent.name + "' ist nun vorbei. Der Einsatzalltag normalisiert sich.", "Info", null, -1, -1));
                aktuellesEvent = null;
            }
        }
        
        if (aktuellesEvent == null && Math.random() < 0.10) {
            double r = Math.random();
            if (r < 0.3) {
                aktuellesEvent = new Event("Airbeat One Festival", "Das grosse Festival in Neustadt-Glewe startet! Erwarte sehr viele Krankentransporte (Alkohol/Drogen) in den naechsten Tagen.", 3, 5.0, 1.0, 1.5);
            } else if (r < 0.6) {
                aktuellesEvent = new Event("Fussball-Derby FC Mecklenburg", "Risikospiel im Stadion Lankow! Hohe Gefahr fuer koerperliche Auseinandersetzungen (R1) heute.", 1, 3.0, 1.0, 1.2);
            } else if (r < 0.8) {
                aktuellesEvent = new Event("Schweriner Stadtfest", "Die Innenstadt ist komplett voll. Mit erhoehten Einsatzzahlen fuer RD und FW ist zu rechnen.", 2, 2.0, 2.0, 1.3);
            } else {
                aktuellesEvent = new Event("Sturmtief 'Zoltan'", "Schwerer Sturm zieht ueber MV! Extrem viele Hilfeleistungen (H1, Baum auf Strasse) erwartet!", 1, 1.0, 8.0, 2.0);
            }
            postfach.add(new Email("Buergermeister", "Großereignis angekuendigt", "Achtung Leitstelle!\n\n" + aktuellesEvent.beschreibung + "\n\nDauer: " + aktuellesEvent.dauerTage + " Tag(e).", "Info", null, -1, -1));
        }

        if (tagesStatistik.isEmpty()) sb.append("Keine Einsaetze gefahren.\n");
        else {
            for (Einsatz e : tagesStatistik) sb.append(e.beschreibung).append(" (+").append(e.xpBelohnung).append(" XP)\n");
            sb.append("\nErreichte XP heute: ").append(tagesXP).append("\n");
            if(abgelehnteEinsaetzeHeute == 0) sb.append("=> BONUS: 1000 EURO (Alle Einsaetze bearbeitet!)\n");
        }
        sb.append("Gesamt XP: ").append(xp).append("\n");

        int altesLevel = level;
        checkLevelUp();
        if (level > altesLevel) sb.append("\n*** GLUECKWUNSCH! Du bist auf LEVEL ").append(level).append(" aufgestiegen! ***\n");
        
        tag++;
        LocalDate neuerTag = getCurrentDate();
        int dayIndex = neuerTag.getDayOfMonth() - 1; 
        
        if (neuerTag.getDayOfMonth() == 1) {
            for (Wache w : wachen) {
                for (Personal p : w.personalPool) {
                    System.arraycopy(p.planNaechsterMonat, 0, p.planAktuellerMonat, 0, 31);
                    for (int i = 0; i < 31; i++) p.planNaechsterMonat[i] = "Frei";
                }
            }
        }

        for(Wache w : wachen) {
            boolean hatLokalerRuheraum = false;
            if(w.upgrades != null) {
                for(WachenAusbau wa : w.upgrades) if(wa.id.equals("ruheraum")) hatLokalerRuheraum = true;
            }

            for (int i = w.personalPool.size() - 1; i >= 0; i--) {
                Personal p = w.personalPool.get(i);
                if (p.name.startsWith("Leihkraft")) { w.personalPool.remove(i); continue; }
                
                if (verliehenesPersonal.containsKey(p)) {
                    Wache heimat = verliehenesPersonal.get(p);
                    if (heimat != w) { 
                        w.personalPool.remove(i);
                        heimat.personalPool.add(p);
                        p.zugewiesenesFahrzeug = "Keines"; 
                    }
                    continue; 
                }
                
                if (p.status.equals("Bereit") || !p.zugewiesenesFahrzeug.equals("Keines")) p.schichtenMonat++;
                
                if (p.krankBis != -1 && tag >= p.krankBis) {
                    p.krankBis = -1; p.geplanterStatus = "Bereit";
                    postfach.add(new Email("Leitstelle", "Gesundmeldung", "Mitarbeiter " + p.name + " ist wieder gesund.", "Info", p, -1, -1));
                }
                if (p.urlaubEnd != -1 && tag >= p.urlaubEnd) {
                    p.urlaubStart = -1; p.urlaubEnd = -1; p.geplanterStatus = "Bereit";
                    postfach.add(new Email("Leitstelle", "Urlaub beendet", "Mitarbeiter " + p.name + " ist aus dem Urlaub zurueck.", "Info", p, -1, -1));
                }
                
                String planHeute = p.planAktuellerMonat[dayIndex];
                if (planHeute == null) planHeute = "Frei";
                
                if (p.krankBis != -1 && tag <= p.krankBis) planHeute = "Krank";
                if (p.urlaubStart != -1 && tag >= p.urlaubStart && tag <= p.urlaubEnd) planHeute = "Urlaub";
                if (p.status.equals("Lehrgang")) planHeute = "Lehrgang";

                if (planHeute.equals("Frei") || planHeute.equals("Krank") || planHeute.equals("Urlaub") || planHeute.equals("Lehrgang")) {
                    p.status = planHeute;
                    p.zugewiesenesFahrzeug = "Keines";
                } else if (planHeute.equals("Bereitschaft")) {
                    p.status = "Bereitschaft";
                    p.zugewiesenesFahrzeug = "Keines";
                } else {
                    p.status = "Bereit";
                    p.zugewiesenesFahrzeug = planHeute;
                }
                
                if (p.schichtenMonat > 15 && cfgKrankheit && p.krankBis == -1 && p.urlaubStart == -1 && !p.status.equals("Lehrgang")) {
                    double chance = hatLokalerRuheraum ? 0.05 : 0.10;
                    
                    for (MitarbeiterEigenschaft e : p.eigenschaften) {
                        if (e.typ.equals("GESUNDHEIT")) chance *= e.effektWert;
                    }
                    
                    if (Math.random() < chance) {
                        int dauer = 2 + (int)(Math.random() * 5);
                        p.krankBis = tag + dauer; 
                        postfach.add(MailGenerator.generiereKrankmeldung(p, tag + 1, tag + dauer));
                    }
                }
                
                if (Math.random() < 0.06 && p.urlaubStart == -1 && p.krankBis == -1 && !p.status.equals("Lehrgang")) {
                    int dauer = 5 + (int)(Math.random() * 10);
                    int startExtra = 2 + (int)(Math.random() * 5);
                    postfach.add(MailGenerator.generiereUrlaubsantrag(p, tag + startExtra, tag + startExtra + dauer));
                }
                
                if (p.qualifikationen.contains("Anwaerter") && p.schichtenMonat >= 1 && !p.praeferenzGesendet) {
                    p.praeferenzGesendet = true;
                    String praef = Math.random() > 0.5 ? "Feuerwehr" : "Rettungsdienst";
                    postfach.add(MailGenerator.generiereAnwaerterWahl(p, tag, praef));
                }
                
                if (p.schichtenMonat >= 5 && Math.random() < 0.05 && p.urlaubStart == -1 && p.krankBis == -1 && !p.status.equals("Lehrgang") && !p.qualifikationen.contains("Anwaerter")) {
                    String[] m = {"GF", "NFS", "NA", "EL"};
                    String reqLehrgang = m[(int)(Math.random() * m.length)];
                    if(!personErfuellt(p, reqLehrgang)) {
                        int cost = reqLehrgang.equals("NA") ? 3000 : (reqLehrgang.equals("NFS") ? 1500 : 1000);
                        postfach.add(MailGenerator.generiereLehrgangsAnfrage(p, tag, reqLehrgang, cost));
                    }
                }
            }
            
            for(Fahrzeug f : w.fuhrpark) {
                if (hatGenugPersonal(f)) {
                    if (hatGenugMaterial(f, w)) {
                        if (f.status == 6 && (f.ausfallGrund.equals("Personal fehlt") || f.ausfallGrund.equals("Personalwechsel") || f.ausfallGrund.equals("Material fehlt"))) {
                            f.status = 2; 
                            f.ausfallGrund = "";
                        }
                    } else {
                        if (f.status == 1 || f.status == 2) {
                            f.status = 6;
                            f.ausfallGrund = "Material fehlt";
                        }
                    }
                } else {
                    if (f.status == 1 || f.status == 2) {
                        f.status = 6;
                        f.ausfallGrund = "Personal fehlt";
                    }
                }
            } 
        } 

        verliehenesPersonal.clear(); 

        JOptionPane.showMessageDialog(frame, sb.toString(), "Feierabend!", JOptionPane.INFORMATION_MESSAGE);
        inGameSekunden = 7 * 3600;
        tagesStatistik.clear();
        abgelehnteEinsaetzeHeute = 0;
        SpeicherManager.speichern("savegame.properties");
        generiereTagesMission();
        uiAktualisieren(getUhrzeit());
    } 

    public static boolean hatGenugMaterial(Fahrzeug f, Wache w) {
        if (!cfgLogistikAktiv) return true;
        for(CustomMaterial cm : customMaterials) {
            if(cm.fahrzeuge.contains(f.typ)) {
                if(w.material.getOrDefault(cm.name, 0) < 5) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean hatGenugGeplantesPersonal() {
        LocalDate tomorrow = getCurrentDate().plusDays(1);
        int dayIndex = tomorrow.getDayOfMonth() - 1;
        
        for(Wache w : wachen) {
            for(Fahrzeug f : w.fuhrpark) {
                if(f.status == 1 || f.status == 2) {
                    ArrayList<String> reqs = getRequiredRoles(f);
                    ArrayList<Personal> avail = new ArrayList<>();
                    for(Personal p : w.personalPool) {
                        String planTomorrow = (tomorrow.getDayOfMonth() == 1) ? p.planNaechsterMonat[dayIndex] : p.planAktuellerMonat[dayIndex];
                        if (planTomorrow != null && planTomorrow.equals(f.funkrufname)) {
                            avail.add(p);
                        }
                    }
                    if(!canFill(reqs, avail)) return false;
                }
            }
        }
        return true;
    }

    public static void checkLevelUp() {
        int req = getRequiredXpForLevel(level);
        while (xp >= req) {
            xp -= req;
            level++;
            req = getRequiredXpForLevel(level);
        }
    }

    public static int getRequiredXpForLevel(int lvl) {
        return lvl * 750;
    }

    public static boolean canFill(java.util.List<String> reqs, java.util.List<Personal> available) {
        ArrayList<String> missing = new ArrayList<>(reqs);
        ArrayList<Personal> pool = new ArrayList<>(available);
        for(int i = 0; i < missing.size(); i++) {
            String r = missing.get(i);
            boolean found = false;
            for(int j = 0; j < pool.size(); j++) {
                if(personErfuellt(pool.get(j), r)) {
                    pool.remove(j);
                    missing.remove(i);
                    i--; 
                    found = true;
                    break;
                }
            }
        }
        return missing.isEmpty();
    }
    
    public static boolean hatGenugPersonal(Fahrzeug f) {
        ArrayList<String> reqs = getRequiredRoles(f);
        ArrayList<Personal> avail = new ArrayList<>();
        for(Wache w : wachen) {
            for(Personal p : w.personalPool) {
                if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && (p.status.equals("Bereit") || p.status.equals("Frei"))) {
                    avail.add(p);
                }
            }
        }
        return canFill(reqs, avail);
    }
    
    public static ArrayList<String> getRequiredRoles(Fahrzeug f) {
        ArrayList<String> r = new ArrayList<>();
        for(int i=0; i<f.reqEL; i++) r.add("EL");
        for(int i=0; i<f.reqGF; i++) r.add("GF");
        for(int i=0; i<f.reqNA; i++) r.add("NA");
        for(int i=0; i<f.reqNFS; i++) r.add("NFS");
        for(int i=0; i<f.reqMA; i++) r.add("MA");
        for(int i=0; i<f.reqTF; i++) r.add("TF");
        for(int i=0; i<f.reqFüAs; i++) r.add("FueAs");
        for(int i=0; i<f.reqRS; i++) r.add("RS");
        for(int i=0; i<f.reqTM; i++) r.add("TM");
        return r;
    }

    public static boolean personErfuellt(Personal p, String req) {
        if(p.qualifikationen.contains(req)) return true;
        if(req.equals("RS") && (p.qualifikationen.contains("NFS") || p.qualifikationen.contains("NA"))) return true;
        if(req.equals("NFS") && p.qualifikationen.contains("NA")) return true;
        if(req.equals("TM") && (p.qualifikationen.contains("TF") || p.qualifikationen.contains("GF") || p.qualifikationen.contains("EL"))) return true;
        if(req.equals("TF") && (p.qualifikationen.contains("GF") || p.qualifikationen.contains("EL"))) return true;
        if(req.equals("GF") && p.qualifikationen.contains("EL")) return true;
        return false;
    }

    public static int getMaxWachenErlaubt() {
        return 1 + (level / 5); 
    }

    public static void initStandardDaten() {
        budget = 25000; xp = 0; level = 1; tag = 1; inGameSekunden = 7 * 3600; abgelehnteEinsaetzeHeute = 0;
        techGrossabnehmer = false; lehrerStufe = 0; calltakerStufe = 0;
        techKlinikCrivitz = false; techKlinikLeezen = false; techKlinikHagenow = false;
        
        wachen.clear(); vorlagenPool.clear(); aktiveEinsaetze.clear(); tagesStatistik.clear(); hauptlager.clear(); lieferungen.clear(); postfach.clear(); customMaterials.clear();
        
        Wache w = new Wache("Wache Nord", "45");
        wachen.add(w);
        Fahrzeug f1 = new Fahrzeug(w.generiereFunkrufname("HLF"), "HLF"); w.addFahrzeug(f1);
        Fahrzeug f2 = new Fahrzeug(w.generiereFunkrufname("RTW"), "RTW"); w.addFahrzeug(f2);

        // Vorlagen mit den neuen Nullen (0, 0) fuer reqTLF und reqMTW
        vorlagenPool.add(new EinsatzVorlage("FW", "H1", "Tueröffnung", 0, 0, 0, 1, 0, 0, 0, 0, false, 0, "", 1));
        vorlagenPool.add(new EinsatzVorlage("RD", "R1", "Schnittverletzung", 1, 0, 0, 0, 0, 0, 0, 0, false, 0, "", 1));
        vorlagenPool.add(new EinsatzVorlage("FW", "F3", "BMA Einkaufszentrum", 0, 0, 0, 2, 1, 1, 0, 0, true, 30, "RTW", 3));
        vorlagenPool.add(new EinsatzVorlage("FW", "F2", "Wohnungsbrand", 1, 0, 0, 2, 1, 0, 0, 0, true, 50, "NEF", 2));
        vorlagenPool.add(new EinsatzVorlage("RD", "R2N1", "Verkehrsunfall (THL)", 2, 1, 0, 1, 0, 0, 0, 0, true, 20, "ELW & HLF", 4));
        vorlagenPool.add(new EinsatzVorlage("KTP", "KTP", "Krankentransport", 0, 0, 1, 0, 0, 0, 0, 0, false, 0, "", 1));
        vorlagenPool.add(new EinsatzVorlage("RD", "R1", "Atemnot", 1, 0, 0, 0, 0, 0, 0, 0, true, 40, "NEF", 1));

        customMaterials.add(new CustomMaterial("Verbandsmaterial", new ArrayList<>(java.util.Arrays.asList("RTW", "KTW", "HLF", "NEF")), 5, new ArrayList<>(), 500, 50, 10));
        customMaterials.add(new CustomMaterial("Medikamente", new ArrayList<>(java.util.Arrays.asList("RTW", "NEF")), 3, new ArrayList<>(), 1000, 20, 5));
        customMaterials.add(new CustomMaterial("Sauerstoff O²", new ArrayList<>(java.util.Arrays.asList("RTW", "KTW", "NEF", "HLF")), 1, new ArrayList<>(), 800, 10, 5));
        customMaterials.add(new CustomMaterial("PA-Gerät", new ArrayList<>(java.util.Arrays.asList("HLF", "DLK")), 2, new ArrayList<>(), 1500, 10, 5));
        customMaterials.add(new CustomMaterial("Schaummittel", new ArrayList<>(java.util.Arrays.asList("HLF")), 5, new ArrayList<>(), 2000, 5, 2));
        customMaterials.add(new CustomMaterial("Ölbindemittel", new ArrayList<>(java.util.Arrays.asList("HLF")), 2, new ArrayList<>(), 400, 20, 10));

        for(CustomMaterial cm : customMaterials) {
            hauptlager.put(cm.name, 100);
            w.material.put(cm.name, 50);
        }

        w.personalPool.add(new Personal("Adriano Quessel", "MA"));
        w.personalPool.add(new Personal("Fabian Stenzel", "GF"));
        w.personalPool.add(new Personal("Ian Wittka", "TF"));
        w.personalPool.add(new Personal("Lukas Muller", "TF"));
        w.personalPool.add(new Personal("Leon Schmidt", "TM"));
        w.personalPool.add(new Personal("Tyra-Jo Wittka", "TM"));
        w.personalPool.add(new Personal("Max Bauer", "NFS"));
        w.personalPool.add(new Personal("Anna Koch", "RS"));

        for(int i=0; i<6; i++) {
            w.personalPool.get(i).zugewiesenesFahrzeug = f1.funkrufname;
            w.personalPool.get(i).planAktuellerMonat[0] = f1.funkrufname;
        }
        for(int i=6; i<8; i++) {
            w.personalPool.get(i).zugewiesenesFahrzeug = f2.funkrufname;
            w.personalPool.get(i).planAktuellerMonat[0] = f2.funkrufname;
        }
        for(Personal p : w.personalPool) p.geplantesFahrzeug = "Keines";

        for(Fahrzeug f : w.fuhrpark) {
            if(!hatGenugPersonal(f)) { 
                f.status = 6; 
                f.ausfallGrund = "Personal fehlt"; 
            } else {
                f.status = 2; 
            }
        }
        generiereTagesMission();
    }

    public static void generiereTagesMission() {
        int rand = (int)(Math.random() * 4);
        if (rand == 0) {
            aktuelleMission = new TagesMission("Krankentransport-Meister", "Schliesse 3 KTP Einsaetze ab.", "KTP", 3, 1500, 300);
        } else if (rand == 1) {
            aktuelleMission = new TagesMission("Feuerwehr-Held", "Schliesse 2 Feuerwehreinsaetze (FW) ab.", "FW", 2, 2000, 400);
        } else if (rand == 2) {
            aktuelleMission = new TagesMission("Rettungsdienst-Profi", "Schliesse 3 Rettungsdiensteinsaetze (RD) ab.", "RD", 3, 1800, 350);
        } else {
            aktuelleMission = new TagesMission("Fleissiger Disponent", "Schliesse 5 beliebige Einsaetze ab.", "ALLE", 5, 2500, 500);
        }
    }
    
    public static void playSound(String dateiName) {
        if (dateiName.equals("notruf.wav") && !cfgSoundNotruf) return;
        if (dateiName.equals("status6.wav") && !cfgSoundStatus6) return;
        if (dateiName.equals("status7.wav") && !cfgSoundStatus7) return;

        new Thread(() -> {
            try {
                java.net.URL soundUrl = LogistikSimulator.class.getResource(dateiName);
                if (soundUrl != null) {
                    javax.sound.sampled.AudioInputStream audioInput = javax.sound.sampled.AudioSystem.getAudioInputStream(soundUrl);
                    javax.sound.sampled.Clip clip = javax.sound.sampled.AudioSystem.getClip();
                    clip.open(audioInput);
                    
                    if (clip.isControlSupported(javax.sound.sampled.FloatControl.Type.MASTER_GAIN)) {
                        javax.sound.sampled.FloatControl gainControl = (javax.sound.sampled.FloatControl) clip.getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN);
                        
                        int vol = 100;
                        if (dateiName.equals("notruf.wav")) vol = volNotruf;
                        else if (dateiName.equals("status6.wav")) vol = volStatus6;
                        else if (dateiName.equals("status7.wav")) vol = volStatus7;
                        
                        if (vol <= 0) {
                            gainControl.setValue(gainControl.getMinimum());
                        } else {
                            float db = (float) (20.0 * Math.log10(vol / 100.0));
                            db = Math.max(db, gainControl.getMinimum());
                            db = Math.min(db, gainControl.getMaximum());
                            gainControl.setValue(db);
                        }
                    }
                    
                    clip.start();
                }
            } catch (Exception ex) {}
        }).start();
    }

    public static void fahrzeugeInspektion() {
        ArrayList<Fahrzeug> faellig = new ArrayList<>();
        for (Wache w : wachen) {
            for (Fahrzeug f : w.fuhrpark) {
                if (f.kilometer >= f.naechsteInspektion && (f.status == 1 || f.status == 2)) {
                    faellig.add(f);
                }
            }
        }
        
        if (faellig.isEmpty()) { 
            JOptionPane.showMessageDialog(frame, "Aktuell muss kein freies Fahrzeug zur Inspektion!"); 
            return; 
        }
        
        String[] namen = new String[faellig.size()]; 
        for(int i=0; i<faellig.size(); i++) {
            namen[i] = faellig.get(i).funkrufname + " (" + faellig.get(i).kilometer + " km)";
        }
        
        String wahl = (String) JOptionPane.showInputDialog(frame, "Welches Fahrzeug soll zur Inspektion? (Kosten: 750 EUR, Dauer: 4 In-Game-Stunden)", "TÜV & Inspektion", JOptionPane.QUESTION_MESSAGE, null, namen, namen[0]);
        
        if (wahl != null) {
            Fahrzeug targetF = null; Wache targetW = null;
            String fName = wahl.split(" ")[0];
            for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if(f.funkrufname.equals(fName)) { targetF = f; targetW = w; break; }
            
            boolean hatLokaleWerkstatt = false;
            if(targetW != null && targetW.upgrades != null) {
                for(WachenAusbau wa : targetW.upgrades) if(wa.id.equals("werkstatt")) hatLokaleWerkstatt = true;
            }
            
            int kosten = hatLokaleWerkstatt ? 375 : 750;
            if (budget >= kosten) {
                budget -= kosten;
                targetF.status = 6; 
                targetF.ausfallGrund = "In Bearbeitung"; 
                targetF.reparaturDauer = 1440; 
                targetF.naechsteInspektion = targetF.kilometer + 1000; 
                uiAktualisieren(getUhrzeit());
            } else { 
                JOptionPane.showMessageDialog(frame, "Nicht genug Budget (" + kosten + " EUR)!"); 
            }
        }
    }
    
    public static double getSpeedMultiplier(Fahrzeug f) {
        double mult = 1.0;
        for(Wache w : wachen) {
            for(Personal p : w.personalPool) {
                if(p.zugewiesenesFahrzeug.equals(f.funkrufname)) {
                    for(MitarbeiterEigenschaft e : p.eigenschaften) {
                        if(e.typ.equals("SPEED")) mult *= e.effektWert;
                    }
                }
            }
        }
        return mult;
    }
}