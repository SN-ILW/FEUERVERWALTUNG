package neuesspiel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ImportManager {

    private static final String DATEI_NAME = "einsaetze.csv";

    // 1. Standard-Methode beim Spielstart (Lädt nur die Original-Datei)
    public static void ladeEinsaetze() {
        File file = new File(DATEI_NAME);
        if (!file.exists()) {
            erstelleBeispielDatei();
        }
        ladeDatei(file);
    }

    // 2. Öffnet ein Auswahlfenster für den Spieler zum Importieren
    public static void importiereEinsaetzeUeberDialog(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Waehle eine Einsatz-CSV-Datei zum Importieren");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Dateien", "csv"));
        
        int wahl = fileChooser.showOpenDialog(parentFrame);
        if (wahl == JFileChooser.APPROVE_OPTION) {
            File gewaehlteDatei = fileChooser.getSelectedFile();
            importiereUndSpeichere(gewaehlteDatei);
        }
    }

    // Die interne Lade-Logik für den Spielstart
    private static void ladeDatei(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String zeile;
            boolean ersteZeile = true;

            while ((zeile = br.readLine()) != null) {
                if (ersteZeile) { ersteZeile = false; continue; }
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

                        EinsatzVorlage v = new EinsatzVorlage(art, stichwort, beschreibung, reqRTW, reqNEF, reqKTW, reqHLF, reqDLK, reqELW, reqTLF, reqMTW, hatNachforderung, nachforderungProzent, nachforderungTyp, minLevel);
                        LogistikSimulator.vorlagenPool.add(v);
                    } catch (Exception ex) {
                        System.out.println("Fehler beim Lesen einer Zeile in " + file.getName() + ": " + zeile);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden der Standard-Datei: " + e.getMessage());
        }
    }

    // Die Logik für den manuellen Import (Zieht neue rein und speichert sie in der Original-Datei)
    private static void importiereUndSpeichere(File file) {
        int importiert = 0;
        int uebersprungen = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String zeile;
            boolean ersteZeile = true;

            while ((zeile = br.readLine()) != null) {
                if (ersteZeile) { ersteZeile = false; continue; }
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

                        // PRÜFEN: Gibt es den Einsatz schon?
                        boolean existiertBereits = false;
                        for (EinsatzVorlage v : LogistikSimulator.vorlagenPool) {
                            if (v.stichwort.equals(stichwort) && v.beschreibung.equals(beschreibung)) {
                                existiertBereits = true;
                                break;
                            }
                        }

                        if (!existiertBereits) {
                            EinsatzVorlage neueVorlage = new EinsatzVorlage(art, stichwort, beschreibung, reqRTW, reqNEF, reqKTW, reqHLF, reqDLK, reqELW, reqTLF, reqMTW, hatNachforderung, nachforderungProzent, nachforderungTyp, minLevel);
                            LogistikSimulator.vorlagenPool.add(neueVorlage);
                            importiert++;
                        } else {
                            uebersprungen++;
                        }
                    } catch (Exception ex) {
                        System.out.println("Fehler beim Importieren einer Zeile: " + zeile);
                    }
                }
            }
            
            // JETZT ALLES SAUBER IN DIE ORIGINAL-DATEI SCHREIBEN!
            speichereEinsaetzeInCSV();
            
            JOptionPane.showMessageDialog(null, "Import abgeschlossen!\n\n"
                    + "Neu hinzugefuegt & dauerhaft im Original gespeichert: " + importiert + "\n"
                    + "Uebersprungen (bereits vorhanden): " + uebersprungen, 
                    "Import Status", JOptionPane.INFORMATION_MESSAGE);
                    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Fehler beim Importieren der Datei:\n" + e.getMessage(), "Import Fehler", JOptionPane.ERROR_MESSAGE);
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
    
    // Speichert den aktuellen Vorlagen-Pool dauerhaft in die CSV!
    public static void speichereEinsaetzeInCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATEI_NAME))) {
            pw.println("Art;Stichwort;Beschreibung;ELW;HLF;DLK;RTW;NEF;KTW;TLF;MTW;HatNachforderung(true/false);NachforderungProzent;NachforderungTyp;MinLevel");
            
            for (EinsatzVorlage v : LogistikSimulator.vorlagenPool) {
                pw.println(v.art + ";" + v.stichwort + ";" + v.beschreibung + ";" +
                           v.reqELW + ";" + v.reqHLF + ";" + v.reqDLK + ";" +
                           v.reqRTW + ";" + v.reqNEF + ";" + v.reqKTW + ";" +
                           v.reqTLF + ";" + v.reqMTW + ";" +
                           v.hatNachforderung + ";" + v.nachforderungProzent + ";" +
                           v.nachforderungTyp + ";" + v.minLevel);
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Speichern der CSV: " + e.getMessage());
        }
    }
}