package com.wdiscute.utils.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;

public class BasicEquipableItem extends Item implements Equipable
{
    EquipmentSlot slot;

    public BasicEquipableItem(Properties properties, EquipmentSlot slot)
    {
        super(properties);
        this.slot = slot;
    }

    @Override
    public EquipmentSlot getEquipmentSlot()
    {
        return slot;
    }
}
