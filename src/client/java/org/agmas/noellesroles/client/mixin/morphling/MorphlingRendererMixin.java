package org.agmas.noellesroles.client.mixin.morphling;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.wathe.client.WatheClient;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;
import org.agmas.noellesroles.ConfigWorldComponent;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.agmas.noellesroles.client.renderer.MorphUtil;
import org.agmas.noellesroles.morphling.MorphlingPlayerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntityRenderer.class)
public abstract class MorphlingRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {


    public MorphlingRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Shadow public abstract Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity);

    @Inject(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    void renderMorphlingSkin(AbstractClientPlayerEntity abstractClientPlayerEntity, CallbackInfoReturnable<Identifier> cir) {
        if (NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE == null) return;
        if (WatheClient.moodComponent != null) {
            if ((ConfigWorldComponent.KEY.get(abstractClientPlayerEntity.getWorld())).insaneSeesMorphs && WatheClient.moodComponent.isLowerThanDepressed() && NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.containsKey(abstractClientPlayerEntity.getUuid())) {
                cir.setReturnValue(WatheClient.PLAYER_ENTRIES_CACHE.get(NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.get(abstractClientPlayerEntity.getUuid())).getSkinTextures().texture());
                cir.cancel();
            }
        }
        if ((MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity)).getMorphTicks() > 0 ) {
            if (abstractClientPlayerEntity.getEntityWorld().getPlayerByUuid((MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity)).disguise) != null) {
                Identifier tex = getTexture((AbstractClientPlayerEntity) abstractClientPlayerEntity.getEntityWorld().getPlayerByUuid((MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity)).disguise));
                cir.setReturnValue(tex);

                cir.cancel();
            } else {
                Log.info(LogCategory.GENERAL, "Morphling disguise is null!!!");
            }
            if (MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity).disguise.equals(MinecraftClient.getInstance().player.getUuid())) {
                cir.setReturnValue(getTexture(MinecraftClient.getInstance().player));
                cir.cancel();
            }
        }
    }
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    void applyModels(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE == null) return;
        if (WatheClient.moodComponent != null) {
            if ((ConfigWorldComponent.KEY.get(abstractClientPlayerEntity.getWorld())).insaneSeesMorphs && WatheClient.moodComponent.isLowerThanDepressed() && NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.containsKey(abstractClientPlayerEntity.getUuid())) {
                if (WatheClient.PLAYER_ENTRIES_CACHE.get(NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.get(abstractClientPlayerEntity.getUuid())).getSkinTextures().model().equals(SkinTextures.Model.SLIM)) {
                    model = MorphUtil.SLIM;
                } else {
                    model = MorphUtil.CLASSIC;
                }
            }
        }
        if ((MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity)).getMorphTicks() > 0 ) {
            if (((AbstractClientPlayerEntity) abstractClientPlayerEntity.getEntityWorld().getPlayerByUuid((MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity)).disguise)).getSkinTextures().model().equals(SkinTextures.Model.SLIM)) {
                model = MorphUtil.SLIM;
            } else {
                model = MorphUtil.CLASSIC;
            }
            if (MorphlingPlayerComponent.KEY.get(abstractClientPlayerEntity).disguise.equals(MinecraftClient.getInstance().player.getUuid())) {
                if (MinecraftClient.getInstance().player.getSkinTextures().model().equals(SkinTextures.Model.SLIM)) {
                    model = MorphUtil.SLIM;
                } else {
                    model = MorphUtil.CLASSIC;
                }
            }
        }
        if (abstractClientPlayerEntity.getSkinTextures().model().equals(SkinTextures.Model.SLIM)) {
            model = MorphUtil.SLIM;
        } else {
            model = MorphUtil.CLASSIC;
        }
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    void getModels(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        MorphUtil.CLASSIC = new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER), false);
        MorphUtil.SLIM = new PlayerEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM), true);
    }
    @WrapOperation(method = "renderArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTextures()Lnet/minecraft/client/util/SkinTextures;"))
    SkinTextures renderArm(AbstractClientPlayerEntity instance, Operation<SkinTextures> original) {
        if (NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE == null) return original.call(instance);
        if ((MorphlingPlayerComponent.KEY.get(instance)).getMorphTicks() > 0) {
            if (instance.getEntityWorld().getPlayerByUuid((MorphlingPlayerComponent.KEY.get(instance)).disguise) != null) {
                 return ((AbstractClientPlayerEntity) instance.getEntityWorld().getPlayerByUuid((MorphlingPlayerComponent.KEY.get(instance)).disguise)).getSkinTextures();
            } else {
                Log.info(LogCategory.GENERAL, "Morphling disguise is null!!!");
            }
        }
        if (WatheClient.moodComponent != null) {
            if ((ConfigWorldComponent.KEY.get(instance.getWorld())).insaneSeesMorphs && WatheClient.moodComponent.isLowerThanDepressed() && NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.containsKey(instance.getUuid())) {
                return WatheClient.PLAYER_ENTRIES_CACHE.get(NoellesrolesClient.SHUFFLED_PLAYER_ENTRIES_CACHE.get(instance.getUuid())).getSkinTextures();
            }
        }
        return original.call(instance);
    }

}
