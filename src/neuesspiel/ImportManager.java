package neuesspiel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ImportManager {

    private static final String DATEI_NAME = "einsaetze.csv";

    // Standard-Methode beim Spielstart
    public static void ladeEinsaetze() {
        File file = new File(DATEI_NAME);
        if (!file.exists()) {
            erstelleBeispielDatei();
        }
        ladeDatei(file, false);
    }

    // NEU: Öffnet ein Auswahlfenster für den Spieler
    public static void importiereEinsaetzeUeberDialog(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Waehle eine Einsatz-CSV-Datei zum Importieren");
        // Erlaubt nur .csv Dateien
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Dateien", "csv"));
        
        int wahl = fileChooser.showOpenDialog(parentFrame);
        if (wahl == JFileChooser.APPROVE_OPTION) {
            File gewaehlteDatei = fileChooser.getSelectedFile();
            ladeDatei(gewaehlteDatei, true);
        }
    }

    // Die eigentliche Lade-Logik für jede beliebige Datei
    private static void ladeDatei(File file, boolean manuellerImport) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String zeile;
            boolean ersteZeile = true;
            int importiert = 0;

            while ((zeile = br.readLine()) != null) {
                if (ersteZeile) {
                    ersteZeile = false;
                    continue;
                }
                
                if (zeile.trim().isEmpty()) continue;

                String[] spalten = zeile.split(";");
                
                if (spalten.length >= 15) {
                    try {
                        String art = spalten[0].trim();
                        String stichwort = spalten[1].trim();
                        String beschreibung = spalten[2].trim();
                        int reqELW = Integer.parseInt(spalten[3].trim());
                        int reqHLF = Integer.parseInt(spalten[4].trim());
                        int reqDLK = Integer.parseInt(spalten[5].trim());
                        int reqRTW = Integer.parseInt(spalten[6].trim());
                        int reqNEF = Integer.parseInt(spalten[7].trim());
                        int reqKTW = Integer.parseInt(spalten[8].trim());
                        int reqTLF = Integer.parseInt(spalten[9].trim());
                        int reqMTW = Integer.parseInt(spalten[10].trim());
                        boolean hatNachforderung = Boolean.parseBoolean(spalten[11].trim());
                        int nachforderungProzent = Integer.parseInt(spalten[12].trim());
                        String nachforderungTyp = spalten[13].trim();
                        int minLevel = Integer.parseInt(spalten[14].trim());

                        EinsatzVorlage neueVorlage = new EinsatzVorlage(
                                art, stichwort, beschreibung, 
                                reqELW, reqHLF, reqDLK, reqRTW, reqNEF, reqKTW, reqTLF, reqMTW, 
                                hatNachforderung, nachforderungProzent, nachforderungTyp, minLevel
                        );
                        
                        LogistikSimulator.vorlagenPool.add(neueVorlage);
                        importiert++;
                        
                    } catch (Exception ex) {
                        System.out.println("Fehler beim Lesen einer Zeile in " + file.getName() + ": " + zeile);
                    }
                }
            }
            
            if (manuellerImport) {
                JOptionPane.showMessageDialog(null, "Import erfolgreich!\nEs wurden " + importiert + " neue Einsaetze zum Spiel hinzugefuegt.", "Import Abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
            } else {
                System.out.println("CSV Import erfolgreich: " + importiert + " Einsaetze geladen!");
            }
            
        } catch (Exception e) {
            if (manuellerImport) {
                JOptionPane.showMessageDialog(null, "Fehler beim Laden der Datei:\n" + e.getMessage(), "Import Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void erstelleBeispielDatei() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATEI_NAME))) {
            pw.println("Art;Stichwort;Beschreibung;ELW;HLF;DLK;RTW;NEF;KTW;TLF;MTW;HatNachforderung(true/false);NachforderungProzent;NachforderungTyp;MinLevel");
            pw.println("FW;H1;Baum auf Strasse;0;1;0;0;0;0;0;0;false;0;;1");
            pw.println("RD;R1;Verdacht auf Herzinfarkt;0;0;0;1;1;0;0;0;false;0;;1");
            pw.println("FW;F2;Wohnungsbrand;1;2;1;1;1;0;0;0;true;50;RTW;2");
            pw.println("KTP;KTP;Krankentransport;0;0;0;0;0;1;0;0;false;0;;1");
            pw.println("RD;R2N1;Verkehrsunfall (THL);1;1;0;2;1;0;0;0;true;30;HLF;3");
        } catch (Exception e) {
            System.out.println("Konnte Beispiel-CSV nicht erstellen.");
        }
    }
}