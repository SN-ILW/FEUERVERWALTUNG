package neuesspiel;

import java.io.*;
import java.util.Properties;
import java.util.ArrayList;

public class SpeicherManager {

    public static void speichern(String dateiPfad) {
        try (FileOutputStream out = new FileOutputStream(dateiPfad)) {
            Properties p = new Properties();
            
            p.setProperty("budget", String.valueOf(LogistikSimulator.budget));
            p.setProperty("xp", String.valueOf(LogistikSimulator.xp));
            p.setProperty("level", String.valueOf(LogistikSimulator.level));
            p.setProperty("tag", String.valueOf(LogistikSimulator.tag));
            p.setProperty("inGameSekunden", String.valueOf(LogistikSimulator.inGameSekunden));
            p.setProperty("speed", String.valueOf(LogistikSimulator.speed));
            
            p.setProperty("aktuellerKredit", String.valueOf(LogistikSimulator.aktuellerKredit));
            p.setProperty("taeglicheKreditRate", String.valueOf(LogistikSimulator.taeglicheKreditRate));
            
            p.setProperty("techGrossabnehmer", String.valueOf(LogistikSimulator.techGrossabnehmer));
            p.setProperty("lehrerStufe", String.valueOf(LogistikSimulator.lehrerStufe));
            p.setProperty("calltakerStufe", String.valueOf(LogistikSimulator.calltakerStufe));
            p.setProperty("techKlinikCrivitz", String.valueOf(LogistikSimulator.techKlinikCrivitz));
            p.setProperty("techKlinikLeezen", String.valueOf(LogistikSimulator.techKlinikLeezen));
            p.setProperty("techKlinikHagenow", String.valueOf(LogistikSimulator.techKlinikHagenow));
            
            p.setProperty("cfgKrankentransport", String.valueOf(LogistikSimulator.cfgKrankentransport));
            p.setProperty("cfgBeschaedigung", String.valueOf(LogistikSimulator.cfgBeschaedigung));
            p.setProperty("cfgKrankheit", String.valueOf(LogistikSimulator.cfgKrankheit));
            p.setProperty("cfgAutoTransfer", String.valueOf(LogistikSimulator.cfgAutoTransfer));
            p.setProperty("cfgLogistikAktiv", String.valueOf(LogistikSimulator.cfgLogistikAktiv));
            
            p.setProperty("cfgSoundNotruf", String.valueOf(LogistikSimulator.cfgSoundNotruf));
            p.setProperty("cfgSoundStatus6", String.valueOf(LogistikSimulator.cfgSoundStatus6));
            p.setProperty("cfgSoundStatus7", String.valueOf(LogistikSimulator.cfgSoundStatus7));
            p.setProperty("volNotruf", String.valueOf(LogistikSimulator.volNotruf));
            p.setProperty("volStatus6", String.valueOf(LogistikSimulator.volStatus6));
            p.setProperty("volStatus7", String.valueOf(LogistikSimulator.volStatus7));

            if (LogistikSimulator.aktuelleMission != null) {
                p.setProperty("miss_titel", LogistikSimulator.aktuelleMission.titel);
                p.setProperty("miss_desc", LogistikSimulator.aktuelleMission.beschreibung);
                p.setProperty("miss_typ", LogistikSimulator.aktuelleMission.typ);
                p.setProperty("miss_ziel", String.valueOf(LogistikSimulator.aktuelleMission.zielWert));
                p.setProperty("miss_geld", String.valueOf(LogistikSimulator.aktuelleMission.belohnungGeld));
                p.setProperty("miss_xp", String.valueOf(LogistikSimulator.aktuelleMission.belohnungXp));
                p.setProperty("miss_fort", String.valueOf(LogistikSimulator.aktuelleMission.fortschritt));
                p.setProperty("miss_done", String.valueOf(LogistikSimulator.aktuelleMission.abgeschlossen));
            }

            p.setProperty("vertragsCount", String.valueOf(LogistikSimulator.aktiveVertraege.size()));
            for(int i = 0; i < LogistikSimulator.aktiveVertraege.size(); i++) {
                Vertrag v = LogistikSimulator.aktiveVertraege.get(i);
                p.setProperty("v_" + i + "_ag", v.auftraggeber);
                p.setProperty("v_" + i + "_desc", v.beschreibung);
                p.setProperty("v_" + i + "_art", v.zielEinsatzArt);
                p.setProperty("v_" + i + "_ziel", String.valueOf(v.zielMenge));
                p.setProperty("v_" + i + "_akt", String.valueOf(v.aktuelleMenge));
                p.setProperty("v_" + i + "_bel", String.valueOf(v.belohnungProTag));
                p.setProperty("v_" + i + "_strafe", String.valueOf(v.strafeBeiFehlschlag));
            }
            
            p.setProperty("vorlagenCount", String.valueOf(LogistikSimulator.vorlagenPool.size()));
            for(int i = 0; i < LogistikSimulator.vorlagenPool.size(); i++) {
                EinsatzVorlage v = LogistikSimulator.vorlagenPool.get(i);
                String prefix = "vorlage_" + i;
                p.setProperty(prefix + "_art", v.art);
                p.setProperty(prefix + "_stichwort", v.stichwort);
                p.setProperty(prefix + "_desc", v.beschreibung);
                p.setProperty(prefix + "_rtw", String.valueOf(v.reqRTW));
                p.setProperty(prefix + "_nef", String.valueOf(v.reqNEF));
                p.setProperty(prefix + "_ktw", String.valueOf(v.reqKTW));
                p.setProperty(prefix + "_hlf", String.valueOf(v.reqHLF));
                p.setProperty(prefix + "_dlk", String.valueOf(v.reqDLK));
                p.setProperty(prefix + "_elw", String.valueOf(v.reqELW));
                p.setProperty(prefix + "_tlf", String.valueOf(v.reqTLF));
                p.setProperty(prefix + "_mtw", String.valueOf(v.reqMTW));
                p.setProperty(prefix + "_hatNach", String.valueOf(v.hatNachforderung));
                p.setProperty(prefix + "_nachProz", String.valueOf(v.nachforderungProzent));
                p.setProperty(prefix + "_nachTyp", v.nachforderungTyp);
                p.setProperty(prefix + "_minLvl", String.valueOf(v.minLevel));
            }
            
            p.setProperty("matDefCount", String.valueOf(LogistikSimulator.customMaterials.size()));
            for(int i=0; i<LogistikSimulator.customMaterials.size(); i++) {
                CustomMaterial cm = LogistikSimulator.customMaterials.get(i);
                p.setProperty("matDef_" + i + "_name", cm.name);
                p.setProperty("matDef_" + i + "_fz", String.join(",", cm.fahrzeuge));
                p.setProperty("matDef_" + i + "_verb", String.valueOf(cm.maxVerbrauch));
                p.setProperty("matDef_" + i + "_preis", String.valueOf(cm.preis));
                p.setProperty("matDef_" + i + "_menge", String.valueOf(cm.bestellMenge));
                p.setProperty("matDef_" + i + "_warn", String.valueOf(cm.warnSchwelle));
            }
            
            p.setProperty("hlCount", String.valueOf(LogistikSimulator.hauptlager.size()));
            int hlIdx = 0;
            for (String mName : LogistikSimulator.hauptlager.keySet()) {
                p.setProperty("hl_" + hlIdx + "_name", mName);
                p.setProperty("hl_" + hlIdx + "_anz", String.valueOf(LogistikSimulator.hauptlager.get(mName)));
                hlIdx++;
            }

            p.setProperty("wachenCount", String.valueOf(LogistikSimulator.wachen.size()));
            for (int i = 0; i < LogistikSimulator.wachen.size(); i++) {
                Wache w = LogistikSimulator.wachen.get(i);
                p.setProperty("wache_" + i + "_name", w.name);
                p.setProperty("wache_" + i + "_kennung", w.kennung);
                
                p.setProperty("wache_" + i + "_matCount", String.valueOf(w.material.size()));
                int wMatIdx = 0;
                for (String mName : w.material.keySet()) {
                    p.setProperty("wache_" + i + "_mat_" + wMatIdx + "_name", mName);
                    p.setProperty("wache_" + i + "_mat_" + wMatIdx + "_anz", String.valueOf(w.material.get(mName)));
                    wMatIdx++;
                }
                
                p.setProperty("wache_" + i + "_upgradeCount", String.valueOf(w.upgrades.size()));
                for(int u = 0; u < w.upgrades.size(); u++) {
                    WachenAusbau wa = w.upgrades.get(u);
                    p.setProperty("wache_" + i + "_upg_" + u + "_id", wa.id);
                    p.setProperty("wache_" + i + "_upg_" + u + "_name", wa.name);
                    p.setProperty("wache_" + i + "_upg_" + u + "_desc", wa.beschreibung);
                    p.setProperty("wache_" + i + "_upg_" + u + "_cost", String.valueOf(wa.kosten));
                }

                p.setProperty("wache_" + i + "_persCount", String.valueOf(w.personalPool.size()));
                for (int j = 0; j < w.personalPool.size(); j++) {
                    Personal pers = w.personalPool.get(j);
                    String prefix = "wache_" + i + "_pers_" + j;
                    p.setProperty(prefix + "_name", pers.name);
                    p.setProperty(prefix + "_status", pers.status);
                    p.setProperty(prefix + "_qual", String.join(",", pers.qualifikationen));
                    p.setProperty(prefix + "_schichten", String.valueOf(pers.schichtenMonat));
                    p.setProperty(prefix + "_uStart", String.valueOf(pers.urlaubStart));
                    p.setProperty(prefix + "_uEnd", String.valueOf(pers.urlaubEnd));
                    p.setProperty(prefix + "_kBis", String.valueOf(pers.krankBis));
                    p.setProperty(prefix + "_fzg", pers.zugewiesenesFahrzeug);
                    p.setProperty(prefix + "_gStat", pers.geplanterStatus);
                    p.setProperty(prefix + "_gFzg", pers.geplantesFahrzeug);
                    
                    p.setProperty(prefix + "_planAkt", String.join(",", pers.planAktuellerMonat));
                    p.setProperty(prefix + "_planNext", String.join(",", pers.planNaechsterMonat));
                    
                    p.setProperty(prefix + "_eigCount", String.valueOf(pers.eigenschaften.size()));
                    for(int x = 0; x < pers.eigenschaften.size(); x++) {
                        MitarbeiterEigenschaft eig = pers.eigenschaften.get(x);
                        p.setProperty(prefix + "_eig_" + x + "_n", eig.name);
                        p.setProperty(prefix + "_eig_" + x + "_d", eig.beschreibung);
                        p.setProperty(prefix + "_eig_" + x + "_t", eig.typ);
                        p.setProperty(prefix + "_eig_" + x + "_e", String.valueOf(eig.effektWert));
                    }
                }
            }

            p.store(out, "Logistik Simulator Savegame");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean laden(String dateiPfad) {
        File file = new File(dateiPfad);
        if (!file.exists()) return false;

        try (FileInputStream in = new FileInputStream(file)) {
            Properties p = new Properties();
            p.load(in);

            LogistikSimulator.wachen.clear();
            LogistikSimulator.aktiveVertraege.clear();
            LogistikSimulator.vorlagenPool.clear();
            LogistikSimulator.customMaterials.clear();
            LogistikSimulator.hauptlager.clear();
            
            LogistikSimulator.budget = Integer.parseInt(p.getProperty("budget", "25000"));
            LogistikSimulator.xp = Integer.parseInt(p.getProperty("xp", "0"));
            LogistikSimulator.level = Integer.parseInt(p.getProperty("level", "1"));
            LogistikSimulator.tag = Integer.parseInt(p.getProperty("tag", "1"));
            LogistikSimulator.inGameSekunden = Integer.parseInt(p.getProperty("inGameSekunden", "25200"));
            LogistikSimulator.speed = Integer.parseInt(p.getProperty("speed", "1"));
            
            LogistikSimulator.aktuellerKredit = Integer.parseInt(p.getProperty("aktuellerKredit", "0"));
            LogistikSimulator.taeglicheKreditRate = Integer.parseInt(p.getProperty("taeglicheKreditRate", "0"));

            LogistikSimulator.techGrossabnehmer = Boolean.parseBoolean(p.getProperty("techGrossabnehmer", "false"));
            LogistikSimulator.lehrerStufe = Integer.parseInt(p.getProperty("lehrerStufe", "0"));
            LogistikSimulator.calltakerStufe = Integer.parseInt(p.getProperty("calltakerStufe", "0"));
            LogistikSimulator.techKlinikCrivitz = Boolean.parseBoolean(p.getProperty("techKlinikCrivitz", "false"));
            LogistikSimulator.techKlinikLeezen = Boolean.parseBoolean(p.getProperty("techKlinikLeezen", "false"));
            LogistikSimulator.techKlinikHagenow = Boolean.parseBoolean(p.getProperty("techKlinikHagenow", "false"));

            boolean legacyWerkstatt = Boolean.parseBoolean(p.getProperty("techWerkstatt", "false"));
            boolean legacyRuheraum = Boolean.parseBoolean(p.getProperty("techRuheraum", "false"));

            LogistikSimulator.cfgKrankentransport = Boolean.parseBoolean(p.getProperty("cfgKrankentransport", "true"));
            LogistikSimulator.cfgBeschaedigung = Boolean.parseBoolean(p.getProperty("cfgBeschaedigung", "true"));
            LogistikSimulator.cfgKrankheit = Boolean.parseBoolean(p.getProperty("cfgKrankheit", "true"));
            LogistikSimulator.cfgAutoTransfer = Boolean.parseBoolean(p.getProperty("cfgAutoTransfer", "false"));
            LogistikSimulator.cfgLogistikAktiv = Boolean.parseBoolean(p.getProperty("cfgLogistikAktiv", "true"));
            
            if (p.containsKey("miss_titel")) {
                LogistikSimulator.aktuelleMission = new TagesMission(p.getProperty("miss_titel"), p.getProperty("miss_desc"), p.getProperty("miss_typ"), Integer.parseInt(p.getProperty("miss_ziel", "1")), Integer.parseInt(p.getProperty("miss_geld", "0")), Integer.parseInt(p.getProperty("miss_xp", "0")));
                LogistikSimulator.aktuelleMission.fortschritt = Integer.parseInt(p.getProperty("miss_fort", "0"));
                LogistikSimulator.aktuelleMission.abgeschlossen = Boolean.parseBoolean(p.getProperty("miss_done", "false"));
            }

            int vCount = Integer.parseInt(p.getProperty("vertragsCount", "0"));
            for(int i = 0; i < vCount; i++) {
                Vertrag v = new Vertrag(p.getProperty("v_" + i + "_ag"), p.getProperty("v_" + i + "_desc"), p.getProperty("v_" + i + "_art"), Integer.parseInt(p.getProperty("v_" + i + "_ziel", "1")), Integer.parseInt(p.getProperty("v_" + i + "_bel", "0")), Integer.parseInt(p.getProperty("v_" + i + "_strafe", "0")));
                v.aktuelleMenge = Integer.parseInt(p.getProperty("v_" + i + "_akt", "0"));
                LogistikSimulator.aktiveVertraege.add(v);
            }
            
            if (p.containsKey("vorlagenCount")) {
                int vorlagenCount = Integer.parseInt(p.getProperty("vorlagenCount"));
                for(int i = 0; i < vorlagenCount; i++) {
                    String prefix = "vorlage_" + i;
                    EinsatzVorlage v = new EinsatzVorlage(
                        p.getProperty(prefix + "_art"),
                        p.getProperty(prefix + "_stichwort"),
                        p.getProperty(prefix + "_desc"),
                        Integer.parseInt(p.getProperty(prefix + "_rtw", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_nef", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_ktw", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_hlf", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_dlk", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_elw", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_tlf", "0")),
                        Integer.parseInt(p.getProperty(prefix + "_mtw", "0")),
                        Boolean.parseBoolean(p.getProperty(prefix + "_hatNach", "false")),
                        Integer.parseInt(p.getProperty(prefix + "_nachProz", "0")),
                        p.getProperty(prefix + "_nachTyp", ""),
                        Integer.parseInt(p.getProperty(prefix + "_minLvl", "1"))
                    );
                    LogistikSimulator.vorlagenPool.add(v);
                }
            }

            int matDefCount = Integer.parseInt(p.getProperty("matDefCount", "0"));
            for(int i=0; i<matDefCount; i++) {
                String n = p.getProperty("matDef_" + i + "_name");
                String fzStr = p.getProperty("matDef_" + i + "_fz", "");
                ArrayList<String> fzList = new ArrayList<>();
                if(!fzStr.isEmpty()) for(String s : fzStr.split(",")) fzList.add(s);
                int verb = Integer.parseInt(p.getProperty("matDef_" + i + "_verb", "1"));
                int preis = Integer.parseInt(p.getProperty("matDef_" + i + "_preis", "100"));
                int menge = Integer.parseInt(p.getProperty("matDef_" + i + "_menge", "10"));
                int warn = Integer.parseInt(p.getProperty("matDef_" + i + "_warn", "5"));
                LogistikSimulator.customMaterials.add(new CustomMaterial(n, fzList, verb, new ArrayList<>(), preis, menge, warn));
            }
            
            int hlCount = Integer.parseInt(p.getProperty("hlCount", "0"));
            for(int i=0; i<hlCount; i++) {
                String n = p.getProperty("hl_" + i + "_name");
                int anz = Integer.parseInt(p.getProperty("hl_" + i + "_anz", "0"));
                LogistikSimulator.hauptlager.put(n, anz);
            }

            int wachenCount = Integer.parseInt(p.getProperty("wachenCount", "0"));
            for (int i = 0; i < wachenCount; i++) {
                Wache w = new Wache(p.getProperty("wache_" + i + "_name"), p.getProperty("wache_" + i + "_kennung"));
                
                int wMatCount = Integer.parseInt(p.getProperty("wache_" + i + "_matCount", "0"));
                for(int m = 0; m < wMatCount; m++) {
                    String n = p.getProperty("wache_" + i + "_mat_" + m + "_name");
                    int anz = Integer.parseInt(p.getProperty("wache_" + i + "_mat_" + m + "_anz", "0"));
                    w.material.put(n, anz);
                }
                
                int upgCount = Integer.parseInt(p.getProperty("wache_" + i + "_upgradeCount", "0"));
                for(int u = 0; u < upgCount; u++) {
                    String uid = p.getProperty("wache_" + i + "_upg_" + u + "_id");
                    String uname = p.getProperty("wache_" + i + "_upg_" + u + "_name");
                    String udesc = p.getProperty("wache_" + i + "_upg_" + u + "_desc");
                    int ucost = Integer.parseInt(p.getProperty("wache_" + i + "_upg_" + u + "_cost", "0"));
                    w.upgrades.add(new WachenAusbau(uid, uname, udesc, ucost));
                }

                int persCount = Integer.parseInt(p.getProperty("wache_" + i + "_persCount", "0"));
                for (int j = 0; j < persCount; j++) {
                    String prefix = "wache_" + i + "_pers_" + j;
                    Personal pers = new Personal(p.getProperty(prefix + "_name"), "Anwaerter");
                    pers.qualifikationen.clear();
                    String qualStr = p.getProperty(prefix + "_qual");
                    if(qualStr != null && !qualStr.isEmpty()) {
                        for(String q : qualStr.split(",")) pers.qualifikationen.add(q);
                    }
                    pers.status = p.getProperty(prefix + "_status");
                    pers.schichtenMonat = Integer.parseInt(p.getProperty(prefix + "_schichten", "0"));
                    pers.urlaubStart = Integer.parseInt(p.getProperty(prefix + "_uStart", "-1"));
                    pers.urlaubEnd = Integer.parseInt(p.getProperty(prefix + "_uEnd", "-1"));
                    pers.krankBis = Integer.parseInt(p.getProperty(prefix + "_kBis", "-1"));
                    pers.zugewiesenesFahrzeug = p.getProperty(prefix + "_fzg", "Keines");
                    pers.geplanterStatus = p.getProperty(prefix + "_gStat", "Bereit");
                    pers.geplantesFahrzeug = p.getProperty(prefix + "_gFzg", "Keines");
                    
                    String[] akt = p.getProperty(prefix + "_planAkt", "").split(",");
                    if(akt.length == 31) pers.planAktuellerMonat = akt;
                    String[] nxt = p.getProperty(prefix + "_planNext", "").split(",");
                    if(nxt.length == 31) pers.planNaechsterMonat = nxt;
                    
                    pers.eigenschaften.clear();
                    int eigCount = Integer.parseInt(p.getProperty(prefix + "_eigCount", "0"));
                    for(int x = 0; x < eigCount; x++) {
                        String eName = p.getProperty(prefix + "_eig_" + x + "_n");
                        String eDesc = p.getProperty(prefix + "_eig_" + x + "_d");
                        String eTyp = p.getProperty(prefix + "_eig_" + x + "_t");
                        double eVal = Double.parseDouble(p.getProperty(prefix + "_eig_" + x + "_e", "1.0"));
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
            
            // --- RETTUNGSSCHIRM FUER ALTE SPIELSTAENDE ---
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
            e.printStackTrace();
            return false;
        }
    }
}