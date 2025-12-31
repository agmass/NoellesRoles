package org.agmas.noellesroles.entities;

import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.noellesroles.Noellesroles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RoleMineEntity extends Entity {
    public RoleMineEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public UUID owner;
    public ArrayList<UUID> previouslyCaught = new ArrayList<>();

    @Override
    public void tick() {
        super.tick();
        if (!getWorld().isClient) {
            if (!GameWorldComponent.KEY.get(getWorld()).isRunning()) {
                discard();
                return;
            }
            if (previouslyCaught.size() >= 3) {
                discard();
                GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(getWorld());
                if (owner == null) return;
                PlayerEntity ownerEntity = getWorld().getPlayerByUuid(owner);
                if (ownerEntity == null) return;
                ownerEntity.getInventory().remove((itemStack -> {
                    return itemStack.isOf(Items.PAPER);
                }),64, ownerEntity.getInventory());
                ItemStack trapperReport = Items.PAPER.getDefaultStack();
                trapperReport.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("item.noellesroles.trapper_report").formatted(Formatting.RESET).formatted(Formatting.GRAY));
                List<Text> loreLines = new ArrayList<>();
                loreLines.add(Text.translatable("tip.trapper.caught", previouslyCaught.size()).formatted(Formatting.RESET).withColor(Colors.GREEN));
                Collections.shuffle(previouslyCaught);
                for (UUID uuid1 : previouslyCaught) {
                    Role role = gameWorldComponent.getRole(uuid1);
                    if (role == null) continue;
                    loreLines.add(Harpymodloader.getRoleName(role).withColor(role.color()));
                }
                ownerEntity.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), SoundCategory.MASTER,1,1);
                LoreComponent loreComponent = new LoreComponent(loreLines);
                trapperReport.set(DataComponentTypes.LORE, loreComponent);
                ownerEntity.getInventory().insertStack(trapperReport);
                return;
            }
            List<PlayerEntity> caughtPlayers = getWorld().getEntitiesByClass(PlayerEntity.class, getBoundingBox().expand(7, 7, 7), (player)->{
                return !player.isSpectator() && !previouslyCaught.contains(player.getUuid()) && !player.getUuid().equals(owner);
            });

            for (PlayerEntity caughtPlayer : caughtPlayers) {
                previouslyCaught.add(caughtPlayer.getUuid());
                getWorld().playSound(this, getBlockPos(), SoundEvent.of(Identifier.of(Noellesroles.MOD_ID, "role_mine_beep")), SoundCategory.MASTER, 1f, 1f + (getRandom().nextBetween(-2, 2)/0.1f));
                if (owner != null) {
                    PlayerEntity ownerEntity = getWorld().getPlayerByUuid(owner);
                    if (ownerEntity == null) continue;
                    ownerEntity.playSoundToPlayer(SoundEvent.of(Identifier.of(Noellesroles.MOD_ID, "role_mine_beep")), SoundCategory.MASTER,1,1);
                }
            }
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
