package uk.debb.vanilla_disable.command.mixin.rule.block.falling;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.debb.vanilla_disable.command.data.DataHandler;

@Mixin(FallingBlock.class)
public abstract class MixinFallingBlock {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        String name = DataHandler.getKeyFromBlockRegistry(blockState.getBlock());
        if (!DataHandler.getCachedBoolean("blocks", name, "can_fall")) {
            ci.cancel();
        }
    }
}
