package neuesspiel;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapPanel extends JPanel {
    
    public JMapViewer map;
    public HashMap<String, Coordinate> wachenKoords = new HashMap<>();
    public HashMap<Integer, EinsatzMarker> aktiveEinsaetze = new HashMap<>();
    private int einsatzCounter = 0;

    public MapPanel() {
        setLayout(new BorderLayout());
        map = new JMapViewer();
        map.setTileSource(new OsmTileSource.Mapnik());
        map.setDisplayPosition(new Coordinate(53.6333, 11.4166), 12);
        
        wachenKoords.put("BF Sued", new Coordinate(53.591535, 11.431180));
        wachenKoords.put("BF Nord", new Coordinate(53.639256, 11.393235));
        wachenKoords.put("FF Mitte", new Coordinate(53.640313, 11.402972));
        wachenKoords.put("FF Warnitz", new Coordinate(53.664316, 11.349134));
        wachenKoords.put("FF Schlossgarten", new Coordinate(53.605449, 11.429440));
        wachenKoords.put("FF Wuestmark", new Coordinate(53.583027, 11.393445));
        wachenKoords.put("FF Wickendorf", new Coordinate(53.680399, 11.427377));

        for (String wName : wachenKoords.keySet()) {
            WachenMarker wache = new WachenMarker(wName, wachenKoords.get(wName));
            wache.setBackColor(wName.startsWith("BF") ? new Color(192, 57, 43) : new Color(243, 156, 18));
            map.addMapMarker(wache);
        }

        add(map, BorderLayout.CENTER);

        Timer animTimer = new Timer(33, e -> {
            boolean brauchtRepaint = false;
            for (org.openstreetmap.gui.jmapviewer.interfaces.MapMarker m : map.getMapMarkerList()) {
                if (m instanceof FahrzeugMarker) {
                    ((FahrzeugMarker) m).updatePosition();
                    brauchtRepaint = true;
                } else if (m instanceof EinsatzMarker) {
                    brauchtRepaint = true; 
                }
            }
            if (brauchtRepaint) map.repaint();
        });
        animTimer.start();
    }

    public int erstelleEinsatz(Coordinate ziel) {
        int id = einsatzCounter++;
        EinsatzMarker em = new EinsatzMarker(ziel, id);
        aktiveEinsaetze.put(id, em);
        map.addMapMarker(em);
        return id;
    }

    // NEU: Sucht, wo das Auto gerade auf der Strasse steht
    public Coordinate getFahrzeugPosition(String fzName) {
        for (org.openstreetmap.gui.jmapviewer.interfaces.MapMarker m : map.getMapMarkerList()) {
            if (m instanceof FahrzeugMarker && ((FahrzeugMarker) m).fzName.equals(fzName)) {
                return new Coordinate(m.getLat(), m.getLon());
            }
        }
        return null; // Wenn es nicht faehrt (auf Wache steht)
    }

    // NEU: Loescht den fahrenden Marker von der Strasse
    public void entferneFahrzeug(String fzName) {
        org.openstreetmap.gui.jmapviewer.interfaces.MapMarker toRemove = null;
        for (org.openstreetmap.gui.jmapviewer.interfaces.MapMarker m : map.getMapMarkerList()) {
            if (m instanceof FahrzeugMarker && ((FahrzeugMarker) m).fzName.equals(fzName)) {
                toRemove = m;
                break;
            }
        }
        if (toRemove != null) map.removeMapMarker(toRemove);
    }

    // UPDATE: Start und Ziel werden nun exakt uebergeben (sowie Farben)
    public void sendeFahrzeug(String fzName, Coordinate start, Coordinate ziel, int ruestZeitSec, int fahrZeitSec, boolean isStatus1, boolean startVisible) {
        entferneFahrzeug(fzName); // Falls er gerade Status 1 faehrt, unterbrechen wir das!
        if (start != null) {
            List<Coordinate> route = RoutingEngine.berechneRoute(start, ziel);
            FahrzeugMarker fm = new FahrzeugMarker(fzName, route, ruestZeitSec * 1000L, fahrZeitSec * 1000L, isStatus1, startVisible);
            map.addMapMarker(fm);
        }
    }

    public void fahrzeugAngekommen(int einsatzId, String fzName) {
        entferneFahrzeug(fzName); // Von Strasse entfernen
        EinsatzMarker em = aktiveEinsaetze.get(einsatzId);
        if (em != null) em.angekommeneFahrzeuge.add(fzName);
        map.repaint();
    }
    
    public void fahrzeugFrei(int einsatzId, String fzName) {
        EinsatzMarker em = aktiveEinsaetze.get(einsatzId);
        if (em != null) {
            em.angekommeneFahrzeuge.remove(fzName);
            if (em.angekommeneFahrzeuge.isEmpty()) {
                map.removeMapMarker(em);
                aktiveEinsaetze.remove(einsatzId);
            }
        }
        map.repaint();
    }

    // ==========================================
    // EIGENE MARKER
    // ==========================================

    class WachenMarker extends MapMarkerDot {
        public WachenMarker(String name, Coordinate coord) { super(name, new Coordinate(coord.getLat(), coord.getLon())); }
        @Override public boolean equals(Object obj) { return this == obj; }
    }

    class EinsatzMarker extends MapMarkerDot {
        private long startTime;
        private int indexOffset;
        public List<String> angekommeneFahrzeuge = new ArrayList<>();

        public EinsatzMarker(Coordinate coord, int index) {
            super(new Coordinate(coord.getLat(), coord.getLon())); 
            this.startTime = System.currentTimeMillis();
            this.indexOffset = index;
        }

        @Override public boolean equals(Object obj) { return this == obj; }

        @Override
        public void paint(Graphics g, Point position, int radius) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            long elapsed = System.currentTimeMillis() - startTime;
            int pulse = 10 + (int)(Math.abs(Math.sin(elapsed / 300.0)) * 20); 
            g2.setColor(new Color(255, 0, 0, 100)); 
            g2.fillOval(position.x - pulse, position.y - pulse, pulse*2, pulse*2);
            g2.setColor(Color.RED);
            g2.fillOval(position.x - 6, position.y - 6, 12, 12);

            if (angekommeneFahrzeuge.isEmpty()) return;

            int boxX = map.getWidth() - 250;
            int boxY = 50 + ((indexOffset % 5) * 150); 
            
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(position.x, position.y, boxX, boxY + 20); 

            int boxHeight = 40 + (angekommeneFahrzeuge.size() * 18);
            g2.setColor(new Color(240, 240, 240, 230));
            g2.fillRect(boxX, boxY, 230, boxHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(boxX, boxY, 230, boxHeight);

            g2.setFont(new Font("Consolas", Font.BOLD, 14));
            g2.setColor(Color.RED);
            g2.drawString("AM EINSATZ", boxX + 75, boxY + 20);
            g2.setColor(Color.BLACK);
            g2.drawLine(boxX, boxY + 25, boxX + 230, boxY + 25);
            
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            int textY = boxY + 45;
            for (String fz : angekommeneFahrzeuge) {
                g2.drawString(fz, boxX + 10, textY);
                textY += 18;
            }
        }
    }

   class FahrzeugMarker extends MapMarkerDot {
        private List<Coordinate> route;
        private long fahrtStartZeit, ankunftZeit;
        public String fzName;
        private boolean isStatus1, startVisible;

        public FahrzeugMarker(String fzName, List<Coordinate> route, long delayMs, long fahrDauerMs, boolean isStatus1, boolean startVisible) {
            super(new Coordinate(route.get(0).getLat(), route.get(0).getLon())); 
            this.fzName = fzName;
            this.route = route;
            this.fahrtStartZeit = System.currentTimeMillis() + delayMs; 
            this.ankunftZeit = fahrtStartZeit + fahrDauerMs;
            this.isStatus1 = isStatus1;
            this.startVisible = startVisible;
        }
        
        @Override public boolean equals(Object obj) { return this == obj; }

        public void updatePosition() {
            long now = System.currentTimeMillis();
            if (now < fahrtStartZeit) return; 

            double progress = (double) (now - fahrtStartZeit) / (ankunftZeit - fahrtStartZeit);
            if (progress >= 1.0) progress = 1.0;
            
            double indexFloat = progress * (route.size() - 1);
            int baseIndex = (int) indexFloat;
            
            if (baseIndex >= route.size() - 1) {
                setLat(route.get(route.size()-1).getLat());
                setLon(route.get(route.size()-1).getLon());
            } else {
                double fraction = indexFloat - baseIndex;
                Coordinate p1 = route.get(baseIndex);
                Coordinate p2 = route.get(baseIndex + 1);
                setLat(p1.getLat() + (p2.getLat() - p1.getLat()) * fraction);
                setLon(p1.getLon() + (p2.getLon() - p1.getLon()) * fraction);
            }
        }

        @Override
        public void paint(Graphics g, Point position, int radius) {
            // Versteckt das Auto während der Rüstzeit
            if (!startVisible && System.currentTimeMillis() < fahrtStartZeit) return; 
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // --- DIE NEUE SCHILD-OPTIK (WIE IM BILD) ---
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
            FontMetrics fm = g2.getFontMetrics();
            
            // Wir messen live aus, wie breit der Text ist
            int paddingX = 6; // Innenabstand links/rechts
            int paddingY = 4; // Innenabstand oben/unten
            int textWidth = fm.stringWidth(fzName);
            int textHeight = fm.getHeight();
            
            // Daraus berechnet sich die exakte Box-Größe
            int boxWidth = textWidth + (paddingX * 2);
            int boxHeight = textHeight + (paddingY * 2);
            
            // Wir zentrieren die Box exakt über dem GPS-Punkt der Straße
            int startX = position.x - (boxWidth / 2);
            int startY = position.y - (boxHeight / 2);
            
            // 1. Hintergrund zeichnen (Status 1 = Grün, Status 3 = Kräftiges Orange wie im Bild)
            g2.setColor(isStatus1 ? new Color(46, 204, 113) : new Color(255, 115, 0)); 
            g2.fillRect(startX, startY, boxWidth, boxHeight);
            
            // 2. Den dicken schwarzen Rahmen zeichnen
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2)); // 2 Pixel dick
            g2.drawRect(startX, startY, boxWidth, boxHeight);
            
            // 3. Den schwarzen Text exakt in die Mitte setzen
            g2.setColor(Color.BLACK);
            g2.drawString(fzName, startX + paddingX, startY + fm.getAscent() + paddingY - 1);
        }
    }
}