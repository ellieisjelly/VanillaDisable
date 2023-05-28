package uk.debb.vanilla_disable.command.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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

import java.util.concurrent.TimeUnit;

@Mixin(Commands.class)
public abstract class CommandsMixin {
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
            while (DataHandler.server == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            RegistryAccess registryAccess = DataHandler.server.registryAccess();

            LiteralArgumentBuilder<CommandSourceStack> overallEntityBuilder = literal("entity");
            DataHandler.entities.forEach((entity, pair) -> {
                LiteralArgumentBuilder<CommandSourceStack> entityBuilder = literal(entity);
                for (String property : pair.first()) {
                    LiteralArgumentBuilder<CommandSourceStack> propertyBuilder = literal(property);
                    switch (DataHandler.cols.get("entities").get(property)) {
                        case "BOOLEAN" -> executeBool(propertyBuilder, "entities", entity, property);
                        case "INTEGER" -> executeInt(propertyBuilder, "entities", entity, property);
                        case "REAL" -> executeDouble(propertyBuilder, "entities", entity, property);
                    }
                    entityBuilder.then(propertyBuilder);
                }
                overallEntityBuilder.then(entityBuilder);
            });

            LiteralArgumentBuilder<CommandSourceStack> overallBlockBuilder = literal("block");
            DataHandler.blocks.forEach((block, pair) -> {
                LiteralArgumentBuilder<CommandSourceStack> blockBuilder = literal(block);
                for (String property : pair.first()) {
                    LiteralArgumentBuilder<CommandSourceStack> propertyBuilder = literal(property);
                    switch (DataHandler.cols.get("blocks").get(property)) {
                        case "BOOLEAN" -> executeBool(propertyBuilder, "blocks", block, property);
                        case "INTEGER" -> executeInt(propertyBuilder, "blocks", block, property);
                        case "REAL" -> executeDouble(propertyBuilder, "blocks", block, property);
                    }
                    blockBuilder.then(propertyBuilder);
                }
                overallBlockBuilder.then(blockBuilder);
            });

            LiteralArgumentBuilder<CommandSourceStack> overallItemBuilder = literal("item");
            DataHandler.items.forEach((item, pair) -> {
                LiteralArgumentBuilder<CommandSourceStack> itemBuilder = literal(item);
                for (String property : pair.first()) {
                    LiteralArgumentBuilder<CommandSourceStack> propertyBuilder = literal(property);
                    switch (DataHandler.cols.get("items").get(property)) {
                        case "BOOLEAN" -> executeBool(propertyBuilder, "items", item, property);
                        case "INTEGER" -> executeInt(propertyBuilder, "items", item, property);
                        case "REAL" -> executeDouble(propertyBuilder, "items", item, property);
                    }
                    itemBuilder.then(propertyBuilder);
                }
                overallItemBuilder.then(itemBuilder);
            });

            LiteralArgumentBuilder<CommandSourceStack> overallAdvancementBuilder = literal("advancement");
            DataHandler.server.getAdvancements().getAllAdvancements()
                    .stream().map(a -> a.getId().toString()).filter(a -> !a.contains("recipe")).forEach((advancement) -> {
                        LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
                        executeBool(temp, "others", advancement, "enabled");
                        overallAdvancementBuilder.then(literal(advancement).then(temp));
                    });

            LiteralArgumentBuilder<CommandSourceStack> overallCommandBuilder = literal("command");
            this.getDispatcher().getRoot().getChildren().stream().map(commandNode -> "/" + commandNode.getName()).forEach((command) -> {
                LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
                executeBool(temp, "others", command, "enabled");
                overallCommandBuilder.then(literal(command).then(temp));
            });

            LiteralArgumentBuilder<CommandSourceStack> overallBiomeBuilder = literal("biome");
            registryAccess.registryOrThrow(Registries.BIOME).keySet().stream().map(Object::toString).forEach((biome) -> {
                LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
                executeBool(temp, "others", biome, "enabled");
                overallBiomeBuilder.then(literal(biome).then(temp));
            });

            LiteralArgumentBuilder<CommandSourceStack> overallStructureBuilder = literal("structure");
            registryAccess.registryOrThrow(Registries.STRUCTURE).keySet().stream().map(Object::toString).forEach((structure) -> {
                LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
                executeBool(temp, "others", structure, "enabled");
                overallStructureBuilder.then(literal(structure).then(temp));
            });

            LiteralArgumentBuilder<CommandSourceStack> overallFeatureBuilder = literal("feature");
            BuiltInRegistries.FEATURE.keySet().stream().map(Object::toString).forEach((feature) -> {
                LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
                executeBool(temp, "others", feature, "enabled");
                overallFeatureBuilder.then(literal(feature).then(temp));
            });
            registryAccess.registryOrThrow(Registries.PLACED_FEATURE).keySet().stream().map(Object::toString).forEach((placedFeature -> {
                LiteralArgumentBuilder<CommandSourceStack> temp = literal("enabled");
                executeBool(temp, "others", placedFeature, "enabled");
                overallFeatureBuilder.then(literal(placedFeature).then(temp));
            }));

            this.getDispatcher().register(literal("vd").then(overallEntityBuilder).then(overallBlockBuilder).then(overallItemBuilder).then(overallAdvancementBuilder).then(overallCommandBuilder).then(overallBiomeBuilder).then(overallStructureBuilder).then(overallFeatureBuilder));
        });
        t.start();
    }

    private void executeBool(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String row, String col) {
        literalArgumentBuilder.executes(context -> {
            boolean value = DataHandler.getBoolean(table, row, col);
            context.getSource().sendSuccess(
                    Component.literal(String.valueOf(value)),
                    false
            );
            return 1;
        }).then(
                argument("value", StringArgumentType.string()).suggests((context, builder) -> {
                    builder.suggest("true");
                    builder.suggest("false");
                    return builder.buildFuture();
                }).executes(context -> {
                    String value = StringArgumentType.getString(context, "value");
                    if (DataHandler.setValue(table, row, col, value)) {
                        context.getSource().sendSuccess(
                                Component.literal("success"),
                                false
                        );
                        return 1;
                    } else {
                        context.getSource().sendFailure(
                                Component.literal("failure")
                        );
                        return 0;
                    }
                })
        );
    }

    private void executeInt(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String row, String col) {
        literalArgumentBuilder.executes(context -> {
            int value = DataHandler.getInt(table, row, col);
            context.getSource().sendSuccess(
                    Component.literal(String.valueOf(value)),
                    false
            );
            return 1;
        }).then(
                argument("value", IntegerArgumentType.integer()).executes(context -> {
                    String value = String.valueOf(IntegerArgumentType.getInteger(context, "value"));
                    if (DataHandler.setValue(table, row, col, value)) {
                        context.getSource().sendSuccess(
                                Component.literal("success"),
                                false
                        );
                        return 1;
                    } else {
                        context.getSource().sendFailure(
                                Component.literal("failure")
                        );
                        return 0;
                    }
                })
        );
    }

    private void executeDouble(LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, String table, String row, String col) {
        literalArgumentBuilder.executes(context -> {
            double value = DataHandler.getDouble(table, row, col);
            context.getSource().sendSuccess(
                    Component.literal(String.valueOf(value)),
                    false
            );
            return 1;
        }).then(
                argument("value", DoubleArgumentType.doubleArg()).executes(context -> {
                    String value = String.valueOf(DoubleArgumentType.getDouble(context, "value"));
                    if (DataHandler.setValue("entities", row, col, value)) {
                        context.getSource().sendSuccess(
                                Component.literal("success"),
                                false
                        );
                        return 1;
                    } else {
                        context.getSource().sendFailure(
                                Component.literal("failure")
                        );
                        return 0;
                    }
                })
        );
    }
}
