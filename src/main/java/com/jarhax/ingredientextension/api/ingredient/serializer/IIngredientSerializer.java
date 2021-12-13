package com.jarhax.ingredientextension.api.ingredient.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.IOException;

/**
 * Ingredient serializers allow for custom logic to be used when an ingredient is serialized from JSON or the network
 * buffer. In addition to standard vanilla ingredients an Ingredient serializer can also produce custom Ingredient
 * implementations.
 *
 * @param <T> The type of ingredient managed by this serializer.
 */
public interface IIngredientSerializer<T extends Ingredient> {

    /**
     * Appends additional properties managed by the serializer to the JSON data. The appended data should be readable by
     * {@link #fromJson(JsonObject)}.
     * <p>
     * The JSON object will already contain a type property that has been populated with the ID of this serializer. This
     * property is required to match the JSON back up to your serializer during the deserialization process. This work
     * is done for you already and does not need to be duplicated here.
     *
     * @param json       The JSON object to append additional properties to.
     * @param ingredient The ingredient instance to write.
     */
    void toJson(JsonObject json, T ingredient);

    /**
     * Produces an ingredient instance by parsing the provided JSON data.
     *
     * @param json The JSON data to parse.
     * @return An ingredient that was read from the JSON data.
     */
    T fromJson(JsonObject json) throws JsonParseException;

    /**
     * Writes additional properties managed by the serializer to a network buffer. All data written to the buffer must
     * be read from the buffer in the same order it was written.
     *
     * @param bytebuf    The buffer to write additional properties to.
     * @param ingredient The ingredient to write.
     * @throws IOException An IOException may be raised if the ingredient can not be written to the network.
     */
    void toNetwork(FriendlyByteBuf bytebuf, T ingredient) throws IOException;

    /**
     * Produces an ingredient by reading properties from a network buffer. It is critical that all data written to the
     * buffer in {@link #toNetwork(FriendlyByteBuf, Ingredient)} is read here, and that it is read in the same order it
     * was written.
     *
     * @param bytebuf The buffer to read properties from.
     * @return The ingredient that was read.
     * @throws IOException An IOException may be raised if the ingredient can not be read from the network.
     */
    T fromNetwork(FriendlyByteBuf bytebuf) throws IOException;
}