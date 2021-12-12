package com.jarhax.ingredientextension;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class contains universal constants that are used throughout the API. These are primarily intended for internal
 * use.
 */
public class Constants {

    /**
     * The ID used by the mod. This will also be used as the namespace for any assets or registry entries created by
     * this mod.
     */
    public static final String MODID = "ingredient-extension-api";

    /**
     * A standard logger instance. This is used for important error messages and debug information.
     */
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    /**
     * A network marker that is used to denote that an extended ingredient that is managed by this API is being sent
     * across a network buffer.
     */
    public static final int NETWORK_MARKER_EXTENDED = new ResourceLocation(MODID, "extended").toString().hashCode();

    /**
     * A network marker that is used to denote that a non-managed ingredient is being sent across a network buffer. This
     * is used for stuff like vanilla ingredients or ingredients added by similar APIs.
     */
    public static final int NETWORK_MARKER_VANILLA = new ResourceLocation(MODID, "vanilla").toString().hashCode();
}