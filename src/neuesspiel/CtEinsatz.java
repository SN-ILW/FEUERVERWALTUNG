package neuesspiel;

import javax.swing.*;
import java.awt.*;

public class CtEinsatz {

    // Farben passend zu deinem Leitstellen-Design
    private static final Color BG_MAIN = new Color(18, 18, 18);
    private static final Color BG_PANEL = new Color(30, 30, 30);
    private static final Color ACCENT_BLUE = new Color(52, 152, 219);
    private static final Color BORDER_COLOR = new Color(50, 50, 50);
    // NEU: Hier speichern wir alle laufenden Einsätze ab!
    
    
    
    public static void oeffneDetails(int einsatzId) {
        // Ein Dialog-Fenster, das nicht blockiert (false), damit die Karte im Hintergrund weiterläuft
        JDialog dialog = new JDialog(CalltakerSimulator.frame, "Einsatzakte", false);
        dialog.setUndecorated(true); // Moderner Look ohne Windows-Rahmen
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(CalltakerSimulator.frame); // Zentriert auf dem Bildschirm

        // Haupt-Panel mit feinem Rahmen
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_MAIN);
        mainPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));

        // --- KOPFZEILE (TOP BAR) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG_PANEL);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // NEU: Wir holen uns die gespeicherte Einsatzakte
        NotrufDialogKI.CtNotruf anruf = CalltakerSimulator.laufendeEinsaetze.get(einsatzId);
        String stichwort = (anruf != null) ? anruf.stichwort : "UNBEKANNT";

        // HIER STEHT JETZT DAS STICHWORT MIT DRIN!
        JLabel lblTitle = new JLabel(" " + stichwort + " | EINSATZ-AKTE #" + einsatzId);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, ACCENT_BLUE));
        
        JButton btnClose = LogistikSimulator.createStyledButton("X SCHLIESSEN", new Color(192, 57, 43));
        btnClose.addActionListener(e -> dialog.dispose());

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnClose, BorderLayout.EAST);

        // --- INHALTSBEREICH (CONTENT) ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(BG_MAIN);
        
        JLabel lblInfo = new JLabel("System bereit. Hier entsteht gleich die Triage- und Patientenversorgung!");
        lblInfo.setForeground(Color.LIGHT_GRAY);
        lblInfo.setFont(new Font("Consolas", Font.ITALIC, 14));
        contentPanel.add(lblInfo);

        // Alles zusammensetzen
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}