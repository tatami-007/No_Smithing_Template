package com.mizi.no_smithing_template;

import com.mojang.logging.LogUtils;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(NoSmithingTemplate.MODID)
public class NoSmithingTemplate {
    public static final String MODID = "no_smithing_template";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NoSmithingTemplate(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        if (FMLEnvironment.dist.isClient()) {
            modContainer.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (IConfigScreenFactory) (container, parent) -> new ConfigurationScreen(container, parent)
            );
        }

        LOGGER.info("No Smithing Template Mod Loaded - Ready to craft without templates, with blacklist support enabled.");
    }
}
