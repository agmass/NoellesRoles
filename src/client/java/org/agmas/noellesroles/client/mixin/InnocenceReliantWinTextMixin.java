package org.agmas.noellesroles.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.doctor4t.trainmurdermystery.api.TMMRoles;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.RoleAnnouncementTexts;
import dev.doctor4t.trainmurdermystery.client.gui.RoleNameRenderer;
import dev.doctor4t.trainmurdermystery.client.gui.RoundTextRenderer;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(RoleAnnouncementTexts.RoleAnnouncementText.class)
public abstract class InnocenceReliantWinTextMixin {

    @Inject(method = "getEndText", at = @At("TAIL"), cancellable = true)
    private void b(GameFunctions.WinStatus status, Text winner, CallbackInfoReturnable<Text> cir) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
        TMMRoles.Role role = gameWorldComponent.getRole(MinecraftClient.getInstance().player);
        if (gameWorldComponent.getRole(MinecraftClient.getInstance().player) != null) {
            switch (status) {
                case TIME:
                case PASSENGERS:
                    cir.setReturnValue(role.isInnocent() ? RoleAnnouncementTexts.CIVILIAN.winText : RoleAnnouncementTexts.KILLER.getLoseText());
                    cir.cancel();
                    break;
                case KILLERS:
                    cir.setReturnValue(role.isInnocent() ? RoleAnnouncementTexts.CIVILIAN.getLoseText() : RoleAnnouncementTexts.KILLER.winText);
                    cir.cancel();
            }
        }
    }
}
