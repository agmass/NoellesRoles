package org.agmas.noellesroles.client.mixin.executioner;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerMoodComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.RoleNameRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.harpymodloader.client.HarpymodloaderClient;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.coroner.BodyDeathReasonComponent;
import org.agmas.noellesroles.executioner.ExecutionerPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RoleNameRenderer.class)
public abstract class ExecutionerHudMixin {

    @Shadow private static float nametagAlpha;

    @Shadow private static Text nametag;


    @Inject(method = "renderHud", at = @At("HEAD"))
    private static void executionerHudRenderer(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        if (HarpymodloaderClient.hudRole == Noellesroles.EXECUTIONER && TMMClient.isPlayerSpectatingOrCreative()) {
            if (NoellesrolesClient.target == null) return;
            ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get(NoellesrolesClient.target);

            if (MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(executionerPlayerComponent.target) == null)
                return;
            context.getMatrices().push();
            context.getMatrices().translate((float) context.getScaledWindowWidth() / 2.0F, (float) context.getScaledWindowHeight() / 2.0F + 6.0F, 0.0F);
            context.getMatrices().scale(0.6F, 0.6F, 1.0F);
            Text name = Text.literal("Executioner Target: " + MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(executionerPlayerComponent.target).getProfile().getName());
            context.drawTextWithShadow(renderer, name, -renderer.getWidth(name) / 2, 32, Colors.RED);

            context.getMatrices().pop();
        }
        if (gameWorldComponent.isRole(MinecraftClient.getInstance().player, Noellesroles.EXECUTIONER) && TMMClient.isPlayerAliveAndInSurvival()) {
            if (!gameWorldComponent.getRole(MinecraftClient.getInstance().player).canUseKiller()) {
                ExecutionerPlayerComponent executionerPlayerComponent = (ExecutionerPlayerComponent) ExecutionerPlayerComponent.KEY.get(player);

                if (MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(executionerPlayerComponent.target) == null)
                    return;
                context.getMatrices().push();
                Text name = Text.literal("Executioner Target: " + MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(executionerPlayerComponent.target).getProfile().getName());
                PlayerSkinDrawer.draw(context,MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(executionerPlayerComponent.target).getSkinTextures().texture(), 2, context.getScaledWindowHeight()-14,12);
                context.drawTextWithShadow(renderer, name, 18, context.getScaledWindowHeight()-12, Colors.RED);

                context.getMatrices().pop();
                return;
            }
        }
    }

    @Inject(method = "renderHud", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getDisplayName()Lnet/minecraft/text/Text;"))
    private static void executionerGetTarget(TextRenderer renderer, ClientPlayerEntity player, DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci, @Local PlayerEntity target) {
        NoellesrolesClient.target = target;
    }
}

