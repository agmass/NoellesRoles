package org.agmas.noellesroles.client.mixin.voodoo;

import com.mojang.authlib.GameProfile;
import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.client.TMMClient;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.SwapperPlayerWidget;
import org.agmas.noellesroles.client.VoodooPlayerWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Mixin(LimitedInventoryScreen.class)
public abstract class VoodoScreenMixin extends LimitedHandledScreen<PlayerScreenHandler>{
    @Shadow @Final public ClientPlayerEntity player;

    public VoodoScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    @Inject(method = "render", at = @At("HEAD"))
    void a(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        ConfigWorldComponent configWorldComponent = (ConfigWorldComponent) ConfigWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(player,Noellesroles.VOODOO)) {
            int y = (((LimitedInventoryScreen)(Object)this).height - 32) / 2;
            int x = ((LimitedInventoryScreen)(Object)this).width / 2;
            if (!configWorldComponent.naturalVoodoosAllowed) {
                // Text text = Text.literal("Voodoo deaths must be triggered by another player!");
                Text name = Text.translatable("hud.voodoo.deaths_must_be_triggered_by_another_player");
                context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, x - (MinecraftClient.getInstance().textRenderer.getWidth(text)/2), y + 40, Color.RED.getRGB());
            }
        }
    }

    @Inject(method = "init", at = @At("HEAD"))
    void b(CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(player,Noellesroles.VOODOO)) {
            List<UUID> entries = new ArrayList<>(MinecraftClient.getInstance().player.networkHandler.getPlayerUuids());
            entries.removeIf((e) -> e.equals(player.getUuid()));
            int apart = 36;
            int x = ((LimitedInventoryScreen)(Object)this).width / 2 - (entries.size()) * apart / 2 + 9;
            int shouldBeY = (((LimitedInventoryScreen)(Object)this).height - 32) / 2;
            int y = shouldBeY + 80;

            for(int i = 0; i < entries.size(); ++i) {
                VoodooPlayerWidget child = new VoodooPlayerWidget(((LimitedInventoryScreen)(Object)this), x + apart * i, y, entries.get(i), MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(entries.get(i)), player.getWorld(), i);
                addDrawableChild(child);
            }
        }
    }

}
