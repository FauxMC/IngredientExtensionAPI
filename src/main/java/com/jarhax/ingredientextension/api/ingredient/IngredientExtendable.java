package com.jarhax.ingredientextension.api.ingredient;

import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public abstract class IngredientExtendable extends Ingredient {

    protected IngredientExtendable(Stream<? extends Value> stream) {

        super(stream);
    }

    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();
}