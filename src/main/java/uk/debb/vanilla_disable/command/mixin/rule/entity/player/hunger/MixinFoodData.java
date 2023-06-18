package uk.debb.vanilla_disable.command.mixin.rule.entity.player.hunger;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.Difficulty;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import uk.debb.vanilla_disable.command.data.DataHandler;

@Mixin(FoodData.class)
public abstract class MixinFoodData {
    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getDifficulty()Lnet/minecraft/world/Difficulty;"
            )
    )
    private Difficulty getDifficulty(Difficulty original) {
        return DataHandler.getCachedBoolean("entities", "minecraft:player", "beta_hunger") ?
                Difficulty.PEACEFUL : original;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
            )
    )
    private boolean getBoolean(boolean original) {
        return !DataHandler.getCachedBoolean("entities", "minecraft:player", "beta_hunger") && original;
    }
}
