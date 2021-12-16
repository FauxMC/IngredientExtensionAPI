package com.faux.ingredientextension.example;

import com.faux.ingredientextension.Constants;
import com.faux.ingredientextension.api.ingredient.IngredientHelper;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class TestEntryPoint implements ModInitializer {

    @Override
    public void onInitialize() {

        Registry.register(IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY, new ResourceLocation(Constants.MODID, "enchanted"), IngredientEnchanted.SERIALIZER);
    }
}