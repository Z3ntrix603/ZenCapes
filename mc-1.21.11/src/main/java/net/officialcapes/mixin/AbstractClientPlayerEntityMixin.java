package net.officialcapes.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import net.officialcapes.OfficialCapesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void onGetSkin(CallbackInfoReturnable<SkinTextures> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && player.getId() == client.player.getId()) {
            Identifier customCape = OfficialCapesClient.getActiveCapeTexture();
            if (customCape != null) {
                SkinTextures original = cir.getReturnValue();
                cir.setReturnValue(new SkinTextures(
                    original.body(),
                    new AssetInfo.TextureAssetInfo(customCape),
                    original.elytra(),
                    original.model(),
                    original.secure()
                ));
            }
        }
    }
}
