package com.wdiscute.utils;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagement
{
    public static List<ItemStack> getListFromInventory(Inventory inventory)
    {
        List<ItemStack> stacks = new ArrayList<>();

        for (ItemStack stack : inventory.items)
        {
            if (!stack.isEmpty())
                stacks.add(stack);
        }

        return stacks;
    }

    public static Map<Item, List<ItemStack>> splitIntoItems(Inventory inventory)
    {
        return splitIntoItems(getListFromInventory(inventory));
    }

    public static Map<Item, List<ItemStack>> splitIntoItems(List<ItemStack> items)
    {
        Map<Item, List<ItemStack>> playerItems = new HashMap<>();

        for (ItemStack stack : items)
            if (!stack.isEmpty())
                playerItems
                        .computeIfAbsent(stack.getItem(), key -> new ArrayList<>())
                        .add(stack);

        return playerItems;
    }

    public static boolean hasEnoughItems(List<MaybeStack> cost, Inventory inventory)
    {
        return hasEnoughItems(cost, getListFromInventory(inventory));
    }

    //this does not check for multiple instances of the same item <-> count pair in the cost!
    //MaybeStacks may contain item counts above 64
    //DataComponentPatch is ignored for this method
    public static boolean hasEnoughItems(List<MaybeStack> cost, List<ItemStack> items)
    {
        var playerItems = splitIntoItems(items);

        for (MaybeStack costmaybeStack : cost)
        {
            if (!playerItems.containsKey(costmaybeStack.toItem())) return false;

            int count = 0;
            for (ItemStack stack : playerItems.get(costmaybeStack.toItem()))
            {
                count += stack.getCount();
            }

            if (count < costmaybeStack.count()) return false;
        }

        return true;
    }

    public static void payItems(List<MaybeStack> costToRemove, Inventory inventory)
    {
        payItems(costToRemove, getListFromInventory(inventory));
    }

    //this does not check if the player has the items to pay or not! It will decrease them regardless
    //MaybeStacks may contain item counts above 64
    //DataComponentPatch is ignored for this method
    public static void payItems(List<MaybeStack> costToRemove, List<ItemStack> itemsToRemoveFrom)
    {
        for (MaybeStack costmaybeStack : costToRemove)
        {
            int countRemaining = costmaybeStack.count();
            if (countRemaining == 0) continue;
            for (ItemStack stack : splitIntoItems(itemsToRemoveFrom).getOrDefault(costmaybeStack.toItem(), List.of()))
            {
                //if stack has more than count, then break out since this cost has been paid
                if (stack.getCount() >= countRemaining)
                {
                    stack.shrink(countRemaining);
                    break;
                }

                //if stack doesn't have enough to pay, shrink countRemaining and stack count
                int count = stack.getCount();
                stack.shrink(countRemaining);
                countRemaining -= count;
            }
        }
    }
}
