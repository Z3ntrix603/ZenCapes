package net.officialcapes.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class OfficialCapesKeybinds {
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("official_capes", "general"));

    public static KeyMapping openGuiKey;
    public static KeyMapping toggleCapeKey;

    public static void register() {
        openGuiKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.official_capes.open_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
        ));

        toggleCapeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.official_capes.toggle_cape",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            CATEGORY
        ));
    }
}
