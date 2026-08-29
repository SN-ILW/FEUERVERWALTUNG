package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Launcher {

    // --- HIER DEINE DATEN EINTRAGEN ---
    public static final String CURRENT_VERSION = "v39"; // Für den Test eine ältere Version eintragen
    public static final String GITHUB_REPO = "SN-ILW/FEUERVERWALTUNG"; 
    public static final String EXE_NAME = "FeuerwehrVerwaltung.exe";
    // ----------------------------------

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", new Color(35, 35, 35));
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("OptionPane.background", new Color(35, 35, 35));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
        } catch (Exception e) {}

        JFrame frame = new JFrame("FEUERWEHR-VERWALTUNGS-SPIEL");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 360); 
        frame.setLocationRelativeTo(null); 
        frame.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(25, 25, 25));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel lblTitle = new JLabel("FeuerwehrVerwaltung", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel lblVersion = new JLabel("Aktuelle Version: " + CURRENT_VERSION, SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setForeground(new Color(150, 150, 150));
        
        topPanel.add(lblTitle);
        topPanel.add(lblVersion);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(new Color(35, 35, 35));

        JButton btnStart = createStyledButton("Verwaltung oeffnen", new Color(39, 174, 96));
        JButton btnFuehrung = createStyledButton("!!BETA!! Fuehrungskraft spielen !!BETA!!", new Color(243, 156, 18)); 
        JButton btnUpdate = createStyledButton("Update suchen", new Color(41, 128, 185));
        JButton btnExit = createStyledButton("Beenden", new Color(192, 57, 43));

        btnStart.addActionListener(e -> {
            frame.dispose(); 
            LogistikSimulator.main(new String[]{}); 
        });
        
        // HIER NEU: Oeffnet das Fahrzeug-Auswahl Fenster!
        btnFuehrung.addActionListener(e -> {
            oeffneFahrzeugAuswahl(frame);
        });

        btnUpdate.addActionListener(e -> checkForUpdates(frame));

        btnExit.addActionListener(e -> System.exit(0));

        centerPanel.add(btnStart);
        centerPanel.add(btnFuehrung); 
        centerPanel.add(btnUpdate);
        centerPanel.add(btnExit);
        
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return btn;
    }

    // --- NEUES FENSTER FUER DIE FAHRZEUGAUSWAHL ---
    private static void oeffneFahrzeugAuswahl(JFrame parentFrame) {
        JDialog d = new JDialog(parentFrame, "Fahrzeug waehlen", true);
        d.setUndecorated(true);
        d.setSize(350, 250);
        d.setLocationRelativeTo(parentFrame);
        d.setLayout(new BorderLayout());

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(20, 20, 20));
        titleBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        JLabel lblTitle = new JLabel(" Fahrzeug auswaehlen");
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

        JPanel content = new JPanel(new GridLayout(3, 1, 10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        content.setBackground(new Color(35, 35, 35));

        JButton btnHLF = createStyledButton("!!BETA!! HLF (Gruppenfuehrer) !!BETA!!", new Color(192, 57, 43)); // Rot fuer Feuerwehr
        JButton btnELW = createStyledButton("ELW (Einsatzleiter)", new Color(142, 68, 173)); // Lila/Blau fuer Einsatzleitung
        JButton btnZurueck = createStyledButton("Zurueck", new Color(100, 100, 100));

        btnHLF.addActionListener(e -> {
            d.dispose(); 
            parentFrame.dispose(); 
            
            // NEU: Lade den Spielstand heimlich im Hintergrund, falls noch nicht passiert!
            if (LogistikSimulator.wachen.isEmpty()) {
                if (!SpeicherManager.laden(SpeicherManager.getDokumentePfad())) {
                    LogistikSimulator.initStandardDaten(); // Falls kein Savegame existiert, mach ein neues
                }
            }
            
            WachalltagSimulator.starten(); 
        });

        btnELW.addActionListener(e -> {
            JOptionPane.showMessageDialog(d, "Der ELW-Modus wird geladen...\n(Dieses Feature ist in Entwicklung!)", "Modus: ELW", JOptionPane.INFORMATION_MESSAGE);
            // Spaeter kommt hier der Startbefehl fuer das ELW-Fenster rein
        });

        btnZurueck.addActionListener(e -> d.dispose());

        content.add(btnHLF);
        content.add(btnELW);
        content.add(btnZurueck);

        d.add(content, BorderLayout.CENTER);
        d.setVisible(true);
    }

    private static void checkForUpdates(JFrame parentFrame) {
        try {
            URL url = new URL("https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                reader.close();
                String json = jsonBuilder.toString();

                String latestVersion = "";
                String downloadUrl = "";

                int tagKey = json.indexOf("\"tag_name\"");
                if (tagKey != -1) {
                    int valStart = json.indexOf("\"", json.indexOf(":", tagKey));
                    int valEnd = json.indexOf("\"", valStart + 1);
                    if (valStart != -1 && valEnd != -1) {
                        latestVersion = json.substring(valStart + 1, valEnd);
                    }
                }
                
                int dlKey = json.indexOf("\"browser_download_url\"");
                while (dlKey != -1) {
                    int valStart = json.indexOf("\"", json.indexOf(":", dlKey));
                    int valEnd = json.indexOf("\"", valStart + 1);
                    if (valStart != -1 && valEnd != -1) {
                        String foundUrl = json.substring(valStart + 1, valEnd);
                        if (foundUrl.endsWith(".exe")) {
                            downloadUrl = foundUrl;
                            break; 
                        }
                    }
                    dlKey = json.indexOf("\"browser_download_url\"", valEnd);
                }

                if (!latestVersion.isEmpty() && !latestVersion.equals(CURRENT_VERSION)) {
                    if (downloadUrl.isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, "Neue Version gefunden (" + latestVersion + "), aber keine .exe hinterlegt!", "Fehler", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int wahl = JOptionPane.showConfirmDialog(parentFrame, 
                        "Version " + latestVersion + " ist verfuegbar!\nSoll das Update jetzt heruntergeladen und installiert werden?", 
                        "Update gefunden", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.QUESTION_MESSAGE);
                        
                    if (wahl == JOptionPane.YES_OPTION) {
                        downloadAndInstallUpdate(parentFrame, downloadUrl);
                    }
                } else {
                    JOptionPane.showMessageDialog(parentFrame, "Du bist auf dem neuesten Stand!\nAktuelle Version: " + CURRENT_VERSION, "Kein Update", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, "Netzwerkfehler: " + conn.getResponseCode(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
            conn.disconnect();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "Fehler: " + ex.getMessage(), "Netzwerkfehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void downloadAndInstallUpdate(JFrame parentFrame, String downloadUrl) {
        JDialog progressDialog = new JDialog(parentFrame, "Update wird heruntergeladen...", true);
        progressDialog.setSize(350, 100);
        progressDialog.setLayout(new BorderLayout(10, 10));
        progressDialog.setLocationRelativeTo(parentFrame);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setBackground(new Color(60, 60, 60));
        progressBar.setForeground(new Color(39, 174, 96));

        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnl.setBackground(new Color(35, 35, 35));
        pnl.add(progressBar, BorderLayout.CENTER);
        progressDialog.add(pnl);

        Thread downloadThread = new Thread(() -> {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setInstanceFollowRedirects(false);
                
                int status = conn.getResponseCode();

                while (status >= 300 && status <= 399) {
                    String redirectUrl = conn.getHeaderField("Location");
                    conn.disconnect();
                    
                    url = new URL(redirectUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    status = conn.getResponseCode();
                }

                if (status != 200) {
                    throw new Exception("HTTP Status: " + status);
                }

                int fileSize = conn.getContentLength();
                if (fileSize == -1) {
                    SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(true));
                }

                InputStream in = conn.getInputStream();
                FileOutputStream out = new FileOutputStream("update.exe");

                byte[] buffer = new byte[8192];
                int bytesRead;
                int downloaded = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;
                    if (fileSize != -1) {
                        int percent = (int) ((downloaded * 100L) / fileSize);
                        SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
                    }
                }

                out.close();
                in.close();
                conn.disconnect();
                
                File checkFile = new File("update.exe");
                if (checkFile.exists() && checkFile.length() < 100000) { 
                    StringBuilder errorContent = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader(checkFile))) {
                        String line;
                        int lines = 0;
                        while ((line = reader.readLine()) != null && lines < 2) { 
                            errorContent.append(line).append(" ");
                            lines++;
                        }
                    }
                    checkFile.delete();
                    throw new Exception("Server meldet:\n" + errorContent.toString());
                }

                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    installAndRestart();
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();
                    JOptionPane.showMessageDialog(parentFrame, "Fehler beim Download: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                });
            }
        });

        downloadThread.start();
        progressDialog.setVisible(true);
    }

    private static void installAndRestart() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                File batFile = new File("update.bat");
                FileWriter fwBat = new FileWriter(batFile);
                fwBat.write("@echo off\n");
                fwBat.write("cd /d \"%~dp0\"\n"); 
                fwBat.write("timeout /t 3 /nobreak > NUL\n"); 
                fwBat.write("del /f /q \"" + EXE_NAME + "\"\n"); 
                fwBat.write("move /y \"update.exe\" \"" + EXE_NAME + "\"\n"); 
                fwBat.write("start \"\" \"" + EXE_NAME + "\"\n"); 
                fwBat.write("del /f /q \"update.vbs\"\n"); 
                fwBat.write("(goto) 2>nul & del \"%~f0\"\n"); 
                fwBat.close();

                File vbsFile = new File("update.vbs");
                FileWriter fwVbs = new FileWriter(vbsFile);
                fwVbs.write("Set WshShell = CreateObject(\"WScript.Shell\")\n");
                fwVbs.write("WshShell.Run chr(34) & \"update.bat\" & Chr(34), 0\n"); 
                fwVbs.write("Set WshShell = Nothing\n");
                fwVbs.close();

                Runtime.getRuntime().exec("wscript update.vbs");
                System.exit(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Konnte Neustart-Skript nicht erstellen: " + e.getMessage());
        }
    }
}