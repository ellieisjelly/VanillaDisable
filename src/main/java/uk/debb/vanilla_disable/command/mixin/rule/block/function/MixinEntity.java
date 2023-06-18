package uk.debb.vanilla_disable.command.mixin.rule.block.function;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.debb.vanilla_disable.command.data.DataHandler;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Inject(method = "onAboveBubbleCol", at = @At("HEAD"), cancellable = true)
    private void cancelAboveBubbleCol(CallbackInfo ci) {
        if (!DataHandler.getCachedBoolean("blocks", "minecraft:bubble_column", "works")) {
            ci.cancel();
        }
    }

    @Inject(method = "onInsideBubbleColumn", at = @At("HEAD"), cancellable = true)
    private void cancelInsideBubbleCol(CallbackInfo ci) {
        if (!DataHandler.getCachedBoolean("blocks", "minecraft:bubble_column", "works")) {
            ci.cancel();
        }
    }
}
