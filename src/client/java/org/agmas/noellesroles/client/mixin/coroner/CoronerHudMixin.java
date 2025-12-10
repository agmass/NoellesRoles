package org.agmas.noellesroles.client.mixin.coroner;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.api.Role;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.RoleNameRenderer;
import dev.doctor4t.trainmurdermystery.entity.PlayerBodyEntity;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.client.HarpymodloaderClient;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public abstract class CoronerHudMixin {

    @Shadow private static float nametagAlpha;

    @Shadow private static Text nametag;


    @Inject(method = "renderHud", at = @At("TAIL"))
    private static void coronerRoleNameRenderer(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        if (NoellesrolesClient.targetBody != null) {
            if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.CORONER) || gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.VULTURE) || TMMClient.isPlayerSpectatingOrCreative()) {

                context.getMatrices().push();
                context.getMatrices().translate((float)context.getScaledWindowWidth() / 2.0F, (float)context.getScaledWindowHeight() / 2.0F + 6.0F, 0.0F);
                context.getMatrices().scale(0.6F, 0.6F, 1.0F);
                PlayerMoodComponent moodComponent = (PlayerMoodComponent) PlayerMoodComponent.KEY.get(MinecraftClient.getInstance().player);
                if (moodComponent.isLowerThanMid() && TMMClient.isPlayerAliveAndInSurvival()) {
                    // Text name = Text.literal("50% sanity required to use ability");
                    Text name = Text.translatable("hud.coroner.sanity_requirements");
                    context.drawTextWithShadow(renderer, name, -renderer.getWidth(name) / 2, 32, Colors.YELLOW);
                    return;
                }
                BodyDeathReasonComponent bodyDeathReasonComponent = (BodyDeathReasonComponent) BodyDeathReasonComponent.KEY.get(NoellesrolesClient.targetBody);
                // Text name = Text.literal("Died " + NoellesrolesClient.targetBody.age/20 + "s ago to ").append(Text.translatable("death_reason." + bodyDeathReasonComponent.deathReason.getNamespace()+ "." + bodyDeathReasonComponent.deathReason.getPath()));

                Text name = Text.translatable("hud.coroner.death_info", NoellesrolesClient.targetBody.age/20).append(Text.translatable("death_reason." + bodyDeathReasonComponent.deathReason.getNamespace()+ "." + bodyDeathReasonComponent.deathReason.getPath()));
                if (bodyDeathReasonComponent.vultured) {
                    name = Text.literal("aa aaaaaa aaa aa a aaaaa aaa").formatted(Formatting.OBFUSCATED);
                }
                context.drawTextWithShadow(renderer, name, -renderer.getWidth(name) / 2, 32, Colors.RED);
                Role foundRole = TMMRoles.CIVILIAN;
                for (Role role : TMMRoles.ROLES) {
                    if (role.identifier().equals(bodyDeathReasonComponent.playerRole)) foundRole =role;
                }
                if ((TMMClient.isPlayerSpectatingOrCreative() || gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.CORONER)) && !bodyDeathReasonComponent.vultured ) {
                    Text roleInfo = Text.translatable("hud.coroner.role_info").withColor(Colors.RED).append(Text.translatable("announcement.role." + bodyDeathReasonComponent.playerRole.getPath()).withColor(foundRole.color()));
                    context.drawTextWithShadow(renderer, roleInfo, -renderer.getWidth(roleInfo) / 2, 48, Colors.WHITE);
                }
                if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.VULTURE) ) {
                    if (bodyDeathReasonComponent.vultured) {
                        Text roleInfo = Text.translatable("hud.vulture.already_consumed").withColor(Noellesroles.VULTURE.color());
                        context.drawTextWithShadow(renderer, roleInfo, -renderer.getWidth(roleInfo) / 2, 48, Colors.WHITE);
                    } else {
                        AbilityPlayerComponent abilityPlayerComponent = AbilityPlayerComponent.KEY.get(player);
                        if (abilityPlayerComponent.cooldown <= 0 && TMMClient.isPlayerAliveAndInSurvival()) {
                            Text roleInfo = Text.translatable("hud.vulture.eat", NoellesrolesClient.abilityBind.getBoundKeyLocalizedText()).withColor(Colors.RED);
                            context.drawTextWithShadow(renderer, roleInfo, -renderer.getWidth(roleInfo) / 2, 48, Colors.WHITE);
                        }
                    }
                }

                context.getMatrices().pop();
                return;
            }
        }
    }
    @Inject(method = "renderHud", at = @At(value = "INVOKE", target = "Ldev/doctor4t/trainmurdermystery/game/GameFunctions;isPlayerSpectatingOrCreative(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    private static void customRaycast(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        float range = GameFunctions.isPlayerSpectatingOrCreative(player) ? 8.0F : 2.0F;
        HitResult line = ProjectileUtil.getCollision(player, (entity) -> entity instanceof PlayerBodyEntity, (double)range);
        NoellesrolesClient.targetBody = null;
        if (line instanceof EntityHitResult ehr) {
            if (ehr.getEntity() instanceof PlayerBodyEntity playerBodyEntity) {
                NoellesrolesClient.targetBody = playerBodyEntity;
            }
        }
    }
}
