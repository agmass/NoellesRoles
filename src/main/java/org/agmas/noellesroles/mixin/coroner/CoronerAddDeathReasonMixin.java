package org.agmas.noellesroles.mixin.coroner;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerPsychoComponent;
import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameFunctions.class)
public abstract class CoronerAddDeathReasonMixin {

    @Inject(method = "killPlayer(Lnet/minecraft/entity/player/PlayerEntity;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Ldev/doctor4t/trainmurdermystery/entity/PlayerBodyEntity;setHeadYaw(F)V"), cancellable = true)
    private static void setDeathReason(PlayerEntity victim, boolean spawnBody, PlayerEntity killer, Identifier identifier, CallbackInfo ci, @Local PlayerBodyEntity playerBodyEntity) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(victim.getWorld());
        if (gameWorldComponent.getRole(victim) == null) return;
        (BodyDeathReasonComponent.KEY.get(playerBodyEntity)).deathReason = identifier;
        (BodyDeathReasonComponent.KEY.get(playerBodyEntity)).playerRole = gameWorldComponent.getRole(victim).identifier();
    }

}
