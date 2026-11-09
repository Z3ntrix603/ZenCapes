package net.officialcapes.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class OfficialCapesKeybinds {
    public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("official_capes", "general"));

    public static KeyBinding openGuiKey;
    public static KeyBinding toggleCapeKey;

    public static void register() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.official_capes.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            CATEGORY
        ));

        toggleCapeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.official_capes.toggle_cape",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            CATEGORY
        ));
    }
}
