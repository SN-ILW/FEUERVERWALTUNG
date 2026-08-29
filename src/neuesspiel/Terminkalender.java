package neuesspiel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

public class Terminkalender {

    private static JDialog d;
    private static JTable table;
    private static DefaultTableModel tableModel;
    private static JTable rowHeaderTable;
    private static DefaultTableModel rowHeaderModel;
    
    // Speichert den Kalender fuer ca. 2 Monate (62 Tage, 25 Zeit-Slots von 07:00 bis 19:00)
    public static String[][] kalenderDaten = new String[62][25]; 

    public static void oeffneKalender() {
        if (LogistikSimulator.wachen.isEmpty()) return;
        Wache w = LogistikSimulator.wachen.get(0);

        d = new JDialog(LogistikSimulator.frame, "Terminkalender", true);
        d.setUndecorated(true);
        d.setSize(1400, 800);
        d.setLocationRelativeTo(LogistikSimulator.frame);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(new Color(35, 35, 35));

        // --- HIER EINFÜGEN: ESC TASTE ZUM SCHLIESSEN & SPEICHERN ---
        d.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeDialog");
        d.getRootPane().getActionMap().put("closeDialog", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SpeicherManager.speichern("savegame.properties");
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                d.dispose();
            }
        });
        // -----------------------------------------------------------

        // --- TITELLEISTE ---
        JPanel titleBar = new JPanel(new BorderLayout());
        // ... (hier geht dein normaler Code weiter)
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        JLabel lblTitle = new JLabel(" Terminkalender & Wach-Events (60-Tage Vorschau)");
        lblTitle.setForeground(new Color(241, 196, 15));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleBar.add(lblTitle, BorderLayout.WEST);
        
        JButton btnCloseTop = new JButton("X");
        btnCloseTop.setBackground(new Color(192, 57, 43)); btnCloseTop.setForeground(Color.WHITE);
        btnCloseTop.addActionListener(e -> d.dispose());
        titleBar.add(btnCloseTop, BorderLayout.EAST);
        d.add(titleBar, BorderLayout.NORTH);

        // --- WERKZEUGLEISTE ---
        JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topMenu.setBackground(new Color(25, 25, 25));
        
        // Monat waehlen (Scrollt automatisch in der Tabelle)
        LinkedHashMap<String, Integer> monthToCol = new LinkedHashMap<>();
        for(int i = 0; i < 62; i++) {
            LocalDate date = LogistikSimulator.getCurrentDate().plusDays(i);
            String monthName = date.format(DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.GERMAN));
            if(!monthToCol.containsKey(monthName)) {
                monthToCol.put(monthName, i);
            }
        }
        
        JLabel lblMonat = new JLabel("Monat:"); lblMonat.setForeground(Color.WHITE);
        JComboBox<String> cbMonat = new JComboBox<>(monthToCol.keySet().toArray(new String[0]));
        cbMonat.addActionListener(e -> {
            String sel = (String) cbMonat.getSelectedItem();
            if(sel != null && monthToCol.containsKey(sel)) {
                int col = monthToCol.get(sel);
                table.scrollRectToVisible(table.getCellRect(0, col, true));
            }
        });
        
        JLabel lblFz = new JLabel("1. Fahrzeug:"); lblFz.setForeground(Color.WHITE);
        JComboBox<String> cbFahrzeug = new JComboBox<>();
        cbFahrzeug.addItem("Kein Fahrzeug");
        for (Fahrzeug f : w.fuhrpark) cbFahrzeug.addItem(f.funkrufname);
        
        JLabel lblPers = new JLabel("2. Personal:"); lblPers.setForeground(Color.WHITE);
        JComboBox<String> cbPersonal = new JComboBox<>();
        cbPersonal.addItem("Kein Personal"); 
        for (Personal p : w.personalPool) cbPersonal.addItem(p.name);
        
        JLabel lblHint = new JLabel(" (Ohne Fz/Pers: Eigener Text! Links=Eintragen, Rechts=Loeschen)");
        lblHint.setForeground(Color.GRAY); lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        topMenu.add(lblMonat); topMenu.add(cbMonat);
        topMenu.add(new JLabel("  |  ")); // Optischer Trenner
        topMenu.add(lblFz); topMenu.add(cbFahrzeug);
        topMenu.add(lblPers); topMenu.add(cbPersonal);
        topMenu.add(lblHint);

        // --- TABELLE ERSTELLEN ---
        tableModel = new DefaultTableModel(0, 62) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(new Color(20, 30, 48));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Spalten (62 rollierende Tage)
        String[] tage = new String[62];
        for(int i=0; i<62; i++) {
            String datum = LogistikSimulator.getShortDatumString(LogistikSimulator.tag + i);
            if (i == 0) tage[i] = "HEUTE (" + datum + ")";
            else if (i == 1) tage[i] = "MORGEN (" + datum + ")";
            else tage[i] = datum;
        }
        tableModel.setColumnIdentifiers(tage);
        for(int i=0; i<62; i++) table.getColumnModel().getColumn(i).setPreferredWidth(180);

        // Zeilen-Header (Uhrzeiten im 30-Min-Takt)
        rowHeaderModel = new DefaultTableModel(0, 1) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        rowHeaderModel.setColumnIdentifiers(new String[]{"Uhrzeit"});
        rowHeaderTable = new JTable(rowHeaderModel);
        rowHeaderTable.setRowHeight(35);
        rowHeaderTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        rowHeaderTable.getTableHeader().setBackground(new Color(20, 30, 48));
        rowHeaderTable.getTableHeader().setForeground(Color.WHITE);
        
        // Uhrzeiten generieren (07:00 bis 19:00 = 25 Slots)
        for(int i = 0; i <= 24; i++) {
            int stunde = 7 + (i / 2);
            String minute = (i % 2 == 0) ? "00" : "30";
            rowHeaderModel.addRow(new Object[]{String.format("%02d:%s Uhr", stunde, minute)});
            
            // Daten aus dem Array laden
            String[] zeile = new String[62];
            for(int tagIdx = 0; tagIdx < 62; tagIdx++) {
                zeile[tagIdx] = kalenderDaten[tagIdx][i] != null ? kalenderDaten[tagIdx][i] : "";
            }
            tableModel.addRow(zeile);
        }

        rowHeaderTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(new Color(45, 45, 45)); c.setForeground(new Color(241, 196, 15));
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = (String) value;
                
                if (val == null || val.isEmpty()) {
                    c.setBackground(new Color(50, 50, 50)); c.setForeground(Color.WHITE);
                } else if (val.contains("KTP")) {
                    c.setBackground(new Color(52, 152, 219)); c.setForeground(Color.WHITE); 
                } else if (val.contains("Event") || val.contains("Schule") || val.contains("Firma")) {
                    c.setBackground(new Color(155, 89, 182)); c.setForeground(Color.WHITE); 
                } else {
                    // NEU: Dynamische Farberkennung des Fahrzeugs!
                    boolean colorFound = false;
                    for (Wache wache : LogistikSimulator.wachen) {
                        for (Fahrzeug f : wache.fuhrpark) {
                            if (val.contains(f.funkrufname)) {
                                c.setBackground(f.stempelFarbe != null ? f.stempelFarbe : new Color(192, 57, 43));
                                c.setForeground(Color.WHITE);
                                colorFound = true;
                                break;
                            }
                        }
                        if (colorFound) break;
                    }
                    
                    // Fallback, falls nur freier Text oder nur Personal eingetragen wurde
                    if(!colorFound) {
                        c.setBackground(new Color(46, 204, 113)); c.setForeground(Color.BLACK); 
                    }
                }
                return c;
            }
        });

        // --- STEMPEL-FUNKTION (Mit Texterkennung und Halten) ---
        MouseAdapter stampAdapter = new MouseAdapter() {
            private String currentDragText = null; 

            @Override 
            public void mousePressed(MouseEvent e) {
                currentDragText = null; 
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col >= 0) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        tableModel.setValueAt("", row, col); 
                        kalenderDaten[col][row] = "";
                    } else if (SwingUtilities.isLeftMouseButton(e)) {
                        String fz = cbFahrzeug.getSelectedItem().toString();
                        String pers = cbPersonal.getSelectedItem().toString();
                        
                        String eintrag = "";
                        if (fz.startsWith("Kein") && pers.startsWith("Kein")) {
                            String input = JOptionPane.showInputDialog(d, "Bitte eigenen Text fuer diesen Termin eingeben:", "Eigener Termin", JOptionPane.PLAIN_MESSAGE);
                            if(input != null && !input.trim().isEmpty()) {
                                currentDragText = input.trim();
                            } else {
                                return; 
                            }
                        } else if (fz.startsWith("Kein")) {
                            currentDragText = pers; 
                        } else if (pers.startsWith("Kein")) {
                            currentDragText = fz + " (Dienstplan-Personal)"; 
                        } else {
                            currentDragText = fz + " (" + pers + ")"; 
                        }
                        
                        if(currentDragText != null && !currentDragText.isEmpty()) {
                            tableModel.setValueAt(currentDragText, row, col);
                            kalenderDaten[col][row] = currentDragText;
                        }
                    }
                }
            }

            @Override 
            public void mouseDragged(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col >= 0) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        tableModel.setValueAt("", row, col); 
                        kalenderDaten[col][row] = "";
                    } else if (SwingUtilities.isLeftMouseButton(e) && currentDragText != null) {
                        tableModel.setValueAt(currentDragText, row, col);
                        kalenderDaten[col][row] = currentDragText;
                    }
                }
            }
        };
        table.addMouseListener(stampAdapter);
        table.addMouseMotionListener(stampAdapter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setRowHeaderView(rowHeaderTable);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(topMenu, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        d.add(centerContainer, BorderLayout.CENTER);

        // --- BOTTOM PANEL ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(25, 25, 25));
        JButton btnSave = new JButton("Speichern & Schliessen");
        btnSave.setBackground(new Color(39, 174, 96)); btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> d.dispose());
        bottomPanel.add(btnSave);
        d.add(bottomPanel, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    public static void autoEintragen(int zielTag, int startSlot, int dauerSlots, String text) {
        int colIndex = zielTag - LogistikSimulator.tag; 
        if(colIndex < 0 || colIndex >= 62) return;
        
        for(int i = 0; i < dauerSlots; i++) {
            if((startSlot + i) <= 24) {
                kalenderDaten[colIndex][startSlot + i] = text;
            }
        }
    }
    
    public static void tagesWechselShift() {
        for(int col = 0; col < 61; col++) {
            for(int row = 0; row < 25; row++) {
                kalenderDaten[col][row] = kalenderDaten[col+1][row];
            }
        }
        for(int row = 0; row < 25; row++) {
            kalenderDaten[61][row] = "";
        }
    }
}