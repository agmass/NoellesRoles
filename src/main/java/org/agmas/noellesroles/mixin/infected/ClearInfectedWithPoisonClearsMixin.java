package org.agmas.noellesroles.mixin.infected;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
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
            }
        }
    }
    @WrapMethod(method = "reset")
    private void noBackfire(Operation<Void> original) {
        InfectedPlayerComponent.KEY.get(player).infectedTicks = 0;
    }
}
