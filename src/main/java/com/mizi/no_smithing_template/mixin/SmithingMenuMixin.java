package com.mizi.no_smithing_template.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    @Shadow @Final private Level level;
    @Shadow @Final private List<SmithingRecipe> recipes;
    @Shadow @Nullable private SmithingRecipe selectedRecipe;

    @SuppressWarnings("ConstantConditions")
    public SmithingMenuMixin() {
        super(null, 0, null, ContainerLevelAccess.NULL);
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    protected void onMayPickup(Player pPlayer, boolean pHasStack, CallbackInfoReturnable<Boolean> cir) {
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        if (this.selectedRecipe != null && this.inputSlots.getItem(0).isEmpty()) {
            if (this.selectedRecipe.isBaseIngredient(base) && this.selectedRecipe.isAdditionIngredient(addition)) {
                cir.setReturnValue(true);
            }
        }
    }


    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void onCreateResult(CallbackInfo ci) {
        ItemStack template = this.inputSlots.getItem(0);
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        if (template.isEmpty() && !base.isEmpty() && !addition.isEmpty()) {
            for (SmithingRecipe recipe : this.recipes) {
                if (recipe.isBaseIngredient(base) && recipe.isAdditionIngredient(addition)) {
                    ItemStack resultStack = recipe.assemble(this.inputSlots, this.level.registryAccess());

                    if (resultStack.isItemEnabled(this.level.enabledFeatures())) {
                        this.selectedRecipe = recipe;
                        this.resultSlots.setRecipeUsed(recipe);
                        this.resultSlots.setItem(0, resultStack);
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }
}