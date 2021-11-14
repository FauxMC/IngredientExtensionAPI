package com.jarhax.ingredientextension.api;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

public interface IIngredientSerializer<T extends Ingredient> {
    
    T parse(FriendlyByteBuf bytebuf);
    
    T parse(JsonObject json);
    
    void write(FriendlyByteBuf bytebuf, T ingredient);
}
