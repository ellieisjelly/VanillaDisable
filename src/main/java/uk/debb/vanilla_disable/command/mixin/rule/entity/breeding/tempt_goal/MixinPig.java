package uk.debb.vanilla_disable.command.mixin.rule.entity.breeding.tempt_goal;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import uk.debb.vanilla_disable.command.data.CommandDataHandler;

@Mixin(Pig.class)
public abstract class MixinPig {
    @ModifyArg(
            method = "registerGoals",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/TemptGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DLnet/minecraft/world/item/crafting/Ingredient;Z)V",
                    ordinal = 1
            ),
            index = 2
    )
    private Ingredient getIngredient(Ingredient original) {
        if (CommandDataHandler.isConnectionNull()) return original;
        return CommandDataHandler.getCachedBreedingItems("minecraft:pig");
    }

    @WrapWithCondition(
            method = "registerGoals",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V",
                    ordinal = 0
            )
    )
    private boolean shouldRegisterGoal(GoalSelector goalSelector, int i, Goal goal) {
        return false;
    }
}