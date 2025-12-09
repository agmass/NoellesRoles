package org.agmas.noellesroles.mixin.executioner;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.util.GunShootPayload;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(GunShootPayload.Receiver.class)
public class NoTargetBackfireMixin {
    @WrapOperation(method = "receive(Ldev/doctor4t/trainmurdermystery/util/GunShootPayload;Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking$Context;)V", at = @At(value = "INVOKE", target = "Ldev/doctor4t/trainmurdermystery/cca/GameWorldComponent;isInnocent(Lnet/minecraft/entity/player/PlayerEntity;)Z", ordinal = 0))
    private boolean noBackfire(GameWorldComponent instance, PlayerEntity player, Operation<Boolean> original) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        for (UUID uuid : gameWorldComponent.getAllWithRole(Noellesroles.EXECUTIONER)) {
            PlayerEntity executioner = player.getWorld().getPlayerByUuid(uuid);
            if (executioner == null) continue;
            ExecutionerPlayerComponent executionerPlayerComponent = ExecutionerPlayerComponent.KEY.get(executioner);
            if (executionerPlayerComponent.target.equals(player.getUuid())) {
                return false;
            }
        }
        if (gameWorldComponent.isRole(player, Noellesroles.VOODOO) && NoellesRolesConfig.HANDLER.instance().voodooShotLikeEvil) {
            return false;
        }
        return original.call(instance,player);
    }
}
