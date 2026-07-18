package com.wdiscute.utils.compat;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class EmiCompat
{
    public static void displayRecipes(ItemStack is)
    {
        EmiApi.displayRecipes(EmiIngredient.of(Ingredient.of(is)));
    }
}
