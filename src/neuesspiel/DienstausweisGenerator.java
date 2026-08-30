package neuesspiel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class DienstausweisGenerator {

    public static void oeffneAusweisErsteller(Personal p, Email triggerMail, JDialog postfachDialog) {
        JDialog d = new JDialog(LogistikSimulator.frame, "Dienstausweis erstellen", true);
        d.setUndecorated(true);
        d.setSize(650, 400); // Etwas groesser fuer bessere Proportionen
        d.setLocationRelativeTo(null);
        d.getContentPane().setBackground(new Color(35, 35, 35)); // Dunkler Hintergrund hinter dem Ausweis

        // --- DER AUSWEIS (Weisse Karte mit rotem Rand) ---
        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Abstand zum Fensterrand

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        // Custom roter Rand mit runden Ecken
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.RED, 6, true),
                BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));

        // --- TITEL ---
        JLabel lblTitle = new JLabel("DIENSTAUSWEIS-FEUERWEHR-SCHWERIN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.RED);
        lblTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.RED)); // Roter Strich darunter
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.CENTER);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        cardPanel.add(titlePanel, BorderLayout.NORTH);

        // --- CENTER BEREICH (Foto & Felder) ---
        JPanel centerPanel = new JPanel(new BorderLayout(25, 0));
        centerPanel.setBackground(Color.WHITE);

        // 1. FOTO (Links)
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setPreferredSize(new Dimension(170, 220));
        photoPanel.setBackground(Color.WHITE);
        
        try {
            // Versucht, perso.png zu laden
            URL imgUrl = DienstausweisGenerator.class.getResource("perso.png");
            ImageIcon icon = (imgUrl != null) ? new ImageIcon(imgUrl) : new ImageIcon("perso.png");
            Image img = icon.getImage().getScaledInstance(170, 220, Image.SCALE_SMOOTH);
            JLabel picLabel = new JLabel(new ImageIcon(img));
            photoPanel.add(picLabel, BorderLayout.CENTER);
        } catch (Exception ex) {
            // Fallback, falls Bild fehlt
            JLabel fallback = new JLabel("FOTO", SwingConstants.CENTER);
            fallback.setBorder(new LineBorder(Color.BLACK, 4));
            photoPanel.add(fallback, BorderLayout.CENTER);
        }
        centerPanel.add(photoPanel, BorderLayout.WEST);

        // 2. EINGABEFELDER (Rechts)
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        fieldsPanel.setBackground(Color.WHITE);

        // Platzhalter-Logik fuer Textfelder
        class PlaceholderField extends JTextField {
            String placeholder;
            boolean showingPlaceholder;

            public PlaceholderField(String placeholder) {
                this.placeholder = placeholder;
                this.showingPlaceholder = true;
                setText(placeholder);
                setForeground(Color.GRAY);
                setFont(new Font("Arial", Font.BOLD, 18));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(Color.BLACK, 5), // Fetter schwarzer Rand
                        BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));

                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (showingPlaceholder) {
                            setText("");
                            setForeground(Color.BLACK);
                            showingPlaceholder = false;
                        }
                    }
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (getText().isEmpty()) {
                            setText(placeholder);
                            setForeground(Color.GRAY);
                            showingPlaceholder = true;
                        }
                    }
                });
            }
            public String getRealText() {
                return showingPlaceholder ? "" : getText();
            }
        }

        PlaceholderField tfVorname = new PlaceholderField("VORNAME");
        PlaceholderField tfNachname = new PlaceholderField("NACHNAME");

        // Geburtsdatum (Dropdowns) mit fetter schwarzer Umrandung
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        datePanel.setBackground(Color.WHITE);
        datePanel.setBorder(new LineBorder(Color.BLACK, 5)); // Fetter schwarzer Rand
        
        JComboBox<String> cbTag = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cbTag.addItem(String.format("%02d", i));
        
        JComboBox<String> cbMonat = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cbMonat.addItem(String.format("%02d", i));
        
        JComboBox<String> cbJahr = new JComboBox<>();
        for (int i = 1980; i <= 2008; i++) cbJahr.addItem(String.valueOf(i));

        Font comboFont = new Font("Arial", Font.BOLD, 18);
        cbTag.setFont(comboFont); cbMonat.setFont(comboFont); cbJahr.setFont(comboFont);
        cbTag.setBackground(Color.WHITE); cbMonat.setBackground(Color.WHITE); cbJahr.setBackground(Color.WHITE);
        
        JLabel dot1 = new JLabel("."); dot1.setFont(comboFont);
        JLabel dot2 = new JLabel("."); dot2.setFont(comboFont);
        
        datePanel.add(cbTag); datePanel.add(dot1); 
        datePanel.add(cbMonat); datePanel.add(dot2); 
        datePanel.add(cbJahr);

        // Schwerin Datum Feld (Automatisch aus dem Spiel)
        String heutigesDatum = LogistikSimulator.getCurrentDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        JTextField tfSchwerin = new JTextField("SCHWERIN, " + heutigesDatum);
        tfSchwerin.setFont(new Font("Arial", Font.BOLD, 18));
        tfSchwerin.setForeground(Color.BLACK);
        tfSchwerin.setHorizontalAlignment(SwingConstants.CENTER);
        tfSchwerin.setEditable(false);
        tfSchwerin.setBackground(Color.WHITE);
        tfSchwerin.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 5), // Fetter schwarzer Rand
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));

        fieldsPanel.add(tfVorname);
        fieldsPanel.add(tfNachname);
        fieldsPanel.add(datePanel);
        fieldsPanel.add(tfSchwerin);

        centerPanel.add(fieldsPanel, BorderLayout.CENTER);
        cardPanel.add(centerPanel, BorderLayout.CENTER);
        cardWrapper.add(cardPanel, BorderLayout.CENTER);

        // --- BUTTONS UNTEN (Im dunklen Theme) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnPanel.setBackground(new Color(35, 35, 35));

        JButton btnAbbrechen = LogistikSimulator.createStyledButton("Abbrechen", new Color(192, 57, 43));
        btnAbbrechen.addActionListener(e -> d.dispose());

        JButton btnEinreichen = LogistikSimulator.createStyledButton("An Personalabteilung senden", new Color(39, 174, 96));
        btnEinreichen.addActionListener(e -> {
            String inputVorname = tfVorname.getRealText().trim();
            String inputNachname = tfNachname.getRealText().trim();

            String[] nameParts = p.name.split(" ", 2);
            String echterVorname = nameParts[0];
            String echterNachname = nameParts.length > 1 ? nameParts[1] : "";

            if (inputVorname.equalsIgnoreCase(echterVorname) && inputNachname.equalsIgnoreCase(echterNachname)) {
                JOptionPane.showMessageDialog(d, "Dienstausweis erfolgreich erstellt und uebermittelt!", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                LogistikSimulator.postfach.remove(triggerMail); // Löscht die Aufforderung
                LogistikSimulator.uiAktualisieren(LogistikSimulator.getUhrzeit());
                d.dispose();
                if(postfachDialog != null) postfachDialog.dispose();
                FensterManager.oeffnePostfach(); // Aktualisiert das Postfach im Hintergrund
            } else {
                JOptionPane.showMessageDialog(d, "Fehler! Die eingegebenen Daten stimmen nicht mit der Personalakte (" + p.name + ") ueberein!", "Behoerden-Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnAbbrechen);
        btnPanel.add(btnEinreichen);

        d.add(cardWrapper, BorderLayout.CENTER);
        d.add(btnPanel, BorderLayout.SOUTH);
        d.setVisible(true);
    }
}