package neuesspiel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class SpeicherManager {
    public static boolean speichern(String dateiname) {
        try {
            Properties p = new Properties();
            p.setProperty("budget", String.valueOf(LogistikSimulator.budget));
            p.setProperty("tag", String.valueOf(LogistikSimulator.tag));
            p.setProperty("level", String.valueOf(LogistikSimulator.level));
            p.setProperty("xp", String.valueOf(LogistikSimulator.xp));
            p.setProperty("sekunden", String.valueOf(LogistikSimulator.inGameSekunden));
            p.setProperty("abgelehnt_heute", String.valueOf(LogistikSimulator.abgelehnteEinsaetzeHeute)); 
            
            p.setProperty("aktuellerKredit", String.valueOf(LogistikSimulator.aktuellerKredit));
            p.setProperty("taeglicheKreditRate", String.valueOf(LogistikSimulator.taeglicheKreditRate));
            
            p.setProperty("cfgSoundNotruf", String.valueOf(LogistikSimulator.cfgSoundNotruf));
            p.setProperty("cfgSoundStatus6", String.valueOf(LogistikSimulator.cfgSoundStatus6));
            p.setProperty("cfgSoundStatus7", String.valueOf(LogistikSimulator.cfgSoundStatus7));
            
            p.setProperty("volNotruf", String.valueOf(LogistikSimulator.volNotruf));
            p.setProperty("volStatus6", String.valueOf(LogistikSimulator.volStatus6));
            p.setProperty("volStatus7", String.valueOf(LogistikSimulator.volStatus7));
            
            p.setProperty("cfg_ktp", String.valueOf(LogistikSimulator.cfgKrankentransport));
            p.setProperty("cfg_dmg", String.valueOf(LogistikSimulator.cfgBeschaedigung));
            p.setProperty("cfg_sick", String.valueOf(LogistikSimulator.cfgKrankheit));
            p.setProperty("cfg_auto", String.valueOf(LogistikSimulator.cfgAutoTransfer));

            p.setProperty("tech_ws", String.valueOf(LogistikSimulator.techWerkstatt));
            p.setProperty("tech_rh", String.valueOf(LogistikSimulator.techRuheraum));
            p.setProperty("tech_ga", String.valueOf(LogistikSimulator.techGrossabnehmer));
            p.setProperty("tech_lehrer", String.valueOf(LogistikSimulator.lehrerStufe)); 
            
            p.setProperty("tech_calltaker_stufe", String.valueOf(LogistikSimulator.calltakerStufe)); 

            p.setProperty("tech_klinik_crivitz", String.valueOf(LogistikSimulator.techKlinikCrivitz)); 
            p.setProperty("tech_klinik_leezen", String.valueOf(LogistikSimulator.techKlinikLeezen)); 
            p.setProperty("tech_klinik_hagenow", String.valueOf(LogistikSimulator.techKlinikHagenow)); 

            p.setProperty("stat_count", String.valueOf(LogistikSimulator.tagesStatistik.size()));
            for(int i = 0; i < LogistikSimulator.tagesStatistik.size(); i++) {
                Einsatz ein = LogistikSimulator.tagesStatistik.get(i);
                p.setProperty("stat_" + i + "_desc", ein.beschreibung);
                p.setProperty("stat_" + i + "_xp", String.valueOf(ein.xpBelohnung));
            }

            p.setProperty("mat_count", String.valueOf(LogistikSimulator.customMaterials.size()));
            for(int i = 0; i < LogistikSimulator.customMaterials.size(); i++) {
                CustomMaterial cm = LogistikSimulator.customMaterials.get(i);
                p.setProperty("mat_" + i + "_name", cm.name);
                p.setProperty("mat_" + i + "_fz", String.join(",", cm.fahrzeuge));
                p.setProperty("mat_" + i + "_sw", String.join(",", cm.einsatzStichworte));
                p.setProperty("mat_" + i + "_max", String.valueOf(cm.maxVerbrauch));
                p.setProperty("mat_" + i + "_preis", String.valueOf(cm.preis));
                p.setProperty("mat_" + i + "_menge", String.valueOf(cm.bestellMenge));
                p.setProperty("mat_" + i + "_warn", String.valueOf(cm.warnSchwelle));
                p.setProperty("lager_" + cm.name, String.valueOf(LogistikSimulator.hauptlager.getOrDefault(cm.name, 0)));
            }

            p.setProperty("vor_count", String.valueOf(LogistikSimulator.vorlagenPool.size()));
            for(int i = 0; i < LogistikSimulator.vorlagenPool.size(); i++) {
                EinsatzVorlage v = LogistikSimulator.vorlagenPool.get(i);
                p.setProperty("vor_" + i + "_art", v.art);
                p.setProperty("vor_" + i + "_sw", v.stichwort);
                p.setProperty("vor_" + i + "_desc", v.beschreibung);
                p.setProperty("vor_" + i + "_rtw", String.valueOf(v.reqRTW));
                p.setProperty("vor_" + i + "_nef", String.valueOf(v.reqNEF));
                p.setProperty("vor_" + i + "_ktw", String.valueOf(v.reqKTW));
                p.setProperty("vor_" + i + "_hlf", String.valueOf(v.reqHLF));
                p.setProperty("vor_" + i + "_dlk", String.valueOf(v.reqDLK));
                p.setProperty("vor_" + i + "_elw", String.valueOf(v.reqELW));
                p.setProperty("vor_" + i + "_hN", String.valueOf(v.hatNachforderung));
                p.setProperty("vor_" + i + "_nP", String.valueOf(v.nachforderungProzent));
                p.setProperty("vor_" + i + "_nT", v.nachforderungTyp);
                p.setProperty("vor_" + i + "_lvl", String.valueOf(v.minLevel));
            }

            p.setProperty("wachen_count", String.valueOf(LogistikSimulator.wachen.size()));
            for(int wIdx = 0; wIdx < LogistikSimulator.wachen.size(); wIdx++) {
                Wache w = LogistikSimulator.wachen.get(wIdx);
                p.setProperty("wache_" + wIdx + "_name", w.name);
                p.setProperty("wache_" + wIdx + "_kennung", w.kennung);
                
                p.setProperty("wache_" + wIdx + "_c_hlf", String.valueOf(w.fahrzeugCounter.getOrDefault("HLF", 0)));
                p.setProperty("wache_" + wIdx + "_c_rtw", String.valueOf(w.fahrzeugCounter.getOrDefault("RTW", 0)));
                p.setProperty("wache_" + wIdx + "_c_elw", String.valueOf(w.fahrzeugCounter.getOrDefault("ELW", 0)));
                p.setProperty("wache_" + wIdx + "_c_dlk", String.valueOf(w.fahrzeugCounter.getOrDefault("DLK", 0)));
                p.setProperty("wache_" + wIdx + "_c_nef", String.valueOf(w.fahrzeugCounter.getOrDefault("NEF", 0)));
                p.setProperty("wache_" + wIdx + "_c_ktw", String.valueOf(w.fahrzeugCounter.getOrDefault("KTW", 0)));
                
                for(CustomMaterial cm : LogistikSimulator.customMaterials) {
                    p.setProperty("wache_" + wIdx + "_mat_" + cm.name, String.valueOf(w.material.getOrDefault(cm.name, 0)));
                }

                p.setProperty("wache_" + wIdx + "_fz_count", String.valueOf(w.fuhrpark.size()));
                for(int fIdx = 0; fIdx < w.fuhrpark.size(); fIdx++) {
                    Fahrzeug f = w.fuhrpark.get(fIdx);
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_kennung", f.funkrufname);
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_typ", f.typ);
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_status", String.valueOf(f.status));
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_ausfall", f.ausfallGrund != null ? f.ausfallGrund : "");
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_rep", String.valueOf(f.reparaturDauer));
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_anf", String.valueOf(f.anfahrtsZeit));
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_origAnf", String.valueOf(f.originalAnfahrt)); 
                    
                    // NEU: Kilometer und Inspektion speichern
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_kilometer", String.valueOf(f.kilometer));
                    p.setProperty("wache_" + wIdx + "_fz_" + fIdx + "_naechsteInspektion", String.valueOf(f.naechsteInspektion));
                }

                p.setProperty("wache_" + wIdx + "_pers_count", String.valueOf(w.personalPool.size()));
                for(int pIdx = 0; pIdx < w.personalPool.size(); pIdx++) {
                    Personal pers = w.personalPool.get(pIdx);
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_name", pers.name);
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_qual", String.join(",", pers.qualifikationen));
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_stat", pers.status);
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_planStat", pers.geplanterStatus);
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_fz", pers.zugewiesenesFahrzeug);
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_plan", pers.geplantesFahrzeug);
                    
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_kBis", String.valueOf(pers.krankBis));
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_uStart", String.valueOf(pers.urlaubStart));
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_uEnd", String.valueOf(pers.urlaubEnd));
                    
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_schichten", String.valueOf(pers.schichtenMonat));
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_lDauer", String.valueOf(pers.lehrgangDauerSec));
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_lThema", pers.lehrgangThema);
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_praef", String.valueOf(pers.praeferenzGesendet));
                    
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_planAkt", String.join(",", pers.planAktuellerMonat));
                    p.setProperty("wache_" + wIdx + "_pers_" + pIdx + "_planNaechst", String.join(",", pers.planNaechsterMonat));
                }
            }

            p.setProperty("mail_count", String.valueOf(LogistikSimulator.postfach.size()));
            for(int i = 0; i < LogistikSimulator.postfach.size(); i++) {
                Email m = LogistikSimulator.postfach.get(i);
                p.setProperty("mail_"+i+"_abs", m.absender);
                p.setProperty("mail_"+i+"_bet", m.betreff);
                p.setProperty("mail_"+i+"_txt", m.text.replace("\n", "||")); 
                p.setProperty("mail_"+i+"_typ", m.typ);
                p.setProperty("mail_"+i+"_gel", String.valueOf(m.gelesen));
                p.setProperty("mail_"+i+"_pName", m.person != null ? m.person.name : "");
                p.setProperty("mail_"+i+"_start", String.valueOf(m.startTag));
                p.setProperty("mail_"+i+"_end", String.valueOf(m.endTag));
            }

            FileOutputStream out = new FileOutputStream(dateiname);
            p.store(out, "BOS Simulator Savegame");
            out.close();
            return true;
        } catch (Exception e) { return false; }
    }

    public static boolean laden(String dateiname) {
        try {
            Properties p = new Properties();
            FileInputStream in = new FileInputStream(dateiname);
            p.load(in);
            in.close();

            LogistikSimulator.budget = Integer.parseInt(p.getProperty("budget", "25000"));
            LogistikSimulator.tag = Integer.parseInt(p.getProperty("tag", "1"));
            LogistikSimulator.level = Integer.parseInt(p.getProperty("level", "1"));
            LogistikSimulator.xp = Integer.parseInt(p.getProperty("xp", "0"));
            LogistikSimulator.inGameSekunden = Integer.parseInt(p.getProperty("sekunden", String.valueOf(7 * 3600)));
            LogistikSimulator.abgelehnteEinsaetzeHeute = Integer.parseInt(p.getProperty("abgelehnt_heute", "0")); 

            LogistikSimulator.cfgKrankentransport = Boolean.parseBoolean(p.getProperty("cfg_ktp", "true"));
            LogistikSimulator.cfgBeschaedigung = Boolean.parseBoolean(p.getProperty("cfg_dmg", "true"));
            LogistikSimulator.cfgKrankheit = Boolean.parseBoolean(p.getProperty("cfg_sick", "true"));
            LogistikSimulator.cfgAutoTransfer = Boolean.parseBoolean(p.getProperty("cfg_auto", "false"));
            
            LogistikSimulator.cfgSoundNotruf = Boolean.parseBoolean(p.getProperty("cfgSoundNotruf", "true"));
            LogistikSimulator.cfgSoundStatus6 = Boolean.parseBoolean(p.getProperty("cfgSoundStatus6", "true"));
            LogistikSimulator.cfgSoundStatus7 = Boolean.parseBoolean(p.getProperty("cfgSoundStatus7", "true"));
            
            LogistikSimulator.aktuellerKredit = Integer.parseInt(p.getProperty("aktuellerKredit", "0"));
            LogistikSimulator.taeglicheKreditRate = Integer.parseInt(p.getProperty("taeglicheKreditRate", "0"));
            
            LogistikSimulator.volNotruf = Integer.parseInt(p.getProperty("volNotruf", "100"));
            LogistikSimulator.volStatus6 = Integer.parseInt(p.getProperty("volStatus6", "100"));
            LogistikSimulator.volStatus7 = Integer.parseInt(p.getProperty("volStatus7", "100"));
            
            LogistikSimulator.techWerkstatt = Boolean.parseBoolean(p.getProperty("tech_ws", "false"));
            LogistikSimulator.techRuheraum = Boolean.parseBoolean(p.getProperty("tech_rh", "false"));
            LogistikSimulator.techGrossabnehmer = Boolean.parseBoolean(p.getProperty("tech_ga", "false"));
            LogistikSimulator.lehrerStufe = Integer.parseInt(p.getProperty("tech_lehrer", "0"));
            
            if (p.containsKey("tech_calltaker_stufe")) {
                LogistikSimulator.calltakerStufe = Integer.parseInt(p.getProperty("tech_calltaker_stufe", "0"));
            } else {
                boolean oldBasic = Boolean.parseBoolean(p.getProperty("tech_calltaker", "false")); 
                boolean oldErw = Boolean.parseBoolean(p.getProperty("tech_calltaker_erw", "false")); 
                if(oldErw) LogistikSimulator.calltakerStufe = 2;
                else if(oldBasic) LogistikSimulator.calltakerStufe = 1;
                else LogistikSimulator.calltakerStufe = 0;
            }

            LogistikSimulator.techKlinikCrivitz = Boolean.parseBoolean(p.getProperty("tech_klinik_crivitz", "false")); 
            LogistikSimulator.techKlinikLeezen = Boolean.parseBoolean(p.getProperty("tech_klinik_leezen", "false")); 
            LogistikSimulator.techKlinikHagenow = Boolean.parseBoolean(p.getProperty("tech_klinik_hagenow", "false")); 

            LogistikSimulator.vorlagenPool.clear();
            int vorCount = Integer.parseInt(p.getProperty("vor_count", "0"));
            for(int i = 0; i < vorCount; i++) {
                LogistikSimulator.vorlagenPool.add(new EinsatzVorlage(
                    p.getProperty("vor_" + i + "_art", "FW"), p.getProperty("vor_" + i + "_sw", "X"), p.getProperty("vor_" + i + "_desc", ""),
                    Integer.parseInt(p.getProperty("vor_" + i + "_rtw", "0")), Integer.parseInt(p.getProperty("vor_" + i + "_nef", "0")), Integer.parseInt(p.getProperty("vor_" + i + "_ktw", "0")),
                    Integer.parseInt(p.getProperty("vor_" + i + "_hlf", "0")), Integer.parseInt(p.getProperty("vor_" + i + "_dlk", "0")), Integer.parseInt(p.getProperty("vor_" + i + "_elw", "0")),
                    Boolean.parseBoolean(p.getProperty("vor_" + i + "_hN", "false")), Integer.parseInt(p.getProperty("vor_" + i + "_nP", "0")), p.getProperty("vor_" + i + "_nT", ""),
                    Integer.parseInt(p.getProperty("vor_" + i + "_lvl", "1"))
                ));
            }

            LogistikSimulator.tagesStatistik.clear();
            int statCount = Integer.parseInt(p.getProperty("stat_count", "0"));
            for(int i = 0; i < statCount; i++) {
                String desc = p.getProperty("stat_" + i + "_desc", "Unbekannter Einsatz");
                int exp = Integer.parseInt(p.getProperty("stat_" + i + "_xp", "0"));
                
                EinsatzVorlage foundV = null;
                for(EinsatzVorlage v : LogistikSimulator.vorlagenPool) {
                    if(v.beschreibung.equals(desc)) { foundV = v; break; }
                }
                if(foundV == null) foundV = new EinsatzVorlage("FW", "X", desc, 0,0,0,0,0,0, false, 0, "", 1);
                
                Einsatz ein = new Einsatz(foundV, "00:00");
                ein.beschreibung = desc;
                ein.xpBelohnung = exp;
                LogistikSimulator.tagesStatistik.add(ein);
            }

            LogistikSimulator.customMaterials.clear();
            LogistikSimulator.hauptlager.clear();
            int matCount = Integer.parseInt(p.getProperty("mat_count", "0"));
            for(int i = 0; i < matCount; i++) {
                String name = p.getProperty("mat_" + i + "_name", "Unknown");
                String fzStr = p.getProperty("mat_" + i + "_fz", "");
                ArrayList<String> fz = fzStr.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(fzStr.split(",")));
                String swStr = p.getProperty("mat_" + i + "_sw", "");
                ArrayList<String> sw = swStr.isEmpty() ? new ArrayList<>() : new ArrayList<>(Arrays.asList(swStr.split(",")));
                int max = Integer.parseInt(p.getProperty("mat_" + i + "_max", "0"));
                int preis = Integer.parseInt(p.getProperty("mat_" + i + "_preis", "500"));
                int menge = Integer.parseInt(p.getProperty("mat_" + i + "_menge", "10"));
                int warn = Integer.parseInt(p.getProperty("mat_" + i + "_warn", "10")); 
                
                LogistikSimulator.customMaterials.add(new CustomMaterial(name, fz, max, sw, preis, menge, warn));
                LogistikSimulator.hauptlager.put(name, Integer.parseInt(p.getProperty("lager_" + name, "0")));
            }

            LogistikSimulator.wachen.clear();
            int wachenCount = Integer.parseInt(p.getProperty("wachen_count", "0"));
            for(int wIdx = 0; wIdx < wachenCount; wIdx++) {
                String wName = p.getProperty("wache_" + wIdx + "_name", "Wache");
                String wKennung = p.getProperty("wache_" + wIdx + "_kennung", "00");
                Wache w = new Wache(wName, wKennung);
                
                w.fahrzeugCounter.put("HLF", Integer.parseInt(p.getProperty("wache_" + wIdx + "_c_hlf", "0")));
                w.fahrzeugCounter.put("RTW", Integer.parseInt(p.getProperty("wache_" + wIdx + "_c_rtw", "0")));
                w.fahrzeugCounter.put("ELW", Integer.parseInt(p.getProperty("wache_" + wIdx + "_c_elw", "0")));
                w.fahrzeugCounter.put("DLK", Integer.parseInt(p.getProperty("wache_" + wIdx + "_c_dlk", "0")));
                w.fahrzeugCounter.put("NEF", Integer.parseInt(p.getProperty("wache_" + wIdx + "_c_nef", "0")));
                w.fahrzeugCounter.put("KTW", Integer.parseInt(p.getProperty("wache_" + wIdx + "_c_ktw", "0")));

                for(CustomMaterial cm : LogistikSimulator.customMaterials) {
                    w.material.put(cm.name, Integer.parseInt(p.getProperty("wache_" + wIdx + "_mat_" + cm.name, "0")));
                }

                int fzCount = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_count", "0"));
                for(int fIdx = 0; fIdx < fzCount; fIdx++) {
                    String kennung = p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_kennung", "Unbekannt");
                    String typ = p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_typ", "RTW");
                    Fahrzeug f = new Fahrzeug(kennung, typ);
                    f.status = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_status", "1"));
                    f.ausfallGrund = p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_ausfall", "");
                    f.reparaturDauer = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_rep", "0"));
                    f.anfahrtsZeit = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_anf", "0"));
                    f.originalAnfahrt = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_origAnf", "0")); 
                    
                    // NEU: Kilometer und Inspektion laden
                    f.kilometer = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_kilometer", "0"));
                    f.naechsteInspektion = Integer.parseInt(p.getProperty("wache_" + wIdx + "_fz_" + fIdx + "_naechsteInspektion", "1000"));
                    
                    w.fuhrpark.add(f);
                }

                int persCount = Integer.parseInt(p.getProperty("wache_" + wIdx + "_pers_count", "0"));
                for(int pIdx = 0; pIdx < persCount; pIdx++) {
                    Personal pers = new Personal(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_name", "Unknown"), "");
                    pers.qualifikationen.clear();
                    String qualString = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_qual", "TM");
                    if (!qualString.isEmpty()) {
                        String[] parts = qualString.split(",");
                        for(String part : parts) {
                            pers.qualifikationen.add(part.trim());
                        }
                    }
                    pers.status = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_stat", "Bereit");
                    pers.geplanterStatus = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_planStat", "Bereit");
                    pers.zugewiesenesFahrzeug = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_fz", "Keines");
                    pers.geplantesFahrzeug = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_plan", pers.zugewiesenesFahrzeug);
                    
                    pers.krankBis = Integer.parseInt(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_kBis", "-1"));
                    pers.urlaubStart = Integer.parseInt(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_uStart", "-1"));
                    pers.urlaubEnd = Integer.parseInt(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_uEnd", "-1"));
                    
                    pers.schichtenMonat = Integer.parseInt(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_schichten", "0"));
                    pers.lehrgangDauerSec = Integer.parseInt(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_lDauer", "0"));
                pers.lehrgangThema = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_lThema", "");
                pers.praeferenzGesendet = Boolean.parseBoolean(p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_praef", "false"));
                
                // Laden der Monats-Plaene (31 Tage x 2)
                String pAkt = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_planAkt", "");
                if (!pAkt.isEmpty()) {
                    String[] parts = pAkt.split(",");
                    for(int i = 0; i < Math.min(parts.length, 31); i++) pers.planAktuellerMonat[i] = parts[i];
                }
                
                String pNaechst = p.getProperty("wache_" + wIdx + "_pers_" + pIdx + "_planNaechst", "");
                if (!pNaechst.isEmpty()) {
                    String[] parts = pNaechst.split(",");
                    for(int i = 0; i < Math.min(parts.length, 31); i++) pers.planNaechsterMonat[i] = parts[i];
                }
                
                w.personalPool.add(pers);
            }
            LogistikSimulator.wachen.add(w);
            }

            LogistikSimulator.postfach.clear();
            int mCount = Integer.parseInt(p.getProperty("mail_count", "0"));
            for(int i = 0; i < mCount; i++) {
                String abs = p.getProperty("mail_"+i+"_abs", "");
                String bet = p.getProperty("mail_"+i+"_bet", "");
                String txt = p.getProperty("mail_"+i+"_txt", "").replace("||", "\n"); 
                String typ = p.getProperty("mail_"+i+"_typ", "");
                boolean gel = Boolean.parseBoolean(p.getProperty("mail_"+i+"_gel", "false"));
                String pName = p.getProperty("mail_"+i+"_pName", "");
                int start = Integer.parseInt(p.getProperty("mail_"+i+"_start", "-1"));
                int end = Integer.parseInt(p.getProperty("mail_"+i+"_end", "-1"));
                
                Personal foundP = null;
                for(Wache w : LogistikSimulator.wachen) {
                    for(Personal pers : w.personalPool) {
                        if(pers.name.equals(pName)) { foundP = pers; break; }
                    }
                    if(foundP != null) break;
                }
                
                Email m = new Email(abs, bet, txt, typ, foundP, start, end);
                m.gelesen = gel;
                LogistikSimulator.postfach.add(m);
            }

            if (LogistikSimulator.vorlagenPool.isEmpty() || LogistikSimulator.customMaterials.isEmpty() || LogistikSimulator.wachen.isEmpty()) {
                LogistikSimulator.initStandardDaten();
            }

            for(Wache w : LogistikSimulator.wachen) {
                for(Fahrzeug f : w.fuhrpark) {
                    if (f.status == 3 || f.status == 4) {
                        f.status = 2; 
                        f.aktuellerEinsatz = null;
                        f.anfahrtsZeit = 0;
                    }
                    
                    boolean persDa = LogistikSimulator.hatGenugPersonal(f);
                    boolean matDa = true;
                    for(CustomMaterial cm : LogistikSimulator.customMaterials) {
                        if(w.material.getOrDefault(cm.name, 0) < 5) matDa = false;
                    }

                    if (f.status == 6 && f.ausfallGrund.startsWith("Material")) {
                        if (matDa && persDa) { f.status = 2; f.ausfallGrund = ""; }
                    }
                    
                    if (f.status == 1 || f.status == 2) {
                        if (!persDa) { f.status = 6; f.ausfallGrund = "Personal fehlt"; }
                        else if (!matDa) { f.status = 6; f.ausfallGrund = "Material fehlt"; }
                    }
                }
            }

            return true;
        } catch (Exception e) { return false; }
    }
}