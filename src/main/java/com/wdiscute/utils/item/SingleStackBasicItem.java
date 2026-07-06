package com.wdiscute.utils.item;

import net.minecraft.world.item.Item;

public class SingleStackBasicItem extends Item
{
    public SingleStackBasicItem()
    {
        super(new Properties().stacksTo(1));
    }
}
