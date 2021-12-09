package com.jarhax.ingredientextension.api.recipe;

import com.jarhax.ingredientextension.api.ingredient.IngredientExtendable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.List;

public class ShapelessMatchingHelper {
    

    
    public static boolean tryMatch(List<Ingredient> ingredients, List<ItemStack> containerItems) {
        
        if(ingredients.size() != containerItems.size()) {
            return false;
        }
        
        BipGraph g = new BipGraph(ingredients.size());
        for(int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            boolean matched = false;
            for(int j = 0; j < containerItems.size(); j++) {
                ItemStack itemStack = containerItems.get(j);
                if(ingredient.test(itemStack)) {
                    g.addEdge(i, j);
                    matched = true;
                }
            }
            if(!matched) {
                return false;
            }
        }
        return g.hopcroftKarp() == ingredients.size();
    }
}
