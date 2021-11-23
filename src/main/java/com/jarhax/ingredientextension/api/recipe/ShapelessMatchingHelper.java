package com.jarhax.ingredientextension.api.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShapelessMatchingHelper {
    
    public static boolean tryMatch(NonNullList<Ingredient> ingredients, NonNullList<ItemStack> containerItems) {
        
        // TODO We need to go through ingredients, make sure that each ingredient has a matching container item
        // A container item may not be used twice
        if(ingredients.size() != containerItems.size()) {
            return false;
        }
        
        Map<Ingredient, Set<ItemStack>> matchingMap = new HashMap<>();
        // O(2n) iterations to see if a match is even possible
        ingredients.forEach(ingredient -> containerItems.stream().filter(ingredient).forEach(stack -> {
            Set<ItemStack> set = matchingMap.computeIfAbsent(ingredient, ingredient1 -> new HashSet<>());
            set.add(stack);
        }));
        if(matchingMap.size() != ingredients.size()) {
            return false;
        }
        
        // TODO this works fine for our example recipe, but fails for the complicated recipe when the items are not in a good order
        ArrayList<ItemStack> usedItems = new ArrayList<>();
        ingredientIndex:
        for(Ingredient ingredient : ingredients) {
            for(ItemStack containerItem : containerItems) {
                if(usedItems.contains(containerItem)) {
                    continue;
                }
                if(ingredient.test(containerItem)) {
                    usedItems.add(containerItem);
                    continue ingredientIndex;
                }
            }
            // at this point we reached an ingredient with no matches
            return false;
        }
        return usedItems.size() == ingredients.size();
    }
}
