package com.wdiscute.utils.item;

import net.minecraft.world.item.Item;

public class FireResistantBasicItem extends Item
{

    public FireResistantBasicItem(Properties properties)
    {
        super(properties.fireResistant());
    }

    @Deprecated
    public FireResistantBasicItem()
    {
        super(new Properties().fireResistant());
    }
}
