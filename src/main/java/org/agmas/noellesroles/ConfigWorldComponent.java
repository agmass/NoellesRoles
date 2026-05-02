package org.agmas.noellesroles;

import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ClientTickingComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.UUID;

public class ConfigWorldComponent implements AutoSyncedComponent, ServerTickingComponent {
    public static final ComponentKey<ConfigWorldComponent> KEY = ComponentRegistry.getOrCreate(Identifier.of(Noellesroles.MOD_ID, "config"), ConfigWorldComponent.class);
    public boolean insaneSeesMorphs = true;
    public boolean naturalVoodoosAllowed = false;
    public int masterKeyVisibleCount = 0;
    public boolean masterKeyIsVisible = false;
    public boolean guesserCanUseInstinct = false;


    public int introvertRange = 20;
    public int defenseVialPrice = 100;
    public int maximumDefenseVials = 0;
    public int roleMinePrice = 100;
    private final World world;

    public void reset() {
        this.sync();
    }

    public ConfigWorldComponent(World world) {
        this.world = world;
    }

    public void sync() {
        KEY.sync(this.world);
    }

    public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        insaneSeesMorphs = NoellesRolesConfig.HANDLER.instance().insanePlayersSeeMorphs;
        naturalVoodoosAllowed = NoellesRolesConfig.HANDLER.instance().voodooNonKillerDeaths;
        masterKeyVisibleCount = NoellesRolesConfig.HANDLER.instance().playerCountToMakeConducterKeyVisible;
        defenseVialPrice = NoellesRolesConfig.HANDLER.instance().defenseVialPrice;
        roleMinePrice = NoellesRolesConfig.HANDLER.instance().roleMinePrice;
        maximumDefenseVials = NoellesRolesConfig.HANDLER.instance().maximumDefenseVials;
        guesserCanUseInstinct = NoellesRolesConfig.HANDLER.instance().guesserCanUseInstinct;
        introvertRange = NoellesRolesConfig.HANDLER.instance().introvertDisableRange;

        tag.putBoolean("insaneSeesMorphs", this.insaneSeesMorphs);
        tag.putBoolean("guesserCanUseInstinct", this.guesserCanUseInstinct);
        tag.putBoolean("naturalVoodoosAllowed", this.naturalVoodoosAllowed);
        tag.putBoolean("masterKeyIsVisible", this.masterKeyIsVisible);
        tag.putInt("masterKeyVisibleCount", this.masterKeyVisibleCount);
        tag.putInt("introvertRange", this.introvertRange);

        tag.putInt("defenseVialPrice", this.defenseVialPrice);
        tag.putInt("roleMinePrice", this.roleMinePrice);
        tag.putInt("maximumDefenseVials", this.maximumDefenseVials);
    }



    public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("insaneSeesMorphs"))   this.insaneSeesMorphs = tag.getBoolean("insaneSeesMorphs");
        if (tag.contains("naturalVoodoosAllowed"))   this.naturalVoodoosAllowed = tag.getBoolean("naturalVoodoosAllowed");
        if (tag.contains("masterKeyIsVisible"))   this.masterKeyIsVisible = tag.getBoolean("masterKeyIsVisible");
        if (tag.contains("masterKeyVisibleCount"))   this.masterKeyVisibleCount = tag.getInt("masterKeyVisibleCount");
        if (tag.contains("introvertRange"))   this.introvertRange = tag.getInt("introvertRange");

        if (tag.contains("defenseVialPrice"))   this.defenseVialPrice = tag.getInt("defenseVialPrice");
        if (tag.contains("guesserCanUseInstinct"))   this.guesserCanUseInstinct = tag.getBoolean("guesserCanUseInstinct");
        if (tag.contains("roleMinePrice"))   this.roleMinePrice = tag.getInt("roleMinePrice");
        if (tag.contains("maximumDefenseVials"))   this.maximumDefenseVials = tag.getInt("maximumDefenseVials");
    }

    @Override
    public void serverTick() {
        if (NoellesRolesConfig.HANDLER.instance().playerCountToMakeConducterKeyVisible == 0) {
            masterKeyIsVisible = false;
        } else {
            if (world.getServer() != null)
                masterKeyIsVisible =  world.getServer().getPlayerManager().getCurrentPlayerCount() >= NoellesRolesConfig.HANDLER.instance().playerCountToMakeConducterKeyVisible;
        }
        this.sync();
    }
}
