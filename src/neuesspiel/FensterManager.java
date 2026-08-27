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

    public static JDialog hotkeyPopup = null;
    public static java.util.function.IntConsumer currentHotkeySetter = null;
    public static JButton currentHotkeyBtn = null;

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
        JDialog d = createFramelessDialog("Personalwesen", 400, 350);
        JPanel content = new JPanel(new GridLayout(6, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JButton b1 = new JButton("Dienstplan / Schichten"); b1.addActionListener(e -> { d.dispose(); Schichtplaner.oeffneSchichtplan(); });
        JButton b2 = new JButton("Mitarbeiter Verwaltung"); b2.addActionListener(e -> { d.dispose(); oeffneMitarbeiterVerwaltung(); });
        JButton b3 = new JButton("Personal einstellen (500 EURO)"); b3.addActionListener(e -> { d.dispose(); personalEinstellen(); });
        JButton b4 = new JButton("Personal weiterbilden"); b4.addActionListener(e -> { d.dispose(); oeffnePersonalWeiterbildung(); });
        JButton b5 = new JButton("Leihkraft anfordern (250 EURO)"); b5.addActionListener(e -> { d.dispose(); leihkraftAnfordern(); });
        JButton b6 = new JButton("Personal umstationieren"); b6.addActionListener(e -> { d.dispose(); oeffnePersonalTransfer(); });

        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); content.add(b6);
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

        JButton b1 = new JButton("Fahrzeuge verwalten / kaufen"); b1.addActionListener(e -> { d.dispose(); oeffneFuhrpark(); });
        JButton b2 = new JButton("Beschaedigtes Fahrzeug reparieren"); b2.addActionListener(e -> { d.dispose(); fahrzeugeReparieren(); });
        JButton b3 = new JButton("Fahrzeug umstationieren"); b3.addActionListener(e -> { d.dispose(); oeffneFahrzeugTransfer(); });
        JButton b4 = new JButton("TÜV & Inspektion durchfuehren"); b4.addActionListener(e -> { d.dispose(); LogistikSimulator.fahrzeugeInspektion(); });

        content.add(b1); content.add(b2); content.add(b3); content.add(b4);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneSystemHauptmenu() {
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
        JButton btnVertragEditor = new JButton("Vertrags-Editor"); btnVertragEditor.addActionListener(e -> { d.dispose(); oeffneVertragsEditor(); });
        
        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); content.add(b6); content.add(b7); content.add(btnVertragEditor);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneMitarbeiterVerwaltung() {
        JDialog d = createFramelessDialog("Mitarbeiter Historie", 1000, 500);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(new Color(35,35,35));

        String[] columns = {"Name", "Personalnummer", "Wache", "Schichten (Monat)", "Qualifikationen", "Eigenschaften", "Ereignisse"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };

        for (Wache w : wachen) {
            for (Personal p : w.personalPool) {
                String ereignis = "Keine Ereignisse";
                if (p.krankBis != -1) ereignis = "Krank bis " + getShortDatumString(p.krankBis);
                else if (p.urlaubStart != -1) ereignis = "Urlaub: " + getShortDatumString(p.urlaubStart) + " - " + getShortDatumString(p.urlaubEnd);
                
                StringBuilder eig = new StringBuilder();
                if (p.eigenschaften != null && !p.eigenschaften.isEmpty()) {
                    for(MitarbeiterEigenschaft e : p.eigenschaften) eig.append(e.name).append(", ");
                    eig.setLength(eig.length() - 2); 
                } else {
                    eig.append("Keine");
                }
                model.addRow(new Object[]{ p.name, p.getPersonalNummer(), w.name, p.schichtenMonat, String.join(", ", p.qualifikationen), eig.toString(), ereignis });
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
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(150); 
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
                    if(!mail.person.qualifikationen.contains(vorhandenerLehrgang)) mail.person.qualifikationen.add(vorhandenerLehrgang);
                }
                mail.typ = "Info"; mail.betreff = "[Anerkannt] " + mail.betreff;
                JOptionPane.showMessageDialog(d, "Vorwissen anerkannt!");
                uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
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
                        mail.person.status = "Lehrgang"; mail.person.geplanterStatus = "Lehrgang";
                        mail.person.lehrgangDauerSec = 3 * 60; mail.person.lehrgangThema = wunschLehrgang;
                        mail.typ = "Info"; mail.betreff = "[Bezahlt] " + mail.betreff;
                        JOptionPane.showMessageDialog(d, "Lehrgang bezahlt! Mitarbeiter ist unterwegs.");
                        uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
                    } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget (" + kosten + "EURO benoetigt)!"); }
                }
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
        btnTM.addActionListener(anwaerterAction); btnRS.addActionListener(anwaerterAction);

        btnGenehmigen.addActionListener(e -> {
            int idx = postfach.size() - 1 - list.getSelectedIndex();
            Email mail = postfach.get(idx);
            if (mail.person != null) {
                mail.person.urlaubStart = mail.startTag; mail.person.urlaubEnd = mail.endTag;
                mail.person.geplanterStatus = "Bereit";
                
                // NEU: Trägt den Urlaub sofort fest in den Dienstplan ein!
                for (int t = mail.startTag; t <= mail.endTag; t++) {
                    java.time.LocalDate date = java.time.LocalDate.of(2026, 6, 1).plusDays(t - 1);
                    java.time.LocalDate heute = LogistikSimulator.getCurrentDate();
                    int dIndex = date.getDayOfMonth() - 1;
                    
                    if (date.getMonthValue() == heute.getMonthValue() && date.getYear() == heute.getYear()) {
                        mail.person.planAktuellerMonat[dIndex] = "Urlaub";
                    } else {
                        mail.person.planNaechsterMonat[dIndex] = "Urlaub";
                    }
                }
                
                mail.typ = "Info"; mail.betreff = "[Genehmigt] " + mail.betreff;
                JOptionPane.showMessageDialog(d, "Urlaub eingetragen und im Dienstplan vermerkt!");
                uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
            }
        });

        btnAblehnen.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if(idx != -1) { 
                Email mail = postfach.get(idx); 
                if (mail.typ.equals("Urlaub") && cfgKrankheit) {
                    if (Math.random() < 0.025) {
                        int dauer = 2 + (int)(Math.random() * 4);
                        mail.person.krankBis = tag + dauer;
                        if (tag == mail.person.krankBis - dauer) { mail.person.status = "Krank"; mail.person.zugewiesenesFahrzeug = "Keines"; }
                        
                        // NEU: Trägt die Krankmeldung aus Frust in den Dienstplan ein
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

        JButton btnClose = new JButton("Schliessen");
        btnClose.addActionListener(e -> d.dispose());

        pnlBtns.add(btnGenehmigen); pnlBtns.add(btnTM); pnlBtns.add(btnRS); pnlBtns.add(btnLehrgang); 
        pnlBtns.add(btnAnerkennen); pnlBtns.add(btnAblehnen); pnlBtns.add(btnLoeschen); pnlBtns.add(btnClose);
        
        d.add(pnlBtns, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), new JScrollPane(txt));
        splitPane.setDividerLocation(300);
        d.add(splitPane, BorderLayout.CENTER);

        d.setVisible(true);
    }

    public static void oeffneBank() {
        JDialog d = createFramelessDialog("Bank & Vertraege", 450, 450);
        JPanel content = new JPanel(new GridLayout(7, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JLabel lblInfo = new JLabel("Aktuelle Schulden: " + LogistikSimulator.aktuellerKredit + " EUR", SwingConstants.CENTER);
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        content.add(lblInfo);

        JButton btnKredit1 = LogistikSimulator.createStyledButton("Kleinkredit (10.000 EUR)", new Color(41, 128, 185));
        JButton btnKredit2 = LogistikSimulator.createStyledButton("Mittelstand (50.000 EUR)", new Color(39, 174, 96));
        JButton btnKredit3 = LogistikSimulator.createStyledButton("Grosskredit (100.000 EUR)", new Color(192, 57, 43));
        
        if (LogistikSimulator.level < 5) btnKredit1.setEnabled(false);
        if (LogistikSimulator.level < 15) btnKredit2.setEnabled(false);
        if (LogistikSimulator.level < 25) btnKredit3.setEnabled(false);
        if (LogistikSimulator.aktuellerKredit > 0) { btnKredit1.setEnabled(false); btnKredit2.setEnabled(false); btnKredit3.setEnabled(false); }

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
        
        content.add(btnKredit1); content.add(btnKredit2); content.add(btnKredit3); content.add(btnSondertilgung); 
        content.add(new JLabel(" ")); content.add(btnVertraege); 
        
        d.add(content, BorderLayout.CENTER);
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
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        Runnable updateList = () -> {
            listModel.clear();
            for (VertragVorlage v : LogistikSimulator.vertragsVorlagen) {
                listModel.addElement(v.auftraggeber + " - " + v.zielMenge + "x " + v.zielEinsatzArt + " (" + v.belohnungProTag + " EUR)");
            }
        };
        updateList.run(); 
        
        JList<String> list = new JList<>(listModel);
        list.setBackground(new Color(43, 43, 43)); list.setForeground(Color.WHITE); list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel pnlBtns = new JPanel(new GridLayout(1, 4, 5, 5));
        pnlBtns.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); pnlBtns.setBackground(new Color(35, 35, 35));
        
        JButton btnNeu = new JButton("Neu"); JButton btnEdit = new JButton("Bearbeiten"); JButton btnDel = new JButton("Loeschen"); JButton btnClose = new JButton("Schliessen");
        btnClose.addActionListener(e -> d.dispose());
        btnNeu.addActionListener(e -> { bearbeiteVertrag(null); updateList.run(); });
        btnEdit.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) { bearbeiteVertrag(LogistikSimulator.vertragsVorlagen.get(idx)); updateList.run(); } 
            else { JOptionPane.showMessageDialog(d, "Bitte waehle zuerst einen Vertrag aus der Liste aus!"); }
        });
        btnDel.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) { LogistikSimulator.vertragsVorlagen.remove(idx); updateList.run(); }
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
    
    public static void oeffneWachenAusbau() {
        JDialog d = createFramelessDialog("Wachen & Gebaeude", 650, 550);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));
        
        JPanel pnlLokaleWache = new JPanel(new BorderLayout(5, 5));
        pnlLokaleWache.setBackground(new Color(35, 35, 35));
        pnlLokaleWache.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Lokale Wachen-Ausbauten", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        
        JPanel pnlWahl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlWahl.setBackground(new Color(35, 35, 35));
        JLabel lblWahl = new JLabel("Wache auswaehlen:"); lblWahl.setForeground(Color.WHITE);
        JComboBox<String> cbWachen = new JComboBox<>();
        for (Wache w : wachen) cbWachen.addItem(w.name);
        pnlWahl.add(lblWahl); pnlWahl.add(cbWachen);
        pnlLokaleWache.add(pnlWahl, BorderLayout.NORTH);
        
        JPanel pnlUpgrades = new JPanel(new GridLayout(4, 1, 5, 5)); 
        pnlUpgrades.setBackground(new Color(35, 35, 35));
        
        JButton btnStufe = new JButton(); 
        btnStufe.setBackground(new Color(41, 128, 185));
        btnStufe.setForeground(Color.WHITE);
        
        JButton btnWerkstatt = new JButton();
        JButton btnRuheraum = new JButton();
        JButton btnLogistik = new JButton();
        
        pnlUpgrades.add(btnStufe);
        pnlUpgrades.add(btnWerkstatt); 
        pnlUpgrades.add(btnRuheraum); 
        pnlUpgrades.add(btnLogistik);
        pnlLokaleWache.add(pnlUpgrades, BorderLayout.CENTER);
        
        Runnable updateLocalButtons = () -> {
            int wIndex = cbWachen.getSelectedIndex();
            if (wIndex == -1) return;
            Wache target = wachen.get(wIndex);
            
            int stufe = target.stufe;
            int nextCost = stufe == 1 ? 25000 : (stufe == 2 ? 50000 : (stufe == 3 ? 100000 : 0));
            if(stufe < 4) {
                btnStufe.setText("Wache auf Stufe " + (stufe+1) + " ausbauen (" + nextCost + " EUR)");
                btnStufe.setEnabled(true);
            } else {
                btnStufe.setText("Wache auf Maximalstufe (4) ausgebaut!");
                btnStufe.setEnabled(false);
            }
            
            boolean hatW = false, hatR = false, hatL = false;
            if (target.upgrades != null) {
                for (WachenAusbau a : target.upgrades) {
                    if (a.id.equals("werkstatt")) hatW = true;
                    if (a.id.equals("ruheraum")) hatR = true;
                    if (a.id.equals("logistik")) hatL = true;
                }
            }
            
            if (hatW) { btnWerkstatt.setText("Lokale Werkstatt (Gekauft)"); btnWerkstatt.setEnabled(false); }
            else if (stufe < 2) { btnWerkstatt.setText("Lokale Werkstatt (Ab Wachen-Stufe 2)"); btnWerkstatt.setEnabled(false); }
            else { btnWerkstatt.setText("Lokale Werkstatt (10.000 EUR)"); btnWerkstatt.setEnabled(true); }
            
            if (hatR) { btnRuheraum.setText("Lokaler Ruheraum (Gekauft)"); btnRuheraum.setEnabled(false); }
            else if (stufe < 2) { btnRuheraum.setText("Lokaler Ruheraum (Ab Wachen-Stufe 2)"); btnRuheraum.setEnabled(false); }
            else { btnRuheraum.setText("Lokaler Ruheraum (15.000 EUR)"); btnRuheraum.setEnabled(true); }
            
            if (hatL) { btnLogistik.setText("Logistik-Zentrum (Gekauft)"); btnLogistik.setEnabled(false); }
            else if (stufe < 3) { btnLogistik.setText("Logistik-Zentrum (Ab Wachen-Stufe 3)"); btnLogistik.setEnabled(false); }
            else { btnLogistik.setText("Logistik-Zentrum (12.500 EUR)"); btnLogistik.setEnabled(true); }
        };
        
        updateLocalButtons.run();
        cbWachen.addActionListener(e -> updateLocalButtons.run());
        
        btnStufe.addActionListener(e -> {
            Wache target = wachen.get(cbWachen.getSelectedIndex());
            int nextCost = target.stufe == 1 ? 25000 : (target.stufe == 2 ? 50000 : 100000);
            if (budget >= nextCost) {
                budget -= nextCost;
                target.stufe++;
                SpeicherManager.speichern("savegame.properties");
                updateLocalButtons.run();
                uiAktualisieren(getUhrzeit());
            } else {
                JOptionPane.showMessageDialog(d, "Nicht genug Budget (" + nextCost + " EUR benoetigt)!");
            }
        });
        
        btnWerkstatt.addActionListener(e -> {
            if (budget >= 10000) { budget -= 10000; wachen.get(cbWachen.getSelectedIndex()).upgrades.add(new WachenAusbau("werkstatt", "Lokale Werkstatt", "Reparaturen 50% guenstiger", 10000)); SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); }
            else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); }
        });
        btnRuheraum.addActionListener(e -> {
            if (budget >= 15000) { budget -= 15000; wachen.get(cbWachen.getSelectedIndex()).upgrades.add(new WachenAusbau("ruheraum", "Lokaler Ruheraum", "Krankheitsrate sinkt", 15000)); SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); }
            else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); }
        });
        btnLogistik.addActionListener(e -> {
            if (budget >= 12500) { budget -= 12500; wachen.get(cbWachen.getSelectedIndex()).upgrades.add(new WachenAusbau("logistik", "Logistik-Zentrum", "Mehr Lagerplatz", 12500)); SpeicherManager.speichern("savegame.properties"); updateLocalButtons.run(); uiAktualisieren(getUhrzeit()); }
            else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); }
        });
        
        JPanel pnlGlobal = new JPanel(new GridLayout(5, 1, 5, 5));
        pnlGlobal.setBackground(new Color(35, 35, 35));
        pnlGlobal.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Zentrale (Leitstelle & Verwaltung)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, Color.WHITE));
        
        JButton btnGruenden = new JButton("Neue Wache gruenden (10.000 EURO) [" + wachen.size() + "/" + getMaxWachenErlaubt() + "]");
        btnGruenden.addActionListener(e -> {
            if(wachen.size() >= getMaxWachenErlaubt()) { JOptionPane.showMessageDialog(d, "Dein Level ist zu niedrig fuer eine weitere Wache!"); return; }
            if(budget >= 10000) {
                String name = JOptionPane.showInputDialog(d, "Name der neuen Wache:");
                if(name != null && !name.trim().isEmpty()) {
                    String kennung = JOptionPane.showInputDialog(d, "Zweinstellige Kennung (z.B. 46):");
                    if(kennung != null && kennung.length() == 2) {
                        budget -= 10000; wachen.add(new Wache(name, kennung)); JOptionPane.showMessageDialog(d, "Wache erfolgreich gegruendet!");
                        d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit());
                    }
                }
            } else { JOptionPane.showMessageDialog(d, "Zu wenig Geld!"); }
        });
        pnlGlobal.add(btnGruenden);
        
        JButton b3 = new JButton(techGrossabnehmer ? "Grossabnehmer (Gekauft)" : "Grossabnehmer (Rabatt im Lager) (20.000 EURO)"); b3.setEnabled(!techGrossabnehmer);
        b3.addActionListener(e -> { if(budget >= 20000) { budget -= 20000; techGrossabnehmer = true; d.dispose(); uiAktualisieren(getUhrzeit()); } });
        pnlGlobal.add(b3);
        
        int nextLevelReq = (lehrerStufe + 1) * 2;
        JButton b4 = new JButton(lehrerStufe >= 5 ? "Lehrer Max. (Stufe 5)" : "Lehrer schulen Stufe " + (lehrerStufe+1) + " (5000 EURO, ab Lvl " + nextLevelReq + ")");
        b4.setEnabled(lehrerStufe < 5 && level >= nextLevelReq);
        b4.addActionListener(e -> { if(budget >= 5000) { budget -= 5000; lehrerStufe++; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        pnlGlobal.add(b4);

        JButton b5 = new JButton();
        if(calltakerStufe == 0) { b5.setText("Calltaker Einstellen (30.000 EURO, ab Lvl 20)"); b5.setEnabled(level >= 20); } 
        else if(calltakerStufe == 1) { b5.setText("Calltaker Erweitern (20.000 EURO, ab Lvl 30)"); b5.setEnabled(level >= 30); } 
        else { b5.setText("Calltaker Maximalstufe erreicht"); b5.setEnabled(false); }
        b5.addActionListener(e -> { int cost = calltakerStufe == 0 ? 30000 : 20000; if(budget >= cost) { budget -= cost; calltakerStufe++; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        pnlGlobal.add(b5);
        
        JPanel pnlKliniken = new JPanel(new GridLayout(1, 3, 5, 5)); pnlKliniken.setBackground(new Color(35,35,35));
        JButton btnCrivitz = new JButton(techKlinikCrivitz ? "Crivitz (Gekauft)" : "Klinik Crivitz (5000)"); btnCrivitz.setEnabled(!techKlinikCrivitz && level >= 10);
        btnCrivitz.addActionListener(e -> { if(budget >= 5000) { budget -= 5000; techKlinikCrivitz = true; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        JButton btnLeezen = new JButton(techKlinikLeezen ? "Leezen (Gekauft)" : "Klinik Leezen (10000)"); btnLeezen.setEnabled(!techKlinikLeezen && level >= 20);
        btnLeezen.addActionListener(e -> { if(budget >= 10000) { budget -= 10000; techKlinikLeezen = true; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        JButton btnHagenow = new JButton(techKlinikHagenow ? "Hagenow (Gekauft)" : "Klinik Hagenow (20000)"); btnHagenow.setEnabled(!techKlinikHagenow && level >= 30);
        btnHagenow.addActionListener(e -> { if(budget >= 20000) { budget -= 20000; techKlinikHagenow = true; d.dispose(); oeffneWachenAusbau(); uiAktualisieren(getUhrzeit()); } });
        pnlKliniken.add(btnCrivitz); pnlKliniken.add(btnLeezen); pnlKliniken.add(btnHagenow);
        pnlGlobal.add(pnlKliniken);

        content.add(pnlLokaleWache, BorderLayout.NORTH);
        content.add(pnlGlobal, BorderLayout.CENTER);
        
        d.add(content, BorderLayout.CENTER);
        d.setVisible(true);
    }

    public static void oeffneKrankenhausWahl(Fahrzeug f) {
        JDialog d = createFramelessDialog("Zielklinik waehlen fuer " + f.funkrufname, 450, 350);
        JPanel content = new JPanel(new GridLayout(5, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JLabel l = new JLabel("Patient verladen. Bitte Zielklinik waehlen:", SwingConstants.CENTER); l.setForeground(Color.WHITE);
        content.add(l);

        boolean k1Voll = klinik1Abgemeldet; boolean k2Voll = klinik2Abgemeldet;

        JButton b1 = new JButton(k1Voll ? "[ABGEMELDET] Helios Kliniken Schwerin" : "Helios Kliniken Schwerin (Fahrzeit: 45s)"); b1.setEnabled(!k1Voll);
        b1.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 45; f.originalAnfahrt = 45; uiAktualisieren(getUhrzeit()); d.dispose(); });
        
        JButton b2 = new JButton(k2Voll ? "[ABGEMELDET] Unimedizin Rostock" : "Universitaetsmedizin Rostock (Fahrzeit: 120s)"); b2.setEnabled(!k2Voll);
        b2.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 120; f.originalAnfahrt = 120; uiAktualisieren(getUhrzeit()); d.dispose(); });

        content.add(b1); content.add(b2);

        if(techKlinikCrivitz) {
            JButton bc = new JButton(klinikCrivitzAbgemeldet ? "[ABGEMELDET] Klinik Crivitz" : "Krankenhaus Crivitz (Fahrzeit: 50s)"); bc.setEnabled(!klinikCrivitzAbgemeldet);
            bc.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 50; f.originalAnfahrt = 50; uiAktualisieren(getUhrzeit()); d.dispose(); });
            content.add(bc);
        }
        if(techKlinikLeezen) {
            JButton bl = new JButton(klinikLeezenAbgemeldet ? "[ABGEMELDET] Klinik Leezen" : "Krankenhaus Leezen (Fahrzeit: 60s)"); bl.setEnabled(!klinikLeezenAbgemeldet);
            bl.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 60; f.originalAnfahrt = 60; uiAktualisieren(getUhrzeit()); d.dispose(); });
            content.add(bl);
        }

        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneBettenUebersicht() {
        JDialog d = createFramelessDialog("Klinik- & Bettenuebersicht", 450, 300);
        JPanel content = new JPanel(new GridLayout(6, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JLabel title = new JLabel("Aktuelle Aufnahmekapazitaeten der Kliniken:", SwingConstants.CENTER); title.setForeground(Color.WHITE);
        content.add(title);

        JLabel lbl1 = new JLabel(klinik1Abgemeldet ? "[X] Helios Kliniken Schwerin: AUFNAHMESTOPP" : "[OK] Helios Kliniken Schwerin: Aufnahmebereit", SwingConstants.CENTER); lbl1.setForeground(klinik1Abgemeldet ? Color.RED : Color.GREEN);
        JLabel lbl2 = new JLabel(klinik2Abgemeldet ? "[X] Unimedizin Rostock: AUFNAHMESTOPP" : "[OK] Unimedizin Rostock: Aufnahmebereit", SwingConstants.CENTER); lbl2.setForeground(klinik2Abgemeldet ? Color.RED : Color.GREEN);
        content.add(lbl1); content.add(lbl2);
        
        if(techKlinikCrivitz) { JLabel lc = new JLabel(klinikCrivitzAbgemeldet ? "[X] Klinik Crivitz: AUFNAHMESTOPP" : "[OK] Klinik Crivitz: Aufnahmebereit", SwingConstants.CENTER); lc.setForeground(klinikCrivitzAbgemeldet ? Color.RED : Color.GREEN); content.add(lc); }
        if(techKlinikLeezen) { JLabel ll = new JLabel(klinikLeezenAbgemeldet ? "[X] Klinik Leezen: AUFNAHMESTOPP" : "[OK] Klinik Leezen: Aufnahmebereit", SwingConstants.CENTER); ll.setForeground(klinikLeezenAbgemeldet ? Color.RED : Color.GREEN); content.add(ll); }
        if(techKlinikHagenow) { JLabel lh = new JLabel(klinikHagenowAbgemeldet ? "[X] Klinik Hagenow: AUFNAHMESTOPP" : "[OK] Klinik Hagenow: Aufnahmebereit", SwingConstants.CENTER); lh.setForeground(klinikHagenowAbgemeldet ? Color.RED : Color.GREEN); content.add(lh); }

        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneFahrzeugTransfer() {
        if(wachen.size() < 2) { JOptionPane.showMessageDialog(frame, "Du brauchst mindestens zwei Wachen fuer einen Transfer!"); return; }
        
        JDialog d = createFramelessDialog("Fahrzeug umstationieren", 400, 200);
        JPanel content = new JPanel(new GridLayout(3, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JComboBox<String> cbFz = new JComboBox<>(); for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) cbFz.addItem(f.funkrufname + " (" + w.name + ")");
        JComboBox<String> cbZiel = new JComboBox<>(); for(Wache w : wachen) cbZiel.addItem(w.name);

        JButton btnTransfer = new JButton("Umstationieren");
        btnTransfer.addActionListener(e -> {
            int fzIndex = cbFz.getSelectedIndex(); int zielIndex = cbZiel.getSelectedIndex();
            if(fzIndex == -1 || zielIndex == -1) return;
            String fzName = cbFz.getItemAt(fzIndex).split(" ")[0];
            Fahrzeug targetFz = null; Wache currentWache = null;
            for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) if(f.funkrufname.equals(fzName)) { targetFz = f; currentWache = w; break; }
            Wache zielWache = wachen.get(zielIndex);
            
            if(currentWache == zielWache) { JOptionPane.showMessageDialog(d, "Das Fahrzeug steht bereits auf dieser Wache!"); return; }
            if(zielWache.fuhrpark.size() >= getFahrzeugLimit(zielWache.stufe)) { JOptionPane.showMessageDialog(d, "Die Zielwache ist voll! (Stufe " + zielWache.stufe + " erreicht)"); return; }
            if(targetFz.status != 1 && targetFz.status != 2 && targetFz.status != 6) { JOptionPane.showMessageDialog(d, "Fahrzeug muss auf Status 1, 2 oder 6 sein!"); return; }
            
            String alteKennung = targetFz.funkrufname;
            currentWache.fuhrpark.remove(targetFz); targetFz.funkrufname = zielWache.generiereFunkrufname(targetFz.typ); zielWache.addFahrzeug(targetFz);
            for(Personal p : currentWache.personalPool) { if(p.zugewiesenesFahrzeug.equals(alteKennung)) p.zugewiesenesFahrzeug = "Keines"; if(p.geplantesFahrzeug.equals(alteKennung)) p.geplantesFahrzeug = "Keines"; }
            
            targetFz.status = 6; targetFz.ausfallGrund = "Personal fehlt";
            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Fahrzeug umstationiert! Neuer Funkrufname: " + targetFz.funkrufname + "\nAchtung: Das Personal wurde vom Fahrzeug entfernt.");
            uiAktualisieren(getUhrzeit()); d.dispose();
        });

        JLabel l1 = new JLabel("Fahrzeug waehlen:"); l1.setForeground(Color.WHITE); content.add(l1); content.add(cbFz);
        JLabel l2 = new JLabel("Zielwache waehlen:"); l2.setForeground(Color.WHITE); content.add(l2); content.add(cbZiel);
        content.add(new JLabel("")); content.add(btnTransfer);

        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffnePersonalWeiterbildung() {
        JDialog d = createFramelessDialog("Manuelle Personal Weiterbildung", 400, 200);
        JPanel content = new JPanel(new GridLayout(3, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        ArrayList<Personal> alleMitarbeiter = new ArrayList<>(); JComboBox<String> cbPers = new JComboBox<>();
        for(Wache w : wachen) { for(Personal p : w.personalPool) { alleMitarbeiter.add(p); cbPers.addItem(p.name + " (" + w.name + ")"); } }
        if(alleMitarbeiter.isEmpty()) { JOptionPane.showMessageDialog(d, "Kein Personal vorhanden!"); return; }

        JComboBox<String> cbKurs = new JComboBox<>(new String[]{"RS (500 EURO)", "NFS (1500 EURO)", "NA (3000 EURO)", "TF (500 EURO)", "GF (1500 EURO)", "MA (1000 EURO)", "FueAs (500 EURO)", "EL (2000 EURO)"});

        JButton btnKaufen = new JButton("Weiterbildung starten");
        btnKaufen.addActionListener(e -> {
            double rabatt = 1.0 - (lehrerStufe * 0.10); 
            int pIndex = cbPers.getSelectedIndex(); String kursStr = (String) cbKurs.getSelectedItem();
            String q = kursStr.split(" ")[0]; int cost = Integer.parseInt(kursStr.split("\\(")[1].split(" ")[0]);
            Personal p = alleMitarbeiter.get(pIndex);
            
            if(p.qualifikationen.contains("Anwaerter")) { JOptionPane.showMessageDialog(d, "Anwaerter koennen erst nach ihrer ersten Schicht ausgebildet werden!"); return; }
            if(p.status.equals("Urlaub") || p.status.equals("Krank") || p.status.equals("Lehrgang")) { JOptionPane.showMessageDialog(d, "Der Mitarbeiter ist aktuell nicht verfuegbar!"); return; }
            if(p.qualifikationen.contains(q)) { JOptionPane.showMessageDialog(d, "Personal erfuellt diese Qualifikation bereits!"); return; }
            
            if(budget >= cost) {
                budget -= cost; p.status = "Lehrgang"; p.geplanterStatus = "Lehrgang"; p.lehrgangDauerSec = (int)(3 * 60 * rabatt); p.lehrgangThema = q;
                SpeicherManager.speichern("savegame.properties");
                JOptionPane.showMessageDialog(d, p.name + " ist nun fuer " + p.lehrgangDauerSec + " Sekunden auf Lehrgang zum " + q + "!");
                uiAktualisieren(getUhrzeit()); d.dispose();
            } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); }
        });

        JLabel l1 = new JLabel("Mitarbeiter waehlen:"); l1.setForeground(Color.WHITE); content.add(l1); content.add(cbPers);
        JLabel l2 = new JLabel("Lehrgang waehlen:"); l2.setForeground(Color.WHITE); content.add(l2); content.add(cbKurs);
        content.add(new JLabel("")); content.add(btnKaufen);

        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneEinstellungen() {
        JDialog d = createFramelessDialog("Spieleinstellungen", 500, 480);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(45, 45, 45)); tabs.setForeground(Color.WHITE);

        // --- TAB 1: ALLGEMEINES & SOUND ---
        JPanel pnlAllgemein = new JPanel(new GridLayout(9, 1, 5, 5)); 
        pnlAllgemein.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); pnlAllgemein.setBackground(new Color(35, 35, 35));

        JCheckBox cbKtp = new JCheckBox("Krankentransport generieren", LogistikSimulator.cfgKrankentransport);
        JCheckBox cbDmg = new JCheckBox("Beschaedigte Fahrzeuge erlauben", LogistikSimulator.cfgBeschaedigung);
        JCheckBox cbSick = new JCheckBox("Krankes Personal erlauben", LogistikSimulator.cfgKrankheit);
        JCheckBox cbAuto = new JCheckBox("Auto-Umlagerung (Lager -> Wache)", LogistikSimulator.cfgAutoTransfer);
        JCheckBox cbLogistik = new JCheckBox("Lager & Logistik System aktivieren", LogistikSimulator.cfgLogistikAktiv);
        JCheckBox[] topBoxes = {cbKtp, cbDmg, cbSick, cbAuto, cbLogistik};
        for (JCheckBox box : topBoxes) { box.setForeground(Color.WHITE); box.setBackground(new Color(35, 35, 35)); box.setFocusPainted(false); pnlAllgemein.add(box); }

        JPanel pnlS1 = new JPanel(new BorderLayout(10, 0)); pnlS1.setBackground(new Color(35, 35, 35));
        JCheckBox cbSoundNotruf = new JCheckBox("Sound: Neuer Notruf", LogistikSimulator.cfgSoundNotruf); cbSoundNotruf.setForeground(Color.WHITE); cbSoundNotruf.setBackground(new Color(35, 35, 35));
        JSlider slNotruf = new JSlider(0, 100, LogistikSimulator.volNotruf); slNotruf.setBackground(new Color(35, 35, 35));
        pnlS1.add(cbSoundNotruf, BorderLayout.WEST); pnlS1.add(slNotruf, BorderLayout.CENTER); pnlAllgemein.add(pnlS1);

        JPanel pnlS2 = new JPanel(new BorderLayout(10, 0)); pnlS2.setBackground(new Color(35, 35, 35));
        JCheckBox cbSoundStatus6 = new JCheckBox("Sound: Status 6", LogistikSimulator.cfgSoundStatus6); cbSoundStatus6.setForeground(Color.WHITE); cbSoundStatus6.setBackground(new Color(35, 35, 35));
        JSlider slStatus6 = new JSlider(0, 100, LogistikSimulator.volStatus6); slStatus6.setBackground(new Color(35, 35, 35));
        pnlS2.add(cbSoundStatus6, BorderLayout.WEST); pnlS2.add(slStatus6, BorderLayout.CENTER); pnlAllgemein.add(pnlS2);

        JPanel pnlS3 = new JPanel(new BorderLayout(10, 0)); pnlS3.setBackground(new Color(35, 35, 35));
        JCheckBox cbSoundStatus7 = new JCheckBox("Sound: Status 7", LogistikSimulator.cfgSoundStatus7); cbSoundStatus7.setForeground(Color.WHITE); cbSoundStatus7.setBackground(new Color(35, 35, 35));
        JSlider slStatus7 = new JSlider(0, 100, LogistikSimulator.volStatus7); slStatus7.setBackground(new Color(35, 35, 35));
        pnlS3.add(cbSoundStatus7, BorderLayout.WEST); pnlS3.add(slStatus7, BorderLayout.CENTER); pnlAllgemein.add(pnlS3);

        tabs.addTab("Allgemein", pnlAllgemein);

        // --- TAB 2: HOTKEYS ---
        JPanel pnlHotkeys = new JPanel(new GridLayout(10, 2, 10, 10)); // 10 Zeilen
        pnlHotkeys.setBorder(BorderFactory.createEmptyBorder(10,20,10,20)); pnlHotkeys.setBackground(new Color(35, 35, 35));

        JLabel lHk1 = new JLabel("Spiel Pausieren:", SwingConstants.RIGHT); lHk1.setForeground(Color.WHITE);
        JButton btnHk1 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyPause));
        btnHk1.addActionListener(e -> assignHotkey(btnHk1, code -> LogistikSimulator.hotkeyPause = code));
        pnlHotkeys.add(lHk1); pnlHotkeys.add(btnHk1);

        JLabel lHk2 = new JLabel("Normale Geschwindigkeit:", SwingConstants.RIGHT); lHk2.setForeground(Color.WHITE);
        JButton btnHk2 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyPlay));
        btnHk2.addActionListener(e -> assignHotkey(btnHk2, code -> LogistikSimulator.hotkeyPlay = code));
        pnlHotkeys.add(lHk2); pnlHotkeys.add(btnHk2);

        JLabel lHk3 = new JLabel("Schneller Vorlauf:", SwingConstants.RIGHT); lHk3.setForeground(Color.WHITE);
        JButton btnHk3 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyFast));
        btnHk3.addActionListener(e -> assignHotkey(btnHk3, code -> LogistikSimulator.hotkeyFast = code));
        pnlHotkeys.add(lHk3); pnlHotkeys.add(btnHk3);

        JLabel lHk4 = new JLabel("Notruf Disponieren:", SwingConstants.RIGHT); lHk4.setForeground(Color.WHITE);
        JButton btnHk4 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyDisp));
        btnHk4.addActionListener(e -> assignHotkey(btnHk4, code -> LogistikSimulator.hotkeyDisp = code));
        pnlHotkeys.add(lHk4); pnlHotkeys.add(btnHk4);

        JLabel lHk5 = new JLabel("Dienstplan oeffnen:", SwingConstants.RIGHT); lHk5.setForeground(Color.WHITE);
        JButton btnHk5 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyDienstplan));
        btnHk5.addActionListener(e -> assignHotkey(btnHk5, code -> LogistikSimulator.hotkeyDienstplan = code));
        pnlHotkeys.add(lHk5); pnlHotkeys.add(btnHk5);

        JLabel lHk6 = new JLabel("Postfach oeffnen:", SwingConstants.RIGHT); lHk6.setForeground(Color.WHITE);
        JButton btnHk6 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyPostfach));
        btnHk6.addActionListener(e -> assignHotkey(btnHk6, code -> LogistikSimulator.hotkeyPostfach = code));
        pnlHotkeys.add(lHk6); pnlHotkeys.add(btnHk6);

        JLabel lHk7 = new JLabel("Fuhrpark oeffnen:", SwingConstants.RIGHT); lHk7.setForeground(Color.WHITE);
        JButton btnHk7 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyFuhrpark));
        btnHk7.addActionListener(e -> assignHotkey(btnHk7, code -> LogistikSimulator.hotkeyFuhrpark = code));
        pnlHotkeys.add(lHk7); pnlHotkeys.add(btnHk7);

        JLabel lHk8 = new JLabel("Einsatz-Ersteller:", SwingConstants.RIGHT); lHk8.setForeground(Color.WHITE);
        JButton btnHk8 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyEinsatzErsteller));
        btnHk8.addActionListener(e -> assignHotkey(btnHk8, code -> LogistikSimulator.hotkeyEinsatzErsteller = code));
        pnlHotkeys.add(lHk8); pnlHotkeys.add(btnHk8);

        JLabel lHk9 = new JLabel("Einsatz-Editor:", SwingConstants.RIGHT); lHk9.setForeground(Color.WHITE);
        JButton btnHk9 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyEinsatzEditor));
        btnHk9.addActionListener(e -> assignHotkey(btnHk9, code -> LogistikSimulator.hotkeyEinsatzEditor = code));
        pnlHotkeys.add(lHk9); pnlHotkeys.add(btnHk9);

        JLabel lHk10 = new JLabel("Personal einstellen:", SwingConstants.RIGHT); lHk10.setForeground(Color.WHITE);
        JButton btnHk10 = new JButton(java.awt.event.KeyEvent.getKeyText(LogistikSimulator.hotkeyPersonalEinstellen));
        btnHk10.addActionListener(e -> assignHotkey(btnHk10, code -> LogistikSimulator.hotkeyPersonalEinstellen = code));
        pnlHotkeys.add(lHk10); pnlHotkeys.add(btnHk10);

        JScrollPane scrollHotkeys = new JScrollPane(pnlHotkeys);
        scrollHotkeys.setBorder(BorderFactory.createEmptyBorder());
        scrollHotkeys.getVerticalScrollBar().setUnitIncrement(16);
        tabs.addTab("Tastatur-Kürzel", scrollHotkeys);

        // --- BUTTONS UNTEN ---
        JPanel pnlBottom = new JPanel(new GridLayout(2, 1, 5, 5)); pnlBottom.setBackground(new Color(35, 35, 35)); pnlBottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        JButton btnReset = new JButton("Spielstand zuruecksetzen"); btnReset.setBackground(new Color(192, 57, 43)); btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> {
            String wahl = JOptionPane.showInputDialog(d, "ACHTUNG: Dies setzt den Spielstand zurueck!\nZum Bestaetigen bitte exakt 'LOESCHEN' eingeben:");
            if(wahl != null && wahl.equals("LOESCHEN")) { new java.io.File("savegame.properties").delete(); LogistikSimulator.initStandardDaten(); LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); JOptionPane.showMessageDialog(d, "Spielstand wurde erfolgreich zurueckgesetzt!"); d.dispose(); }
        });
        
        // --- HIER IST DER REPARIERTE BUTTON! ---
        JButton btnSave = LogistikSimulator.createStyledButton("Speichern & Schliessen", new Color(39, 174, 96));
        btnSave.addActionListener(e -> {
            LogistikSimulator.cfgKrankentransport = cbKtp.isSelected(); LogistikSimulator.cfgBeschaedigung = cbDmg.isSelected(); LogistikSimulator.cfgKrankheit = cbSick.isSelected(); LogistikSimulator.cfgAutoTransfer = cbAuto.isSelected(); LogistikSimulator.cfgLogistikAktiv = cbLogistik.isSelected();
            LogistikSimulator.cfgSoundNotruf = cbSoundNotruf.isSelected(); LogistikSimulator.cfgSoundStatus6 = cbSoundStatus6.isSelected(); LogistikSimulator.cfgSoundStatus7 = cbSoundStatus7.isSelected();
            LogistikSimulator.volNotruf = slNotruf.getValue(); LogistikSimulator.volStatus6 = slStatus6.getValue(); LogistikSimulator.volStatus7 = slStatus7.getValue();
            SpeicherManager.speichern("savegame.properties"); uiAktualisieren(getUhrzeit()); d.dispose();
        });
        pnlBottom.add(btnReset); pnlBottom.add(btnSave);

        d.add(tabs, BorderLayout.CENTER); d.add(pnlBottom, BorderLayout.SOUTH); d.setVisible(true);
    }

    private static void assignHotkey(JButton btn, java.util.function.IntConsumer setter) {
        currentHotkeyBtn = btn;
        currentHotkeySetter = setter;
        
        hotkeyPopup = new JDialog(LogistikSimulator.frame, "Hotkey festlegen", true);
        hotkeyPopup.setUndecorated(true); 
        hotkeyPopup.setSize(350, 80); 
        hotkeyPopup.setLocationRelativeTo(LogistikSimulator.frame);
        hotkeyPopup.getContentPane().setBackground(new Color(231, 76, 60));
        
        JLabel lbl = new JLabel("Bitte druecke JETZT die neue Taste...", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE); 
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hotkeyPopup.add(lbl);
        
        hotkeyPopup.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                hotkeyPopup = null;
            }
        });
        
        hotkeyPopup.setVisible(true);
    }

    public static void oeffneFuhrpark() {
        if(wachen.isEmpty()){ JOptionPane.showMessageDialog(frame, "Du hast keine Wache!"); return; }
        JDialog d = createFramelessDialog("Fuhrpark verwalten", 400, 350);
        JPanel content = new JPanel(new GridLayout(9, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JLabel l = new JLabel("Fuer welche Wache?"); l.setForeground(Color.WHITE); content.add(l);
        JComboBox<String> cbWachen = new JComboBox<>(); for(Wache w : wachen) cbWachen.addItem(w.name); content.add(cbWachen);

        JButton b1 = new JButton("ELW kaufen (1500 EURO)"); b1.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "ELW", 1500));
        JButton b2 = new JButton("HLF kaufen (3000 EURO)"); b2.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "HLF", 3000));
        JButton b3 = new JButton("DLK kaufen (5000 EURO)"); b3.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "DLK", 5000));
        JButton b4 = new JButton("RTW kaufen (2000 EURO)"); b4.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "RTW", 2000));
        JButton b5 = new JButton("NEF kaufen (2500 EURO)"); b5.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "NEF", 2500));
        JButton b6 = new JButton("KTW kaufen (1000 EURO)"); b6.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "KTW", 1000));
        JButton b7 = new JButton("TLF kaufen (2500 EURO)"); b7.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "TLF", 2500));
        JButton b8 = new JButton("MTW kaufen (1000 EURO)"); b8.addActionListener(e -> kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "MTW", 1000));

        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); content.add(b6); content.add(b7); content.add(b8);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneLogistikMenu() {
        JDialog d = createFramelessDialog("Wache versorgen", 400, 300);
        JPanel content = new JPanel(new GridLayout(0, 1, 5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JLabel l = new JLabel("Zu beliefernde Wache auswaehlen:"); l.setForeground(Color.WHITE); content.add(l);
        JComboBox<String> wahlen = new JComboBox<>(); for(Wache w : wachen) wahlen.addItem(w.name); content.add(wahlen);

        for(CustomMaterial cm : customMaterials) {
            JButton btn = new JButton("10x " + cm.name + " umlagern");
            btn.addActionListener(e -> {
                Wache target = wachen.get(wahlen.getSelectedIndex());
                if (hauptlager.getOrDefault(cm.name, 0) >= 10) { hauptlager.put(cm.name, hauptlager.get(cm.name) - 10); target.material.put(cm.name, target.material.getOrDefault(cm.name, 0) + 10); SpeicherManager.speichern("savegame.properties"); uiAktualisieren(getUhrzeit()); } 
                else { JOptionPane.showMessageDialog(d, "Zu wenig Bestand im Hauptlager!"); }
            });
            content.add(btn);
        }
        
        JScrollPane scroll = new JScrollPane(content); scroll.getVerticalScrollBar().setUnitIncrement(16); scroll.setBorder(BorderFactory.createEmptyBorder());
        d.add(scroll, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneBestellMenu() {
        JDialog d = createFramelessDialog("Einkauf (Lieferung in 60s)", 400, 300);
        JPanel content = new JPanel(new GridLayout(0, 1, 5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        for(int i=0; i<customMaterials.size(); i++) {
            CustomMaterial cm = customMaterials.get(i);
            double rabatt = techGrossabnehmer ? 0.8 : 1.0; int endPreis = (int)(cm.preis * rabatt);
            JButton btn = new JButton(cm.bestellMenge + "x " + cm.name + " (" + endPreis + " EURO)");
            btn.addActionListener(e -> {
                if (budget >= endPreis) { budget -= endPreis; lieferungen.add(new Bestellung(cm.name, cm.bestellMenge, 60)); SpeicherManager.speichern("savegame.properties"); uiAktualisieren(getUhrzeit()); } 
                else { JOptionPane.showMessageDialog(d, "Nicht genug Budget!"); }
            });
            content.add(btn);
        }

        JScrollPane scroll = new JScrollPane(content); scroll.getVerticalScrollBar().setUnitIncrement(16); scroll.setBorder(BorderFactory.createEmptyBorder());
        d.add(scroll, BorderLayout.CENTER); d.setVisible(true);
    }
    
    public static void oeffneMaterialErsteller() {
        JDialog d = createFramelessDialog("Eigenes Material Erstellen", 500, 400);
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBackground(new Color(35, 35, 35));
        
        JTextField txtName = new JTextField();
        JSpinner sVerbrauch = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner sPreis = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 10));
        JSpinner sMenge = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 10));
        JSpinner sWarn = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 5));
        
        JLabel l1 = new JLabel("Material Name:"); l1.setForeground(Color.WHITE); form.add(l1); form.add(txtName);
        JLabel l2 = new JLabel("Max. Verbrauch pro Einsatz:"); l2.setForeground(Color.WHITE); form.add(l2); form.add(sVerbrauch);
        JLabel l3 = new JLabel("Kaufpreis (Shop):"); l3.setForeground(Color.WHITE); form.add(l3); form.add(sPreis);
        JLabel l4 = new JLabel("Bestellmenge pro Kauf:"); l4.setForeground(Color.WHITE); form.add(l4); form.add(sMenge);
        JLabel l5 = new JLabel("Warnschwelle (Bestand):"); l5.setForeground(Color.WHITE); form.add(l5); form.add(sWarn);
        
        JLabel l6 = new JLabel("Fahrzeug-Bindung:"); l6.setForeground(Color.WHITE); form.add(l6);
        JPanel pnlFz = new JPanel(new GridLayout(3, 3)); pnlFz.setBackground(new Color(35, 35, 35));
        JCheckBox cbFz_RTW = new JCheckBox("RTW"); cbFz_RTW.setForeground(Color.WHITE); cbFz_RTW.setBackground(new Color(35,35,35));
        JCheckBox cbFz_HLF = new JCheckBox("HLF"); cbFz_HLF.setForeground(Color.WHITE); cbFz_HLF.setBackground(new Color(35,35,35));
        JCheckBox cbFz_NEF = new JCheckBox("NEF"); cbFz_NEF.setForeground(Color.WHITE); cbFz_NEF.setBackground(new Color(35,35,35));
        JCheckBox cbFz_KTW = new JCheckBox("KTW"); cbFz_KTW.setForeground(Color.WHITE); cbFz_KTW.setBackground(new Color(35,35,35));
        JCheckBox cbFz_ELW = new JCheckBox("ELW"); cbFz_ELW.setForeground(Color.WHITE); cbFz_ELW.setBackground(new Color(35,35,35));
        JCheckBox cbFz_DLK = new JCheckBox("DLK"); cbFz_DLK.setForeground(Color.WHITE); cbFz_DLK.setBackground(new Color(35,35,35));
        JCheckBox cbFz_TLF = new JCheckBox("TLF"); cbFz_TLF.setForeground(Color.WHITE); cbFz_TLF.setBackground(new Color(35,35,35));
        JCheckBox cbFz_MTW = new JCheckBox("MTW"); cbFz_MTW.setForeground(Color.WHITE); cbFz_MTW.setBackground(new Color(35,35,35));
        
        pnlFz.add(cbFz_RTW); pnlFz.add(cbFz_HLF); pnlFz.add(cbFz_NEF); 
        pnlFz.add(cbFz_KTW); pnlFz.add(cbFz_ELW); pnlFz.add(cbFz_DLK);
        pnlFz.add(cbFz_TLF); pnlFz.add(cbFz_MTW);
        form.add(pnlFz);

        JPanel topArea = new JPanel(new BorderLayout()); topArea.setBackground(new Color(35, 35, 35));
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
            if(cbFz_TLF.isSelected()) fzList.add("TLF"); if(cbFz_MTW.isSelected()) fzList.add("MTW");
            
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

        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT)); topSelect.setBackground(new Color(35, 35, 35));
        JLabel lblTop = new JLabel("Zu bearbeitendes Material:"); lblTop.setForeground(Color.WHITE); topSelect.add(lblTop);
        JComboBox<String> cMatWahl = new JComboBox<>();
        for(CustomMaterial cm : customMaterials) cMatWahl.addItem(cm.name);
        topSelect.add(cMatWahl);
        d.add(topSelect, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5)); form.setBackground(new Color(35, 35, 35));
        JTextField txtName = new JTextField();
        JSpinner sVerbrauch = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner sPreis = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 10));
        JSpinner sMenge = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 10));
        JSpinner sWarn = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 5));
        
        JLabel l1 = new JLabel("Material Name:"); l1.setForeground(Color.WHITE); form.add(l1); form.add(txtName);
        JLabel l2 = new JLabel("Max. Verbrauch pro Einsatz:"); l2.setForeground(Color.WHITE); form.add(l2); form.add(sVerbrauch);
        JLabel l3 = new JLabel("Kaufpreis (Shop):"); l3.setForeground(Color.WHITE); form.add(l3); form.add(sPreis);
        JLabel l4 = new JLabel("Bestellmenge pro Kauf:"); l4.setForeground(Color.WHITE); form.add(l4); form.add(sMenge);
        JLabel l5 = new JLabel("Warnschwelle (Bestand):"); l5.setForeground(Color.WHITE); form.add(l5); form.add(sWarn);
        
        JLabel l6 = new JLabel("Fahrzeug-Bindung:"); l6.setForeground(Color.WHITE); form.add(l6);
        JPanel pnlFz = new JPanel(new GridLayout(3, 3)); pnlFz.setBackground(new Color(35, 35, 35));
        JCheckBox cbFz_RTW = new JCheckBox("RTW"); cbFz_RTW.setForeground(Color.WHITE); cbFz_RTW.setBackground(new Color(35,35,35));
        JCheckBox cbFz_HLF = new JCheckBox("HLF"); cbFz_HLF.setForeground(Color.WHITE); cbFz_HLF.setBackground(new Color(35,35,35));
        JCheckBox cbFz_NEF = new JCheckBox("NEF"); cbFz_NEF.setForeground(Color.WHITE); cbFz_NEF.setBackground(new Color(35,35,35));
        JCheckBox cbFz_KTW = new JCheckBox("KTW"); cbFz_KTW.setForeground(Color.WHITE); cbFz_KTW.setBackground(new Color(35,35,35));
        JCheckBox cbFz_ELW = new JCheckBox("ELW"); cbFz_ELW.setForeground(Color.WHITE); cbFz_ELW.setBackground(new Color(35,35,35));
        JCheckBox cbFz_DLK = new JCheckBox("DLK"); cbFz_DLK.setForeground(Color.WHITE); cbFz_DLK.setBackground(new Color(35,35,35));
        JCheckBox cbFz_TLF = new JCheckBox("TLF"); cbFz_TLF.setForeground(Color.WHITE); cbFz_TLF.setBackground(new Color(35,35,35));
        JCheckBox cbFz_MTW = new JCheckBox("MTW"); cbFz_MTW.setForeground(Color.WHITE); cbFz_MTW.setBackground(new Color(35,35,35));
        
        pnlFz.add(cbFz_RTW); pnlFz.add(cbFz_HLF); pnlFz.add(cbFz_NEF); 
        pnlFz.add(cbFz_KTW); pnlFz.add(cbFz_ELW); pnlFz.add(cbFz_DLK);
        pnlFz.add(cbFz_TLF); pnlFz.add(cbFz_MTW);
        form.add(pnlFz);

        Runnable ladeMaterial = () -> {
            CustomMaterial cm = customMaterials.get(cMatWahl.getSelectedIndex());
            txtName.setText(cm.name); sVerbrauch.setValue(cm.maxVerbrauch); sPreis.setValue(cm.preis);
            sMenge.setValue(cm.bestellMenge); sWarn.setValue(cm.warnSchwelle);
            cbFz_RTW.setSelected(cm.fahrzeuge.contains("RTW")); cbFz_HLF.setSelected(cm.fahrzeuge.contains("HLF"));
            cbFz_NEF.setSelected(cm.fahrzeuge.contains("NEF")); cbFz_KTW.setSelected(cm.fahrzeuge.contains("KTW"));
            cbFz_ELW.setSelected(cm.fahrzeuge.contains("ELW")); cbFz_DLK.setSelected(cm.fahrzeuge.contains("DLK"));
            cbFz_TLF.setSelected(cm.fahrzeuge.contains("TLF")); cbFz_MTW.setSelected(cm.fahrzeuge.contains("MTW"));
        };
        ladeMaterial.run();
        cMatWahl.addActionListener(e -> ladeMaterial.run());

        JPanel topArea = new JPanel(new BorderLayout()); topArea.setBackground(new Color(35, 35, 35));
        topArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        topArea.add(form, BorderLayout.CENTER); d.add(topArea, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Aenderungen Speichern");
        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Name fehlt!"); return; }
            CustomMaterial cm = customMaterials.get(cMatWahl.getSelectedIndex());
            
            String oldWach = cm.name;
            int oldLag = hauptlager.getOrDefault(cm.name, 0); hauptlager.remove(cm.name);
            
            cm.name = txtName.getText().trim(); cm.maxVerbrauch = (int) sVerbrauch.getValue();
            cm.preis = (int) sPreis.getValue(); cm.bestellMenge = (int) sMenge.getValue(); cm.warnSchwelle = (int) sWarn.getValue();
            
            cm.fahrzeuge.clear();
            if(cbFz_RTW.isSelected()) cm.fahrzeuge.add("RTW"); if(cbFz_HLF.isSelected()) cm.fahrzeuge.add("HLF");
            if(cbFz_NEF.isSelected()) cm.fahrzeuge.add("NEF"); if(cbFz_KTW.isSelected()) cm.fahrzeuge.add("KTW");
            if(cbFz_ELW.isSelected()) cm.fahrzeuge.add("ELW"); if(cbFz_DLK.isSelected()) cm.fahrzeuge.add("DLK");
            if(cbFz_TLF.isSelected()) cm.fahrzeuge.add("TLF"); if(cbFz_MTW.isSelected()) cm.fahrzeuge.add("MTW");
            
            hauptlager.put(cm.name, oldLag);
            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Erfolgreich aktualisiert!"); d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneEinsatzErsteller() {
        JDialog d = createFramelessDialog("Einsatz-Vorlagen Ersteller", 600, 550);
        JPanel mainPanel = new JPanel(new GridLayout(12, 2, 5, 5)); 
        mainPanel.setBackground(new Color(35, 35, 35));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> artBox = new JComboBox<>(new String[]{"FW", "RD", "KTP"});
        JTextField stichwortField = new JTextField(); JTextField txtDesc = new JTextField();
        JSpinner sELW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sHLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sDLK = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sRTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sNEF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sKTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sTLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sMTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        
        JCheckBox cbNach = new JCheckBox("Nachforderung moeglich?"); cbNach.setForeground(Color.WHITE); cbNach.setBackground(new Color(35,35,35));
        JSpinner sProb = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5)); JSpinner sMinLevel = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        JLabel l1 = new JLabel("Art:"); l1.setForeground(Color.WHITE); mainPanel.add(l1); mainPanel.add(artBox);
        JLabel l2 = new JLabel("Stichwort (z.B. F1, R1):"); l2.setForeground(Color.WHITE); mainPanel.add(l2); mainPanel.add(stichwortField);
        JLabel l3 = new JLabel("Beschreibung (Einsatzname):"); l3.setForeground(Color.WHITE); mainPanel.add(l3); mainPanel.add(txtDesc);
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
        bottomForm.add(cbNach); 
        JLabel l11 = new JLabel("Wahrscheinlichkeit (%):"); l11.setForeground(Color.WHITE); bottomForm.add(l11); bottomForm.add(sProb);
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
        
        pnlFahrzeuge.add(cbN_RTW); pnlFahrzeuge.add(cbN_HLF); pnlFahrzeuge.add(cbN_NEF); 
        pnlFahrzeuge.add(cbN_KTW); pnlFahrzeuge.add(cbN_ELW); pnlFahrzeuge.add(cbN_DLK);
        pnlFahrzeuge.add(cbN_TLF); pnlFahrzeuge.add(cbN_MTW);
        bottomForm.add(pnlFahrzeuge);

        d.add(mainPanel, BorderLayout.NORTH); d.add(bottomForm, BorderLayout.CENTER);

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
                    (int)sRTW.getValue(), (int)sNEF.getValue(), (int)sKTW.getValue(), 
                    (int)sHLF.getValue(), (int)sDLK.getValue(), (int)sELW.getValue(), (int)sTLF.getValue(), (int)sMTW.getValue(),
                    cbNach.isSelected(), (int)sProb.getValue(), nTyp, (int)sMinLevel.getValue());
            vorlagenPool.add(v); SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Einsatz-Vorlage erfolgreich hinzugefuegt!"); d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneEinsatzBearbeiter() {
        if(vorlagenPool.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Einsatzvorlagen!"); return; }
        JDialog d = createFramelessDialog("Einsatz-Vorlagen Bearbeiten", 600, 600);

        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT)); topSelect.setBackground(new Color(35, 35, 35));
        JLabel lblTop = new JLabel("Vorlage waehlen:"); lblTop.setForeground(Color.WHITE); topSelect.add(lblTop);
        JComboBox<String> cWahl = new JComboBox<>();
        for(EinsatzVorlage v : vorlagenPool) cWahl.addItem("[" + v.stichwort + "] " + v.beschreibung);
        topSelect.add(cWahl); d.add(topSelect, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(12, 2, 5, 5)); mainPanel.setBackground(new Color(35, 35, 35));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> artBox = new JComboBox<>(new String[]{"FW", "RD", "KTP"});
        JTextField stichwortField = new JTextField(); JTextField txtDesc = new JTextField();
        JSpinner sELW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sHLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sDLK = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sRTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sNEF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sKTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner sTLF = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1)); JSpinner sMTW = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        JCheckBox cbNach = new JCheckBox("Nachforderung moeglich?"); cbNach.setForeground(Color.WHITE); cbNach.setBackground(new Color(35,35,35));
        JSpinner sProb = new JSpinner(new SpinnerNumberModel(0, 0, 100, 5)); JSpinner sMinLevel = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        JLabel l1 = new JLabel("Art:"); l1.setForeground(Color.WHITE); mainPanel.add(l1); mainPanel.add(artBox);
        JLabel l2 = new JLabel("Stichwort (z.B. F1, R1):"); l2.setForeground(Color.WHITE); mainPanel.add(l2); mainPanel.add(stichwortField);
        JLabel l3 = new JLabel("Beschreibung (Einsatzname):"); l3.setForeground(Color.WHITE); mainPanel.add(l3); mainPanel.add(txtDesc);
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
        bottomForm.add(cbNach); 
        JLabel l11 = new JLabel("Wahrscheinlichkeit (%):"); l11.setForeground(Color.WHITE); bottomForm.add(l11); bottomForm.add(sProb);
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

        pnlFahrzeuge.add(cbN_RTW); pnlFahrzeuge.add(cbN_HLF); pnlFahrzeuge.add(cbN_NEF); 
        pnlFahrzeuge.add(cbN_KTW); pnlFahrzeuge.add(cbN_ELW); pnlFahrzeuge.add(cbN_DLK);
        pnlFahrzeuge.add(cbN_TLF); pnlFahrzeuge.add(cbN_MTW);
        bottomForm.add(pnlFahrzeuge);

        JPanel centerPanel = new JPanel(new BorderLayout()); centerPanel.setBackground(new Color(35, 35, 35));
        centerPanel.add(mainPanel, BorderLayout.NORTH); centerPanel.add(bottomForm, BorderLayout.CENTER); d.add(centerPanel, BorderLayout.CENTER);

        Runnable ladeEinsatz = () -> {
            EinsatzVorlage v = vorlagenPool.get(cWahl.getSelectedIndex());
            artBox.setSelectedItem(v.art); stichwortField.setText(v.stichwort); txtDesc.setText(v.beschreibung);
            sELW.setValue(v.reqELW); sHLF.setValue(v.reqHLF); sDLK.setValue(v.reqDLK);
            sRTW.setValue(v.reqRTW); sNEF.setValue(v.reqNEF); sKTW.setValue(v.reqKTW);
            sTLF.setValue(v.reqTLF); sMTW.setValue(v.reqMTW); 
            
            cbNach.setSelected(v.hatNachforderung); sProb.setValue(v.nachforderungProzent); sMinLevel.setValue(v.minLevel);
            cbN_RTW.setSelected(v.nachforderungTyp.contains("RTW")); cbN_HLF.setSelected(v.nachforderungTyp.contains("HLF"));
            cbN_NEF.setSelected(v.nachforderungTyp.contains("NEF")); cbN_KTW.setSelected(v.nachforderungTyp.contains("KTW"));
            cbN_ELW.setSelected(v.nachforderungTyp.contains("ELW")); cbN_DLK.setSelected(v.nachforderungTyp.contains("DLK"));
            cbN_TLF.setSelected(v.nachforderungTyp.contains("TLF")); cbN_MTW.setSelected(v.nachforderungTyp.contains("MTW"));
        };
        ladeEinsatz.run(); cWahl.addActionListener(e -> ladeEinsatz.run());

        JButton btnAdd = new JButton("Aenderungen Speichern");
        btnAdd.addActionListener(e -> {
            String nTyp = "";
            if (cbN_RTW.isSelected()) nTyp += "RTW & "; if (cbN_HLF.isSelected()) nTyp += "HLF & ";
            if (cbN_NEF.isSelected()) nTyp += "NEF & "; if (cbN_KTW.isSelected()) nTyp += "KTW & ";
            if (cbN_ELW.isSelected()) nTyp += "ELW & "; if (cbN_DLK.isSelected()) nTyp += "DLK & ";
            if (cbN_TLF.isSelected()) nTyp += "TLF & "; if (cbN_MTW.isSelected()) nTyp += "MTW & ";
            if (!nTyp.isEmpty()) nTyp = nTyp.substring(0, nTyp.length() - 3);

            if(stichwortField.getText().trim().isEmpty() || txtDesc.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Bitte alle Felder ausfuellen!"); return; }

            EinsatzVorlage v = vorlagenPool.get(cWahl.getSelectedIndex());
            v.art = (String)artBox.getSelectedItem(); v.stichwort = stichwortField.getText().replaceAll("[^a-zA-Z0-9 ]", ""); v.beschreibung = txtDesc.getText();
            v.reqELW = (int)sELW.getValue(); v.reqHLF = (int)sHLF.getValue(); v.reqDLK = (int)sDLK.getValue();
            v.reqRTW = (int)sRTW.getValue(); v.reqNEF = (int)sNEF.getValue(); v.reqKTW = (int)sKTW.getValue();
            v.reqTLF = (int)sTLF.getValue(); v.reqMTW = (int)sMTW.getValue();
            
            v.hatNachforderung = cbNach.isSelected(); v.nachforderungProzent = (int)sProb.getValue(); v.nachforderungTyp = nTyp; v.minLevel = (int)sMinLevel.getValue();

            SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, "Vorlage erfolgreich aktualisiert!"); d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneNachforderungMenu() {
        JDialog d = createFramelessDialog("Offene Nachforderungen", 500, 300); 
        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 5, 5)); centerPanel.setBackground(new Color(35, 35, 35));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        boolean found = false;
        for (Einsatz ein : aktiveEinsaetze) {
            if (ein.lagemeldungAbgegeben && !ein.nachforderungBedient && !ein.nachforderungTyp.isEmpty()) {
                found = true; 
                JButton btn = new JButton(ein.beschreibung + " (" + ein.alarmUhrzeit + ") -> Benoetigt: " + ein.nachforderungTyp); 
                btn.setBackground(new Color(211, 84, 0)); btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    String[] reqs = ein.nachforderungTyp.split(" & "); int fehlend = 0; ArrayList<Fahrzeug> gefundene = new ArrayList<>();
                    for (String t : reqs) { 
                        String typStr = t.trim(); Fahrzeug f = null;
                        for(Wache w : wachen) { for(Fahrzeug testF : w.fuhrpark) if (testF.typ.equals(typStr) && (testF.status == 1 || testF.status == 2) && !gefundene.contains(testF)) { f = testF; break; } if(f != null) break; }
                        if (f == null && typStr.equals("KTW")) { for(Wache w : wachen) { for(Fahrzeug testF : w.fuhrpark) if (testF.typ.equals("RTW") && (testF.status == 1 || testF.status == 2) && !gefundene.contains(testF)) { f = testF; break; } if(f != null) break; } }
                        if (f != null) gefundene.add(f); else fehlend++; 
                    }
                    if (fehlend > 0) {
                        int wahl = JOptionPane.showConfirmDialog(d, "Es fehlen " + fehlend + " Fahrzeuge! Kreis alarmieren?", "Ueberlandhilfe", JOptionPane.YES_NO_OPTION);
                        if (wahl == JOptionPane.YES_OPTION && budget >= (fehlend*500)) { budget -= (fehlend*500); ein.nachforderungBedient = true; } 
                        else if (wahl == JOptionPane.YES_OPTION) { JOptionPane.showMessageDialog(d, "Zu wenig Geld!", "Fehler", JOptionPane.ERROR_MESSAGE); return; } else { return; }
                    } else { ein.nachforderungBedient = true; }
                    
                    if (ein.nachforderungBedient) { 
                        int multiplier = isRushHour() ? 3 : 1;
                        for (Fahrzeug f : gefundene) { 
                            int baseTime = 30; 
                            for(Wache wCheck : wachen) { for(Personal p : wCheck.personalPool) { if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) { baseTime = 60; break; } } }
                            f.status = 3; f.anfahrtsZeit = (int)(baseTime * multiplier * getSpeedMultiplier(f)); f.aktuellerEinsatz = ein; 
                        } 
                    } 
                    uiAktualisieren(getUhrzeit()); d.dispose();
                }); 
                centerPanel.add(btn);
            }
        }
        
        if (!found) {
            centerPanel.setLayout(new BorderLayout());
            JLabel lNone = new JLabel("Keine offenen Nachforderungen.", SwingConstants.CENTER); lNone.setForeground(Color.WHITE);
            centerPanel.add(lNone, BorderLayout.CENTER);
        }
        
        JScrollPane scrollPane = new JScrollPane(centerPanel); scrollPane.setBorder(BorderFactory.createEmptyBorder()); d.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); bottomPanel.setBackground(new Color(35, 35, 35));
        JButton btnClose = new JButton("Schliessen"); btnClose.addActionListener(e -> d.dispose()); bottomPanel.add(btnClose); d.add(bottomPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    public static void oeffneEinsatzDetails(Einsatz ein) {
        JDialog d = createFramelessDialog("Einsatzakte: " + ein.vorlage.stichwort, 500, 400);
        
        JTextArea txtLage = new JTextArea(ein.getLagemeldungText());
        txtLage.setEditable(false); txtLage.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLage.setMargin(new Insets(10, 10, 10, 10)); txtLage.setBackground(new Color(35, 35, 35)); txtLage.setForeground(Color.WHITE);
        d.add(new JScrollPane(txtLage), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout()); btnPanel.setBackground(new Color(35, 35, 35));
        JButton btnBeenden = new JButton("Einsatz sofort abbrechen (-250 XP)");
        btnBeenden.setBackground(new Color(192, 57, 43)); btnBeenden.setForeground(Color.WHITE);
        btnBeenden.addActionListener(e -> {
            ein.bereitZumLoeschen = true; xp -= 250; abgelehnteEinsaetzeHeute++; uiAktualisieren(getUhrzeit()); d.dispose();
        });
        btnPanel.add(btnBeenden);

        d.add(btnPanel, BorderLayout.SOUTH); d.setVisible(true);
    }
    
    public static void oeffnePersonalTransfer() {
        if(wachen.size() < 2) { JOptionPane.showMessageDialog(frame, "Du brauchst mindestens zwei Wachen fuer einen Transfer!"); return; }
        
        JDialog d = createFramelessDialog("Personal transferieren", 450, 300);
        JPanel content = new JPanel(new GridLayout(4, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35,35,35));

        JComboBox<String> cbPers = new JComboBox<>();
        ArrayList<Personal> pList = new ArrayList<>();
        ArrayList<Wache> wList = new ArrayList<>(); 
        
        for(Wache w : wachen) {
            for(Personal p : w.personalPool) {
                pList.add(p);
                wList.add(w);
                cbPers.addItem(p.name + " (" + w.name + ")");
            }
        }

        JComboBox<String> cbZiel = new JComboBox<>();
        for(Wache w : wachen) cbZiel.addItem(w.name);

        JComboBox<String> cbArt = new JComboBox<>(new String[]{"Dauerhaft (Versetzung)", "Temporaer (Bis Tagesabschluss)"});

        JButton btnTransfer = new JButton("Transferieren");
        btnTransfer.addActionListener(e -> {
            int pIndex = cbPers.getSelectedIndex();
            int zielIndex = cbZiel.getSelectedIndex();
            if(pIndex == -1 || zielIndex == -1) return;

            Personal p = pList.get(pIndex);
            Wache alteWache = wList.get(pIndex);
            Wache zielWache = wachen.get(zielIndex);

            if(alteWache == zielWache) {
                JOptionPane.showMessageDialog(d, "Die Person arbeitet bereits auf dieser Wache!"); return;
            }
            if(p.status.equals("Lehrgang")) {
                JOptionPane.showMessageDialog(d, "Mitarbeiter ist auf Lehrgang und kann nicht transferiert werden!"); return;
            }

            alteWache.personalPool.remove(p);
            zielWache.personalPool.add(p);
            
            p.zugewiesenesFahrzeug = "Keines";
            p.geplantesFahrzeug = "Keines";

            if(cbArt.getSelectedIndex() == 1) { 
                if(!LogistikSimulator.verliehenesPersonal.containsKey(p)) {
                    LogistikSimulator.verliehenesPersonal.put(p, alteWache);
                }
            } else { 
                LogistikSimulator.verliehenesPersonal.remove(p);
            }

            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, p.name + " wurde erfolgreich nach " + zielWache.name + " umstationiert.\nAchtung: Bitte teile die Person im Schichtplan neu einem Fahrzeug zu!");
            LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
            d.dispose();
        });

        JLabel l1 = new JLabel("Mitarbeiter waehlen:"); l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Ziel-Wache:"); l2.setForeground(Color.WHITE);
        JLabel l3 = new JLabel("Art des Transfers:"); l3.setForeground(Color.WHITE);

        content.add(l1); content.add(cbPers);
        content.add(l2); content.add(cbZiel);
        content.add(l3); content.add(cbArt);
        content.add(new JLabel("")); content.add(btnTransfer);

        d.add(content, BorderLayout.CENTER);
        d.setVisible(true);
    }
    
    public static void oeffneAlarmierungsFenster(Einsatz ein) {
        JDialog d = createFramelessDialog("Alarmierung: " + ein.vorlage.stichwort + " - " + ein.beschreibung, 800, 600);
        
        JPanel pnlTop = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlTop.setBackground(new Color(35, 35, 35));
        pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitel = new JLabel("NOTRUF: " + ein.vorlage.stichwort + " | " + ein.beschreibung, SwingConstants.CENTER);
        lblTitel.setForeground(new Color(231, 76, 60));
        lblTitel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTop.add(lblTitel);
        
        StringBuilder reqText = new StringBuilder("Benoetigt: ");
        if(ein.vorlage.reqELW > 0) reqText.append(ein.vorlage.reqELW).append("x ELW  ");
        if(ein.vorlage.reqHLF > 0) reqText.append(ein.vorlage.reqHLF).append("x HLF  ");
        if(ein.vorlage.reqDLK > 0) reqText.append(ein.vorlage.reqDLK).append("x DLK  ");
        if(ein.vorlage.reqRTW > 0) reqText.append(ein.vorlage.reqRTW).append("x RTW  ");
        if(ein.vorlage.reqNEF > 0) reqText.append(ein.vorlage.reqNEF).append("x NEF  ");
        if(ein.vorlage.reqKTW > 0) reqText.append(ein.vorlage.reqKTW).append("x KTW  ");
        if(ein.vorlage.reqTLF > 0) reqText.append(ein.vorlage.reqTLF).append("x TLF  ");
        if(ein.vorlage.reqMTW > 0) reqText.append(ein.vorlage.reqMTW).append("x MTW  ");
        
        JLabel lblReq = new JLabel(reqText.toString(), SwingConstants.CENTER);
        lblReq.setForeground(Color.WHITE);
        lblReq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlTop.add(lblReq);
        
        d.add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Auswaehlen", "Funkrufname", "Typ", "Wache", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class; 
            }
            @Override public boolean isCellEditable(int row, int column) {
                return column == 0; 
            }
        };

        ArrayList<Fahrzeug> verfuegbar = new ArrayList<>();
        for(Wache w : wachen) {
            for(Fahrzeug f : w.fuhrpark) {
                if(f.status == 1 || f.status == 2) {
                    verfuegbar.add(f);
                    model.addRow(new Object[]{false, f.funkrufname, f.typ, w.name, "Status " + f.status});
                }
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setBackground(new Color(43, 43, 43));
        table.setForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20, 30, 48));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        d.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlBottom.setBackground(new Color(35, 35, 35));

        JButton btnAAO = new JButton("AAO nutzen (Auto-Auswahl)");
        btnAAO.setBackground(new Color(41, 128, 185));
        btnAAO.setForeground(Color.WHITE);
        
        JButton btnAlarm = new JButton("ALARM AUSLOESEN");
        btnAlarm.setBackground(new Color(39, 174, 96));
        btnAlarm.setForeground(Color.WHITE);
        btnAlarm.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton btnAbbruch = new JButton("Abbrechen");
        btnAbbruch.addActionListener(e -> d.dispose());

        btnAAO.addActionListener(e -> {
            for(int i = 0; i < model.getRowCount(); i++) model.setValueAt(false, i, 0); 
            
            int needELW = ein.vorlage.reqELW, needHLF = ein.vorlage.reqHLF, needDLK = ein.vorlage.reqDLK;
            int needRTW = ein.vorlage.reqRTW, needNEF = ein.vorlage.reqNEF, needKTW = ein.vorlage.reqKTW;
            int needTLF = ein.vorlage.reqTLF, needMTW = ein.vorlage.reqMTW;
            
            for(int i = 0; i < verfuegbar.size(); i++) {
                Fahrzeug f = verfuegbar.get(i);
                if(f.typ.equals("ELW") && needELW > 0) { model.setValueAt(true, i, 0); needELW--; }
                else if(f.typ.equals("HLF") && needHLF > 0) { model.setValueAt(true, i, 0); needHLF--; }
                else if(f.typ.equals("DLK") && needDLK > 0) { model.setValueAt(true, i, 0); needDLK--; }
                else if(f.typ.equals("RTW") && needRTW > 0) { model.setValueAt(true, i, 0); needRTW--; }
                else if(f.typ.equals("NEF") && needNEF > 0) { model.setValueAt(true, i, 0); needNEF--; }
                else if(f.typ.equals("KTW") && needKTW > 0) { model.setValueAt(true, i, 0); needKTW--; }
                else if(f.typ.equals("TLF") && needTLF > 0) { model.setValueAt(true, i, 0); needTLF--; }
                else if(f.typ.equals("MTW") && needMTW > 0) { model.setValueAt(true, i, 0); needMTW--; }
            }
            if(needKTW > 0) {
                for(int i = 0; i < verfuegbar.size(); i++) {
                    Fahrzeug f = verfuegbar.get(i);
                    if(f.typ.equals("RTW") && !(Boolean)model.getValueAt(i, 0) && needKTW > 0) {
                        model.setValueAt(true, i, 0); needKTW--;
                    }
                }
            }
        });

        btnAlarm.addActionListener(e -> {
            ArrayList<Fahrzeug> selectedFz = new ArrayList<>();
            int sELW=0, sHLF=0, sDLK=0, sRTW=0, sNEF=0, sKTW=0, sTLF=0, sMTW=0;
            
            for(int i = 0; i < model.getRowCount(); i++) {
                if((Boolean)model.getValueAt(i, 0)) {
                    Fahrzeug f = verfuegbar.get(i);
                    selectedFz.add(f);
                    switch(f.typ) {
                        case "ELW": sELW++; break;
                        case "HLF": sHLF++; break;
                        case "DLK": sDLK++; break;
                        case "RTW": sRTW++; break;
                        case "NEF": sNEF++; break;
                        case "KTW": sKTW++; break;
                        case "TLF": sTLF++; break;
                        case "MTW": sMTW++; break;
                    }
                }
            }
            
            int mELW = Math.max(0, ein.vorlage.reqELW - sELW);
            int mHLF = Math.max(0, ein.vorlage.reqHLF - sHLF);
            int mDLK = Math.max(0, ein.vorlage.reqDLK - sDLK);
            int mNEF = Math.max(0, ein.vorlage.reqNEF - sNEF);
            int mTLF = Math.max(0, ein.vorlage.reqTLF - sTLF);
            int mMTW = Math.max(0, ein.vorlage.reqMTW - sMTW);
            
            int fehlendeKTW = Math.max(0, ein.vorlage.reqKTW - sKTW);
            int ueberschussRTW = Math.max(0, sRTW - ein.vorlage.reqRTW);
            fehlendeKTW = Math.max(0, fehlendeKTW - ueberschussRTW);
            int mRTW = Math.max(0, ein.vorlage.reqRTW - sRTW);
            
            int totalMissing = mELW + mHLF + mDLK + mNEF + mTLF + mMTW + mRTW + fehlendeKTW;
            
            boolean ueberlandHilfeAktiv = false;
            if(totalMissing > 0) {
                int wahl = JOptionPane.showConfirmDialog(d, "Dir fehlen " + totalMissing + " Fahrzeuge zum Ausruecken!\nSoll der Landkreis aushelfen? (" + (totalMissing*500) + " EURO)", "Ueberlandhilfe", JOptionPane.YES_NO_OPTION);
                if (wahl == JOptionPane.YES_OPTION) {
                    if (budget >= (totalMissing*500)) {
                        budget -= (totalMissing*500);
                        ueberlandHilfeAktiv = true;
                    } else {
                        JOptionPane.showMessageDialog(d, "Zu wenig Geld!", "Fehler", JOptionPane.ERROR_MESSAGE);
                        return; 
                    }
                } else {
                    return; 
                }
            }

            boolean matsDa = true;
            if (cfgLogistikAktiv) {
                for(String m : ein.reqMaterial.keySet()) {
                    boolean found = false;
                    for(Wache w : wachen) { if(w.hatMaterial(m, ein.reqMaterial.get(m))) found = true; }
                    if(!found) matsDa = false;
                }
            }
            if (!matsDa) {
                JOptionPane.showMessageDialog(d, "Nicht genug Material (" + ein.reqMaterial.keySet().iterator().next() + ") auf den Wachen!", "Material fehlt", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int xpBel = 0;
            int multiplier = isRushHour() ? 3 : 1;
            
            for (Fahrzeug f : selectedFz) {
                int baseTime = 30; 
                for(Wache wCheck : wachen) {
                    for(Personal p : wCheck.personalPool) { 
                        if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) { baseTime = 60; break; } 
                    }
                }
                f.status = 3; 
                f.anfahrtsZeit = (int)(baseTime * multiplier * getSpeedMultiplier(f)); 
                f.aktuellerEinsatz = ein;
                xpBel += 25;
            }
            
            if (!selectedFz.isEmpty() || ueberlandHilfeAktiv) {
                ein.xpBelohnung = xpBel * ein.vorlage.minLevel;
                aktiveEinsaetze.add(ein);
            }
            
            LogistikSimulator.aktuellerNotruf = null;
            uiAktualisieren(getUhrzeit());
            d.dispose();
        });

        pnlBottom.add(btnAAO);
        pnlBottom.add(btnAlarm);
        pnlBottom.add(btnAbbruch);
        d.add(pnlBottom, BorderLayout.SOUTH);

        d.setVisible(true);
    }
}