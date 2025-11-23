package org.agmas.noellesroles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerNoteComponent;
import dev.doctor4t.trainmurdermystery.cca.ScoreboardRoleSelectorComponent;
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import dev.doctor4t.trainmurdermystery.util.AnnounceWelcomePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mixin(GameFunctions.class)
public abstract class RoleAssignerMixin {

    @Inject(method = "resetPlayer", at = @At("TAIL"))
    private static void jesterWrite(ServerPlayerEntity player, CallbackInfo ci) {
        ((MorphlingPlayerComponent)MorphlingPlayerComponent.KEY.get(player)).reset();
    }
    @Inject(method = "assignRolesAndGetKillerCount", at = @At("TAIL"))
    private static void jesterWrite(@NotNull ServerWorld world, @NotNull List<ServerPlayerEntity> players, GameWorldComponent gameComponent, CallbackInfoReturnable<Integer> cir) {

        boolean allRolesFilled = false;
        int desiredRoleCount = (int)Math.floor((double)((float)players.size() * 0.2F));

        ArrayList<TMMRoles.Role> shuffledRoles = new ArrayList<>(TMMRoles.ROLES);
        Collections.shuffle(shuffledRoles);
        for (TMMRoles.Role shuffledRole : shuffledRoles) {
            Log.info(LogCategory.GENERAL, shuffledRole.identifier()+"");
        }

        // There's an entire complex system for rat's roles but... I'm just gonna randomly set the roles with a shuffle. Lol

        ArrayList<ServerPlayerEntity> playersForCivillianRoles = new ArrayList<>();

        players.forEach((p) -> {
            if (!gameComponent.isRole(p, TMMRoles.VIGILANTE) && !gameComponent.isRole(p, TMMRoles.KILLER)) {
                playersForCivillianRoles.add(p);
                if (Noellesroles.forceRoles.containsKey(p)) {
                    assignOneOfRole(Noellesroles.forceRoles.get(p), List.of(p), gameComponent);
                }
            }
        });

        while (!allRolesFilled) {
            allRolesFilled = true;
            for (TMMRoles.Role moddedRole : shuffledRoles) {
                if (NoellesRolesConfig.HANDLER.instance().disabled.contains(moddedRole.identifier().getPath())) continue;
                if (moddedRole == TMMRoles.KILLER || moddedRole == TMMRoles.VIGILANTE || moddedRole == TMMRoles.CIVILIAN || moddedRole == TMMRoles.LOOSE_END) continue;
                if (moddedRole.canUseKiller()) continue;
                if (Noellesroles.rolePlayerCaps.containsKey(moddedRole.identifier()) && Noellesroles.rolePlayerCaps.get(moddedRole.identifier()) <= gameComponent.getAllWithRole(moddedRole).size()) continue;
                if (gameComponent.getAllWithRole(moddedRole).size() >= desiredRoleCount) continue;
                Collections.shuffle(playersForCivillianRoles);

                if (assignOneOfRole(moddedRole, playersForCivillianRoles,gameComponent)) allRolesFilled = false;
            }
        }


        ArrayList<ServerPlayerEntity> playersForKillerRoles = new ArrayList<>();

        players.forEach((p) -> {
            if (gameComponent.isRole(p, TMMRoles.KILLER)) {
                playersForKillerRoles.add(p);
            }
        });

        allRolesFilled = false;

        while (!allRolesFilled) {
            allRolesFilled = true;
            for (TMMRoles.Role moddedRole : shuffledRoles) {
                if (NoellesRolesConfig.HANDLER.instance().disabled.contains(moddedRole.identifier().getPath())) continue;
                if (moddedRole == TMMRoles.KILLER || moddedRole == TMMRoles.VIGILANTE || moddedRole == TMMRoles.CIVILIAN || moddedRole == TMMRoles.LOOSE_END) continue;
                if (!moddedRole.canUseKiller()) continue;
                if (Noellesroles.rolePlayerCaps.containsKey(moddedRole.identifier()) && Noellesroles.rolePlayerCaps.get(moddedRole.identifier()) <= gameComponent.getAllWithRole(moddedRole).size()) continue;
                if (gameComponent.getAllWithRole(moddedRole).size() >= desiredRoleCount) continue;
                Collections.shuffle(playersForKillerRoles);

                if (assignOneOfRole(moddedRole, playersForKillerRoles,gameComponent)) allRolesFilled = false;
            }
        }

        Noellesroles.forceRoles.clear();
    }

    @Redirect(method = "initializeGame", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/networking/v1/ServerPlayNetworking;send(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/packet/CustomPayload;)V", ordinal = 1))
    private static void jesterWrite(ServerPlayerEntity player, CustomPayload payload, @Local int killerCount, @Local List<ServerPlayerEntity> players) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());

        if (Noellesroles.isAnyModdedRole(player)) {
            TMMRoles.Role role = gameWorldComponent.getRole(player);
            ServerPlayNetworking.send(player, new AnnounceWelcomePayload(RoleAnnouncementTexts.ROLE_ANNOUNCEMENT_TEXTS.indexOf(Noellesroles.roleRoleAnnouncementTextHashMap.get(role)), killerCount, players.size()-killerCount));
        } else {
            ServerPlayNetworking.send(player,payload);
        }
    }


    @Unique
    private static boolean assignOneOfRole(TMMRoles.Role role, @NotNull List<ServerPlayerEntity> players, GameWorldComponent gameWorldComponent) {
        for (PlayerEntity p : players) {
            if (!Noellesroles.isAnyModdedRole(p)) {
                Log.info(LogCategory.GENERAL, "Gave " + role.identifier() + " to " + p.getName());
                gameWorldComponent.addRole(p,role);
                noellesRolesItems(role,p);
                return true;

            }
        }
        return false;
    }

    @Unique
    private static void noellesRolesItems(TMMRoles.Role role, PlayerEntity player) {
        if (role.identifier().equals(Noellesroles.JESTER_ID)) {
            player.giveItemStack(ModItems.FAKE_KNIFE.getDefaultStack());
            player.giveItemStack(ModItems.FAKE_REVOLVER.getDefaultStack());
        }
        if (role.identifier().equals(Noellesroles.AWESOME_BINGLUS_ID)) {
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
            player.giveItemStack(TMMItems.NOTE.getDefaultStack());
        }
        if (role.identifier().equals(Noellesroles.HOST_ID)) {
            player.giveItemStack(ModItems.MASTER_KEY.getDefaultStack());
        }
    }
}
