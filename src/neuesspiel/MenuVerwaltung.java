package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static neuesspiel.LogistikSimulator.*;
import static neuesspiel.FensterManager.createFramelessDialog;
import static neuesspiel.FensterManager.assignHotkey;

public class MenuVerwaltung {

    public static void oeffneSystemHauptmenu() {
        JDialog d = createFramelessDialog("System & Editor", 400, 450); 
        JPanel content = new JPanel(new GridLayout(8, 1, 10, 10)); 
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35,35,35));

        JButton b1 = new JButton("Spieleinstellungen"); b1.addActionListener(e -> { d.dispose(); FensterManager.oeffneEinstellungen(); });
        JButton b2 = new JButton("Spiel Speichern"); b2.addActionListener(e -> { d.dispose(); SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(frame, "Spiel gespeichert!"); });
        
        JButton b3 = new JButton("Spiel Laden (Standard)"); 
        b3.addActionListener(e -> { 
            d.dispose(); 
            if(SpeicherManager.laden("savegame.properties")) { JOptionPane.showMessageDialog(frame, "Spielstand geladen!"); uiAktualisieren(getUhrzeit()); }
        });

        JButton b4 = new JButton("Einsatz-Vorlage erstellen"); b4.addActionListener(e -> { d.dispose(); FensterManager.oeffneEinsatzErsteller(); });
        JButton b5 = new JButton("Einsatz-Vorlage bearbeiten"); b5.addActionListener(e -> { d.dispose(); FensterManager.oeffneEinsatzBearbeiter(); });
        JButton b6 = new JButton("Material-Vorlage erstellen"); b6.addActionListener(e -> { d.dispose(); FensterManager.oeffneMaterialErsteller(); });
        JButton b7 = new JButton("Material-Vorlage bearbeiten"); b7.addActionListener(e -> { d.dispose(); FensterManager.oeffneMaterialBearbeiter(); });
        JButton btnVertragEditor = new JButton("Vertrags-Editor"); btnVertragEditor.addActionListener(e -> { d.dispose(); FensterManager.oeffneVertragsEditor(); });
        
        content.add(b1); content.add(b2); content.add(b3); 
        content.add(b4); content.add(b5); content.add(b6); content.add(b7); content.add(btnVertragEditor);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneEinstellungen() {
        JDialog d = createFramelessDialog("Spieleinstellungen & System", 550, 750);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(45, 45, 45));
        tabbedPane.setForeground(Color.WHITE);

        // ==========================================
        // TAB 1: HOTKEYS
        // ==========================================
        JPanel pnlKeys = new JPanel(new GridLayout(0, 2, 10, 10));
        pnlKeys.setBackground(new Color(35, 35, 35));
        pnlKeys.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        class HotkeyRow {
            void add(String text, int currentKey, java.util.function.IntConsumer setter) {
                JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                JButton btn = new JButton(java.awt.event.KeyEvent.getKeyText(currentKey));
                btn.setBackground(new Color(60, 60, 60)); 
                btn.setForeground(Color.WHITE); 
                btn.setFocusPainted(false);
                btn.addActionListener(e -> FensterManager.assignHotkey(btn, setter));
                
                pnlKeys.add(lbl); pnlKeys.add(btn);
            }
        }
        HotkeyRow row = new HotkeyRow();

        row.add("Spiel Pausieren:", LogistikSimulator.hotkeyPause, code -> LogistikSimulator.hotkeyPause = code);
        row.add("Normale Geschwindigkeit:", LogistikSimulator.hotkeyPlay, code -> LogistikSimulator.hotkeyPlay = code);
        row.add("Schneller Vorlauf:", LogistikSimulator.hotkeyFast, code -> LogistikSimulator.hotkeyFast = code);
        row.add("Notruf Disponieren:", LogistikSimulator.hotkeyDisp, code -> LogistikSimulator.hotkeyDisp = code);
        row.add("Schnellspeichern:", LogistikSimulator.hotkeySave, code -> LogistikSimulator.hotkeySave = code);
        pnlKeys.add(new JLabel(" ")); pnlKeys.add(new JLabel(" ")); // Lücke
        row.add("Dienstplan oeffnen:", LogistikSimulator.hotkeyDienstplan, code -> LogistikSimulator.hotkeyDienstplan = code);
        row.add("Postfach oeffnen:", LogistikSimulator.hotkeyPostfach, code -> LogistikSimulator.hotkeyPostfach = code);
        row.add("Fuhrpark oeffnen:", LogistikSimulator.hotkeyFuhrpark, code -> LogistikSimulator.hotkeyFuhrpark = code);
        row.add("Kalender oeffnen:", LogistikSimulator.hotkeyKalender, code -> LogistikSimulator.hotkeyKalender = code);
        row.add("Personalwesen:", LogistikSimulator.hotkeyPersonalEinstellen, code -> LogistikSimulator.hotkeyPersonalEinstellen = code);
        row.add("Lager & Logistik:", LogistikSimulator.hotkeyLogistik, code -> LogistikSimulator.hotkeyLogistik = code);
        row.add("Wachen & Gebaeude:", LogistikSimulator.hotkeyBauen, code -> LogistikSimulator.hotkeyBauen = code);
        row.add("Kliniken & Betten:", LogistikSimulator.hotkeyKlinik, code -> LogistikSimulator.hotkeyKlinik = code);
        row.add("Bank & Finanzen:", LogistikSimulator.hotkeyBank, code -> LogistikSimulator.hotkeyBank = code);
        row.add("System & Editor:", LogistikSimulator.hotkeySystem, code -> LogistikSimulator.hotkeySystem = code);
        row.add("Einsatz-Ersteller:", LogistikSimulator.hotkeyEinsatzErsteller, code -> LogistikSimulator.hotkeyEinsatzErsteller = code);
        row.add("Einsatz-Editor:", LogistikSimulator.hotkeyEinsatzEditor, code -> LogistikSimulator.hotkeyEinsatzEditor = code);

        JScrollPane scrollKeys = new JScrollPane(pnlKeys);
        scrollKeys.setBorder(BorderFactory.createEmptyBorder());
        scrollKeys.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Steuerung & Hotkeys", scrollKeys);

        // ==========================================
        // TAB 2: SYSTEM & GAMEPLAY
        // ==========================================
        JPanel pnlSystem = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlSystem.setBackground(new Color(35, 35, 35));
        pnlSystem.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JCheckBox cbWirtschaft = new JCheckBox("Wirtschaftssystem & Gehaelter aktivieren", LogistikSimulator.cfgWirtschaftsSystem);
        JCheckBox cbZufriedenheit = new JCheckBox("Personal-Zufriedenheit (Kuendigungen) aktivieren", LogistikSimulator.cfgZufriedenheit);
        JCheckBox cbLogistik = new JCheckBox("Lagerhaltung & Verbrauchsmaterial aktivieren", LogistikSimulator.cfgLogistikAktiv);
        JCheckBox cbKrank = new JCheckBox("Krankheitsfaelle beim Personal", LogistikSimulator.cfgKrankheit);
        JCheckBox cbKtp = new JCheckBox("Krankentransporte (KTP) generieren", LogistikSimulator.cfgKrankentransport);
        JCheckBox cbDefekt = new JCheckBox("Fahrzeug-Beschaedigungen aktivieren", LogistikSimulator.cfgBeschaedigung);
        JCheckBox cbFunk = new JCheckBox("KI Funkverkehr im Hintergrund", LogistikSimulator.cfgKiFunk);
        JCheckBox cbAutoLager = new JCheckBox("Autom. Wachenbelieferung (von Hauptlager)", LogistikSimulator.cfgAutoTransfer);
        
        JCheckBox cbSoundNotruf = new JCheckBox("Sound bei Notruf", LogistikSimulator.cfgSoundNotruf);
        JSlider slNotruf = new JSlider(0, 100, LogistikSimulator.volNotruf);
        slNotruf.setBackground(new Color(35, 35, 35)); slNotruf.setForeground(Color.WHITE);
        slNotruf.setMajorTickSpacing(25); slNotruf.setMinorTickSpacing(5); slNotruf.setPaintTicks(true);
        
        JCheckBox cbSoundStatus6 = new JCheckBox("Sound bei Defekt (Status 6)", LogistikSimulator.cfgSoundStatus6);
        JSlider slStatus6 = new JSlider(0, 100, LogistikSimulator.volStatus6);
        slStatus6.setBackground(new Color(35, 35, 35)); slStatus6.setForeground(Color.WHITE);
        slStatus6.setMajorTickSpacing(25); slStatus6.setMinorTickSpacing(5); slStatus6.setPaintTicks(true);
        
        JCheckBox cbSoundStatus7 = new JCheckBox("Sound bei Patient geladen (Status 7)", LogistikSimulator.cfgSoundStatus7);
        JSlider slStatus7 = new JSlider(0, 100, LogistikSimulator.volStatus7);
        slStatus7.setBackground(new Color(35, 35, 35)); slStatus7.setForeground(Color.WHITE);
        slStatus7.setMajorTickSpacing(25); slStatus7.setMinorTickSpacing(5); slStatus7.setPaintTicks(true);
        
        pnlSystem.add(new JLabel("Gameplay:")); pnlSystem.add(cbWirtschaft); pnlSystem.add(cbZufriedenheit); 
        pnlSystem.add(cbLogistik); pnlSystem.add(cbKrank); pnlSystem.add(cbKtp); pnlSystem.add(cbDefekt);
        pnlSystem.add(new JLabel(" ")); pnlSystem.add(new JLabel("Sonstiges:")); 
        pnlSystem.add(cbFunk); pnlSystem.add(cbAutoLager);
        pnlSystem.add(new JLabel(" ")); pnlSystem.add(new JLabel("Sounds & Lautstaerke:")); 
        pnlSystem.add(cbSoundNotruf); pnlSystem.add(slNotruf);
        pnlSystem.add(cbSoundStatus6); pnlSystem.add(slStatus6);
        pnlSystem.add(cbSoundStatus7); pnlSystem.add(slStatus7);

        JScrollPane scrollSys = new JScrollPane(pnlSystem);
        scrollSys.setBorder(BorderFactory.createEmptyBorder());
        scrollSys.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Spielregeln & Sounds", scrollSys);

        // ==========================================
        // TAB 3: DATENVERWALTUNG & EDITOREN
        // ==========================================
        JPanel pnlDaten = new JPanel(new GridLayout(0, 1, 5, 5));
        pnlDaten.setBackground(new Color(35, 35, 35));
        pnlDaten.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblSpeicher = new JLabel("Spielstandverwaltung:");
        lblSpeicher.setForeground(new Color(241, 196, 15)); // Gelb
        lblSpeicher.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlDaten.add(lblSpeicher);

        JButton btnSave = LogistikSimulator.createStyledButton("Spielstand Speichern", new Color(41, 128, 185));
        btnSave.addActionListener(e -> { SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, "Spiel gespeichert!"); });
        
        JButton btnLoad = LogistikSimulator.createStyledButton("Spielstand Laden", new Color(39, 174, 96));
        btnLoad.addActionListener(e -> { 
            if(SpeicherManager.laden("savegame.properties")) { 
                JOptionPane.showMessageDialog(d, "Spielstand geladen!"); 
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); 
                d.dispose();
            } 
        });

        JButton btnExportSave = LogistikSimulator.createStyledButton("Backup Exportieren (Ordner waehlen)", new Color(52, 73, 94));
        btnExportSave.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Spielstand exportieren / sichern");
            if (chooser.showSaveDialog(d) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                String path = file.getAbsolutePath();
                if (!path.endsWith(".properties")) path += ".properties";
                SpeicherManager.speichern(path);
                JOptionPane.showMessageDialog(d, "Backup erfolgreich exportiert unter:\n" + path);
            }
        });

        JButton btnImportSave = LogistikSimulator.createStyledButton("Spielstand Importieren (Datei waehlen)", new Color(142, 68, 173));
        btnImportSave.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Spielstand Datei auswaehlen");
            if (chooser.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                if (SpeicherManager.laden(file.getAbsolutePath())) {
                    SpeicherManager.speichern("savegame.properties"); // Überschreibt den lokalen Standard-Spielstand
                    JOptionPane.showMessageDialog(d, "Spielstand erfolgreich importiert und geladen!");
                    LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Fehler beim Laden der Datei!", "Import fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnReset = LogistikSimulator.createStyledButton("Spielstand Zuruecksetzen (ACHTUNG!)", new Color(192, 57, 43));
        btnReset.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(d, "Wirklich ALLES zuruecksetzen?\nDer aktuelle Spielstand geht unwiederbringlich verloren!", "Achtung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                LogistikSimulator.initStandardDaten();
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                JOptionPane.showMessageDialog(d, "Spiel wurde erfolgreich auf Werkseinstellungen zurueckgesetzt.");
                d.dispose();
            }
        });
        
        pnlDaten.add(btnSave); 
        pnlDaten.add(btnLoad); 
        pnlDaten.add(btnExportSave); 
        pnlDaten.add(btnImportSave); 
        pnlDaten.add(btnReset);

        pnlDaten.add(new JLabel(" ")); // Lücke

        // Block: Editoren
        JLabel lblEdit = new JLabel("Editoren & Vorlagen:");
        lblEdit.setForeground(new Color(241, 196, 15));
        lblEdit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlDaten.add(lblEdit);

        JButton b4 = LogistikSimulator.createStyledButton("Einsatz-Vorlage erstellen", new Color(142, 68, 173)); 
        b4.addActionListener(e -> { d.dispose(); FensterManager.oeffneEinsatzErsteller(); });
        JButton b5 = LogistikSimulator.createStyledButton("Einsatz-Vorlage bearbeiten", new Color(142, 68, 173)); 
        b5.addActionListener(e -> { d.dispose(); FensterManager.oeffneEinsatzBearbeiter(); });
        JButton b6 = LogistikSimulator.createStyledButton("Material-Vorlage erstellen", new Color(142, 68, 173)); 
        b6.addActionListener(e -> { d.dispose(); FensterManager.oeffneMaterialErsteller(); });
        JButton b7 = LogistikSimulator.createStyledButton("Material-Vorlage bearbeiten", new Color(142, 68, 173)); 
        b7.addActionListener(e -> { d.dispose(); FensterManager.oeffneMaterialBearbeiter(); });
        JButton b8 = LogistikSimulator.createStyledButton("Vertrags-Editor", new Color(142, 68, 173)); 
        b8.addActionListener(e -> { d.dispose(); FensterManager.oeffneVertragsEditor(); });
        pnlDaten.add(b4); pnlDaten.add(b5); pnlDaten.add(b6); pnlDaten.add(b7); pnlDaten.add(b8);

        pnlDaten.add(new JLabel(" ")); // Lücke

        // Block: Im- & Export
        JLabel lblExport = new JLabel("Im- & Export Einsaetze:");
        lblExport.setForeground(new Color(241, 196, 15));
        lblExport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlDaten.add(lblExport);

        JButton btnExport = LogistikSimulator.createStyledButton("Einsaetze exportieren (CSV)", new Color(243, 156, 18));
        btnExport.addActionListener(e -> { 
            ImportManager.speichereEinsaetzeInCSV(); 
            JOptionPane.showMessageDialog(d, "Einsaetze erfolgreich in CSV exportiert!"); 
        });
        
        JButton btnImport = LogistikSimulator.createStyledButton("Einsaetze importieren (CSV)", new Color(243, 156, 18));
btnImport.addActionListener(e -> { 
    // Ruft jetzt das Fenster zur Datei-Auswahl auf!
    ImportManager.importiereEinsaetzeUeberDialog(LogistikSimulator.frame); 
});
        pnlDaten.add(btnExport); pnlDaten.add(btnImport);

        JScrollPane scrollDaten = new JScrollPane(pnlDaten);
        scrollDaten.setBorder(BorderFactory.createEmptyBorder());
        scrollDaten.getVerticalScrollBar().setUnitIncrement(16);
        tabbedPane.addTab("Datenverwaltung & Editoren", scrollDaten);


        // ==========================================
        // SPEICHERN & SCHLIESSEN BUTTON
        // ==========================================
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBottom.setBackground(new Color(35, 35, 35));
        
        JButton btnClose = LogistikSimulator.createStyledButton("Speichern & Schliessen", new Color(39, 174, 96));
        btnClose.addActionListener(e -> {
            LogistikSimulator.cfgWirtschaftsSystem = cbWirtschaft.isSelected();
            LogistikSimulator.cfgZufriedenheit = cbZufriedenheit.isSelected();
            LogistikSimulator.cfgLogistikAktiv = cbLogistik.isSelected();
            LogistikSimulator.cfgKrankheit = cbKrank.isSelected();
            LogistikSimulator.cfgKrankentransport = cbKtp.isSelected();
            LogistikSimulator.cfgBeschaedigung = cbDefekt.isSelected();
            LogistikSimulator.cfgKiFunk = cbFunk.isSelected();
            LogistikSimulator.cfgAutoTransfer = cbAutoLager.isSelected();
            
            LogistikSimulator.cfgSoundNotruf = cbSoundNotruf.isSelected();
            LogistikSimulator.cfgSoundStatus6 = cbSoundStatus6.isSelected();
            LogistikSimulator.cfgSoundStatus7 = cbSoundStatus7.isSelected();
            
            LogistikSimulator.volNotruf = slNotruf.getValue();
            LogistikSimulator.volStatus6 = slStatus6.getValue();
            LogistikSimulator.volStatus7 = slStatus7.getValue();

            SpeicherManager.speichern("savegame.properties");
            LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
            d.dispose();
        });
        
        pnlBottom.add(btnClose);
        
        d.setLayout(new BorderLayout());
        d.add(tabbedPane, BorderLayout.CENTER);
        d.add(pnlBottom, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    public static void oeffneEinsatzErsteller() {
        JDialog d = createFramelessDialog("Einsatz-Vorlagen Ersteller", 600, 550);
        JPanel mainPanel = new JPanel(new GridLayout(12, 2, 5, 5)); mainPanel.setBackground(new Color(35, 35, 35)); mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> artBox = new JComboBox<>(new String[]{"FW", "RD", "KTP"}); JTextField stichwortField = new JTextField(); JTextField txtDesc = new JTextField();
        JSpinner sELW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sHLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sDLK = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sRTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sNEF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sKTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sTLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sMTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JCheckBox cbNach = new JCheckBox("Nachforderung moeglich?"); cbNach.setForeground(Color.WHITE); cbNach.setBackground(new Color(35,35,35));
        JSpinner sProb = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5)); JSpinner sMinLevel = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        JLabel l1 = new JLabel("Art:"); l1.setForeground(Color.WHITE); mainPanel.add(l1); mainPanel.add(artBox);
        JLabel l2 = new JLabel("Stichwort (z.B. F1, R1):"); l2.setForeground(Color.WHITE); mainPanel.add(l2); mainPanel.add(stichwortField);
        JLabel l3 = new JLabel("Beschreibung:"); l3.setForeground(Color.WHITE); mainPanel.add(l3); mainPanel.add(txtDesc);
        JLabel l4 = new JLabel("Benoetigte ELW:"); l4.setForeground(Color.WHITE); mainPanel.add(l4); mainPanel.add(sELW);
        JLabel l5 = new JLabel("Benoetigte HLF:"); l5.setForeground(Color.WHITE); mainPanel.add(l5); mainPanel.add(sHLF);
        JLabel l6 = new JLabel("Benoetigte DLK:"); l6.setForeground(Color.WHITE); mainPanel.add(l6); mainPanel.add(sDLK);
        JLabel l7 = new JLabel("Benoetigte RTW:"); l7.setForeground(Color.WHITE); mainPanel.add(l7); mainPanel.add(sRTW);
        JLabel l8 = new JLabel("Benoetigte NEF:"); l8.setForeground(Color.WHITE); mainPanel.add(l8); mainPanel.add(sNEF);
        JLabel l9 = new JLabel("Benoetigte KTW:"); l9.setForeground(Color.WHITE); mainPanel.add(l9); mainPanel.add(sKTW);
        JLabel lTLF = new JLabel("Benoetigte TLF:"); lTLF.setForeground(Color.WHITE); mainPanel.add(lTLF); mainPanel.add(sTLF); 
        JLabel lMTW = new JLabel("Benoetigte MTW:"); lMTW.setForeground(Color.WHITE); mainPanel.add(lMTW); mainPanel.add(sMTW);
        JLabel l10 = new JLabel("Spawnt ab Level:"); l10.setForeground(Color.WHITE); mainPanel.add(l10); mainPanel.add(sMinLevel);

        JPanel bottomForm = new JPanel(new GridLayout(2, 2, 5, 5)); bottomForm.setBackground(new Color(35, 35, 35));
        bottomForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Nachforderung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        bottomForm.add(cbNach); JLabel l11 = new JLabel("Wahrscheinlichkeit (%):"); l11.setForeground(Color.WHITE); bottomForm.add(l11); bottomForm.add(sProb);
        JLabel l12 = new JLabel("Welche Fahrzeuge:"); l12.setForeground(Color.WHITE); bottomForm.add(l12);
        
        JPanel pnlFahrzeuge = new JPanel(new GridLayout(3, 3)); pnlFahrzeuge.setBackground(new Color(35, 35, 35)); 
        JCheckBox cbN_RTW = new JCheckBox("RTW"); cbN_RTW.setForeground(Color.WHITE); cbN_RTW.setBackground(new Color(35,35,35));
        JCheckBox cbN_HLF = new JCheckBox("HLF"); cbN_HLF.setForeground(Color.WHITE); cbN_HLF.setBackground(new Color(35,35,35));
        JCheckBox cbN_NEF = new JCheckBox("NEF"); cbN_NEF.setForeground(Color.WHITE); cbN_NEF.setBackground(new Color(35,35,35));
        JCheckBox cbN_KTW = new JCheckBox("KTW"); cbN_KTW.setForeground(Color.WHITE); cbN_KTW.setBackground(new Color(35,35,35));
        JCheckBox cbN_ELW = new JCheckBox("ELW"); cbN_ELW.setForeground(Color.WHITE); cbN_ELW.setBackground(new Color(35,35,35));
        JCheckBox cbN_DLK = new JCheckBox("DLK"); cbN_DLK.setForeground(Color.WHITE); cbN_DLK.setBackground(new Color(35,35,35));
        JCheckBox cbN_TLF = new JCheckBox("TLF"); cbN_TLF.setForeground(Color.WHITE); cbN_TLF.setBackground(new Color(35,35,35)); 
        JCheckBox cbN_MTW = new JCheckBox("MTW"); cbN_MTW.setForeground(Color.WHITE); cbN_MTW.setBackground(new Color(35,35,35));
        pnlFahrzeuge.add(cbN_RTW); pnlFahrzeuge.add(cbN_HLF); pnlFahrzeuge.add(cbN_NEF); pnlFahrzeuge.add(cbN_KTW); pnlFahrzeuge.add(cbN_ELW); pnlFahrzeuge.add(cbN_DLK); pnlFahrzeuge.add(cbN_TLF); pnlFahrzeuge.add(cbN_MTW);
        bottomForm.add(pnlFahrzeuge); d.add(mainPanel, BorderLayout.NORTH); d.add(bottomForm, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Vorlage Speichern");
        btnAdd.addActionListener(e -> {
            String nTyp = "";
            if (cbN_RTW.isSelected()) nTyp += "RTW & "; if (cbN_HLF.isSelected()) nTyp += "HLF & ";
            if (cbN_NEF.isSelected()) nTyp += "NEF & "; if (cbN_KTW.isSelected()) nTyp += "KTW & ";
            if (cbN_ELW.isSelected()) nTyp += "ELW & "; if (cbN_DLK.isSelected()) nTyp += "DLK & ";
            if (cbN_TLF.isSelected()) nTyp += "TLF & "; if (cbN_MTW.isSelected()) nTyp += "MTW & ";
            if (!nTyp.isEmpty()) nTyp = nTyp.substring(0, nTyp.length() - 3);
            if(stichwortField.getText().trim().isEmpty() || txtDesc.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Bitte alle Felder ausfuellen!"); return; }

            EinsatzVorlage v = new EinsatzVorlage((String)artBox.getSelectedItem(), stichwortField.getText().replaceAll("[^a-zA-Z0-9 ]", ""), txtDesc.getText(), 
                    (int)sRTW.getValue(), (int)sNEF.getValue(), (int)sKTW.getValue(), (int)sHLF.getValue(), (int)sDLK.getValue(), (int)sELW.getValue(), (int)sTLF.getValue(), (int)sMTW.getValue(),
                    cbNach.isSelected(), (int)sProb.getValue(), nTyp, (int)sMinLevel.getValue());
            vorlagenPool.add(v);
            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Vorlage hinzugefuegt!");
            ImportManager.speichereEinsaetzeInCSV();
            d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneEinsatzBearbeiter() {
        if(vorlagenPool.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Einsatzvorlagen!"); return; }
        JDialog d = createFramelessDialog("Einsatz-Vorlagen Bearbeiten", 600, 600);

        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT)); topSelect.setBackground(new Color(35, 35, 35));
        JLabel lblTop = new JLabel("Vorlage waehlen:"); lblTop.setForeground(Color.WHITE); topSelect.add(lblTop);
        JComboBox<String> cWahl = new JComboBox<>(); for(EinsatzVorlage v : vorlagenPool) cWahl.addItem("[" + v.stichwort + "] " + v.beschreibung);
        topSelect.add(cWahl); d.add(topSelect, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(12, 2, 5, 5)); mainPanel.setBackground(new Color(35, 35, 35)); mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JComboBox<String> artBox = new JComboBox<>(new String[]{"FW", "RD", "KTP"}); JTextField stichwortField = new JTextField(); JTextField txtDesc = new JTextField();
        JSpinner sELW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sHLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sDLK = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sRTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sNEF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sKTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sTLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sMTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JCheckBox cbNach = new JCheckBox("Nachforderung moeglich?"); cbNach.setForeground(Color.WHITE); cbNach.setBackground(new Color(35,35,35));
        JSpinner sProb = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5)); JSpinner sMinLevel = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        JLabel l1 = new JLabel("Art:"); l1.setForeground(Color.WHITE); mainPanel.add(l1); mainPanel.add(artBox);
        JLabel l2 = new JLabel("Stichwort (z.B. F1, R1):"); l2.setForeground(Color.WHITE); mainPanel.add(l2); mainPanel.add(stichwortField);
        JLabel l3 = new JLabel("Beschreibung:"); l3.setForeground(Color.WHITE); mainPanel.add(l3); mainPanel.add(txtDesc);
        JLabel l4 = new JLabel("Benoetigte ELW:"); l4.setForeground(Color.WHITE); mainPanel.add(l4); mainPanel.add(sELW);
        JLabel l5 = new JLabel("Benoetigte HLF:"); l5.setForeground(Color.WHITE); mainPanel.add(l5); mainPanel.add(sHLF);
        JLabel l6 = new JLabel("Benoetigte DLK:"); l6.setForeground(Color.WHITE); mainPanel.add(l6); mainPanel.add(sDLK);
        JLabel l7 = new JLabel("Benoetigte RTW:"); l7.setForeground(Color.WHITE); mainPanel.add(l7); mainPanel.add(sRTW);
        JLabel l8 = new JLabel("Benoetigte NEF:"); l8.setForeground(Color.WHITE); mainPanel.add(l8); mainPanel.add(sNEF);
        JLabel l9 = new JLabel("Benoetigte KTW:"); l9.setForeground(Color.WHITE); mainPanel.add(l9); mainPanel.add(sKTW);
        JLabel lTLF = new JLabel("Benoetigte TLF:"); lTLF.setForeground(Color.WHITE); mainPanel.add(lTLF); mainPanel.add(sTLF);
        JLabel lMTW = new JLabel("Benoetigte MTW:"); lMTW.setForeground(Color.WHITE); mainPanel.add(lMTW); mainPanel.add(sMTW);
        JLabel l10 = new JLabel("Spawnt ab Level:"); l10.setForeground(Color.WHITE); mainPanel.add(l10); mainPanel.add(sMinLevel);

        JPanel bottomForm = new JPanel(new GridLayout(2, 2, 5, 5)); bottomForm.setBackground(new Color(35, 35, 35));
        bottomForm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Nachforderung", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        bottomForm.add(cbNach); JLabel l11 = new JLabel("Wahrscheinlichkeit (%):"); l11.setForeground(Color.WHITE); bottomForm.add(l11); bottomForm.add(sProb);
        JLabel l12 = new JLabel("Welche Fahrzeuge:"); l12.setForeground(Color.WHITE); bottomForm.add(l12);
        
        JPanel pnlFahrzeuge = new JPanel(new GridLayout(3, 3)); pnlFahrzeuge.setBackground(new Color(35, 35, 35));
        JCheckBox cbN_RTW = new JCheckBox("RTW"); cbN_RTW.setForeground(Color.WHITE); cbN_RTW.setBackground(new Color(35,35,35));
        JCheckBox cbN_HLF = new JCheckBox("HLF"); cbN_HLF.setForeground(Color.WHITE); cbN_HLF.setBackground(new Color(35,35,35));
        JCheckBox cbN_NEF = new JCheckBox("NEF"); cbN_NEF.setForeground(Color.WHITE); cbN_NEF.setBackground(new Color(35,35,35));
        JCheckBox cbN_KTW = new JCheckBox("KTW"); cbN_KTW.setForeground(Color.WHITE); cbN_KTW.setBackground(new Color(35,35,35));
        JCheckBox cbN_ELW = new JCheckBox("ELW"); cbN_ELW.setForeground(Color.WHITE); cbN_ELW.setBackground(new Color(35,35,35));
        JCheckBox cbN_DLK = new JCheckBox("DLK"); cbN_DLK.setForeground(Color.WHITE); cbN_DLK.setBackground(new Color(35,35,35));
        JCheckBox cbN_TLF = new JCheckBox("TLF"); cbN_TLF.setForeground(Color.WHITE); cbN_TLF.setBackground(new Color(35,35,35));
        JCheckBox cbN_MTW = new JCheckBox("MTW"); cbN_MTW.setForeground(Color.WHITE); cbN_MTW.setBackground(new Color(35,35,35));
        pnlFahrzeuge.add(cbN_RTW); pnlFahrzeuge.add(cbN_HLF); pnlFahrzeuge.add(cbN_NEF); pnlFahrzeuge.add(cbN_KTW); pnlFahrzeuge.add(cbN_ELW); pnlFahrzeuge.add(cbN_DLK); pnlFahrzeuge.add(cbN_TLF); pnlFahrzeuge.add(cbN_MTW);
        bottomForm.add(pnlFahrzeuge);

        JPanel centerPanel = new JPanel(new BorderLayout()); centerPanel.setBackground(new Color(35, 35, 35));
        centerPanel.add(mainPanel, BorderLayout.NORTH); centerPanel.add(bottomForm, BorderLayout.CENTER); d.add(centerPanel, BorderLayout.CENTER);

        Runnable ladeEinsatz = () -> {
            EinsatzVorlage v = vorlagenPool.get(cWahl.getSelectedIndex());
            artBox.setSelectedItem(v.art); stichwortField.setText(v.stichwort); txtDesc.setText(v.beschreibung);
            sELW.setValue(v.reqELW); sHLF.setValue(v.reqHLF); sDLK.setValue(v.reqDLK); sRTW.setValue(v.reqRTW); sNEF.setValue(v.reqNEF); sKTW.setValue(v.reqKTW); sTLF.setValue(v.reqTLF); sMTW.setValue(v.reqMTW); 
            cbNach.setSelected(v.hatNachforderung); sProb.setValue(v.nachforderungProzent); sMinLevel.setValue(v.minLevel);
            cbN_RTW.setSelected(v.nachforderungTyp.contains("RTW")); cbN_HLF.setSelected(v.nachforderungTyp.contains("HLF")); cbN_NEF.setSelected(v.nachforderungTyp.contains("NEF")); cbN_KTW.setSelected(v.nachforderungTyp.contains("KTW"));
            cbN_ELW.setSelected(v.nachforderungTyp.contains("ELW")); cbN_DLK.setSelected(v.nachforderungTyp.contains("DLK")); cbN_TLF.setSelected(v.nachforderungTyp.contains("TLF")); cbN_MTW.setSelected(v.nachforderungTyp.contains("MTW"));
        };
        ladeEinsatz.run(); cWahl.addActionListener(e -> ladeEinsatz.run());

        JButton btnAdd = new JButton("Aenderungen Speichern");
        btnAdd.addActionListener(e -> {
            String nTyp = "";
            if (cbN_RTW.isSelected()) nTyp += "RTW & "; if (cbN_HLF.isSelected()) nTyp += "HLF & "; if (cbN_NEF.isSelected()) nTyp += "NEF & "; if (cbN_KTW.isSelected()) nTyp += "KTW & ";
            if (cbN_ELW.isSelected()) nTyp += "ELW & "; if (cbN_DLK.isSelected()) nTyp += "DLK & "; if (cbN_TLF.isSelected()) nTyp += "TLF & "; if (cbN_MTW.isSelected()) nTyp += "MTW & ";
            if (!nTyp.isEmpty()) nTyp = nTyp.substring(0, nTyp.length() - 3);
            if(stichwortField.getText().trim().isEmpty() || txtDesc.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Bitte alle Felder ausfuellen!"); return; }

            EinsatzVorlage v = vorlagenPool.get(cWahl.getSelectedIndex());
            v.art = (String)artBox.getSelectedItem(); v.stichwort = stichwortField.getText().replaceAll("[^a-zA-Z0-9 ]", ""); v.beschreibung = txtDesc.getText();
            v.reqELW = (int)sELW.getValue(); v.reqHLF = (int)sHLF.getValue(); v.reqDLK = (int)sDLK.getValue(); v.reqRTW = (int)sRTW.getValue(); v.reqNEF = (int)sNEF.getValue(); v.reqKTW = (int)sKTW.getValue(); v.reqTLF = (int)sTLF.getValue(); v.reqMTW = (int)sMTW.getValue();
            v.hatNachforderung = cbNach.isSelected(); v.nachforderungProzent = (int)sProb.getValue(); v.nachforderungTyp = nTyp; v.minLevel = (int)sMinLevel.getValue();

            SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, "Vorlage aktualisiert!"); d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneBank() {
        JDialog d = createFramelessDialog("Bank & Vertraege", 550, 450); 
        JPanel content = new JPanel(new BorderLayout(10, 10)); content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

        JLabel lblInfo = new JLabel("Aktuelle Schulden: " + LogistikSimulator.aktuellerKredit + " EUR", SwingConstants.CENTER); lblInfo.setForeground(Color.WHITE); lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        content.add(lblInfo, BorderLayout.NORTH);

        JPanel centerPnl = new JPanel(new GridLayout(0, 1, 10, 10)); centerPnl.setBackground(new Color(35, 35, 35));

        JPanel gridKredite = new JPanel(new GridLayout(2, 2, 10, 10)); gridKredite.setBackground(new Color(35, 35, 35));
        JButton btnKredit1 = LogistikSimulator.createStyledButton("Kleinkredit (10.000)", new Color(41, 128, 185));
        JButton btnKredit2 = LogistikSimulator.createStyledButton("Mittelstand (50.000)", new Color(39, 174, 96));
        JButton btnKredit3 = LogistikSimulator.createStyledButton("Grosskredit (100.000)", new Color(192, 57, 43));
        JButton btnSondertilgung = LogistikSimulator.createStyledButton("Sofort-Tilgung", new Color(243, 156, 18));
        
        if (LogistikSimulator.level < 5) btnKredit1.setEnabled(false); if (LogistikSimulator.level < 15) btnKredit2.setEnabled(false); if (LogistikSimulator.level < 25) btnKredit3.setEnabled(false);
        if (LogistikSimulator.aktuellerKredit > 0) { btnKredit1.setEnabled(false); btnKredit2.setEnabled(false); btnKredit3.setEnabled(false); }

        java.awt.event.ActionListener listener = e -> {
            int betrag = e.getSource() == btnKredit1 ? 10000 : (e.getSource() == btnKredit2 ? 50000 : 100000);
            int rate = e.getSource() == btnKredit1 ? 500 : (e.getSource() == btnKredit2 ? 1500 : 2500);
            if(JOptionPane.showConfirmDialog(d, "Kredit aufnehmen?", "Bank", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                LogistikSimulator.budget += betrag; LogistikSimulator.aktuellerKredit = betrag + (betrag / 10); LogistikSimulator.taeglicheKreditRate = rate;
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose();
            }
        };
        btnKredit1.addActionListener(listener); btnKredit2.addActionListener(listener); btnKredit3.addActionListener(listener);
        
        btnSondertilgung.addActionListener(e -> {
             if (LogistikSimulator.budget >= LogistikSimulator.aktuellerKredit) {
                 LogistikSimulator.budget -= LogistikSimulator.aktuellerKredit; LogistikSimulator.aktuellerKredit = 0; LogistikSimulator.taeglicheKreditRate = 0;
                 LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose();
             }
        });
        
        gridKredite.add(btnKredit1); gridKredite.add(btnKredit2); gridKredite.add(btnKredit3); gridKredite.add(btnSondertilgung);
        centerPnl.add(gridKredite);

        JButton btnVertraege = LogistikSimulator.createStyledButton("Vertraege & Dauerauftraege", new Color(142, 68, 173));
        btnVertraege.addActionListener(e -> { d.dispose(); FensterManager.oeffneVertragsMenu(); });
        centerPnl.add(btnVertraege);

        if (LogistikSimulator.cfgWirtschaftsSystem) {
            JLabel lblGehalt = new JLabel("Unbezahlte Gehälter & Unterhalt: " + LogistikSimulator.offeneGehaelterUndKosten + " EUR", SwingConstants.CENTER);
            lblGehalt.setForeground(new Color(230, 126, 34)); lblGehalt.setFont(new Font("Segoe UI", Font.BOLD, 15));
            centerPnl.add(lblGehalt);

            JButton btnFreigabe = LogistikSimulator.createStyledButton("Gehälter & Kosten bezahlen", new Color(39, 174, 96));
            btnFreigabe.addActionListener(e -> {
                if (LogistikSimulator.budget >= LogistikSimulator.offeneGehaelterUndKosten) {
                    LogistikSimulator.budget -= LogistikSimulator.offeneGehaelterUndKosten;
                    JOptionPane.showMessageDialog(d, "Erfolgreich bezahlt!");
                    LogistikSimulator.offeneGehaelterUndKosten = 0; LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose();
                } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget vorhanden!", "Fehler", JOptionPane.ERROR_MESSAGE); }
            });
            centerPnl.add(btnFreigabe);
        }

        content.add(centerPnl, BorderLayout.CENTER); d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneVertragsMenu() {
        if(LogistikSimulator.vertragsVorlagen.isEmpty()) LogistikSimulator.vertragsVorlagen.add(new VertragVorlage("Klinikverbund", "Taegliche KTW Fahrten", "KTP", 3, 1500, 1000));
        String[] namen = new String[LogistikSimulator.vertragsVorlagen.size()];
        for(int i=0; i<LogistikSimulator.vertragsVorlagen.size(); i++) { VertragVorlage v = LogistikSimulator.vertragsVorlagen.get(i); namen[i] = v.auftraggeber + " (" + v.zielMenge + "x " + v.zielEinsatzArt + " / Tag)"; }
        String wahl = (String) JOptionPane.showInputDialog(LogistikSimulator.frame, "Verfuegbare Vertraege:\nAktive Vertraege: " + LogistikSimulator.aktiveVertraege.size(), "Vertragsverwaltung", JOptionPane.QUESTION_MESSAGE, null, namen, namen[0]);
        if (wahl != null) {
            for(VertragVorlage vv : LogistikSimulator.vertragsVorlagen) {
                if(wahl.startsWith(vv.auftraggeber)) {
                    LogistikSimulator.aktiveVertraege.add(new Vertrag(vv.auftraggeber, vv.beschreibung, vv.zielEinsatzArt, vv.zielMenge, vv.belohnungProTag, vv.strafeBeiFehlschlag));
                    JOptionPane.showMessageDialog(LogistikSimulator.frame, "Vertrag unterschrieben!"); break;
                }
            }
        }
    }

    public static void oeffneVertragsEditor() {
        JDialog d = createFramelessDialog("Vertrags-Verwaltung", 500, 400);
        DefaultListModel<String> listModel = new DefaultListModel<>();
        Runnable updateList = () -> { listModel.clear(); for (VertragVorlage v : LogistikSimulator.vertragsVorlagen) listModel.addElement(v.auftraggeber + " - " + v.zielMenge + "x " + v.zielEinsatzArt + " (" + v.belohnungProTag + " EUR)"); };
        updateList.run(); 
        
        JList<String> list = new JList<>(listModel); list.setBackground(new Color(43, 43, 43)); list.setForeground(Color.WHITE); list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel pnlBtns = new JPanel(new GridLayout(1, 4, 5, 5)); pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); pnlBtns.setBackground(new Color(35, 35, 35));
        
        JButton btnNeu = new JButton("Neu"); JButton btnEdit = new JButton("Bearbeiten"); JButton btnDel = new JButton("Loeschen"); JButton btnClose = new JButton("Schliessen");
        btnClose.addActionListener(e -> d.dispose());
        btnNeu.addActionListener(e -> { FensterManager.bearbeiteVertrag(null); updateList.run(); });
        btnEdit.addActionListener(e -> {
            int idx = list.getSelectedIndex(); if (idx != -1) { FensterManager.bearbeiteVertrag(LogistikSimulator.vertragsVorlagen.get(idx)); updateList.run(); } else { JOptionPane.showMessageDialog(d, "Bitte waehle zuerst einen Vertrag aus der Liste aus!"); }
        });
        btnDel.addActionListener(e -> { int idx = list.getSelectedIndex(); if (idx != -1) { LogistikSimulator.vertragsVorlagen.remove(idx); updateList.run(); } });
        
        pnlBtns.add(btnNeu); pnlBtns.add(btnEdit); pnlBtns.add(btnDel); pnlBtns.add(btnClose);
        d.add(new JScrollPane(list), BorderLayout.CENTER); d.add(pnlBtns, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneWachenAusbau() {
        JDialog d = createFramelessDialog("Wachen & Gebaeude", 650, 550);
        JPanel content = new JPanel(new BorderLayout(10, 10)); content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));
        
        JPanel pnlLokaleWache = new JPanel(new BorderLayout(5, 5)); pnlLokaleWache.setBackground(new Color(35, 35, 35)); pnlLokaleWache.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Lokale Wachen-Ausbauten", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        JPanel pnlWahl = new JPanel(new FlowLayout(FlowLayout.LEFT)); pnlWahl.setBackground(new Color(35, 35, 35)); JLabel lblWahl = new JLabel("Wache auswaehlen:"); lblWahl.setForeground(Color.WHITE);
        JComboBox<String> cbWachen = new JComboBox<>(); for (Wache w : wachen) cbWachen.addItem(w.name);
        pnlWahl.add(lblWahl); pnlWahl.add(cbWachen); pnlLokaleWache.add(pnlWahl, BorderLayout.NORTH);
        
        JPanel pnlUpgrades = new JPanel(new GridLayout(4, 1, 5, 5)); pnlUpgrades.setBackground(new Color(35, 35, 35));
        JButton btnStufe = new JButton(); btnStufe.setBackground(new Color(41, 128, 185)); btnStufe.setForeground(Color.WHITE);
        JButton btnWerkstatt = new JButton(); JButton btnRuheraum = new JButton(); JButton btnLogistik = new JButton();
        pnlUpgrades.add(btnStufe); pnlUpgrades.add(btnWerkstatt); pnlUpgrades.add(btnRuheraum); pnlUpgrades.add(btnLogistik);
        pnlLokaleWache.add(pnlUpgrades, BorderLayout.CENTER);
        
        Runnable updateLocalButtons = () -> {
            int wIndex = cbWachen.getSelectedIndex(); if (wIndex == -1) return; Wache target = wachen.get(wIndex);
            int stufe = target.stufe; int nextCost = stufe == 1 ? 10000 : (stufe == 2 ? 20000 : (stufe == 3 ? 50000 : 0));
            if(stufe < 4) { btnStufe.setText("Wache auf Stufe " + (stufe+1) + " ausbauen (" + nextCost + " EUR)"); btnStufe.setEnabled(true); } else { btnStufe.setText("Wache auf Maximalstufe (4) ausgebaut!"); btnStufe.setEnabled(false); }
            boolean hatW = false, hatR = false, hatL = false;
            if (target.upgrades != null) { for (WachenAusbau a : target.upgrades) { if (a.id.equals("werkstatt")) hatW = true; if (a.id.equals("ruheraum")) hatR = true; if (a.id.equals("logistik")) hatL = true; } }
            if (hatW) { btnWerkstatt.setText("Lokale Werkstatt (Gekauft)"); btnWerkstatt.setEnabled(false); } else if (stufe < 2) { btnWerkstatt.setText("Lokale Werkstatt (Ab Stufe 2)"); btnWerkstatt.setEnabled(false); } else { btnWerkstatt.setText("Lokale Werkstatt (10.000 EUR)"); btnWerkstatt.setEnabled(true); }
            if (hatR) { btnRuheraum.setText("Lokaler Ruheraum (Gekauft)"); btnRuheraum.setEnabled(false); } else if (stufe < 2) { btnRuheraum.setText("Lokaler Ruheraum (Ab Stufe 2)"); btnRuheraum.setEnabled(false); } else { btnRuheraum.setText("Lokaler Ruheraum (15.000 EUR)"); btnRuheraum.setEnabled(true); }
            if (hatL) { btnLogistik.setText("Logistik-Zentrum (Gekauft)"); btnLogistik.setEnabled(false); } else if (stufe < 3) { btnLogistik.setText("Logistik-Zentrum (Ab Stufe 3)"); btnLogistik.setEnabled(false); } else { btnLogistik.setText("Logistik-Zentrum (12.500 EUR)"); btnLogistik.setEnabled(true); }
        };
        updateLocalButtons.run(); cbWachen.addActionListener(e -> updateLocalButtons.run());
        
        btnStufe.addActionListener(e -> {
            Wache target = wachen.get(cbWachen.getSelectedIndex()); int nextCost = target.stufe == 1 ? 10000 : (target.stufe == 2 ? 20000 : 50000);
            if (budget >= nextCost) { budget -= nextCost; target.stufe++; SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); }
        });
        btnWerkstatt.addActionListener(e -> { if (budget >= 10000) { budget -= 10000; wachen.get(cbWachen.getSelectedIndex()).upgrades.add(new WachenAusbau("werkstatt", "Lokale Werkstatt", "Reparaturen 50% guenstiger", 10000)); SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); } });
        btnRuheraum.addActionListener(e -> { if (budget >= 15000) { budget -= 15000; wachen.get(cbWachen.getSelectedIndex()).upgrades.add(new WachenAusbau("ruheraum", "Lokaler Ruheraum", "Krankheitsrate sinkt", 15000)); SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); } });
        btnLogistik.addActionListener(e -> { if (budget >= 12500) { budget -= 12500; wachen.get(cbWachen.getSelectedIndex()).upgrades.add(new WachenAusbau("logistik", "Logistik-Zentrum", "Mehr Lagerplatz", 12500)); SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); } });
        
        JPanel pnlGlobal = new JPanel(new GridLayout(5, 1, 5, 5)); pnlGlobal.setBackground(new Color(35, 35, 35)); pnlGlobal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Zentrale (Leitstelle & Verwaltung)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        JButton btnGruenden = new JButton("Neue Wache gruenden (50.000 EURO) [" + wachen.size() + "/" + getMaxWachenErlaubt() + "]");
        btnGruenden.addActionListener(e -> {
            if(wachen.size() >= getMaxWachenErlaubt()) { JOptionPane.showMessageDialog(d, "Level zu niedrig fuer weitere Wache!"); return; }
            if(budget >= 50000) {
                String name = JOptionPane.showInputDialog(d, "Name der neuen Wache:");
                if(name != null && !name.trim().isEmpty()) { String kennung = JOptionPane.showInputDialog(d, "Zweinstellige Kennung (z.B. 46):"); if(kennung != null && kennung.length() == 2) { budget -= 50000; wachen.add(new Wache(name, kennung)); JOptionPane.showMessageDialog(d, "Wache erfolgreich gegruendet!"); d.dispose(); FensterManager.oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } }
            } else { JOptionPane.showMessageDialog(d, "Zu wenig Geld!"); }
        });
        pnlGlobal.add(btnGruenden);
        
        JButton b3 = new JButton(techGrossabnehmer ? "Grossabnehmer (Gekauft)" : "Grossabnehmer (Rabatt im Lager) (20.000 EURO)"); b3.setEnabled(!techGrossabnehmer); b3.addActionListener(e -> { if(budget >= 20000) { budget -= 20000; techGrossabnehmer = true; d.dispose(); uiAktualisieren(getUhrzeit()); } });
        pnlGlobal.add(b3);
        
        int nextLevelReq = (lehrerStufe + 1) * 2; JButton b4 = new JButton(lehrerStufe >= 5 ? "Lehrer Max. (Stufe 5)" : "Lehrer schulen Stufe " + (lehrerStufe+1) + " (5000 EURO, ab Lvl " + nextLevelReq + ")"); b4.setEnabled(lehrerStufe < 5 && level >= nextLevelReq); b4.addActionListener(e -> { if(budget >= 5000) { budget -= 5000; lehrerStufe++; d.dispose(); FensterManager.oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        pnlGlobal.add(b4);

        JButton b5 = new JButton(); if(calltakerStufe == 0) { b5.setText("Calltaker Einstellen (30.000 EURO, ab Lvl 20)"); b5.setEnabled(level >= 20); } else if(calltakerStufe == 1) { b5.setText("Calltaker Erweitern (20.000 EURO, ab Lvl 30)"); b5.setEnabled(level >= 30); } else { b5.setText("Calltaker Maximalstufe erreicht"); b5.setEnabled(false); }
        b5.addActionListener(e -> { int cost = calltakerStufe == 0 ? 30000 : 20000; if(budget >= cost) { budget -= cost; calltakerStufe++; d.dispose(); FensterManager.oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        pnlGlobal.add(b5);
        
        JPanel pnlKliniken = new JPanel(new GridLayout(1, 3, 5, 5)); pnlKliniken.setBackground(new Color(35,35,35));
        JButton btnCrivitz = new JButton(techKlinikCrivitz ? "Crivitz (Gekauft)" : "Klinik Crivitz (5000)"); btnCrivitz.setEnabled(!techKlinikCrivitz && level >= 10); btnCrivitz.addActionListener(e -> { if(budget >= 5000) { budget -= 5000; techKlinikCrivitz = true; d.dispose(); FensterManager.oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        JButton btnLeezen = new JButton(techKlinikLeezen ? "Leezen (Gekauft)" : "Klinik Leezen (10000)"); btnLeezen.setEnabled(!techKlinikLeezen && level >= 20); btnLeezen.addActionListener(e -> { if(budget >= 10000) { budget -= 10000; techKlinikLeezen = true; d.dispose(); FensterManager.oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        JButton btnHagenow = new JButton(techKlinikHagenow ? "Hagenow (Gekauft)" : "Klinik Hagenow (20000)"); btnHagenow.setEnabled(!techKlinikHagenow && level >= 30); btnHagenow.addActionListener(e -> { if(budget >= 20000) { budget -= 20000; techKlinikHagenow = true; d.dispose(); FensterManager.oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        pnlKliniken.add(btnCrivitz); pnlKliniken.add(btnLeezen); pnlKliniken.add(btnHagenow); pnlGlobal.add(pnlKliniken);

        content.add(pnlLokaleWache, BorderLayout.NORTH); content.add(pnlGlobal, BorderLayout.CENTER); d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffnePostfach() {
        JDialog d = createFramelessDialog("E-Mail Postfach & Antraege", 1000, 600); 
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(35, 35, 35));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = postfach.size() - 1; i >= 0; i--) { 
            Email e = postfach.get(i); 
            String prefix = e.gelesen ? "[Gelesen] " : "[NEU] "; 
            String typ = e.typ.equals("Info") ? "[Info]" : "[" + e.typ + "]"; 
            listModel.addElement(prefix + typ + " " + e.betreff + " - von: " + e.absender); 
        }

        // --- MODERNES DESIGN FÜR DIE LISTEN ---
        JList<String> list = new JList<>(listModel); 
        list.setBackground(new Color(43, 43, 43)); 
        list.setForeground(Color.WHITE);
        list.setFont(new Font("Segoe UI", Font.BOLD, 13));
        list.setSelectionBackground(new Color(52, 152, 219));
        list.setSelectionForeground(Color.WHITE);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Macht die Einträge höher
                if (value.toString().contains("[NEU]")) label.setForeground(new Color(241, 196, 15)); // Neue Mails in Gelb
                return label;
            }
        });

        JTextArea txt = new JTextArea("Bitte waehle eine E-Mail aus der Liste links aus."); 
        txt.setEditable(false); 
        txt.setBackground(new Color(30, 30, 30)); 
        txt.setForeground(Color.WHITE); 
        txt.setMargin(new Insets(20, 20, 20, 20)); 
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setLineWrap(true); 
        txt.setWrapStyleWord(true);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15)); 
        pnlBtns.setBackground(new Color(25, 25, 25));
        
        // --- BUTTONS IM FLAT-DESIGN ---
        JButton btnGenehmigen = LogistikSimulator.createStyledButton("Antrag Genehmigen", new Color(39, 174, 96)); 
        JButton btnAblehnen = LogistikSimulator.createStyledButton("Antrag Ablehnen", new Color(192, 57, 43)); 
        JButton btnTM = LogistikSimulator.createStyledButton("Als TM ausbilden", new Color(41, 128, 185)); 
        JButton btnRS = LogistikSimulator.createStyledButton("Als RS ausbilden", new Color(41, 128, 185)); 
        JButton btnLehrgang = LogistikSimulator.createStyledButton("Lehrgang Bezahlen", new Color(39, 174, 96)); 
        JButton btnAnerkennen = LogistikSimulator.createStyledButton("Vorwissen Anerkennen", new Color(39, 174, 96)); 
        
        // --- NEUER KNOPF FÜR DEN DIENSTAUSWEIS ---
        JButton btnAusweis = LogistikSimulator.createStyledButton("Ausweis erstellen", new Color(142, 68, 173));

        JButton btnLoeschen = LogistikSimulator.createStyledButton("Mail Loeschen", new Color(100, 100, 100));

        btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false); btnAusweis.setVisible(false);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
                int idx = postfach.size() - 1 - list.getSelectedIndex(); 
                Email mail = postfach.get(idx); 
                mail.gelesen = true;
                
                String prefix = "[Gelesen] "; 
                String typStr = mail.typ.equals("Info") ? "[Info]" : "[" + mail.typ + "]";
                listModel.set(list.getSelectedIndex(), prefix + typStr + " " + mail.betreff + " - von: " + mail.absender);
                
                txt.setText("Von: " + mail.absender + "\nBetreff: " + mail.betreff + "\n-----------------------------------------------------------\n\n" + mail.text);
                
                btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false); btnAusweis.setVisible(false);
                
                if (mail.typ.equals("Urlaub") || mail.typ.equals("Wunschfrei") || mail.typ.equals("Urlaubsverlaengerung")) { 
                    btnGenehmigen.setVisible(true); btnAblehnen.setVisible(true); 
                } 
                else if (mail.typ.equals("Dienstausweis")) {
                    btnAusweis.setVisible(true); 
                }
                else if (mail.typ.equals("Anwaerter")) { btnTM.setVisible(true); btnRS.setVisible(true); } 
                else if (mail.typ.equals("Lehrgang_Anfrage")) { btnLehrgang.setVisible(true); btnAblehnen.setVisible(true); } 
                else if (mail.typ.equals("Vorwissen")) { btnAnerkennen.setVisible(true); btnAblehnen.setVisible(true); } 
                else if (mail.typ.equals("Gehaltsverhandlung")) {
                    d.dispose(); 
                    try { 
                        double forderung = Double.parseDouble(mail.text); 
                        FensterManager.zeigeGehaltsVerhandlung(mail.person, forderung, mail); 
                    } catch(Exception ex) {}
                }
            }
        });

        // --- AKTION FÜR DEN DIENSTAUSWEIS ---
        btnAusweis.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex(); 
            Email mail = postfach.get(idx);
            if (mail.person != null) {
                DienstausweisGenerator.oeffneAusweisErsteller(mail.person, mail, d);
            }
        });

        btnAnerkennen.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex(); Email mail = postfach.get(idx);
            if (mail.person != null) { String[] parts = mail.text.split("##"); if(parts.length > 1) { String vorhandenerLehrgang = parts[1].trim(); if(!mail.person.qualifikationen.contains(vorhandenerLehrgang)) mail.person.qualifikationen.add(vorhandenerLehrgang); } mail.typ = "Info"; mail.betreff = "[Anerkannt] " + mail.betreff; JOptionPane.showMessageDialog(d, "Vorwissen anerkannt!"); uiAktualisieren(getUhrzeit()); d.dispose(); FensterManager.oeffnePostfach(); }
        });

        btnLehrgang.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex(); Email mail = postfach.get(idx);
            if (mail.person != null) { String[] parts = mail.text.split("##"); if(parts.length > 2) { String wunschLehrgang = parts[1].trim(); int kosten = Integer.parseInt(parts[2].trim()); if (budget >= kosten) { budget -= kosten; mail.person.status = "Lehrgang"; mail.person.geplanterStatus = "Lehrgang"; mail.person.lehrgangDauerSec = 3 * 60; mail.person.lehrgangThema = wunschLehrgang; mail.typ = "Info"; mail.betreff = "[Bezahlt] " + mail.betreff; JOptionPane.showMessageDialog(d, "Lehrgang bezahlt! Mitarbeiter ist unterwegs."); uiAktualisieren(getUhrzeit()); d.dispose(); FensterManager.oeffnePostfach(); } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget (" + kosten + "EURO benoetigt)!"); } } }
        });

        java.awt.event.ActionListener anwaerterAction = e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex(); Email mail = postfach.get(idx); String role = ((JButton)e.getSource()).getText().contains("TM") ? "TM" : "RS";
            if (mail.person != null) { mail.person.qualifikationen.remove("Anwaerter"); mail.person.qualifikationen.add(role); mail.typ = "Info"; mail.betreff = "[Uebernommen: " + role + "] " + mail.betreff; JOptionPane.showMessageDialog(d, mail.person.name + " ist nun voll ausgebildeter " + role + "!"); uiAktualisieren(getUhrzeit()); d.dispose(); FensterManager.oeffnePostfach(); }
        };
        btnTM.addActionListener(anwaerterAction); btnRS.addActionListener(anwaerterAction);

        btnGenehmigen.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex(); Email mail = postfach.get(idx);
            if (mail.person != null) { 
                mail.person.urlaubStart = mail.startTag; 
                mail.person.urlaubEnd = mail.endTag; 
                mail.person.geplanterStatus = "Bereit";
                
                String eintragsTyp = mail.typ.equals("Wunschfrei") ? "Frei" : "Urlaub";
                
                for (int t = mail.startTag; t <= mail.endTag; t++) { 
                    java.time.LocalDate date = java.time.LocalDate.of(2026, 6, 1).plusDays(t - 1); 
                    java.time.LocalDate heute = LogistikSimulator.getCurrentDate(); 
                    int dIndex = date.getDayOfMonth() - 1; 
                    
                    if (date.getMonthValue() == heute.getMonthValue() && date.getYear() == heute.getYear()) { 
                        mail.person.planAktuellerMonat[dIndex] = eintragsTyp; 
                    } else { 
                        mail.person.planNaechsterMonat[dIndex] = eintragsTyp; 
                    } 
                    
                    int kalenderCol = t - LogistikSimulator.tag;
                    if (kalenderCol >= 0 && kalenderCol < 62) {
                        for(int slot = 0; slot < 25; slot++) {
                            Terminkalender.kalenderDaten[kalenderCol][slot] = eintragsTyp + " (" + mail.person.name + ")";
                        }
                    }
                }
                
                mail.typ = "Info"; 
                mail.betreff = "[Genehmigt] " + mail.betreff; 
                JOptionPane.showMessageDialog(d, eintragsTyp + " eingetragen und im Dienstplan & Kalender geblockt!"); 
                uiAktualisieren(getUhrzeit()); 
                d.dispose(); 
                FensterManager.oeffnePostfach(); 
            }
        });

        btnAblehnen.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if(idx != -1) { 
                Email mail = postfach.get(postfach.size() - 1 - idx); 
                
                if (mail.typ.equals("Urlaub") && cfgKrankheit) { 
                    if (Math.random() < 0.05) { 
                        int dauer = 2 + (int)(Math.random() * 4); 
                        mail.person.krankBis = tag + dauer; 
                        if (tag == mail.person.krankBis - dauer) { 
                            mail.person.status = "Krank"; mail.person.zugewiesenesFahrzeug = "Keines"; 
                        } 
                        for (int t = tag + 1; t <= tag + dauer; t++) { 
                            java.time.LocalDate date = java.time.LocalDate.of(2026, 6, 1).plusDays(t - 1); 
                            java.time.LocalDate heute = LogistikSimulator.getCurrentDate(); 
                            int dIndex = date.getDayOfMonth() - 1; 
                            if (date.getMonthValue() == heute.getMonthValue() && date.getYear() == heute.getYear()) { 
                                mail.person.planAktuellerMonat[dIndex] = "Krank"; 
                            } else { 
                                mail.person.planNaechsterMonat[dIndex] = "Krank"; 
                            } 
                        } 
                        postfach.add(0, MailGenerator.generiereKrankmeldung(mail.person, tag + 1, tag + dauer)); 
                        JOptionPane.showMessageDialog(d, "Urlaub abgelehnt. Die Laune ist im Keller...", "Info", JOptionPane.INFORMATION_MESSAGE); 
                    } else {
                        JOptionPane.showMessageDialog(d, "Antrag abgelehnt.");
                    }
                } else if (mail.typ.equals("Wunschfrei")) {
                     JOptionPane.showMessageDialog(d, "Wunschfrei verwehrt. Mitarbeiter muss planmaessig erscheinen.");
                }
                
                mail.typ = "Info"; 
                listModel.set(idx, "[Gelesen] [Abgelehnt] " + mail.betreff + " - von: " + mail.absender); 
                btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false); btnAusweis.setVisible(false);
                uiAktualisieren(getUhrzeit()); 
            }
        });

        btnLoeschen.addActionListener(e -> { 
            int selectedIndex = list.getSelectedIndex(); 
            if (selectedIndex != -1) { 
                int echterIndex = LogistikSimulator.postfach.size() - 1 - selectedIndex; 
                LogistikSimulator.postfach.remove(echterIndex); 
                ((DefaultListModel) list.getModel()).remove(selectedIndex); 
                txt.setText("Bitte waehle eine E-Mail aus der Liste links aus."); 
                btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false); btnAusweis.setVisible(false);
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); 
            } 
        });

        JButton btnClose = LogistikSimulator.createStyledButton("Schliessen", new Color(192, 57, 43)); 
        btnClose.addActionListener(e -> d.dispose());
        
        JButton btnKalenderPostfach = LogistikSimulator.createStyledButton("Kalender oeffnen", new Color(142, 68, 173)); 
        btnKalenderPostfach.addActionListener(e -> { d.dispose(); Terminkalender.oeffneKalender(); });
        
        pnlBtns.add(btnGenehmigen); pnlBtns.add(btnTM); pnlBtns.add(btnRS); pnlBtns.add(btnLehrgang); pnlBtns.add(btnAnerkennen); pnlBtns.add(btnAusweis); pnlBtns.add(btnAblehnen); pnlBtns.add(btnLoeschen); pnlBtns.add(btnKalenderPostfach); pnlBtns.add(btnClose);
        
        d.add(pnlBtns, BorderLayout.SOUTH); 
        
        JScrollPane scrollList = new JScrollPane(list);
        scrollList.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollTxt = new JScrollPane(txt);
        scrollTxt.setBorder(BorderFactory.createEmptyBorder());
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollList, scrollTxt); 
        splitPane.setDividerLocation(350); 
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(5);
        d.add(splitPane, BorderLayout.CENTER); 
        
        d.setVisible(true);
    }
    
    public static void bearbeiteVertrag(VertragVorlage v) {
        JTextField fAuftraggeber = new JTextField(v == null ? "" : v.auftraggeber);
        JTextField fZielArt = new JTextField(v == null ? "KTP" : v.zielEinsatzArt);
        JTextField fMenge = new JTextField(v == null ? "3" : String.valueOf(v.zielMenge));
        JTextField fGeld = new JTextField(v == null ? "1500" : String.valueOf(v.belohnungProTag));
        JTextField fStrafe = new JTextField(v == null ? "1000" : String.valueOf(v.strafeBeiFehlschlag));

        Object[] msg = { "Auftraggeber (Name):", fAuftraggeber, "Geforderte Einsatz-Art (z.B. KTP, R1, FW):", fZielArt, "Menge pro Tag:", fMenge, "Taegliche Belohnung (EUR):", fGeld, "Strafe bei Fehlschlag (EUR):", fStrafe };
        String titel = v == null ? "Neuen Vertrag erstellen" : "Vertrag bearbeiten";
        
        if (JOptionPane.showConfirmDialog(LogistikSimulator.frame, msg, titel, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (v == null) {
                    VertragVorlage neu = new VertragVorlage(fAuftraggeber.getText(), "Individueller Vertrag", fZielArt.getText(), Integer.parseInt(fMenge.getText()), Integer.parseInt(fGeld.getText()), Integer.parseInt(fStrafe.getText()));
                    LogistikSimulator.vertragsVorlagen.add(neu);
                } else {
                    v.auftraggeber = fAuftraggeber.getText(); v.zielEinsatzArt = fZielArt.getText(); v.zielMenge = Integer.parseInt(fMenge.getText());
                    v.belohnungProTag = Integer.parseInt(fGeld.getText()); v.strafeBeiFehlschlag = Integer.parseInt(fStrafe.getText());
                }
            } catch(Exception ex) { JOptionPane.showMessageDialog(LogistikSimulator.frame, "Fehlerhafte Eingabe bei den Zahlen!"); }
        }
    }
    
}