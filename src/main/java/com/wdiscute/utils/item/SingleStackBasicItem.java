package com.wdiscute.utils.item;

import net.minecraft.world.item.Item;

public class SingleStackBasicItem extends Item
{
    @Deprecated
    public SingleStackBasicItem()
    {
        super(new Properties().stacksTo(1));
    }

    public SingleStackBasicItem(Properties properties)
    {
        super(properties.stacksTo(1));
    }
}
