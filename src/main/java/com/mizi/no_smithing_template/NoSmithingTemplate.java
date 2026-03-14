package com.mizi.no_smithing_template;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(NoSmithingTemplate.MODID)
public class NoSmithingTemplate {
    public static final String MODID = "no_smithing_template";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NoSmithingTemplate() {
        LOGGER.info("No Smithing Template Mod Loaded - Ready to craft without templates!");
    }
}
