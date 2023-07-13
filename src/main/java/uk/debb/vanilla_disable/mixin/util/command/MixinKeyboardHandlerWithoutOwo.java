package uk.debb.vanilla_disable.mixin.util.command;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.debb.vanilla_disable.config.command.CommandConfigHotkeyManager;

import java.util.Objects;

@Restriction(conflict = @Condition("owo"))
@Mixin(KeyboardHandler.class)
public abstract class MixinKeyboardHandlerWithoutOwo {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("RETURN"))
    private void keyPress(CallbackInfo ci) {
        if (CommandConfigHotkeyManager.isPressed()) {
            Objects.requireNonNull(this.minecraft.player).displayClientMessage(Component.translatable("vd.key.cannot_open_gui").withStyle(ChatFormatting.RED), true);
        }
    }
}