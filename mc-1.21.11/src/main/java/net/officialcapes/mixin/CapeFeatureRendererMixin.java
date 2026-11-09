package net.officialcapes.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.officialcapes.OfficialCapesClient;
import net.officialcapes.config.OfficialCapesConfig;
import net.officialcapes.render.CapeClothRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin {

    @Inject(
        method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRenderCape(
        MatrixStack matrices,
        OrderedRenderCommandQueue queue,
        int light,
        PlayerEntityRenderState state,
        float limbAngle,
        float limbDistance,
        CallbackInfo ci
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && state.id == client.player.getId()) {
            OfficialCapesConfig config = OfficialCapesConfig.getInstance();
            if (config.enabled && config.physicsEnabled) {
                Identifier customCape = OfficialCapesClient.getActiveCapeTexture();
                if (customCape != null) {
                    CapeClothRenderer.render(
                        matrices,
                        queue,
                        light,
                        customCape,
                        OfficialCapesClient.SIMULATION,
                        1.0f
                    );
                    ci.cancel();
                }
            }
        }
    }
}
