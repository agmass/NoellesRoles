package org.agmas.noellesroles.mixin.jester;

import dev.doctor4t.trainmurdermystery.cca.GameWorldComponent;
import dev.doctor4t.trainmurdermystery.cca.PlayerPsychoComponent;
import dev.doctor4t.trainmurdermystery.game.GameConstants;
import dev.doctor4t.trainmurdermystery.game.GameFunctions;
import dev.doctor4t.trainmurdermystery.item.RevolverItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.agmas.noellesroles.ModItems;
import org.agmas.noellesroles.Noellesroles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RevolverItem.class)
public abstract class FakeRevolverMixin {

    @Inject(method = "getGunTarget", at = @At("HEAD"), cancellable = true)
    private static void jesterFakeGun(PlayerEntity user, CallbackInfoReturnable<HitResult> cir) {
        if (user.getMainHandStack().isOf(ModItems.FAKE_REVOLVER)) {
            cir.setReturnValue(null);
        }
    }

}
