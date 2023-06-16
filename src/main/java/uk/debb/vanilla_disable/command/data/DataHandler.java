package uk.debb.vanilla_disable.command.data;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.packs.VanillaHusbandryAdvancements;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.LevelResource;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static uk.debb.vanilla_disable.command.data.DataType.*;

public class DataHandler {
    public static final Object2ObjectMap<String, Object2ObjectMap<String, DataType>> cols = new Object2ObjectOpenHashMap<>();

    public static final Object2ObjectMap<String, Object2ObjectMap<String, String>> entities = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<String, Object2ObjectMap<String, String>> blocks = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<String, Object2ObjectMap<String, String>> items = new Object2ObjectOpenHashMap<>();
    public static final ObjectSet<String> others = new ObjectArraySet<>();

    public static final Object2ObjectMap<String, Object2ObjectMap<String, String>> entityData = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<String, Object2ObjectMap<String, String>> blockData = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<String, Object2ObjectMap<String, String>> itemData = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<String, String> otherData = new Object2ObjectOpenHashMap<>();

    public static final Object2IntMap<String> intRowMaximums = new Object2IntArrayMap<>();
    public static final Object2DoubleMap<String> doubleRowMaximums = new Object2DoubleArrayMap<>();
    public static final Object2ObjectMap<String, List<String>> stringColSuggestions = new Object2ObjectOpenHashMap<>();
    public static final ObjectList<String> differentDataTypes = new ObjectArrayList<>();

    public static MinecraftServer server;
    public static boolean populationDone = false;
    private static Connection connection;
    private static Statement statement;
    public static RegistryAccess registryAccess;
    private static String PATH;

    private static Registry<Block> blockRegistry;
    private static Registry<Item> itemRegistry;
    private static Registry<EntityType<?>> entityTypeRegistry;
    private static Registry<PaintingVariant> paintingVariantRegistry;
    private static Registry<VillagerProfession> villagerProfessionRegistry;
    private static Registry<VillagerType> villagerTypeRegistry;
    private static Registry<BlockEntityType<?>> blockEntityRegistry;
    public static Registry<Enchantment> enchantmentRegistry;
    public static Registry<Potion> potionRegistry;
    public static Registry<Feature<?>> featureRegistry;
    private static Registry<DamageType> damageTypeRegistry;
    private static Registry<MobEffect> mobEffectRegistry;
    public static Registry<Biome> biomeRegistry;
    public static Registry<PlacedFeature> placedFeatureRegistry;
    public static Registry<Structure> structureRegistry;

    /**
     * Cleans up data for display (removes underscores, 'namespace:' prefixes, 'group/' prefixes)
     * @param o The object to be cleaned up
     * @return The cleaned up object as a string
     */
    private static String cleanup(Object o) {
        String s = o.toString().replace("_", " ");
        if (s.contains(":")) {
            s = s.split(":")[1];
        }
        if (s.contains("/")) {
            s = s.split("/")[1];
        }
        return s;
    }

    /**
     * Cleans up data for display (removes 'namespace:' prefixes)
     * @param o The object to be cleaned up
     * @return The cleaned up object as a string
     */
    private static String lightCleanup(Object o) {
        String s = o.toString();
        if (s.contains(":")) {
            s = s.split(":")[1];
        }
        return s;
    }

    /**
     * Populates the data maps with the data dynamically pulled from registries, and custom data.
     * This includes names of columns corresponding to data type, default value for each row, and descriptions.
     */
    public static void populate() {
        registryAccess = server.registryAccess();
        blockRegistry = registryAccess.registryOrThrow(Registries.BLOCK);
        itemRegistry = registryAccess.registryOrThrow(Registries.ITEM);
        entityTypeRegistry = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
        paintingVariantRegistry = registryAccess.registryOrThrow(Registries.PAINTING_VARIANT);
        villagerProfessionRegistry = registryAccess.registryOrThrow(Registries.VILLAGER_PROFESSION);
        villagerTypeRegistry = registryAccess.registryOrThrow(Registries.VILLAGER_TYPE);
        blockEntityRegistry = registryAccess.registryOrThrow(Registries.BLOCK_ENTITY_TYPE);
        enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
        potionRegistry = registryAccess.registryOrThrow(Registries.POTION);
        featureRegistry = registryAccess.registryOrThrow(Registries.FEATURE);
        damageTypeRegistry = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
        mobEffectRegistry = registryAccess.registryOrThrow(Registries.MOB_EFFECT);
        biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
        placedFeatureRegistry = registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
        structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);

        cols.put("entities", new Object2ObjectOpenHashMap<>() {{

            put("can_be_on_fire", BOOLEAN);
            put("can_sprint", BOOLEAN);
            put("can_crouch", BOOLEAN);
            put("can_swim", BOOLEAN);
            put("can_jump", BOOLEAN);
            put("can_be_invisible", BOOLEAN);
            put("flying_speed", REAL);
            put("beta_hunger", BOOLEAN);

            put("item_stats", BOOLEAN);
            put("entity_stats", BOOLEAN);
            put("time_stats", BOOLEAN);
            put("distance_stats", BOOLEAN);
            put("damage_stats", BOOLEAN);
            put("block_gui_interaction_stats", BOOLEAN);
            put("block_general_interaction_stats", BOOLEAN);
            put("general_stats", BOOLEAN);

            damageTypeRegistry.keySet().forEach(damageType -> {
                put(damageType + "_damage", BOOLEAN);
                put(damageType + "_death", BOOLEAN);
            });

            entityTypeRegistry.keySet().forEach(entityType ->
                    put(lightCleanup(entityType) + "_knockback", BOOLEAN));
            put("explosion_knockback", BOOLEAN);

            mobEffectRegistry.keySet().forEach(mobEffect ->
                    put(mobEffect + "_effect", BOOLEAN));

            paintingVariantRegistry.keySet().forEach(painting ->
                    put(painting + "_painting", BOOLEAN));

            villagerProfessionRegistry.keySet().forEach(profession ->
                    put(profession + "_profession", BOOLEAN));
            villagerTypeRegistry.keySet().forEach(type ->
                    put(type + "_type", BOOLEAN));

            put("can_despawn", BOOLEAN);
            put("despawn_time", INTEGER);

            put("can_spawn", BOOLEAN);
            put("spawn_egg", BOOLEAN);
            put("spawner", BOOLEAN);
            put("can_breed", BOOLEAN);
            put("can_exist", BOOLEAN);
            put("can_be_cured", BOOLEAN);
            put("can_be_converted_to", BOOLEAN);
            put("burns_in_sunlight", BOOLEAN);
            put("spawned_by_villagers", BOOLEAN);
            put("can_drop_xp", BOOLEAN);
            put("ai", BOOLEAN);
            put("can_trade", BOOLEAN);
            put("daily_restocks", INTEGER);
            put("can_be_lit", BOOLEAN);
            put("despawn_on_player_death", BOOLEAN);
            put("min_spawn_distance", INTEGER);
            put("min_despawn_distance", INTEGER);
            put("instant_despawn_distance", INTEGER);

            itemRegistry.keySet().forEach(item ->
                    put("can_breed_with_" + lightCleanup(item), BOOLEAN));

            biomeRegistry.keySet().forEach(biome ->
                    put("can_spawn_in_" + lightCleanup(biome), BOOLEAN));
        }});
        cols.put("blocks", new Object2ObjectOpenHashMap<>() {{
            put("can_place_in_overworld", BOOLEAN);
            put("can_place_in_nether", BOOLEAN);
            put("can_place_in_end", BOOLEAN);
            put("can_break", BOOLEAN);
            put("can_interact", BOOLEAN);
            put("works", BOOLEAN);
            put("friction_factor", REAL);
            put("speed_factor", REAL);
            put("jump_factor", REAL);
            put("can_be_filled_by_dripstone", BOOLEAN);
            put("fluid_reaches_far", BOOLEAN);
            put("fluid_reaches_far_in_nether", BOOLEAN);
            put("fluid_speed", INTEGER);
            put("fluid_speed_in_nether", INTEGER);
            put("redstone_delay", INTEGER);
            put("redstone_duration", INTEGER);
            put("can_drop_xp", BOOLEAN);
            put("can_fall", BOOLEAN);
            put("can_be_trampled", BOOLEAN);
            put("alpha_behaviour", BOOLEAN);
            put("opening_blockable", BOOLEAN);
            put("cooldown", INTEGER);
            put("push_behaviour", CLOB);
            put("ignited_by_lava", BOOLEAN);
            put("destroy_speed", REAL);
            put("requires_correct_tool_for_drops", BOOLEAN);
            put("burn_odds", INTEGER);
            put("ignite_odds", INTEGER);
        }});
        cols.put("items", new Object2ObjectOpenHashMap<>() {{
            put("works", BOOLEAN);
            put("durability", INTEGER);
            put("burns", BOOLEAN);
            put("can_spam", BOOLEAN);
            put("nutrition", INTEGER);
            put("saturation", REAL);
            put("can_break_blocks_in_creative", BOOLEAN);

            enchantmentRegistry.keySet().forEach(enchantment ->
                    put(enchantment + "_enchantment", BOOLEAN));

            potionRegistry.keySet().forEach(potion ->
                    put(potion + "_effect", BOOLEAN));

            put("dispenser_interaction", BOOLEAN);
            put("cauldron_interaction", BOOLEAN);
            put("fuel_duration", INTEGER);
        }});

        entityTypeRegistry.forEach((entityType) ->
                entities.put(Objects.requireNonNull(entityTypeRegistry.getKey(entityType)).toString(), new Object2ObjectOpenHashMap<>() {{
                    Entity entity = entityType.create(server.overworld());

                    if (entityType.equals(EntityType.PLAYER)) {
                        put("can_be_on_fire", "true");
                        put("can_sprint", "true");
                        put("can_crouch", "true");
                        put("can_swim", "true");
                        put("can_jump", "true");
                        put("can_be_invisible", "true");
                        put("flying_speed", "0.05");
                        put("beta_hunger", "false");

                        put("item_stats", "true");
                        put("entity_stats", "true");
                        put("time_stats", "true");
                        put("distance_stats", "true");
                        put("damage_stats", "true");
                        put("block_gui_interaction_stats", "true");
                        put("block_general_interaction_stats", "true");
                        put("general_stats", "true");

                        damageTypeRegistry.keySet().forEach(damageType -> {
                            put(damageType + "_damage", "true");
                            put(damageType + "_death", "true");
                        });
                    }

                    if (entity instanceof LivingEntity || entityType.equals(EntityType.PLAYER)) {
                        entityTypeRegistry.keySet().forEach(entityType ->
                                put(lightCleanup(entityType) + "_knockback", "true"));
                        put("explosion_knockback", "true");

                        mobEffectRegistry.keySet().forEach(mobEffect ->
                                put(mobEffect + "_effect", "true"));

                        put("can_despawn", String.valueOf(!entityType.getCategory().isPersistent()));

                        put("can_spawn", "true");
                        put("spawn_egg", "true");
                        put("spawner", "true");
                        put("can_drop_xp", "true");
                        put("ai", "true");
                    }

                    if (!entityType.getCategory().equals(MobCategory.MISC)) {
                        put("min_spawn_distance", "24");
                        put("min_despawn_distance", String.valueOf(entityType.getCategory().getNoDespawnDistance()));
                        put("instant_despawn_distance", String.valueOf(entityType.getCategory().getDespawnDistance()));

                        biomeRegistry.forEach(biome -> {
                            String biomeString = Objects.requireNonNull(biomeRegistry.getKey(biome)).toString();
                            boolean canSpawnIn = biome.getMobSettings().spawners.values().stream().anyMatch(ls ->
                                    ls.unwrap().stream().anyMatch(entry -> entry.type.equals(entityType)));
                            put("can_spawn_in_" + lightCleanup(biomeString), String.valueOf(canSpawnIn));
                        });
                    }

                    if (entityType.equals(EntityType.PAINTING)) {
                        paintingVariantRegistry.keySet().forEach(painting ->
                                put(painting + "_painting", "true"));
                    }

                    if (entityType.equals(EntityType.VILLAGER)) {
                        villagerProfessionRegistry.keySet().forEach(profession ->
                                put(profession + "_profession", "true"));
                        villagerTypeRegistry.keySet().forEach(type -> put(type + "_type", "true"));
                        put("can_breed", "true");
                        itemRegistry.forEach(item -> {
                            boolean villagerWants = Villager.WANTED_ITEMS.contains(item);
                            put("can_breed_with_" +
                                    lightCleanup(Objects.requireNonNull(itemRegistry.getKey(item)).toString()),
                                    String.valueOf(villagerWants));
                        });
                    }

                    if (VanillaHusbandryAdvancements.BREEDABLE_ANIMALS.contains(entityType) ||
                            VanillaHusbandryAdvancements.INDIRECTLY_BREEDABLE_ANIMALS.contains(entityType)) {
                        put("can_breed", "true");
                        try {
                            Class<?> animalClass = entity.getClass();
                            Method isFood = animalClass.getMethod("isFood", ItemStack.class);
                            itemRegistry.stream().forEach(item -> {
                                try {
                                    boolean itemIsFood = (boolean) isFood.invoke(entity, new ItemStack(item));
                                    put("can_breed_with_" +
                                            lightCleanup(Objects.requireNonNull(itemRegistry.getKey(item)).toString()),
                                            String.valueOf(itemIsFood));
                                } catch (InvocationTargetException | IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (entityType.equals(EntityType.ZOMBIE_VILLAGER)) {
                        put("can_be_cured", "true");
                    }

                    if (entityType.equals(EntityType.ZOMBIE_VILLAGER) || entityType.equals(EntityType.WITCH) ||
                            entityType.equals(EntityType.ZOMBIFIED_PIGLIN) || entityType.equals(EntityType.ZOGLIN) ||
                            entityType.equals(EntityType.DROWNED) || entityType.equals(EntityType.STRAY)) {
                        put("can_be_converted_to", "true");
                    }

                    if (entityType.equals(EntityType.SKELETON) || entityType.equals(EntityType.ZOMBIE) ||
                            entityType.equals(EntityType.PHANTOM) || entityType.equals(EntityType.STRAY) ||
                            entityType.equals(EntityType.DROWNED) || entityType.equals(EntityType.ZOMBIE_VILLAGER)) {
                        put("burns_in_sunlight", "true");
                    }

                    if (entityType.equals(EntityType.VILLAGER) || entityType.equals(EntityType.WANDERING_TRADER) ||
                            entityType.equals(EntityType.PIGLIN)) {
                        put("can_trade", "true");
                    }

                    if (entityType.equals(EntityType.VILLAGER) || entityType.equals(EntityType.WANDERING_TRADER)) {
                        put("daily_restocks", "true");
                    }

                    if (entityType.equals(EntityType.ITEM)) {
                        put("despawn_time", "6000");
                    }

                    if (entityType.equals(EntityType.CREEPER)) {
                        put("can_be_lit", "true");
                    }

                    if (entityType.equals(EntityType.ENDER_PEARL)) {
                        put("despawn_on_player_death", "true");
                    }

                    put("can_exist", "true");
                }}));

        blockRegistry.forEach((block) ->
                blocks.put(Objects.requireNonNull(blockRegistry.getKey(block)).toString(), new Object2ObjectOpenHashMap<>() {{
                    String name = block.toString();
                    BlockState blockState = block.defaultBlockState();

                    put("can_place_in_overworld", "true");
                    put("can_place_in_nether", String.valueOf(!block.equals(Blocks.WATER)));
                    put("can_place_in_end", "true");
                    put("can_break", "true");
                    put("can_interact", "true");
                    put("works", "true");

                    if (block.equals(Blocks.ICE) || block.equals(Blocks.PACKED_ICE) ||
                            block.equals(Blocks.BLUE_ICE)) {
                        put("friction_factor", "0.98");
                    } else if (block.equals(Blocks.SLIME_BLOCK)) {
                        put("friction_factor", "0.8");
                    } else {
                        put("friction_factor", "0.6");
                    }

                    boolean isHoneyBlock = block.equals(Blocks.HONEY_BLOCK);
                    if (block.equals(Blocks.SOUL_SAND) || isHoneyBlock) {
                        put("speed_factor", "0.4");
                    } else {
                        put("speed_factor", "1.0");
                    }

                    if (isHoneyBlock) {
                        put("jump_factor", "0.5");
                    } else {
                        put("jump_factor", "1.0");
                    }

                    if (block.equals(Blocks.CAULDRON)) {
                        put("can_be_filled_by_dripstone", "true");
                    }

                    if (block.equals(Blocks.WATER)) {
                        put("fluid_reaches_far", "true");
                        put("fluid_reaches_far_in_nether", "true");
                        put("fluid_speed", "5");
                        put("fluid_speed_in_nether", "5");
                    }

                    if (block.equals(Blocks.LAVA)) {
                        put("fluid_reaches_far", "false");
                        put("fluid_reaches_far_in_nether", "true");
                        put("fluid_speed", "30");
                        put("fluid_speed_in_nether", "10");
                    }

                    boolean isObserver = block.equals(Blocks.OBSERVER);
                    if (block.equals(Blocks.REPEATER) || block.equals(Blocks.COMPARATOR) || isObserver) {
                        put("redstone_delay", "2");
                    }

                    if (isObserver) {
                        put("redstone_duration", "2");
                    }

                    if (name.contains("button")) {
                        if (name.contains("stone")) {
                            put("redstone_duration", "20");
                        } else {
                            put("redstone_duration", "30");
                        }
                    }

                    if (name.contains("ore") || block.equals(Blocks.SPAWNER) ||
                            (name.contains("sculk") && !block.equals(Blocks.SCULK_VEIN)) ||
                            block.equals(Blocks.GRINDSTONE) || block instanceof AbstractFurnaceBlock) {
                        put("can_drop_xp", "true");
                    }

                    if (block instanceof FallingBlock || block.equals(Blocks.POINTED_DRIPSTONE)) {
                        put("can_fall", "true");
                    }

                    if (block.equals(Blocks.FARMLAND)) {
                        put("can_be_trampled", "true");
                    }

                    if (block.equals(Blocks.TNT)) {
                        put("alpha_behaviour", "false");
                    }

                    if (name.contains("chest") || name.contains("shulker_box")) {
                        put("opening_blockable", "true");
                    }

                    if (block.equals(Blocks.NETHER_PORTAL)) {
                        put("cooldown", "300");
                    }

                    if (!blockEntityRegistry.containsKey(blockRegistry.getKey(block))) {
                        if (block.equals(Blocks.OBSIDIAN) || block.equals(Blocks.CRYING_OBSIDIAN) ||
                                block.equals(Blocks.REINFORCED_DEEPSLATE) || block.equals(Blocks.RESPAWN_ANCHOR)) {
                            put("push_behaviour", "'" + PushReaction.BLOCK + "'");
                        } else {
                            put("push_behaviour", "'" + blockState.getPistonPushReaction() + "'");
                        }
                    }

                    put("ignited_by_lava", String.valueOf(blockState.ignitedByLava()));
                    put("destroy_speed", String.valueOf(blockState.getDestroySpeed(null, null)));
                    put("requires_correct_tool_for_drops", String.valueOf(blockState.requiresCorrectToolForDrops()));
                    put("burn_odds", String.valueOf(((FireBlock)Blocks.FIRE).getBurnOdds(block.defaultBlockState())));
                    put("ignite_odds", String.valueOf(((FireBlock)Blocks.FIRE).getIgniteOdds(block.defaultBlockState())));
                }}));

        itemRegistry.forEach((item) ->
                items.put(Objects.requireNonNull(itemRegistry.getKey(item)).toString(), new Object2ObjectOpenHashMap<>() {{
                    if (!blockRegistry.stream().map(Block::toString).toList().contains(item.toString())) {
                        String name = item.toString();

                        put("works", "true");

                        put("burns", String.valueOf(!(name.contains("netherite") || name.contains("debris"))));

                        if (item.equals(Items.BOW) || item.equals(Items.CROSSBOW)) {
                            put("can_spam", "false");
                        }

                        if (enchantmentRegistry.stream().anyMatch(enchantment -> enchantment.canEnchant(item.getDefaultInstance())) ||
                                item.equals(Items.ENCHANTED_BOOK)) {
                            put("durability", String.valueOf(item.getMaxDamage()));
                            enchantmentRegistry.forEach((enchantment) ->
                                    put(enchantmentRegistry.getKey(enchantment) + "_enchantment",
                                            String.valueOf(enchantment.canEnchant(item.getDefaultInstance()) || item.equals(Items.ENCHANTED_BOOK))));
                        }

                        FoodProperties foodProperties = item.getFoodProperties();
                        if (foodProperties != null) {
                            put("nutrition", String.valueOf(foodProperties.getNutrition()));
                            put("saturation", String.valueOf(foodProperties.getSaturationModifier()));
                        }

                        if (item.equals(Items.POTION) || item.equals(Items.SPLASH_POTION) ||
                                item.equals(Items.LINGERING_POTION) || item.equals(Items.TIPPED_ARROW)) {
                            potionRegistry.keySet().forEach((potion) -> put(potion + "_effect", "true"));
                        }

                        if (DispenserBlock.DISPENSER_REGISTRY.containsKey(item)) {
                            put("dispenser_interaction", "true");
                        }

                        if (CauldronInteraction.EMPTY.containsKey(item) || CauldronInteraction.WATER.containsKey(item) ||
                                CauldronInteraction.LAVA.containsKey(item) || CauldronInteraction.POWDER_SNOW.containsKey(item)) {
                            put("cauldron_interaction", "true");
                        }

                        Map<Item, Integer> fuels = AbstractFurnaceBlockEntity.getFuel();
                        if (fuels.containsKey(item)) {
                            put("fuel_duration", String.valueOf(fuels.get(item)));
                        }

                        put("can_break_blocks_in_creative", String.valueOf(!(item instanceof SwordItem)));
                    }
                }}));

        others.addAll(server.getAdvancements().getAllAdvancements()
                .stream().map(a -> a.getId().toString()).filter(a -> !a.contains("recipe")).toList());
        others.addAll(new Commands(Commands.CommandSelection.ALL, Commands.createValidationContext(VanillaRegistries.createLookup()))
                .getDispatcher().getRoot().getChildren().stream().map(commandNode -> "/" + commandNode.getName()).toList());
        others.addAll(structureRegistry.keySet()
                .stream().map(s -> s + "_structure").toList());
        others.addAll(placedFeatureRegistry.keySet()
                .stream().map(s -> s + "_feature").toList());
        others.addAll(featureRegistry.keySet()
                .stream().map(s -> s + "_feature").toList());
        others.addAll(biomeRegistry.keySet()
                .stream().map(s -> s + "_biome").toList());

        entityData.put("stats", new Object2ObjectOpenHashMap<>() {{
            put("item_stats", "Collect statistics related to items.");
            put("entity_stats", "Collect statistics related to entities.");
            put("time_stats", "Collect statistics related to time.");
            put("distance_stats", "Collect statistics related to distance.");
            put("damage_stats", "Collect statistics related to damage.");
            put("block_gui_interaction_stats", "Collect statistics related to block GUIs.");
            put("block_general_interaction_stats", "Collect statistics related to blocks.");
            put("general_stats", "Collect general statistics.");
        }});
        entityData.put("damage", new Object2ObjectOpenHashMap<>() {{
            damageTypeRegistry.keySet().forEach(damageType ->
                    put(damageType + "_damage", "Toggles " + cleanup(damageType) + " damage affecting the player."));
        }});
        entityData.put("knockback", new Object2ObjectOpenHashMap<>() {{
            entityTypeRegistry.keySet().forEach(entityType ->
                    put(lightCleanup(entityType) + "_knockback", "Toggles knockback from " + cleanup(entityType) + " affecting the mob."));
            put("explosion_knockback", "Toggles knockback from explosions affecting the entity.");
        }});
        entityData.put("effects", new Object2ObjectOpenHashMap<>() {{
            mobEffectRegistry.keySet().forEach(mobEffect ->
                    put(mobEffect + "_effect", "Toggles " + cleanup(mobEffect) + " affecting the mob."));
        }});
        entityData.put("death", new Object2ObjectOpenHashMap<>() {{
            damageTypeRegistry.keySet().forEach(damageType ->
                    put(damageType + "_death", "Toggles " + cleanup(damageType) + " damage being able to kill the player."));
        }});
        entityData.put("painting", new Object2ObjectOpenHashMap<>() {{
            paintingVariantRegistry.keySet().forEach(painting ->
                    put(painting + "_painting", "Toggles the " + cleanup(painting) + " design being able to show on paintings."));
        }});
        entityData.put("villager_type", new Object2ObjectOpenHashMap<>() {{
            villagerTypeRegistry.keySet().forEach(villagerType ->
                    put(villagerType + "_type", "Toggles villagers being able to be of the " + cleanup(villagerType) + " type."));
        }});
        entityData.put("villager_profession", new Object2ObjectOpenHashMap<>() {{
            villagerProfessionRegistry.keySet().forEach(villagerProfession ->
                    put(villagerProfession + "_profession", "Toggles villagers being able to have the " + cleanup(villagerProfession) + " profession."));
        }});
        entityData.put("player", new Object2ObjectOpenHashMap<>() {{
            put("can_be_on_fire", "Toggle the player being able to be on fire.");
            put("can_sprint", "Toggle the player being able to sprint.");
            put("can_crouch", "Toggle the player being able to crouch.");
            put("can_swim", "Toggle the player being able to swim.");
            put("can_jump", "Toggle the player being able to jump.");
            put("can_be_invisible", "Toggle the player being able to be invisible.");
            put("flying_speed", "Control the player's flying speed.");
            put("beta_hunger", "Toggle hunger having beta behaviour");
        }});
        entityData.put("spawning", new Object2ObjectOpenHashMap<>() {{
            put("can_despawn", "Toggle the mob being able to despawn.");
            put("despawn_time", "Controls how long it takes for an entity to despawn.");
            put("can_spawn", "Toggle the mob being able to spawn.");
            put("spawn_egg", "Toggle the mob being able to be spawned by a spawn egg.");
            put("spawner", "Toggle the mob being able to be spawned by a spawner.");
            put("despawn_on_player_death", "Toggle the ender pearl despawning when the player dies (fixes MC-199344).");
            put("can_breed", "Toggle the mob being able to breed.");
            put("spawned_by_villagers", "Toggle the mob being able to be spawned by villagers.");
            put("min_spawn_distance", "Control the minimum distance away from the player where the entity can spawn.");
            put("min_despawn_distance", "Control the minimum distance away from the player where the entity can despawn.");
            put("instant_despawn_distance", "Control the distance away from the player where the entity will instantly despawn.");
        }});
        entityData.put("breeding_ingredient", new Object2ObjectOpenHashMap<>() {{
            itemRegistry.keySet().forEach(item ->
                    put("can_breed_with_" + lightCleanup(item), "Toggles the mob being able to be bred with " + cleanup(item) + "."));
        }});
        entityData.put("possible_biomes", new Object2ObjectOpenHashMap<>() {{
            biomeRegistry.keySet().forEach(biome ->
                    put("can_spawn_in_" + lightCleanup(biome), "Toggle the mob being able to spawn in the " + cleanup(biome) + " biome."));
        }});
        entityData.put("other", new Object2ObjectOpenHashMap<>() {{
            put("can_exist", "Toggle the entity being able to exist.");
            put("can_be_cured", "Toggle the mob being able to be cured.");
            put("can_be_converted_to", "Toggle the mob being able to convert to another mob.");
            put("burns_in_sunlight", "Toggle the mob being able to burn in sunlight.");
            put("can_drop_xp", "Toggle the mob being able to drop XP.");
            put("ai", "Toggle the mob's AI.");
            put("can_trade", "Toggle the mob being able to trade.");
            put("daily_restocks", "Control the number of times per day a villager restocks.");
            put("can_be_lit", "Toggle the creeper being able to be lit by a flint and steel.");
        }});

        blockData.put("fluid", new Object2ObjectOpenHashMap<>() {{
            put("fluid_reaches_far", "Toggle whether the fluid can travel 8 blocks or only 4 in the overworld or end.");
            put("fluid_reaches_far_in_nether", "Toggle whether the fluid can travel 8 blocks or only 4 in the nether.");
            put("fluid_speed", "Control how fast the fluid flows in the overworld or end.");
            put("fluid_speed_in_nether", "Control how fast the fluid flows in the nether.");
        }});
        blockData.put("other", new Object2ObjectOpenHashMap<>() {{
            put("can_place_in_overworld", "Toggle being able to place the block in the overworld.");
            put("can_place_in_nether", "Toggle being able to place the block in the nether.");
            put("can_place_in_end", "Toggle being able to place the block in the end.");
            put("can_break", "Toggle being able to break the block.");
            put("can_interact", "Toggle being able to interact with the block.");
            put("works", "Toggle the block being able to carry out its function.");
            put("friction_factor", "Control how much friction is applied to entities on the block.");
            put("speed_factor", "Control how fast entities can travel on the block relative to others.");
            put("jump_factor", "Control how high entities can jump on the block relative to others.");
            put("can_be_filled_by_dripstone", "Toggle the block being able to be filled by dripstone.");
            put("redstone_delay", "Control the redstone delay of the block.");
            put("redstone_duration", "Control the redstone duration of the block.");
            put("can_drop_xp", "Toggle the block being able to drop XP.");
            put("can_fall", "Toggle the block being able to fall.");
            put("can_be_trampled", "Toggle farmland being able to be trampled by the player.");
            put("alpha_behaviour", "Toggle tnt behaving like it did in alpha.");
            put("opening_blockable", "Toggle solid blocks or cats blocking the opening of the container.");
            put("cooldown", "Control the cooldown of the portal.");
            put("push_behaviour", "Control the behaviour of the block being pushed by a piston.");
            put("ignited_by_lava", "Toggle the block being able to be ignited by lava.");
            put("destroy_speed", "Control how fast the block is destroyed.");
            put("requires_correct_tool_for_drops", "Toggle whether the block requires the correct tool to drop its drops.");
            put("burn_odds", "Control the chance that the block will burn.");
            put("ignite_odds", "Control the chance that the block will ignite.");
        }});

        itemData.put("enchantment", new Object2ObjectOpenHashMap<>() {{
            enchantmentRegistry.keySet().forEach(enchantment ->
                    put(enchantment + "_enchantment", "Toggle the " + cleanup(enchantment) + " enchantment being able to be applied to the item."));
        }});
        itemData.put("potion", new Object2ObjectOpenHashMap<>() {{
            potionRegistry.keySet().forEach(potion ->
                    put(potion + "_effect", "Toggle the " + cleanup(potion) + " potion effect being able to be applied to the item."));
        }});
        itemData.put("other", new Object2ObjectOpenHashMap<>() {{
            put("works", "Toggle the item being able to carry out its purpose.");
            put("durability", "Control the durability of the item.");
            put("burns", "Toggle the item being able to burn in fire or lava.");
            put("can_spam", "Toggle being able to spam the bow/crossbow");
            put("nutrition", "Control the nutrition of the item.");
            put("saturation", "Control the saturation of the item.");
            put("can_break_blocks_in_creative", "Toggle whether the item can be used to break blocks in creative mode.");
            put("dispenser_interaction", "Toggle the item having a special interaction with a dispenser.");
            put("cauldron_interaction", "Toggle the item having a special interaction with a cauldron.");
            put("fuel_duration", "Controls how long a fuel lasts in a furnace, blast furnace, or smoker.");
        }});

        server.getAdvancements().getAllAdvancements()
                .stream().map(a -> a.getId().toString()).filter(a -> !a.contains("recipe")).forEach(advancement ->
                        otherData.put(advancement, "Toggle the " + cleanup(advancement) + " advancement being obtainable."));
        new Commands(Commands.CommandSelection.ALL, Commands.createValidationContext(VanillaRegistries.createLookup()))
                .getDispatcher().getRoot().getChildren().stream().map(commandNode -> "/" + commandNode.getName()).forEach(command ->
                        otherData.put(command, "Toggle the /" + cleanup(command) + " command being usable."));
        structureRegistry.keySet().forEach(structure ->
                otherData.put(structure + "_structure", "Toggle the " + cleanup(structure) + " structure being able to be generated."));
        placedFeatureRegistry.keySet().forEach(placedFeature ->
                otherData.put(placedFeature + "_feature", "Toggle the " + cleanup(placedFeature) + " feature being able to be generated."));
        featureRegistry.keySet().forEach(feature ->
                otherData.put(feature + "_feature", "Toggle the " + cleanup(feature) + " feature being able to be generated."));
        biomeRegistry.keySet().forEach(biome ->
                otherData.put(biome + "_biome", "Toggle the " + cleanup(biome) + " biome being able to be generated."));

        intRowMaximums.put("nutrition", 20);
        doubleRowMaximums.put("saturation", 9.9);
        stringColSuggestions.put("push_behaviour", Arrays.stream(PushReaction.values()).map(Enum::name).toList());
        differentDataTypes.add("player");
        differentDataTypes.add("spawning");
        differentDataTypes.add("fluid");
        differentDataTypes.add("other");

        populationDone = true;
    }

    /**
     * Get the name of a block from the registry.
     * @param block The block to get the name of.
     * @return The name of the block.
     */
    public static String getKeyFromBlockRegistry(Block block) {
        return Objects.requireNonNull(blockRegistry.getKey(block)).toString();
    }

    /**
     * Get the name of an item from the registry.
     * @param item The item to get the name of.
     * @return The name of the block.
     */
    public static String getKeyFromItemRegistry(Item item) {
        return Objects.requireNonNull(itemRegistry.getKey(item)).toString();
    }

    /**
     * Generates the default data for the database.
     * @param create Whether to create the tables or not.
     * @param tables The tables to generate data for.
     */
    private static void generateData(boolean create, String tables) {
        try {
            if (create) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS entities(id CLOB NOT NULL, " +
                        cols.get("entities").entrySet().stream().map(entry -> "\"" + entry.getKey() + "\" " + entry.getValue())
                                .collect(Collectors.joining(", ")) + ");");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS blocks(id CLOB NOT NULL, " +
                        cols.get("blocks").entrySet().stream().map(entry -> "\"" + entry.getKey() + "\" " + entry.getValue())
                                .collect(Collectors.joining(", ")) + ");");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS items(id CLOB NOT NULL, " +
                        cols.get("items").entrySet().stream().map(entry -> "\"" + entry.getKey() + "\" " + entry.getValue())
                                .collect(Collectors.joining(", ")) + ");");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS others(id CLOB NOT NULL, enabled BOOLEAN);");
            }

            if (tables.equals("*") || tables.equals("entities")) {
                entities.forEach((key, value) -> {
                    try {
                        statement.executeUpdate("INSERT INTO entities (id, \"" + String.join("\", \"", value.keySet()) + "\") VALUES ('" + key + "', " + String.join(", ", value.values()) + ");");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if (tables.equals("*") || tables.equals("blocks")) {
                blocks.forEach((key, value) -> {
                    try {
                        statement.executeUpdate("INSERT INTO blocks (id, \"" + String.join("\", \"", value.keySet()) + "\") VALUES ('" + key + "', " + String.join(", ", value.values()) + ");");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if (tables.equals("*") || tables.equals("items")) {
                items.forEach((key, value) -> {
                    try {
                        statement.executeUpdate("INSERT INTO items (id, \"" + String.join("\", \"", value.keySet()) + "\") VALUES ('" + key + "', " + String.join(", ", value.values()) + ");");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            if (tables.equals("*") || tables.equals("others")) {
                others.forEach((key) -> {
                    try {
                        statement.executeUpdate("INSERT INTO others (id, enabled) VALUES ('" + key + "', true);");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Inserts default values into the database the first time, updates the database between versions.
     */
    public static void handleDatabase() {
        PATH = server.getWorldPath(LevelResource.ROOT) + "/vanilla_disable.sql";

        try {
            connection = DriverManager.getConnection("jdbc:h2:mem:vd");
            statement = connection.createStatement();

            generateData(true, "*");

            if (new File(PATH).exists()) {
                Scanner scanner = new Scanner(new File(PATH));
                while (scanner.hasNext()) {
                    try {
                        statement.execute(scanner.nextLine());
                    } catch (SQLException ignored) {}
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down the database connection when necessary.
     */
    public static void closeConnection() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the connection is null.
     * @return Whether the connection is null.
     */
    public static boolean isConnectionNull() {
        return connection == null;
    }

    /**
     * Saves commands to an .sql file for persistence.
     * @param command The command to save.
     */
    public static void writeToFile(String command) {
        try {
            FileWriter writer = new FileWriter(PATH, true);
            writer.write(command + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets a single row-column pair to a value.
     * @param table The table in which to set the value.
     * @param row The row in which to set the value.
     * @param column The column in which to set the value.
     * @param value The value to set.
     * @param isString Whether the value is a string.
     */
    public static void setValue(String table, String row, String column, String value, boolean isString) {
        if (isString) {
            value = "'" + value + "'";
        }
        try {
            String query = "UPDATE " + table + " SET \"" + column + "\" = " + value + " WHERE id = '" + row + "';";
            statement.executeUpdate(query);
            writeToFile(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets all rows in a column to a value.
     * @param table The table in which to set the value.
     * @param column The column in which to set the value.
     * @param value The value to set.
     * @param isString Whether the value is a string.
     */
    public static void setAll(String table, String column, String value, boolean isString) {
        if (isString) {
            value = "'" + value + "'";
        }
        try {
            String query = "UPDATE " + table + " SET \"" + column + "\" = " + value + " WHERE \"" + column + "\" IS NOT NULL;";
            statement.executeUpdate(query);
            writeToFile(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets all rows which match a pattern in a column to a value.
     * @param table The table in which to set the value.
     * @param column The column in which to set the value.
     * @param value The value to set.
     * @param isString Whether the value is a string.
     * @param pattern The pattern to match.
     */
    public static void setMatching(String table, String column, String value, boolean isString, String pattern) {
        if (pattern.contains(";") || pattern.contains("SELECT") || pattern.contains("ALTER")) {
            throw new RuntimeException("SQL injection attempted. Command not executed.");
        }
        if (isString) {
            value = "'" + value + "'";
        }
        try {
            String query = "UPDATE " + table + " SET \"" + column + "\" = " + value + " WHERE \"" + column + "\" IS NOT NULL AND id LIKE '" + pattern + "';";
            statement.executeUpdate(query);
            writeToFile(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set all rows in a column to a value if they match a condition.
     * Used for the 'others' table where different groups are handled differently.
     * @param value The value to set.
     * @param condition The condition for which to check when checking whether to set a row.
     */
    public static void setWithCondition(String value, String condition) {
        try {
            String query = "UPDATE others SET enabled = " + value + " WHERE id LIKE '" + condition + "';";
            statement.executeUpdate(query);
            writeToFile(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the default value for a row-column pair.
     * @param table The table from which to get the value.
     * @param row The row from which to get the value.
     * @param column The column from which to get the value.
     * @return The value.
     */
    public static String getDefault(String table, String row, String column) {
        return switch (table) {
            case "blocks" -> blocks.get(row).get(column);
            case "entities" -> entities.get(row).get(column);
            case "items" -> items.get(row).get(column);
            default -> "true";
        };
    }

    /**
     * Get a boolean value from the database.
     * @param table The table from which to get the value.
     * @param row The row from which to get the value.
     * @param column The column from which to get the value.
     * @return The value.
     */
    public static boolean getBoolean(String table, String row, String column) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT \"" + column + "\" FROM " + table + " WHERE id = '" + row + "';");
            resultSet.next();
            boolean bool = resultSet.getBoolean(column);
            resultSet.close();
            return bool;
        } catch (SQLException | NullPointerException ignored) {}
        return Boolean.parseBoolean(getDefault(table, row, column));
    }

    /**
     * Get an integer value from the database.
     * @param table The table from which to get the value.
     * @param row The row from which to get the value.
     * @param column The column from which to get the value.
     * @return The value.
     */
    public static int getInt(String table, String row, String column) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT \"" + column + "\" FROM " + table + " WHERE id = '" + row + "';");
            resultSet.next();
            int integer = resultSet.getInt(column);
            resultSet.close();
            return integer;
        } catch (SQLException | NullPointerException ignored) {}
        return Integer.parseInt(getDefault(table, row, column));
    }

    /**
     * Get a double value from the database.
     * @param table The table from which to get the value.
     * @param row The row from which to get the value.
     * @param column The column from which to get the value.
     * @return The value.
     */
    public static double getDouble(String table, String row, String column) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT \"" + column + "\" FROM " + table + " WHERE id = '" + row + "';");
            resultSet.next();
            double dbl = resultSet.getDouble(column);
            resultSet.close();
            return dbl;
        } catch (SQLException | NullPointerException ignored) {}
        return Double.parseDouble(getDefault(table, row, column));
    }

    /**
     * Get a string value from the database.
     * @param table The table from which to get the value.
     * @param row The row from which to get the value.
     * @param column The column from which to get the value.
     * @return The value.
     */
    public static String getString(String table, String row, String column) {
        if (connection == null) return "";
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT \"" + column + "\" FROM " + table + " WHERE id = '" + row + "';");
            resultSet.next();
            String str = resultSet.getString(column);
            resultSet.close();
            return str;
        } catch (SQLException | NullPointerException ignored) {}
        return getDefault(table, row, column);
    }

    /**
     * Reset all tables in the database by deleting the sql script and resetting the in-memory db.
     */
    public static void resetAll() {
        if (!new File(PATH).exists()) return;
        if (!new File(PATH).delete()) {
            throw new RuntimeException("Could not delete file " + PATH);
        }
        closeConnection();
        handleDatabase();
    }

    /**
     * Reset a single table in the database by removing its commands from the sql script and resetting the in-memory db.
     * @param db The table to reset.
     */
    public static void resetOne(String db) {
        if (!new File(PATH).exists()) return;
        try {
            List<String> lines = FileUtils.readLines(new File(PATH), Charset.defaultCharset())
                    .stream().filter(line -> !line.contains("UPDATE " + db)).toList();
            FileUtils.writeLines(new File(PATH), lines, false);

            statement.executeUpdate("DELETE FROM " + db + ";");
            generateData(false, db);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reset some columns in a database by removing them from the sql script and resetting the in-memory db.
     * @param db The table to reset.
     * @param cols The columns to reset.
     */
    public static void resetPartial(String db, ObjectSet<String> cols) {
        if (!new File(PATH).exists()) return;
        try {
            List<String> lines = FileUtils.readLines(new File(PATH), Charset.defaultCharset())
                    .stream().filter(line -> {
                        if (line.contains("UPDATE " + db)) {
                            return cols.stream().noneMatch(line::contains);
                        }
                        return true;
                    }).toList();
            FileUtils.writeLines(new File(PATH), lines, false);

            statement.executeUpdate("DELETE FROM " + db + ";");
            generateData(false, db);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
