package net.officialcapes.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.officialcapes.cape.CapeDefinition;
import net.officialcapes.cape.CapeRegistry;
import net.officialcapes.config.OfficialCapesConfig;
import net.officialcapes.gui.OfficialCapesScreen;

public class OfficialCapesCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register(OfficialCapesCommand::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext context) {
        dispatcher.register(
            ClientCommands.literal("cape")
                .executes(ctx -> openScreen())
                .then(ClientCommands.literal("gui")
                    .executes(ctx -> openScreen()))
                .then(ClientCommands.literal("toggle")
                    .executes(ctx -> toggleCape(ctx.getSource())))
                .then(ClientCommands.literal("set")
                    .then(ClientCommands.argument("cape_id", StringArgumentType.word())
                        .executes(ctx -> setCape(ctx.getSource(), StringArgumentType.getString(ctx, "cape_id")))))
                .then(ClientCommands.literal("physics")
                    .then(ClientCommands.literal("on")
                        .executes(ctx -> setPhysics(ctx.getSource(), true)))
                    .then(ClientCommands.literal("off")
                        .executes(ctx -> setPhysics(ctx.getSource(), false))))
        );

        dispatcher.register(
            ClientCommands.literal("capes")
                .executes(ctx -> openScreen())
        );
    }

    private static int openScreen() {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> client.setScreenAndShow(new OfficialCapesScreen(null)));
        return 1;
    }

    private static int toggleCape(FabricClientCommandSource source) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        boolean state = config.toggleCape();
        Component msg = state ?
            Component.literal("§a[Official Capes] Cape aktiviert.") :
            Component.literal("§c[Official Capes] Cape deaktiviert.");
        source.sendFeedback(msg);
        return 1;
    }

    private static int setCape(FabricClientCommandSource source, String id) {
        CapeDefinition cape = CapeRegistry.get(id);
        if (cape == null) {
            source.sendFeedback(Component.literal("§c[Official Capes] Cape nicht gefunden: " + id));
            return 0;
        }
        OfficialCapesConfig.getInstance().selectCape(id);
        source.sendFeedback(Component.literal("§a[Official Capes] Ausgerüstet: " + cape.getName()));
        return 1;
    }

    private static int setPhysics(FabricClientCommandSource source, boolean enabled) {
        OfficialCapesConfig config = OfficialCapesConfig.getInstance();
        config.physicsEnabled = enabled;
        config.save();
        Component msg = enabled ?
            Component.literal("§a[Official Capes] Physik aktiviert.") :
            Component.literal("§6[Official Capes] Physik deaktiviert (Standard-Umhang).");
        source.sendFeedback(msg);
        return 1;
    }
}
