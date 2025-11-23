package org.agmas.noellesroles.client;

import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.trainmurdermystery.util.ShopEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import org.agmas.noellesroles.AbilityPlayerComponent;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.agmas.noellesroles.packet.MorphC2SPacket;
import org.agmas.noellesroles.packet.SwapperC2SPacket;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.UUID;

public class SwapperPlayerWidget extends ButtonWidget{
    public final LimitedInventoryScreen screen;
    public final AbstractClientPlayerEntity disguiseTarget;

    public static UUID playerChoiceOne = null;

    public SwapperPlayerWidget(LimitedInventoryScreen screen, int x, int y, @NotNull AbstractClientPlayerEntity disguiseTarget, int index) {
        super(x, y, 16, 16, disguiseTarget.getName(), (a) -> {
            if ((AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player)).cooldown == 0) {
                if (MinecraftClient.getInstance().player.getWorld().getPlayerByUuid(disguiseTarget.getUuid()) == null) return;
                if (MinecraftClient.getInstance().player.getWorld().getPlayerByUuid(disguiseTarget.getUuid()).hasVehicle()) return;
                if (playerChoiceOne != null) {
                    ClientPlayNetworking.send(new SwapperC2SPacket(playerChoiceOne, disguiseTarget.getUuid()));
                } else {
                    playerChoiceOne = disguiseTarget.getUuid();
                    Log.info(LogCategory.GENERAL, "clicked");
                }
            }
        }, DEFAULT_NARRATION_SUPPLIER);
        this.screen = screen;
        this.disguiseTarget = disguiseTarget;
    }

    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if ((AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player)).cooldown == 0) {
            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, disguiseTarget.getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY(), 0);
            }

            context.drawTooltip(MinecraftClient.getInstance().textRenderer, disguiseTarget.getName(), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(disguiseTarget.getName()) / 2, this.getY() - 9);
        }

        if ((AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player)).cooldown > 0) {
            context.setShaderColor(0.25f,0.25f,0.25f,0.5f);
            context.drawGuiTexture(ShopEntry.Type.POISON.getTexture(), this.getX() - 7, this.getY() - 7, 30, 30);
            PlayerSkinDrawer.draw(context, disguiseTarget.getSkinTextures().texture(), this.getX(), this.getY(), 16);
            if (this.isHovered()) {
                this.drawShopSlotHighlight(context, this.getX(), this.getY(), 0);
            }

            context.drawTooltip(MinecraftClient.getInstance().textRenderer, disguiseTarget.getName(), this.getX() - 4 - MinecraftClient.getInstance().textRenderer.getWidth(disguiseTarget.getName()) / 2, this.getY() - 9);

            context.setShaderColor(1f,1f,1f,1f);
            context.drawText(MinecraftClient.getInstance().textRenderer, AbilityPlayerComponent.KEY.get(MinecraftClient.getInstance().player).cooldown/20+"",this.getX(),this.getY(), Color.RED.getRGB(),true);

        }

    }

    private void drawShopSlotHighlight(DrawContext context, int x, int y, int z) {
        int color = -1862287543;
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y, x + 16, y + 14, color, color, z);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 14, x + 15, y + 15, color, color, z);
        context.fillGradient(RenderLayer.getGuiOverlay(), x, y + 15, x + 14, y + 16, color, color, z);
    }

    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
    }

}
