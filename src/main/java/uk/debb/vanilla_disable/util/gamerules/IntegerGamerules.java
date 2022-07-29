package uk.debb.vanilla_disable.util.gamerules;

import net.minecraft.world.level.GameRules;
import static uk.debb.vanilla_disable.util.gamerules.GameruleCategories.*;

public enum IntegerGamerules {
    MIN_SPAWN_DISTANCE(null, VD_DESPAWNING, 24, 0, 512),
    MONSTER_MAX_DESPAWN(null, VD_DESPAWNING, 128, 0, 512),
    CREATURE_MAX_DESPAWN(null, VD_DESPAWNING, 128, 0, 512),
    AMBIENT_MAX_DESPAWN(null, VD_DESPAWNING, 128, 0, 512),
    AXOLOTL_MAX_DESPAWN(null, VD_DESPAWNING, 128, 0, 512),
    GLOWSQUID_MAX_DESPAWN(null, VD_DESPAWNING, 128, 0, 512),
    WATER_CREATURE_MAX_DESPAWN(null, VD_DESPAWNING, 128, 0, 512),
    WATER_AMBIENT_MAX_DESPAWN(null, VD_DESPAWNING, 64, 0, 512),
    MONSTER_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    CREATURE_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    AMBIENT_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    AXOLOTL_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    GLOWSQUID_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    WATER_CREATURE_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    WATER_AMBIENT_MIN_DESPAWN(null, VD_DESPAWNING, 32, 0, 512),
    ITEM_DESPAWN_TIME(null, VD_DESPAWNING, 300, 0),

    MONSTER_MOBCAP(null, VD_SPAWN_LIMITS, 70, 0),
    CREATURE_MOBCAP(null, VD_SPAWN_LIMITS, 10, 0),
    AMBIENT_MOBCAP(null, VD_SPAWN_LIMITS, 15, 0),
    AXOLOTL_MOBCAP(null, VD_SPAWN_LIMITS, 5, 0),
    GLOWSQUID_MOBCAP(null, VD_SPAWN_LIMITS, 5, 0),
    WATER_CREATURE_MOBCAP(null, VD_SPAWN_LIMITS, 5, 0),
    WATER_AMBIENT_MOBCAP(null, VD_SPAWN_LIMITS, 20, 0),
    MONSTER_MAX_LIGHT_LEVEL(null, VD_SPAWN_LIMITS, 0, 0, 15),

    WATER_FLOW_SPEED(null, VD_FLUIDS, 5, 1, 128),
    LAVA_FLOW_SPEED(null, VD_FLUIDS, 30, 1, 128),
    LAVA_FLOW_SPEED_NETHER(null, VD_FLUIDS, 10, 1, 128),

    VILLAGER_DAILY_RESTOCKS(null, VD_MOBS, 2, 0),

    REPEATER_BASE_DELAY(null, VD_REDSTONE, 2, 0),
    REPEATER_SIGNAL(null, VD_REDSTONE, 15, 0, 15),
    COMPARATOR_BASE_DELAY(null, VD_REDSTONE, 2, 0),
    WOOD_BUTTON_PRESS_DURATION(null, VD_REDSTONE, 30, 0),
    STONE_BUTTON_PRESS_DURATION(null, VD_REDSTONE, 20, 0),
    OBSERVER_DELAY(null, VD_REDSTONE, 2, 0),
    OBSERVER_DURATION(null, VD_REDSTONE, 2, 0),
    PISTON_PUSH_LIMIT(null, VD_REDSTONE, 12, 0),

    NETHER_PORTAL_COOLDOWN(null, VD_BLOCKS, 300, 0),
    DEFAULT_BLOCK_FRICTION(null, VD_BLOCKS, 60, 0),
    ICE_FRICTION(null, VD_BLOCKS, 98, 0),
    SLIME_FRICTION(null, VD_BLOCKS, 80, 0),
    DEFAULT_BLOCK_SPEED(null, VD_BLOCKS, 100, 0),
    SOUL_SAND_SPEED(null, VD_BLOCKS, 40, 0),
    HONEY_BLOCK_SPEED(null, VD_BLOCKS, 40, 0),
    DEFAULT_BLOCK_JUMP(null, VD_BLOCKS, 100, 0),
    HONEY_BLOCK_JUMP(null, VD_BLOCKS, 50, 0),

    APPLE_NUTRITION(null, VD_FOOD, 4, 0, 20),
    APPLE_SATURATION(null, VD_FOOD, 3, 0, 99),
    BAKED_POTATO_NUTRITION(null, VD_FOOD, 5, 0, 20),
    BAKED_POTATO_SATURATION(null, VD_FOOD, 6, 0, 99),
    BEEF_NUTRITION(null, VD_FOOD, 3, 0, 20),
    BEEF_SATURATION(null, VD_FOOD, 3, 0, 99),
    BEETROOT_NUTRITION(null, VD_FOOD, 1, 0, 20),
    BEETROOT_SATURATION(null, VD_FOOD, 6, 0, 99),
    BEETROOT_SOUP_NUTRITION(null, VD_FOOD, 6, 0, 20),
    BEETROOT_SOUP_SATURATION(null, VD_FOOD, 6, 0, 99),
    BREAD_NUTRITION(null, VD_FOOD, 5, 0, 20),
    BREAD_SATURATION(null, VD_FOOD, 6, 0, 99),
    CARROT_NUTRITION(null, VD_FOOD, 3, 0, 20),
    CARROT_SATURATION(null, VD_FOOD, 6, 0, 99),
    CHICKEN_NUTRITION(null, VD_FOOD, 2, 0, 20),
    CHICKEN_SATURATION(null, VD_FOOD, 3, 0, 99),
    CHORUS_FRUIT_NUTRITION(null, VD_FOOD, 4, 0, 20),
    CHORUS_FRUIT_SATURATION(null, VD_FOOD, 3, 0, 99),
    COD_NUTRITION(null, VD_FOOD, 2, 0, 20),
    COD_SATURATION(null, VD_FOOD, 1, 0, 99),
    COOKED_BEEF_NUTRITION(null, VD_FOOD, 8, 0, 20),
    COOKED_BEEF_SATURATION(null, VD_FOOD, 8, 0, 99),
    COOKED_CHICKEN_NUTRITION(null, VD_FOOD, 6, 0, 20),
    COOKED_CHICKEN_SATURATION(null, VD_FOOD, 6, 0, 99),
    COOKED_COD_NUTRITION(null, VD_FOOD, 5, 0, 20),
    COOKED_COD_SATURATION(null, VD_FOOD, 6, 0, 99),
    COOKED_MUTTON_NUTRITION(null, VD_FOOD, 6, 0, 20),
    COOKED_MUTTON_SATURATION(null, VD_FOOD, 8, 0, 99),
    COOKED_PORKCHOP_NUTRITION(null, VD_FOOD, 8, 0, 20),
    COOKED_PORKCHOP_SATURATION(null, VD_FOOD, 8, 0, 99),
    COOKED_RABBIT_NUTRITION(null, VD_FOOD, 5, 0, 20),
    COOKED_RABBIT_SATURATION(null, VD_FOOD, 6, 0, 99),
    COOKED_SALMON_NUTRITION(null, VD_FOOD, 6, 0, 20),
    COOKED_SALMON_SATURATION(null, VD_FOOD, 8, 0, 99),
    COOKIE_NUTRITION(null, VD_FOOD, 2, 0, 20),
    COOKIE_SATURATION(null, VD_FOOD, 2, 0, 99),
    DRIED_KELP_NUTRITION(null, VD_FOOD, 1, 0, 20),
    DRIED_KELP_SATURATION(null, VD_FOOD, 3, 0, 99),
    ENCHANTED_GOLDEN_APPLE_NUTRITION(null, VD_FOOD, 4, 0, 20),
    ENCHANTED_GOLDEN_APPLE_SATURATION(null, VD_FOOD, 12, 0, 99),
    GOLDEN_APPLE_NUTRITION(null, VD_FOOD, 4, 0, 20),
    GOLDEN_APPLE_SATURATION(null, VD_FOOD, 12, 0, 99),
    GOLDEN_CARROT_NUTRITION(null, VD_FOOD, 6, 0, 20),
    GOLDEN_CARROT_SATURATION(null, VD_FOOD, 12, 0, 99),
    HONEY_BOTTLE_NUTRITION(null, VD_FOOD, 6, 0, 20),
    HONEY_BOTTLE_SATURATION(null, VD_FOOD, 1, 0, 99),
    MELON_SLICE_NUTRITION(null, VD_FOOD, 2, 0, 20),
    MELON_SLICE_SATURATION(null, VD_FOOD, 3, 0, 99),
    MUSHROOM_STEW_NUTRITION(null, VD_FOOD, 6, 0, 20),
    MUSHROOM_STEW_SATURATION(null, VD_FOOD, 3, 0, 99),
    MUTTON_NUTRITION(null, VD_FOOD, 2, 0, 20),
    MUTTON_SATURATION(null, VD_FOOD, 3, 0, 99),
    POISONOUS_POTATO_NUTRITION(null, VD_FOOD, 2, 0, 20),
    POISONOUS_POTATO_SATURATION(null, VD_FOOD, 3, 0, 99),
    PORKCHOP_NUTRITION(null, VD_FOOD, 3, 0, 20),
    PORKCHOP_SATURATION(null, VD_FOOD, 3, 0, 99),
    POTATO_NUTRITION(null, VD_FOOD, 1, 0, 20),
    POTATO_SATURATION(null, VD_FOOD, 1, 0, 99),
    PUFFERFISH_NUTRITION(null, VD_FOOD, 1, 0, 20),
    PUFFERFISH_SATURATION(null, VD_FOOD, 3, 0, 99),
    PUMPKIN_PIE_NUTRITION(null, VD_FOOD, 8, 0, 20),
    PUMPKIN_PIE_SATURATION(null, VD_FOOD, 3, 0, 99),
    RABBIT_NUTRITION(null, VD_FOOD, 3, 0, 20),
    RABBIT_SATURATION(null, VD_FOOD, 3, 0, 99),
    RABBIT_STEW_NUTRITION(null, VD_FOOD, 10, 0, 20),
    RABBIT_STEW_SATURATION(null, VD_FOOD, 6, 0, 99),
    ROTTEN_FLESH_NUTRITION(null, VD_FOOD, 4, 0, 20),
    ROTTEN_FLESH_SATURATION(null, VD_FOOD, 1, 0, 99),
    SALMON_NUTRITION(null, VD_FOOD, 2, 0, 20),
    SALMON_SATURATION(null, VD_FOOD, 1, 0, 99),
    SPIDER_EYE_NUTRITION(null, VD_FOOD, 2, 0, 20),
    SPIDER_EYE_SATURATION(null, VD_FOOD, 8, 0, 99),
    SUSPICIOUS_STEW_NUTRITION(null, VD_FOOD, 6, 0, 20),
    SUSPICIOUS_STEW_SATURATION(null, VD_FOOD, 6, 0, 99),
    SWEET_BERRIES_NUTRITION(null, VD_FOOD, 2, 0, 20),
    SWEET_BERRIES_SATURATION(null, VD_FOOD, 1, 0, 99),
    GLOW_BERRIES_NUTRITION(null, VD_FOOD, 2, 0, 20),
    GLOW_BERRIES_SATURATION(null, VD_FOOD, 1, 0, 99),
    TROPICAL_FISH_NUTRITION(null, VD_FOOD, 1, 0, 20),
    TROPICAL_FISH_SATURATION(null, VD_FOOD, 1, 0, 99);

    private final int defaultInt;
    private final int minValue;
    private final int maxValue;
    private GameRules.Key<GameRules.IntegerValue> gameRule;
    private final GameruleCategories category;

    IntegerGamerules(GameRules.Key<GameRules.IntegerValue> gameRule, GameruleCategories category, int defaultInt, int minValue, int maxValue) {
        this.gameRule = gameRule;
        this.category = category;
        this.defaultInt = defaultInt;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    IntegerGamerules(GameRules.Key<GameRules.IntegerValue> gameRule, GameruleCategories category, int defaultInt, int minValue) {
        this.gameRule = gameRule;
        this.category = category;
        this.defaultInt = defaultInt;
        this.minValue = minValue;
        this.maxValue = Integer.MAX_VALUE;
    }

    public GameRules.Key<GameRules.IntegerValue> getGameRule() {
        return this.gameRule;
    }
    public void setGameRule(GameRules.Key<GameRules.IntegerValue> gameRule) {
        this.gameRule = gameRule;
    }
    public GameruleCategories getCategory() {
        return this.category;
    }
    public int getDefaultInt() {
        return this.defaultInt;
    }
    public int getMinInt() {
        return this.minValue;
    }
    public int getMaxInt() {
        return this.maxValue;
    }
}
