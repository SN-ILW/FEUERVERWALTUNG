package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FensterManager {

    public static JDialog hotkeyPopup = null;
    public static java.util.function.IntConsumer currentHotkeySetter = null;
    public static JButton currentHotkeyBtn = null;

    // WICHTIG: Muss public sein, damit die Menü-Klassen darauf zugreifen können!
    public static JDialog createFramelessDialog(String title, int width, int height) {
        JDialog d = new JDialog(LogistikSimulator.frame, title, true);
        d.setUndecorated(true);
        d.setSize(width, height);
        d.setLocationRelativeTo(LogistikSimulator.frame);
        d.setLayout(new BorderLayout());
        
        // --- ESC TASTE ZUM SCHLIESSEN & SPEICHERN ---
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

    // WICHTIG: Muss public sein!
    public static void assignHotkey(JButton btn, java.util.function.IntConsumer setter) {
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

    // ==========================================
    // DELEGATES ZU DEN NEUEN DATEIEN
    // ==========================================

    // Personal
    public static void oeffnePersonalHauptmenu() { MenuPersonal.oeffnePersonalHauptmenu(); }
    public static void oeffneMitarbeiterVerwaltung() { MenuPersonal.oeffneMitarbeiterVerwaltung(); }
    public static void oeffnePersonalTransfer() { MenuPersonal.oeffnePersonalTransfer(); }
    public static void oeffnePersonalWeiterbildung() { MenuPersonal.oeffnePersonalWeiterbildung(); }
    public static void zeigeGehaltsVerhandlung(Personal p, double f, Email m) { MenuPersonal.zeigeGehaltsVerhandlung(p, f, m); }
    public static void personalEinstellen() { MenuPersonal.personalEinstellen(); }
    public static void leihkraftAnfordern() { MenuPersonal.leihkraftAnfordern(); }

    // Logistik & Fuhrpark
    public static void oeffneLogistikHauptmenu() { MenuLogistik.oeffneLogistikHauptmenu(); }
    public static void oeffneMaterialUebersicht() { MenuLogistik.oeffneMaterialUebersicht(); }
    public static void oeffneLogistikMenu() { MenuLogistik.oeffneLogistikMenu(); }
    public static void oeffneBestellMenu() { MenuLogistik.oeffneBestellMenu(); }
    public static void oeffneMaterialErsteller() { MenuLogistik.oeffneMaterialErsteller(); }
    public static void oeffneMaterialBearbeiter() { MenuLogistik.oeffneMaterialBearbeiter(); }
    public static void oeffneFuhrparkHauptmenu() { MenuLogistik.oeffneFuhrparkHauptmenu(); }
    public static void oeffneFuhrpark() { MenuLogistik.oeffneFuhrpark(); }
    public static void oeffneFahrzeugTransfer() { MenuLogistik.oeffneFahrzeugTransfer(); }
    public static void fahrzeugeReparieren() { MenuLogistik.fahrzeugeReparieren(); }
    public static void kaufFahrzeug(Wache w, String typ, int preis) { MenuLogistik.kaufFahrzeug(w, typ, preis); }
    public static void behebeStatus6(String grund, int kosten, int dauer) { MenuLogistik.behebeStatus6(grund, kosten, dauer); }

    // Verwaltung & System
    public static void oeffneSystemHauptmenu() { MenuVerwaltung.oeffneSystemHauptmenu(); }
    public static void oeffneEinstellungen() { MenuVerwaltung.oeffneEinstellungen(); }
    public static void oeffneEinsatzErsteller() { MenuVerwaltung.oeffneEinsatzErsteller(); }
    public static void oeffneEinsatzBearbeiter() { MenuVerwaltung.oeffneEinsatzBearbeiter(); }
    public static void oeffneBank() { MenuVerwaltung.oeffneBank(); }
    public static void oeffneVertragsMenu() { MenuVerwaltung.oeffneVertragsMenu(); }
    public static void oeffneVertragsEditor() { MenuVerwaltung.oeffneVertragsEditor(); }
    public static void bearbeiteVertrag(VertragVorlage v) { MenuVerwaltung.bearbeiteVertrag(v); }
    public static void oeffneWachenAusbau() { MenuVerwaltung.oeffneWachenAusbau(); }
    public static void oeffnePostfach() { MenuVerwaltung.oeffnePostfach(); }

    // Einsatz & Alarmierung
    public static void oeffneNachforderungMenu() { MenuEinsatz.oeffneNachforderungMenu(); }
    public static void oeffneEinsatzDetails(Einsatz ein) { MenuEinsatz.oeffneEinsatzDetails(ein); }
    public static void oeffneAlarmierungsFenster(Einsatz ein) { MenuEinsatz.oeffneAlarmierungsFenster(ein); }
    public static void oeffneKrankenhausWahl(Fahrzeug f) { MenuEinsatz.oeffneKrankenhausWahl(f); }
    public static void oeffneBettenUebersicht() { MenuEinsatz.oeffneBettenUebersicht(); }
}