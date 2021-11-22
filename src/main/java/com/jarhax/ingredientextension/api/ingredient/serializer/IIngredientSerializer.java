package com.jarhax.ingredientextension.api.ingredient.serializer;

import com.google.gson.JsonObject;
import com.jarhax.ingredientextension.Constants;
import com.jarhax.ingredientextension.api.ingredient.IngredientExtendable;
import com.mojang.datafixers.kinds.Const;
import com.mojang.serialization.Lifecycle;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface IIngredientSerializer<T extends Ingredient> {

    public static final Registry<IIngredientSerializer<?>> INGREDIENT_SERIALIZER_REGISTRY = createRegistry(new ResourceLocation(Constants.MODID, "ingredient_serializers"));

    T parse(FriendlyByteBuf bytebuf);

    T parse(JsonObject json);

    void write(FriendlyByteBuf bytebuf, T ingredient);

    @Nullable
    public static ResourceLocation getSerializerId(Ingredient ingredient) {

        if (ingredient instanceof IngredientExtendable extended) {

            return INGREDIENT_SERIALIZER_REGISTRY.getKey(extended.getSerializer());
        }

        return null;
    }

    public static <T extends Ingredient> void writeIngredient(FriendlyByteBuf buf, T ingredient) {

    }

    public static Ingredient readIngredient(FriendlyByteBuf buf) {

        final ResourceLocation typeId = ResourceLocation.tryParse(buf.readUtf());
        final IIngredientSerializer<?> serializer = getSerializer(typeId);

        if (serializer != null) {

            return serializer.parse(buf);
        }

        final String errorMessage = "No ingredient serializer found with ID '" + typeId + "'!";
        Constants.LOGGER.error(errorMessage);
        throw new RuntimeException(errorMessage);
    }

    /**
     * Registers a new ingredient serializer with the registry.
     *
     * @param id         The ID of the value being registered.
     * @param serializer The serializer being registered.
     * @param <T>        The type of the serializer being registered.
     * @return The serializer being registered.
     */
    public static <T extends IIngredientSerializer<?>> T register(ResourceLocation id, T serializer) {

        Registry.register(INGREDIENT_SERIALIZER_REGISTRY, id, serializer);
        return serializer;
    }

    public static IIngredientSerializer<?> getSerializer(ResourceLocation id) {

        return INGREDIENT_SERIALIZER_REGISTRY.get(id);
    }

    /**
     * Creates a new modded registry and registers it with the game. While this method is not strictly needed it allows
     * us to handle generics a bit cleaner.
     *
     * @param id  The ID of the registry to create and register.
     * @param <T> The type of value held by the registry. This can just be inferred.
     * @return A registry for the given type.
     */
    private static <T> Registry<T> createRegistry(ResourceLocation id) {

        final WritableRegistry<T> registry = new MappedRegistry<>(ResourceKey.createRegistryKey(id), Lifecycle.stable());
        return FabricRegistryBuilder.from(registry).buildAndRegister();
    }
}