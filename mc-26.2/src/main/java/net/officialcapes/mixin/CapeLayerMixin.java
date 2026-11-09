package net.officialcapes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.officialcapes.OfficialCapesClient;
import net.officialcapes.config.OfficialCapesConfig;
import net.officialcapes.render.CapeClothRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public class CapeLayerMixin {

    @Inject(
        method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onSubmitCape(
        PoseStack matrices,
        SubmitNodeCollector collector,
        int light,
        AvatarRenderState state,
        float f,
        float g,
        CallbackInfo ci
    ) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && state.id == client.player.getId()) {
            OfficialCapesConfig config = OfficialCapesConfig.getInstance();
            if (config.enabled && config.physicsEnabled) {
                Identifier customCape = OfficialCapesClient.getActiveCapeTexture();
                if (customCape != null) {
                    CapeClothRenderer.render(
                        matrices,
                        collector,
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
