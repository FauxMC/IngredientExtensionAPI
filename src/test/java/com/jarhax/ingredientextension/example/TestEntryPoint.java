package com.jarhax.ingredientextension.example;

import com.jarhax.ingredientextension.Constants;
import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class TestEntryPoint implements ModInitializer {

    @Override
    public void onInitialize() {

        Registry.register(IIngredientSerializer.INGREDIENT_SERIALIZER_REGISTRY, new ResourceLocation(Constants.MODID, "enchanted"), IngredientEnchanted.SERIALIZER);
    }
}