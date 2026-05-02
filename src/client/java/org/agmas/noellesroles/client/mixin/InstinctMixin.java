package org.agmas.noellesroles.client.mixin;

import dev.doctor4t.wathe.Wathe;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.client.gui.RoundTextRenderer;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.math.MathHelper;
import org.agmas.harpymodloader.component.WorldModifierComponent;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.agmas.noellesroles.infected.InfectedPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(WatheClient.class)
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
            WorldModifierComponent worldModifierComponent = WorldModifierComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
            if (worldModifierComponent.isModifier((PlayerEntity) target, Noellesroles.STEALTH)) {
                if (target.distanceTo(MinecraftClient.getInstance().player) < 10) {
                    cir.setReturnValue(-1);
                    cir.cancel();
                    return;
                }
            }
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.INFECTED)) {
                if (InfectedPlayerComponent.KEY.get(target).infectedTicks > 0) {
                    cir.setReturnValue(Color.GREEN.getRGB());
                }
            }
            if (worldModifierComponent.isModifier(MinecraftClient.getInstance().player, Noellesroles.INTROVERT) && !MinecraftClient.getInstance().player.isSpectator()) {
                boolean introvertEnabled = true;
                for (AbstractClientPlayerEntity player : MinecraftClient.getInstance().world.getPlayers()) {
                    if (player.isSpectator()) continue;
                    if (MinecraftClient.getInstance().player.isSpectator()) continue;
                    if (player.equals(MinecraftClient.getInstance().player)) continue;
                    if (player.distanceTo(MinecraftClient.getInstance().player) < ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().world).introvertRange) {
                        introvertEnabled = false;
                        break;
                    }
                }
                if (introvertEnabled) {
                    cir.setReturnValue(Colors.BLUE);
                }
            }
            if (worldModifierComponent.isModifier(MinecraftClient.getInstance().player, Noellesroles.GUESSER) && !ConfigWorldComponent.KEY.get(MinecraftClient.getInstance().world).guesserCanUseInstinct) {
                if (gameWorldComponent.getRole((PlayerEntity) target).isInnocent()) {
                    cir.setReturnValue(-1);
                }
            }
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER)) {
                ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get((PlayerEntity) MinecraftClient.getInstance().player);
                if (executionerPlayerComponent.target.equals(target.getUuid())) {
                    cir.setReturnValue(Color.YELLOW.getRGB());
                    cir.cancel();
                }
            }
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.INFECTED) && WatheClient.isInstinctEnabled()) {
                cir.setReturnValue(Color.PINK.getRGB());
                cir.cancel();
            }
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.JESTER) && WatheClient.isInstinctEnabled()) {
                    cir.setReturnValue(Color.PINK.getRGB());
                    cir.cancel();
            }
            if (!((PlayerEntity)target).isSpectator() && WatheClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole((PlayerEntity) target, Noellesroles.MIMIC) && WatheClient.isKiller()  && WatheClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(MathHelper.hsvToRgb(0.0F, 1.0F, 0.6F));
                    cir.cancel();
                }
            }
            if (!((PlayerEntity)target).isSpectator() && WatheClient.isInstinctEnabled()) {
                Role role = gameWorldComponent.getRole((PlayerEntity) target);
                if (role != null) {
                    if (WatheClient.isKiller() && WatheClient.isPlayerAliveAndInSurvival()) {
                        if (Noellesroles.KILLER_SIDED_NEUTRALS.contains(role)) {
                            cir.setReturnValue(role.color());
                            cir.cancel();
                        } else if (!role.isInnocent() && !role.canUseKiller()) {
                           cir.setReturnValue(5168437);
                           cir.cancel();
                        }
                    }
                }
            }
            if (!((PlayerEntity)target).isSpectator() && WatheClient.isInstinctEnabled()) {
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER) && WatheClient.isPlayerAliveAndInSurvival()) {
                    cir.setReturnValue(Noellesroles.EXECUTIONER.color());
                    cir.cancel();
                }
            }
        }
    }
}
