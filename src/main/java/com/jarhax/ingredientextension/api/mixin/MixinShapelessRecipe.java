package com.jarhax.ingredientextension.api.mixin;

import com.jarhax.ingredientextension.api.recipe.ISimpleton;
import com.jarhax.ingredientextension.api.recipe.ShapelessMatchingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShapelessRecipe.class)
public abstract class MixinShapelessRecipe implements ISimpleton {
    
    @Shadow
    public abstract NonNullList<Ingredient> getIngredients();
    
    @Shadow
    @Final
    private NonNullList<Ingredient> ingredients;
    
    /**
     * @author
     */
    @Overwrite
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        StackedContents stackedContents = new StackedContents();
        NonNullList<ItemStack> containerItems = NonNullList.create();
        int itemCount = 0;
        for(int j = 0; j < craftingContainer.getContainerSize(); ++j) {
            ItemStack itemStack = craftingContainer.getItem(j);
            if(itemStack.isEmpty())
                continue;
            ++itemCount;
            if(ingredientextensionapi$isSimple()) {
                stackedContents.accountStack(itemStack, 1);
            } else {
                containerItems.add(itemStack);
            }
            
        }
        if(itemCount != this.ingredients.size()) {
            return false;
        }
        
        if(ingredientextensionapi$isSimple()) {
            return stackedContents.canCraft((ShapelessRecipe) (Object) this, null);
        } else {
            return ShapelessMatchingHelper.tryMatch(this.getIngredients(), containerItems);
        }
        
    }
    
    
    @Override
    public boolean ingredientextensionapi$isSimple() {
        return this.getIngredients().stream().map(ingredient -> ((ISimpleton) ingredient)).allMatch(ISimpleton::ingredientextensionapi$isSimple);
    }
}
