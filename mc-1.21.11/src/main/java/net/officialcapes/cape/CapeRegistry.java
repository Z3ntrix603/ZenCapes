package net.officialcapes.cape;

import java.util.*;

public class CapeRegistry {
    private static final Map<String, CapeDefinition> CAPES = new LinkedHashMap<>();
    private static final List<CapeDefinition> CAPE_LIST = new ArrayList<>();

    static {
        // Minecon Capes
        register(new CapeDefinition("minecon_2011", "Minecon 2011", CapeCategory.MINECON, "Verliehen auf der allerersten Minecon 2011 in Las Vegas. Klassisches rotes Design mit Spitzhacke."));
        register(new CapeDefinition("minecon_2012", "Minecon 2012", CapeCategory.MINECON, "Verliehen auf der Minecon 2012 in Paris (Disneyland). Dunkelblau mit goldener Spitzhacke."));
        register(new CapeDefinition("minecon_2013", "Minecon 2013", CapeCategory.MINECON, "Verliehen auf der Minecon 2013 in Orlando, Florida. Waldgrün mit Kolben-Motiv."));
        register(new CapeDefinition("minecon_2015", "Minecon 2015", CapeCategory.MINECON, "Verliehen auf der Minecon 2015 in London. Eisengolem-Gesicht mit Mohnblume."));
        register(new CapeDefinition("minecon_2016", "Minecon 2016", CapeCategory.MINECON, "Verliehen auf der Minecon 2016 in Anaheim. Dunkelviolett mit leuchtenden Enderman-Augen."));
        register(new CapeDefinition("minecon_2019", "Founder's Cape 2019", CapeCategory.MINECON, "Verliehen während des Minecon Live 2019 Events. Schlichtes Schiefergrau mit Creeper."));

        // Mitarbeiter / Staff Capes
        register(new CapeDefinition("mojang_classic", "Mojang Classic", CapeCategory.STAFF, "Offizielles Cape für Mojang Mitarbeiter mit dem klassischen weißen Rubin-Logo."));
        register(new CapeDefinition("mojang_studios", "Mojang Studios", CapeCategory.STAFF, "Modernes offizielles Mitarbeiter-Cape von Mojang Studios im kräftigen Rot."));
        register(new CapeDefinition("mojang_spil", "Mojang Spil", CapeCategory.STAFF, "Historisches Mitarbeiter-Cape aus den frühesten Mojang-Tagen in Skandinavien."));
        register(new CapeDefinition("mojang_office", "Mojang Developer", CapeCategory.STAFF, "Exklusives Entwickler-Cape für Entwickler und Studio-Mitarbeiter."));

        // Event Capes
        register(new CapeDefinition("anniversary_15", "15th Anniversary", CapeCategory.EVENTS, "Offizielles Jubiläums-Cape zum 15. Geburtstag von Minecraft im Creeper-Grün."));
        register(new CapeDefinition("mcc_15", "MCC 15 Year Cape", CapeCategory.EVENTS, "Königliches Indigo-Cape mit goldener Ender Cup Krone anlässlich des MCC-Jubiläumsevents."));
        register(new CapeDefinition("cherry", "Cherry Blossom", CapeCategory.EVENTS, "Sanftes Kirschblüten-Cape zum Release des Trails & Tales Updates."));
        register(new CapeDefinition("experience", "Minecraft Experience", CapeCategory.EVENTS, "Exklusives Event-Cape der Minecraft Experience Dallas mit Portal-Vortex."));
        register(new CapeDefinition("twitch", "Twitch Cape", CapeCategory.EVENTS, "Offizielles Partnerschafts-Cape zur Minecraft 15-Jahre Twitch-Kampagne."));
        register(new CapeDefinition("tiktok", "TikTok Cape", CapeCategory.EVENTS, "Offizielles Partnerschafts-Cape zur Minecraft TikTok-Kampagne im Glitch-Look."));
        register(new CapeDefinition("birthday", "Birthday Cape", CapeCategory.EVENTS, "Minecraft Geburtstagsumhang mit Festtagstorte, Kerzen und Streuseln."));
        register(new CapeDefinition("new_year_2011", "Neujahr 2011", CapeCategory.EVENTS, "Klassischer Silvester-Umhang zur Begrüßung des Jahres 2011."));
        register(new CapeDefinition("valentine", "Valentine Cape", CapeCategory.EVENTS, "Sonderumhang zum Valentinstag 2011 mit leuchtendem Rubin-Herz."));

        // Community Capes
        register(new CapeDefinition("migrator", "Migrator Cape", CapeCategory.COMMUNITY, "Verliehen an alle Spieler, die ihr Mojang-Konto erfolgreich migriert haben."));
        register(new CapeDefinition("vanilla", "Vanilla Cape", CapeCategory.COMMUNITY, "Verliehen an Spieler, die Java & Bedrock vor der Zusammenlegung besaßen."));
        register(new CapeDefinition("turtle", "Turtle Cape", CapeCategory.COMMUNITY, "Verliehen an Unterstützer der Minecraft Meeresschildkröten-Kampagne."));
        register(new CapeDefinition("translator", "Translator Cape", CapeCategory.COMMUNITY, "Verliehen an engagierte Crowdin-Übersetzer für weltweite Lokalisierung."));
        register(new CapeDefinition("prismarine", "Prismarin Cape", CapeCategory.COMMUNITY, "Sonderumhang für chinesische Übersetzer und Contributor im Meeres-Design."));
        register(new CapeDefinition("cobalt", "Cobalt Cape", CapeCategory.COMMUNITY, "Verliehen an Sieger und Meister des Mojang/Oxeye-Spiels Cobalt."));
        register(new CapeDefinition("scrolls", "Scrolls Cape", CapeCategory.COMMUNITY, "Verliehen an Turniersieger des taktischen Kartenspiels Scrolls."));
        register(new CapeDefinition("pan_cape", "Pancakes Cape", CapeCategory.COMMUNITY, "Der berühmte goldene Pfannkuchen-Umhang mit schmelzendem Butterstück."));

        // Spezial & Seltene Persönlichkeiten
        register(new CapeDefinition("bacon", "Bacon Cape (Jonas)", CapeCategory.SPECIAL, "Das berühmte Speck-Cape, eigens verliehen an den Spieler Jonas."));
        register(new CapeDefinition("millionth_customer", "Millionster Kunde", CapeCategory.SPECIAL, "Verliehen an akjanew für den Kauf der millionsten Minecraft-Kopie."));
        register(new CapeDefinition("danny_style", "Danny Style", CapeCategory.SPECIAL, "Elegantes Anzug- und Krawatten-Cape für Musiker DannyBStyle."));
        register(new CapeDefinition("julian_clark", "JulianClark", CapeCategory.SPECIAL, "Persönliches Cape für Julian Clark als Dankeschön für Videobeiträge."));
        register(new CapeDefinition("cheap_shot", "CheapShot", CapeCategory.SPECIAL, "Exklusives Sonnenstrahlen-Cape für den Spieler CheapShot."));
        register(new CapeDefinition("mr_messiah", "MrMessiah", CapeCategory.SPECIAL, "Verliehen an MrMessiah für die Erfindung des ersten 'BetterLight' Beleuchtungssystems."));
        register(new CapeDefinition("dinnerbone", "Dinnerbone Cape", CapeCategory.SPECIAL, "Das um 180 Grad gedrehte Spezial-Cape des Mojang-Entwicklers Dinnerbone."));
        register(new CapeDefinition("grumm", "Grumm Cape", CapeCategory.SPECIAL, "Das invertierte Mitarbeiter-Cape des Entwicklers Grumm."));
        register(new CapeDefinition("ray_cokes", "Ray Cokes Cape", CapeCategory.SPECIAL, "Einzigartiges TV-Testbild-Cape für den britischen TV-Moderator Ray Cokes."));
    }

    private static void register(CapeDefinition cape) {
        CAPES.put(cape.getId(), cape);
        CAPE_LIST.add(cape);
    }

    public static List<CapeDefinition> getAll() {
        return Collections.unmodifiableList(CAPE_LIST);
    }

    public static CapeDefinition get(String id) {
        if (id == null) return null;
        return CAPES.get(id);
    }

    public static List<CapeDefinition> getByCategory(CapeCategory category) {
        if (category == null || category == CapeCategory.ALL) {
            return getAll();
        }
        List<CapeDefinition> filtered = new ArrayList<>();
        for (CapeDefinition cape : CAPE_LIST) {
            if (cape.getCategory() == category) {
                filtered.add(cape);
            }
        }
        return filtered;
    }

    public static CapeDefinition getDefaultCape() {
        return CAPES.get("minecon_2011");
    }
}
