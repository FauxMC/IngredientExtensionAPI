package com.faux.ingredientextension.api.ingredient;

import com.faux.ingredientextension.Constants;
import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.faux.ingredientextension.util.BipGraph;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;
import java.util.List;

/**
 * A collection of static helper functions used throughout the API and its patches.
 */
public class IngredientHelper {

    /**
     * A registry that holds all ingredient serializers managed by this API.
     */
    public static final Registry<IIngredientSerializer<?>> INGREDIENT_SERIALIZER_REGISTRY = createRegistry(new ResourceLocation(Constants.MODID, "ingredient_serializers"));

    /**
     * Checks if any ingredients within a given collection require special testing through {@link
     * Ingredient#test(ItemStack)}. This allows code like the vanilla shapeless recipes to determine if cached item IDs
     * can be used over testing each stack.
     *
     * @param ingredients The ingredients to test.
     * @return If any of the ingredients require testing through their {@link Ingredient#test(ItemStack)} method.
     */
    public static boolean requiresTesting(Collection<Ingredient> ingredients) {

        for (Ingredient ingredient : ingredients) {

            if (ingredient instanceof IngredientExtendable extendable && extendable.requiresTesting()) {

                return true;
            }
        }

        return false;
    }

    /**
     * Registers a new ingredient serializer with the registry. This is required for the serializer to be usable.
     *
     * @param id         A namespaced identifier that is unique to the registered serializer.
     * @param serializer The serializer to register.
     * @param <T>        The type of serializer being registered.
     * @return The registered serializer.
     */
    public static <T extends IIngredientSerializer<?>> T register(ResourceLocation id, T serializer) {

        return Registry.register(INGREDIENT_SERIALIZER_REGISTRY, id, serializer);
    }

    /**
     * Gets the ingredient serializer registered with the given namespaced identifier.
     *
     * @param id The namespaced identifier of the serializer being requested.
     * @return The serializer if one could be found. If no serializer was found this will return null.
     */
    public static IIngredientSerializer<?> getSerializer(ResourceLocation id) {

        return INGREDIENT_SERIALIZER_REGISTRY.get(id);
    }

    /**
     * Tests if each ingredient has a unique corresponding input that passes its {@link Ingredient#test(ItemStack)}
     * method. This provides similar functionality to a vanilla shapeless crafting recipe matcher.
     *
     * @param ingredients A list of ingredients to test.
     * @param inputs      A list of inputs to test against the ingredients.
     * @return When each ingredient has a unique corresponding input that passes its {@link Ingredient#test(ItemStack)}
     * this will be true.
     */
    public static boolean testUnorderedMatch(List<Ingredient> ingredients, List<ItemStack> inputs) {

        // There must be an equal number of ingredients and inputs for each to have a unique corresponding value.
        if (ingredients.size() == inputs.size()) {

            final BipGraph g = new BipGraph(ingredients.size());

            for (int i = 0; i < ingredients.size(); i++) {

                final Ingredient ingredient = ingredients.get(i);
                boolean matched = false;

                for (int j = 0; j < inputs.size(); j++) {

                    final ItemStack itemStack = inputs.get(j);

                    if (ingredient.test(itemStack)) {

                        g.addEdge(i, j);
                        matched = true;
                    }
                }

                // If an ingredient has no matches we can fail early.
                if (!matched) {

                    return false;
                }
            }
            return g.hopcroftKarp() == ingredients.size();
        }

        return false;
    }

    /**
     * Creates a new modded registry and registers it with the game while preserving generics.
     *
     * @param id  The ID of the registry to create and register.
     * @param <T> The type of value held by the registry. This can just be inferred.
     * @return A registry for the given type.
     */
    private static <T> Registry<T> createRegistry(ResourceLocation id) {

        final WritableRegistry<T> registry = new MappedRegistry<>(ResourceKey.createRegistryKey(id), Lifecycle.stable(), null);
        return FabricRegistryBuilder.from(registry).buildAndRegister();
    }
}