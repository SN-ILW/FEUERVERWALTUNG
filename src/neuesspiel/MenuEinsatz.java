package neuesspiel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static neuesspiel.LogistikSimulator.*;
import static neuesspiel.FensterManager.createFramelessDialog;

public class MenuEinsatz {

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
        JDialog d = createFramelessDialog("EINSATZAKTE: " + ein.vorlage.stichwort + " - " + ein.beschreibung, 650, 550);
        
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body { font-family: 'Segoe UI', sans-serif; background-color: #232323; color: #e0e0e0; padding: 10px; margin: 0; }")
            .append(".box { background-color: #2a2a2a; border-left: 4px solid #3498db; padding: 8px; margin-bottom: 10px; }")
            .append(".box-pat { border-left-color: #e74c3c; }")
            .append(".box-lage { border-left-color: #f1c40f; }")
            .append(".title { font-weight: bold; font-size: 13px; color: #ffffff; margin-bottom: 4px; }")
            .append(".log-entry { font-family: 'Consolas', monospace; font-size: 11px; color: #bdc3c7; margin-bottom: 2px; }")
            .append("</style></head><body>");

        // 1. Allgemein & Lage
        html.append("<div class='box box-lage'>")
            .append("<div class='title'>EINSATZLAGE & OBJEKT</div>")
            .append("<b>Stichwort:</b> ").append(ein.vorlage.stichwort).append("<br>")
            .append("<b>Objekt / Lage:</b> ").append(ein.schadensObjekt).append("<br>")
            .append("<b>Lagemeldung:</b> ").append(ein.getLagemeldungText().replace("\n", "<br>"))
            .append("</div>");

        // 2. Patienten & Medizinische Daten
        html.append("<div class='box box-pat'>")
            .append("<div class='title'>PATIENTEN & MANV-STATUS</div>")
            .append("<b>Betroffene Personen:</b> ").append(ein.patientenAnzahl).append("<br>")
            .append("<b>Status:</b> ").append(ein.patientenStatusText)
            .append("</div>");

        // 3. Funksprueche / Einsatz-Protokoll
        html.append("<div class='box'>")
            .append("<div class='title'>EINSATZPROTOKOLL & FUNKVERLAUF</div>");
        
        if (ein.einsatzProtokoll.isEmpty()) {
            html.append("<div class='log-entry'>Keine Funksprueche protokolliert.</div>");
        } else {
            for (String log : ein.einsatzProtokoll) {
                html.append("<div class='log-entry'>&gt; ").append(log).append("</div>");
            }
        }
        html.append("</div>");

        html.append("</body></html>");

        JEditorPane txtAkte = new JEditorPane("text/html", html.toString());
        txtAkte.setEditable(false);
        txtAkte.setBackground(new Color(35, 35, 35));

        d.add(new JScrollPane(txtAkte), BorderLayout.CENTER);

        // Buttons unten
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        btnPanel.setBackground(new Color(35, 35, 35));
        
        JButton btnBeenden = new JButton("Einsatz sofort abbrechen (-250 XP)");
        btnBeenden.setBackground(new Color(192, 57, 43)); 
        btnBeenden.setForeground(Color.WHITE);
        btnBeenden.addActionListener(e -> {
            ein.bereitZumLoeschen = true; 
            LogistikSimulator.xp -= 250; 
            LogistikSimulator.abgelehnteEinsaetzeHeute++; 
            LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit()); 
            d.dispose();
        });

        JButton btnClose = new JButton("Schliessen");
        btnClose.addActionListener(e -> d.dispose());

        btnPanel.add(btnBeenden);
        btnPanel.add(btnClose);

        d.add(btnPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }
    
    public static void oeffneAlarmierungsFenster(Einsatz ein) {
        JDialog d = createFramelessDialog("Alarmierung: " + ein.vorlage.stichwort + " - " + ein.beschreibung, 800, 600);
        
        JPanel pnlTop = new JPanel(new GridLayout(2, 1, 5, 5)); pnlTop.setBackground(new Color(35, 35, 35)); pnlTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitel = new JLabel("NOTRUF: " + ein.vorlage.stichwort + " | " + ein.beschreibung, SwingConstants.CENTER);
        lblTitel.setForeground(new Color(231, 76, 60)); lblTitel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTop.add(lblTitel);
        
        StringBuilder reqText = new StringBuilder("Benoetigt: ");
        if(ein.vorlage.reqELW > 0) reqText.append(ein.vorlage.reqELW).append("x ELW  "); if(ein.vorlage.reqHLF > 0) reqText.append(ein.vorlage.reqHLF).append("x HLF  ");
        if(ein.vorlage.reqDLK > 0) reqText.append(ein.vorlage.reqDLK).append("x DLK  "); if(ein.vorlage.reqRTW > 0) reqText.append(ein.vorlage.reqRTW).append("x RTW  ");
        if(ein.vorlage.reqNEF > 0) reqText.append(ein.vorlage.reqNEF).append("x NEF  "); if(ein.vorlage.reqKTW > 0) reqText.append(ein.vorlage.reqKTW).append("x KTW  ");
        if(ein.vorlage.reqTLF > 0) reqText.append(ein.vorlage.reqTLF).append("x TLF  "); if(ein.vorlage.reqMTW > 0) reqText.append(ein.vorlage.reqMTW).append("x MTW  ");
        
        JLabel lblReq = new JLabel(reqText.toString(), SwingConstants.CENTER); lblReq.setForeground(Color.WHITE); lblReq.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlTop.add(lblReq); d.add(pnlTop, BorderLayout.NORTH);

        String[] cols = {"Auswaehlen", "Funkrufname", "Typ", "Wache", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int row, int column) { return column == 0; }
        };

        ArrayList<Fahrzeug> verfuegbar = new ArrayList<>();
        for(Wache w : wachen) {
            for(Fahrzeug f : w.fuhrpark) {
                if(f.status == 1 || f.status == 2) {
                    verfuegbar.add(f); model.addRow(new Object[]{false, f.funkrufname, f.typ, w.name, "Status " + f.status});
                }
            }
        }

        JTable table = new JTable(model); table.setRowHeight(25); table.setBackground(new Color(43, 43, 43)); table.setForeground(Color.WHITE);
        table.getTableHeader().setBackground(new Color(20, 30, 48)); table.getTableHeader().setForeground(Color.WHITE); table.getColumnModel().getColumn(0).setMaxWidth(80);
        d.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10)); pnlBottom.setBackground(new Color(35, 35, 35));

        JButton btnAAO = new JButton("AAO nutzen (Auto-Auswahl)"); btnAAO.setBackground(new Color(41, 128, 185)); btnAAO.setForeground(Color.WHITE);
        JButton btnAlarm = new JButton("ALARM AUSLOESEN"); btnAlarm.setBackground(new Color(39, 174, 96)); btnAlarm.setForeground(Color.WHITE); btnAlarm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton btnAbbruch = new JButton("Abbrechen"); btnAbbruch.addActionListener(e -> d.dispose());

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
                    if(f.typ.equals("RTW") && !(Boolean)model.getValueAt(i, 0) && needKTW > 0) { model.setValueAt(true, i, 0); needKTW--; }
                }
            }
        });

        btnAlarm.addActionListener(e -> {
            ArrayList<Fahrzeug> selectedFz = new ArrayList<>();
            int sELW=0, sHLF=0, sDLK=0, sRTW=0, sNEF=0, sKTW=0, sTLF=0, sMTW=0;
            
            for(int i = 0; i < model.getRowCount(); i++) {
                if((Boolean)model.getValueAt(i, 0)) {
                    Fahrzeug f = verfuegbar.get(i); selectedFz.add(f);
                    switch(f.typ) {
                        case "ELW": sELW++; break; case "HLF": sHLF++; break; case "DLK": sDLK++; break;
                        case "RTW": sRTW++; break; case "NEF": sNEF++; break; case "KTW": sKTW++; break;
                        case "TLF": sTLF++; break; case "MTW": sMTW++; break;
                    }
                }
            }
            
            int mELW = Math.max(0, ein.vorlage.reqELW - sELW); int mHLF = Math.max(0, ein.vorlage.reqHLF - sHLF);
            int mDLK = Math.max(0, ein.vorlage.reqDLK - sDLK); int mNEF = Math.max(0, ein.vorlage.reqNEF - sNEF);
            int mTLF = Math.max(0, ein.vorlage.reqTLF - sTLF); int mMTW = Math.max(0, ein.vorlage.reqMTW - sMTW);
            
            int fehlendeKTW = Math.max(0, ein.vorlage.reqKTW - sKTW);
            int ueberschussRTW = Math.max(0, sRTW - ein.vorlage.reqRTW);
            fehlendeKTW = Math.max(0, fehlendeKTW - ueberschussRTW);
            int mRTW = Math.max(0, ein.vorlage.reqRTW - sRTW);
            
            int totalMissing = mELW + mHLF + mDLK + mNEF + mTLF + mMTW + mRTW + fehlendeKTW;
            
            boolean ueberlandHilfeAktiv = false;
            if(totalMissing > 0) {
                int wahl = JOptionPane.showConfirmDialog(d, "Dir fehlen " + totalMissing + " Fahrzeuge zum Ausruecken!\nSoll der Landkreis aushelfen? (" + (totalMissing*500) + " EURO)", "Ueberlandhilfe", JOptionPane.YES_NO_OPTION);
                if (wahl == JOptionPane.YES_OPTION) {
                    if (budget >= (totalMissing*500)) { budget -= (totalMissing*500); ueberlandHilfeAktiv = true; } 
                    else { JOptionPane.showMessageDialog(d, "Zu wenig Geld!", "Fehler", JOptionPane.ERROR_MESSAGE); return; }
                } else { return; }
            }

            boolean matsDa = true;
            if (cfgLogistikAktiv) {
                for(String m : ein.reqMaterial.keySet()) {
                    boolean found = false; for(Wache w : wachen) { if(w.hatMaterial(m, ein.reqMaterial.get(m))) found = true; }
                    if(!found) matsDa = false;
                }
            }
            if (!matsDa) {
                JOptionPane.showMessageDialog(d, "Nicht genug Material (" + ein.reqMaterial.keySet().iterator().next() + ") auf den Wachen!", "Material fehlt", JOptionPane.ERROR_MESSAGE); return;
            }

            int xpBel = 0; int multiplier = isRushHour() ? 3 : 1;
            
            for (Fahrzeug f : selectedFz) {
                int baseTime = 30; 
                for(Wache wCheck : wachen) { for(Personal p : wCheck.personalPool) { if(p.zugewiesenesFahrzeug.equals(f.funkrufname) && p.status.equals("Frei")) { baseTime = 60; break; } } }
                f.status = 3; f.anfahrtsZeit = (int)(baseTime * multiplier * getSpeedMultiplier(f)); f.aktuellerEinsatz = ein; xpBel += 25;
            }
            
            if (!selectedFz.isEmpty() || ueberlandHilfeAktiv) { ein.xpBelohnung = xpBel * ein.vorlage.minLevel; aktiveEinsaetze.add(ein); }
            LogistikSimulator.aktuellerNotruf = null; uiAktualisieren(getUhrzeit()); d.dispose();
        });

        pnlBottom.add(btnAAO); pnlBottom.add(btnAlarm); pnlBottom.add(btnAbbruch); d.add(pnlBottom, BorderLayout.SOUTH); d.setVisible(true);
    }
    
    public static void oeffneKrankenhausWahl(Fahrzeug f) {
        JDialog d = createFramelessDialog("Zielklinik waehlen fuer " + f.funkrufname, 450, 350);
        JPanel content = new JPanel(new GridLayout(5, 1, 10, 10)); content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

        JLabel l = new JLabel("Patient verladen. Bitte Zielklinik waehlen:", SwingConstants.CENTER); l.setForeground(Color.WHITE); content.add(l);

        JButton b1 = new JButton(klinik1Abgemeldet ? "[ABGEMELDET] Helios Kliniken Schwerin" : "Helios Kliniken Schwerin (Fahrzeit: 45s)"); b1.setEnabled(!klinik1Abgemeldet);
        b1.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 45; f.originalAnfahrt = 45; uiAktualisieren(getUhrzeit()); d.dispose(); });
        JButton b2 = new JButton(klinik2Abgemeldet ? "[ABGEMELDET] Unimedizin Rostock" : "Universitaetsmedizin Rostock (Fahrzeit: 120s)"); b2.setEnabled(!klinik2Abgemeldet);
        b2.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 120; f.originalAnfahrt = 120; uiAktualisieren(getUhrzeit()); d.dispose(); });
        content.add(b1); content.add(b2);

        if(techKlinikCrivitz) {
            JButton bc = new JButton(klinikCrivitzAbgemeldet ? "[ABGEMELDET] Klinik Crivitz" : "Krankenhaus Crivitz (Fahrzeit: 50s)"); bc.setEnabled(!klinikCrivitzAbgemeldet);
            bc.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 50; f.originalAnfahrt = 50; uiAktualisieren(getUhrzeit()); d.dispose(); }); content.add(bc);
        }
        if(techKlinikLeezen) {
            JButton bl = new JButton(klinikLeezenAbgemeldet ? "[ABGEMELDET] Klinik Leezen" : "Krankenhaus Leezen (Fahrzeit: 60s)"); bl.setEnabled(!klinikLeezenAbgemeldet);
            bl.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 60; f.originalAnfahrt = 60; uiAktualisieren(getUhrzeit()); d.dispose(); }); content.add(bl);
        }
        if(techKlinikHagenow) {
            JButton bh = new JButton(klinikHagenowAbgemeldet ? "[ABGEMELDET] Klinik Hagenow" : "Krankenhaus Hagenow (Fahrzeit: 80s)"); bh.setEnabled(!klinikHagenowAbgemeldet);
            bh.addActionListener(e -> { f.status = 8; f.anfahrtsZeit = 80; f.originalAnfahrt = 80; uiAktualisieren(getUhrzeit()); d.dispose(); }); content.add(bh);
        }
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }

    public static void oeffneBettenUebersicht() {
        JDialog d = createFramelessDialog("Klinik- & Bettenuebersicht", 450, 300);
        JPanel content = new JPanel(new GridLayout(6, 1, 10, 10)); content.setBorder(BorderFactory.createEmptyBorder(10,10,10,10)); content.setBackground(new Color(35, 35, 35));

        JLabel title = new JLabel("Aktuelle Aufnahmekapazitaeten der Kliniken:", SwingConstants.CENTER); title.setForeground(Color.WHITE); content.add(title);
        JLabel lbl1 = new JLabel(klinik1Abgemeldet ? "[X] Helios Kliniken Schwerin: AUFNAHMESTOPP" : "[OK] Helios Kliniken Schwerin: Aufnahmebereit", SwingConstants.CENTER); lbl1.setForeground(klinik1Abgemeldet ? Color.RED : Color.GREEN);
        JLabel lbl2 = new JLabel(klinik2Abgemeldet ? "[X] Unimedizin Rostock: AUFNAHMESTOPP" : "[OK] Unimedizin Rostock: Aufnahmebereit", SwingConstants.CENTER); lbl2.setForeground(klinik2Abgemeldet ? Color.RED : Color.GREEN);
        content.add(lbl1); content.add(lbl2);
        
        if(techKlinikCrivitz) { JLabel lc = new JLabel(klinikCrivitzAbgemeldet ? "[X] Klinik Crivitz: AUFNAHMESTOPP" : "[OK] Klinik Crivitz: Aufnahmebereit", SwingConstants.CENTER); lc.setForeground(klinikCrivitzAbgemeldet ? Color.RED : Color.GREEN); content.add(lc); }
        if(techKlinikLeezen) { JLabel ll = new JLabel(klinikLeezenAbgemeldet ? "[X] Klinik Leezen: AUFNAHMESTOPP" : "[OK] Klinik Leezen: Aufnahmebereit", SwingConstants.CENTER); ll.setForeground(klinikLeezenAbgemeldet ? Color.RED : Color.GREEN); content.add(ll); }
        if(techKlinikHagenow) { JLabel lh = new JLabel(klinikHagenowAbgemeldet ? "[X] Klinik Hagenow: AUFNAHMESTOPP" : "[OK] Klinik Hagenow: Aufnahmebereit", SwingConstants.CENTER); lh.setForeground(klinikHagenowAbgemeldet ? Color.RED : Color.GREEN); content.add(lh); }
        d.add(content, BorderLayout.CENTER); d.setVisible(true);
    }
}