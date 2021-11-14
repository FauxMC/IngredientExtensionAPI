package com.jarhax.ingredientextension;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

    public static final String MODID = "ingredient-extension-api";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final int NETWORK_MARKER = MODID.hashCode();
    public static final ResourceLocation VANILLA_INGREDIENT_TYPE = new ResourceLocation(MODID, "vanilla");
}
