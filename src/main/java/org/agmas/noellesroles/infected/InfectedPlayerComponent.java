package org.agmas.noellesroles.infected;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPoisonComponent;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class InfectedPlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public static final ComponentKey<InfectedPlayerComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "infected"), InfectedPlayerComponent.class);
    private final PlayerEntity player;
    public int infectedTicks = 0;
    public UUID infector;

    public void reset() {
        infector = null;
        infectedTicks = 0;
        PlayerPoisonComponent.KEY.get(player).reset();
        this.sync();
    }
    public void infect(PlayerEntity infector) {
        infectedTicks = 1;
        this.infector = infector.getUuid();
    }

    public InfectedPlayerComponent(PlayerEntity player) {
        this.player = player;
    }

    public void sync() {
        KEY.sync(this.player);
    }

    public void clientTick() {
    }

    public void serverTick() {
        if (this.infectedTicks > 0) {
            if (GameWorldComponent.KEY.get(player.getWorld()).isRole(player,Noellesroles.INFECTED) && player.isSpectator()) {
                reset();
                return;
            }
            if (player.getWorld().getPlayerByUuid(infector) != null) {
                if (GameFunctions.isPlayerEliminated(player.getWorld().getPlayerByUuid(infector))) {
                    reset();
                    return;
                }
            } else {
                reset();
                return;
            }
            ++this.infectedTicks;
            PlayerPoisonComponent.KEY.get(player).poisonTicks = 10;
            PlayerPoisonComponent.KEY.get(player).poisoner = infector;
            if (infectedTicks > NoellesRolesConfig.HANDLER.instance().infectedKillTime) {
                if (player.getWorld().getPlayerByUuid(infector) != null) {
                    GameFunctions.killPlayer(this.player, true, this.infector == null ? null : this.player.getWorld().getPlayerByUuid(this.infector), Noellesroles.INFECTION_DEATH_REASON);
                    AbilityPlayerComponent.KEY.get(player.getWorld().getPlayerByUuid(infector)).cooldown = 0;
                    AbilityPlayerComponent.KEY.get(player.getWorld().getPlayerByUuid(infector)).sync();
                    infectedTicks = 0;
                    infector = null;
                    PlayerPoisonComponent.KEY.get(player).reset();
                }
            }
            if ((infectedTicks % 20 == 0 && NoellesRolesConfig.HANDLER.instance().infectedCoughChance >= player.getRandom().nextBetween(0,100)) || infectedTicks == NoellesRolesConfig.HANDLER.instance().infectedKillTime-(20*10)) {
                player.getWorld().playSound(null, player.getPos().x,player.getEyePos().y,player.getPos().z, SoundEvent.of(Identifier.of(Noellesroles.MOD_ID, "cough")), SoundCategory.MASTER, 1f, 1f + (player.getRandom().nextBetween(-2, 2)*0.1f));
                player.setVelocity(player.getRotationVec(0f).negate().multiply(0.75f));
                player.velocityDirty = true;
                player.velocityModified = true;
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), player.getVelocity()));
            }
        }
        this.sync();
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("infectedTicks", this.infectedTicks);
    }

    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        this.infectedTicks = tag.contains("infectedTicks") ? tag.getInt("infectedTicks") : 0;
    }
}
