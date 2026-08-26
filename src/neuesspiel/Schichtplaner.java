package neuesspiel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import static neuesspiel.LogistikSimulator.*;

public class Schichtplaner {

    private static boolean zeigeAktuellenMonat = true;
    private static Wache aktuelleWache = null;
    private static DefaultTableModel tableModel;
    private static JTable table;
    private static JPanel pnlFahrzeugBoxen;
    private static JScrollPane scrollBoxen; // NEU: Als globale Variable, um den Titel zu aendern
    
    private static JTable rowHeaderTable;
    private static DefaultTableModel rowHeaderModel;
    
    private static int ausgewaehlterTagIndex = 0; // NEU: Merkt sich, welcher Tag gerade unten links angezeigt wird

    public static void oeffneSchichtplan() {
        if (wachen.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Wachen vorhanden!"); return; }
        aktuelleWache = wachen.get(0);
        zeigeAktuellenMonat = true;
        ausgewaehlterTagIndex = 0; // Beim Oeffnen immer Tag 1 anzeigen

        JDialog d = new JDialog(frame, "Dienstplan", true);
        d.setUndecorated(true);
        d.setSize(1400, 800);
        d.setLocationRelativeTo(frame);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(35, 35, 35));

        // --- CUSTOM TITLE BAR ---
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        JLabel lblTitle = new JLabel(" Dienstplan");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleBar.add(lblTitle, BorderLayout.WEST);

        final Point[] dragPoint = new Point[1];
        titleBar.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { dragPoint[0] = e.getPoint(); }});
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) { d.setLocation(d.getLocation().x + e.getX() - dragPoint[0].x, d.getLocation().y + e.getY() - dragPoint[0].y); }
        });
        d.add(titleBar, BorderLayout.NORTH);

        // --- OBERE LEISTE ---
        JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topMenu.setBackground(new Color(25, 25, 25));

        JLabel lblMonat = new JLabel("Ansicht: Aktueller Monat");
        lblMonat.setForeground(Color.WHITE); lblMonat.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnAktuell = createBtn("Aktueller Monat", new Color(41, 128, 185));
        JButton btnFolge = createBtn("Folge-Monat", new Color(39, 174, 96));
        JButton btnCopy = createBtn("Aktuellen kopieren", new Color(211, 84, 0));

        topMenu.add(lblMonat); topMenu.add(Box.createHorizontalStrut(20));
        topMenu.add(btnAktuell); topMenu.add(btnFolge); topMenu.add(btnCopy);

        // --- LINKE LEISTE (Stempel & Akkordeon) ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setBackground(new Color(30, 30, 30));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlWahl = new JPanel(new GridLayout(4, 1, 0, 5));
        pnlWahl.setOpaque(false);
        JLabel l1 = new JLabel("1. Wache waehlen:"); l1.setForeground(Color.WHITE); l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JComboBox<String> cbWachen = new JComboBox<>();
        for (Wache w : wachen) cbWachen.addItem(w.name);
        
        JLabel l2 = new JLabel("2. Stempel waehlen:"); l2.setForeground(Color.WHITE); l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        DefaultListModel<String> stempelModel = new DefaultListModel<>();
        stempelModel.addElement("Frei"); 
        stempelModel.addElement("Bereitschaft"); // <-- HIER EINFUEGEN
        stempelModel.addElement("Urlaub"); 
        stempelModel.addElement("Krank"); 
        stempelModel.addElement("Lehrgang");
        for (Fahrzeug f : aktuelleWache.fuhrpark) stempelModel.addElement(f.funkrufname);
        
        JList<String> stempelListe = new JList<String>(stempelModel) {
            @Override
            public String getToolTipText(MouseEvent evt) {
                int index = locationToIndex(evt.getPoint());
                if (index > -1) {
                    String item = getModel().getElementAt(index);
                    for (Fahrzeug f : aktuelleWache.fuhrpark) {
                        if (f.funkrufname.equals(item)) {
                            ArrayList<String> reqs = getRequiredRoles(f);
                            HashMap<String, Integer> counts = new HashMap<>();
                            for (String r : reqs) counts.put(r, counts.getOrDefault(r, 0) + 1);
                            ArrayList<String> res = new ArrayList<>();
                            for (String r : counts.keySet()) res.add(counts.get(r) + "x " + r);
                            return "Benoetigt: " + String.join(", ", res);
                        }
                    }
                }
                return null;
            }
        };
        stempelListe.setSelectedIndex(0);
        stempelListe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        pnlWahl.add(l1); pnlWahl.add(cbWachen); pnlWahl.add(l2);
        leftPanel.add(pnlWahl, BorderLayout.NORTH);
        
        JPanel pnlListenWrapper = new JPanel(new BorderLayout());
        pnlListenWrapper.setOpaque(false);
        pnlListenWrapper.add(new JScrollPane(stempelListe), BorderLayout.CENTER);
        
        pnlFahrzeugBoxen = new JPanel();
        pnlFahrzeugBoxen.setLayout(new BoxLayout(pnlFahrzeugBoxen, BoxLayout.Y_AXIS));
        pnlFahrzeugBoxen.setBackground(new Color(35, 35, 35));
        
        scrollBoxen = new JScrollPane(pnlFahrzeugBoxen);
        scrollBoxen.setPreferredSize(new Dimension(300, 200));
        scrollBoxen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Einteilung Tag 1", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));
        pnlListenWrapper.add(scrollBoxen, BorderLayout.SOUTH);
        leftPanel.add(pnlListenWrapper, BorderLayout.CENTER);

        // --- TABELLE UND ROWHEADER (Fixierte Spalten) ---
        tableModel = new DefaultTableModel(0, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(new Color(20, 30, 48));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
        // NEU: Klick auf Tabellen-Kopf (Tage) aendert die Anzeige unten links
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                if (col >= 0) {
                    ausgewaehlterTagIndex = col;
                    updateFahrzeugInfo();
                }
            }
        });
        
        rowHeaderModel = new DefaultTableModel(0, 2) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        rowHeaderModel.setColumnIdentifiers(new String[]{"Name", "Qualifikationen"});
        rowHeaderTable = new JTable(rowHeaderModel);
        rowHeaderTable.setRowHeight(28);
        rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rowHeaderTable.getTableHeader().setBackground(new Color(20, 30, 48));
        rowHeaderTable.getTableHeader().setForeground(Color.WHITE);
        rowHeaderTable.getTableHeader().setReorderingAllowed(false);
        
        // NEU: Weiße Leer-Spalte fixen durch exakte Breiten-Verriegelung
        rowHeaderTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        rowHeaderTable.getColumnModel().getColumn(0).setMinWidth(150);
        rowHeaderTable.getColumnModel().getColumn(0).setMaxWidth(150);
        
        rowHeaderTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        rowHeaderTable.getColumnModel().getColumn(1).setMinWidth(200);
        rowHeaderTable.getColumnModel().getColumn(1).setMaxWidth(200);
        
        rowHeaderTable.setPreferredScrollableViewportSize(new Dimension(350, 0)); // Exakt 150 + 200
        
        rowHeaderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(40, 40, 40)); c.setForeground(Color.WHITE);
                return c;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = (String) value;
                if (val.equals("Krank")) { c.setBackground(new Color(231, 76, 60)); c.setForeground(Color.WHITE); }
                else if (val.equals("Urlaub")) { c.setBackground(new Color(243, 156, 18)); c.setForeground(Color.BLACK); }
                else if (val.equals("Lehrgang")) { c.setBackground(new Color(155, 89, 182)); c.setForeground(Color.WHITE); }
                else if (val.equals("Bereitschaft")) { c.setBackground(new Color(241, 196, 15)); c.setForeground(Color.BLACK); } // <-- NEU
                else if (val.equals("Frei")) { c.setBackground(new Color(52, 152, 219)); c.setForeground(Color.WHITE); }
                else { c.setBackground(new Color(46, 204, 113)); c.setForeground(Color.BLACK); }
                return c;
            }
        });

        // "Mal-Werkzeug" (Stempel-Listener)
MouseAdapter stampAdapter = new MouseAdapter() {
            private void paintCell(MouseEvent e, boolean isClick) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    ausgewaehlterTagIndex = col; // Angeklickter Tag wird sofort zur Info unten links

                    // --- NEUE SPERRE FÜR VERGANGENE TAGE ---
                    int heutigerTagIndex = LogistikSimulator.getCurrentDate().getDayOfMonth() - 1;
                    
                    // Wenn wir im aktuellen Monat sind UND die Spalte VOR dem heutigen Tag liegt
                    if (zeigeAktuellenMonat && col < heutigerTagIndex) {
                        // Fehlermeldung nur beim echten Klicken anzeigen (nicht beim Draggen/Malen)
                        if (isClick) {
                            JOptionPane.showMessageDialog(table, "Vergangene Schichten können rückwirkend nicht mehr geändert werden!", "Tag gesperrt", JOptionPane.WARNING_MESSAGE);
                        }
                        return; // Bricht das Stempeln sofort ab!
                    }
                    // ---------------------------------------

                    String werkzeug = SwingUtilities.isRightMouseButton(e) ? "Frei" : stempelListe.getSelectedValue();
                    if(werkzeug != null) {
                        tableModel.setValueAt(werkzeug, row, col);
                        updateFahrzeugInfo();
                    }
                }
            }
            
            @Override public void mousePressed(MouseEvent e) { paintCell(e, true); }  // true = Ist ein Klick
            @Override public void mouseDragged(MouseEvent e) { paintCell(e, false); } // false = Ist nur ein Ziehen
        };
        table.addMouseListener(stampAdapter);
        table.addMouseMotionListener(stampAdapter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setRowHeaderView(rowHeaderTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());

        // --- UNTERE LEISTE ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(25, 25, 25));
        JButton btnClose = createBtn("Abbrechen", new Color(192, 57, 43));
        JButton btnSave = createBtn("Speichern & Schliessen", new Color(39, 174, 96));
        bottomPanel.add(btnClose); bottomPanel.add(btnSave);

        // --- HAUPT-LAYOUT ---
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(topMenu, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        d.add(leftPanel, BorderLayout.WEST);
        d.add(centerContainer, BorderLayout.CENTER);
        d.add(bottomPanel, BorderLayout.SOUTH);

        // --- FUNKTIONEN ---
        cbWachen.addActionListener(e -> {
            saveTableData();
            aktuelleWache = wachen.get(cbWachen.getSelectedIndex());
            stempelModel.clear();
            stempelModel.addElement("Frei"); 
            stempelModel.addElement("Bereitschaft"); // <-- UND HIER EINFUEGEN
            stempelModel.addElement("Urlaub"); 
            stempelModel.addElement("Krank"); 
            stempelModel.addElement("Lehrgang");
            for (Fahrzeug f : aktuelleWache.fuhrpark) stempelModel.addElement(f.funkrufname);
            stempelListe.setSelectedIndex(0);
            loadTableData();
        });

        btnAktuell.addActionListener(e -> { saveTableData(); zeigeAktuellenMonat = true; lblMonat.setText("Ansicht: Aktueller Monat"); loadTableData(); });
        btnFolge.addActionListener(e -> { saveTableData(); zeigeAktuellenMonat = false; lblMonat.setText("Ansicht: Folge-Monat"); loadTableData(); });

        btnCopy.addActionListener(e -> {
            int wahl = JOptionPane.showConfirmDialog(d, "Aktuellen Monat in den Folge-Monat kopieren?", "Kopieren", JOptionPane.YES_NO_OPTION);
            if (wahl == JOptionPane.YES_OPTION) {
                for (Personal p : aktuelleWache.personalPool) System.arraycopy(p.planAktuellerMonat, 0, p.planNaechsterMonat, 0, 31);
                loadTableData();
            }
        });

        btnClose.addActionListener(e -> d.dispose());
btnSave.addActionListener(e -> {
            saveTableData();
            
            if (zeigeAktuellenMonat) {
                int heuteIndex = LogistikSimulator.getCurrentDate().getDayOfMonth() - 1;
                java.util.HashSet<String> fzMitFreiemPersonal = new java.util.HashSet<>();
                
                for (Wache w : wachen) {
                    for (Personal p : w.personalPool) {
                        String neuerPlan = p.planAktuellerMonat[heuteIndex];
                        if (neuerPlan == null) neuerPlan = "Frei";
                        
                        if (p.krankBis != -1 && tag <= p.krankBis) continue;
                        if (p.urlaubStart != -1 && tag >= p.urlaubStart && tag <= p.urlaubEnd) continue;
                        if (p.status.equals("Lehrgang")) continue;

                        String alterStatus = p.status;
                        String altesFz = p.zugewiesenesFahrzeug;
                        
                        // Pruefen, woher die Person kommt
                        if (!neuerPlan.equals("Frei") && !neuerPlan.equals("Bereitschaft") && !neuerPlan.equals(altesFz)) {
                            if (alterStatus.equals("Frei")) {
                                fzMitFreiemPersonal.add(neuerPlan);
                            }
                        }
                        
                        // Live-Status ueberschreiben
                        if (neuerPlan.equals("Frei")) {
                            p.status = "Frei";
                            p.zugewiesenesFahrzeug = "Keines";
                        } else if (neuerPlan.equals("Bereitschaft")) {
                            p.status = "Bereitschaft";
                            p.zugewiesenesFahrzeug = "Keines";
                        } else {
                            p.status = "Bereit";
                            p.zugewiesenesFahrzeug = neuerPlan;
                        }
                    }
                    
                    for (Fahrzeug f : w.fuhrpark) {
                        boolean hatGenug = hatGenugPersonal(f);
                        
                        if (!hatGenug) {
                            if (f.status == 1 || f.status == 2 || (f.status == 6 && f.ausfallGrund.equals("Personalwechsel"))) {
                                f.status = 6;
                                f.ausfallGrund = "Personal fehlt";
                                f.reparaturDauer = 0;
                            }
                        } else {
                            if (f.status == 6 && f.ausfallGrund.equals("Personal fehlt")) {
                                f.ausfallGrund = "Personalwechsel";
                                // Kam jemand aus dem "Frei"? Dann 60s, ansonsten (Bereitschaft/Umsetzen) 30s
                                f.reparaturDauer = fzMitFreiemPersonal.contains(f.funkrufname) ? 60 : 30;
                            }
                        }
                    }
                }
            }
            
            SpeicherManager.speichern("savegame.properties");
            d.dispose();
            uiAktualisieren(getUhrzeit());
        });

        loadTableData();
        d.setVisible(true);
    }

    private static void loadTableData() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        
        LocalDate cDate = LogistikSimulator.getCurrentDate();
        if (!zeigeAktuellenMonat) cDate = cDate.plusMonths(1);
        int tageImMonat = cDate.lengthOfMonth();
        
        // Verhindert Absturz, falls man von einem 31-Tage-Monat auf einen 30-Tage-Monat wechselt
        if (ausgewaehlterTagIndex >= tageImMonat) {
            ausgewaehlterTagIndex = tageImMonat - 1;
        }

        String[] columns = new String[tageImMonat];
        for (int i = 0; i < tageImMonat; i++) columns[i] = String.valueOf(i + 1);

        tableModel.setColumnIdentifiers(columns);
        for(int i = 0; i < tableModel.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(80);
        }

        tableModel.setRowCount(0);
        rowHeaderModel.setRowCount(0);

        for (Personal p : aktuelleWache.personalPool) {
            rowHeaderModel.addRow(new Object[]{ p.name, String.join(", ", p.qualifikationen) });
            
            String[] row = new String[tageImMonat];
            String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
            for (int i = 0; i < tageImMonat; i++) {
                row[i] = plan[i] != null ? plan[i] : "Frei";
            }
            tableModel.addRow(row);
        }
        
        updateFahrzeugInfo();
    }

    private static void saveTableData() {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        
        int colsToSave = tableModel.getColumnCount();
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            Personal p = aktuelleWache.personalPool.get(r);
            String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
            for (int c = 0; c < colsToSave; c++) {
                plan[c] = (String) tableModel.getValueAt(r, c);
            }
        }
    }

    private static void updateFahrzeugInfo() {
        if (pnlFahrzeugBoxen == null || scrollBoxen == null) return;
        
        pnlFahrzeugBoxen.removeAll();
        
        // NEU: Ueberschrift passt sich dem ausgewaehlten Tag an
        scrollBoxen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Einteilung Tag " + (ausgewaehlterTagIndex + 1), javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));

        for (Fahrzeug f : aktuelleWache.fuhrpark) {
            ArrayList<String> reqs = getRequiredRoles(f);
            ArrayList<Personal> besatzung = new ArrayList<>();
            
            for (int i = 0; i < aktuelleWache.personalPool.size(); i++) {
                if (tableModel.getColumnCount() > ausgewaehlterTagIndex) {
                    // Prueft die Zuweisung fuer genau DEN ausgewaehlten Tag
                    String assignment = (String) tableModel.getValueAt(i, ausgewaehlterTagIndex); 
                    if (assignment != null && assignment.equals(f.funkrufname)) {
                        besatzung.add(aktuelleWache.personalPool.get(i));
                    }
                }
            }

            boolean isReady = canFill(reqs, besatzung);
            
            JPanel fzPanel = new JPanel();
            fzPanel.setLayout(new BoxLayout(fzPanel, BoxLayout.Y_AXIS));
            fzPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(30, 30, 30)));
            
            JButton btnHeader = new JButton(f.funkrufname + ": " + besatzung.size() + "/" + reqs.size() + " Mann");
            btnHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnHeader.setFocusPainted(false);
            btnHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnHeader.setForeground(Color.WHITE);
            btnHeader.setBackground(isReady ? new Color(39, 174, 96) : new Color(192, 57, 43));
            btnHeader.setMaximumSize(new Dimension(300, 35));
            btnHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
            detailsPanel.setBackground(new Color(40, 40, 40));
            detailsPanel.setVisible(false);
            
            if (!isReady) {
                ArrayList<String> missing = new ArrayList<>(reqs);
                for(Personal p : besatzung) {
                    for(int i = 0; i < missing.size(); i++) {
                        if(personErfuellt(p, missing.get(i))) { missing.remove(i); break; }
                    }
                }
                HashMap<String, Integer> mCount = new HashMap<>();
                for(String m : missing) mCount.put(m, mCount.getOrDefault(m, 0) + 1);
                for(String k : mCount.keySet()) {
                    JLabel lblFehlt = new JLabel(" FEHLT: " + mCount.get(k) + "x " + k);
                    lblFehlt.setForeground(Color.ORANGE);
                    lblFehlt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    detailsPanel.add(lblFehlt);
                }
            } else {
                JLabel lblOk = new JLabel(" Alle Positionen besetzt.");
                lblOk.setForeground(Color.LIGHT_GRAY);
                detailsPanel.add(lblOk);
            }
            
            btnHeader.addActionListener(e -> {
                detailsPanel.setVisible(!detailsPanel.isVisible());
                pnlFahrzeugBoxen.revalidate();
            });

            fzPanel.add(btnHeader);
            fzPanel.add(detailsPanel);
            pnlFahrzeugBoxen.add(fzPanel);
        }

        pnlFahrzeugBoxen.revalidate();
        pnlFahrzeugBoxen.repaint();
    }

    private static JButton createBtn(String t, Color c) {
        JButton b = new JButton(t); b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }
}