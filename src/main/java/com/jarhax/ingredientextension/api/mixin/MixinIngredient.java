package com.jarhax.ingredientextension.api.mixin;

import com.jarhax.ingredientextension.Constants;
import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
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

        final Ingredient self = (Ingredient) (Object) this;
        friendlyByteBuf.writeInt(Constants.NETWORK_MARKER);
        IIngredientSerializer.writeIngredient(friendlyByteBuf, self);
    }

    @Inject(method = "fromNetwork", at = @At("HEAD"))
    private static void fromNetwork(FriendlyByteBuf friendlyByteBuf, CallbackInfoReturnable<Ingredient> cir) {

        final int marker = friendlyByteBuf.readInt();

        if (marker == Constants.NETWORK_MARKER) {

            cir.setReturnValue(IIngredientSerializer.readIngredient(friendlyByteBuf));
        }

        else {

            // TODO error
        }
    }
}
