package uk.debb.vanilla_disable.data.gamerule;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;

public class RegisterGamerules implements ModInitializer {
    public static final CustomGameRuleCategory VANILLA_DISABLE = new CustomGameRuleCategory(
            new ResourceLocation("vd.gamerule.category.vanilla_disable"),
            Component.translatable("vd.gamerule.category.vanilla_disable")
                    .withStyle(ChatFormatting.BOLD)
                    .withStyle(ChatFormatting.DARK_GREEN)
    );
    public static GameRules.Key<GameRules.IntegerValue> RAID_WAVES_EASY;
    public static GameRules.Key<GameRules.IntegerValue> RAID_WAVES_NORMAL;
    public static GameRules.Key<GameRules.IntegerValue> RAID_WAVES_HARD;
    public static GameRules.Key<GameRules.BooleanValue> RECIPE_BOOK_ENABLED;
    public static MinecraftServer server;

    @Override
    public void onInitialize() {
        RAID_WAVES_EASY = GameRuleRegistry.register("raidWavesEasy", VANILLA_DISABLE, GameRuleFactory.createIntRule(4, 1));
        RAID_WAVES_NORMAL = GameRuleRegistry.register("raidWavesNormal", VANILLA_DISABLE, GameRuleFactory.createIntRule(6, 1));
        RAID_WAVES_HARD = GameRuleRegistry.register("raidWavesHard", VANILLA_DISABLE, GameRuleFactory.createIntRule(8, 1));
        RECIPE_BOOK_ENABLED = GameRuleRegistry.register("recipeBookEnabled", VANILLA_DISABLE, GameRuleFactory.createBooleanRule(true));
    }
}