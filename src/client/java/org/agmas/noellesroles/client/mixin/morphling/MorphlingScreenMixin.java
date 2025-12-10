package org.agmas.noellesroles.client.mixin.morphling;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.client.gui.RoundTextRenderer;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedHandledScreen;
import dev.doctor4t.trainmurdermystery.client.gui.screen.ingame.LimitedInventoryScreen;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.util.AnnounceWelcomePayload;
import dev.doctor4t.trainmurdermystery.util.ShopEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.MorphlingPlayerWidget;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;


@Mixin(LimitedInventoryScreen.class)
public abstract class MorphlingScreenMixin extends LimitedHandledScreen<PlayerScreenHandler>{
    @Shadow @Final public ClientPlayerEntity player;

    public MorphlingScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }


    @Inject(method = "init", at = @At("TAIL"))
    void renderMorphlingHeads(CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = (GameWorldComponent) GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(player,Noellesroles.MORPHLING)) {
            List<AbstractClientPlayerEntity> entries = MinecraftClient.getInstance().world.getPlayers();
            entries.removeIf((e) -> e.getUuid().equals(player.getUuid()));
            int apart = 36;
            int x = ((LimitedInventoryScreen)(Object)this).width / 2 - (entries.size()) * apart / 2 + 9;
            int shouldBeY = (((LimitedInventoryScreen)(Object)this).height - 32) / 2;
            int y = shouldBeY + 80;

            for(int i = 0; i < entries.size(); ++i) {
                MorphlingPlayerWidget child = new MorphlingPlayerWidget(((LimitedInventoryScreen)(Object)this), x + apart * i, y, entries.get(i), i);
                addDrawableChild(child);
            }
        }
    }

}
