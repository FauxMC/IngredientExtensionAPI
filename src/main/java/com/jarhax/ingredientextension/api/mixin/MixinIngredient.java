package com.jarhax.ingredientextension.api.mixin;

import com.jarhax.ingredientextension.api.IngredientExtensionAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ingredient.class)
public class MixinIngredient {
    
    @Inject(method = "toNetwork", at = @At("HEAD"))
    private void toNetwork(FriendlyByteBuf friendlyByteBuf, CallbackInfo ci) {
        friendlyByteBuf.writeUtf(IngredientExtensionAPI.MARKER);
        if(this.getClass().equals(Ingredient.class)) {
            friendlyByteBuf.writeUtf(IngredientExtensionAPI.MODID + ":vanilla_ingredient");
        }
        // other mods should write their type?
    }
    
    @Inject(method = "fromNetwork", at = @At("HEAD"))
    private static void fromNetwork(FriendlyByteBuf friendlyByteBuf, CallbackInfoReturnable<Ingredient> cir) {
        if(friendlyByteBuf.readUtf().equals(IngredientExtensionAPI.MARKER)) {
            String type = friendlyByteBuf.readUtf();
            
        }
    }
}
