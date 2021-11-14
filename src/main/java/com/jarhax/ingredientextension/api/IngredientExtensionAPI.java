package com.jarhax.ingredientextension.api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class IngredientExtensionAPI implements ModInitializer {
    
    public static final String MODID = "ingredient-extension-api";
    
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    
    public static final String MARKER = MODID + ":custom_ingredient";
    public static final Map<ResourceLocation, IIngredientSerializer<?>> SERIALIZERS = new HashMap<>();
    
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        
        LOGGER.info("Hello Fabric world!");
    }
}
