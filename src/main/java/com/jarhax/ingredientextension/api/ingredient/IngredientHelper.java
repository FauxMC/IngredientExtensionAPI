package com.jarhax.ingredientextension.api.ingredient;

import net.minecraft.world.item.crafting.Ingredient;

import java.util.Collection;

public class IngredientHelper {
    
    public static boolean requiresTesting(Collection<Ingredient> ingredients) {
        for(Ingredient ingredient : ingredients) {
            if(ingredient instanceof IngredientExtendable extendable && extendable.requiresTesting()) {
                return true;
            }
        }
        return false;
    }
    
}
