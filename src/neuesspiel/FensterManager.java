package neuesspiel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static neuesspiel.LogistikSimulator.*;

public class FensterManager {

    // --- DIE MAGISCHE METHODE FÜR RAHMENLOSE FENSTER ---
    private static JDialog createFramelessDialog(String title, int width, int height) {
        JDialog d = new JDialog(frame, title, true);
        d.setUndecorated(true);
        d.setSize(width, height);
        d.setLocationRelativeTo(frame);
        d.setLayout(new BorderLayout());
        
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        
        JLabel lblTitle = new JLabel(" " + title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleBar.add(lblTitle, BorderLayout.WEST);
        
        JButton btnClose = new JButton("X");
        btnClose.setBackground(new Color(192, 57, 43));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        btnClose.addActionListener(e -> d.dispose());
        titleBar.add(btnClose, BorderLayout.EAST);
        
        final Point[] dragPoint = new Point[1];
        titleBar.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { dragPoint[0] = e.getPoint(); }});
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { d.setLocation(d.getLocation().x + e.getX() - dragPoint[0].x, d.getLocation().y + e.getY() - dragPoint[0].y); }
        });
        
        d.add(titleBar, BorderLayout.NORTH);
        return d;
    }

    public static void oeffnePersonalHauptmenu() {
        JDialog d = createFramelessDialog("Personalwesen", 400, 300);
        JPanel content = new JPanel(new GridLayout(5, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JButton b1 = new JButton("Dienstplan / Schichten"); b1.addActionListener(e -> { d.dispose(); Schichtplaner.oeffneSchichtplan(); });
        JButton b2 = new JButton("Mitarbeiter Verwaltung"); b2.addActionListener(e -> { d.dispose(); oeffneMitarbeiterVerwaltung(); });
        JButton b3 = new JButton("Personal einstellen (500 EURO)"); b3.addActionListener(e -> { d.dispose(); personalEinstellen(); });
        JButton b4 = new JButton("Personal weiterbilden"); b4.addActionListener(e -> { d.dispose(); oeffnePersonalWeiterbildung(); });
        JButton b5 = new JButton("Leihkraft anfordern (250 EURO)"); b5.addActionListener(e -> { d.dispose(); leihkraftAnfordern(); });

        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneLogistikHauptmenu() {
        JDialog d = createFramelessDialog("Lager & Logistik", 400, 200);
        JPanel content = new JPanel(new GridLayout(3, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JButton b1 = new JButton("Einkauf (Lager fuellen)"); b1.addActionListener(e -> { d.dispose(); oeffneBestellMenu(); });
        JButton b2 = new JButton("Logistik (Wache versorgen)"); b2.addActionListener(e -> { d.dispose(); oeffneLogistikMenu(); });
        JButton b3 = new JButton("Material- & Lageruebersicht"); b3.addActionListener(e -> { d.dispose(); oeffneMaterialUebersicht(); });

        content.add(b1); content.add(b2); content.add(b3);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneMaterialUebersicht() {
        JDialog d = createFramelessDialog("Material- & Lageruebersicht", 800, 500);
        JPanel content = new JPanel(new BorderLayout(10,10));
        content.setBackground(new Color(35,35,35));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(35, 35, 35));
        JLabel lblSearch = new JLabel("Nach Material suchen: "); lblSearch.setForeground(Color.WHITE);
        JTextField txtSearch = new JTextField(20);
        topPanel.add(lblSearch); topPanel.add(txtSearch);
        content.add(topPanel, BorderLayout.NORTH);

        ArrayList<String> cols = new ArrayList<>();
        cols.add("Material"); cols.add("Warnschwelle"); cols.add("Hauptlager");
        for (Wache w : wachen) cols.add(w.name);

        DefaultTableModel model = new DefaultTableModel(cols.toArray(new String[0]), 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (CustomMaterial cm : customMaterials) {
            ArrayList<Object> row = new ArrayList<>();
            row.add(cm.name); row.add(cm.warnSchwelle); row.add(hauptlager.getOrDefault(cm.name, 0));
            for (Wache w : wachen) row.add(w.material.getOrDefault(cm.name, 0));
            model.addRow(row.toArray());
        }

        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? new Color(60, 60, 60) : new Color(43, 43, 43)); c.setForeground(Color.WHITE); return c;
            }
        });

        table.getTableHeader().setBackground(new Color(20, 30, 48)); table.getTableHeader().setForeground(Color.WHITE); table.setRowHeight(30);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model); table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); } @Override public void removeUpdate(DocumentEvent e) { filter(); } @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                sorter.setRowFilter(text.length() == 0 ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });

        content.add(new JScrollPane(table), BorderLayout.CENTER);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneFuhrparkHauptmenu() {
        JDialog d = createFramelessDialog("Fuhrpark & Werkstatt", 400, 250); 
        
        JPanel content = new JPanel(new GridLayout(4, 1, 10, 10)); 
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35,35,35));

        JButton b1 = new JButton("Fahrzeuge verwalten / kaufen"); 
        b1.addActionListener(e -> { d.dispose(); oeffneFuhrpark(); });
        
        JButton b2 = new JButton("Beschaedigtes Fahrzeug reparieren"); 
        b2.addActionListener(e -> { d.dispose(); fahrzeugeReparieren(); });
        
        JButton b3 = new JButton("Fahrzeug umstationieren"); 
        b3.addActionListener(e -> { d.dispose(); oeffneFahrzeugTransfer(); });
        
        JButton b4 = new JButton("TÜV & Inspektion durchfuehren"); 
        b4.addActionListener(e -> { d.dispose(); LogistikSimulator.fahrzeugeInspektion(); });

        content.add(b1); 
        content.add(b2); 
        content.add(b3);
        content.add(b4);

        d.add(content, BorderLayout.CENTER); 
        d.setVisible(true);
    }

    public static void oeffneSystemHauptmenu() {
        // Fenster etwas vergroessert fuer 8 Buttons
        JDialog d = createFramelessDialog("System & Editor", 400, 450);
        JPanel content = new JPanel(new GridLayout(8, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35,35,35));

        JButton b1 = new JButton("Spieleinstellungen"); b1.addActionListener(e -> { d.dispose(); oeffneEinstellungen(); });
        JButton b2 = new JButton("Spiel Speichern"); b2.addActionListener(e -> { d.dispose(); SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(frame, "Spiel gespeichert!"); });
        JButton b3 = new JButton("Spiel Laden"); b3.addActionListener(e -> { d.dispose(); SpeicherManager.laden("savegame.properties"); JOptionPane.showMessageDialog(frame, "Spielstand geladen!"); uiAktualisieren(getUhrzeit()); });
        JButton b4 = new JButton("Einsatz-Vorlage erstellen"); b4.addActionListener(e -> { d.dispose(); oeffneEinsatzErsteller(); });
        JButton b5 = new JButton("Einsatz-Vorlage bearbeiten"); b5.addActionListener(e -> { d.dispose(); oeffneEinsatzBearbeiter(); });
        JButton b6 = new JButton("Material-Vorlage erstellen"); b6.addActionListener(e -> { d.dispose(); oeffneMaterialErsteller(); });
        JButton b7 = new JButton("Material-Vorlage bearbeiten"); b7.addActionListener(e -> { d.dispose(); oeffneMaterialBearbeiter(); });
        
        JButton btnVertragEditor = new JButton("Vertrags-Editor"); 
        btnVertragEditor.addActionListener(e -> { d.dispose(); oeffneVertragsEditor(); });
        
        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); content.add(b6); content.add(b7); content.add(btnVertragEditor);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneMitarbeiterVerwaltung() {
        JDialog d = createFramelessDialog("Mitarbeiter Historie", 900, 500);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(35,35,35));

        String[] columns = {"Name", "Personalnummer", "Wache", "Schichten (Monat)", "Qualifikationen", "Ereignisse"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };

        for (Wache w : wachen) {
            for (Personal p : w.personalPool) {
                String ereignis = "Keine Ereignisse";
                if (p.krankBis != -1) ereignis = "Krank bis " + getShortDatumString(p.krankBis);
                else if (p.urlaubStart != -1) ereignis = "Urlaub: " + getShortDatumString(p.urlaubStart) + " - " + getShortDatumString(p.urlaubEnd);
                model.addRow(new Object[]{ p.name, p.getPersonalNummer(), w.name, p.schichtenMonat, String.join(", ", p.qualifikationen), ereignis });
            }
        }

        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(isSelected ? new Color(60, 60, 60) : new Color(43, 43, 43)); c.setForeground(Color.WHITE); return c;
            }
        });
        table.getTableHeader().setBackground(new Color(20, 30, 48)); table.getTableHeader().setForeground(Color.WHITE); table.setRowHeight(25);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model); table.setRowSorter(sorter);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); topPanel.setBackground(new Color(35, 35, 35));
        JLabel lblSearch = new JLabel("Suchen: "); lblSearch.setForeground(Color.WHITE);
        JTextField txtSearch = new JTextField(20);
        topPanel.add(lblSearch); topPanel.add(txtSearch);
        
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); } @Override public void removeUpdate(DocumentEvent e) { filter(); } @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() { sorter.setRowFilter(txtSearch.getText().trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + txtSearch.getText().trim())); }
        });

        content.add(topPanel, BorderLayout.NORTH); content.add(new JScrollPane(table), BorderLayout.CENTER);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffnePostfach() {
        JDialog d = createFramelessDialog("E-Mail Postfach", 900, 500);
        d.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = postfach.size() - 1; i >= 0; i--) {
            Email e = postfach.get(i);
            String prefix = e.gelesen ? "[Gelesen] " : "[NEU] ";
            String typ = e.typ.equals("Info") ? "[Info]" : "[Bearbeitet]";
            listModel.addElement(prefix + typ + " " + e.betreff + " - von: " + e.absender);
        }

        JList<String> list = new JList<>(listModel);
        list.setBackground(new Color(43, 43, 43));
        list.setForeground(Color.WHITE);
        
        JTextArea txt = new JTextArea();
        txt.setEditable(false);
        txt.setBackground(new Color(35, 35, 35));
        txt.setForeground(Color.WHITE);
        txt.setMargin(new Insets(10, 10, 10, 10));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);

        JPanel pnlBtns = new JPanel(new FlowLayout());
        pnlBtns.setBackground(new Color(35, 35, 35));
        
        JButton btnGenehmigen = new JButton("Urlaub Genehmigen");
        JButton btnAblehnen = new JButton("Ablehnen");
        JButton btnTM = new JButton("Als TM ausbilden");
        JButton btnRS = new JButton("Als RS ausbilden");
        JButton btnLehrgang = new JButton("Lehrgang Bezahlen");
        JButton btnAnerkennen = new JButton("Anerkennen");
        JButton btnLoeschen = new JButton("Loeschen");
        
        btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedIndex() != -1) {
                int idx = postfach.size() - 1 - list.getSelectedIndex();
                Email mail = postfach.get(idx);
                mail.gelesen = true;
                
                String prefix = "[Gelesen] ";
                String typStr = mail.typ.equals("Info") ? "[Info]" : "[Bearbeitet]";
                listModel.set(list.getSelectedIndex(), prefix + typStr + " " + mail.betreff + " - von: " + mail.absender);
                
                txt.setText("Von: " + mail.absender + "\nBetreff: " + mail.betreff + "\n\n" + mail.text);
                btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false);
                
                if (mail.typ.equals("Urlaub") || mail.typ.equals("Urlaubsverlaengerung")) {
                    btnGenehmigen.setVisible(true); btnAblehnen.setVisible(true);
                } else if (mail.typ.equals("Anwaerter")) {
                    btnTM.setVisible(true); btnRS.setVisible(true);
                } else if (mail.typ.equals("Lehrgang_Anfrage")) {
                    btnLehrgang.setVisible(true); btnAblehnen.setVisible(true);
                } else if (mail.typ.equals("Vorwissen")) {
                    btnAnerkennen.setVisible(true); btnAblehnen.setVisible(true);
                }
            }
        });

        btnAnerkennen.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex();
            Email mail = postfach.get(idx);
            if (mail.person != null) {
                String[] parts = mail.text.split("##");
                if(parts.length > 1) {
                    String vorhandenerLehrgang = parts[1].trim();
                    if(!mail.person.qualifikationen.contains(vorhandenerLehrgang)) {
                        mail.person.qualifikationen.add(vorhandenerLehrgang);
                    }
                }
                mail.typ = "Info"; mail.betreff = "[Anerkannt] " + mail.betreff;
                JOptionPane.showMessageDialog(d, "Vorwissen anerkannt!");
                uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
            } else {
                JOptionPane.showMessageDialog(d, "Diese alte E-Mail wurde durch das Speichern beschaedigt.\nBitte loesche sie.");
            }
        });

        btnLehrgang.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex();
            Email mail = postfach.get(idx);
            if (mail.person != null) {
                String[] parts = mail.text.split("##");
                if(parts.length > 2) {
                    String wunschLehrgang = parts[1].trim();
                    int kosten = Integer.parseInt(parts[2].trim());
                    if (budget >= kosten) {
                        budget -= kosten;
                        mail.person.status = "Lehrgang";
                        mail.person.geplanterStatus = "Lehrgang";
                        mail.person.lehrgangDauerSec = 3 * 60; // 3 "Tage" (Minuten in Realzeit bei Speed 1)
                        mail.person.lehrgangThema = wunschLehrgang;
                        mail.typ = "Info"; mail.betreff = "[Bezahlt] " + mail.betreff;
                        JOptionPane.showMessageDialog(d, "Lehrgang bezahlt! Mitarbeiter ist unterwegs.");
                        uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
                    } else {
                        JOptionPane.showMessageDialog(d, "Nicht genug Budget (" + kosten + "EURO benoetigt)!");
                    }
                } else {
                    JOptionPane.showMessageDialog(d, "Mail defekt", "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(d, "Diese alte E-Mail wurde durch das Speichern beschaedigt.\nBitte loesche sie, der Mitarbeiter fragt spaeter erneut!");
            }
        });

        java.awt.event.ActionListener anwaerterAction = e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex();
            Email mail = postfach.get(idx);
            String role = ((JButton)e.getSource()).getText().contains("TM") ? "TM" : "RS";
            if (mail.person != null) {
                mail.person.qualifikationen.remove("Anwaerter");
                mail.person.qualifikationen.add(role);
                mail.typ = "Info"; mail.betreff = "[Uebernommen: " + role + "] " + mail.betreff;
                JOptionPane.showMessageDialog(d, mail.person.name + " ist nun voll ausgebildeter " + role + "!");
                uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
            }
        };
        btnTM.addActionListener(anwaerterAction);
        btnRS.addActionListener(anwaerterAction);

        btnGenehmigen.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex();
            Email mail = postfach.get(idx);
            if (mail.person != null) {
                mail.person.urlaubStart = mail.startTag;
                mail.person.urlaubEnd = mail.endTag;
                mail.person.geplanterStatus = "Bereit";
                mail.typ = "Info"; mail.betreff = "[Genehmigt] " + mail.betreff;
                JOptionPane.showMessageDialog(d, "Urlaub eingetragen!");
                uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
            }
        });

        btnAblehnen.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if(idx != -1) { 
                Email mail = postfach.get(idx); 
                
                // 2.5% Chance auf "Frust-Krankmeldung", wenn Urlaub abgelehnt wird
                if (mail.typ.equals("Urlaub") && cfgKrankheit) {
                    if (Math.random() < 0.025) {
                        int dauer = 2 + (int)(Math.random() * 4); // 2 bis 5 Tage "krank"
                        mail.person.krankBis = tag + dauer;
                        
                        // Sofortiges Update im Schichtplan (wenn die Ablehnung heute passiert)
                        if (tag == mail.person.krankBis - dauer) {
                            mail.person.status = "Krank";
                            mail.person.zugewiesenesFahrzeug = "Keines";
                        }
                        
                        // Wir schicken die E-Mail heimlich in den Posteingang
                        postfach.add(0, MailGenerator.generiereKrankmeldung(mail.person, tag + 1, tag + dauer));
                        JOptionPane.showMessageDialog(d, "Urlaub abgelehnt. Hoffen wir mal, dass das keine Konsequenzen hat...", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                
                mail.typ = "Info"; 
                listModel.set(idx, "[Abgelehnt] " + mail.betreff + " - von: " + mail.absender); 
                btnGenehmigen.setVisible(false); btnAblehnen.setVisible(false); btnTM.setVisible(false); btnRS.setVisible(false); btnLehrgang.setVisible(false); btnAnerkennen.setVisible(false); 
                uiAktualisieren(getUhrzeit()); 
            }
        });

        btnLoeschen.addActionListener(e -> {
            int selectedIndex = list.getSelectedIndex(); 
            if (selectedIndex != -1) {
                int echterIndex = LogistikSimulator.postfach.size() - 1 - selectedIndex;
                LogistikSimulator.postfach.remove(echterIndex);
                ((DefaultListModel) list.getModel()).remove(selectedIndex); 
                txt.setText("Keine E-Mail ausgewaehlt."); 
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
            }
        });

        pnlBtns.add(btnGenehmigen); pnlBtns.add(btnTM); pnlBtns.add(btnRS); pnlBtns.add(btnLehrgang); pnlBtns.add(btnAnerkennen); pnlBtns.add(btnAblehnen); pnlBtns.add(btnLoeschen);
        d.add(pnlBtns, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), new JScrollPane(txt));
        splitPane.setDividerLocation(300);
        d.add(splitPane, BorderLayout.CENTER);

        d.setVisible(true);
    }

    public static void oeffneBank() {
        JDialog d = createFramelessDialog("Bank & Vertraege", 450, 450);
        d.setLayout(new GridLayout(7, 1, 10, 10)); // Auf 7 Reihen angepasst, da der Editor weg ist!
        d.getContentPane().setBackground(new Color(35, 35, 35));

        JLabel lblInfo = new JLabel("Aktuelle Schulden: " + LogistikSimulator.aktuellerKredit + " EUR", SwingConstants.CENTER);
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        d.add(lblInfo);

        JButton btnKredit1 = LogistikSimulator.createStyledButton("Kleinkredit (10.000 EUR)", new Color(41, 128, 185));
        JButton btnKredit2 = LogistikSimulator.createStyledButton("Mittelstand (50.000 EUR)", new Color(39, 174, 96));
        JButton btnKredit3 = LogistikSimulator.createStyledButton("Grosskredit (100.000 EUR)", new Color(192, 57, 43));
        
        if (LogistikSimulator.level < 5) btnKredit1.setEnabled(false);
        if (LogistikSimulator.level < 15) btnKredit2.setEnabled(false);
        if (LogistikSimulator.level < 25) btnKredit3.setEnabled(false);
        if (LogistikSimulator.aktuellerKredit > 0) {
            btnKredit1.setEnabled(false); btnKredit2.setEnabled(false); btnKredit3.setEnabled(false);
        }

        java.awt.event.ActionListener listener = e -> {
            int betrag = e.getSource() == btnKredit1 ? 10000 : (e.getSource() == btnKredit2 ? 50000 : 100000);
            int rate = e.getSource() == btnKredit1 ? 500 : (e.getSource() == btnKredit2 ? 1500 : 2500);
            if(JOptionPane.showConfirmDialog(d, "Kredit aufnehmen?", "Bank", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                LogistikSimulator.budget += betrag;
                LogistikSimulator.aktuellerKredit = betrag + (betrag / 10);
                LogistikSimulator.taeglicheKreditRate = rate;
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                d.dispose();
            }
        };
        btnKredit1.addActionListener(listener); btnKredit2.addActionListener(listener); btnKredit3.addActionListener(listener);
        
        JButton btnSondertilgung = LogistikSimulator.createStyledButton("Sofort-Tilgung", new Color(243, 156, 18));
        btnSondertilgung.addActionListener(e -> {
             if (LogistikSimulator.budget >= LogistikSimulator.aktuellerKredit) {
                 LogistikSimulator.budget -= LogistikSimulator.aktuellerKredit;
                 LogistikSimulator.aktuellerKredit = 0; LogistikSimulator.taeglicheKreditRate = 0;
                 LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose();
             }
        });
        
        JButton btnVertraege = LogistikSimulator.createStyledButton("Vertraege & Dauerauftraege", new Color(142, 68, 173));
        btnVertraege.addActionListener(e -> { d.dispose(); oeffneVertragsMenu(); });
        
        d.add(btnKredit1); d.add(btnKredit2); d.add(btnKredit3); d.add(btnSondertilgung); 
        d.add(new JLabel(" ")); d.add(btnVertraege); 
        d.setVisible(true);
    }

    public static void oeffneVertragsMenu() {
        if(LogistikSimulator.vertragsVorlagen.isEmpty()) {
            LogistikSimulator.vertragsVorlagen.add(new VertragVorlage("Klinikverbund", "Taegliche KTW Fahrten", "KTP", 3, 1500, 1000));
        }
        
        String[] namen = new String[LogistikSimulator.vertragsVorlagen.size()];
        for(int i=0; i<LogistikSimulator.vertragsVorlagen.size(); i++) {
            VertragVorlage v = LogistikSimulator.vertragsVorlagen.get(i);
            namen[i] = v.auftraggeber + " (" + v.zielMenge + "x " + v.zielEinsatzArt + " / Tag)";
        }
        
        String wahl = (String) JOptionPane.showInputDialog(LogistikSimulator.frame, "Verfuegbare Vertraege (Achtung: Strafen bei Nicht-Erfuellung!):\nAktive Vertraege: " + LogistikSimulator.aktiveVertraege.size(), "Vertragsverwaltung", JOptionPane.QUESTION_MESSAGE, null, namen, namen[0]);
        
        if (wahl != null) {
            for(VertragVorlage vv : LogistikSimulator.vertragsVorlagen) {
                if(wahl.startsWith(vv.auftraggeber)) {
                    LogistikSimulator.aktiveVertraege.add(new Vertrag(vv.auftraggeber, vv.beschreibung, vv.zielEinsatzArt, vv.zielMenge, vv.belohnungProTag, vv.strafeBeiFehlschlag));
                    JOptionPane.showMessageDialog(LogistikSimulator.frame, "Vertrag unterschrieben! Er muss ab HEUTE jeden Tag erfuellt werden.");
                    break;
                }
            }
        }
    }

    public static void oeffneVertragsEditor() {
        JDialog d = createFramelessDialog("Vertrags-Verwaltung", 500, 400);
        d.setLayout(new BorderLayout());
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        Runnable updateList = () -> {
            listModel.clear();
            for (VertragVorlage v : LogistikSimulator.vertragsVorlagen) {
                listModel.addElement(v.auftraggeber + " - " + v.zielMenge + "x " + v.zielEinsatzArt + " (" + v.belohnungProTag + " EUR)");
            }
        };
        updateList.run(); 
        
        JList<String> list = new JList<>(listModel);
        list.setBackground(new Color(43, 43, 43));
        list.setForeground(Color.WHITE);
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel pnlBtns = new JPanel(new GridLayout(1, 4, 5, 5));
        pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlBtns.setBackground(new Color(35, 35, 35));
        
        JButton btnNeu = new JButton("Neu");
        JButton btnEdit = new JButton("Bearbeiten");
        JButton btnDel = new JButton("Loeschen");
        JButton btnClose = new JButton("Schliessen");
        
        btnClose.addActionListener(e -> d.dispose());
        
        btnNeu.addActionListener(e -> {
            bearbeiteVertrag(null);
            updateList.run();
        });
        
        btnEdit.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) {
                bearbeiteVertrag(LogistikSimulator.vertragsVorlagen.get(idx));
                updateList.run();
            } else {
                JOptionPane.showMessageDialog(d, "Bitte waehle zuerst einen Vertrag aus der Liste aus!");
            }
        });
        
        btnDel.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) {
                LogistikSimulator.vertragsVorlagen.remove(idx);
                updateList.run();
            }
        });
        
        pnlBtns.add(btnNeu); pnlBtns.add(btnEdit); pnlBtns.add(btnDel); pnlBtns.add(btnClose);
        
        d.add(new JScrollPane(list), BorderLayout.CENTER);
        d.add(pnlBtns, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
    public static void bearbeiteVertrag(VertragVorlage v) {
        JTextField fAuftraggeber = new JTextField(v == null ? "" : v.auftraggeber);
        JTextField fZielArt = new JTextField(v == null ? "KTP" : v.zielEinsatzArt);
        JTextField fMenge = new JTextField(v == null ? "3" : String.valueOf(v.zielMenge));
        JTextField fGeld = new JTextField(v == null ? "1500" : String.valueOf(v.belohnungProTag));
        JTextField fStrafe = new JTextField(v == null ? "1000" : String.valueOf(v.strafeBeiFehlschlag));

        Object[] msg = { 
            "Auftraggeber (Name):", fAuftraggeber, 
            "Geforderte Einsatz-Art (z.B. KTP, R1, FW):", fZielArt, 
            "Menge pro Tag:", fMenge, 
            "Taegliche Belohnung (EUR):", fGeld, 
            "Strafe bei Fehlschlag (EUR):", fStrafe 
        };
        
        String titel = v == null ? "Neuen Vertrag erstellen" : "Vertrag bearbeiten";
        
        if (JOptionPane.showConfirmDialog(LogistikSimulator.frame, msg, titel, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (v == null) {
                    VertragVorlage neu = new VertragVorlage(fAuftraggeber.getText(), "Individueller Vertrag", fZielArt.getText(), Integer.parseInt(fMenge.getText()), Integer.parseInt(fGeld.getText()), Integer.parseInt(fStrafe.getText()));
                    LogistikSimulator.vertragsVorlagen.add(neu);
                } else {
                    v.auftraggeber = fAuftraggeber.getText();
                    v.zielEinsatzArt = fZielArt.getText();
                    v.zielMenge = Integer.parseInt(fMenge.getText());
                    v.belohnungProTag = Integer.parseInt(fGeld.getText());
                    v.strafeBeiFehlschlag = Integer.parseInt(fStrafe.getText());
                }
            } catch(Exception ex) {
                JOptionPane.showMessageDialog(LogistikSimulator.frame, "Fehlerhafte Eingabe bei den Zahlen!");
            }
        }
    }
    
    public static void oeffneWachenAusbau() {
        JDialog d = createFramelessDialog("Wachen & Gebaeude", 600, 450);
        d.setLayout(new GridLayout(7, 1, 10, 10)); // Layout fuer 7 Zeilen
        
        JPanel p0 = new JPanel(new BorderLayout());
        JLabel lblWachenInfo = new JLabel("Neue Wache bauen (Aktuell: " + wachen.size() + " / Erlaubt: " + getMaxWachenErlaubt() + ")");
        JButton b0 = new JButton("Wache gruenden (10.000 EURO)");
        b0.addActionListener(e -> {
            if(wachen.size() >= getMaxWachenErlaubt()) { JOptionPane.showMessageDialog(d, "Dein Level ist zu niedrig fuer eine weitere Wache!"); return; }
            if(budget >= 10000) {
                String name = JOptionPane.showInputDialog(d, "Name der neuen Wache:");
                if(name != null && !name.trim().isEmpty()) {
                    String kennung = JOptionPane.showInputDialog(d, "Zweinstellige Kennung (z.B. 46):");
                    if(kennung != null && kennung.length() == 2) {
                        budget -= 10000;
                        wachen.add(new Wache(name, kennung));
                        JOptionPane.showMessageDialog(d, "Wache erfolgreich gegruendet!");
                        d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit());
                    }
                }
            } else { JOptionPane.showMessageDialog(d, "Zu wenig Geld!"); }
        });
        p0.add(lblWachenInfo, BorderLayout.CENTER); p0.add(b0, BorderLayout.EAST);

        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(new JLabel("Eigene Werkstatt (Reparaturen 50% guenstiger)"), BorderLayout.CENTER);
        JButton b1 = new JButton(techWerkstatt ? "Gekauft" : "Kaufen (10.000 EURO)");
        b1.setEnabled(!techWerkstatt);
        b1.addActionListener(e -> { if(budget >= 10000) { budget -= 10000; techWerkstatt = true; d.dispose(); uiAktualisieren(getUhrzeit()); } });
        p1.add(b1, BorderLayout.EAST);

        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(new JLabel("Ruheraum (Krankheitsrate sinkt um 50%)"), BorderLayout.CENTER);
        JButton b2 = new JButton(techRuheraum ? "Gekauft" : "Kaufen (15.000 EURO)");
        b2.setEnabled(!techRuheraum);
        b2.addActionListener(e -> { if(budget >= 15000) { budget -= 15000; techRuheraum = true; d.dispose(); uiAktualisieren(getUhrzeit()); } });
        p2.add(b2, BorderLayout.EAST);

        JPanel p3 = new JPanel(new BorderLayout());
        p3.add(new JLabel("Grossabnehmer (Lager-Einkaeufe 20% guenstiger)"), BorderLayout.CENTER);
        JButton b3 = new JButton(techGrossabnehmer ? "Gekauft" : "Kaufen (20.000 EURO)");
        b3.setEnabled(!techGrossabnehmer);
        b3.addActionListener(e -> { if(budget >= 20000) { budget -= 20000; techGrossabnehmer = true; d.dispose(); uiAktualisieren(getUhrzeit()); } });
        p3.add(b3, BorderLayout.EAST);
        
        JPanel p4 = new JPanel(new BorderLayout());
        JLabel lblLehrer = new JLabel("Lehrer schulen (Lehrgaenge 10% schneller. Aktuell: Stufe " + lehrerStufe + "/5)");
        p4.add(lblLehrer, BorderLayout.CENTER);
        int nextLevelReq = (lehrerStufe + 1) * 2;
        JButton b4 = new JButton(lehrerStufe >= 5 ? "Maximalstufe erreicht" : "Stufe " + (lehrerStufe+1) + " Kaufen (5000 EURO, ab Level " + nextLevelReq + ")");
        b4.setEnabled(lehrerStufe < 5 && level >= nextLevelReq);
        b4.addActionListener(e -> { if(budget >= 5000) { budget -= 5000; lehrerStufe++; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        p4.add(b4, BorderLayout.EAST);

        JPanel p5 = new JPanel(new BorderLayout());
        JLabel lblCalltaker = new JLabel();
        JButton b5 = new JButton();
        if(calltakerStufe == 0) {
            lblCalltaker.setText("Calltaker (Basis: Dispo Lvl 1-2. Upgrade ab Lvl 30)");
            b5.setText("Kaufen (30.000 EURO, ab Lvl 20)");
            b5.setEnabled(level >= 20);
        } else if(calltakerStufe == 1) {
            lblCalltaker.setText("Calltaker (Basis: Dispo Lvl 1-2)");
            b5.setText("Erweitern (20.000 EURO, ab Lvl 30)");
            b5.setEnabled(level >= 30);
        } else {
            lblCalltaker.setText("Calltaker (Maximal: Dispo Lvl 1-4 & Auto-Klinik)");
            b5.setText("Maximalstufe");
            b5.setEnabled(false);
        }
        b5.addActionListener(e -> { 
            int cost = calltakerStufe == 0 ? 30000 : 20000;
            if(budget >= cost) { budget -= cost; calltakerStufe++; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } 
        });
        p5.add(lblCalltaker, BorderLayout.CENTER); p5.add(b5, BorderLayout.EAST);

        d.add(p0); d.add(p1); d.add(p2); d.add(p3); d.add(p4); d.add(p5);
        
        JPanel pnlKliniken = new JPanel(new GridLayout(1, 3, 5, 5));
        pnlKliniken.setBorder(BorderFactory.createTitledBorder("Krankenhaeuser (Zielorte fuer RTWs)"));
        
        JButton btnCrivitz = new JButton(techKlinikCrivitz ? "Crivitz (Gekauft)" : "Klinik Crivitz (5000 EURO)");
        btnCrivitz.setEnabled(!techKlinikCrivitz && level >= 10);
        btnCrivitz.addActionListener(e -> { if(budget >= 5000) { budget -= 5000; techKlinikCrivitz = true; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        
        JButton btnLeezen = new JButton(techKlinikLeezen ? "Leezen (Gekauft)" : "Klinik Leezen (10.000 EURO)");
        btnLeezen.setEnabled(!techKlinikLeezen && level >= 20);
        btnLeezen.addActionListener(e -> { if(budget >= 10000) { budget -= 10000; techKlinikLeezen = true; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });

        JButton btnHagenow = new JButton(techKlinikHagenow ? "Hagenow (Gekauft)" : "Klinik Hagenow (20.000 EURO)");
        btnHagenow.setEnabled(!techKlinikHagenow && level >= 30);
        btnHagenow.addActionListener(e -> { if(budget >= 20000) { budget -= 20000; techKlinikHagenow = true; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        
        pnlKliniken.add(btnCrivitz); pnlKliniken.add(btnLeezen); pnlKliniken.add(btnHagenow);
        d.add(pnlKliniken);

        d.setVisible(true);
    }

    public static void oeffneKrankenhausWahl(Fahrzeug f) {
        JDialog d = createFramelessDialog("Zielklinik waehlen fuer " + f.funkrufname, 450, 350);
        d.setLayout(new GridLayout(5, 1, 10, 10));

        JLabel l = new JLabel("Patient verladen. Bitte Zielklinik waehlen:", SwingConstants.CENTER);
        d.add(l);

        boolean k1Voll = klinik1Abgemeldet;
        boolean k2Voll = klinik2Abgemeldet;

        JButton b1 = new JButton(k1Voll ? "[ABGEMELDET] Helios Kliniken Schwerin" : "Helios Kliniken Schwerin (Fahrzeit: 45s)");
        b1.setEnabled(!k1Voll);
        b1.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 45; f.originalAnfahrt = 45; uiAktualisieren(getUhrzeit()); d.dispose(); });
        
        JButton b2 = new JButton(k2Voll ? "[ABGEMELDET] Unimedizin Rostock" : "Universitaetsmedizin Rostock (Fahrzeit: 120s)");
        b2.setEnabled(!k2Voll);
        b2.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 120; f.originalAnfahrt = 120; uiAktualisieren(getUhrzeit()); d.dispose(); });

        d.add(b1); d.add(b2);

        if(techKlinikCrivitz) {
            JButton bc = new JButton(klinikCrivitzAbgemeldet ? "[ABGEMELDET] Klinik Crivitz" : "Krankenhaus Crivitz (Fahrzeit: 50s)");
            bc.setEnabled(!klinikCrivitzAbgemeldet);
            bc.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 50; f.originalAnfahrt = 50; uiAktualisieren(getUhrzeit()); d.dispose(); });
            d.add(bc);
        }
        
        if(techKlinikLeezen) {
            JButton bl = new JButton(klinikLeezenAbgemeldet ? "[ABGEMELDET] Klinik Leezen" : "Krankenhaus Leezen (Fahrzeit: 60s)");
            bl.setEnabled(!klinikLeezenAbgemeldet);
            bl.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 60; f.originalAnfahrt = 60; uiAktualisieren(getUhrzeit()); d.dispose(); });
            d.add(bl);
        }

        d.setVisible(true);
    }

    public static void oeffneBettenUebersicht() {
        JDialog d = createFramelessDialog("Klinik- & Bettenuebersicht", 450, 300);
        d.setLayout(new GridLayout(6, 1, 10, 10));

        d.add(new JLabel("Aktuelle Aufnahmekapazitaeten der Kliniken:", SwingConstants.CENTER));

        JLabel lbl1 = new JLabel(klinik1Abgemeldet ? "[X] Helios Kliniken Schwerin: AUFNAHMESTOPP" : "[OK] Helios Kliniken Schwerin: Aufnahmebereit", SwingConstants.CENTER);
        lbl1.setForeground(klinik1Abgemeldet ? Color.RED : Color.GREEN);
        
        JLabel lbl2 = new JLabel(klinik2Abgemeldet ? "[X] Unimedizin Rostock: AUFNAHMESTOPP" : "[OK] Unimedizin Rostock: Aufnahmebereit", SwingConstants.CENTER);
        lbl2.setForeground(klinik2Abgemeldet ? Color.RED : Color.GREEN);
        
        d.add(lbl1); d.add(lbl2);
        
        if(techKlinikCrivitz) {
            JLabel lc = new JLabel(klinikCrivitzAbgemeldet ? "[X] Klinik Crivitz: AUFNAHMESTOPP" : "[OK] Klinik Crivitz: Aufnahmebereit", SwingConstants.CENTER);
            lc.setForeground(klinikCrivitzAbgemeldet ? Color.RED : Color.GREEN);
            d.add(lc);
        }
        if(techKlinikLeezen) {
            JLabel ll = new JLabel(klinikLeezenAbgemeldet ? "[X] Klinik Leezen: AUFNAHMESTOPP" : "[OK] Klinik Leezen: Aufnahmebereit", SwingConstants.CENTER);
            ll.setForeground(klinikLeezenAbgemeldet ? Color.RED : Color.GREEN);
            d.add(ll);
        }
        if(techKlinikHagenow) {
            JLabel lh = new JLabel(klinikHagenowAbgemeldet ? "[X] Klinik Hagenow: AUFNAHMESTOPP" : "[OK] Klinik Hagenow: Aufnahmebereit", SwingConstants.CENTER);
            lh.setForeground(klinikHagenowAbgemeldet ? Color.RED : Color.GREEN);
            d.add(lh);
        }

        d.setVisible(true);
    }

    public static void oeffneFahrzeugTransfer() {
        if(wachen.size() < 2) { JOptionPane.showMessageDialog(frame, "Du brauchst mindestens zwei Wachen fuer einen Transfer!"); return; }
        
        JDialog d = createFramelessDialog("Fahrzeug umstationieren", 400, 200);
        d.setLayout(new GridLayout(3, 2, 10, 10));

        JComboBox<String> cbFz = new JComboBox<>();
        for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) cbFz.addItem(f.funkrufname + " (" + w.name + ")");
        
        JComboBox<String> cbZiel = new JComboBox<>();
        for(Wache w : wachen) cbZiel.addItem(w.name);

        JButton btnTransfer = new JButton("Umstationieren");
        btnTransfer.addActionListener(e -> {
            int fzIndex = cbFz.getSelectedIndex();
            int zielIndex = cbZiel.getSelectedIndex();
            if(fzIndex == -1 || zielIndex == -1) return;
            
            String fzName = cbFz.getItemAt(fzIndex).split(" ")[0];
            Fahrzeug targetFz = null; Wache currentWache = null;
            for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if(f.funkrufname.equals(fzName)) { targetFz = f; currentWache = w; break; }
            
            Wache zielWache = wachen.get(zielIndex);
            
            if(currentWache == zielWache) { JOptionPane.showMessageDialog(d, "Das Fahrzeug steht bereits auf dieser Wache!"); return; }
            if(targetFz.status != 1 && targetFz.status != 2 && targetFz.status != 6) { JOptionPane.showMessageDialog(d, "Fahrzeug muss auf Status 1, 2 oder 6 sein!"); return; }
            
            String alteKennung = targetFz.funkrufname;
            currentWache.fuhrpark.remove(targetFz);
            targetFz.funkrufname = zielWache.generiereFunkrufname(targetFz.typ);
            zielWache.addFahrzeug(targetFz);
            
            for(Personal p : currentWache.personalPool) {
                if(p.zugewiesenesFahrzeug.equals(alteKennung)) p.zugewiesenesFahrzeug = "Keines";
                if(p.geplantesFahrzeug.equals(alteKennung)) p.geplantesFahrzeug = "Keines";
            }
            
            targetFz.status = 6; targetFz.ausfallGrund = "Personal fehlt";
            JOptionPane.showMessageDialog(d, "Fahrzeug umstationiert! Neuer Funkrufname: " + targetFz.funkrufname + "\nAchtung: Das Personal wurde vom Fahrzeug entfernt.");
            uiAktualisieren(getUhrzeit()); d.dispose();
        });

        d.add(new JLabel("Fahrzeug waehlen:")); d.add(cbFz);
        d.add(new JLabel("Zielwache waehlen:")); d.add(cbZiel);
        d.add(new JLabel("")); d.add(btnTransfer);

        d.setVisible(true);
    }

    public static void oeffnePersonalWeiterbildung() {
        JDialog d = createFramelessDialog("Manuelle Personal Weiterbildung", 400, 200);
        d.setLayout(new GridLayout(3, 2, 10, 10));

        ArrayList<Personal> alleMitarbeiter = new ArrayList<>();
        JComboBox<String> cbPers = new JComboBox<>();
        for(Wache w : wachen) {
            for(Personal p : w.personalPool) {
                alleMitarbeiter.add(p);
                cbPers.addItem(p.name + " (" + w.name + ")");
            }
        }
        
        if(alleMitarbeiter.isEmpty()) { JOptionPane.showMessageDialog(d, "Kein Personal vorhanden!"); return; }

        JComboBox<String> cbKurs = new JComboBox<>(new String[]{"RS (500 EURO)", "NFS (1500 EURO)", "NA (3000 EURO)", "TF (500 EURO)", "GF (1500 EURO)", "MA (1000 EURO)", "FueAs (500 EURO)", "EL (2000 EURO)"});

        JButton btnKaufen = new JButton("Weiterbildung starten");
        btnKaufen.addActionListener(e -> {
            double rabatt = 1.0 - (lehrerStufe * 0.10); 
            int pIndex = cbPers.getSelectedIndex();
            String kursStr = (String) cbKurs.getSelectedItem();
            String q = kursStr.split(" ")[0];
            int cost = Integer.parseInt(kursStr.split("\\(")[1].split(" ")[0]);
            
            Personal p = alleMitarbeiter.get(pIndex);
            
            if(p.qualifikationen.contains("Anwaerter")) { JOptionPane.showMessageDialog(d, "Anwaerter koennen erst nach ihrer ersten Schicht ausgebildet werden!"); return; }
            if(p.status.equals("Urlaub") || p.status.equals("Krank") || p.status.equals("Lehrgang")) { JOptionPane.showMessageDialog(d, "Der Mitarbeiter ist aktuell nicht verfuegbar!"); return; }
            if(p.qualifikationen.contains(q)) { JOptionPane.showMessageDialog(d, "Personal erfuellt diese Qualifikation bereits!"); return; }
            
            if(budget >= cost) {
                budget -= cost;
                p.status = "Lehrgang";
                p.geplanterStatus = "Lehrgang";
                p.lehrgangDauerSec = (int)(3 * 60 * rabatt); 
                p.lehrgangThema = q;
                JOptionPane.showMessageDialog(d, p.name + " ist nun fuer " + p.lehrgangDauerSec + " Sekunden auf Lehrgang zum " + q + "!");
                uiAktualisieren(getUhrzeit()); d.dispose();
            } else {
                JOptionPane.showMessageDialog(d, "Nicht genug Budget!");
            }
        });

        d.add(new JLabel("Mitarbeiter waehlen:")); d.add(cbPers);
        d.add(new JLabel("Lehrgang waehlen:")); d.add(cbKurs);
        d.add(new JLabel("")); d.add(btnKaufen);

        d.setVisible(true);
    }

    public static void oeffneEinstellungen() {
        JDialog d = createFramelessDialog("Spieleinstellungen", 500, 450); // Fenster leicht vergroessert fuer den neuen Logistik Schalter
        d.setLayout(new GridLayout(10, 1, 5, 5)); 
        d.getContentPane().setBackground(new Color(35, 35, 35));

        JCheckBox cbKtp = new JCheckBox("Krankentransport generieren", LogistikSimulator.cfgKrankentransport);
        JCheckBox cbDmg = new JCheckBox("Beschaedigte Fahrzeuge erlauben", LogistikSimulator.cfgBeschaedigung);
        JCheckBox cbSick = new JCheckBox("Krankes Personal erlauben", LogistikSimulator.cfgKrankheit);
        JCheckBox cbAuto = new JCheckBox("Auto-Umlagerung (Lager -> Wache)", LogistikSimulator.cfgAutoTransfer);
        
        // NEU: Logistik Checkbox
        JCheckBox cbLogistik = new JCheckBox("Lager & Logistik System aktivieren", LogistikSimulator.cfgLogistikAktiv);
        
        JCheckBox[] topBoxes = {cbKtp, cbDmg, cbSick, cbAuto, cbLogistik};
        for (JCheckBox box : topBoxes) {
            box.setForeground(Color.WHITE); box.setBackground(new Color(35, 35, 35)); box.setFocusPainted(false);
            d.add(box);
        }

        JPanel pnlS1 = new JPanel(new BorderLayout(10, 0)); pnlS1.setBackground(new Color(35, 35, 35));
        JCheckBox cbSoundNotruf = new JCheckBox("Sound: Neuer Notruf", LogistikSimulator.cfgSoundNotruf);
        cbSoundNotruf.setForeground(Color.WHITE); cbSoundNotruf.setBackground(new Color(35, 35, 35));
        JSlider slNotruf = new JSlider(0, 100, LogistikSimulator.volNotruf); slNotruf.setBackground(new Color(35, 35, 35));
        pnlS1.add(cbSoundNotruf, BorderLayout.WEST); pnlS1.add(slNotruf, BorderLayout.CENTER);
        d.add(pnlS1);

        JPanel pnlS2 = new JPanel(new BorderLayout(10, 0)); pnlS2.setBackground(new Color(35, 35, 35));
        JCheckBox cbSoundStatus6 = new JCheckBox("Sound: Status 6 (Defekt)", LogistikSimulator.cfgSoundStatus6);
        cbSoundStatus6.setForeground(Color.WHITE); cbSoundStatus6.setBackground(new Color(35, 35, 35));
        JSlider slStatus6 = new JSlider(0, 100, LogistikSimulator.volStatus6); slStatus6.setBackground(new Color(35, 35, 35));
        pnlS2.add(cbSoundStatus6, BorderLayout.WEST); pnlS2.add(slStatus6, BorderLayout.CENTER);
        d.add(pnlS2);

        JPanel pnlS3 = new JPanel(new BorderLayout(10, 0)); pnlS3.setBackground(new Color(35, 35, 35));
        JCheckBox cbSoundStatus7 = new JCheckBox("Sound: Status 7 (Warten)", LogistikSimulator.cfgSoundStatus7);
        cbSoundStatus7.setForeground(Color.WHITE); cbSoundStatus7.setBackground(new Color(35, 35, 35));
        JSlider slStatus7 = new JSlider(0, 100, LogistikSimulator.volStatus7); slStatus7.setBackground(new Color(35, 35, 35));
        pnlS3.add(cbSoundStatus7, BorderLayout.WEST); pnlS3.add(slStatus7, BorderLayout.CENTER);
        d.add(pnlS3);
        
        JButton btnReset = new JButton("Spielstand zuruecksetzen");
        btnReset.setBackground(new Color(192, 57, 43)); btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> {
            String wahl = JOptionPane.showInputDialog(d, "ACHTUNG: Dies setzt den Spielstand zurueck!\nZum Bestaetigen bitte exakt 'LOESCHEN' eingeben:");
            if(wahl != null && wahl.equals("LOESCHEN")) {
                new java.io.File("savegame.properties").delete();
                LogistikSimulator.initStandardDaten(); LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                JOptionPane.showMessageDialog(d, "Spielstand wurde erfolgreich zurueckgesetzt!"); d.dispose();
            } else if (wahl != null) JOptionPane.showMessageDialog(d, "Eingabe fehlerhaft. Abbruch.");
        });

        JButton btnSave = new JButton("Speichern & Schliessen");
        btnSave.addActionListener(e -> {
            LogistikSimulator.cfgKrankentransport = cbKtp.isSelected();
            LogistikSimulator.cfgBeschaedigung = cbDmg.isSelected();
            LogistikSimulator.cfgKrankheit = cbSick.isSelected();
            LogistikSimulator.cfgAutoTransfer = cbAuto.isSelected();
            
            // Logistik Schalter uebernehmen
            LogistikSimulator.cfgLogistikAktiv = cbLogistik.isSelected();
            
            LogistikSimulator.cfgSoundNotruf = cbSoundNotruf.isSelected();
            LogistikSimulator.cfgSoundStatus6 = cbSoundStatus6.isSelected();
            LogistikSimulator.cfgSoundStatus7 = cbSoundStatus7.isSelected();
            
            LogistikSimulator.volNotruf = slNotruf.getValue();
            LogistikSimulator.volStatus6 = slStatus6.getValue();
            LogistikSimulator.volStatus7 = slStatus7.getValue();
            
            SpeicherManager.speichern("savegame.properties");
            uiAktualisieren(getUhrzeit()); // Damit sich der Button direkt versteckt/zeigt
            d.dispose();
        });

        d.add(btnReset); d.add(btnSave);
        d.setVisible(true);
    }

    public static void oeffneFuhrpark() {
        if(wachen.isEmpty()){ JOptionPane.showMessageDialog(frame, "Du hast keine Wache!"); return; }
        JDialog d = createFramelessDialog("Fuhrpark verwalten", 400, 300);
        d.setLayout(new GridLayout(7, 2, 10, 10));

        d.add(new JLabel("Fuer welche Wache moechtest du kaufen?"));
        JComboBox<String> cbWachen = new JComboBox<>();
        for(Wache w : wachen) cbWachen.addItem(w.name);
        d.add(cbWachen);

        JButton b1 = new JButton("ELW kaufen (1500 EURO)"); b1.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "ELW", 1500));
        JButton b2 = new JButton("HLF kaufen (3000 EURO)"); b2.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "HLF", 3000));
        JButton b3 = new JButton("DLK kaufen (5000 EURO)"); b3.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "DLK", 5000));
        JButton b4 = new JButton("RTW kaufen (2000 EURO)"); b4.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "RTW", 2000));
        JButton b5 = new JButton("NEF kaufen (2500 EURO)"); b5.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "NEF", 2500));
        JButton b6 = new JButton("KTW kaufen (1000 EURO)"); b6.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "KTW", 1000));

        d.add(b1); d.add(b2); d.add(b3); d.add(b4); d.add(b5); d.add(b6);
        d.setVisible(true);
    }

    public static void oeffneMaterialErsteller() {
        JDialog d = createFramelessDialog("Eigenes Material Erstellen", 500, 400);
        d.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField txtName = new JTextField();
        JSpinner sVerbrauch = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner sPreis = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 10));
        JSpinner sMenge = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 10));
        JSpinner sWarn = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 5));
        
        form.add(new JLabel("Material Name:")); form.add(txtName);
        form.add(new JLabel("Max. Verbrauch pro Einsatz:")); form.add(sVerbrauch);
        form.add(new JLabel("Kaufpreis (Shop):")); form.add(sPreis);
        form.add(new JLabel("Bestellmenge pro Kauf:")); form.add(sMenge);
        form.add(new JLabel("Warnschwelle (Bestand):")); form.add(sWarn);
        
        form.add(new JLabel("Fahrzeug-Bindung:"));
        JPanel pnlFzOuter = new JPanel(new BorderLayout());
        JPanel pnlFz = new JPanel(new GridLayout(2, 3));
        JCheckBox cbFz_RTW = new JCheckBox("RTW"); JCheckBox cbFz_HLF = new JCheckBox("HLF"); 
        JCheckBox cbFz_NEF = new JCheckBox("NEF"); JCheckBox cbFz_KTW = new JCheckBox("KTW"); 
        JCheckBox cbFz_ELW = new JCheckBox("ELW"); JCheckBox cbFz_DLK = new JCheckBox("DLK");
        pnlFz.add(cbFz_RTW); pnlFz.add(cbFz_HLF); pnlFz.add(cbFz_NEF); pnlFz.add(cbFz_KTW); pnlFz.add(cbFz_ELW); pnlFz.add(cbFz_DLK);
        pnlFzOuter.add(pnlFz, BorderLayout.CENTER);
        form.add(pnlFzOuter);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        topArea.add(form, BorderLayout.CENTER);
        d.add(topArea, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Material Anlegen");
        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Name fehlt!"); return; }
            ArrayList<String> fzList = new ArrayList<>();
            if(cbFz_RTW.isSelected()) fzList.add("RTW"); if(cbFz_HLF.isSelected()) fzList.add("HLF");
            if(cbFz_NEF.isSelected()) fzList.add("NEF"); if(cbFz_KTW.isSelected()) fzList.add("KTW");
            if(cbFz_ELW.isSelected()) fzList.add("ELW"); if(cbFz_DLK.isSelected()) fzList.add("DLK");
            
            CustomMaterial mat = new CustomMaterial(txtName.getText().trim(), fzList, (int)sVerbrauch.getValue(), new ArrayList<>(), (int)sPreis.getValue(), (int)sMenge.getValue(), (int)sWarn.getValue());
            customMaterials.add(mat);
            for(Wache w : wachen) w.material.put(mat.name, 0);
            hauptlager.put(mat.name, 0);
            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Material hinzugefuegt!");
            d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void oeffneMaterialBearbeiter() {
        if(customMaterials.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Materialien!"); return; }
        JDialog d = createFramelessDialog("Material Bearbeiten", 500, 450);
        d.setLayout(new BorderLayout());

        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topSelect.add(new JLabel("Zu bearbeitendes Material:"));
        JComboBox<String> cMatWahl = new JComboBox<>();
        for(CustomMaterial cm : customMaterials) cMatWahl.addItem(cm.name);
        topSelect.add(cMatWahl);
        d.add(topSelect, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        JTextField txtName = new JTextField();
        JSpinner sVerbrauch = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner sPreis = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 10));
        JSpinner sMenge = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 10));
        JSpinner sWarn = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 5));
        
        form.add(new JLabel("Material Name:")); form.add(txtName);
        form.add(new JLabel("Max. Verbrauch pro Einsatz:")); form.add(sVerbrauch);
        form.add(new JLabel("Kaufpreis (Shop):")); form.add(sPreis);
        form.add(new JLabel("Bestellmenge pro Kauf:")); form.add(sMenge);
        form.add(new JLabel("Warnschwelle (Bestand):")); form.add(sWarn);
        
        form.add(new JLabel("Fahrzeug-Bindung:"));
        JPanel pnlFzOuter = new JPanel(new BorderLayout());
        JPanel pnlFz = new JPanel(new GridLayout(2, 3));
        JCheckBox cbFz_RTW = new JCheckBox("RTW"); JCheckBox cbFz_HLF = new JCheckBox("HLF"); 
        JCheckBox cbFz_NEF = new JCheckBox("NEF"); JCheckBox cbFz_KTW = new JCheckBox("KTW"); 
        JCheckBox cbFz_ELW = new JCheckBox("ELW"); JCheckBox cbFz_DLK = new JCheckBox("DLK");
        pnlFz.add(cbFz_RTW); pnlFz.add(cbFz_HLF); pnlFz.add(cbFz_NEF); pnlFz.add(cbFz_KTW); pnlFz.add(cbFz_ELW); pnlFz.add(cbFz_DLK);
        pnlFzOuter.add(pnlFz, BorderLayout.CENTER);
        form.add(pnlFzOuter);

        Runnable ladeMaterial = () -> {
            CustomMaterial cm = customMaterials.get(cMatWahl.getSelectedIndex());
            txtName.setText(cm.name);
            sVerbrauch.setValue(cm.maxVerbrauch);
            sPreis.setValue(cm.preis);
            sMenge.setValue(cm.bestellMenge);
            sWarn.setValue(cm.warnSchwelle);
            cbFz_RTW.setSelected(cm.fahrzeuge.contains("RTW"));
            cbFz_HLF.setSelected(cm.fahrzeuge.contains("HLF"));
            cbFz_NEF.setSelected(cm.fahrzeuge.contains("NEF"));
            cbFz_KTW.setSelected(cm.fahrzeuge.contains("KTW"));
            cbFz_ELW.setSelected(cm.fahrzeuge.contains("ELW"));
            cbFz_DLK.setSelected(cm.fahrzeuge.contains("DLK"));
        };
        ladeMaterial.run();
        cMatWahl.addActionListener(e -> ladeMaterial.run());

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        topArea.add(form, BorderLayout.CENTER);
        d.add(topArea, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Aenderungen Speichern");
        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Name fehlt!"); return; }
            CustomMaterial cm = customMaterials.get(cMatWahl.getSelectedIndex());
            
            String oldWach = cm.name;
            int oldLag = hauptlager.getOrDefault(cm.name, 0);
            hauptlager.remove(cm.name);
            
            cm.name = txtName.getText().trim();
            cm.maxVerbrauch = (int) sVerbrauch.getValue();
            cm.preis = (int) sPreis.getValue();
            cm.bestellMenge = (int) sMenge.getValue();
            cm.warnSchwelle = (int) sWarn.getValue();
            
            cm.fahrzeuge.clear();
            if(cbFz_RTW.isSelected()) cm.fahrzeuge.add("RTW"); if(cbFz_HLF.isSelected()) cm.fahrzeuge.add("HLF");
            if(cbFz_NEF.isSelected()) cm.fahrzeuge.add("NEF"); if(cbFz_KTW.isSelected()) cm.fahrzeuge.add("KTW");
            if(cbFz_ELW.isSelected()) cm.fahrzeuge.add("ELW"); if(cbFz_DLK.isSelected()) cm.fahrzeuge.add("DLK");
            
            hauptlager.put(cm.name, oldLag);
            
            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Erfolgreich aktualisiert!");
            d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void oeffneEinsatzErsteller() {
        JDialog d = createFramelessDialog("Einsatz-Vorlagen Ersteller", 600, 500);
        d.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> artBox = new JComboBox<>(new String[]{"FW", "RD", "KTP"});
        JTextField stichwortField = new JTextField();
        JTextField txtDesc = new JTextField();
        JSpinner sELW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sHLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sDLK = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sRTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sNEF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sKTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JCheckBox cbNach = new JCheckBox("Nachforderung moeglich?");
        JSpinner sProb = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
        JSpinner sMinLevel = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        mainPanel.add(new JLabel("Art:")); mainPanel.add(artBox);
        mainPanel.add(new JLabel("Stichwort (z.B. F1, R1):")); mainPanel.add(stichwortField);
        mainPanel.add(new JLabel("Beschreibung (Einsatzname):")); mainPanel.add(txtDesc);
        mainPanel.add(new JLabel("Benoetigte ELW:")); mainPanel.add(sELW);
        mainPanel.add(new JLabel("Benoetigte HLF:")); mainPanel.add(sHLF);
        mainPanel.add(new JLabel("Benoetigte DLK:")); mainPanel.add(sDLK);
        mainPanel.add(new JLabel("Benoetigte RTW:")); mainPanel.add(sRTW);
        mainPanel.add(new JLabel("Benoetigte NEF:")); mainPanel.add(sNEF);
        mainPanel.add(new JLabel("Benoetigte KTW:")); mainPanel.add(sKTW);
        mainPanel.add(new JLabel("Spawnt ab Level:")); mainPanel.add(sMinLevel);

        JPanel bottomForm = new JPanel(new GridLayout(2, 2, 5, 5));
        bottomForm.setBorder(BorderFactory.createTitledBorder("Nachforderung:"));
        bottomForm.add(cbNach); 
        bottomForm.add(new JLabel("Wahrscheinlichkeit (%):")); bottomForm.add(sProb);
        bottomForm.add(new JLabel("Welche Fahrzeuge (Nachforderung):"));
        JPanel pnlFahrzeuge = new JPanel(new GridLayout(2, 3));
        JCheckBox cbN_RTW = new JCheckBox("RTW"); JCheckBox cbN_HLF = new JCheckBox("HLF"); 
        JCheckBox cbN_NEF = new JCheckBox("NEF"); JCheckBox cbN_KTW = new JCheckBox("KTW"); 
        JCheckBox cbN_ELW = new JCheckBox("ELW"); JCheckBox cbN_DLK = new JCheckBox("DLK");
        pnlFahrzeuge.add(cbN_RTW); pnlFahrzeuge.add(cbN_HLF); pnlFahrzeuge.add(cbN_NEF); pnlFahrzeuge.add(cbN_KTW); pnlFahrzeuge.add(cbN_ELW); pnlFahrzeuge.add(cbN_DLK);
        bottomForm.add(pnlFahrzeuge);

        d.add(mainPanel, BorderLayout.NORTH);
        d.add(bottomForm, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Vorlage Speichern");
        btnAdd.addActionListener(e -> {
            String nTyp = "";
            if (cbN_RTW.isSelected()) nTyp += "RTW & "; if (cbN_HLF.isSelected()) nTyp += "HLF & ";
            if (cbN_NEF.isSelected()) nTyp += "NEF & "; if (cbN_KTW.isSelected()) nTyp += "KTW & ";
            if (cbN_ELW.isSelected()) nTyp += "ELW & "; if (cbN_DLK.isSelected()) nTyp += "DLK & ";
            if (!nTyp.isEmpty()) nTyp = nTyp.substring(0, nTyp.length() - 3);

            if(stichwortField.getText().trim().isEmpty() || txtDesc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(d, "Bitte alle Felder ausfuellen!"); return;
            }

            EinsatzVorlage v = new EinsatzVorlage((String)artBox.getSelectedItem(), stichwortField.getText().replaceAll("[^a-zA-Z0-9 ]", ""), txtDesc.getText(), 
                    (int)sRTW.getValue(), (int)sNEF.getValue(), (int)sKTW.getValue(), 
                    (int)sHLF.getValue(), (int)sDLK.getValue(), (int)sELW.getValue(), 
                    cbNach.isSelected(), (int)sProb.getValue(), nTyp, (int)sMinLevel.getValue());
            vorlagenPool.add(v);
            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Einsatz-Vorlage erfolgreich hinzugefuegt!");
            d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void oeffneEinsatzBearbeiter() {
        if(vorlagenPool.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Einsatzvorlagen!"); return; }
        JDialog d = createFramelessDialog("Einsatz-Vorlagen Bearbeiten", 600, 550);
        d.setLayout(new BorderLayout());

        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topSelect.add(new JLabel("Vorlage waehlen:"));
        JComboBox<String> cWahl = new JComboBox<>();
        for(EinsatzVorlage v : vorlagenPool) cWahl.addItem("[" + v.stichwort + "] " + v.beschreibung);
        topSelect.add(cWahl);
        d.add(topSelect, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(10, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> artBox = new JComboBox<>(new String[]{"FW", "RD", "KTP"});
        JTextField stichwortField = new JTextField();
        JTextField txtDesc = new JTextField();
        JSpinner sELW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sHLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sDLK = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sRTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sNEF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sKTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JCheckBox cbNach = new JCheckBox("Nachforderung moeglich?");
        JSpinner sProb = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5));
        JSpinner sMinLevel = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        mainPanel.add(new JLabel("Art:")); mainPanel.add(artBox);
        mainPanel.add(new JLabel("Stichwort (z.B. F1, R1):")); mainPanel.add(stichwortField);
        mainPanel.add(new JLabel("Beschreibung (Einsatzname):")); mainPanel.add(txtDesc);
        mainPanel.add(new JLabel("Benoetigte ELW:")); mainPanel.add(sELW);
        mainPanel.add(new JLabel("Benoetigte HLF:")); mainPanel.add(sHLF);
        mainPanel.add(new JLabel("Benoetigte DLK:")); mainPanel.add(sDLK);
        mainPanel.add(new JLabel("Benoetigte RTW:")); mainPanel.add(sRTW);
        mainPanel.add(new JLabel("Benoetigte NEF:")); mainPanel.add(sNEF);
        mainPanel.add(new JLabel("Benoetigte KTW:")); mainPanel.add(sKTW);
        mainPanel.add(new JLabel("Spawnt ab Level:")); mainPanel.add(sMinLevel);

        JPanel bottomForm = new JPanel(new GridLayout(2, 2, 5, 5));
        bottomForm.setBorder(BorderFactory.createTitledBorder("Nachforderung:"));
        bottomForm.add(cbNach); 
        bottomForm.add(new JLabel("Wahrscheinlichkeit (%):")); bottomForm.add(sProb);
        bottomForm.add(new JLabel("Welche Fahrzeuge (Nachforderung):"));
        JPanel pnlFahrzeuge = new JPanel(new GridLayout(2, 3));
        JCheckBox cbN_RTW = new JCheckBox("RTW"); JCheckBox cbN_HLF = new JCheckBox("HLF"); 
        JCheckBox cbN_NEF = new JCheckBox("NEF"); JCheckBox cbN_KTW = new JCheckBox("KTW"); 
        JCheckBox cbN_ELW = new JCheckBox("ELW"); JCheckBox cbN_DLK = new JCheckBox("DLK");
        pnlFahrzeuge.add(cbN_RTW); pnlFahrzeuge.add(cbN_HLF); pnlFahrzeuge.add(cbN_NEF); pnlFahrzeuge.add(cbN_KTW); pnlFahrzeuge.add(cbN_ELW); pnlFahrzeuge.add(cbN_DLK);
        bottomForm.add(pnlFahrzeuge);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(mainPanel, BorderLayout.NORTH);
        centerPanel.add(bottomForm, BorderLayout.CENTER);
        d.add(centerPanel, BorderLayout.CENTER);

        Runnable ladeEinsatz = () -> {
            EinsatzVorlage v = vorlagenPool.get(cWahl.getSelectedIndex());
            artBox.setSelectedItem(v.art);
            stichwortField.setText(v.stichwort);
            txtDesc.setText(v.beschreibung);
            sELW.setValue(v.reqELW); sHLF.setValue(v.reqHLF); sDLK.setValue(v.reqDLK);
            sRTW.setValue(v.reqRTW); sNEF.setValue(v.reqNEF); sKTW.setValue(v.reqKTW);
            cbNach.setSelected(v.hatNachforderung);
            sProb.setValue(v.nachforderungProzent);
            sMinLevel.setValue(v.minLevel);
            
            cbN_RTW.setSelected(v.nachforderungTyp.contains("RTW"));
            cbN_HLF.setSelected(v.nachforderungTyp.contains("HLF"));
            cbN_NEF.setSelected(v.nachforderungTyp.contains("NEF"));
            cbN_KTW.setSelected(v.nachforderungTyp.contains("KTW"));
            cbN_ELW.setSelected(v.nachforderungTyp.contains("ELW"));
            cbN_DLK.setSelected(v.nachforderungTyp.contains("DLK"));
        };
        ladeEinsatz.run();
        cWahl.addActionListener(e -> ladeEinsatz.run());

        JButton btnAdd = new JButton("Aenderungen Speichern");
        btnAdd.addActionListener(e -> {
            String nTyp = "";
            if (cbN_RTW.isSelected()) nTyp += "RTW & "; if (cbN_HLF.isSelected()) nTyp += "HLF & ";
            if (cbN_NEF.isSelected()) nTyp += "NEF & "; if (cbN_KTW.isSelected()) nTyp += "KTW & ";
            if (cbN_ELW.isSelected()) nTyp += "ELW & "; if (cbN_DLK.isSelected()) nTyp += "DLK & ";
            if (!nTyp.isEmpty()) nTyp = nTyp.substring(0, nTyp.length() - 3);

            if(stichwortField.getText().trim().isEmpty() || txtDesc.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(d, "Bitte alle Felder ausfuellen!"); return;
            }

            EinsatzVorlage v = vorlagenPool.get(cWahl.getSelectedIndex());
            v.art = (String)artBox.getSelectedItem();
            v.stichwort = stichwortField.getText().replaceAll("[^a-zA-Z0-9 ]", "");
            v.beschreibung = txtDesc.getText();
            v.reqELW = (int)sELW.getValue(); v.reqHLF = (int)sHLF.getValue(); v.reqDLK = (int)sDLK.getValue();
            v.reqRTW = (int)sRTW.getValue(); v.reqNEF = (int)sNEF.getValue(); v.reqKTW = (int)sKTW.getValue();
            v.hatNachforderung = cbNach.isSelected(); v.nachforderungProzent = (int)sProb.getValue();
            v.nachforderungTyp = nTyp; v.minLevel = (int)sMinLevel.getValue();

            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Vorlage erfolgreich aktualisiert!");
            d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void oeffneNachforderungMenu() {
        JDialog d = createFramelessDialog("Offene Nachforderungen", 500, 300); 
        d.setLayout(new BorderLayout(10, 10));
        
        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        boolean found = false;
        for (Einsatz ein : aktiveEinsaetze) {
            if (ein.lagemeldungAbgegeben && !ein.nachforderungBedient && !ein.nachforderungTyp.isEmpty()) {
                found = true; 
                JButton btn = new JButton(ein.beschreibung + " (" + ein.alarmUhrzeit + ") -> Benoetigt: " + ein.nachforderungTyp); 
                btn.setBackground(new Color(211, 84, 0));
                btn.addActionListener(e -> {
                    String[] reqs = ein.nachforderungTyp.split(" & "); int fehlend = 0; ArrayList<Fahrzeug> gefundene = new ArrayList<>();
                    for (String t : reqs) { 
                        String typStr = t.trim();
                        Fahrzeug f = null;
                        for(Wache w : wachen) { 
                            for(Fahrzeug testF : w.fuhrpark) if (testF.typ.equals(typStr) && (testF.status == 1 || testF.status == 2) && !gefundene.contains(testF)) { f = testF; break; }
                            if(f != null) break; 
                        }
                        
                        // RTW kann fuer KTW nachgefordert werden
                        if (f == null && typStr.equals("KTW")) {
                            for(Wache w : wachen) { 
                                for(Fahrzeug testF : w.fuhrpark) if (testF.typ.equals("RTW") && (testF.status == 1 || testF.status == 2) && !gefundene.contains(testF)) { f = testF; break; }
                                if(f != null) break; 
                            }
                        }
                        
                        if (f != null) gefundene.add(f); else fehlend++; 
                    }
                    
                    if (fehlend > 0) {
                        int wahl = JOptionPane.showConfirmDialog(d, "Es fehlen " + fehlend + " Fahrzeuge! Kreis alarmieren?", "Ueberlandhilfe", JOptionPane.YES_NO_OPTION);
                        if (wahl == JOptionPane.YES_OPTION && budget >= (fehlend*500)) { budget -= (fehlend*500); ein.nachforderungBedient = true; } 
                        else if (wahl == JOptionPane.YES_OPTION) { JOptionPane.showMessageDialog(d, "Zu wenig Geld!", "Fehler", JOptionPane.ERROR_MESSAGE); return; } 
                        else { return; }
                    } else { ein.nachforderungBedient = true; }
                    
                    if (ein.nachforderungBedient) { 
                        int multiplier = isRushHour() ? 3 : 1;
                        for (Fahrzeug f : gefundene) { 
                            int baseTime = 30; 
                            for(Wache wCheck : wachen) {
                                for(Personal p : wCheck.personalPool) { 
                                    if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) { 
                                        baseTime = 60; break; 
                                    } 
                                }
                            }
                            f.status = 3; f.anfahrtsZeit = baseTime * multiplier; f.aktuellerEinsatz = ein; 
                        } 
                    } 
                    uiAktualisieren(getUhrzeit()); d.dispose();
                }); 
                centerPanel.add(btn);
            }
        }
        
        if (!found) {
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(new JLabel("Keine offenen Nachforderungen.", SwingConstants.CENTER), BorderLayout.CENTER);
        }
        
        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        d.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnClose = new JButton("Schliessen"); 
        btnClose.addActionListener(e -> d.dispose()); 
        bottomPanel.add(btnClose); 
        d.add(bottomPanel, BorderLayout.SOUTH);
        
        d.setVisible(true);
    }

    public static void oeffneLogistikMenu() {
        JDialog d = createFramelessDialog("Wache versorgen", 400, 300);
        d.setLayout(new GridLayout(0, 1, 5, 5));

        d.add(new JLabel("Zu beliefernde Wache auswaehlen:"));
        JComboBox<String> wahlen = new JComboBox<>();
        for(Wache w : wachen) wahlen.addItem(w.name);
        d.add(wahlen);

        for(CustomMaterial cm : customMaterials) {
            JButton btn = new JButton("10x " + cm.name + " umlagern");
            btn.addActionListener(e -> {
                Wache target = wachen.get(wahlen.getSelectedIndex());
                if (hauptlager.getOrDefault(cm.name, 0) >= 10) {
                    hauptlager.put(cm.name, hauptlager.get(cm.name) - 10);
                    target.material.put(cm.name, target.material.getOrDefault(cm.name, 0) + 10);
                    uiAktualisieren(getUhrzeit());
                } else {
                    JOptionPane.showMessageDialog(d, "Zu wenig Bestand im Hauptlager!");
                }
            });
            d.add(btn);
        }
        
        JScrollPane scroll = new JScrollPane(d.getContentPane());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        d.setContentPane(scroll);

        d.setVisible(true);
    }

    public static void oeffneBestellMenu() {
        JDialog d = createFramelessDialog("Einkauf (Lieferung in 60s)", 400, 300);
        d.setLayout(new GridLayout(0, 1, 5, 5));

        for(int i=0; i<customMaterials.size(); i++) {
            CustomMaterial cm = customMaterials.get(i);
            double rabatt = techGrossabnehmer ? 0.8 : 1.0;
            int endPreis = (int)(cm.preis * rabatt);
            JButton btn = new JButton(cm.bestellMenge + "x " + cm.name + " (" + endPreis + " EURO)");
            btn.addActionListener(e -> {
                if (budget >= endPreis) {
                    budget -= endPreis;
                    lieferungen.add(new Bestellung(cm.name, cm.bestellMenge, 60));
                    uiAktualisieren(getUhrzeit());
                } else {
                    JOptionPane.showMessageDialog(d, "Nicht genug Budget!");
                }
            });
            d.add(btn);
        }

        JScrollPane scroll = new JScrollPane(d.getContentPane());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        d.setContentPane(scroll);

        d.setVisible(true);
    }

    public static void oeffneEinsatzDetails(Einsatz ein) {
        JDialog d = createFramelessDialog("Einsatzakte: " + ein.vorlage.stichwort, 500, 400);
        d.setLayout(new BorderLayout());

        JTextArea txtLage = new JTextArea(ein.getLagemeldungText());
        txtLage.setEditable(false);
        txtLage.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLage.setMargin(new Insets(10, 10, 10, 10));
        d.add(new JScrollPane(txtLage), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnBeenden = new JButton("Einsatz sofort abbrechen (-250 XP)");
        btnBeenden.setBackground(Color.RED);
        btnBeenden.setForeground(Color.WHITE);
        btnBeenden.addActionListener(e -> {
            ein.bereitZumLoeschen = true;
            xp -= 250;
            abgelehnteEinsaetzeHeute++;
            uiAktualisieren(getUhrzeit());
            d.dispose();
        });
        btnPanel.add(btnBeenden);

        d.add(btnPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }
}