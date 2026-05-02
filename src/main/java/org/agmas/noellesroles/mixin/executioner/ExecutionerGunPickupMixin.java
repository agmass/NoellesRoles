package org.agmas.noellesroles.mixin.executioner;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.tag.WatheItemTags;
import dev.doctor4t.wathe.util.AnnounceWelcomePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Mixin(value = ItemEntity.class, priority = 1500)
public abstract class ExecutionerGunPickupMixin extends Entity {

    public ExecutionerGunPickupMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getStack();

    @Shadow private int pickupDelay;

    @Shadow @Nullable private UUID owner;

    @Shadow @Nullable public abstract Entity getOwner();

    @WrapMethod(method = "onPlayerCollision")
    private void executionerConfirm(PlayerEntity player, Operation<Void> original) {
        if (getStack().isIn(WatheItemTags.GUNS) && NoellesRolesConfig.HANDLER.instance().executionCanPickUpGun) {
            if (GameWorldComponent.KEY.get(player.getWorld()).isRole(player, Noellesroles.EXECUTIONER) && !player.equals(getOwner()) && !player.getInventory().contains((itemStack) -> itemStack.isIn(WatheItemTags.GUNS))) {
                if (!getWorld().isClient) { // is this the best way to do it? NO! I'm just lazy and this is the only way i FOUND to do it! HAHAhAHAHAHAHAHAHAHAH
                    ItemStack itemStack = this.getStack();
                    Item item = itemStack.getItem();
                    int i = itemStack.getCount();
                    if (pickupDelay == 0 && (owner == null || owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
                        player.sendPickup(this, i);
                        if (itemStack.isEmpty()) {
                            this.discard();
                            itemStack.setCount(i);
                        }

                        player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                        player.triggerItemPickedUpByEntityCriteria((ItemEntity) (Object) this);
                    }
                }
            }
        }
        original.call(player);
    }
}
