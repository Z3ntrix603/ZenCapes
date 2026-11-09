package net.officialcapes.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.officialcapes.OfficialCapesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void onGetSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        Minecraft client = Minecraft.getInstance();

        if (client.player != null && player.getId() == client.player.getId()) {
            Identifier customCape = OfficialCapesClient.getActiveCapeTexture();
            if (customCape != null) {
                PlayerSkin original = cir.getReturnValue();
                cir.setReturnValue(new PlayerSkin(
                    original.body(),
                    new ClientAsset.ResourceTexture(customCape),
                    original.elytra(),
                    original.model(),
                    original.secure()
                ));
            }
        }
    }
}
