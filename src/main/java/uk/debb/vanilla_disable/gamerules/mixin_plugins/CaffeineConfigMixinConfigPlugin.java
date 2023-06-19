package uk.debb.vanilla_disable.gamerules.mixin_plugins;

import net.caffeinemc.caffeineconfig.AbstractCaffeineConfigMixinPlugin;
import net.caffeinemc.caffeineconfig.CaffeineConfig;
import net.fabricmc.loader.api.FabricLoader;

public class CaffeineConfigMixinConfigPlugin extends AbstractCaffeineConfigMixinPlugin {
    public static CaffeineConfig caffeineConfig;

    @Override
    protected CaffeineConfig createConfig() {
        caffeineConfig = CaffeineConfig.builder("VanillaDisable")
                .addMixinOption("biome", true)
                .addMixinOption("enchantments", true)
                .addMixinOption("misc", true)
                .addMixinOption("mobs", true)
                .addMixinOption("spawn_limits", true)
                .addMixinOption("worldgen", true)
                .withInfoUrl("https://github.com/DragonEggBedrockBreaking/VanillaDisable/wiki/Mixin-Configuration-File")
                .build(FabricLoader.getInstance().getConfigDir().resolve("vanilla-disable-mixin.properties"));

        return caffeineConfig;
    }

    @Override
    protected String mixinPackageRoot() {
        return "uk.debb.vanilla_disable.gamerules.mixin.";
    }
}