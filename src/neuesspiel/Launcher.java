package neuesspiel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Launcher {

    // --- HIER DEINE DATEN EINTRAGEN ---
    // Passe dies an die Version an, die du als naechstes hochlädst (z.B. "v2" oder "v4")
    public static final String CURRENT_VERSION = "v5"; 
    
    // Dein Repo
    public static final String GITHUB_REPO = "SN-ILW/Feuerwehr-Verwaltung"; 
    
    // So heisst die Datei, die deine GitHub-Action am Ende ausspuckt!
    public static final String EXE_NAME = "Feuerwehr-Verwaltung.exe";
    // ----------------------------------

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", new Color(35, 35, 35));
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("OptionPane.background", new Color(35, 35, 35));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
        } catch (Exception e) {}

        JFrame frame = new JFrame("BOS Leitstellen Simulator - Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null); 
        frame.setLayout(new BorderLayout());
        
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(25, 25, 25));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel lblTitle = new JLabel("BOS Leitstellen & Logistik Simulator", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JLabel lblVersion = new JLabel("Aktuelle Version: " + CURRENT_VERSION, SwingConstants.CENTER);
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblVersion.setForeground(new Color(150, 150, 150));
        
        topPanel.add(lblTitle);
        topPanel.add(lblVersion);
        frame.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.setBackground(new Color(35, 35, 35));

        JButton btnStart = createStyledButton("Verwaltung oeffnen", new Color(39, 174, 96));
        JButton btnUpdate = createStyledButton("Update suchen", new Color(41, 128, 185));
        JButton btnExit = createStyledButton("Beenden", new Color(192, 57, 43));

        btnStart.addActionListener(e -> {
            frame.dispose(); 
            LogistikSimulator.main(new String[]{}); 
        });

        btnUpdate.addActionListener(e -> checkForUpdates(frame));

        btnExit.addActionListener(e -> System.exit(0));

        centerPanel.add(btnStart);
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

    private static void checkForUpdates(JFrame parentFrame) {
        try {
            URL url = new URL("https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            // GANZ WICHTIG: Das ist der Schluessel, damit GitHub die Anfrage nicht mehr blockiert!
            conn.setRequestProperty("User-Agent", "AutoUpdater-BOS");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String latestVersion = "";
                String downloadUrl = "";
                
                while ((line = reader.readLine()) != null) {
                    if (line.contains("\"tag_name\":")) {
                        latestVersion = line.split(":")[1].replace("\"", "").replace(",", "").trim();
                    }
                    // Wir suchen jetzt nach der .exe Datei statt nach der .jar!
                    if (line.contains("\"browser_download_url\":") && line.contains(".exe")) {
                        downloadUrl = line.split("\"")[3];
                    }
                }
                reader.close();

                if (!latestVersion.isEmpty() && !latestVersion.equals(CURRENT_VERSION)) {
                    if (downloadUrl.isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, "Neue Version gefunden (" + latestVersion + "), aber es wurde keine .exe im GitHub Release gefunden!", "Fehler", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(parentFrame, "Konnte nicht nach Updates suchen. HTTP Fehlercode: " + conn.getResponseCode(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
            conn.disconnect();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "Fehler bei der Verbindung zu GitHub:\n" + ex.getMessage(), "Netzwerkfehler", JOptionPane.ERROR_MESSAGE);
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
                HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl).openConnection();
                conn.setRequestProperty("User-Agent", "AutoUpdater-BOS"); // Auch hier den User-Agent setzen
                conn.setInstanceFollowRedirects(true);
                int status = conn.getResponseCode();
                
                // GitHub leitet Downloads oft auf externe Server um, das fangen wir hier sauber ab
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    String redirectUrl = conn.getHeaderField("Location");
                    conn = (HttpURLConnection) new URL(redirectUrl).openConnection();
                    conn.setRequestProperty("User-Agent", "AutoUpdater-BOS");
                }

                int fileSize = conn.getContentLength();
                InputStream in = conn.getInputStream();
                // Wir speichern das Update als update.exe
                FileOutputStream out = new FileOutputStream("update.exe");

                byte[] buffer = new byte[4096];
                int bytesRead;
                int downloaded = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;
                    int percent = (int) ((downloaded * 100L) / fileSize);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
                }

                out.close();
                in.close();
                conn.disconnect();

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
            // Prueft, ob wir auf Windows sind
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                // Erstellt das Batch-Skript fuer Windows
                File batFile = new File("update.bat");
                FileWriter fw = new FileWriter(batFile);
                fw.write("@echo off\n");
                fw.write("timeout /t 2 /nobreak > NUL\n"); // Wartet 2 Sekunden, bis das aktuelle Spiel zu ist
                fw.write("del \"" + EXE_NAME + "\"\n"); // Loescht die alte Feuerwehr-Verwaltung.exe
                fw.write("move /y \"update.exe\" \"" + EXE_NAME + "\"\n"); // Macht die update.exe zur neuen Feuerwehr-Verwaltung.exe
                fw.write("start \"\" \"" + EXE_NAME + "\"\n"); // Startet das Spiel neu
                fw.write("del update.bat\n"); // Das Skript loescht sich abschliessend selbst
                fw.close();

                Runtime.getRuntime().exec("cmd /c start update.bat");
                System.exit(0); // Beendet das aktuelle Spiel
            } else {
                JOptionPane.showMessageDialog(null, "Update erfolgreich als 'update.exe' heruntergeladen.\nAutomatischer Neustart wird nur unter Windows unterstuetzt.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Konnte Neustart-Skript nicht erstellen: " + e.getMessage());
        }
    }
}