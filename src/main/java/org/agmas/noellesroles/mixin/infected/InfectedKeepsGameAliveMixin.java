package org.agmas.noellesroles.mixin.infected;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.game.gamemode.MurderGameMode;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.UUID;

@Mixin(MurderGameMode.class)
public class InfectedKeepsGameAliveMixin {

    @WrapOperation(method = "tickServerGameLoop", at = @At(value = "INVOKE", target = "Ldev/doctor4t/wathe/cca/GameWorldComponent;getAllKillerTeamPlayers()Ljava/util/List;"))
    private List<UUID> noBackfire(GameWorldComponent instance, Operation<List<UUID>> original) {
        List<UUID> killers = original.call(instance);
        instance.getRoles().forEach((u,r)->{
            if (r.equals(Noellesroles.INFECTED)) killers.add(u);
        });
        return killers;
    }
}
