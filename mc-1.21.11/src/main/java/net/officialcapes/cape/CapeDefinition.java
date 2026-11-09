package net.officialcapes.cape;

import net.minecraft.util.Identifier;

public class CapeDefinition {
    private final String id;
    private final String name;
    private final CapeCategory category;
    private final String description;
    private final Identifier texture;

    public CapeDefinition(String id, String name, CapeCategory category, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.texture = Identifier.of("official_capes", "textures/capes/" + id + ".png");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CapeCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Identifier getTexture() {
        return texture;
    }
}
