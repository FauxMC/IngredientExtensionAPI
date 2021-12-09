package com.jarhax.ingredientextension.example;

import com.google.gson.JsonObject;
import com.jarhax.ingredientextension.api.ingredient.IngredientExtendable;
import com.jarhax.ingredientextension.api.ingredient.serializer.IIngredientSerializer;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

public final class IngredientEnchanted extends IngredientExtendable {

    public static final IIngredientSerializer<IngredientEnchanted> SERIALIZER = new Serializer();

    private final boolean strictMatch;
    private final Enchantment enchantment;

    public IngredientEnchanted(boolean strictMatch, Enchantment enchantment) {

        this.strictMatch = strictMatch;
        this.enchantment = enchantment;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {

        return stack != null && stack.isEnchanted() && EnchantmentHelper.getItemEnchantmentLevel(this.enchantment, stack) > 0;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {

        return SERIALIZER;
    }

    @Override
    public boolean isEmpty() {

        return false;
    }
    
    @Override
    public boolean requiresTesting() {
        return true;
    }
    
    static final class Serializer implements IIngredientSerializer<IngredientEnchanted> {

        @Override
        public IngredientEnchanted fromNetwork(FriendlyByteBuf bytebuf) {

            final ResourceLocation id = new ResourceLocation(bytebuf.readUtf());
            final boolean strictMatch = bytebuf.readBoolean();
            return new IngredientEnchanted(strictMatch, Registry.ENCHANTMENT.get(id));
        }

        @Override
        public IngredientEnchanted fromJson(JsonObject json) {

            final ResourceLocation id = new ResourceLocation(json.get("enchantment").getAsString());
            final boolean strictMatch = json.has("strict") && json.get("strict").getAsBoolean();
            return new IngredientEnchanted(strictMatch, Registry.ENCHANTMENT.get(id));
        }

        @Override
        public void toNetwork(FriendlyByteBuf bytebuf, IngredientEnchanted ingredient) {

            bytebuf.writeUtf(Registry.ENCHANTMENT.getKey(ingredient.enchantment).toString());
            bytebuf.writeBoolean(ingredient.strictMatch);
        }
    }
}
