package com.faux.ingredientextension.mixin;

import com.faux.ingredientextension.api.ingredient.IngredientExtendable;
import com.faux.ingredientextension.api.ingredient.IngredientHelper;
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

    /**
     * This Mixin patches {@link ShapelessRecipe#matches(CraftingContainer, Level)} to account for non-vanilla
     * ingredients. The vanilla code for shapeless recipes use cached properties such as {@link
     * Ingredient#getStackingIds()} rather than testing ingredients with their {@link Ingredient#test(ItemStack)}
     * method. This behavior is undesirable for custom ingredients which rely on testing for things like ItemStack
     * sensitive matching.
     * <p>
     * These issues are circumvented by replacing the vanilla logic with new logic that uses a Bipartite graph. This
     * logic will only be used if one or more ingredients in the recipe are non-standard ingredients that return true
     * from their {@link IngredientExtendable#requiresTesting()} method.
     * <p>
     * This new logic has been tested for compatibility with vanilla ingredients and recipes. The performance
     * characteristics are also comparable to the vanilla implementation.
     */
    @Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("HEAD"), cancellable = true)
    public void matches(CraftingContainer craftingContainer, Level level, CallbackInfoReturnable<Boolean> cir) {

        // Only use our custom logic if it is required.
        if (!IngredientHelper.requiresTesting(this.ingredients)) {

            return;
        }

        final NonNullList<ItemStack> containerItems = NonNullList.create();
        int itemCount = 0;

        for (int i = 0; i < craftingContainer.getContainerSize(); ++i) {

            final ItemStack itemStack = craftingContainer.getItem(i);

            if (!itemStack.isEmpty()) {

                itemCount++;
                containerItems.add(itemStack);
            }

        }

        if (itemCount == this.ingredients.size()) {

            cir.setReturnValue(IngredientHelper.testUnorderedMatch(this.getIngredients(), containerItems));
        }
    }
}
