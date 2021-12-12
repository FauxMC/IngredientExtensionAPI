package com.jarhax.ingredientextension.api.mixin;

import com.jarhax.ingredientextension.api.ingredient.IngredientHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapelessRecipe.class)
public abstract class MixinShapelessRecipe {
    
    @Shadow
    public abstract NonNullList<Ingredient> getIngredients();
    
    @Shadow
    @Final
    private NonNullList<Ingredient> ingredients;
    
    
    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    public void matches(CraftingContainer craftingContainer, Level level, CallbackInfoReturnable<Boolean> cir) {
        
        if(!IngredientHelper.requiresTesting(this.ingredients)) {
            return;
        }
        NonNullList<ItemStack> containerItems = NonNullList.create();
        int itemCount = 0;
        for(int i = 0; i < craftingContainer.getContainerSize(); ++i) {
            ItemStack itemStack = craftingContainer.getItem(i);
            if(!itemStack.isEmpty()) {
                itemCount++;
                containerItems.add(itemStack);
            }
            
        }
        
        if(itemCount == this.ingredients.size()) {

            // We could return false if the count is not good, but it may mess with other mixins
            cir.setReturnValue(IngredientHelper.testUnorderedMatch(this.getIngredients(), containerItems));
        }

    }
}
