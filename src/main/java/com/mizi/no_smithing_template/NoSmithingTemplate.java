package com.mizi.no_smithing_template;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(NoSmithingTemplate.MODID)
public class NoSmithingTemplate {
    public static final String MODID = "no_smithing_template";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NoSmithingTemplate() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        LOGGER.info("No Smithing Template Mod Loaded - Configured with blacklists!");
        MinecraftForge.EVENT_BUS.register(this);
    }
}