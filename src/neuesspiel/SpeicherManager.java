package neuesspiel;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.ArrayList;

public class SpeicherManager {

    // NEU: Diese Methode sucht den sicheren Windows-Benutzerordner (C:\Users\DeinName\)
    public static String getSicherenSpeicherPfad() {
        return System.getProperty("user.home") + java.io.File.separator + "FeuerwehrVerwaltung_Savegame.properties";
    }

    private static void setSafe(Properties p, String key, String val) {
        if (key != null) {
            p.setProperty(key, val != null ? val : "");
        }
    }

    private static int parseIntSafe(String val, int def) {
        try {
            if(val == null || val.trim().isEmpty()) return def;
            return Integer.parseInt(val.trim());
        } catch(Exception e) { return def; }
    }

    private static double parseDoubleSafe(String val, double def) {
        try {
            if(val == null || val.trim().isEmpty()) return def;
            return Double.parseDouble(val.trim());
        } catch(Exception e) { return def; }
    }

    private static boolean parseBoolSafe(String val, boolean def) {
        if(val == null || val.trim().isEmpty()) return def;
        return Boolean.parseBoolean(val.trim());
    }

    public static void speichern(String dateiPfad) {
        System.out.println("\n=== [DEBUG] SPEICHER-VORGANG GESTARTET ===");
        
        // HIER DER TRICK: Wir ignorieren den uebergebenen Pfad und nutzen unseren sicheren Pfad!
        String echterPfad = getSicherenSpeicherPfad();
        
        try (FileOutputStream out = new FileOutputStream(echterPfad);
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
             
            Properties p = new Properties();
            
            setSafe(p, "budget", String.valueOf(LogistikSimulator.budget));
            setSafe(p, "xp", String.valueOf(LogistikSimulator.xp));
            setSafe(p, "level", String.valueOf(LogistikSimulator.level));
            setSafe(p, "tag", String.valueOf(LogistikSimulator.tag));
            setSafe(p, "inGameSekunden", String.valueOf(LogistikSimulator.inGameSekunden));
            setSafe(p, "speed", String.valueOf(LogistikSimulator.speed));
            
            setSafe(p, "aktuellerKredit", String.valueOf(LogistikSimulator.aktuellerKredit));
            setSafe(p, "taeglicheKreditRate", String.valueOf(LogistikSimulator.taeglicheKreditRate));
            
            setSafe(p, "techGrossabnehmer", String.valueOf(LogistikSimulator.techGrossabnehmer));
            setSafe(p, "lehrerStufe", String.valueOf(LogistikSimulator.lehrerStufe));
            setSafe(p, "calltakerStufe", String.valueOf(LogistikSimulator.calltakerStufe));
            setSafe(p, "techKlinikCrivitz", String.valueOf(LogistikSimulator.techKlinikCrivitz));
            setSafe(p, "techKlinikLeezen", String.valueOf(LogistikSimulator.techKlinikLeezen));
            setSafe(p, "techKlinikHagenow", String.valueOf(LogistikSimulator.techKlinikHagenow));
            
            setSafe(p, "cfgKrankentransport", String.valueOf(LogistikSimulator.cfgKrankentransport));
            setSafe(p, "cfgBeschaedigung", String.valueOf(LogistikSimulator.cfgBeschaedigung));
            setSafe(p, "cfgKrankheit", String.valueOf(LogistikSimulator.cfgKrankheit));
            setSafe(p, "cfgAutoTransfer", String.valueOf(LogistikSimulator.cfgAutoTransfer));
            setSafe(p, "cfgLogistikAktiv", String.valueOf(LogistikSimulator.cfgLogistikAktiv));
            
            setSafe(p, "cfgSoundNotruf", String.valueOf(LogistikSimulator.cfgSoundNotruf));
            setSafe(p, "cfgSoundStatus6", String.valueOf(LogistikSimulator.cfgSoundStatus6));
            setSafe(p, "cfgSoundStatus7", String.valueOf(LogistikSimulator.cfgSoundStatus7));
            setSafe(p, "volNotruf", String.valueOf(LogistikSimulator.volNotruf));
            setSafe(p, "volStatus6", String.valueOf(LogistikSimulator.volStatus6));
            setSafe(p, "volStatus7", String.valueOf(LogistikSimulator.volStatus7));

            setSafe(p, "hk_pause", String.valueOf(LogistikSimulator.hotkeyPause));
            setSafe(p, "hk_play", String.valueOf(LogistikSimulator.hotkeyPlay));
            setSafe(p, "hk_fast", String.valueOf(LogistikSimulator.hotkeyFast));
            setSafe(p, "hk_disp", String.valueOf(LogistikSimulator.hotkeyDisp));
            setSafe(p, "hk_dienst", String.valueOf(LogistikSimulator.hotkeyDienstplan));
            setSafe(p, "hk_post", String.valueOf(LogistikSimulator.hotkeyPostfach));
            setSafe(p, "hk_fuhr", String.valueOf(LogistikSimulator.hotkeyFuhrpark));
            setSafe(p, "hk_e_erst", String.valueOf(LogistikSimulator.hotkeyEinsatzErsteller));
            setSafe(p, "hk_e_edit", String.valueOf(LogistikSimulator.hotkeyEinsatzEditor));
            setSafe(p, "hk_pers", String.valueOf(LogistikSimulator.hotkeyPersonalEinstellen));

            if (LogistikSimulator.aktuelleMission != null) {
                setSafe(p, "miss_titel", LogistikSimulator.aktuelleMission.titel);
                setSafe(p, "miss_desc", LogistikSimulator.aktuelleMission.beschreibung);
                setSafe(p, "miss_typ", LogistikSimulator.aktuelleMission.typ);
                setSafe(p, "miss_ziel", String.valueOf(LogistikSimulator.aktuelleMission.zielWert));
                setSafe(p, "miss_geld", String.valueOf(LogistikSimulator.aktuelleMission.belohnungGeld));
                setSafe(p, "miss_xp", String.valueOf(LogistikSimulator.aktuelleMission.belohnungXp));
                setSafe(p, "miss_fort", String.valueOf(LogistikSimulator.aktuelleMission.fortschritt));
                setSafe(p, "miss_done", String.valueOf(LogistikSimulator.aktuelleMission.abgeschlossen));
            }

            setSafe(p, "vertragsCount", String.valueOf(LogistikSimulator.aktiveVertraege.size()));
            for(int i = 0; i < LogistikSimulator.aktiveVertraege.size(); i++) {
                Vertrag v = LogistikSimulator.aktiveVertraege.get(i);
                if (v == null) continue;
                setSafe(p, "v_" + i + "_ag", v.auftraggeber);
                setSafe(p, "v_" + i + "_desc", v.beschreibung);
                setSafe(p, "v_" + i + "_art", v.zielEinsatzArt);
                setSafe(p, "v_" + i + "_ziel", String.valueOf(v.zielMenge));
                setSafe(p, "v_" + i + "_akt", String.valueOf(v.aktuelleMenge));
                setSafe(p, "v_" + i + "_bel", String.valueOf(v.belohnungProTag));
                setSafe(p, "v_" + i + "_strafe", String.valueOf(v.strafeBeiFehlschlag));
            }
            
            setSafe(p, "vorlagenCount", String.valueOf(LogistikSimulator.vorlagenPool.size()));
            for(int i = 0; i < LogistikSimulator.vorlagenPool.size(); i++) {
                EinsatzVorlage v = LogistikSimulator.vorlagenPool.get(i);
                if (v == null) continue;
                String prefix = "vorlage_" + i;
                setSafe(p, prefix + "_art", v.art);
                setSafe(p, prefix + "_stichwort", v.stichwort);
                setSafe(p, prefix + "_desc", v.beschreibung);
                setSafe(p, prefix + "_rtw", String.valueOf(v.reqRTW));
                setSafe(p, prefix + "_nef", String.valueOf(v.reqNEF));
                setSafe(p, prefix + "_ktw", String.valueOf(v.reqKTW));
                setSafe(p, prefix + "_hlf", String.valueOf(v.reqHLF));
                setSafe(p, prefix + "_dlk", String.valueOf(v.reqDLK));
                setSafe(p, prefix + "_elw", String.valueOf(v.reqELW));
                setSafe(p, prefix + "_tlf", String.valueOf(v.reqTLF));
                setSafe(p, prefix + "_mtw", String.valueOf(v.reqMTW));
                setSafe(p, prefix + "_hatNach", String.valueOf(v.hatNachforderung));
                setSafe(p, prefix + "_nachProz", String.valueOf(v.nachforderungProzent));
                setSafe(p, prefix + "_nachTyp", v.nachforderungTyp);
                setSafe(p, prefix + "_minLvl", String.valueOf(v.minLevel));
            }
            
            setSafe(p, "matDefCount", String.valueOf(LogistikSimulator.customMaterials.size()));
            for(int i = 0; i < LogistikSimulator.customMaterials.size(); i++) {
                CustomMaterial cm = LogistikSimulator.customMaterials.get(i);
                if (cm == null) continue;
                setSafe(p, "matDef_" + i + "_name", cm.name);
                setSafe(p, "matDef_" + i + "_fz", cm.fahrzeuge != null ? String.join(",", cm.fahrzeuge) : "");
                setSafe(p, "matDef_" + i + "_verb", String.valueOf(cm.maxVerbrauch));
                setSafe(p, "matDef_" + i + "_preis", String.valueOf(cm.preis));
                setSafe(p, "matDef_" + i + "_menge", String.valueOf(cm.bestellMenge));
                setSafe(p, "matDef_" + i + "_warn", String.valueOf(cm.warnSchwelle));
            }
            
            setSafe(p, "hlCount", String.valueOf(LogistikSimulator.hauptlager.size()));
            int hlIdx = 0;
            for (String mName : LogistikSimulator.hauptlager.keySet()) {
                setSafe(p, "hl_" + hlIdx + "_name", mName);
                setSafe(p, "hl_" + hlIdx + "_anz", String.valueOf(LogistikSimulator.hauptlager.get(mName)));
                hlIdx++;
            }

            setSafe(p, "wachenCount", String.valueOf(LogistikSimulator.wachen.size()));
            for (int i = 0; i < LogistikSimulator.wachen.size(); i++) {
                Wache w = LogistikSimulator.wachen.get(i);
                if (w == null) continue;
                setSafe(p, "wache_" + i + "_name", w.name);
                setSafe(p, "wache_" + i + "_kennung", w.kennung);
                
                setSafe(p, "wache_" + i + "_stufe", String.valueOf(w.stufe));
                
                if (w.fuhrpark != null) {
                    setSafe(p, "wache_" + i + "_fzgCount", String.valueOf(w.fuhrpark.size()));
                    for (int f = 0; f < w.fuhrpark.size(); f++) {
                        Fahrzeug fzg = w.fuhrpark.get(f);
                        if (fzg == null) continue;
                        String fPfx = "wache_" + i + "_fzg_" + f;
                        setSafe(p, fPfx + "_funk", fzg.funkrufname);
                        setSafe(p, fPfx + "_typ", fzg.typ);
                        setSafe(p, fPfx + "_status", String.valueOf(fzg.status));
                        setSafe(p, fPfx + "_km", String.valueOf(fzg.kilometer));
                        setSafe(p, fPfx + "_tuev", String.valueOf(fzg.naechsteInspektion));
                        setSafe(p, fPfx + "_grund", fzg.ausfallGrund);
                        setSafe(p, fPfx + "_repDauer", String.valueOf(fzg.reparaturDauer));
                    }
                } else {
                    setSafe(p, "wache_" + i + "_fzgCount", "0");
                }
                
                if (w.material != null) {
                    setSafe(p, "wache_" + i + "_matCount", String.valueOf(w.material.size()));
                    int wMatIdx = 0;
                    for (String mName : w.material.keySet()) {
                        setSafe(p, "wache_" + i + "_mat_" + wMatIdx + "_name", mName);
                        setSafe(p, "wache_" + i + "_mat_" + wMatIdx + "_anz", String.valueOf(w.material.get(mName)));
                        wMatIdx++;
                    }
                } else {
                    setSafe(p, "wache_" + i + "_matCount", "0");
                }
                
                if (w.upgrades != null) {
                    setSafe(p, "wache_" + i + "_upgradeCount", String.valueOf(w.upgrades.size()));
                    for(int u = 0; u < w.upgrades.size(); u++) {
                        WachenAusbau wa = w.upgrades.get(u);
                        if (wa == null) continue;
                        setSafe(p, "wache_" + i + "_upg_" + u + "_id", wa.id);
                        setSafe(p, "wache_" + i + "_upg_" + u + "_name", wa.name);
                        setSafe(p, "wache_" + i + "_upg_" + u + "_desc", wa.beschreibung);
                        setSafe(p, "wache_" + i + "_upg_" + u + "_cost", String.valueOf(wa.kosten));
                    }
                } else {
                    setSafe(p, "wache_" + i + "_upgradeCount", "0");
                }

                if (w.personalPool != null) {
                    setSafe(p, "wache_" + i + "_persCount", String.valueOf(w.personalPool.size()));
                    for (int j = 0; j < w.personalPool.size(); j++) {
                        Personal pers = w.personalPool.get(j);
                        if (pers == null) continue;
                        String prefix = "wache_" + i + "_pers_" + j;
                        setSafe(p, prefix + "_name", pers.name);
                        setSafe(p, prefix + "_status", pers.status);
                        setSafe(p, prefix + "_qual", pers.qualifikationen != null ? String.join(",", pers.qualifikationen) : "");
                        setSafe(p, prefix + "_schichten", String.valueOf(pers.schichtenMonat));
                        setSafe(p, prefix + "_uStart", String.valueOf(pers.urlaubStart));
                        setSafe(p, prefix + "_uEnd", String.valueOf(pers.urlaubEnd));
                        setSafe(p, prefix + "_kBis", String.valueOf(pers.krankBis));
                        setSafe(p, prefix + "_fzg", pers.zugewiesenesFahrzeug);
                        setSafe(p, prefix + "_gStat", pers.geplanterStatus);
                        setSafe(p, prefix + "_gFzg", pers.geplantesFahrzeug);
                        
                        setSafe(p, prefix + "_planAkt", pers.planAktuellerMonat != null ? String.join(",", pers.planAktuellerMonat) : "");
                        setSafe(p, prefix + "_planNext", pers.planNaechsterMonat != null ? String.join(",", pers.planNaechsterMonat) : "");
                        
                        if (pers.eigenschaften != null) {
                            setSafe(p, prefix + "_eigCount", String.valueOf(pers.eigenschaften.size()));
                            for(int x = 0; x < pers.eigenschaften.size(); x++) {
                                MitarbeiterEigenschaft eig = pers.eigenschaften.get(x);
                                if (eig == null) continue;
                                setSafe(p, prefix + "_eig_" + x + "_n", eig.name);
                                setSafe(p, prefix + "_eig_" + x + "_d", eig.beschreibung);
                                setSafe(p, prefix + "_eig_" + x + "_t", eig.typ);
                                setSafe(p, prefix + "_eig_" + x + "_e", String.valueOf(eig.effektWert));
                            }
                        } else {
                            setSafe(p, prefix + "_eigCount", "0");
                        }
                    }
                } else {
                    setSafe(p, "wache_" + i + "_persCount", "0");
                }
            }

            for (String key : p.stringPropertyNames()) {
                System.out.println("SCHREIBE: " + key + " = " + p.getProperty(key));
            }
            System.out.println("=== [DEBUG] SPEICHER-VORGANG BEENDET ===\n");

            p.store(writer, "Logistik Simulator Savegame");
        } catch (Exception e) {
            System.out.println("!!! FEHLER BEIM SPEICHERN: " + e.getMessage());
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Achtung: Fehler beim Speichern aufgetreten!\n" + e.getMessage(), "Speicherfehler", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean laden(String dateiPfad) {
        String echterPfad = getSicherenSpeicherPfad();
        File file = new File(echterPfad);
        
        if (!file.exists()) {
            System.out.println("[DEBUG] Keine Savegame Datei unter " + echterPfad + " gefunden.");
            return false;
        }

        try (FileInputStream in = new FileInputStream(file);
             InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
             
            Properties p = new Properties();
            p.load(reader);

            System.out.println("\n=== [DEBUG] LADE-VORGANG GESTARTET ===");
            for (String key : p.stringPropertyNames()) {
                System.out.println("GELADEN: " + key + " = " + p.getProperty(key));
            }
            System.out.println("=== [DEBUG] LADE-VORGANG BEENDET ===\n");

            LogistikSimulator.wachen.clear();
            LogistikSimulator.aktiveVertraege.clear();
            LogistikSimulator.vorlagenPool.clear();
            LogistikSimulator.customMaterials.clear();
            LogistikSimulator.hauptlager.clear();
            
            LogistikSimulator.budget = parseIntSafe(p.getProperty("budget"), 25000);
            LogistikSimulator.xp = parseIntSafe(p.getProperty("xp"), 0);
            LogistikSimulator.level = parseIntSafe(p.getProperty("level"), 1);
            LogistikSimulator.tag = parseIntSafe(p.getProperty("tag"), 1);
            LogistikSimulator.inGameSekunden = parseIntSafe(p.getProperty("inGameSekunden"), 25200);
            LogistikSimulator.speed = parseIntSafe(p.getProperty("speed"), 1);
            
            LogistikSimulator.aktuellerKredit = parseIntSafe(p.getProperty("aktuellerKredit"), 0);
            LogistikSimulator.taeglicheKreditRate = parseIntSafe(p.getProperty("taeglicheKreditRate"), 0);

            LogistikSimulator.techGrossabnehmer = parseBoolSafe(p.getProperty("techGrossabnehmer"), false);
            LogistikSimulator.lehrerStufe = parseIntSafe(p.getProperty("lehrerStufe"), 0);
            LogistikSimulator.calltakerStufe = parseIntSafe(p.getProperty("calltakerStufe"), 0);
            LogistikSimulator.techKlinikCrivitz = parseBoolSafe(p.getProperty("techKlinikCrivitz"), false);
            LogistikSimulator.techKlinikLeezen = parseBoolSafe(p.getProperty("techKlinikLeezen"), false);
            LogistikSimulator.techKlinikHagenow = parseBoolSafe(p.getProperty("techKlinikHagenow"), false);

            boolean legacyWerkstatt = parseBoolSafe(p.getProperty("techWerkstatt"), false);
            boolean legacyRuheraum = parseBoolSafe(p.getProperty("techRuheraum"), false);

            LogistikSimulator.cfgKrankentransport = parseBoolSafe(p.getProperty("cfgKrankentransport"), true);
            LogistikSimulator.cfgBeschaedigung = parseBoolSafe(p.getProperty("cfgBeschaedigung"), true);
            LogistikSimulator.cfgKrankheit = parseBoolSafe(p.getProperty("cfgKrankheit"), true);
            LogistikSimulator.cfgAutoTransfer = parseBoolSafe(p.getProperty("cfgAutoTransfer"), false);
            LogistikSimulator.cfgLogistikAktiv = parseBoolSafe(p.getProperty("cfgLogistikAktiv"), true);
            
            LogistikSimulator.hotkeyPause = parseIntSafe(p.getProperty("hk_pause"), java.awt.event.KeyEvent.VK_SPACE);
            LogistikSimulator.hotkeyPlay = parseIntSafe(p.getProperty("hk_play"), java.awt.event.KeyEvent.VK_1);
            LogistikSimulator.hotkeyFast = parseIntSafe(p.getProperty("hk_fast"), java.awt.event.KeyEvent.VK_2);
            LogistikSimulator.hotkeyDisp = parseIntSafe(p.getProperty("hk_disp"), java.awt.event.KeyEvent.VK_D);
            LogistikSimulator.hotkeyDienstplan = parseIntSafe(p.getProperty("hk_dienst"), java.awt.event.KeyEvent.VK_F1);
            LogistikSimulator.hotkeyPostfach = parseIntSafe(p.getProperty("hk_post"), java.awt.event.KeyEvent.VK_M);
            LogistikSimulator.hotkeyFuhrpark = parseIntSafe(p.getProperty("hk_fuhr"), java.awt.event.KeyEvent.VK_F);
            LogistikSimulator.hotkeyEinsatzErsteller = parseIntSafe(p.getProperty("hk_e_erst"), java.awt.event.KeyEvent.VK_F2);
            LogistikSimulator.hotkeyEinsatzEditor = parseIntSafe(p.getProperty("hk_e_edit"), java.awt.event.KeyEvent.VK_F3);
            LogistikSimulator.hotkeyPersonalEinstellen = parseIntSafe(p.getProperty("hk_pers"), java.awt.event.KeyEvent.VK_P);
            
            if (p.containsKey("miss_titel")) {
                LogistikSimulator.aktuelleMission = new TagesMission(p.getProperty("miss_titel", "Mission"), p.getProperty("miss_desc", ""), p.getProperty("miss_typ", "ALLE"), parseIntSafe(p.getProperty("miss_ziel"), 1), parseIntSafe(p.getProperty("miss_geld"), 0), parseIntSafe(p.getProperty("miss_xp"), 0));
                LogistikSimulator.aktuelleMission.fortschritt = parseIntSafe(p.getProperty("miss_fort"), 0);
                LogistikSimulator.aktuelleMission.abgeschlossen = parseBoolSafe(p.getProperty("miss_done"), false);
            }

            int vCount = parseIntSafe(p.getProperty("vertragsCount"), 0);
            for(int i = 0; i < vCount; i++) {
                Vertrag v = new Vertrag(p.getProperty("v_" + i + "_ag", "Auftraggeber"), p.getProperty("v_" + i + "_desc", ""), p.getProperty("v_" + i + "_art", "KTP"), parseIntSafe(p.getProperty("v_" + i + "_ziel"), 1), parseIntSafe(p.getProperty("v_" + i + "_bel"), 0), parseIntSafe(p.getProperty("v_" + i + "_strafe"), 0));
                v.aktuelleMenge = parseIntSafe(p.getProperty("v_" + i + "_akt"), 0);
                LogistikSimulator.aktiveVertraege.add(v);
            }
            
            if (p.containsKey("vorlagenCount")) {
                int vorlagenCount = parseIntSafe(p.getProperty("vorlagenCount"), 0);
                for(int i = 0; i < vorlagenCount; i++) {
                    String prefix = "vorlage_" + i;
                    EinsatzVorlage v = new EinsatzVorlage(
                        p.getProperty(prefix + "_art", "RD"),
                        p.getProperty(prefix + "_stichwort", "Info"),
                        p.getProperty(prefix + "_desc", "Einsatz"),
                        parseIntSafe(p.getProperty(prefix + "_rtw"), 0),
                        parseIntSafe(p.getProperty(prefix + "_nef"), 0),
                        parseIntSafe(p.getProperty(prefix + "_ktw"), 0),
                        parseIntSafe(p.getProperty(prefix + "_hlf"), 0),
                        parseIntSafe(p.getProperty(prefix + "_dlk"), 0),
                        parseIntSafe(p.getProperty(prefix + "_elw"), 0),
                        parseIntSafe(p.getProperty(prefix + "_tlf"), 0),
                        parseIntSafe(p.getProperty(prefix + "_mtw"), 0),
                        parseBoolSafe(p.getProperty(prefix + "_hatNach"), false),
                        parseIntSafe(p.getProperty(prefix + "_nachProz"), 0),
                        p.getProperty(prefix + "_nachTyp", ""),
                        parseIntSafe(p.getProperty(prefix + "_minLvl"), 1)
                    );
                    LogistikSimulator.vorlagenPool.add(v);
                }
            }

            int matDefCount = parseIntSafe(p.getProperty("matDefCount"), 0);
            for(int i = 0; i < matDefCount; i++) {
                String n = p.getProperty("matDef_" + i + "_name", "Unbekannt");
                String fzStr = p.getProperty("matDef_" + i + "_fz", "");
                ArrayList<String> fzList = new ArrayList<>();
                if(!fzStr.isEmpty()) for(String s : fzStr.split(",")) fzList.add(s);
                int verb = parseIntSafe(p.getProperty("matDef_" + i + "_verb"), 1);
                int preis = parseIntSafe(p.getProperty("matDef_" + i + "_preis"), 100);
                int menge = parseIntSafe(p.getProperty("matDef_" + i + "_menge"), 10);
                int warn = parseIntSafe(p.getProperty("matDef_" + i + "_warn"), 5);
                LogistikSimulator.customMaterials.add(new CustomMaterial(n, fzList, verb, new ArrayList<>(), preis, menge, warn));
            }
            
            int hlCount = parseIntSafe(p.getProperty("hlCount"), 0);
            for(int i = 0; i < hlCount; i++) {
                String n = p.getProperty("hl_" + i + "_name", "Unbekannt");
                int anz = parseIntSafe(p.getProperty("hl_" + i + "_anz"), 0);
                LogistikSimulator.hauptlager.put(n, anz);
            }

            int wachenCount = parseIntSafe(p.getProperty("wachenCount"), 0);
            for (int i = 0; i < wachenCount; i++) {
                Wache w = new Wache(p.getProperty("wache_" + i + "_name", "Wache"), p.getProperty("wache_" + i + "_kennung", "00"));
                
                w.stufe = parseIntSafe(p.getProperty("wache_" + i + "_stufe"), 1);
                
                int fzgCount = parseIntSafe(p.getProperty("wache_" + i + "_fzgCount"), 0);
                for(int f = 0; f < fzgCount; f++) {
                    String fPfx = "wache_" + i + "_fzg_" + f;
                    String funk = p.getProperty(fPfx + "_funk", "Unbekannt");
                    String typ = p.getProperty(fPfx + "_typ", "RTW");
                    Fahrzeug fzg = new Fahrzeug(funk, typ);
                    fzg.status = parseIntSafe(p.getProperty(fPfx + "_status"), 6);
                    fzg.kilometer = parseIntSafe(p.getProperty(fPfx + "_km"), 0);
                    fzg.naechsteInspektion = parseIntSafe(p.getProperty(fPfx + "_tuev"), 1000);
                    fzg.ausfallGrund = p.getProperty(fPfx + "_grund", "");
                    fzg.reparaturDauer = parseIntSafe(p.getProperty(fPfx + "_repDauer"), 0);
                    w.fuhrpark.add(fzg);
                }
                
                LogistikSimulator.sortiereFuhrpark(w); // NEU: Autos beim Laden ordnen!
                
                int wMatCount = parseIntSafe(p.getProperty("wache_" + i + "_matCount"), 0);
                for(int m = 0; m < wMatCount; m++) {
                    String n = p.getProperty("wache_" + i + "_mat_" + m + "_name", "Unbekannt");
                    int anz = parseIntSafe(p.getProperty("wache_" + i + "_mat_" + m + "_anz"), 0);
                    w.material.put(n, anz);
                }
                
                int upgCount = parseIntSafe(p.getProperty("wache_" + i + "_upgradeCount"), 0);
                for(int u = 0; u < upgCount; u++) {
                    String uid = p.getProperty("wache_" + i + "_upg_" + u + "_id", "");
                    String uname = p.getProperty("wache_" + i + "_upg_" + u + "_name", "");
                    String udesc = p.getProperty("wache_" + i + "_upg_" + u + "_desc", "");
                    int ucost = parseIntSafe(p.getProperty("wache_" + i + "_upg_" + u + "_cost"), 0);
                    w.upgrades.add(new WachenAusbau(uid, uname, udesc, ucost));
                }

                int persCount = parseIntSafe(p.getProperty("wache_" + i + "_persCount"), 0);
                for (int j = 0; j < persCount; j++) {
                    String prefix = "wache_" + i + "_pers_" + j;
                    Personal pers = new Personal(p.getProperty(prefix + "_name", "Unbekannt"), "Anwaerter");
                    pers.qualifikationen.clear();
                    String qualStr = p.getProperty(prefix + "_qual", "");
                    if(!qualStr.isEmpty()) {
                        for(String q : qualStr.split(",")) pers.qualifikationen.add(q);
                    }
                    pers.status = p.getProperty(prefix + "_status", "Frei");
                    pers.schichtenMonat = parseIntSafe(p.getProperty(prefix + "_schichten"), 0);
                    pers.urlaubStart = parseIntSafe(p.getProperty(prefix + "_uStart"), -1);
                    pers.urlaubEnd = parseIntSafe(p.getProperty(prefix + "_uEnd"), -1);
                    pers.krankBis = parseIntSafe(p.getProperty(prefix + "_kBis"), -1);
                    pers.zugewiesenesFahrzeug = p.getProperty(prefix + "_fzg", "Keines");
                    pers.geplanterStatus = p.getProperty(prefix + "_gStat", "Bereit");
                    pers.geplantesFahrzeug = p.getProperty(prefix + "_gFzg", "Keines");
                    
                    String[] akt = p.getProperty(prefix + "_planAkt", "").split(",");
                    if(akt.length == 31) pers.planAktuellerMonat = akt; else pers.planAktuellerMonat = new String[31];
                    
                    String[] nxt = p.getProperty(prefix + "_planNext", "").split(",");
                    if(nxt.length == 31) pers.planNaechsterMonat = nxt; else pers.planNaechsterMonat = new String[31];
                    
                    pers.eigenschaften.clear();
                    int eigCount = parseIntSafe(p.getProperty(prefix + "_eigCount"), 0);
                    for(int x = 0; x < eigCount; x++) {
                        String eName = p.getProperty(prefix + "_eig_" + x + "_n", "");
                        String eDesc = p.getProperty(prefix + "_eig_" + x + "_d", "");
                        String eTyp = p.getProperty(prefix + "_eig_" + x + "_t", "");
                        double eVal = parseDoubleSafe(p.getProperty(prefix + "_eig_" + x + "_e"), 1.0);
                        pers.eigenschaften.add(new MitarbeiterEigenschaft(eName, eDesc, eTyp, eVal));
                    }
                    w.personalPool.add(pers);
                }
                
                if (i == 0) {
                    boolean hasW = false, hasR = false;
                    for(WachenAusbau wa : w.upgrades) { if(wa.id.equals("werkstatt")) hasW = true; if(wa.id.equals("ruheraum")) hasR = true; }
                    if(legacyWerkstatt && !hasW) w.upgrades.add(new WachenAusbau("werkstatt", "Lokale Werkstatt", "Reparaturen 50% guenstiger", 10000));
                    if(legacyRuheraum && !hasR) w.upgrades.add(new WachenAusbau("ruheraum", "Lokaler Ruheraum", "Krankheitsrate sinkt", 15000));
                }
                
                LogistikSimulator.wachen.add(w);
            }
            
            if (LogistikSimulator.wachen.isEmpty()) {
                System.out.println("[DEBUG] Keine Wachen gefunden. Generiere Standard-Daten!");
                return false; 
            }

            if (LogistikSimulator.customMaterials.isEmpty()) {
                LogistikSimulator.customMaterials.add(new CustomMaterial("Verbandsmaterial", new ArrayList<>(java.util.Arrays.asList("RTW", "KTW", "HLF", "NEF")), 5, new ArrayList<>(), 500, 50, 10));
                LogistikSimulator.customMaterials.add(new CustomMaterial("Medikamente", new ArrayList<>(java.util.Arrays.asList("RTW", "NEF")), 3, new ArrayList<>(), 1000, 20, 5));
                LogistikSimulator.customMaterials.add(new CustomMaterial("Sauerstoff O²", new ArrayList<>(java.util.Arrays.asList("RTW", "KTW", "NEF", "HLF")), 1, new ArrayList<>(), 800, 10, 5));
                LogistikSimulator.customMaterials.add(new CustomMaterial("PA-Gerät", new ArrayList<>(java.util.Arrays.asList("HLF", "DLK")), 2, new ArrayList<>(), 1500, 10, 5));
                LogistikSimulator.customMaterials.add(new CustomMaterial("Schaummittel", new ArrayList<>(java.util.Arrays.asList("HLF")), 5, new ArrayList<>(), 2000, 5, 2));
                LogistikSimulator.customMaterials.add(new CustomMaterial("Ölbindemittel", new ArrayList<>(java.util.Arrays.asList("HLF")), 2, new ArrayList<>(), 400, 20, 10));
                
                for(CustomMaterial cm : LogistikSimulator.customMaterials) {
                    LogistikSimulator.hauptlager.put(cm.name, 100);
                    for(Wache w : LogistikSimulator.wachen) w.material.put(cm.name, 50);
                }
            }

            if (LogistikSimulator.vorlagenPool.isEmpty()) {
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("FW", "H1", "Tueröffnung", 0, 0, 0, 1, 0, 0, 0, 0, false, 0, "", 1));
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("RD", "R1", "Schnittverletzung", 1, 0, 0, 0, 0, 0, 0, 0, false, 0, "", 1));
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("FW", "F3", "BMA Einkaufszentrum", 0, 0, 0, 2, 1, 1, 0, 0, true, 30, "RTW", 3));
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("FW", "F2", "Wohnungsbrand", 1, 0, 0, 2, 1, 0, 0, 0, true, 50, "NEF", 2));
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("RD", "R2N1", "Verkehrsunfall (THL)", 2, 1, 0, 1, 0, 0, 0, 0, true, 20, "ELW & HLF", 4));
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("KTP", "KTP", "Krankentransport", 0, 0, 1, 0, 0, 0, 0, 0, false, 0, "", 1));
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage("RD", "R1", "Atemnot", 1, 0, 0, 0, 0, 0, 0, 0, true, 40, "NEF", 1));
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("!!! FEHLER BEIM LADEN: " + e.getMessage());
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Achtung: Fehler beim Laden des Spielstands!\n" + e.getMessage(), "Ladefehler", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}