package com.faux.ingredientextension.api.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.faux.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.stream.Stream;

/**
 * This class provides an extendable base for defining custom ingredient types. All custom ingredient types managed by
 * our API must extend from this class.
 */
public abstract class IngredientExtendable extends Ingredient {

    /**
     * Constructs an ingredient with an array of values. By default, these values are used for the {@link #isEmpty()}
     * check and for display purposes in places like the recipe book.
     *
     * @param values An array of values used for the {@link #isEmpty()} check and for display purposes in places like
     *               the recipe book. If this stream is empty your ingredient will not work properly without additional
     *               steps!
     */
    protected IngredientExtendable(Value... values) {

        this(Stream.of(values));
    }

    /**
     * Constructs an ingredient with an array of values. By default, these values are used for the {@link #isEmpty()}
     * check and for display purposes in places like the recipe book.
     *
     * @param values An array of values used for the {@link #isEmpty()} check and for display purposes in places like
     *               the recipe book. If this stream is empty your ingredient will not work properly without additional
     *               steps!
     */
    protected IngredientExtendable(ItemLike... values) {

        this(Stream.of(values).map(item -> new Ingredient.ItemValue(new ItemStack(item))));
    }

    /**
     * Constructs an ingredient with an array of values. By default, these values are used for the {@link #isEmpty()}
     * check and for display purposes in places like the recipe book.
     *
     * @param values An array of values used for the {@link #isEmpty()} check and for display purposes in places like
     *               the recipe book. If this stream is empty your ingredient will not work properly without additional
     *               steps!
     */
    protected IngredientExtendable(ItemStack... values) {

        this(Stream.of(values).map(Ingredient.ItemValue::new));
    }

    /**
     * Constructs an ingredient with a stream of values. By default, these values are used for the {@link #isEmpty()}
     * check and for display purposes in places like the recipe book.
     *
     * @param values A stream of values used for the {@link #isEmpty()} check and for display purposes in places like
     *               the recipe book. If this stream is empty your ingredient will not work properly without additional
     *               steps!
     */
    protected IngredientExtendable(Stream<? extends Value> values) {

        super(values);
    }

    /**
     * Handles serializing the ingredient to JSON. We take control of this process and write the serializer type from
     * {@link #getSerializer()} in a format that can be deserialized by our API. We then pass off serialization to the
     * serializer which is responsible for writing all properties specific to the custom ingredient type.
     *
     * @return The ingredient as JSON data.
     */
    @Override
    public final JsonElement toJson() {

        final IIngredientSerializer serializer = this.getSerializer();
        final JsonObject json = new JsonObject();

        // Write the serializer ID to the JSON. This allows us to route the JSON deserialization through the right serializer instance.
        json.addProperty("type", IngredientHelper.INGREDIENT_SERIALIZER_REGISTRY.getKey(serializer).toString());

        // Write properties specific to the custom ingredient by passing through to the serializer.
        serializer.toJson(json, this);

        return json;
    }

    /**
     * Gets the serializer instance that is responsible for serializing this ingredient. This is used to determine which
     * serializer should handle writing the ingredient to the network buffer and writing to JSON.
     *
     * @return The serializer responsible for serializing this ingredient.
     */
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();

    /**
     * Determines if this ingredient requires testing to be done through {@link #test(ItemStack)} or if other cacheable
     * properties like {@link #getStackingIds()} can be used instead. The default behaviour is to return true for
     * non-vanilla ingredients that are also not empty.
     * <p>
     * Vanilla systems like shapeless recipe matching prefer to use cacheable properties like {@link #getStackingIds()}
     * rather than {@link #test(ItemStack)} as a form of optimization. This form of optimization is not possible for
     * ingredients that are not deterministic such as NBT sensitive ingredients. When this method returns true our
     * patches will modify the logic to use {@link #test(ItemStack)} where possible.
     *
     * @return Does this ingredient require testing to be done through {@link #test(ItemStack)}.
     */
    public boolean requiresTesting() {

        return !this.isEmpty();
    }
}