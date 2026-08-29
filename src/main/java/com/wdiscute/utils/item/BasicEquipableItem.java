package com.wdiscute.utils.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;

public class BasicEquipableItem extends Item
{
    public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public BasicEquipableItem(Properties properties, EquipmentSlot slot, Identifier assetID)
    {
        super(properties
                .stacksTo(1)
                .component(
                        DataComponents.EQUIPPABLE,
                        Equippable
                                .builder(slot)
                                .setAsset(ResourceKey.create(ROOT_ID, assetID))
                                .build()
                ));
    }

    public BasicEquipableItem(Properties properties, EquipmentSlot slot, Holder<SoundEvent> equipSound, Identifier assetID)
    {
        super(properties
                .component(
                DataComponents.EQUIPPABLE,
                Equippable
                        .builder(slot)
                        .setEquipSound(equipSound)
                        .setAsset(ResourceKey.create(ROOT_ID, assetID))
                        .build()
        ));
    }
}
