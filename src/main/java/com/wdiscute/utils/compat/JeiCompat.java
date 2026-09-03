package com.wdiscute.utils.compat;

import com.wdiscute.utils.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JeiCompat implements IModPlugin
{
    @Override
    public Identifier getPluginUid()
    {
        return Utils.rl("wdutils", "jei_plugin");
    }

    public static IRecipesGui iRecipesGui = null;
    public static IFocusFactory iFocusFactory = null;

    public static void displayRecipes(ItemStack is)
    {
        IFocus<ItemStack> focus = iFocusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, is);
        iRecipesGui.show(focus);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime)
    {
        iRecipesGui = jeiRuntime.getRecipesGui();
        iFocusFactory = jeiRuntime.getJeiHelpers().getFocusFactory();
    }
}
