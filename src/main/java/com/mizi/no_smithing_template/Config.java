package com.mizi.no_smithing_template;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        BUILDER.push("General Settings");
        BLACKLIST = BUILDER
                .comment("Which items [FORCE REQUIRE] a smithing template for upgrading?",
                        "Supported formats: 'modid' (entire mod) or 'modid:itemid' (specific item)",
                        "哪些物品升级时【强制需要】锻造模板？",
                        "支持格式: 'modid' (禁用整个模组) 或 'modid:itemid' (禁用特定物品)")
                .defineList("forcedTemplateItems", List.of(), obj -> obj instanceof String);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public static boolean isTemplateRequired(ItemStack stack) {
        if (stack.isEmpty()) return false;
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (rl == null) return false;

        List<? extends String> list = BLACKLIST.get();
        String fullPath = rl.toString();
        String modId = rl.getNamespace();

        return list.contains(fullPath) || list.contains(modId);
    }
}