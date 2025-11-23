package org.agmas.noellesroles;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.agmas.noellesroles.packet.MorphC2SPacket;
import org.agmas.noellesroles.packet.SwapperC2SPacket;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

public class Noellesroles implements ModInitializer {

    public static String MOD_ID = "noellesroles";

    public static HashMap<Identifier, Integer> rolePlayerCaps = new HashMap<>();
    public static HashMap<PlayerEntity, TMMRoles.Role> forceRoles = new HashMap<>();
    public static HashMap<RoleAnnouncementTexts.RoleAnnouncementText, Boolean> roleAnnouncementIsEvil = new HashMap<>();

    public static Identifier JESTER_ID = Identifier.of(MOD_ID, "jester");
    public static Identifier MORPHLING_ID = Identifier.of(MOD_ID, "morphling");
    public static Identifier HOST_ID = Identifier.of(MOD_ID, "host");
    public static Identifier BARTENDER_ID = Identifier.of(MOD_ID, "bartender");
    public static Identifier NOISEMAKER_ID = Identifier.of(MOD_ID, "noisemaker");
    public static Identifier PHANTOM_ID = Identifier.of(MOD_ID, "phantom");
    public static Identifier AWESOME_BINGLUS_ID = Identifier.of(MOD_ID, "awesome_binglus");
    public static Identifier SWAPPER_ID = Identifier.of(MOD_ID, "swapper");

    public static HashMap<TMMRoles.Role, RoleAnnouncementTexts.RoleAnnouncementText> roleRoleAnnouncementTextHashMap = new HashMap<>();
    public static TMMRoles.Role JESTER = trueRegisterRole(new TMMRoles.Role(JESTER_ID,new Color(255,86,243).getRGB() ,false,false));
    public static TMMRoles.Role MORPHLING =trueRegisterRole(new TMMRoles.Role(MORPHLING_ID, new Color(170, 2, 61).getRGB(),false,true));
    public static TMMRoles.Role HOST =trueRegisterRole(new TMMRoles.Role(HOST_ID, new Color(255, 205, 84).getRGB(),true,false));
    public static TMMRoles.Role AWESOME_BINGLUS = trueRegisterRole(new TMMRoles.Role(AWESOME_BINGLUS_ID, new Color(155, 255, 168).getRGB(),true,false));

    public static TMMRoles.Role BARTENDER =trueRegisterRole(new TMMRoles.Role(BARTENDER_ID, new Color(217,241,240).getRGB(),true,false));
    public static TMMRoles.Role NOISEMAKER =trueRegisterRole(new TMMRoles.Role(NOISEMAKER_ID, new Color(200, 255, 0).getRGB(),true,false));
    public static TMMRoles.Role SWAPPER = trueRegisterRole(new TMMRoles.Role(SWAPPER_ID, new Color(63, 0, 255).getRGB(),false,true));
    public static TMMRoles.Role PHANTOM =trueRegisterRole(new TMMRoles.Role(PHANTOM_ID, new Color(80, 5, 5, 192).getRGB(),false,true));


    public static final CustomPayload.Id<MorphC2SPacket> MORPH_PACKET = MorphC2SPacket.ID;
    public static final CustomPayload.Id<SwapperC2SPacket> SWAP_PACKET = SwapperC2SPacket.ID;
    public static final CustomPayload.Id<AbilityC2SPacket> ABILITY_PACKET = AbilityC2SPacket.ID;
    @Override
    public void onInitialize() {
        ModItems.init();

        rolePlayerCaps.put(JESTER_ID, 1);
        rolePlayerCaps.put(HOST_ID, 1);
        NoellesRolesConfig.HANDLER.load();

        PayloadTypeRegistry.playC2S().register(MorphC2SPacket.ID, MorphC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SwapperC2SPacket.ID, SwapperC2SPacket.CODEC);

        registerPackets();
        registerCommands();
    }


    public static TMMRoles.Role trueRegisterRole(TMMRoles.Role role) {
        TMMRoles.registerRole(role);
        try {
            Constructor<RoleAnnouncementTexts.RoleAnnouncementText> constructor = RoleAnnouncementTexts.RoleAnnouncementText.class.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            RoleAnnouncementTexts.RoleAnnouncementText announcementText = constructor.newInstance(role.identifier().getPath(), role.color());
            RoleAnnouncementTexts.registerRoleAnnouncementText(announcementText);
            roleRoleAnnouncementTextHashMap.put(role,announcementText);
            roleAnnouncementIsEvil.put(announcementText, !role.canUseKiller());;
            return role;
        } catch (Exception e) {
            Log.info(LogCategory.GENERAL, e.getMessage());
        }
        return null;
    }


    public static boolean isAnyModdedRole(PlayerEntity player) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        return gameWorldComponent.getRole(player) != null && (gameWorldComponent.getRole(player) != TMMRoles.CIVILIAN && gameWorldComponent.getRole(player) != TMMRoles.VIGILANTE && gameWorldComponent.getRole(player) != TMMRoles.KILLER);
    }

    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.MORPH_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());

            if (gameWorldComponent.isRole(context.player(), MORPHLING)) {
                MorphlingPlayerComponent morphlingPlayerComponent = (MorphlingPlayerComponent) MorphlingPlayerComponent.KEY.get(context.player());
                morphlingPlayerComponent.startMorph(payload.player());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.SWAP_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), SWAPPER)) {
                if (payload.player() != null) {
                    if (context.player().getWorld().getPlayerByUuid(payload.player()) != null) {
                        if (payload.player2() != null) {
                            if (context.player().getWorld().getPlayerByUuid(payload.player2()) != null) {
                                Vec3d swapperPos = context.player().getWorld().getPlayerByUuid(payload.player2()).getPos();
                                Vec3d swappedPos = context.player().getWorld().getPlayerByUuid(payload.player()).getPos();
                                context.player().getWorld().getPlayerByUuid(payload.player2()).refreshPositionAfterTeleport(swappedPos.x, swappedPos.y, swappedPos.z);
                                context.player().getWorld().getPlayerByUuid(payload.player()).refreshPositionAfterTeleport(swapperPos.x, swapperPos.y, swapperPos.z);
                            }
                        }
                    }
                }
                AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(1, 0);
                abilityPlayerComponent.sync();
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.ABILITY_PACKET, (payload, context) -> {
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), PHANTOM) && abilityPlayerComponent.cooldown <= 0) {
                context.player().addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 30 * 20,0,true,false,true));
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(1, 30);
            }
        });
    }

    public void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("forceModdedRole").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("player", EntityArgumentType.player()).then(CommandManager.argument("role", StringArgumentType.string()).executes(commandContext -> {
                ServerPlayerEntity entity = EntityArgumentType.getPlayer(commandContext, "player");
                String roleName = StringArgumentType.getString(commandContext, "role");
                for (TMMRoles.Role role : TMMRoles.ROLES) {
                    if (role.identifier().getPath().equals(roleName)) {
                        forceRoles.put(entity,role);
                        commandContext.getSource().sendMessage(Text.literal("Forced Role"));
                        return 1;
                    }
                }
                commandContext.getSource().sendMessage(Text.literal("Invalid role/player name").withColor(Color.RED.getRGB()));
                return 1;
            }))));
            dispatcher.register(CommandManager.literal("banRole").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("roleName", StringArgumentType.string()).executes((commandContext)->{

                String roleName = StringArgumentType.getString(commandContext, "roleName");
                for (TMMRoles.Role role : TMMRoles.ROLES) {
                    if (role.identifier().getPath().equals(roleName)) {
                        NoellesRolesConfig.HANDLER.instance().disabled.add(roleName);
                        NoellesRolesConfig.HANDLER.save();
                        commandContext.getSource().sendMessage(Text.literal("Banned Role"));
                        return 1;
                    }
                }
                commandContext.getSource().sendMessage(Text.literal("Invalid role name, use /getRoleNames to get role names.").withColor(Color.RED.getRGB()));
                return 1;
            })));
            dispatcher.register(CommandManager.literal("unbanRole").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).then(CommandManager.argument("roleName", StringArgumentType.string()).executes((commandContext)->{

                String roleName = StringArgumentType.getString(commandContext, "roleName");
                for (TMMRoles.Role role : TMMRoles.ROLES) {
                    if (role.identifier().getPath().equals(roleName) && NoellesRolesConfig.HANDLER.instance().disabled.contains(roleName)) {
                        NoellesRolesConfig.HANDLER.instance().disabled.remove(roleName);
                        NoellesRolesConfig.HANDLER.save();
                        commandContext.getSource().sendMessage(Text.literal("Unbanned Role"));
                        return 1;
                    }
                }
                commandContext.getSource().sendMessage(Text.literal("Invalid role name (or role is not in the ban list), use /getRoleNames to get role names.").withColor(Color.RED.getRGB()));
                return 1;
            })));
            dispatcher.register(CommandManager.literal("getRoleNames").executes((commandContext -> {
                for (TMMRoles.Role role : TMMRoles.ROLES) {
                    commandContext.getSource().sendMessage(Text.literal(role.identifier().getPath()).withColor(role.color()).append(NoellesRolesConfig.HANDLER.instance().disabled.contains(role.identifier().getPath()) ? Text.literal(" (BANNED)").formatted(Formatting.RED) : Text.literal("")));
                }
                return 1;
            })));
        });
    }



}
