package org.agmas.noellesroles.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.client.WatheClient;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(PlayerEntity.class)
public abstract class StepSoundMixin {


    @WrapMethod(method = "playStepSound")
    private void b(BlockPos pos, BlockState state, Operation<Void> original) {
        if (MinecraftClient.getInstance().player != null) {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
            if (gameWorldComponent.isRole(((PlayerEntity) (Object) this), Noellesroles.PHANTOM) && ((PlayerEntity) (Object) this).isInvisible()) {
                return;
            }
            WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
            if (worldModifierComponent.isModifier(((PlayerEntity) (Object) this), Noellesroles.STEALTH)) {
                return;
            }
        }
        original.call(pos,state);
    }

}
