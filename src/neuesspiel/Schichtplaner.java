package neuesspiel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static neuesspiel.LogistikSimulator.*;

public class Schichtplaner {

    public static void updateFahrzeugUebersicht(JEditorPane area, DefaultTableModel model, boolean isMorgen) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='background-color:#2b2b2b; color:#a9b7c6; font-family:sans-serif; font-size:11px; margin:0; padding:5px;'>");
        sb.append("<h3 style='color:white; margin-top:0; margin-bottom:10px;'>FAHRZEUGE (").append(isMorgen ? "Morgen" : "Heute").append(")</h3>");
        
        ArrayList<Personal> tempAvail = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String qualStr = (String) model.getValueAt(i, 2);
            String status = (String) model.getValueAt(i, 3);
            String fz = (String) model.getValueAt(i, 4);
            
            boolean isValidStatus = status.equals("Bereit") || status.equals("Frei");
            
            if (!fz.equals("Keines") && isValidStatus) {
                Personal fake = new Personal("Tmp", "TM");
                fake.qualifikationen.clear();
                fake.qualifikationen.addAll(Arrays.asList(qualStr.split(", ")));
                fake.zugewiesenesFahrzeug = fz;
                tempAvail.add(fake);
            }
        }
        
        for(Wache w : wachen) {
            for (Fahrzeug f : w.fuhrpark) {
                ArrayList<String> reqs = getRequiredRoles(f);
                ArrayList<Personal> fzAvail = new ArrayList<>();
                for(Personal p : tempAvail) if(p.zugewiesenesFahrzeug.equals(f.funkrufname)) fzAvail.add(p);
                
                ArrayList<String> missing = new ArrayList<>();
                for(String r : reqs) {
                    boolean found = false;
                    for(int i=0; i<fzAvail.size(); i++) {
                        if(LogistikSimulator.personErfuellt(fzAvail.get(i), r)) { fzAvail.remove(i); found = true; break; }
                    }
                    if(!found) missing.add(r);
                }
                
                if (missing.isEmpty()) {
                    sb.append("<div style='background-color:#27ae60; color:white; padding:5px; margin-bottom:5px; border-radius:3px;'>");
                    sb.append("<b>[").append(f.typ).append("] ").append(f.funkrufname).append("</b> (").append(w.name).append(")<br>-> Einsatzbereit!</div>");
                } else {
                    sb.append("<div style='background-color:#c0392b; color:white; padding:5px; margin-bottom:5px; border-radius:3px;'>");
                    sb.append("<b>[").append(f.typ).append("] ").append(f.funkrufname).append("</b> (").append(w.name).append(")<br>-> FEHLT: ");
                    HashMap<String, Integer> mCount = new HashMap<>();
                    for(String m : missing) mCount.put(m, mCount.getOrDefault(m, 0)+1);
                    ArrayList<String> mList = new ArrayList<>();
                    for(String k : mCount.keySet()) mList.add(mCount.get(k) + "x " + k);
                    sb.append(String.join(", ", mList));
                    sb.append("</div>");
                }
            }
        }
        sb.append("</body></html>");
        area.setText(sb.toString());
    }

    public static void oeffneSchichtplan() {
        ArrayList<Personal> alleMitarbeiter = new ArrayList<>();
        ArrayList<String> wachenNamen = new ArrayList<>();
        
        for(Wache w : wachen) {
            for(Personal p : w.personalPool) {
                alleMitarbeiter.add(p); wachenNamen.add(w.name);
            }
        }
        
        JDialog d = new JDialog(frame, "Schichtplan / Personalwesen", true);
        d.setSize(1200, 600); d.setLayout(new BorderLayout()); d.setLocationRelativeTo(frame);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); filterPanel.setBackground(new Color(20, 30, 48));
        JComboBox<String> cbTag = new JComboBox<>(new String[]{"AKTUELL / Heute", "Naechster Tag"}); 
        
        JComboBox<String> cbWache = new JComboBox<>(); cbWache.addItem("Alle Wachen");
        for(Wache w : wachen) cbWache.addItem(w.name);
        if(wachen.size() > 0) cbWache.setSelectedIndex(1); 
        
        JComboBox<String> cbFahrzeug = new JComboBox<>(); cbFahrzeug.addItem("Alle Fahrzeuge");
        for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) cbFahrzeug.addItem(f.funkrufname);
        cbFahrzeug.addItem("Keines"); 
        
        JComboBox<String> cbPosition = new JComboBox<>(new String[]{"Alle", "NA", "NFS", "RS", "GF", "MA", "TF", "TM", "EL", "FüAs"}); 
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Alle", "Bereit", "Frei", "Urlaub", "Krank", "Lehrgang"});
        
        filterPanel.add(new JLabel("Tag:")); filterPanel.add(cbTag); 
        filterPanel.add(new JLabel("Heimatwache:")); filterPanel.add(cbWache); 
        filterPanel.add(new JLabel("Fahrzeug:")); filterPanel.add(cbFahrzeug); 
        filterPanel.add(new JLabel("Position:")); filterPanel.add(cbPosition); 
        filterPanel.add(new JLabel("Status:")); filterPanel.add(cbStatus);
        
        String[] columns = {"Heimatwache", "Mitarbeiter", "Qualifikationen", "Status", "Fahrzeugzuweisung"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) { 
            @Override public boolean isCellEditable(int row, int column) { return column == 3 || column == 4; } 
        };
        
        boolean initialMorgen = cbTag.getSelectedIndex() == 1;
        for (int i = 0; i < alleMitarbeiter.size(); i++) {
            Personal p = alleMitarbeiter.get(i); String wName = wachenNamen.get(i);
            model.addRow(new Object[]{wName, p.name, String.join(", ", p.qualifikationen), initialMorgen ? p.geplanterStatus : p.status, initialMorgen ? p.geplantesFahrzeug : p.zugewiesenesFahrzeug}); 
        }
        
        JTable table = new JTable(model); 
        table.setRowHeight(30); table.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        table.getTableHeader().setBackground(new Color(20, 30, 48)); table.getTableHeader().setForeground(Color.WHITE);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model); table.setRowSorter(sorter);
        
        Runnable applyFilters = () -> {
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            String wacheFilter = (String) cbWache.getSelectedItem(); if (!"Alle Wachen".equals(wacheFilter)) filters.add(RowFilter.regexFilter("^" + wacheFilter + "$", 0)); 
            String fzFilter = (String) cbFahrzeug.getSelectedItem(); if (!"Alle Fahrzeuge".equals(fzFilter)) filters.add(RowFilter.regexFilter("^" + fzFilter + "$", 4)); 
            String posFilter = (String) cbPosition.getSelectedItem(); if (!"Alle".equals(posFilter)) filters.add(RowFilter.regexFilter("\\b" + posFilter + "\\b", 2)); 
            String statusFilter = (String) cbStatus.getSelectedItem(); if (!"Alle".equals(statusFilter)) filters.add(RowFilter.regexFilter("^" + statusFilter + "$", 3)); 
            sorter.setRowFilter(RowFilter.andFilter(filters));
        };

        cbWache.addActionListener(e -> applyFilters.run()); cbFahrzeug.addActionListener(e -> applyFilters.run()); 
        cbPosition.addActionListener(e -> applyFilters.run()); cbStatus.addActionListener(e -> applyFilters.run());

        JComboBox<String> statEditor = new JComboBox<>(new String[]{"Bereit", "Frei", "Urlaub"});
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(statEditor));

        JComboBox<String> fzEditor = new JComboBox<>(); fzEditor.addItem("Keines"); 
        for(Wache w : wachen) for(Fahrzeug f : w.fuhrpark) fzEditor.addItem(f.funkrufname);
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(fzEditor));
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 3); 
                String fz = (String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
                
                if (column == 4 || column == 3) {
                    if (status.equals("Krank")) { c.setBackground(new Color(231, 76, 60)); c.setForeground(Color.WHITE); } 
                    else if (status.equals("Urlaub")) { c.setBackground(new Color(243, 156, 18)); c.setForeground(Color.BLACK); } 
                    else if (status.equals("Lehrgang")) { c.setBackground(new Color(155, 89, 182)); c.setForeground(Color.WHITE); } 
                    else if (status.equals("Frei")) { c.setBackground(new Color(52, 152, 219)); c.setForeground(Color.WHITE); } 
                    else if (!fz.equals("Keines")) { c.setBackground(new Color(46, 204, 113)); c.setForeground(Color.BLACK); } 
                    else { c.setBackground(Color.WHITE); c.setForeground(Color.BLACK); }
                } else { c.setBackground(new Color(43, 43, 43)); c.setForeground(Color.WHITE); } 
                return c;
            }
        });

        JEditorPane fzUebersicht = new JEditorPane("text/html", ""); fzUebersicht.setEditable(false);
        JScrollPane scrollUebersicht = new JScrollPane(fzUebersicht); scrollUebersicht.setPreferredSize(new Dimension(300, 0)); scrollUebersicht.setBorder(BorderFactory.createEmptyBorder());
        
        boolean[] isUpdating = {false}; 
        JButton btnSave = new JButton("Schichtplan Speichern & Schliessen"); btnSave.setBackground(new Color(39, 174, 96));
        
        JButton btnClear = new JButton("Einteilung loeschen");
        btnClear.setBackground(new Color(192, 57, 43)); 
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(e -> {
            int wahl = JOptionPane.showConfirmDialog(d, 
                "Moechtest du die aktuell angezeigte Schichtplanung komplett leeren?", 
                "Einteilung loeschen", JOptionPane.YES_NO_OPTION);
                
            if (wahl == JOptionPane.YES_OPTION) {
                boolean isHeute = cbTag.getSelectedIndex() == 0; 
                
                for (Wache w : LogistikSimulator.wachen) {
                    for (Personal p : w.personalPool) {
                        if (isHeute) {
                            p.zugewiesenesFahrzeug = "Keines";
                        } else {
                            p.geplantesFahrzeug = "Keines";
                        }
                    }
                    
                    if (isHeute) {
                        for (Fahrzeug f : w.fuhrpark) {
                            if (f.status == 1 || f.status == 2) {
                                f.status = 6;
                                f.ausfallGrund = "Personal fehlt";
                            }
                        }
                    }
                }
                
                // Aktualisiere die UI durch Neuladen der Zeilen
                isUpdating[0] = true;
                for (int i = 0; i < model.getRowCount(); i++) {
                    Personal p = alleMitarbeiter.get(i);
                    model.setValueAt(isHeute ? p.zugewiesenesFahrzeug : p.geplantesFahrzeug, i, 4);
                }
                updateFahrzeugUebersicht(fzUebersicht, model, !isHeute);
                isUpdating[0] = false;
                
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                SpeicherManager.speichern("savegame.properties");
            }
        });
        
        JButton btnCopy = new JButton("Schicht von Heute uebernehmen"); 
        btnCopy.setBackground(new Color(41, 128, 185)); btnCopy.setVisible(initialMorgen);
        btnCopy.addActionListener(e -> {
            isUpdating[0] = true;
            for(int i = 0; i < model.getRowCount(); i++) {
                Personal p = alleMitarbeiter.get(i);
                p.geplantesFahrzeug = p.zugewiesenesFahrzeug; 
                p.geplanterStatus = p.status.equals("Krank") ? "Bereit" : p.status; 
                model.setValueAt(p.geplanterStatus, i, 3);
                model.setValueAt(p.geplantesFahrzeug, i, 4);
            }
            updateFahrzeugUebersicht(fzUebersicht, model, true);
            isUpdating[0] = false;
        });
        
        cbTag.addActionListener(e -> {
            isUpdating[0] = true; boolean isMorgen = cbTag.getSelectedIndex() == 1; btnCopy.setVisible(isMorgen); 
            for(int i = 0; i < model.getRowCount(); i++) {
                Personal p = alleMitarbeiter.get(i);
                model.setValueAt(isMorgen ? p.geplanterStatus : p.status, i, 3);
                model.setValueAt(isMorgen ? p.geplantesFahrzeug : p.zugewiesenesFahrzeug, i, 4);
            }
            applyFilters.run(); updateFahrzeugUebersicht(fzUebersicht, model, isMorgen);
            isUpdating[0] = false;
        });

        model.addTableModelListener(e -> {
            if(isUpdating[0]) return;
            boolean isMorgen = cbTag.getSelectedIndex() == 1; int row = e.getFirstRow();
            if (row >= 0) {
                Personal p = alleMitarbeiter.get(row);
                String statVal = (String) model.getValueAt(row, 3); String fzVal = (String) model.getValueAt(row, 4);
                if((statVal.equals("Urlaub") || statVal.equals("Lehrgang") || statVal.equals("Krank")) && !fzVal.equals("Keines")) {
                    isUpdating[0] = true; fzVal = "Keines"; model.setValueAt("Keines", row, 4); isUpdating[0] = false;
                }
                if (isMorgen) { p.geplanterStatus = statVal; p.geplantesFahrzeug = fzVal; } else { p.status = statVal; p.zugewiesenesFahrzeug = fzVal; }
                updateFahrzeugUebersicht(fzUebersicht, model, isMorgen);
            }
        });
        
        applyFilters.run(); updateFahrzeugUebersicht(fzUebersicht, model, initialMorgen); 

        btnSave.addActionListener(e -> {
            for(Wache w : wachen) {
                for(Fahrzeug f : w.fuhrpark) {
                    if(f.status == 6 && (f.ausfallGrund.startsWith("Krankheit") || f.ausfallGrund.equals("Personal fehlt"))) {
                        if(hatGenugPersonal(f)) { 
                            f.ausfallGrund = "Personalwechsel"; 
                            
                            int wechselDauer = 30;
                            for (Personal p : w.personalPool) {
                                if (p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) {
                                    wechselDauer = 60; 
                                    break;
                                }
                            }
                            f.reparaturDauer = wechselDauer; 
                        }
                    }
                }
            }
            SpeicherManager.speichern("savegame.properties"); d.dispose(); uiAktualisieren(getUhrzeit());
        });
        
        JPanel mainContent = new JPanel(new BorderLayout()); mainContent.add(scrollUebersicht, BorderLayout.WEST); mainContent.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Hier wurde der Button nun korrekt eingefügt
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        bottomButtons.add(btnClear); 
        bottomButtons.add(btnCopy); 
        bottomButtons.add(btnSave);
        
        d.add(filterPanel, BorderLayout.NORTH); d.add(mainContent, BorderLayout.CENTER); d.add(bottomButtons, BorderLayout.SOUTH); d.setVisible(true);
    }
}