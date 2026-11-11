package net.officialcapes.cape;

import java.util.*;

public class CapeRegistry {
    private static final Map<String, CapeDefinition> CAPES = new LinkedHashMap<>();
    private static final List<CapeDefinition> CAPE_LIST = new ArrayList<>();

    static {
        // Minecon Capes
        register(new CapeDefinition("minecon_2011", "Minecon 2011", CapeCategory.MINECON, "Awarded at the first-ever MINECON 2011 in Las Vegas. Classic red design with pickaxe."));
        register(new CapeDefinition("minecon_2012", "Minecon 2012", CapeCategory.MINECON, "Awarded at MINECON 2012 in Paris (Disneyland). Dark blue with golden pickaxe."));
        register(new CapeDefinition("minecon_2013", "Minecon 2013", CapeCategory.MINECON, "Awarded at MINECON 2013 in Orlando, Florida. Forest green with piston motif."));
        register(new CapeDefinition("minecon_2015", "Minecon 2015", CapeCategory.MINECON, "Awarded at MINECON 2015 in London. Iron Golem face with poppy flower."));
        register(new CapeDefinition("minecon_2016", "Minecon 2016", CapeCategory.MINECON, "Awarded at MINECON 2016 in Anaheim. Dark purple with glowing Enderman eyes."));
        register(new CapeDefinition("minecon_2019", "Founder's Cape 2019", CapeCategory.MINECON, "Awarded during MINECON Live 2019. Sleek slate grey with Creeper face."));

        // Staff Capes
        register(new CapeDefinition("mojang_classic", "Mojang Classic", CapeCategory.STAFF, "Official Mojang Studios staff cape featuring the classic white ruby logo."));
        register(new CapeDefinition("mojang_studios", "Mojang Studios", CapeCategory.STAFF, "Modern official Mojang Studios employee cape in vibrant crimson red."));
        register(new CapeDefinition("mojang_spil", "Mojang Spil", CapeCategory.STAFF, "Historical Mojang staff cape from early development days in Scandinavia."));
        register(new CapeDefinition("mojang_office", "Mojang Developer", CapeCategory.STAFF, "Exclusive developer cape for Mojang developers and studio staff."));

        // Event Capes
        register(new CapeDefinition("anniversary_15", "15th Anniversary", CapeCategory.EVENTS, "Official anniversary cape celebrating 15 years of Minecraft in Creeper green."));
        register(new CapeDefinition("mcc_15", "MCC 15 Year Cape", CapeCategory.EVENTS, "Royal indigo cape featuring a golden Ender Cup crown for the MCC 15th anniversary event."));
        register(new CapeDefinition("cherry", "Cherry Blossom", CapeCategory.EVENTS, "Gentle cherry blossom cape released alongside the Trails & Tales update."));
        register(new CapeDefinition("experience", "Minecraft Experience", CapeCategory.EVENTS, "Exclusive event cape from the Minecraft Experience in Dallas featuring portal vortex."));
        register(new CapeDefinition("twitch", "Twitch Cape", CapeCategory.EVENTS, "Official partnership cape from the Minecraft 15th Anniversary Twitch campaign."));
        register(new CapeDefinition("tiktok", "TikTok Cape", CapeCategory.EVENTS, "Official partnership cape from the Minecraft TikTok campaign with glitch design."));
        register(new CapeDefinition("birthday", "Birthday Cape", CapeCategory.EVENTS, "Minecraft birthday cape featuring celebratory birthday cake, candles and sprinkles."));
        register(new CapeDefinition("new_year_2011", "New Year 2011", CapeCategory.EVENTS, "Classic New Year's Eve cape welcoming the arrival of year 2011."));
        register(new CapeDefinition("valentine", "Valentine Cape", CapeCategory.EVENTS, "Special Valentine's Day 2011 cape featuring a radiant ruby heart."));

        // Community Capes
        register(new CapeDefinition("migrator", "Migrator Cape", CapeCategory.COMMUNITY, "Awarded to players who migrated their legacy Mojang account to a Microsoft account."));
        register(new CapeDefinition("vanilla", "Vanilla Cape", CapeCategory.COMMUNITY, "Awarded to players who owned both Java and Bedrock editions prior to the bundle."));
        register(new CapeDefinition("turtle", "Turtle Cape", CapeCategory.COMMUNITY, "Awarded to supporters and contributors to the Minecraft sea turtle campaign."));
        register(new CapeDefinition("translator", "Translator Cape", CapeCategory.COMMUNITY, "Awarded to dedicated Crowdin translators for global localization of Minecraft."));
        register(new CapeDefinition("prismarine", "Prismarine Cape", CapeCategory.COMMUNITY, "Special cape for Chinese translators and community contributors in oceanic prismarine."));
        register(new CapeDefinition("cobalt", "Cobalt Cape", CapeCategory.COMMUNITY, "Awarded to champions and masters of the Mojang/Oxeye action game Cobalt."));
        register(new CapeDefinition("scrolls", "Scrolls Cape", CapeCategory.COMMUNITY, "Awarded to tournament winners of the tactical card game Scrolls."));
        register(new CapeDefinition("pan_cape", "Pancakes Cape", CapeCategory.COMMUNITY, "The famous golden pancake cape with a melting pat of butter."));

        // Special & Rare Personalities
        register(new CapeDefinition("bacon", "Bacon Cape (Jonas)", CapeCategory.SPECIAL, "The legendary bacon cape specially granted to player Jonas."));
        register(new CapeDefinition("millionth_customer", "Millionth Customer", CapeCategory.SPECIAL, "Awarded to akjanew for purchasing the one-millionth copy of Minecraft."));
        register(new CapeDefinition("danny_style", "Danny Style", CapeCategory.SPECIAL, "Elegant suit and tie cape granted to musician DannyBStyle."));
        register(new CapeDefinition("julian_clark", "JulianClark", CapeCategory.SPECIAL, "Personal cape granted to Julian Clark in appreciation for video contributions."));
        register(new CapeDefinition("cheap_shot", "CheapShot", CapeCategory.SPECIAL, "Exclusive sunburst rays cape granted to player CheapShot."));
        register(new CapeDefinition("mr_messiah", "MrMessiah", CapeCategory.SPECIAL, "Awarded to MrMessiah for inventing the original 'BetterLight' ambient occlusion system."));
        register(new CapeDefinition("dinnerbone", "Dinnerbone Cape", CapeCategory.SPECIAL, "The upside-down cape personal to Mojang developer Dinnerbone."));
        register(new CapeDefinition("grumm", "Grumm Cape", CapeCategory.SPECIAL, "The inverted staff cape personal to Mojang developer Grumm."));
        register(new CapeDefinition("ray_cokes", "Ray Cokes Cape", CapeCategory.SPECIAL, "Unique television test-pattern cape granted to British TV presenter Ray Cokes."));
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
