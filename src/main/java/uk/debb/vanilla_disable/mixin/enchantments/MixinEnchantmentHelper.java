package uk.debb.vanilla_disable.mixin.enchantments;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.debb.vanilla_disable.util.GameruleHelper;
import uk.debb.vanilla_disable.util.Gamerules;
import uk.debb.vanilla_disable.util.VDServer;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantmentHelper {
    /**
     * @author DragonEggBedrockBreaking
     * @reason map of all enchantments to their gamerules
     */
    @Unique
    private static final Object2ObjectMap<Enchantment, GameRules.Key<GameRules.BooleanValue>> enchantmentMap = new Object2ObjectOpenHashMap<Enchantment, GameRules.Key<GameRules.BooleanValue>>();

    /**
     * @author DragonEggBedrockBreaking
     * @reason the map otherwise initialises before the gamerules are created and always returns null
     */
    @Unique
    private static void addOptionsToMap() {
        enchantmentMap.put(Enchantments.AQUA_AFFINITY, Gamerules.AQUA_AFFINITY_ENCHANTMENT);
        enchantmentMap.put(Enchantments.BANE_OF_ARTHROPODS, Gamerules.BANE_OF_ARTHROPODS_ENCHANTMENT);
        enchantmentMap.put(Enchantments.BLAST_PROTECTION, Gamerules.BLAST_PROTECTION_ENCHANTMENT);
        enchantmentMap.put(Enchantments.CHANNELING, Gamerules.CHANNELING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.DEPTH_STRIDER, Gamerules.DEPTH_STRIDER_ENCHANTMENT);
        enchantmentMap.put(Enchantments.BLOCK_EFFICIENCY, Gamerules.EFFICIENCY_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FALL_PROTECTION, Gamerules.FEATHER_FALLING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FIRE_ASPECT, Gamerules.FIRE_ASPECT_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FIRE_PROTECTION, Gamerules.FIRE_PROTECTION_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FLAMING_ARROWS, Gamerules.FLAME_ENCHANTMENT);
        enchantmentMap.put(Enchantments.BLOCK_FORTUNE, Gamerules.FORTUNE_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FROST_WALKER, Gamerules.FROST_WALKER_ENCHANTMENT);
        enchantmentMap.put(Enchantments.IMPALING, Gamerules.IMPALING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.INFINITY_ARROWS, Gamerules.INFINITY_ENCHANTMENT);
        enchantmentMap.put(Enchantments.KNOCKBACK, Gamerules.KNOCKBACK_ENCHANTMENT);
        enchantmentMap.put(Enchantments.MOB_LOOTING, Gamerules.LOOTING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.LOYALTY, Gamerules.LOYALTY_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FISHING_LUCK, Gamerules.LUCK_OF_THE_SEA_ENCHANTMENT);
        enchantmentMap.put(Enchantments.FISHING_SPEED, Gamerules.LURE_ENCHANTMENT);
        enchantmentMap.put(Enchantments.MENDING, Gamerules.MENDING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.MULTISHOT, Gamerules.MULTISHOT_ENCHANTMENT);
        enchantmentMap.put(Enchantments.PIERCING, Gamerules.PIERCING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.POWER_ARROWS, Gamerules.POWER_ENCHANTMENT);
        enchantmentMap.put(Enchantments.PROJECTILE_PROTECTION, Gamerules.PROJECTILE_PROTECTION_ENCHANTMENT);
        enchantmentMap.put(Enchantments.ALL_DAMAGE_PROTECTION, Gamerules.PROTECTION_ENCHANTMENT);
        enchantmentMap.put(Enchantments.PUNCH_ARROWS, Gamerules.PUNCH_ENCHANTMENT);
        enchantmentMap.put(Enchantments.QUICK_CHARGE, Gamerules.QUICK_CHARGE_ENCHANTMENT);
        enchantmentMap.put(Enchantments.RESPIRATION, Gamerules.RESPIRATION_ENCHANTMENT);
        enchantmentMap.put(Enchantments.RIPTIDE, Gamerules.RIPTIDE_ENCHANTMENT);
        enchantmentMap.put(Enchantments.SHARPNESS, Gamerules.SHARPNESS_ENCHANTMENT);
        enchantmentMap.put(Enchantments.SILK_TOUCH, Gamerules.SILK_TOUCH_ENCHANTMENT);
        enchantmentMap.put(Enchantments.SMITE, Gamerules.SMITE_ENCHANTMENT);
        enchantmentMap.put(Enchantments.SOUL_SPEED, Gamerules.SOUL_SPEED_ENCHANTMENT);
        enchantmentMap.put(Enchantments.SWEEPING_EDGE, Gamerules.SWEEPING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.THORNS, Gamerules.THORNS_ENCHANTMENT);
        enchantmentMap.put(Enchantments.UNBREAKING, Gamerules.UNBREAKING_ENCHANTMENT);
        enchantmentMap.put(Enchantments.BINDING_CURSE, Gamerules.BINDING_CURSE);
        enchantmentMap.put(Enchantments.VANISHING_CURSE, Gamerules.VANISHING_CURSE);
    }

    /**
     * @author DragonEggBedrockBreaking
     * @reason disable vanilla enchantments
     * @param enchantment the enchantment on the item to disable
     * @param stack the stack of items with that enchantment
     * @param cir the returnable callback info (Integer)
     */
    @Inject(method = "getItemEnchantmentLevel", at = @At("HEAD"), cancellable = true)
    private static void removeEnchantmentLevel(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (VDServer.getServer() == null) return;
        if (enchantmentMap.isEmpty()) {
            addOptionsToMap();
        }
        GameRules.Key<GameRules.BooleanValue> gameRule = enchantmentMap.get(enchantment);
        if (!GameruleHelper.getBool(Gamerules.ENCHANTMENTS_ENABLED) ||
            (gameRule != null && !GameruleHelper.getBool(gameRule))) {
            cir.setReturnValue(0);
        }
    }
}
