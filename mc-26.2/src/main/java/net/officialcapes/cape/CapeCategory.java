package net.officialcapes.cape;

import net.minecraft.network.chat.Component;

public enum CapeCategory {
    ALL("All", "category.official_capes.all"),
    MINECON("Minecon", "category.official_capes.minecon"),
    STAFF("Staff", "category.official_capes.staff"),
    EVENTS("Events", "category.official_capes.events"),
    COMMUNITY("Community", "category.official_capes.community"),
    SPECIAL("Special", "category.official_capes.special");

    private final String displayName;
    private final String translationKey;

    CapeCategory(String displayName, String translationKey) {
        this.displayName = displayName;
        this.translationKey = translationKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public Component getText() {
        return Component.literal(displayName);
    }
}
