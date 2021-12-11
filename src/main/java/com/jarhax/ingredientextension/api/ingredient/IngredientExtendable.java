package com.jarhax.ingredientextension.api.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.stream.Stream;

public abstract class IngredientExtendable extends Ingredient {
    
    protected IngredientExtendable(Stream<? extends Value> stream) {
        
        super(stream);
    }
    
    protected IngredientExtendable() {
        
        super(Stream.empty());
    }
    
    @Override
    public final JsonElement toJson() {
        
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", IIngredientSerializer.INGREDIENT_SERIALIZER_REGISTRY.getKey(getSerializer()).toString());
        return super.toJson();
    }
    
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();
    
    public boolean requiresTesting() {
        return this != Ingredient.EMPTY;
    }
}