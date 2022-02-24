package uk.debb.vanilla_disable.mixin.fluids;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.debb.vanilla_disable.gamerules.RegisterGamerules;

@Mixin(LavaFluid.class)
public abstract class MixinLavaFluid {
    /**
     * @author DragonEggBedrockBreaking
     * @reason edit how far the fluid flows
     * @param world the world
     * @param cir the returnable callback info
     */
    @Inject(method = "getDropOff", at = @At("HEAD"), cancellable = true)
    private void getLavaDropOff(LevelReader world, CallbackInfoReturnable<Integer> cir) {
        if (RegisterGamerules.getServer() == null) return;
        if (world instanceof Level) {
            if (world.dimensionType().ultraWarm()) {
                cir.setReturnValue(((Level) world).getGameRules().getBoolean(RegisterGamerules.LAVA_REACHES_FAR_IN_NETHER) ? 1 : 2);
            } else {
                cir.setReturnValue(((Level) world).getGameRules().getBoolean(RegisterGamerules.LAVA_REACHES_FAR) ? 1 : 2);
            }
        }
    }

    /**
     * @author DragonEggBedrockBreaking
     * @reason edit how fast the fluid flows
     * @param world the world
     * @param cir the returnable callback info
     */
    @Inject(method = "getTickDelay", at = @At("HEAD"), cancellable = true)
    private void getLavaTickDelay(LevelReader world, CallbackInfoReturnable<Integer> cir) {
        if (RegisterGamerules.getServer() == null) return;
        if (world instanceof Level) {
            if (world.dimensionType().ultraWarm()) {
                cir.setReturnValue(((Level) world).getGameRules().getInt(RegisterGamerules.LAVA_FLOW_SPEED_NETHER));
            } else {
                cir.setReturnValue(((Level) world).getGameRules().getInt(RegisterGamerules.LAVA_FLOW_SPEED));
            }
        }
    }

    /**
     * @author DragonEggBedrockBreaking
     * @reason modify whether or not the fluid can form infinite water sources
     * @param cir the returnable callback info
     */
    @Inject(method = "canConvertToSource", at = @At("HEAD"), cancellable = true)
    private void canLavaConvertToSource(CallbackInfoReturnable<Boolean> cir) {
        if (RegisterGamerules.getServer() == null) return;
        cir.setReturnValue(RegisterGamerules.getServer().getGameRules().getBoolean(RegisterGamerules.INFINITE_LAVA));
    }
}
