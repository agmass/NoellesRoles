package org.agmas.noellesroles.mixin.noisemaker;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerShopComponent;
import dev.doctor4t.trainmurdermystery.index.TMMItems;
import dev.doctor4t.trainmurdermystery.index.TMMSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerShopComponent.class)
public abstract class NoisemakerPlayerShopComponentMixin {
    @Shadow public int balance;

    @Shadow @Final private PlayerEntity player;

    @Shadow public abstract void sync();

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void noisemakerTryBuy(int index, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.getWorld());
        if (gameWorldComponent.isRole(player,Noellesroles.NOISEMAKER)) {
            if (index == 0) {
                if (balance >= 75) {
                    this.balance -= 75;
                    sync();
                    player.giveItemStack(TMMItems.FIRECRACKER.getDefaultStack());
                    PlayerEntity var6 = this.player;
                    if (var6 instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) var6;
                        player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(TMMSounds.UI_SHOP_BUY), SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + this.player.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
                    }
                } else {
                    this.player.sendMessage(Text.literal("Purchase Failed").formatted(Formatting.DARK_RED), true);
                    PlayerEntity var4 = this.player;
                    if (var4 instanceof ServerPlayerEntity) {
                        ServerPlayerEntity player = (ServerPlayerEntity) var4;
                        player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(TMMSounds.UI_SHOP_BUY_FAIL), SoundCategory.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + this.player.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
                    }
                }
            }
            ci.cancel();
        }
    }

}
