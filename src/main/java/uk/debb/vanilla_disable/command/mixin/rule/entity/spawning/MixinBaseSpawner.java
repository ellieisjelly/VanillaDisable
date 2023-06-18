package uk.debb.vanilla_disable.command.mixin.rule.entity.spawning;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import uk.debb.vanilla_disable.command.data.DataHandler;

import java.util.function.Function;

@Mixin(BaseSpawner.class)
public abstract class MixinBaseSpawner {
    @Shadow
    protected abstract SpawnData getOrCreateNextSpawnData(@Nullable Level arg, RandomSource arg2, BlockPos arg3);

    @ModifyExpressionValue(
            method = "serverTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/BaseSpawner;isNearPlayer(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean isNearPlayer(boolean original, ServerLevel level, BlockPos blockPos) {
        CompoundTag compoundTag = this.getOrCreateNextSpawnData(level, level.getRandom(), blockPos).getEntityToSpawn();
        Entity entity = EntityType.loadEntityRecursive(compoundTag, level, Function.identity());
        if (entity != null) {
            String entityType = DataHandler.getKeyFromEntityTypeRegistry(entity.getType());
            if (!DataHandler.getCachedBoolean("entities", entityType, "spawner")) {
                return false;
            }
        }
        return original;
    }
}
