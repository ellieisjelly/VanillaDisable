package uk.debb.vanilla_disable.command.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import uk.debb.vanilla_disable.command.data.DataHandler;
import uk.debb.vanilla_disable.command.data.DataType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(Commands.class)
public abstract class MixinCommands {
    @Shadow
    public static LiteralArgumentBuilder<CommandSourceStack> literal(String pString0) {
        return null;
    }

    @Shadow
    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String pString0, ArgumentType<T> pArgumentType1) {
        return null;
    }

    @Shadow
    public abstract CommandDispatcher<CommandSourceStack> getDispatcher();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        Thread t = new Thread(() -> {
            while (!DataHandler.populationDone) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            RegistryAccess registryAccess = DataHandler.server.registryAccess();

            LiteralArgumentBuilder<CommandSourceStack> updateDB = literal("updateDB").executes(context -> {
                DataHandler.updateDB();
                context.getSource().sendSuccess(
                        Component.literal("The database has been updated."),
                        false
                );
                return 1;
            });

            LiteralArgumentBuilder<CommandSourceStack> overallResetDBBuilder = literal("reset");
            LiteralArgumentBuilder<CommandSourceStack> resetDBBuilder = literal("all").executes(context -> {
                DataHandler.resetAll();
                context.getSource().sendSuccess(
                        Component.literal("All databases have been reset."),
                        false
                );
                return 1;
            });
            overallResetDBBuilder.then(resetDBBuilder);
            Stream.of("entities", "blocks", "items", "others").forEach(table -> {
                LiteralArgumentBuilder<CommandSourceStack> tableBuilder = literal(table).executes(context -> {
                    DataHandler.resetOne(table, true);
                    context.getSource().sendSuccess(
                            Component.literal("The " + table + " table has been reset."),
                            false
                    );
                    return 1;
                });
                Object2ObjectMap<String, Object2ObjectMap<String, String>> groups = switch (table) {
                    case "entities" -> DataHandler.entityData;
                    case "blocks" -> DataHandler.blockData;
                    case "items" -> DataHandler.itemData;
                    default -> new Object2ObjectOpenHashMap<>();
                };
                groups.forEach((group, data) -> {
                    tableBuilder.then(literal(group).executes(context -> {
                        DataHandler.resetPartial(table, data.keySet());
                        context.getSource().sendSuccess(
                                Component.literal("The " + group + " group in the " + table + " table has been reset."),
                                false
                        );
                        return 1;
                    }));
                });
                overallResetDBBuilder.then(tableBuilder);
            });

            this.getDispatcher().register(literal("vd").then(literal("rule")
                            .then(majorBuilder("entity", DataHandler.entities, DataHandler.entityData, "entities"))
                            .then(majorBuilder("block", DataHandler.blocks, DataHandler.blockData, "blocks"))
                            .then(majorBuilder("item", DataHandler.items, DataHandler.itemData, "items"))
                            .then(literal("other")
                                    .then(minorBuilderComplex("advancement", DataHandler.server.getAdvancements().getAllAdvancements()
                                            .stream().map(a -> a.getId().toString()).filter(a -> !a.contains("recipe")), "minecraft:%/%"))
                                    .then(minorBuilderComplex("command", this.getDispatcher().getRoot().getChildren().stream().map(commandNode -> "/" + commandNode.getName()), "/%"))
                                    .then(minorBuilderSimple("biome", registryAccess.registryOrThrow(Registries.BIOME).keySet().stream().map(Object::toString)))
                                    .then(minorBuilderSimple("structure", registryAccess.registryOrThrow(Registries.STRUCTURE).keySet().stream().map(Object::toString)))
                                    .then(minorBuilder(new ObjectArrayList<>() {{
                                        add(BuiltInRegistries.FEATURE.keySet().stream().map(Object::toString));
                                        add(registryAccess.registryOrThrow(Registries.PLACED_FEATURE).keySet().stream().map(Object::toString));
                                    }}))
                            )
                    ).then(overallResetDBBuilder).then(updateDB)
            );
        });
        t.start();
    }

    ArgumentType<?> getArgumentTypeForType(DataType type, String col) {
        return switch (type) {
            case BOOLEAN -> BoolArgumentType.bool();
            case INTEGER ->
                    IntegerArgumentType.integer(0, DataHandler.intRowMaximums.getOrDefault(col, Integer.MAX_VALUE));
            case REAL ->
                    DoubleArgumentType.doubleArg(0.0, DataHandler.doubleRowMaximums.getOrDefault(col, Double.MAX_VALUE));
            case STRING -> StringArgumentType.greedyString();
        };
    }

    String getArgumentValueForType(DataType type, CommandContext<?> context) {
        return switch (type) {
            case BOOLEAN -> String.valueOf(BoolArgumentType.getBool(context, "value"));
            case INTEGER -> String.valueOf(IntegerArgumentType.getInteger(context, "value"));
            case REAL -> String.valueOf(DoubleArgumentType.getDouble(context, "value"));
            case STRING -> StringArgumentType.getString(context, "value");
        };
    }

    String validateItemList(String value) {
        List<String> items = BuiltInRegistries.ITEM.keySet().stream().map(Object::toString).toList();
        return String.join(",", Arrays.stream(value.replace(" ", "").split(",")).filter(items::contains).collect(Collectors.toSet()));
    }

    private void execute(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String row, String col, String description, String defaultValue, DataType type) {
        literalArgumentBuilder.executes(context -> {
            String value = switch (type) {
                case BOOLEAN -> String.valueOf(DataHandler.getBoolean(table, row, col));
                case INTEGER -> String.valueOf(DataHandler.getInt(table, row, col));
                case REAL -> String.valueOf(DataHandler.getDouble(table, row, col));
                case STRING -> DataHandler.getString(table, row, col);
            };
            context.getSource().sendSuccess(
                    Component.literal(description + "\nThe current value is: " + value + "\nThe default value is: " + defaultValue.replace("'", "")),
                    false
            );
            return 1;
        }).then(
                argument("value", getArgumentTypeForType(type, col)).executes(context -> {
                    String value = getArgumentValueForType(type, context);
                    if (type.equals(DataType.STRING)) {
                        value = validateItemList(value);
                    }
                    if (value.equals("")) {
                        context.getSource().sendSuccess(
                                Component.literal("The format or the values are invalid."),
                                false
                        );
                        return 0;
                    }
                    DataHandler.setValue(table, row, col, value, type.equals(DataType.STRING));
                    context.getSource().sendSuccess(
                            Component.literal("Successfully set the value to " + value + "."),
                            false
                    );
                    return 1;
                })
        );
    }

    private void execute(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String row, String col, String description, String defaultValue, List<String> options) {
        literalArgumentBuilder.executes(context -> {
            context.getSource().sendSuccess(
                    Component.literal(description + "\nThe current value is: " + DataHandler.getString(table, row, col) + "\nThe default value is: " + defaultValue.replace("'", "")),
                    false
            );
            return 1;
        }).then(
                argument("value", StringArgumentType.word()).suggests((ctx, builder) -> SharedSuggestionProvider.suggest(options, builder)).executes(context -> {
                    String value = StringArgumentType.getString(context, "value");
                    if (!options.contains(value)) {
                        context.getSource().sendFailure(
                                Component.literal("Invalid value.")
                        );
                        return 0;
                    }
                    DataHandler.setValue(table, row, col, value, true);
                    context.getSource().sendSuccess(
                            Component.literal("Successfully set the value to " + value + "."),
                            false
                    );
                    return 1;
                })
        );
    }

    private void execute(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String col, DataType type) {
        literalArgumentBuilder.then(
                argument("value", getArgumentTypeForType(type, col)).executes(context -> {
                    String value = getArgumentValueForType(type, context);
                    if (type.equals(DataType.STRING)) {
                        value = validateItemList(value);
                    }
                    if (value.equals("")) {
                        context.getSource().sendSuccess(
                                Component.literal("The format or the values are invalid."),
                                false
                        );
                        return 0;
                    }
                    DataHandler.setAll(table, col, value, type.equals(DataType.STRING));
                    context.getSource().sendSuccess(
                            Component.literal("Successfully set the values to " + value + "."),
                            false
                    );
                    return 1;
                })
        );
    }

    private void execute(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String col, List<String> options) {
        literalArgumentBuilder.then(
                argument("value", StringArgumentType.word()).suggests((ctx, builder) -> SharedSuggestionProvider.suggest(options, builder)).executes(context -> {
                    String value = StringArgumentType.getString(context, "value");
                    if (!options.contains(value)) {
                        context.getSource().sendFailure(
                                Component.literal("Invalid value.")
                        );
                        return 0;
                    }
                    DataHandler.setAll(table, col, value, true);
                    context.getSource().sendSuccess(
                            Component.literal("Successfully set the values to " + value + "."),
                            false
                    );
                    return 1;
                })
        );
    }

    private void execute(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String condition) {
        literalArgumentBuilder.then(
                argument("value", BoolArgumentType.bool()).executes(context -> {
                    String value = String.valueOf(BoolArgumentType.getBool(context, "value"));
                    DataHandler.setWithCondition(value, condition);
                    context.getSource().sendSuccess(
                            Component.literal("Successfully set the values to " + value + "."),
                            false
                    );
                    return 1;
                })
        );
    }

    private void allCols(LiteralArgumentBuilder<CommandSourceStack> groupBuilder, String table, String row, String group, Object2ObjectMap<String, String> info, ObjectSet<String> possible) {
        groupBuilder.then(literal("all").then(argument("value", BoolArgumentType.bool()).executes(context -> {
            String value = String.valueOf(BoolArgumentType.getBool(context, "value"));
            info.keySet().stream().filter(possible::contains).forEach((groupProperty ->
                    DataHandler.setValue(table, row, groupProperty, value, false)));
            context.getSource().sendSuccess(
                    Component.literal("Successfully set the value of all " + group + " properties to " + value + "."),
                    false
            );
            return 1;
        })));
    }

    private void allCols(LiteralArgumentBuilder<CommandSourceStack> groupBuilder, String table, String group, Object2ObjectMap<String, String> info) {
        groupBuilder.then(literal("all").then(argument("value", BoolArgumentType.bool()).executes(context -> {
            String value = String.valueOf(BoolArgumentType.getBool(context, "value"));
            info.keySet().forEach((groupProperty ->
                    DataHandler.setAll(table, groupProperty, value, false)));
            context.getSource().sendSuccess(
                    Component.literal("Successfully set the value of all " + group + " properties to " + value + "."),
                    false
            );
            return 1;
        })));
    }

    private LiteralArgumentBuilder<CommandSourceStack> majorBuilder(String base, Object2ObjectMap<String, Object2ObjectMap<String, String>> data, Object2ObjectMap<String, Object2ObjectMap<String, String>> otherData, String table) {
        LiteralArgumentBuilder<CommandSourceStack> overallBuilder = literal(base);
        data.forEach((row, map) -> {
            LiteralArgumentBuilder<CommandSourceStack> rowBuilder = literal(row);
            otherData.forEach((group, info) -> {
                Object2ObjectMap<String, String> properties = info.entrySet().stream().filter(entry -> map.containsKey(entry.getKey()))
                        .collect(Object2ObjectOpenHashMap::new, (m, entry) -> m.put(entry.getKey(), entry.getValue()), Object2ObjectOpenHashMap::putAll);
                if (properties.isEmpty()) return;
                LiteralArgumentBuilder<CommandSourceStack> groupBuilder = literal(group);
                properties.forEach((key, value) -> {
                    LiteralArgumentBuilder<CommandSourceStack> propertyBuilder = literal(key);
                    if (DataHandler.stringColSuggestions.containsKey(key)) {
                        execute(propertyBuilder, table, row, key, value, map.get(key), DataHandler.stringColSuggestions.get(key));
                    } else {
                        execute(propertyBuilder, table, row, key, value, map.get(key),
                                DataHandler.cols.get(table).get(key));
                    }
                    groupBuilder.then(propertyBuilder);
                });
                if (!DataHandler.differentDataTypes.contains(group)) {
                    allCols(groupBuilder, table, row, group, info, map.keySet());
                }
                rowBuilder.then(groupBuilder);
            });
            overallBuilder.then(rowBuilder);
        });

        LiteralArgumentBuilder<CommandSourceStack> allBuilder = literal("all");
        otherData.forEach((group, info) -> {
            LiteralArgumentBuilder<CommandSourceStack> groupBuilder = literal(group);
            info.keySet().forEach((property) -> {
                LiteralArgumentBuilder<CommandSourceStack> propertyBuilder = literal(property);
                if (DataHandler.stringColSuggestions.containsKey(property)) {
                    execute(propertyBuilder, table, property, DataHandler.stringColSuggestions.get(property));
                } else {
                    execute(propertyBuilder, table, property, DataHandler.cols.get(table).get(property));
                }
                groupBuilder.then(propertyBuilder);
            });
            if (!DataHandler.differentDataTypes.contains(group)) {
                allCols(groupBuilder, table, group, info);
            }
            allBuilder.then(groupBuilder);
        });
        overallBuilder.then(allBuilder);

        return overallBuilder;
    }

    private LiteralArgumentBuilder<CommandSourceStack> minorBuilderComplex(String base, Stream<String> stream, String condition) {
        return minorBuilder(base, stream, condition, "");
    }

    private LiteralArgumentBuilder<CommandSourceStack> minorBuilderSimple(String base, Stream<String> stream) {
        return minorBuilder(base, stream, "%_" + base, "_" + base);
    }

    private LiteralArgumentBuilder<CommandSourceStack> minorBuilder(String base, Stream<String> stream, String condition, String ending) {
        LiteralArgumentBuilder<CommandSourceStack> overallBuilder = literal(base);
        stream.forEach((property) -> {
            LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
            execute(temp, "others", property + ending, "enabled", DataHandler.otherData.get(property + ending), "true", DataType.BOOLEAN);
            overallBuilder.then(literal(property).then(temp));
        });

        LiteralArgumentBuilder<CommandSourceStack> allBuilder = literal("enabled");
        execute(allBuilder, condition);
        overallBuilder.then(literal("all").then(allBuilder));

        return overallBuilder;
    }

    private LiteralArgumentBuilder<CommandSourceStack> minorBuilder(ObjectList<Stream<String>> streams) {
        LiteralArgumentBuilder<CommandSourceStack> overallBuilder = literal("feature");
        streams.forEach(stream -> stream.forEach((property) -> {
            LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
            execute(temp, "others", property + "_feature", "enabled", DataHandler.otherData.get(property + "_feature"), "true", DataType.BOOLEAN);
            overallBuilder.then(literal(property).then(temp));
        }));

        LiteralArgumentBuilder<CommandSourceStack> allBuilder = literal("all");
        execute(allBuilder, "%_feature");
        overallBuilder.then(allBuilder);

        return overallBuilder;
    }
}
