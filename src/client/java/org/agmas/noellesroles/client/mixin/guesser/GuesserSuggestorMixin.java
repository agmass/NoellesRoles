package org.agmas.noellesroles.client.mixin.guesser;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.doctor4t.wathe.api.WatheRoles;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.agmas.harpymodloader.Harpymodloader;
import org.agmas.noellesroles.Noellesroles;
import org.agmas.noellesroles.client.ui.guesser.GuesserRoleWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatInputSuggestor.class)
public abstract class GuesserSuggestorMixin {
    @Shadow @Final private TextFieldWidget textField;

    @Shadow public abstract void show(boolean narrateFirstSuggestion);

    @Shadow @Nullable private ChatInputSuggestor.SuggestionWindow window;

    @Shadow @Final private List<OrderedText> messages;

    @Shadow private int x;

    @Shadow private int width;

    @Shadow private boolean windowActive;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    void a(CallbackInfo ci) {
        if (textField instanceof GuesserRoleWidget) {
            messages.clear();
            WatheRoles.ROLES.forEach((m) -> {
                if (Noellesroles.KILLER_SIDED_NEUTRALS.contains(m)) return;
                GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(MinecraftClient.getInstance().player.getWorld());
                if (!gameWorldComponent.isInnocent(MinecraftClient.getInstance().player)) {
                    if (Noellesroles.KILLER_SIDED_NEUTRALS.contains(m)) return;
                    if (m.canUseKiller()) return;
                }
                if (m.identifier().getPath().startsWith(textField.getText()) || textField.getText().isEmpty()) {
                    MutableText s = Text.literal(m.identifier().getPath());
                    if (!MinecraftClient.getInstance().getLanguageManager().getLanguage().startsWith("en_")) {
                        s.append(Text.literal(" (").append((Harpymodloader.getRoleName(m)).append(")")));
                    }
                    messages.add(s.withColor(m.color()).asOrderedText());
                }
            });
            x = textField.getX();
            width = this.textField.getWidth();

            window = null;
            if (windowActive && client.options.getAutoSuggestions().getValue()) {
                show(false);
            }

            ci.cancel();
        }
    }
    @Inject(method = "show", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;sortSuggestions(Lcom/mojang/brigadier/suggestion/Suggestions;)Ljava/util/List;", shift = At.Shift.BEFORE))
    void doNotCloseInventory(boolean narrateFirstSuggestion, CallbackInfo ci, @Local(ordinal = 2) LocalIntRef j) {
        if (textField instanceof GuesserRoleWidget) {
            j.set(textField.getY() + textField.getHeight());
        }
    }
    @Inject(method = "renderMessages", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", shift = At.Shift.BEFORE))
    void doNotCloseInventory(DrawContext context, CallbackInfo ci, @Local(ordinal = 1) LocalIntRef j, @Local(ordinal = 0) int i) {
        if (textField instanceof GuesserRoleWidget) {
            j.set(textField.getY() + textField.getHeight() + 12 * i);
        }
    }
}
