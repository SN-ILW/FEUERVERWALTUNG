package neuesspiel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static neuesspiel.LogistikSimulator.*;

public class FensterManager {

    public static void oeffnePersonalHauptmenu() {
        JDialog d = new JDialog(frame, "Personalwesen", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(5, 1, 10, 10));
        d.setLocationRelativeTo(frame);

        JButton b1 = new JButton("Dienstplan / Schichten");
        b1.addActionListener(e -> Schichtplaner.oeffneSchichtplan());
        JButton b2 = new JButton("Mitarbeiter Verwaltung");
        b2.addActionListener(e -> oeffneMitarbeiterVerwaltung());
        JButton b3 = new JButton("Personal einstellen (500 EURO)");
        b3.addActionListener(e -> { personalEinstellen(); d.dispose(); });
        JButton b4 = new JButton("Personal weiterbilden");
        b4.addActionListener(e -> { oeffnePersonalWeiterbildung(); d.dispose(); });
        JButton b5 = new JButton("Leihkraft anfordern (250 EURO)");
        b5.addActionListener(e -> { leihkraftAnfordern(); d.dispose(); });

        d.add(b1); d.add(b2); d.add(b3); d.add(b4); d.add(b5);
        d.setVisible(true);
    }

    public static void oeffneLogistikHauptmenu() {
        JDialog d = new JDialog(frame, "Lager & Logistik", true);
        d.setSize(400, 250);
        d.setLayout(new GridLayout(3, 1, 10, 10));
        d.setLocationRelativeTo(frame);

        JButton b1 = new JButton("Einkauf (Lager fuellen)");
        b1.addActionListener(e -> { oeffneBestellMenu(); d.dispose(); });
        JButton b2 = new JButton("Logistik (Wache versorgen)");
        b2.addActionListener(e -> { oeffneLogistikMenu(); d.dispose(); });
        JButton b3 = new JButton("Material- & Lageruebersicht");
        b3.addActionListener(e -> { oeffneMaterialUebersicht(); d.dispose(); });

        d.add(b1); d.add(b2); d.add(b3);
        d.setVisible(true);
    }

    public static void oeffneMaterialUebersicht() {
        JDialog d = new JDialog(frame, "Material- & Lageruebersicht", true);
        d.setSize(800, 500);
        d.setLayout(new BorderLayout(10, 10));
        d.setLocationRelativeTo(frame);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(35, 35, 35));
        JLabel lblSearch = new JLabel("Nach Material suchen: ");
        lblSearch.setForeground(Color.WHITE);
        JTextField txtSearch = new JTextField(20);
        topPanel.add(lblSearch);
        topPanel.add(txtSearch);
        d.add(topPanel, BorderLayout.NORTH);

        ArrayList<String> cols = new ArrayList<>();
        cols.add("Material");
        cols.add("Warnschwelle");
        cols.add("Hauptlager");
        for (Wache w : wachen) cols.add(w.name);

        DefaultTableModel model = new DefaultTableModel(cols.toArray(new String[0]), 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (CustomMaterial cm : customMaterials) {
            ArrayList<Object> row = new ArrayList<>();
            row.add(cm.name);
            row.add(cm.warnSchwelle);
            row.add(hauptlager.getOrDefault(cm.name, 0));
            for (Wache w : wachen) row.add(w.material.getOrDefault(cm.name, 0));
            model.addRow(row.toArray());
        }

        JTable table = new JTable(model);
        
        // Darkmode fuer die Tabelle
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(43, 43, 43));
                c.setForeground(Color.WHITE);
                if (isSelected) {
                    c.setBackground(new Color(60, 60, 60));
                }
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(20, 30, 48));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setRowHeight(30);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        d.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bot = new JPanel();
        bot.setBackground(new Color(35, 35, 35));
        JButton btnClose = new JButton("Schliessen");
        btnClose.addActionListener(e -> d.dispose());
        bot.add(btnClose);
        d.add(bot, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void oeffneFuhrparkHauptmenu() {
        JDialog d = new JDialog(frame, "Fuhrpark & Werkstatt", true);
        d.setSize(400, 250);
        d.setLayout(new GridLayout(3, 1, 10, 10));
        d.setLocationRelativeTo(frame);

        JButton b1 = new JButton("Fahrzeuge verwalten / kaufen");
        b1.addActionListener(e -> { oeffneFuhrpark(); d.dispose(); });
        JButton b2 = new JButton("Beschaedigtes Fahrzeug reparieren");
        b2.addActionListener(e -> { fahrzeugeReparieren(); d.dispose(); });
        JButton b3 = new JButton("Fahrzeug umstationieren");
        b3.addActionListener(e -> { oeffneFahrzeugTransfer(); d.dispose(); });

        d.add(b1); d.add(b2); d.add(b3);
        d.setVisible(true);
    }

    public static void oeffneSystemHauptmenu() {
        JDialog d = new JDialog(frame, "System & Editor", true);
        d.setSize(400, 400);
        d.setLayout(new GridLayout(7, 1, 10, 10));
        d.setLocationRelativeTo(frame);

        JButton b1 = new JButton("Spieleinstellungen");
        b1.addActionListener(e -> { oeffneEinstellungen(); d.dispose(); });
        JButton b2 = new JButton("Spiel Speichern");
        b2.addActionListener(e -> { SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, "Spiel gespeichert!"); d.dispose(); });
        JButton b3 = new JButton("Spiel Laden");
        b3.addActionListener(e -> { SpeicherManager.laden("savegame.properties"); JOptionPane.showMessageDialog(d, "Spielstand geladen!"); d.dispose(); uiAktualisieren(getUhrzeit()); });
        
        JButton b4 = new JButton("Einsatz-Vorlage erstellen");
        b4.addActionListener(e -> { oeffneEinsatzErsteller(); d.dispose(); });
        JButton b5 = new JButton("Einsatz-Vorlage bearbeiten");
        b5.addActionListener(e -> { oeffneEinsatzBearbeiter(); d.dispose(); });
        
        JButton b6 = new JButton("Material-Vorlage erstellen");
        b6.addActionListener(e -> { oeffneMaterialErsteller(); d.dispose(); });
        JButton b7 = new JButton("Material-Vorlage bearbeiten");
        b7.addActionListener(e -> { oeffneMaterialBearbeiter(); d.dispose(); });

        d.add(b1); d.add(b2); d.add(b3); d.add(b4); d.add(b5); d.add(b6); d.add(b7);
        d.setVisible(true);
    }

    public static void oeffneMitarbeiterVerwaltung() {
        JDialog d = new JDialog(frame, "Mitarbeiter Verwaltung & Historie", true);
        d.setSize(900, 500);
        d.setLayout(new BorderLayout(10, 10));
        d.setLocationRelativeTo(frame);

        String[] columns = {"Name", "Personalnummer", "Wache", "Schichten (Monat)", "Qualifikationen", "Ereignisse"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Wache w : wachen) {
            for (Personal p : w.personalPool) {
                String ereignis = "Keine Ereignisse";
                if (p.status.equals("Lehrgang")) ereignis = "Auf Lehrgang (" + p.lehrgangThema + ")";
                else if (p.krankBis != -1) ereignis = "Krank bis " + getShortDatumString(p.krankBis);
                else if (p.urlaubStart != -1) ereignis = "Urlaub: " + getShortDatumString(p.urlaubStart) + " - " + getShortDatumString(p.urlaubEnd);
                
                model.addRow(new Object[]{
                    p.name, p.getPersonalNummer(), w.name, p.schichtenMonat, String.join(", ", p.qualifikationen), ereignis
                });
            }
        }

        JTable table = new JTable(model);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(43, 43, 43));
                c.setForeground(Color.WHITE);
                if (isSelected) c.setBackground(new Color(60, 60, 60));
                return c;
            }
        });
        
        table.getTableHeader().setBackground(new Color(20, 30, 48));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(35, 35, 35));
        JLabel lblSearch = new JLabel("Nach Mitarbeiter suchen: ");
        lblSearch.setForeground(Color.WHITE);
        JTextField txtSearch = new JTextField(20);
        topPanel.add(lblSearch); topPanel.add(txtSearch);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        d.add(topPanel, BorderLayout.NORTH);
        d.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(35, 35, 35));
        JButton btnClose = new JButton("Schliessen");
        btnClose.addActionListener(e -> d.dispose());
        bottom.add(btnClose);
        d.add(bottom, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void oeffnePostfach() {
        JDialog d = new JDialog(frame, "E-Mail Postfach", true);
        d.setSize(900, 500);
        d.setLocationRelativeTo(frame);
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
            int idx = postfach.size() - 1 - list.getSelectedIndex();
            Email mail = postfach.get(idx);
            mail.typ = "Info"; mail.betreff = "[Abgelehnt] " + mail.betreff;
            uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
        });

        btnLoeschen.addActionListener(e -> {
            if (list.getSelectedIndex() != -1) {
                int idx = postfach.size() - 1 - list.getSelectedIndex();
                postfach.remove(idx);
                uiAktualisieren(getUhrzeit()); d.dispose(); oeffnePostfach();
            }
        });

        pnlBtns.add(btnGenehmigen); pnlBtns.add(btnTM); pnlBtns.add(btnRS); pnlBtns.add(btnLehrgang); pnlBtns.add(btnAnerkennen); pnlBtns.add(btnAblehnen); pnlBtns.add(btnLoeschen);
        d.add(pnlBtns, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), new JScrollPane(txt));
        splitPane.setDividerLocation(300);
        d.add(splitPane, BorderLayout.CENTER);

        d.setVisible(true);
    }

    public static void oeffneWachenAusbau() {
        JDialog d = new JDialog(frame, "Wachen & Gebaeude", true);
        d.setSize(600, 450);
        d.setLayout(new GridLayout(6, 1, 10, 10));
        d.setLocationRelativeTo(frame);

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
        
        // NEU: Kliniken Kaufen Panel
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
        d.setLayout(new GridLayout(7, 1, 10, 10)); // Layout anpassen fuer 7 Zeilen
        d.add(pnlKliniken);

        d.setVisible(true);
    }

    public static void oeffneKrankenhausWahl(Fahrzeug f) {
        JDialog d = new JDialog(frame, "Zielklinik waehlen fuer " + f.funkrufname, true);
        d.setSize(450, 350);
        d.setLayout(new GridLayout(5, 1, 10, 10));
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Klinik- & Bettenuebersicht", false);
        d.setSize(450, 300);
        d.setLayout(new GridLayout(6, 1, 10, 10));
        d.setLocationRelativeTo(frame);

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
        
        JDialog d = new JDialog(frame, "Fahrzeug umstationieren", true);
        d.setSize(400, 200);
        d.setLayout(new GridLayout(3, 2, 10, 10));
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Manuelle Personal Weiterbildung", true);
        d.setSize(400, 200);
        d.setLayout(new GridLayout(3, 2, 10, 10));
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Spieleinstellungen", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(6, 1));
        d.setLocationRelativeTo(frame);

        JCheckBox cbKtp = new JCheckBox("Krankentransport generieren", cfgKrankentransport);
        JCheckBox cbDmg = new JCheckBox("Beschaedigte Fahrzeuge erlauben", cfgBeschaedigung);
        JCheckBox cbSick = new JCheckBox("Krankes Personal erlauben", cfgKrankheit);
        JCheckBox cbAuto = new JCheckBox("Auto-Umlagerung (Lager -> Wache)", cfgAutoTransfer);
        
        JButton btnReset = new JButton("Spielstand zuruecksetzen");
        btnReset.setBackground(new Color(192, 57, 43));
        btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> {
            String wahl = JOptionPane.showInputDialog(d, "ACHTUNG: Dies setzt den Spielstand zurueck!\n(Einsaetze und Materialien bleiben erhalten.)\nZum Bestaetigen bitte exakt 'LOESCHEN' eingeben:");
            if(wahl != null && wahl.equals("LOESCHEN")) {
                File file = new File("savegame.properties");
                if(file.exists()) file.delete();
                initStandardDaten();
                uiAktualisieren(getUhrzeit());
                JOptionPane.showMessageDialog(d, "Spielstand wurde erfolgreich zurueckgesetzt!");
                d.dispose();
            } else if (wahl != null) {
                JOptionPane.showMessageDialog(d, "Eingabe fehlerhaft. Abbruch.");
            }
        });

        JButton btnSave = new JButton("Speichern & Schliessen");
        btnSave.addActionListener(e -> {
            cfgKrankentransport = cbKtp.isSelected();
            cfgBeschaedigung = cbDmg.isSelected();
            cfgKrankheit = cbSick.isSelected();
            cfgAutoTransfer = cbAuto.isSelected();
            SpeicherManager.speichern("savegame.properties");
            d.dispose();
        });

        d.add(cbKtp); d.add(cbDmg); d.add(cbSick); d.add(cbAuto); d.add(btnReset); d.add(btnSave);
        d.setVisible(true);
    }

    public static void oeffneFuhrpark() {
        if(wachen.isEmpty()){ JOptionPane.showMessageDialog(frame, "Du hast keine Wache!"); return; }
        JDialog d = new JDialog(frame, "Fuhrpark verwalten", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(7, 2, 10, 10));
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Eigenes Material Erstellen", true);
        d.setSize(500, 400);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Material Bearbeiten", true);
        d.setSize(500, 450);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Einsatz-Vorlagen Ersteller", true);
        d.setSize(600, 500);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Einsatz-Vorlagen Bearbeiten", true);
        d.setSize(600, 550);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(frame);

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
        JDialog d = new JDialog(frame, "Offene Nachforderungen", true); 
        d.setSize(500, 300); 
        d.setLayout(new BorderLayout(10, 10));
        d.setLocationRelativeTo(frame);
        
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
        JDialog d = new JDialog(frame, "Wache versorgen", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(0, 1, 5, 5));
        d.setLocationRelativeTo(frame);

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
                   // JOptionPane.showMessageDialog(d, "Material umgelagert!");
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
        JDialog d = new JDialog(frame, "Einkauf (Lieferung in 60s)", true);
        d.setSize(400, 300);
        d.setLayout(new GridLayout(0, 1, 5, 5));
        d.setLocationRelativeTo(frame);

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
                    //JOptionPane.showMessageDialog(d, "Bestellung aufgegeben!");
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
        JDialog d = new JDialog(frame, "Einsatzakte: " + ein.vorlage.stichwort, true);
        d.setSize(500, 400);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(frame);

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