package com.mizi.no_smithing_template;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        BUILDER.push("General Settings");
        BLACKLIST = BUILDER
                .comment(
                        "Which items should still require a smithing template?",
                        "Supported formats: 'modid' for an entire mod or 'modid:itemid' for a specific item."
                )
                .defineList("forcedTemplateItems", List.of(), obj -> obj instanceof String);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private Config() {}

    public static boolean isTemplateRequired(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (rl == null) {
            return false;
        }

        List<? extends String> list = BLACKLIST.get();
        String fullPath = rl.toString();
        String modId = rl.getNamespace();

        return list.contains(fullPath) || list.contains(modId);
    }
}
