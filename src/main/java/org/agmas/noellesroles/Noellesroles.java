package org.agmas.noellesroles;

import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerPsychoComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent;
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.trainmurdermystery.client.util.TMMItemTooltips;
import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import dev.doctor4t.trainmurdermystery.event.AllowPlayerDeath;
import dev.doctor4t.trainmurdermystery.event.CanSeePoison;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import dev.doctor4t.trainmurdermystery.util.AnnounceWelcomePayload;
import dev.doctor4t.trainmurdermystery.util.ShopEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Vec3d;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.config.HarpyModLoaderConfig;
import org.agmas.harpymodloader.events.ModdedRoleAssigned;
import org.agmas.noellesroles.bartender.BartenderPlayerComponent;
import org.agmas.noellesroles.config.NoellesRolesConfig;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.agmas.noellesroles.framing.FramingShopEntry;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.AbilityC2SPacket;
import org.agmas.noellesroles.packet.MorphC2SPacket;
import org.agmas.noellesroles.packet.SwapperC2SPacket;
import org.agmas.noellesroles.packet.VultureEatC2SPacket;
import org.agmas.noellesroles.recaller.RecallerPlayerComponent;
import org.agmas.noellesroles.voodoo.VoodooPlayerComponent;
import org.agmas.noellesroles.vulture.VulturePlayerComponent;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;

public class Noellesroles implements ModInitializer {

    public static String MOD_ID = "noellesroles";


    public static Identifier JESTER_ID = Identifier.of(MOD_ID, "jester");
    public static Identifier MORPHLING_ID = Identifier.of(MOD_ID, "morphling");
    public static Identifier CONDUCTOR_ID = Identifier.of(MOD_ID, "conductor");
    public static Identifier BARTENDER_ID = Identifier.of(MOD_ID, "bartender");
    public static Identifier NOISEMAKER_ID = Identifier.of(MOD_ID, "noisemaker");
    public static Identifier PHANTOM_ID = Identifier.of(MOD_ID, "phantom");
    public static Identifier AWESOME_BINGLUS_ID = Identifier.of(MOD_ID, "awesome_binglus");
    public static Identifier SWAPPER_ID = Identifier.of(MOD_ID, "swapper");
    public static Identifier VOODOO_ID = Identifier.of(MOD_ID, "voodoo");
    public static Identifier TRAPPER_ID = Identifier.of(MOD_ID, "trapper");
    public static Identifier CORONER_ID = Identifier.of(MOD_ID, "coroner");
    public static Identifier RECALLER_ID = Identifier.of(MOD_ID, "recaller");
    public static Identifier EXECUTIONER_ID = Identifier.of(MOD_ID, "executioner");
    public static Identifier VULTURE_ID = Identifier.of(MOD_ID, "vulture");
    public static Identifier BETTER_VIGILANTE_ID = Identifier.of(MOD_ID, "better_vigilante");
    public static Identifier THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID = Identifier.of(MOD_ID, "the_insane_damned_paranoid_killer");

    public static HashMap<Role, RoleAnnouncementTexts.RoleAnnouncementText> roleRoleAnnouncementTextHashMap = new HashMap<>();
    public static Role JESTER = TMMRoles.registerRole(new Role(JESTER_ID,new Color(255,86,243).getRGB() ,false,false, Role.MoodType.FAKE,Integer.MAX_VALUE,true));
    public static Role MORPHLING =TMMRoles.registerRole(new Role(MORPHLING_ID, new Color(170, 2, 61).getRGB(),false,true, Role.MoodType.FAKE,Integer.MAX_VALUE,true));
    public static Role CONDUCTOR =TMMRoles.registerRole(new Role(CONDUCTOR_ID, new Color(255, 205, 84).getRGB(),true,false, Role.MoodType.REAL,TMMRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role AWESOME_BINGLUS = TMMRoles.registerRole(new Role(AWESOME_BINGLUS_ID, new Color(155, 255, 168).getRGB(),true,false, Role.MoodType.REAL,TMMRoles.CIVILIAN.getMaxSprintTime(),false));

    public static Role BARTENDER =TMMRoles.registerRole(new Role(BARTENDER_ID, new Color(217,241,240).getRGB(),true,false, Role.MoodType.REAL,TMMRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role NOISEMAKER =TMMRoles.registerRole(new Role(NOISEMAKER_ID, new Color(200, 255, 0).getRGB(),true,false, Role.MoodType.REAL,TMMRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role SWAPPER = TMMRoles.registerRole(new Role(SWAPPER_ID, new Color(57, 4, 170).getRGB(),false,true, Role.MoodType.FAKE,Integer.MAX_VALUE,true));
    public static Role PHANTOM =TMMRoles.registerRole(new Role(PHANTOM_ID, new Color(80, 5, 5, 192).getRGB(),false,true, Role.MoodType.FAKE,Integer.MAX_VALUE,true));

    public static Role VOODOO =TMMRoles.registerRole(new Role(VOODOO_ID, new Color(128, 114, 253).getRGB(),true,false,Role.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES =TMMRoles.registerRole(new Role(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID, new Color(255, 0, 0, 192).getRGB(),false,true, Role.MoodType.FAKE,Integer.MAX_VALUE,true));
    //public static Role TRAPPER =TMMRoles.registerRole(new Role(TRAPPER_ID, new Color(132, 186, 167).getRGB(),true,false,Role.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(),false));
    public static Role CORONER =TMMRoles.registerRole(new Role(CORONER_ID, new Color(122, 122, 122).getRGB(),true,false,Role.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(),false));

    public static Role EXECUTIONER =TMMRoles.registerRole(new Role(EXECUTIONER_ID, new Color(74, 27, 5).getRGB(),false,false,Role.MoodType.FAKE, TMMRoles.CIVILIAN.getMaxSprintTime(),true));
    public static Role RECALLER = TMMRoles.registerRole(new Role(RECALLER_ID, new Color(158, 255, 255).getRGB(),true,false,Role.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(),false));

    public static Role VULTURE =TMMRoles.registerRole(new Role(VULTURE_ID, new Color(181, 103, 0).getRGB(),false,false,Role.MoodType.FAKE, TMMRoles.CIVILIAN.getMaxSprintTime(),true));
    public static Role BETTER_VIGILANTE =TMMRoles.registerRole(new Role(BETTER_VIGILANTE_ID, new Color(0, 255, 255).getRGB(),true,false,Role.MoodType.REAL, TMMRoles.CIVILIAN.getMaxSprintTime(),false));

    public static final CustomPayload.Id<MorphC2SPacket> MORPH_PACKET = MorphC2SPacket.ID;
    public static final CustomPayload.Id<SwapperC2SPacket> SWAP_PACKET = SwapperC2SPacket.ID;
    public static final CustomPayload.Id<AbilityC2SPacket> ABILITY_PACKET = AbilityC2SPacket.ID;
    public static final CustomPayload.Id<VultureEatC2SPacket> VULTURE_PACKET = VultureEatC2SPacket.ID;
    public static final ArrayList<Role> VANNILA_ROLES = new ArrayList<>();
    public static final ArrayList<Identifier> VANNILA_ROLE_IDS = new ArrayList<>();

    public static ArrayList<ShopEntry> FRAMING_ROLES_SHOP = new ArrayList<>();

    @Override
    public void onInitialize() {
        VANNILA_ROLES.add(TMMRoles.KILLER);
        VANNILA_ROLES.add(TMMRoles.VIGILANTE);
        VANNILA_ROLES.add(TMMRoles.CIVILIAN);
        VANNILA_ROLES.add(TMMRoles.LOOSE_END);

        VANNILA_ROLE_IDS.add(TMMRoles.LOOSE_END.identifier());
        VANNILA_ROLE_IDS.add(TMMRoles.VIGILANTE.identifier());
        VANNILA_ROLE_IDS.add(TMMRoles.CIVILIAN.identifier());
        VANNILA_ROLE_IDS.add(TMMRoles.KILLER.identifier());

        FRAMING_ROLES_SHOP.add(new FramingShopEntry(TMMItems.LOCKPICK.getDefaultStack(), 50, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(ModItems.DELUSION_VIAL.getDefaultStack(), 30, ShopEntry.Type.POISON));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(TMMItems.FIRECRACKER.getDefaultStack(), 5, ShopEntry.Type.TOOL));
        FRAMING_ROLES_SHOP.add(new FramingShopEntry(TMMItems.NOTE.getDefaultStack(), 5, ShopEntry.Type.TOOL));

        NoellesRolesConfig.HANDLER.load();
        ModItems.init();

        Harpymodloader.setRoleMaximum(CONDUCTOR_ID,1);
        Harpymodloader.setRoleMaximum(EXECUTIONER_ID,1);
        Harpymodloader.setRoleMaximum(VULTURE_ID,1);
        Harpymodloader.setRoleMaximum(JESTER_ID,1);
        Harpymodloader.setRoleMaximum(BETTER_VIGILANTE_ID,1);

        PayloadTypeRegistry.playC2S().register(MorphC2SPacket.ID, MorphC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.ID, AbilityC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SwapperC2SPacket.ID, SwapperC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(VultureEatC2SPacket.ID, VultureEatC2SPacket.CODEC);

        registerEvents();

        registerPackets();
        //NoellesRolesEntities.init();

    }



    public void registerEvents() {
        AllowPlayerDeath.EVENT.register(((playerEntity, identifier) -> {
            if (identifier == GameConstants.DeathReasons.FELL_OUT_OF_TRAIN) return true;
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(playerEntity.getWorld());
            if (gameWorldComponent.isRole(playerEntity,Noellesroles.JESTER)) {
                PlayerPsychoComponent component =  PlayerPsychoComponent.KEY.get(playerEntity);
                if (component.getPsychoTicks() > GameConstants.getInTicks(0,44)) {
                    return false;
                }
            }
            BartenderPlayerComponent bartenderPlayerComponent = BartenderPlayerComponent.KEY.get(playerEntity);
            if (bartenderPlayerComponent.armor > 0) {
                playerEntity.getWorld().playSound(playerEntity, playerEntity.getBlockPos(), TMMSounds.ITEM_PSYCHO_ARMOUR, SoundCategory.MASTER, 5.0F, 1.0F);
                bartenderPlayerComponent.armor--;
                return false;
            }
            return true;
        }));
        CanSeePoison.EVENT.register((player)->{
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
            if (gameWorldComponent.isRole((PlayerEntity) player, Noellesroles.BARTENDER)) {
                return true;
            }
            return false;
        });
        ModdedRoleAssigned.EVENT.register((player,role)->{
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(player);
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
            abilityPlayerComponent.cooldown = NoellesRolesConfig.HANDLER.instance().generalCooldownTicks;
            if (role.equals(EXECUTIONER)) {
                ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get(player);
                executionerPlayerComponent.won = false;
                executionerPlayerComponent.reset();
                executionerPlayerComponent.sync();
            }
            if (role.equals(VULTURE)) {
                VulturePlayerComponent vulturePlayerComponent = VulturePlayerComponent.KEY.get(player);
                vulturePlayerComponent.reset();
                vulturePlayerComponent.bodiesRequired = (int)((player.getWorld().getPlayers().size()/3f) - Math.floor(player.getWorld().getPlayers().size()/6f));
                vulturePlayerComponent.sync();
            }
            if (role.equals(BETTER_VIGILANTE)) {
                player.giveItemStack(TMMItems.GRENADE.getDefaultStack());
            }
            if (role.equals(JESTER)) {
                player.giveItemStack(ModItems.FAKE_KNIFE.getDefaultStack());
                player.giveItemStack(ModItems.FAKE_REVOLVER.getDefaultStack());
            }
            if (role.equals(CONDUCTOR)) {
                player.giveItemStack(ModItems.MASTER_KEY.getDefaultStack());
            }
            if (role.equals(AWESOME_BINGLUS)) {
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
        });
        ServerTickEvents.END_SERVER_TICK.register(((server) -> {
            if (server.getPlayerManager().getCurrentPlayerCount() >= 8) {
                Harpymodloader.setRoleMaximum(VULTURE,1);
            } else {
                Harpymodloader.setRoleMaximum(VULTURE,0);
            }
        }));
        if (!NoellesRolesConfig.HANDLER.instance().shitpostRoles) {
            HarpyModLoaderConfig.HANDLER.load();
            if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(AWESOME_BINGLUS_ID.getPath())) {
                HarpyModLoaderConfig.HANDLER.instance().disabled.add(AWESOME_BINGLUS_ID.getPath());
            }
            if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(BETTER_VIGILANTE_ID.getPath())) {
                HarpyModLoaderConfig.HANDLER.instance().disabled.add(BETTER_VIGILANTE_ID.getPath());
            }
            if (!HarpyModLoaderConfig.HANDLER.instance().disabled.contains(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID.getPath())) {
                HarpyModLoaderConfig.HANDLER.instance().disabled.add(THE_INSANE_DAMNED_PARANOID_KILLER_OF_DOOM_DEATH_DESTRUCTION_AND_WAFFLES_ID.getPath());
            }
            HarpyModLoaderConfig.HANDLER.save();
        }

    }


    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.MORPH_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());

            if (payload.player() == null) return;
            if (abilityPlayerComponent.cooldown > 0) return;
            if (context.player().getWorld().getPlayerByUuid(payload.player()) == null) return;

            if (gameWorldComponent.isRole(context.player(), VOODOO)) {
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 30);
                abilityPlayerComponent.sync();
                VoodooPlayerComponent voodooPlayerComponent = (VoodooPlayerComponent) VoodooPlayerComponent.KEY.get(context.player());
                voodooPlayerComponent.setTarget(payload.player());

            }
            if (gameWorldComponent.isRole(context.player(), MORPHLING)) {
                MorphlingPlayerComponent morphlingPlayerComponent = (MorphlingPlayerComponent) MorphlingPlayerComponent.KEY.get(context.player());
                morphlingPlayerComponent.startMorph(payload.player());
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.VULTURE_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            AbilityPlayerComponent abilityPlayerComponent = (AbilityPlayerComponent) AbilityPlayerComponent.KEY.get(context.player());

            if (gameWorldComponent.isRole(context.player(), VULTURE) && GameFunctions.isPlayerAliveAndSurvival(context.player())) {
                if (abilityPlayerComponent.cooldown > 0) return;
                abilityPlayerComponent.sync();
                List<PlayerBodyEntity> playerBodyEntities = context.player().getWorld().getEntitiesByType(TypeFilter.equals(PlayerBodyEntity.class), context.player().getBoundingBox().expand(10), (playerBodyEntity -> {
                    return playerBodyEntity.getUuid().equals(payload.playerBody());
                }));
                if (!playerBodyEntities.isEmpty()) {
                    BodyDeathReasonComponent bodyDeathReasonComponent = BodyDeathReasonComponent.KEY.get(playerBodyEntities.getFirst());
                    if (!bodyDeathReasonComponent.vultured) {
                        abilityPlayerComponent.cooldown = GameConstants.getInTicks(0, 20);
                        VulturePlayerComponent vulturePlayerComponent = VulturePlayerComponent.KEY.get(context.player());
                        vulturePlayerComponent.bodiesEaten++;
                        vulturePlayerComponent.sync();
                        context.player().playSound(SoundEvents.ENTITY_PLAYER_BURP, 1.0F, 0.5F);
                        context.player().addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 2));
                        if (vulturePlayerComponent.bodiesEaten >= vulturePlayerComponent.bodiesRequired) {
                            ArrayList<Role> shuffledKillerRoles = new ArrayList<>(TMMRoles.ROLES);
                            shuffledKillerRoles.removeIf(role -> Harpymodloader.VANNILA_ROLES.contains(role) || !role.canUseKiller() || HarpyModLoaderConfig.HANDLER.instance().disabled.contains(role.identifier().getPath()));
                            if (shuffledKillerRoles.isEmpty()) shuffledKillerRoles.add(TMMRoles.KILLER);
                            Collections.shuffle(shuffledKillerRoles);

                            PlayerShopComponent playerShopComponent = (PlayerShopComponent) PlayerShopComponent.KEY.get(context.player());
                            gameWorldComponent.addRole(context.player(),shuffledKillerRoles.getFirst());
                            ModdedRoleAssigned.EVENT.invoker().assignModdedRole(context.player(),shuffledKillerRoles.getFirst());
                            playerShopComponent.setBalance(100);
                            if (Harpymodloader.VANNILA_ROLES.contains(gameWorldComponent.getRole(context.player()))) {
                                ServerPlayNetworking.send((ServerPlayerEntity) context.player(), new AnnounceWelcomePayload(RoleAnnouncementTexts.ROLE_ANNOUNCEMENT_TEXTS.indexOf(TMMRoles.KILLER), gameWorldComponent.getAllKillerTeamPlayers().size(), 0));
                            } else {
                                ServerPlayNetworking.send((ServerPlayerEntity) context.player(), new AnnounceWelcomePayload(RoleAnnouncementTexts.ROLE_ANNOUNCEMENT_TEXTS.indexOf(Harpymodloader.autogeneratedAnnouncements.get(gameWorldComponent.getRole(context.player()))), gameWorldComponent.getAllKillerTeamPlayers().size(), 0));
                            }
                        }

                        bodyDeathReasonComponent.vultured = true;
                        bodyDeathReasonComponent.sync();
                    }
                }

            }
        });
        ServerPlayNetworking.registerGlobalReceiver(Noellesroles.SWAP_PACKET, (payload, context) -> {
            GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(context.player().getWorld());
            if (gameWorldComponent.isRole(context.player(), SWAPPER)) {
                if (payload.player() != null) {
                    if (context.player().getWorld().getPlayerByUuid(payload.player()) != null) {
                        if (payload.player2() != null) {
                            if (context.player().getWorld().getPlayerByUuid(payload.player2()) != null) {
                                PlayerEntity player1 = context.player().getWorld().getPlayerByUuid(payload.player2());
                                PlayerEntity player2 = context.player().getWorld().getPlayerByUuid(payload.player());
                                Vec3d swapperPos = context.player().getWorld().getPlayerByUuid(payload.player2()).getPos();
                                Vec3d swappedPos = context.player().getWorld().getPlayerByUuid(payload.player()).getPos();
                                if (!context.player().getWorld().isSpaceEmpty(player1)) return;
                                if (!context.player().getWorld().isSpaceEmpty(player2)) return;
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
            if (gameWorldComponent.isRole(context.player(), RECALLER) && abilityPlayerComponent.cooldown <= 0) {
                RecallerPlayerComponent recallerPlayerComponent = RecallerPlayerComponent.KEY.get(context.player());
                PlayerShopComponent playerShopComponent = PlayerShopComponent.KEY.get(context.player());
                if (!recallerPlayerComponent.placed) {
                    abilityPlayerComponent.cooldown = GameConstants.getInTicks(0,10);
                    recallerPlayerComponent.setPosition();
                }
                else if (playerShopComponent.balance >= 100) {
                    playerShopComponent.balance -= 100;
                    playerShopComponent.sync();
                    abilityPlayerComponent.cooldown = GameConstants.getInTicks(0,30);
                    recallerPlayerComponent.teleport();
                }

            }
            if (gameWorldComponent.isRole(context.player(), PHANTOM) && abilityPlayerComponent.cooldown <= 0) {
                context.player().addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 30 * 20,0,true,false,true));
                abilityPlayerComponent.cooldown = GameConstants.getInTicks(1, 30);
            }
        });
    }



}
