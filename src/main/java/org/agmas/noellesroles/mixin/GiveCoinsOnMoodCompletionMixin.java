package org.agmas.noellesroles.mixin;

import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent;
import dev.doctor4t.trainmurdermystery.client.gui.StoreRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMoodComponent.class)
public abstract class GiveCoinsOnMoodCompletionMixin {

    @Shadow public abstract float getMood();

    @Shadow @Final private PlayerEntity player;

    @Inject(method = "setMood", at = @At("HEAD"))
    void giveCoinsForMood(float mood, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent)GameWorldComponent.KEY.get(player.getWorld());
        if (mood > getMood()) {
            if (gameWorldComponent.getRole(player) != null) {
                if (gameWorldComponent.getRole(player).getMoodType().equals(Role.MoodType.REAL)) {
                    PlayerShopComponent shopComponent = PlayerShopComponent.KEY.get(player);
                    shopComponent.addToBalance(50);
                }
            }
        }
    }
}
