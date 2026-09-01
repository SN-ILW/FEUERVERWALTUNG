package neuesspiel;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RoutingEngine {
    
    public static boolean isReady = true; 

    public static void init() {
        System.out.println("RoutingEngine: Nutze blitzschnelle Online-Navigation (OSRM).");
    }

    public static List<Coordinate> berechneRoute(Coordinate start, Coordinate ziel) {
        List<Coordinate> route = new ArrayList<>();
        
        // WICHTIG: Wir machen echte Kopien (Klone) der Koordinaten, 
        // damit das Auto beim Fahren nicht die Wache mitzieht!
        route.add(new Coordinate(start.getLat(), start.getLon()));

        try {
            String urlString = "http://router.project-osrm.org/route/v1/driving/" 
                    + start.getLon() + "," + start.getLat() + ";" 
                    + ziel.getLon() + "," + ziel.getLat() 
                    + "?geometries=geojson&overview=full";
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "LeitstellenSimulatorSchwerin/1.0"); 
            conn.setConnectTimeout(2000); 
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                String json = response.toString();
                
                int coordsStart = json.indexOf("\"coordinates\":[[");
                if (coordsStart != -1) {
                    int coordsEnd = json.indexOf("]]", coordsStart);
                    String coordsString = json.substring(coordsStart + 16, coordsEnd); 
                    
                    String[] points = coordsString.split("\\],\\[");
                    for (String p : points) {
                        String[] lonLat = p.split(",");
                        if (lonLat.length == 2) {
                            double lon = Double.parseDouble(lonLat[0]);
                            double lat = Double.parseDouble(lonLat[1]);
                            route.add(new Coordinate(lat, lon)); 
                        }
                    }
                }
            }
            conn.disconnect();
            
        } catch (Exception e) {
            System.out.println("Routing Fehler -> Nutze Luftlinie.");
        }

        route.add(new Coordinate(ziel.getLat(), ziel.getLon())); 
        return route;
    }
}