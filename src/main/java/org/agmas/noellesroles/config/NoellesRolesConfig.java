package org.agmas.noellesroles.config;

import dev.doctor4t.wathe.game.GameConstants;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.Noellesroles;

import java.util.ArrayList;
import java.util.List;

public class NoellesRolesConfig {
    public static ConfigClassHandler<NoellesRolesConfig> HANDLER = ConfigClassHandler.createBuilder(NoellesRolesConfig.class)
            .id(Identifier.of(Noellesroles.MOD_ID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve( Noellesroles.MOD_ID + ".json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry(comment = "Whether insane players will randomly see people as morphed.")
    public boolean insanePlayersSeeMorphs = true;
    @SerialEntry(comment = "Allows the shitpost roles to retain their disable/enable state after a server restart")
    public boolean shitpostRoles = false;

    @SerialEntry(comment = "Starting cooldown (in ticks)")
    public int generalCooldownTicks = GameConstants.getInTicks(0,30);

    @SerialEntry(comment = "Allow Natural deaths to trigger voodoo (deaths without an assigned killer)")
    public boolean voodooNonKillerDeaths = false;

    @SerialEntry(comment = "Makes voodoos act like Evil players when shot by a revolver (no backfire, no gun lost)")
    public boolean voodooShotLikeEvil = true;

    @SerialEntry(comment = "Civillians can get the guesser modifier.")
    public boolean allowCivillianGuessers = false;

    @SerialEntry(comment = "How the guesser dies after an incorrect guess.\n\"none\" (default) - nothing happens, 2 minute cooldown applied\n\"death\" kills the player with a voodoo death message\n\"explode\" explodes the guesser, killing anyone nearby")
    public String guesserDiesAfterIncorrectGuess = "none";

    @SerialEntry(comment = "How many players must be online for the Master Key to look like a master key and not a lockpick. (0 = key always looks like a lockpick, 1-6 = key always looks normal)")
    public int playerCountToMakeConducterKeyVisible = 10;


    @SerialEntry(comment = "How many defense vials can be bought by one bartender in a round. (0 = no limit)")
    public int maximumDefenseVials = 0;

    @SerialEntry(comment = "Price of the Bartender's Defense Vial.")
    public int defenseVialPrice = 100;

    @SerialEntry(comment = "How long Defence Vials last (in ticks). -1 = infinite")
    public int defenseMaximumTime = -1;

    @SerialEntry(comment = "Price of the Trapper's Role Mine.")
    public int roleMinePrice = 100;

    @SerialEntry(comment = "How far an introvert must be from other players to see people in Instinct.")
    public int introvertDisableRange = 20;
    @SerialEntry(comment = "How long the Infected's poison takes to kill a target. (default = 1100 ticks)")
    public int infectedKillTime = 1100;
    @SerialEntry(comment = "How likely someone who is infected will cough every second of 100. (default = 5%)")
    public int infectedCoughChance = 5;

    @SerialEntry(comment = "If players with the guesser modifier can use instinct.")
    public boolean guesserCanUseInstinct = true;

    @SerialEntry(comment = "Trappers see people's names rather than roles")
    public boolean trapperSeesNames = false;
    @SerialEntry(comment = "Recons see people's names rather than roles")
    public boolean reconsSeeNames = false;

    @SerialEntry(comment = "If the Executioner can pick up dropped guns.")
    public boolean executionCanPickUpGun = true;
}