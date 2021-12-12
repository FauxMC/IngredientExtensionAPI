package com.jarhax.ingredientextension.api.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jarhax.ingredientextension.Constants;
import com.jarhax.ingredientextension.api.ingredient.IngredientExtendable;
import com.jarhax.ingredientextension.api.ingredient.IngredientHelper;
import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ingredient.class)
public class MixinIngredient {
    
    @Inject(method = "toNetwork", at = @At("HEAD"), cancellable = true)
    private void toNetwork(FriendlyByteBuf buf, CallbackInfo ci) {
        
        final Ingredient self = (Ingredient) (Object) this;
        
        // Extended Ingredient
        if(self instanceof IngredientExtendable extended) {
            
            final IIngredientSerializer<?> serializer = extended.getSerializer();
            final ResourceLocation serializerId = IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY.getKey(serializer);
            
            self.dissolve();
            buf.writeInt(Constants.NETWORK_MARKER_EXTENDED);
            buf.writeUtf(serializerId.toString());
            ((IIngredientSerializer) serializer).toNetwork(buf, self);
            ci.cancel();
        }
        
        // Externally Managed Ingredient
        else {
            
            buf.writeInt(Constants.NETWORK_MARKER_VANILLA);
        }
    }
    
    @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
    private static void fromNetwork(FriendlyByteBuf friendlyByteBuf, CallbackInfoReturnable<Ingredient> cir) {
        
        final int marker = friendlyByteBuf.readInt();
        
        // Handle extended ingredients
        if(marker == Constants.NETWORK_MARKER_EXTENDED) {

            final ResourceLocation typeId = ResourceLocation.tryParse(friendlyByteBuf.readUtf());
            final IIngredientSerializer<?> serializer = IngredientHelper.getSerializer(typeId);

            if (serializer != null) {

                cir.setReturnValue(serializer.fromNetwork(friendlyByteBuf));
            }

            else {

                final String errorMessage = "No ingredient serializer found with ID '" + typeId + "'!";
                Constants.LOGGER.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }
        
        // Handle non-vanilla ingredients
        else if(marker != Constants.NETWORK_MARKER_VANILLA) {
            
            final String message = "Failed to deserialize ingredient! Expected a marker with value '" + Constants.NETWORK_MARKER_EXTENDED + "' or '" + Constants.NETWORK_MARKER_VANILLA + "'. Got '" + marker + "' instead.";
            Constants.LOGGER.error(message);
            throw new RuntimeException(message);
        }
    }
    
    @Inject(method = "fromJson(Lcom/google/gson/JsonElement;)Lnet/minecraft/world/item/crafting/Ingredient;", at = @At("HEAD"), cancellable = true)
    private static void fromJson(JsonElement jsonElement, CallbackInfoReturnable<Ingredient> callback) {
        
        if(jsonElement instanceof JsonObject jsonObj && jsonObj.has("type")) {
            
            final ResourceLocation typeId = new ResourceLocation(jsonObj.get("type").getAsString());
            final IIngredientSerializer<?> serializer = IngredientHelper.getSerializer(typeId);
            
            Constants.LOGGER.info(serializer);
            if(serializer != null) {
                
                final Ingredient out = serializer.fromJson(jsonObj);
                callback.setReturnValue(out);
            } else if(!jsonObj.has("ignoreIngredientExtensionAPI")) {
                
                Constants.LOGGER.warn("Tried to read an ingredient of unknown type {}.", typeId);
            }
        }
    }
}