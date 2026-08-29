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
    private static JScrollPane scrollBoxen; 
    
    private static JTable rowHeaderTable;
    private static DefaultTableModel rowHeaderModel;
    
    private static int ausgewaehlterTagIndex = 0; 

    // --- HELFER FÜR DIE 2-TAGE REGEL ---
    private static boolean isWorking(String s) {
        return s != null && !s.equals("Frei") && !s.equals("Krank") && !s.equals("Urlaub") && !s.equals("Lehrgang");
    }

    private static boolean canWorkToday(String[] plan, int day) {
        int max = plan.length;
        
        // Prüfe ob die 2 Tage davor gearbeitet wurde
        if (day >= 2 && isWorking(plan[day - 1]) && isWorking(plan[day - 2])) return false;
        
        // Prüfe ob die 2 Tage danach gearbeitet wird (verhindert Block-Bildung bei manuellen Einträgen)
        if (day <= max - 3 && isWorking(plan[day + 1]) && isWorking(plan[day + 2])) return false;
        
        // Prüfe ob 1 Tag davor und 1 Tag danach gearbeitet wird (wäre auch 3 am Stück)
        if (day >= 1 && day <= max - 2 && isWorking(plan[day - 1]) && isWorking(plan[day + 1])) return false;
        
        return true; 
    }

    private static int getSchichtenImPlan(Personal p, boolean aktuell) {
        String[] plan = aktuell ? p.planAktuellerMonat : p.planNaechsterMonat;
        int count = 0;
        for (String s : plan) {
            if (isWorking(s)) count++; 
        }
        return count;
    }
    
    private static void updateSchichtenAnzeige() {
        if (rowHeaderModel == null || tableModel == null) return;
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            int count = 0;
            for (int c = 0; c < tableModel.getColumnCount(); c++) {
                String val = (String) tableModel.getValueAt(r, c);
                if (isWorking(val)) count++;
            }
            rowHeaderModel.setValueAt(count + "x", r, 2);
        }
    }

    public static void oeffneSchichtplan() {
        if (wachen.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Wachen vorhanden!"); return; }
        aktuelleWache = wachen.get(0);
        zeigeAktuellenMonat = true;
        ausgewaehlterTagIndex = 0; 

        JDialog d = new JDialog(frame, "Dienstplan", true);
        d.setUndecorated(true);
        d.setSize(1400, 800);
        d.setLocationRelativeTo(frame);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(35, 35, 35));
        
        d.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        d.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SpeicherManager.speichern("savegame.properties");
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                d.dispose();
            }
        });
        
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

        JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topMenu.setBackground(new Color(25, 25, 25));

        JLabel lblMonat = new JLabel("Ansicht: Aktueller Monat");
        lblMonat.setForeground(Color.WHITE); lblMonat.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnAktuell = createBtn("Aktueller Monat", new Color(41, 128, 185));
        JButton btnFolge = createBtn("Folge-Monat", new Color(39, 174, 96));
        JButton btnCopy = createBtn("Aktuellen kopieren", new Color(211, 84, 0));
        
        JButton btnAutoTag = createBtn("Auto-Fill (Nur Tag)", new Color(155, 89, 182));
        JButton btnAuto = createBtn("Auto-Fill (Ganzen Monat)", new Color(142, 68, 173));

        topMenu.add(lblMonat); topMenu.add(Box.createHorizontalStrut(20));
        topMenu.add(btnAktuell); topMenu.add(btnFolge); topMenu.add(btnCopy); 
        topMenu.add(btnAutoTag); topMenu.add(btnAuto);

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
        stempelModel.addElement("Bereitschaft");
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

        tableModel = new DefaultTableModel(0, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(new Color(20, 30, 48));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
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
        
        rowHeaderModel = new DefaultTableModel(0, 3) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        rowHeaderModel.setColumnIdentifiers(new String[]{"Name", "Qualifikationen", "Schichten"});
        rowHeaderTable = new JTable(rowHeaderModel);
        rowHeaderTable.setRowHeight(28);
        rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        rowHeaderTable.getTableHeader().setBackground(new Color(20, 30, 48));
        rowHeaderTable.getTableHeader().setForeground(Color.WHITE);
        rowHeaderTable.getTableHeader().setReorderingAllowed(false);
        
        rowHeaderTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        rowHeaderTable.getColumnModel().getColumn(0).setMinWidth(130);
        rowHeaderTable.getColumnModel().getColumn(0).setMaxWidth(130);
        
        rowHeaderTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        rowHeaderTable.getColumnModel().getColumn(1).setMinWidth(140);
        rowHeaderTable.getColumnModel().getColumn(1).setMaxWidth(140);
        
        rowHeaderTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        rowHeaderTable.getColumnModel().getColumn(2).setMinWidth(80);
        rowHeaderTable.getColumnModel().getColumn(2).setMaxWidth(80);
        
        rowHeaderTable.setPreferredScrollableViewportSize(new Dimension(350, 0));
        
        rowHeaderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(40, 40, 40)); 
                
                if (column == 2) {
                    c.setForeground(new Color(241, 196, 15));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    c.setForeground(Color.WHITE);
                    setHorizontalAlignment(SwingConstants.LEFT);
                    setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
                return c;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = (String) value;
                
                if (val == null) {
                    c.setBackground(new Color(52, 152, 219)); c.setForeground(Color.WHITE);
                    return c;
                }
                
                if (val.equals("Krank")) { c.setBackground(new Color(231, 76, 60)); c.setForeground(Color.WHITE); }
                else if (val.equals("Urlaub")) { c.setBackground(new Color(243, 156, 18)); c.setForeground(Color.BLACK); }
                else if (val.equals("Lehrgang")) { c.setBackground(new Color(155, 89, 182)); c.setForeground(Color.WHITE); }
                else if (val.equals("Bereitschaft")) { c.setBackground(new Color(241, 196, 15)); c.setForeground(Color.BLACK); }
                else if (val.equals("Frei")) { c.setBackground(new Color(52, 152, 219)); c.setForeground(Color.WHITE); }
                else { 
                    boolean farbeGefunden = false;
                    for (Wache w : wachen) {
                        for (Fahrzeug f : w.fuhrpark) {
                            if (f.funkrufname.equals(val)) {
                                if (f.stempelFarbe != null) {
                                    c.setBackground(f.stempelFarbe);
                                    double luma = (0.299 * f.stempelFarbe.getRed()) + (0.587 * f.stempelFarbe.getGreen()) + (0.114 * f.stempelFarbe.getBlue());
                                    if (luma > 140) {
                                        c.setForeground(Color.BLACK); 
                                    } else {
                                        c.setForeground(Color.WHITE); 
                                    }
                                } else {
                                    c.setBackground(new Color(46, 204, 113)); 
                                    c.setForeground(Color.BLACK);
                                }
                                farbeGefunden = true;
                                break;
                            }
                        }
                        if (farbeGefunden) break;
                    }
                    if (!farbeGefunden) {
                        c.setBackground(new Color(46, 204, 113)); 
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        MouseAdapter stampAdapter = new MouseAdapter() {
            private void paintCell(MouseEvent e, boolean isClick) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    ausgewaehlterTagIndex = col;

                    int heutigerTagIndex = LogistikSimulator.getCurrentDate().getDayOfMonth() - 1;
                    if (zeigeAktuellenMonat && col < heutigerTagIndex) {
                        if (isClick) {
                            JOptionPane.showMessageDialog(table, "Vergangene Schichten koennen rueckwirkend nicht mehr geaendert werden!", "Tag gesperrt", JOptionPane.WARNING_MESSAGE);
                        }
                        return;
                    }

                    String werkzeug = SwingUtilities.isRightMouseButton(e) ? "Frei" : stempelListe.getSelectedValue();
                    if(werkzeug != null) {
                        tableModel.setValueAt(werkzeug, row, col);
                        updateFahrzeugInfo();
                        updateSchichtenAnzeige(); 
                    }
                }
            }
            
            @Override public void mousePressed(MouseEvent e) { paintCell(e, true); }
            @Override public void mouseDragged(MouseEvent e) { paintCell(e, false); }
        };
        table.addMouseListener(stampAdapter);
        table.addMouseMotionListener(stampAdapter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setRowHeaderView(rowHeaderTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(25, 25, 25));
        JButton btnClose = createBtn("Abbrechen", new Color(192, 57, 43));
        JButton btnSave = createBtn("Speichern & Schliessen", new Color(39, 174, 96));
        bottomPanel.add(btnClose); bottomPanel.add(btnSave);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(topMenu, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        d.add(leftPanel, BorderLayout.WEST);
        d.add(centerContainer, BorderLayout.CENTER);
        d.add(bottomPanel, BorderLayout.SOUTH);

        cbWachen.addActionListener(e -> {
            saveTableData();
            aktuelleWache = wachen.get(cbWachen.getSelectedIndex());
            stempelModel.clear();
            stempelModel.addElement("Frei"); 
            stempelModel.addElement("Bereitschaft");
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
        
        btnAutoTag.addActionListener(e -> {
            saveTableData(); 
            int day = ausgewaehlterTagIndex;
            
            int heutigerTagIndex = LogistikSimulator.getCurrentDate().getDayOfMonth() - 1;
            if (zeigeAktuellenMonat && day < heutigerTagIndex) {
                JOptionPane.showMessageDialog(d, "Vergangene Schichten koennen nicht mehr automatisch geaendert werden!", "Fehler", JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Personal p : aktuelleWache.personalPool) {
                String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
                if (plan[day] == null) plan[day] = "Frei";
                if (!plan[day].equals("Krank") && !plan[day].equals("Urlaub") && !plan[day].equals("Lehrgang")) {
                    plan[day] = "Frei"; 
                }
            }
            
            ArrayList<Personal> fairesPersonal = new ArrayList<>(aktuelleWache.personalPool);
            fairesPersonal.sort(java.util.Comparator.comparingInt(p -> getSchichtenImPlan(p, zeigeAktuellenMonat)));
            
            for (Fahrzeug f : aktuelleWache.fuhrpark) {
                ArrayList<String> reqs = getRequiredRoles(f);
                ArrayList<String> missing = new ArrayList<>(reqs);
                
                for (Personal p : fairesPersonal) { 
                    String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
                    if (plan[day].equals("Frei") && canWorkToday(plan, day)) { 
                        for (int i = 0; i < missing.size(); i++) {
                            if (personErfuellt(p, missing.get(i))) {
                                missing.remove(i);
                                plan[day] = f.funkrufname; 
                                break; 
                            }
                        }
                    }
                }
            }
            
            // Bereitschaft einteilen: Ignoriert die canWorkToday-Regel (darf auch am 3. Tag sein!)
            int bereitschaftsZaehler = 0;
            for (Personal p : fairesPersonal) {
                String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
                if (plan[day].equals("Frei")) {
                    plan[day] = "Bereitschaft";
                    bereitschaftsZaehler++;
                    if (bereitschaftsZaehler >= 2) break;
                }
            }
            
            loadTableData(); 
            JOptionPane.showMessageDialog(d, "Dienstplan fuer Tag " + (day + 1) + " fair befuellt!");
        });

        btnAuto.addActionListener(e -> {
            saveTableData(); 
            int tageImMonat = tableModel.getColumnCount();
            
            for (int day = 0; day < tageImMonat; day++) {
                int heutigerTagIndex = LogistikSimulator.getCurrentDate().getDayOfMonth() - 1;
                if (zeigeAktuellenMonat && day < heutigerTagIndex) continue;

                for (Personal p : aktuelleWache.personalPool) {
                    String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
                    if (plan[day] == null) plan[day] = "Frei";
                    if (!plan[day].equals("Krank") && !plan[day].equals("Urlaub") && !plan[day].equals("Lehrgang")) {
                        plan[day] = "Frei"; 
                    }
                }
                
                ArrayList<Personal> fairesPersonal = new ArrayList<>(aktuelleWache.personalPool);
                fairesPersonal.sort(java.util.Comparator.comparingInt(p -> getSchichtenImPlan(p, zeigeAktuellenMonat)));
                
                for (Fahrzeug f : aktuelleWache.fuhrpark) {
                    ArrayList<String> reqs = getRequiredRoles(f);
                    ArrayList<String> missing = new ArrayList<>(reqs);
                    
                    for (Personal p : fairesPersonal) { 
                        String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
                        if (plan[day].equals("Frei") && canWorkToday(plan, day)) { 
                            for (int i = 0; i < missing.size(); i++) {
                                if (personErfuellt(p, missing.get(i))) {
                                    missing.remove(i);
                                    plan[day] = f.funkrufname; 
                                    break; 
                                }
                            }
                        }
                    }
                }
                
                // Bereitschaft einteilen: Ignoriert die canWorkToday-Regel!
                int bereitschaftsZaehler = 0;
                for (Personal p : fairesPersonal) {
                    String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
                    if (plan[day].equals("Frei")) {
                        plan[day] = "Bereitschaft";
                        bereitschaftsZaehler++;
                        if (bereitschaftsZaehler >= 2) break;
                    }
                }
            }
            loadTableData(); 
            JOptionPane.showMessageDialog(d, "Dienstplan fair generiert! Fahrzeug-Schichten mit Pausen & Bereitschaft verteilt.");
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
                        
                        if (!neuerPlan.equals("Frei") && !neuerPlan.equals("Bereitschaft") && !neuerPlan.equals(altesFz)) {
                            if (alterStatus.equals("Frei")) {
                                fzMitFreiemPersonal.add(neuerPlan);
                            }
                        }
                        
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
            rowHeaderModel.addRow(new Object[]{ p.name, String.join(", ", p.qualifikationen), "0" });
            
            String[] row = new String[tageImMonat];
            String[] plan = zeigeAktuellenMonat ? p.planAktuellerMonat : p.planNaechsterMonat;
            for (int i = 0; i < tageImMonat; i++) {
                row[i] = plan[i] != null ? plan[i] : "Frei";
            }
            tableModel.addRow(row);
        }
        
        updateFahrzeugInfo();
        updateSchichtenAnzeige(); 
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
        
        scrollBoxen.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Einteilung Tag " + (ausgewaehlterTagIndex + 1), javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));

        for (Fahrzeug f : aktuelleWache.fuhrpark) {
            ArrayList<String> reqs = getRequiredRoles(f);
            ArrayList<Personal> besatzung = new ArrayList<>();
            
            for (int i = 0; i < aktuelleWache.personalPool.size(); i++) {
                if (tableModel.getColumnCount() > ausgewaehlterTagIndex) {
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