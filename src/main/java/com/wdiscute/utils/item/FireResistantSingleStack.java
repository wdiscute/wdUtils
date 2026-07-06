package com.wdiscute.utils.item;

import net.minecraft.world.item.Item;

public class FireResistantSingleStack extends Item
{
    public FireResistantSingleStack()
    {
        super(new Properties().stacksTo(1).fireResistant());
    }
}
