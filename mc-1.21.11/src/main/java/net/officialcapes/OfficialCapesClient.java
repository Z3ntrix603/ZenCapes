package net.officialcapes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.officialcapes.cape.CapeDefinition;
import net.officialcapes.cape.CapeRegistry;
import net.officialcapes.command.OfficialCapesCommand;
import net.officialcapes.config.OfficialCapesConfig;
import net.officialcapes.gui.OfficialCapesScreen;
import net.officialcapes.keybind.OfficialCapesKeybinds;
import net.officialcapes.physics.CapeSimulation;

public class OfficialCapesClient implements ClientModInitializer {
    public static final String MOD_ID = "official_capes";
    public static final CapeSimulation SIMULATION = new CapeSimulation();

    @Override
    public void onInitializeClient() {
        System.out.println("[Official Capes] Initializing Official Capes & Dawn/NoRisk Physics Mod...");

        // Load config
        OfficialCapesConfig.getInstance();

        // Register Keybindings
        OfficialCapesKeybinds.register();

        // Register Chat Commands (/cape and /capes)
        OfficialCapesCommand.register();

        // Register Tick Events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Check Keybinding: Open GUI
            while (OfficialCapesKeybinds.openGuiKey.wasPressed()) {
                client.setScreen(new OfficialCapesScreen(null));
            }

            // Check Keybinding: Toggle Cape
            while (OfficialCapesKeybinds.toggleCapeKey.wasPressed()) {
                OfficialCapesConfig config = OfficialCapesConfig.getInstance();
                boolean state = config.toggleCape();
                Text msg = state ?
                    Text.literal("§a[Official Capes] Cape aktiviert") :
                    Text.literal("§c[Official Capes] Cape deaktiviert");
                client.player.sendMessage(msg, true);
            }
        });

        System.out.println("[Official Capes] Successfully loaded 36 official capes with cloth physics!");
    }

    public static Identifier getActiveCapeTexture() {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        if (!config.enabled) {
            return null;
        }
        CapeDefinition def = CapeRegistry.get(config.selectedCapeId);
        return def != null ? def.getTexture() : null;
    }

    public static boolean shouldRenderCustomCape(AbstractClientPlayerEntity player) {
        if (player == null) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || player.getId() != client.player.getId()) return false;

        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        return config.enabled;
    }
}
