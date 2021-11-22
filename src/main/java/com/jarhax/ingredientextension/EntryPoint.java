package com.jarhax.ingredientextension;

import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import com.jarhax.ingredientextension.example.IngredientEnchanted;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class EntryPoint implements ModInitializer {

    @Override
    public void onInitialize() {

        Registry.register(IIngredientSerializer.INGREDIENT_SERIALIZER_REGISTRY, new ResourceLocation(Constants.MODID, "enchanted"), IngredientEnchanted.SERIALIZER);
    }
}