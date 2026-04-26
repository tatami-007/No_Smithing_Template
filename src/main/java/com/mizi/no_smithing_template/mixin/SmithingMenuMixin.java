package com.mizi.no_smithing_template.mixin;

import com.mizi.no_smithing_template.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = SmithingMenu.class, priority = 1000, remap = false)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Shadow(remap = false) @Final private Level level;
    @Shadow(remap = false) @Final private List<RecipeHolder<SmithingRecipe>> recipes;
    @Shadow(remap = false) @Nullable private RecipeHolder<SmithingRecipe> selectedRecipe;

    @SuppressWarnings("ConstantConditions")
    public SmithingMenuMixin() {
        super((MenuType<?>) null, 0, (Inventory) null, ContainerLevelAccess.NULL);
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true, remap = false)
    protected void onMayPickup(Player player, boolean hasStack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        if (this.selectedRecipe != null && this.inputSlots.getItem(0).isEmpty() && !Config.isTemplateRequired(base)) {
            SmithingRecipe recipe = this.selectedRecipe.value();
            if (this.createBypassInput(recipe, base, addition) != null) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true, remap = false)
    private void onCreateResult(CallbackInfo ci) {
        ItemStack template = this.inputSlots.getItem(0);
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        if (template.isEmpty() && !base.isEmpty() && !addition.isEmpty()) {
            this.selectedRecipe = null;
            this.resultSlots.setItem(0, ItemStack.EMPTY);

            if (Config.isTemplateRequired(base)) {
                ci.cancel();
                return;
            }

            for (RecipeHolder<SmithingRecipe> recipeHolder : this.recipes) {
                SmithingRecipe recipe = recipeHolder.value();
                SmithingRecipeInput recipeInput = this.createBypassInput(recipe, base, addition);
                if (recipeInput == null) {
                    continue;
                }

                try {
                    ItemStack resultStack = recipe.assemble(recipeInput, this.level.registryAccess());
                    if (resultStack.isEmpty() || !resultStack.isItemEnabled(this.level.enabledFeatures())) {
                        continue;
                    }

                    if (Config.isTemplateRequired(resultStack)) {
                        continue;
                    }

                    this.selectedRecipe = recipeHolder;
                    this.resultSlots.setRecipeUsed(recipeHolder);
                    this.resultSlots.setItem(0, resultStack);
                    ci.cancel();
                    return;
                } catch (Exception ignored) {
                }
            }

            ci.cancel();
        }
    }

    @Nullable
    private SmithingRecipeInput createBypassInput(SmithingRecipe recipe, ItemStack base, ItemStack addition) {
        if (!recipe.isBaseIngredient(base) || !recipe.isAdditionIngredient(addition) || Config.isTemplateRequired(base)) {
            return null;
        }

        ItemStack templateCandidate = this.findTemplateCandidate(recipe);
        if (templateCandidate.isEmpty()) {
            return null;
        }

        SmithingRecipeInput recipeInput = new SmithingRecipeInput(templateCandidate, base, addition);
        return recipe.matches(recipeInput, this.level) ? recipeInput : null;
    }

    private ItemStack findTemplateCandidate(SmithingRecipe recipe) {
        for (var item : BuiltInRegistries.ITEM) {
            ItemStack candidate = new ItemStack(item);
            if (recipe.isTemplateIngredient(candidate)) {
                return candidate;
            }
        }

        return ItemStack.EMPTY;
    }
}
