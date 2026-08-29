package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class DatenManager {

    // Liefert das Panel für den Tab in den Einstellungen zurueck
    public static JPanel createImportExportTab(JFrame parentFrame) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton btnImportEinsaetze = LogistikSimulator.createStyledButton("Einsaetze Importieren (.csv)", new Color(41, 128, 185));
        JButton btnExportEinsaetze = LogistikSimulator.createStyledButton("Einsaetze Exportieren (.csv)", new Color(39, 174, 96));
        JButton btnImportSave = LogistikSimulator.createStyledButton("Savegame Importieren (.prop)", new Color(211, 84, 0));
        JButton btnExportSave = LogistikSimulator.createStyledButton("Savegame Exportieren (.prop)", new Color(142, 68, 173));

        btnImportEinsaetze.addActionListener(e -> ImportManager.importiereEinsaetzeUeberDialog(parentFrame));
        btnExportEinsaetze.addActionListener(e -> exportiereDatei("einsaetze.csv", parentFrame));
        btnImportSave.addActionListener(e -> importiereSavegame(parentFrame));
        btnExportSave.addActionListener(e -> exportiereDatei("savegame.properties", parentFrame));

        panel.add(btnImportEinsaetze);
        panel.add(btnExportEinsaetze);
        panel.add(btnImportSave);
        panel.add(btnExportSave);

        return panel;
    }

    private static void exportiereDatei(String dateiName, JFrame parentFrame) {
        File sourceFile = new File(dateiName);
        if (!sourceFile.exists()) {
            JOptionPane.showMessageDialog(parentFrame, "Die Datei " + dateiName + " existiert noch nicht!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Zielordner fuer Export auswaehlen");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 

        if (chooser.showSaveDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            try {
                File destDir = chooser.getSelectedFile();
                File destFile = new File(destDir, dateiName);
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(parentFrame, "Export erfolgreich!\nGespeichert unter:\n" + destFile.getAbsolutePath(), "Erfolg", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Fehler beim Exportieren:\n" + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void importiereSavegame(JFrame parentFrame) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Savegame (.properties) zum Importieren auswaehlen");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Properties Dateien", "properties"));

        if (chooser.showOpenDialog(parentFrame) == JFileChooser.APPROVE_OPTION) {
            int warnung = JOptionPane.showConfirmDialog(parentFrame, 
                "ACHTUNG: Wenn du ein Savegame importierst, wird dein aktueller Spielstand ueberschrieben!\nDas Spiel wird nach dem Import automatisch beendet.\n\nWirklich importieren?", 
                "Warnung", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (warnung == JOptionPane.YES_OPTION) {
                try {
                    File sourceFile = chooser.getSelectedFile();
                    File destFile = new File("savegame.properties");
                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    JOptionPane.showMessageDialog(parentFrame, "Savegame erfolgreich importiert!\nDas Spiel wird nun beendet. Bitte starte es neu, um den Spielstand zu laden.", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, "Fehler beim Importieren:\n" + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}