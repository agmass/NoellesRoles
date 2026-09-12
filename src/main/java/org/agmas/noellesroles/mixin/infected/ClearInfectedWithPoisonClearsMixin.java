package org.agmas.noellesroles.mixin.infected;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(PlayerPoisonComponent.class)
public class ClearInfectedWithPoisonClearsMixin {
    @Shadow @Final private PlayerEntity player;

    @WrapMethod(method = "setPoisonTicks")
    private void poisonOverride(int ticks, UUID poisoner, Operation<Void> original) {
        if (player.getWorld().getPlayerByUuid(poisoner) != null) {
            if (GameWorldComponent.KEY.get(player.getWorld()).isRole(poisoner, Noellesroles.INFECTED)) {
                InfectedPlayerComponent.KEY.get(player).infector = poisoner;
                InfectedPlayerComponent.KEY.get(player).infectedTicks = 1;
                return;
            }
        }
        original.call(ticks, poisoner);
    }
    @Inject(method = "reset", at = @At("HEAD"))
    private void noBackfire(CallbackInfo ci) {
        InfectedPlayerComponent.KEY.get(player).infectedTicks = 0;
    }
}
