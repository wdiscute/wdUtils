package com.wdiscute.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

import java.util.Random;

@Mod(Utils.MOD_ID)
public class Utils
{
    public static final String MOD_ID = "wdutils";

    public Utils()
    {
    }

    public static final Random r = new Random();

    public static ResourceLocation rl(String ns, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }

    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }

    public static Holder<EntityType<?>> holderEntity(EntityType<?> entityType)
    {
        return Holder.direct(entityType);
    }

    public static Holder<EntityType<?>> holderEntity(String ns, String path)
    {
        return Holder.Reference.createStandAlone(BuiltInRegistries.ENTITY_TYPE.holderOwner(), ResourceKey.create(Registries.ENTITY_TYPE, rl(ns, path)));
    }

    public static boolean alwaysTrue(Object... o)
    {
        return true;
    }

    public static boolean alwaysFalse(Object... o)
    {
        return false;
    }

    public static void nothing(Object... o)
    {
    }

    @EventBusSubscriber(modid = Utils.MOD_ID)
    static class Events
    {
        @SubscribeEvent
        public static void registerReloadListeners(AddReloadListenerEvent event)
        {
            event.addListener(new DataEntry.DataEntryReloadListener());
        }
    }
}
