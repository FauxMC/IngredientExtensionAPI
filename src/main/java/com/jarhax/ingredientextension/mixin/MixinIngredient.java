package com.jarhax.ingredientextension.mixin;

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

    /**
     * This Mixin patches {@link Ingredient#toNetwork(FriendlyByteBuf)} to add support for expanded ingredients. This is
     * done by modifying the network specification. All ingredients will have an additional Integer written to the
     * buffer. This value is used as a flag to determine what serialization method should be used.
     * <p>
     * Ingredients managed by this API will have a flag value of {@value Constants#NETWORK_MARKER_EXTENDED}. This flag
     * will always be followed by a UTF-8 String that contains the registry ID of the serializer that manages this type
     * of ingredient. Additional data may be encoded by the serializer and is specific to each serializer
     * implementation.
     * <p>
     * Vanilla ingredients and Ingredients not managed by this API will have the flag value of {@value
     * Constants#NETWORK_MARKER_VANILLA}. When this flag is encountered we hand network serialization back to the
     * vanilla game.
     */
    @Inject(method = "toNetwork", at = @At("HEAD"), cancellable = true)
    private void toNetwork(FriendlyByteBuf buf, CallbackInfo ci) {

        final Ingredient self = (Ingredient) (Object) this;

        // Extended Ingredient
        if (self instanceof IngredientExtendable extended) {

            final IIngredientSerializer<?> serializer = extended.getSerializer();
            final ResourceLocation serializerId = IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY.getKey(serializer);

            self.dissolve();
            buf.writeInt(Constants.NETWORK_MARKER_EXTENDED);
            buf.writeUtf(serializerId.toString());

            try {

                ((IIngredientSerializer) serializer).toNetwork(buf, self);
            }

            catch (Exception e) {

                Constants.LOGGER.error("Failed to write Ingredient to network! ID={}, SerializerClass={} IngredientClass={}, Ingredient={}", serializerId, serializer.getClass().getName(), self.getClass().getName(), self);
                Constants.LOGGER.catching(e);
                throw new RuntimeException(e);
            }

            ci.cancel();
        }

        // Externally Managed Ingredient
        else {

            buf.writeInt(Constants.NETWORK_MARKER_VANILLA);
        }
    }

    /**
     * This Mixin patches {@link Ingredient#fromNetwork(FriendlyByteBuf)} to add support for expanded ingredients. This
     * is done by modifying the network specification. All ingredients will have an additional Integer written to the
     * buffer. This value is used as a flag to determine what serialization method should be used.
     * <p>
     * Ingredients managed by this API will have a flag value of {@value Constants#NETWORK_MARKER_EXTENDED}. This flag
     * will always be followed by a UTF-8 String that contains the registry ID of the serializer that manages this type
     * of ingredient. Additional data may be encoded by the serializer and is specific to each serializer
     * implementation.
     * <p>
     * Vanilla ingredients and Ingredients not managed by this API will have the flag value of {@value
     * Constants#NETWORK_MARKER_VANILLA}. When this flag is encountered we hand network serialization back to the
     * vanilla game.
     */
    @Inject(method = "fromNetwork", at = @At("HEAD"), cancellable = true)
    private static void fromNetwork(FriendlyByteBuf friendlyByteBuf, CallbackInfoReturnable<Ingredient> cir) {

        final int marker = friendlyByteBuf.readInt();

        // Handle extended ingredients
        if (marker == Constants.NETWORK_MARKER_EXTENDED) {

            final ResourceLocation typeId = ResourceLocation.tryParse(friendlyByteBuf.readUtf());
            final IIngredientSerializer<?> serializer = IngredientHelper.getSerializer(typeId);

            if (serializer != null) {

                try {

                    cir.setReturnValue(serializer.fromNetwork(friendlyByteBuf));
                }

                catch (Exception e) {

                    Constants.LOGGER.error("Failed to write Ingredient to network! ID={}, SerializerClass={}", typeId, serializer.getClass().getName());
                    Constants.LOGGER.catching(e);
                    throw new RuntimeException(e);
                }
            }

            else {

                final String errorMessage = "No ingredient serializer found with ID '" + typeId + "'!";
                Constants.LOGGER.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        }

        // Handle non-vanilla ingredients
        else if (marker != Constants.NETWORK_MARKER_VANILLA) {

            final String message = "Failed to deserialize ingredient! Expected a marker with value '" + Constants.NETWORK_MARKER_EXTENDED + "' or '" + Constants.NETWORK_MARKER_VANILLA + "'. Got '" + marker + "' instead.";
            Constants.LOGGER.error(message);
            throw new RuntimeException(message);
        }
    }

    /**
     * This Mixin patches {@link Ingredient#fromJson(JsonElement)} to add support for additional Ingredient types. If
     * the JSON data is an object and contains a type property we will attempt to match the value of that type to an
     * Ingredient serializer using {@link IngredientHelper#getSerializer(ResourceLocation)}. If the ID matches up to a
     * registered serializer the deserialization process will be handed off to them.
     * <p>
     * It is conceivable that other mods and frameworks may wish to use the type property for their own unrelated
     * systems. In these scenarios the warning of an unknown ingredient type will be undesirable. This warning can be
     * disabled by including a boolean with the name {@literal ignoreIngredientExtensionAPI} and the value {@literal
     * true}.
     */
    @Inject(method = "fromJson(Lcom/google/gson/JsonElement;)Lnet/minecraft/world/item/crafting/Ingredient;", at = @At("HEAD"), cancellable = true)
    private static void fromJson(JsonElement jsonElement, CallbackInfoReturnable<Ingredient> callback) {

        // Only process JSON objects with a type property.
        if (jsonElement instanceof JsonObject jsonObj && jsonObj.has("type")) {

            final ResourceLocation typeId = new ResourceLocation(jsonObj.get("type").getAsString());
            final IIngredientSerializer<?> serializer = IngredientHelper.getSerializer(typeId);

            // If the type matches up to a known serializer hand off the deserialization.
            if (serializer != null) {

                final Ingredient out = serializer.fromJson(jsonObj);
                callback.setReturnValue(out);
            }

            // If the type does not match up attempt to post a warning.
            else if (!jsonObj.has("ignoreIngredientExtensionAPI")) {

                Constants.LOGGER.warn("Tried to read an ingredient of unknown type {}.", typeId);
            }
        }
    }
}