package com.jarhax.ingredientextension;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

    public static final String MODID = "ingredient-extension-api";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static final ResourceLocation INGREDIENT_TYPE_EXTENDED = new ResourceLocation(MODID, "extended");
    public static final int NETWORK_MARKER_EXTENDED = INGREDIENT_TYPE_EXTENDED.toString().hashCode();

    public static final ResourceLocation INGREDIENT_TYPE_VANILLA = new ResourceLocation(MODID, "vanilla");
    public static final int NETWORK_MARKER_VANILLA = INGREDIENT_TYPE_VANILLA.toString().hashCode();
}
