package org.agmas.noellesroles.client.mixin;

import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerPoisonComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.RoundTextRenderer;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.util.AnnounceWelcomePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(TMMClient.class)
public abstract class InstinctMixin {


    @Shadow public static KeyBinding instinctKeybind;

    @Inject(method = "isInstinctEnabled", at = @At("HEAD"), cancellable = true)
    private static void b(CallbackInfoReturnable<Boolean> cir) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.JESTER)) {
            if (instinctKeybind.isPressed()) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "getInstinctHighlight", at = @At("HEAD"), cancellable = true)
    private static void getInstinctHighlightColor(Entity target, CallbackInfoReturnable<Integer> cir) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        if (target instanceof PlayerEntity) {
            if (!((PlayerEntity)target).isSpectator()) {
                BartenderPlayerComponent bartenderPlayerComponent = BartenderPlayerComponent.KEY.get((PlayerEntity) target);
                PlayerPoisonComponent playerPoisonComponent =  PlayerPoisonComponent.KEY.get((PlayerEntity) target);
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.BARTENDER) && bartenderPlayerComponent.glowTicks > 0) {
                    cir.setReturnValue(Color.GREEN.getRGB());
                }
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.BARTENDER) && bartenderPlayerComponent.armor > 0) {
                    cir.setReturnValue(Color.BLUE.getRGB());
                    cir.cancel();
                }
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.BARTENDER) && playerPoisonComponent.poisonTicks > 0) {
                    cir.setReturnValue(Color.RED.getRGB());
                }
            }
        }
        if (target instanceof PlayerEntity) {
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER)) {
                ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get((PlayerEntity) MinecraftClient.getInstance().player);
                if (executionerPlayerComponent.target.equals(target.getUuid())) {
                    cir.setReturnValue(Color.YELLOW.getRGB());
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && TMMClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole((PlayerEntity) target, Noellesroles.VULTURE) && TMMClient.isKiller() && TMMClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Noellesroles.VULTURE.color());
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && TMMClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole((PlayerEntity) target, Noellesroles.EXECUTIONER) && TMMClient.isKiller() && TMMClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Noellesroles.EXECUTIONER.color());
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && TMMClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole((PlayerEntity) target, Noellesroles.JESTER) && TMMClient.isKiller() && TMMClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Color.PINK.getRGB());
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && TMMClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.JESTER) && TMMClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Color.PINK.getRGB());
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && TMMClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER) && TMMClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Noellesroles.EXECUTIONER.color());
                    cir.cancel();
                }
            }
        }
    }
}
