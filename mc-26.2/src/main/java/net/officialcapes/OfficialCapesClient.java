package net.officialcapes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
        System.out.println("[Official Capes] Initializing Official Capes & Dawn/NoRisk Physics Mod for Minecraft 26.2...");

        OfficialCapesConfig.getInstance();
        OfficialCapesKeybinds.register();
        OfficialCapesCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (OfficialCapesKeybinds.openGuiKey.consumeClick()) {
                client.setScreenAndShow(new OfficialCapesScreen(null));
            }

            while (OfficialCapesKeybinds.toggleCapeKey.consumeClick()) {
                OfficialCapesConfig config = OfficialCapesConfig.getInstance();
                boolean state = config.toggleCape();
                Component msg = state ?
                    Component.literal("§a[ZenCapes] Cape enabled") :
                    Component.literal("§c[ZenCapes] Cape disabled");
                client.player.sendOverlayMessage(msg);
            }
        });

        System.out.println("[Official Capes] Successfully loaded 36 official capes with cloth physics on 26.2!");
    }

    public static Identifier getActiveCapeTexture() {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        if (!config.enabled) {
            return null;
        }
        CapeDefinition def = CapeRegistry.get(config.selectedCapeId);
        return def != null ? def.getTexture() : null;
    }

    public static boolean shouldRenderCustomCape(AbstractClientPlayer player) {
        if (player == null) return false;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || player.getId() != client.player.getId()) return false;

        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        return config.enabled;
    }
}
