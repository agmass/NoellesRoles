package org.agmas.noellesroles.mixin.host;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerPsychoComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public class MoneyIncreaseMixin {
    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"))
    private static void increaseMoney(PlayerEntity victim, boolean spawnBody, PlayerEntity killer, Identifier identifier, CallbackInfo ci) {
        if (killer != null) {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(victim.getWorld());
            if (gameWorldComponent.isRole(victim, Noellesroles.CONDUCTOR) && !gameWorldComponent.isInnocent(killer)) {
                PlayerShopComponent component = PlayerShopComponent.KEY.get(killer);
                component.addToBalance(100);
            }
        }
    }
}
