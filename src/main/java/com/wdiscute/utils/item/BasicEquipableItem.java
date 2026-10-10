package com.wdiscute.utils.item;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Equipable;

public class BasicEquipableItem extends ArmorItem implements Equipable
{
    EquipmentSlot slot;

    public BasicEquipableItem(Properties properties, Holder<ArmorMaterial> material, Type type, EquipmentSlot slot)
    {
        super(material, type, properties);
        this.slot = slot;
    }

    @Override
    public EquipmentSlot getEquipmentSlot()
    {
        return slot;
    }
}
