package net.officialcapes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.officialcapes.cape.CapeDefinition;
import net.officialcapes.cape.CapeRegistry;
import net.officialcapes.config.OfficialCapesConfig;
import net.officialcapes.gui.OfficialCapesScreen;

public class OfficialCapesCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(OfficialCapesCommand::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(
            ClientCommandManager.literal("cape")
                .executes(ctx -> openScreen())
                .then(ClientCommandManager.literal("gui")
                    .executes(ctx -> openScreen()))
                .then(ClientCommandManager.literal("toggle")
                    .executes(ctx -> toggleCape(ctx.getSource())))
                .then(ClientCommandManager.literal("set")
                    .then(ClientCommandManager.argument("cape_id", StringArgumentType.word())
                        .executes(ctx -> setCape(ctx.getSource(), StringArgumentType.getString(ctx, "cape_id")))))
                .then(ClientCommandManager.literal("physics")
                    .then(ClientCommandManager.literal("on")
                        .executes(ctx -> setPhysics(ctx.getSource(), true)))
                    .then(ClientCommandManager.literal("off")
                        .executes(ctx -> setPhysics(ctx.getSource(), false))))
        );

        // Alias /capes
        dispatcher.register(
            ClientCommandManager.literal("capes")
                .executes(ctx -> openScreen())
        );
    }

    private static int openScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> client.setScreen(new OfficialCapesScreen(null)));
        return 1;
    }

    private static int toggleCape(FabricClientCommandSource source) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        boolean state = config.toggleCape();
        Text msg = state ?
            Text.literal("§a[ZenCapes] Cape enabled.") :
            Text.literal("§c[ZenCapes] Cape disabled.");
        source.sendFeedback(msg);
        return 1;
    }

    private static int setCape(FabricClientCommandSource source, String id) {
        CapeDefinition cape = CapeRegistry.get(id);
        if (cape == null) {
            source.sendFeedback(Text.literal("§c[ZenCapes] Cape not found: " + id));
            return 0;
        }
        OfficialCapesConfig.getInstance().selectCape(id);
        source.sendFeedback(Text.literal("§a[ZenCapes] Equipped: " + cape.getName()));
        return 1;
    }

    private static int setPhysics(FabricClientCommandSource source, boolean enabled) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        config.physicsEnabled = enabled;
        config.save();
        Text msg = enabled ?
            Text.literal("§a[ZenCapes] Physics enabled.") :
            Text.literal("§6[ZenCapes] Physics disabled (vanilla cape).");
        source.sendFeedback(msg);
        return 1;
    }
}
