package uk.debb.vanilla_disable.command.mixin.rule.block.function;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.RedStoneWireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import uk.debb.vanilla_disable.command.data.CommandDataHandler;

@Mixin(RedStoneWireBlock.class)
public abstract class MixinRedstoneWireBlock {
    @ModifyReturnValue(method = "getWireSignal", at = @At("RETURN"))
    private int getWireSignal(int original) {
        if (!CommandDataHandler.getCachedBoolean("blocks", "minecraft:redstone_wire", "works")) {
            return 0;
        }
        return original;
    }
}
