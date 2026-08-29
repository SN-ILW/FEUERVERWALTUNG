package neuesspiel;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static neuesspiel.LogistikSimulator.*;
import static neuesspiel.FensterManager.createFramelessDialog;

public class MenuPersonal {

    public static void oeffnePersonalHauptmenu() {
        JDialog d = createFramelessDialog("Personalwesen", 400, 400); 
        JPanel content = new JPanel(new GridLayout(7, 1, 10, 10)); 
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JButton b1 = new JButton("Dienstplan / Schichten"); b1.addActionListener(e -> { d.dispose(); Schichtplaner.oeffneSchichtplan(); });
        JButton b2 = new JButton("Mitarbeiter Verwaltung"); b2.addActionListener(e -> { d.dispose(); FensterManager.oeffneMitarbeiterVerwaltung(); });
        JButton b3 = new JButton("Personal einstellen (500 EURO)"); b3.addActionListener(e -> { d.dispose(); FensterManager.personalEinstellen(); });
        JButton b4 = new JButton("Personal weiterbilden"); b4.addActionListener(e -> { d.dispose(); FensterManager.oeffnePersonalWeiterbildung(); });
        JButton b5 = new JButton("Leihkraft anfordern (250 EURO)"); b5.addActionListener(e -> { d.dispose(); FensterManager.leihkraftAnfordern(); });
        JButton b6 = new JButton("Personal umstationieren"); b6.addActionListener(e -> { d.dispose(); FensterManager.oeffnePersonalTransfer(); });
        
        JButton btnKalenderPers = LogistikSimulator.createStyledButton("Terminkalender & Events (K)", new Color(155, 89, 182));
        btnKalenderPers.addActionListener(e -> { d.dispose(); Terminkalender.oeffneKalender(); });
        
        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); content.add(b6); content.add(btnKalenderPers);
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
                } else { eig.append("Keine"); }
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
        table.getColumnModel().getColumn(0).setPreferredWidth(150); table.getColumnModel().getColumn(5).setPreferredWidth(150); 
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

    public static void oeffnePersonalTransfer() {
        if(wachen.size() < 2) { JOptionPane.showMessageDialog(frame, "Du brauchst mindestens zwei Wachen fuer einen Transfer!"); return; }
        
        JDialog d = createFramelessDialog("Personal transferieren", 450, 300);
        JPanel content = new JPanel(new GridLayout(4, 2, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35,35,35));

        JComboBox<String> cbPers = new JComboBox<>(); ArrayList<Personal> pList = new ArrayList<>(); ArrayList<Wache> wList = new ArrayList<>(); 
        for(Wache w : wachen) { for(Personal p : w.personalPool) { pList.add(p); wList.add(w); cbPers.addItem(p.name + " (" + w.name + ")"); } }

        JComboBox<String> cbZiel = new JComboBox<>(); for(Wache w : wachen) cbZiel.addItem(w.name);
        JComboBox<String> cbArt = new JComboBox<>(new String[]{"Dauerhaft (Versetzung)", "Temporaer (Bis Tagesabschluss)"});

        JButton btnTransfer = new JButton("Transferieren");
        btnTransfer.addActionListener(e -> {
            int pIndex = cbPers.getSelectedIndex(); int zielIndex = cbZiel.getSelectedIndex();
            if(pIndex == -1 || zielIndex == -1) return;

            Personal p = pList.get(pIndex); Wache alteWache = wList.get(pIndex); Wache zielWache = wachen.get(zielIndex);
            if(alteWache == zielWache) { JOptionPane.showMessageDialog(d, "Die Person arbeitet bereits auf dieser Wache!"); return; }
            if(p.status.equals("Lehrgang")) { JOptionPane.showMessageDialog(d, "Mitarbeiter ist auf Lehrgang!"); return; }

            alteWache.personalPool.remove(p); zielWache.personalPool.add(p);
            p.zugewiesenesFahrzeug = "Keines"; p.geplantesFahrzeug = "Keines";

            if(cbArt.getSelectedIndex() == 1) { 
                if(!LogistikSimulator.verliehenesPersonal.containsKey(p)) LogistikSimulator.verliehenesPersonal.put(p, alteWache);
            } else { LogistikSimulator.verliehenesPersonal.remove(p); }

            SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, p.name + " wurde erfolgreich nach " + zielWache.name + " umstationiert.");
            LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose();
        });

        JLabel l1 = new JLabel("Mitarbeiter waehlen:"); l1.setForeground(Color.WHITE); content.add(l1); content.add(cbPers);
        JLabel l2 = new JLabel("Ziel-Wache:"); l2.setForeground(Color.WHITE); content.add(l2); content.add(cbZiel);
        JLabel l3 = new JLabel("Art des Transfers:"); l3.setForeground(Color.WHITE); content.add(l3); content.add(cbArt);
        content.add(new JLabel("")); content.add(btnTransfer);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffnePersonalWeiterbildung() {
        JDialog d = createFramelessDialog("Manuelle Personal Weiterbildung", 800, 600);
        d.setLayout(new BorderLayout(10, 10));
        d.getContentPane().setBackground(new Color(35, 35, 35));

        class Lehrgang {
            String name; int preis;
            public Lehrgang(String n, int p) { this.name = n; this.preis = p; }
            @Override public String toString() { return name + " (" + preis + " EURO)"; }
        }

        JComboBox<Lehrgang> cbKurs = new JComboBox<>(new Lehrgang[]{
            new Lehrgang("RS", 250), new Lehrgang("TM", 250), new Lehrgang("NFS", 500), new Lehrgang("TF", 500),
            new Lehrgang("GF", 750), new Lehrgang("MA", 750), new Lehrgang("FueAs", 800), new Lehrgang("EL", 1000)
        });
        cbKurs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        ArrayList<Personal> alleMitarbeiter = new ArrayList<>();
        for(Wache w : wachen) for(Personal p : w.personalPool) alleMitarbeiter.add(p);
        
        if(alleMitarbeiter.isEmpty()) { JOptionPane.showMessageDialog(d, "Kein Personal vorhanden!"); d.dispose(); return; }

        String[] cols = {"Name (Wache)", "Status", "Aktuelle Qualifikationen", "Buchen"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int col) { return (col == 3) ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int row, int col) {
                if (col == 3) {
                    Personal p = alleMitarbeiter.get(row); Lehrgang l = (Lehrgang) cbKurs.getSelectedItem();
                    if(p.qualifikationen.contains("Anwaerter") || p.status.equals("Urlaub") || p.status.equals("Krank") || p.status.equals("Lehrgang") || p.qualifikationen.contains(l.name)) return false;
                    return true; 
                } return false;
            }
        };

        for (Personal p : alleMitarbeiter) {
            String wacheName = ""; for(Wache w : wachen) if(w.personalPool.contains(p)) wacheName = w.name;
            String qualis = p.qualifikationen.isEmpty() ? "-" : String.join(", ", p.qualifikationen);
            model.addRow(new Object[]{p.name + " (" + wacheName + ")", p.status, qualis, false});
        }

        JTable table = new JTable(model); table.setRowHeight(30); table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); table.getTableHeader().setBackground(new Color(20, 20, 20)); table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            JCheckBox cb = new JCheckBox(); { cb.setHorizontalAlignment(SwingConstants.CENTER); }
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                cb.setSelected(value != null && (Boolean)value); cb.setEnabled(table.isCellEditable(row, column)); 
                cb.setBackground(isSelected ? new Color(41, 128, 185) : new Color(43, 43, 43)); return cb;
            }
        });

        JScrollPane scroll = new JScrollPane(table); scroll.getViewport().setBackground(new Color(35, 35, 35)); scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); topPnl.setBackground(new Color(45, 45, 45));
        JLabel lblTop = new JLabel("Lehrgang auswaehlen:"); lblTop.setForeground(Color.WHITE); lblTop.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPnl.add(lblTop); topPnl.add(cbKurs);

        JPanel bottomPnl = new JPanel(new BorderLayout()); bottomPnl.setBackground(new Color(45, 45, 45)); bottomPnl.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JButton btnClose = new JButton("Schliessen"); btnClose.setBackground(new Color(192, 57, 43)); btnClose.setForeground(Color.WHITE); btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.addActionListener(e -> d.dispose()); bottomPnl.add(btnClose, BorderLayout.WEST);

        JPanel rightBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0)); rightBottom.setBackground(new Color(45, 45, 45));
        JLabel lblTotal = new JLabel("Gesamtkosten: 0 EURO"); lblTotal.setForeground(new Color(241, 196, 15)); lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton btnKaufen = new JButton("Weiterbildung starten"); btnKaufen.setBackground(new Color(39, 174, 96)); btnKaufen.setForeground(Color.WHITE); btnKaufen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightBottom.add(lblTotal); rightBottom.add(btnKaufen); bottomPnl.add(rightBottom, BorderLayout.EAST);

        model.addTableModelListener(e -> {
            int count = 0; for (int i = 0; i < model.getRowCount(); i++) if ((Boolean) model.getValueAt(i, 3)) count++;
            Lehrgang l = (Lehrgang) cbKurs.getSelectedItem(); lblTotal.setText("Gesamtkosten: " + (count * l.preis) + " EURO");
        });

        cbKurs.addActionListener(e -> { for (int i = 0; i < model.getRowCount(); i++) model.setValueAt(false, i, 3); model.fireTableDataChanged(); });

        btnKaufen.addActionListener(e -> {
            int count = 0; for (int i = 0; i < model.getRowCount(); i++) if ((Boolean) model.getValueAt(i, 3)) count++;
            if (count == 0) { JOptionPane.showMessageDialog(d, "Bitte waehle Mitarbeiter aus!"); return; }
            Lehrgang l = (Lehrgang) cbKurs.getSelectedItem(); int totalCost = count * l.preis;

            if (budget >= totalCost) {
                double rabatt = 1.0 - (lehrerStufe * 0.10); budget -= totalCost; int lehrgangsDauer = (int)(3 * 60 * rabatt);
                for (int i = 0; i < model.getRowCount(); i++) {
                    if ((Boolean) model.getValueAt(i, 3)) {
                        Personal p = alleMitarbeiter.get(i); p.status = "Lehrgang"; p.geplanterStatus = "Lehrgang"; p.lehrgangDauerSec = lehrgangsDauer; p.lehrgangThema = l.name;
                    }
                }
                SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, count + " Mitarbeiter sind auf Lehrgang (" + l.name + ")!");
                uiAktualisieren(getUhrzeit()); d.dispose();
            } else { JOptionPane.showMessageDialog(d, "Nicht genug Budget! (" + totalCost + " EUR benoetigt)"); }
        });

        d.add(topPnl, BorderLayout.NORTH); d.add(scroll, BorderLayout.CENTER); d.add(bottomPnl, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void zeigeGehaltsVerhandlung(Personal p, double forderung, Email mail) {
        JDialog d = createFramelessDialog("Gehaltsverhandlung: " + p.name, 600, 400);
        JTextArea txt = new JTextArea("Absender: " + p.name + " (" + String.join(", ", p.qualifikationen) + ")\nBetreff: Antrag auf Anpassung der Verguetung\n\nHallo Leitstelle,\n\nich bin nun seit geraumer Zeit auf der Wache taetig und habe bereits " + p.schichtenMonat + " Schichten absolviert.\nMein aktueller Stundenlohn liegt bei " + String.format("%.2f", p.stundenLohn) + " EUR.\n\nAufgrund meiner Leistungen beantrage ich eine Anpassung meines Stundenlohns auf " + String.format("%.2f", forderung) + " EUR.\n\nMit freundlichen Gruessen,\n" + p.name);
        txt.setEditable(false); txt.setMargin(new Insets(15, 15, 15, 15)); txt.setBackground(new Color(35, 35, 35)); txt.setForeground(Color.WHITE); txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        d.add(new JScrollPane(txt), BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); btnPanel.setBackground(new Color(35, 35, 35));
        JButton btnGenehmigen = new JButton("Genehmigen (" + String.format("%.2f", forderung) + " €/h)"); btnGenehmigen.setBackground(new Color(39, 174, 96)); btnGenehmigen.setForeground(Color.WHITE);
        JButton btnGegenangebot = new JButton("Gegenangebot"); btnGegenangebot.setBackground(new Color(241, 196, 15)); btnGegenangebot.setForeground(Color.BLACK);
        JButton btnAblehnen = new JButton("Ablehnen"); btnAblehnen.setBackground(new Color(192, 57, 43)); btnAblehnen.setForeground(Color.WHITE);
        
        btnGenehmigen.addActionListener(e -> {
            p.stundenLohn = forderung; p.abgelehnteForderungen = 0; JOptionPane.showMessageDialog(d, p.name + " freut sich!");
            LogistikSimulator.postfach.remove(mail); LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose(); FensterManager.oeffnePostfach(); 
        });
        
        btnGegenangebot.addActionListener(e -> {
            double kompromiss = p.stundenLohn + ((forderung - p.stundenLohn) / 2.0);
            String eingabe = JOptionPane.showInputDialog(d, "Dein Gegenangebot (in €):", String.format("%.2f", kompromiss).replace(",", "."));
            if (eingabe != null) {
                try {
                    double gebot = Double.parseDouble(eingabe.replace(",", "."));
                    if (gebot > p.stundenLohn && gebot < forderung) {
                        p.stundenLohn = gebot; JOptionPane.showMessageDialog(d, "Einigung erzielt! Neuer Stundenlohn: " + String.format("%.2f", gebot) + " €/h.");
                        LogistikSimulator.postfach.remove(mail); LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose(); FensterManager.oeffnePostfach();
                    } else { JOptionPane.showMessageDialog(d, "Ungueltiges Gegenangebot!"); }
                } catch(Exception ex) { JOptionPane.showMessageDialog(d, "Zahlenformat ungueltig!"); }
            }
        });
        
        btnAblehnen.addActionListener(e -> {
            p.abgelehnteForderungen++;
            if (p.abgelehnteForderungen >= 2) JOptionPane.showMessageDialog(d, "Achtung: " + p.name + " ist sehr unzufrieden!", "Warnung", JOptionPane.WARNING_MESSAGE);
            else JOptionPane.showMessageDialog(d, "Gehaltserhoehung abgelehnt.");
            LogistikSimulator.postfach.remove(mail); LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); d.dispose(); FensterManager.oeffnePostfach();
        });
        
        btnPanel.add(btnGenehmigen); btnPanel.add(btnGegenangebot); btnPanel.add(btnAblehnen);
        d.add(btnPanel, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void personalEinstellen() {
        if(wachen.isEmpty()){ JOptionPane.showMessageDialog(frame, "Du hast noch keine Wache!"); return; }
        if(budget >= 500) {
            String[] wNamen = new String[wachen.size()]; for(int i=0; i<wachen.size(); i++) wNamen[i] = wachen.get(i).name;
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
                    boolean hatSchon = false; for(MitarbeiterEigenschaft e : potenziell.eigenschaften) if(e.name.equals(gewaehlt.name)) hatSchon = true;
                    if(!hatSchon) potenziell.eigenschaften.add(gewaehlt);
                }
                
                target.personalPool.add(potenziell); budget -= 500;
                if (Math.random() > 0.6) {
                    String[] vorwissen = {"TM", "RS", "GF"}; String w = vorwissen[(int)(Math.random() * vorwissen.length)];
                    potenziell.qualifikationen.remove("Anwaerter");
                    if (!potenziell.qualifikationen.contains(w)) potenziell.qualifikationen.add(w); 
                    postfach.add(new Email("Personalabteilung", "Neue Personalakte: " + potenziell.name, "Mitarbeiter bringt Vorerfahrung als [" + w + "] mit.", "Info", potenziell, tag, tag));
                }
                
                StringBuilder traitText = new StringBuilder(); for(MitarbeiterEigenschaft e : potenziell.eigenschaften) traitText.append("\n- ").append(e.name).append(" (").append(e.beschreibung).append(")");
                JOptionPane.showMessageDialog(frame, neu + " wurde auf " + target.name + " eingestellt!\n\nBesondere Eigenschaften:" + traitText.toString());
                SpeicherManager.speichern("savegame.properties"); uiAktualisieren(getUhrzeit());
            }
        } else { JOptionPane.showMessageDialog(frame, "Zu wenig Budget (500 EURO benoetigt)!", "Fehler", JOptionPane.ERROR_MESSAGE); }
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
                                for(int i=0; i<reqs.size(); i++) if(personErfuellt(p, reqs.get(i))) { reqs.remove(i); break; }
                            }
                        }
                        if(!reqs.isEmpty()) {
                            String missingRole = reqs.get(0); budget -= 250;
                            Personal leih = new Personal("Leihkraft (" + missingRole + ")", missingRole);
                            leih.zugewiesenesFahrzeug = f.funkrufname; leih.geplantesFahrzeug = "Keines";
                            w.personalPool.add(leih); helped = true;
                            SpeicherManager.speichern("savegame.properties");
                            if(hatGenugPersonal(f)) { f.status = 6; f.ausfallGrund = "Personalwechsel"; f.reparaturDauer = 30; }
                            break;
                        }
                    }
                }
                if(helped) break;
            }
            if(!helped) JOptionPane.showMessageDialog(frame, "Aktuell fehlt keinem Fahrzeug Personal!");
        } else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget (250 EURO)!", "Fehler", JOptionPane.ERROR_MESSAGE); }
    }
}