package neuesspiel;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static neuesspiel.LogistikSimulator.*;
import static neuesspiel.FensterManager.createFramelessDialog;

public class MenuLogistik {

    public static void oeffneLogistikHauptmenu() {
        JDialog d = createFramelessDialog("Lager & Logistik", 400, 200);
        JPanel content = new JPanel(new GridLayout(3, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        content.setBackground(new Color(35, 35, 35));

        JButton b1 = new JButton("Einkauf (Lager fuellen)"); b1.addActionListener(e -> { d.dispose(); FensterManager.oeffneBestellMenu(); });
        JButton b2 = new JButton("Logistik (Wache versorgen)"); b2.addActionListener(e -> { d.dispose(); FensterManager.oeffneLogistikMenu(); });
        JButton b3 = new JButton("Material- & Lageruebersicht"); b3.addActionListener(e -> { d.dispose(); FensterManager.oeffneMaterialUebersicht(); });

        content.add(b1); content.add(b2); content.add(b3);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneMaterialUebersicht() {
        JDialog d = createFramelessDialog("Material- & Lageruebersicht", 800, 500);
        JPanel content = new JPanel(new BorderLayout(10,10)); content.setBackground(new Color(35,35,35));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); topPanel.setBackground(new Color(35, 35, 35));
        JLabel lblSearch = new JLabel("Nach Material suchen: "); lblSearch.setForeground(Color.WHITE);
        JTextField txtSearch = new JTextField(20); topPanel.add(lblSearch); topPanel.add(txtSearch);
        content.add(topPanel, BorderLayout.NORTH);

        ArrayList<String> cols = new ArrayList<>(); cols.add("Material"); cols.add("Warnschwelle"); cols.add("Hauptlager");
        for (Wache w : wachen) cols.add(w.name);

        DefaultTableModel model = new DefaultTableModel(cols.toArray(new String[0]), 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };

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
            private void filter() { String text = txtSearch.getText().trim(); sorter.setRowFilter(text.length() == 0 ? null : RowFilter.regexFilter("(?i)" + text)); }
        });

        content.add(new JScrollPane(table), BorderLayout.CENTER); d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneLogistikMenu() {
        JDialog d = createFramelessDialog("Wache versorgen", 400, 300);
        JPanel content = new JPanel(new GridLayout(0, 1, 5, 5));
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

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
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

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
        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5)); form.setBackground(new Color(35, 35, 35));
        
        JTextField txtName = new JTextField(); JSpinner sVerbrauch = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner sPreis = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 10)); JSpinner sMenge = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 10));
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
        pnlFz.add(cbFz_RTW); pnlFz.add(cbFz_HLF); pnlFz.add(cbFz_NEF); pnlFz.add(cbFz_KTW); pnlFz.add(cbFz_ELW); pnlFz.add(cbFz_DLK); pnlFz.add(cbFz_TLF); pnlFz.add(cbFz_MTW);
        form.add(pnlFz);

        JPanel topArea = new JPanel(new BorderLayout()); topArea.setBackground(new Color(35, 35, 35)); topArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        topArea.add(form, BorderLayout.CENTER); d.add(topArea, BorderLayout.CENTER);

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
            hauptlager.put(mat.name, 0); SpeicherManager.speichern("savegame.properties");
            JOptionPane.showMessageDialog(d, "Material hinzugefuegt!"); d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneMaterialBearbeiter() {
        if(customMaterials.isEmpty()) { JOptionPane.showMessageDialog(frame, "Keine Materialien!"); return; }
        JDialog d = createFramelessDialog("Material Bearbeiten", 500, 450);

        JPanel topSelect = new JPanel(new FlowLayout(FlowLayout.LEFT)); topSelect.setBackground(new Color(35, 35, 35));
        JLabel lblTop = new JLabel("Zu bearbeitendes Material:"); lblTop.setForeground(Color.WHITE); topSelect.add(lblTop);
        JComboBox<String> cMatWahl = new JComboBox<>(); for(CustomMaterial cm : customMaterials) cMatWahl.addItem(cm.name);
        topSelect.add(cMatWahl); d.add(topSelect, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5)); form.setBackground(new Color(35, 35, 35));
        JTextField txtName = new JTextField(); JSpinner sVerbrauch = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner sPreis = new JSpinner(new SpinnerNumberModel(500, 10, 10000, 10)); JSpinner sMenge = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 10)); JSpinner sWarn = new JSpinner(new SpinnerNumberModel(5, 0, 1000, 5));
        
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
        pnlFz.add(cbFz_RTW); pnlFz.add(cbFz_HLF); pnlFz.add(cbFz_NEF); pnlFz.add(cbFz_KTW); pnlFz.add(cbFz_ELW); pnlFz.add(cbFz_DLK); pnlFz.add(cbFz_TLF); pnlFz.add(cbFz_MTW);
        form.add(pnlFz);

        Runnable ladeMaterial = () -> {
            CustomMaterial cm = customMaterials.get(cMatWahl.getSelectedIndex());
            txtName.setText(cm.name); sVerbrauch.setValue(cm.maxVerbrauch); sPreis.setValue(cm.preis); sMenge.setValue(cm.bestellMenge); sWarn.setValue(cm.warnSchwelle);
            cbFz_RTW.setSelected(cm.fahrzeuge.contains("RTW")); cbFz_HLF.setSelected(cm.fahrzeuge.contains("HLF")); cbFz_NEF.setSelected(cm.fahrzeuge.contains("NEF")); cbFz_KTW.setSelected(cm.fahrzeuge.contains("KTW"));
            cbFz_ELW.setSelected(cm.fahrzeuge.contains("ELW")); cbFz_DLK.setSelected(cm.fahrzeuge.contains("DLK")); cbFz_TLF.setSelected(cm.fahrzeuge.contains("TLF")); cbFz_MTW.setSelected(cm.fahrzeuge.contains("MTW"));
        };
        ladeMaterial.run(); cMatWahl.addActionListener(e -> ladeMaterial.run());

        JPanel topArea = new JPanel(new BorderLayout()); topArea.setBackground(new Color(35, 35, 35)); topArea.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        topArea.add(form, BorderLayout.CENTER); d.add(topArea, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Aenderungen Speichern");
        btnAdd.addActionListener(e -> {
            if(txtName.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(d, "Name fehlt!"); return; }
            CustomMaterial cm = customMaterials.get(cMatWahl.getSelectedIndex());
            String oldWach = cm.name; int oldLag = hauptlager.getOrDefault(cm.name, 0); hauptlager.remove(cm.name);
            
            cm.name = txtName.getText().trim(); cm.maxVerbrauch = (int) sVerbrauch.getValue(); cm.preis = (int) sPreis.getValue(); cm.bestellMenge = (int) sMenge.getValue(); cm.warnSchwelle = (int) sWarn.getValue();
            
            cm.fahrzeuge.clear();
            if(cbFz_RTW.isSelected()) cm.fahrzeuge.add("RTW"); if(cbFz_HLF.isSelected()) cm.fahrzeuge.add("HLF");
            if(cbFz_NEF.isSelected()) cm.fahrzeuge.add("NEF"); if(cbFz_KTW.isSelected()) cm.fahrzeuge.add("KTW");
            if(cbFz_ELW.isSelected()) cm.fahrzeuge.add("ELW"); if(cbFz_DLK.isSelected()) cm.fahrzeuge.add("DLK");
            if(cbFz_TLF.isSelected()) cm.fahrzeuge.add("TLF"); if(cbFz_MTW.isSelected()) cm.fahrzeuge.add("MTW");
            
            hauptlager.put(cm.name, oldLag); SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, "Erfolgreich aktualisiert!"); d.dispose();
        });
        d.add(btnAdd, BorderLayout.SOUTH); d.setVisible(true);
    }

    public static void oeffneFuhrparkHauptmenu() {
        JDialog d = createFramelessDialog("Fuhrpark & Werkstatt", 400, 300); 
        JPanel content = new JPanel(new GridLayout(5, 1, 10, 10)); 
        content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35,35,35));

        JButton b1 = new JButton("Fahrzeuge verwalten / kaufen"); b1.addActionListener(e -> { d.dispose(); FensterManager.oeffneFuhrpark(); });
        JButton b2 = new JButton("Beschaedigtes Fahrzeug reparieren"); b2.addActionListener(e -> { d.dispose(); FensterManager.fahrzeugeReparieren(); });
        JButton b3 = new JButton("Fahrzeug umstationieren"); b3.addActionListener(e -> { d.dispose(); FensterManager.oeffneFahrzeugTransfer(); });
        JButton b4 = new JButton("TÜV & Inspektion durchfuehren"); b4.addActionListener(e -> { d.dispose(); LogistikSimulator.fahrzeugeInspektion(); });
        JButton b5 = new JButton("Fahrzeug-Uebersicht & Inspektion"); b5.addActionListener(e -> { d.dispose(); oeffneFahrzeugVerwaltung(); });
        
        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); 
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneFuhrpark() {
        if(wachen.isEmpty()){ JOptionPane.showMessageDialog(frame, "Du hast keine Wache!"); return; }
        JDialog d = createFramelessDialog("Fuhrpark verwalten", 400, 350);
        JPanel content = new JPanel(new GridLayout(9, 2, 10, 10)); content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

        JLabel l = new JLabel("Fuer welche Wache?"); l.setForeground(Color.WHITE); content.add(l);
        JComboBox<String> cbWachen = new JComboBox<>(); for(Wache w : wachen) cbWachen.addItem(w.name); content.add(cbWachen);

        JButton b1 = new JButton("ELW kaufen (2500 EURO)"); b1.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "ELW", 2500));
        JButton b2 = new JButton("HLF kaufen (2000 EURO)"); b2.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "HLF", 2000));
        JButton b3 = new JButton("DLK kaufen (2500 EURO)"); b3.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "DLK", 2500));
        JButton b4 = new JButton("RTW kaufen (2000 EURO)"); b4.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "RTW", 2000));
        JButton b5 = new JButton("NEF kaufen (2500 EURO)"); b5.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "NEF", 2500));
        JButton b6 = new JButton("KTW kaufen (1000 EURO)"); b6.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "KTW", 1000));
        JButton b7 = new JButton("TLF kaufen (2500 EURO)"); b7.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "TLF", 2500));
        JButton b8 = new JButton("MTW kaufen (1000 EURO)"); b8.addActionListener(e -> FensterManager.kaufFahrzeug(wachen.get(cbWachen.getSelectedIndex()), "MTW", 1000));

        content.add(b1); content.add(b2); content.add(b3); content.add(b4); content.add(b5); content.add(b6); content.add(b7); content.add(b8);
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }
    
    public static void oeffneFahrzeugTransfer() {
        if(wachen.size() < 2) { JOptionPane.showMessageDialog(frame, "Du brauchst mindestens zwei Wachen fuer einen Transfer!"); return; }
        
        JDialog d = createFramelessDialog("Fahrzeug umstationieren", 400, 200);
        JPanel content = new JPanel(new GridLayout(3, 2, 10, 10)); content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

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
            
            LogistikSimulator.sortiereFuhrpark(currentWache); LogistikSimulator.sortiereFuhrpark(zielWache);
            for(Personal p : currentWache.personalPool) { if(p.zugewiesenesFahrzeug.equals(alteKennung)) p.zugewiesenesFahrzeug = "Keines"; if(p.geplantesFahrzeug.equals(alteKennung)) p.geplantesFahrzeug = "Keines"; }
            targetFz.status = 6; targetFz.ausfallGrund = "Personal fehlt";
            SpeicherManager.speichern("savegame.properties"); JOptionPane.showMessageDialog(d, "Fahrzeug umstationiert! Neuer Funkrufname: " + targetFz.funkrufname + "\nAchtung: Personal wurde vom Fahrzeug entfernt.");
            uiAktualisieren(getUhrzeit()); d.dispose();
        });

        JLabel l1 = new JLabel("Fahrzeug waehlen:"); l1.setForeground(Color.WHITE); content.add(l1); content.add(cbFz);
        JLabel l2 = new JLabel("Zielwache waehlen:"); l2.setForeground(Color.WHITE); content.add(l2); content.add(cbZiel);
        content.add(new JLabel("")); content.add(btnTransfer); d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }
    
    public static void kaufFahrzeug(Wache w, String typ, int preis) {
        if(w.fuhrpark.size() >= getFahrzeugLimit(w.stufe)) {
            JOptionPane.showMessageDialog(frame, "Die Wache ist voll! (Stufe " + w.stufe + " erlaubt max. " + getFahrzeugLimit(w.stufe) + " Fahrzeuge).", "Fehler", JOptionPane.ERROR_MESSAGE); return;
        }
        if (budget >= preis) {
            budget -= preis; Fahrzeug f = new Fahrzeug(w.generiereFunkrufname(typ), typ); w.addFahrzeug(f);
            f.status = 6; f.ausfallGrund = "Personal fehlt"; sortiereFuhrpark(w); SpeicherManager.speichern("savegame.properties"); uiAktualisieren(getUhrzeit());
        } else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget!", "Fehler", JOptionPane.ERROR_MESSAGE); }
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
            boolean hatLokaleWerkstatt = false; if(targetW != null && targetW.upgrades != null) { for(WachenAusbau wa : targetW.upgrades) if(wa.id.equals("werkstatt")) hatLokaleWerkstatt = true; }
            
            int kosten = hatLokaleWerkstatt ? 500 : 1000;
            if (budget >= kosten) { budget -= kosten; targetF.ausfallGrund = "Wartet auf Reparatur"; targetF.reparaturDauer = 300; uiAktualisieren(getUhrzeit()); } 
            else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget (" + kosten + " EUR)!"); }
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
                for (Fahrzeug f : defekt) if (f.funkrufname.equals(wahl)) { f.ausfallGrund = "In Bearbeitung"; f.reparaturDauer = dauer; break; }
                uiAktualisieren(getUhrzeit());
            } else { JOptionPane.showMessageDialog(frame, "Nicht genug Budget!"); }
        }
    }

        public static void oeffneFahrzeugVerwaltung () {
        JDialog d = createFramelessDialog("Fahrzeug-Uebersicht & Inspektion", 1000, 500);
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.setBackground(new Color(35,35,35));

        String[] columns = {"Funkkennung", "Fahrzeugart", "Zustand (KM-Stand)", "Dienstplan-Farbe (Klick)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return Integer.class; // Fuer den Fortschrittsbalken
                if (columnIndex == 3) return Color.class;   // Fuer das Farb-Kaestchen
                return String.class;
            }
        };

        // Liste zum Zwischenspeichern der Objekte
        ArrayList<Fahrzeug> fzList = new ArrayList<>();
        
        for (Wache w : wachen) {
            for (Fahrzeug f : w.fuhrpark) {
                fzList.add(f);
                // Tausche "kilometerStand" gegen deine Variable aus, falls sie anders heisst!
                model.addRow(new Object[]{f.funkrufname, f.typ, f.kilometer, f.stempelFarbe});
            }
        }

        JTable table = new JTable(model);
        
        // --- MODERNES DESIGN ---
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(41, 128, 185));
        table.setSelectionForeground(Color.WHITE);

        // Standard-Renderer (Zebra-Muster) fuer Text-Spalten
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!isSelected) c.setBackground(row % 2 == 0 ? new Color(43, 43, 43) : new Color(50, 50, 50));
                c.setForeground(Color.WHITE);
                return c;
            }
        };
        table.getColumnModel().getColumn(0).setCellRenderer(defaultRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(defaultRenderer);

        // --- FORTSCHRITTSBALKEN FUER KM-STAND ---
        table.getColumnModel().getColumn(2).setCellRenderer(new TableCellRenderer() {
            private final JProgressBar pb = new JProgressBar(0, 10000); // Inspektion bei 10.000km
            private final JPanel pnl = new JPanel(new BorderLayout());
            {
                pnl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                pb.setStringPainted(true);
                pb.setFont(new Font("Segoe UI", Font.BOLD, 11));
                pb.setBackground(new Color(30, 30, 30));
                pnl.add(pb, BorderLayout.CENTER);
            }
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (isSelected) pnl.setBackground(new Color(41, 128, 185));
                else pnl.setBackground(row % 2 == 0 ? new Color(43, 43, 43) : new Color(50, 50, 50));
                
                int km = (value instanceof Integer) ? (Integer) value : 0;
                pb.setValue(km);
                pb.setString(km + " / 10.000 km");
                
                // Faerbt sich von Gruen zu Rot, je naeher die Inspektion rueckt
                if(km > 8000) pb.setForeground(new Color(231, 76, 60)); // Rot
                else if(km > 5000) pb.setForeground(new Color(243, 156, 18)); // Orange
                else pb.setForeground(new Color(46, 204, 113)); // Gruen
                return pnl;
            }
        });

        // --- FARB-KAESTCHEN FUER STEMPELFARBE ---
        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            private final JPanel pnl = new JPanel();
            private final JPanel wrapper = new JPanel(new BorderLayout());
            {
                wrapper.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                pnl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                wrapper.add(pnl, BorderLayout.CENTER);
            }
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (isSelected) wrapper.setBackground(new Color(41, 128, 185));
                else wrapper.setBackground(row % 2 == 0 ? new Color(43, 43, 43) : new Color(50, 50, 50));
                
                if (value instanceof Color) pnl.setBackground((Color) value);
                else pnl.setBackground(new Color(46, 204, 113)); // Standard Gruen
                return wrapper;
            }
        });

        // Klick-Erkennung, um die Farbe zu aendern
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 3) {
                    Fahrzeug f = fzList.get(table.convertRowIndexToModel(row));
                    Color newColor = JColorChooser.showDialog(d, "Stempelfarbe fuer " + f.funkrufname, f.stempelFarbe);
                    if (newColor != null) {
                        f.stempelFarbe = newColor;
                        model.setValueAt(newColor, row, col);
                        SpeicherManager.speichern("savegame.properties");
                    }
                }
            }
        });

        table.getTableHeader().setBackground(new Color(25, 25, 25)); 
        table.getTableHeader().setForeground(Color.LIGHT_GRAY); 
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(35, 35, 35));
        
        JButton btnInspektion = LogistikSimulator.createStyledButton("Fahrzeug warten & Inspektion (1.500 EUR)", new Color(243, 156, 18));
        btnInspektion.setForeground(Color.BLACK);
        
        btnInspektion.addActionListener(e -> {
            int viewRow = table.getSelectedRow();
            if (viewRow == -1) {
                JOptionPane.showMessageDialog(d, "Bitte waehle zuerst ein Fahrzeug aus!");
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            Fahrzeug f = fzList.get(modelRow);
            
            if (f.status == 6) {
                JOptionPane.showMessageDialog(d, "Das Fahrzeug steht bereits in der Werkstatt!");
                return;
            }
            if (budget < 1500) {
                JOptionPane.showMessageDialog(d, "Nicht genug Budget! (1.500 EUR benoetigt)");
                return;
            }
            
            int wahl = JOptionPane.showConfirmDialog(d, "Inspektion fuer " + f.funkrufname + " durchfuehren?\nDas Fahrzeug geht fuer 120 Sekunden in Status 6.", "Werkstatt", JOptionPane.YES_NO_OPTION);
            if (wahl == JOptionPane.YES_OPTION) {
                budget -= 1500;
                f.kilometer = 0; // KM-Stand wird auf 0 gesetzt
                f.status = 6;
                f.ausfallGrund = "Inspektion";
                f.reparaturDauer = 120; // 120 Sekunden offline
                model.setValueAt(0, modelRow, 2); // UI Tabelle aktualisieren
                
                SpeicherManager.speichern("savegame.properties");
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                JOptionPane.showMessageDialog(d, f.funkrufname + " ist jetzt in der Werkstatt!");
            }
        });

        JButton btnClose = LogistikSimulator.createStyledButton("Schliessen", new Color(108, 122, 137));
        btnClose.addActionListener(e -> d.dispose());

        bottomPanel.add(btnInspektion);
        bottomPanel.add(btnClose);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(35, 35, 35));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        content.add(scrollPane, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);
        
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }
        
}