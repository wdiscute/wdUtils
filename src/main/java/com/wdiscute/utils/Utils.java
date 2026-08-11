package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.*;

@Mod(Utils.MOD_ID)
public class Utils
{
    public static final String MOD_ID = "wdutils";

    public static final Random r = new Random();

    public Utils()
    {
    }

    public static Identifier rl(String ns, String path)
    {
        return Identifier.fromNamespaceAndPath(ns, path);
    }

    public static Identifier rl(String path)
    {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    @SafeVarargs
    public static <T> boolean containsAny(List<T> list, T... contains)
    {
        for (T s : contains)
            if (list.contains(s)) return true;

        return false;
    }

    @SafeVarargs
    public static <T> boolean containsAll(List<T> list, T... contains)
    {
        for (T s : contains)
            if (!list.contains(s)) return false;
        return true;
    }

    @SafeVarargs
    public static <T> boolean containsNone(List<T> list, T... contains)
    {
        return !containsAny(list, contains);
    }

    public static String calculateRealLifeTimeFromTicks(long ticks)
    {
        long ticksRemainingToCalculate = ticks / 20;
        String finalString = "";

        //days
        if (ticksRemainingToCalculate > 86400)
        {
            finalString += ticksRemainingToCalculate / 86400 + "d ";
            ticksRemainingToCalculate = ticksRemainingToCalculate % 86400;
        }

        //hours
        if (ticksRemainingToCalculate > 3600)
        {
            finalString += ticksRemainingToCalculate / 3600 + "h ";
            ticksRemainingToCalculate = ticksRemainingToCalculate % 3600;
        }

        //minutes
        if (ticksRemainingToCalculate > 60)
        {
            finalString += ticksRemainingToCalculate / 60 + "m ";
            ticksRemainingToCalculate = ticksRemainingToCalculate % 60;
        }

        //seconds
        if (ticksRemainingToCalculate > 0)
        {
            finalString += ticksRemainingToCalculate + "s";
        }
        return finalString;
    }

    public static Holder<EntityType<?>> holderEntity(EntityType<?> entityType)
    {
        return Holder.direct(entityType);
    }

    //returns a smoothed value from min to max, cycling once every `time` seconds
    private static final double TAU = Math.PI * 2.0;
    public static float smooth(float min, float max, float time)
    {
        double angle = System.currentTimeMillis() * (TAU / (time * 1000.0));
        return (float) (min + (Math.sin(angle) + 1.0) * 0.5 * (max - min));
    }

    //0-255
    public static int intToRed(int packedColor)
    {
        return packedColor >> 16 & 255;
    }

    //0-255
    public static int intToGreen(int packedColor)
    {
        return packedColor >> 8 & 255;
    }

    //0-255
    public static int intToBlue(int packedColor)
    {
        return packedColor & 255;
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

    public record Duo<F, S>(F first, S second)
    {
        public static <F, S> Codec<Duo<F, S>> codec(
                Codec<F> firstCodec,
                Codec<S> secondCodec
        )
        {
            return RecordCodecBuilder.create(instance -> instance.group(
                    firstCodec.fieldOf("first").forGetter(Duo::first),
                    secondCodec.fieldOf("second").forGetter(Duo::second)
            ).apply(instance, Duo::new));
        }

        public static <F, S> Codec<Duo<F, S>> codec(
                Codec<F> firstCodec, String firstName,
                Codec<S> secondCodec, String secondName
        )
        {
            return RecordCodecBuilder.create(instance -> instance.group(
                    firstCodec.fieldOf(firstName).forGetter(Duo::first),
                    secondCodec.fieldOf(secondName).forGetter(Duo::second)
            ).apply(instance, Duo::new));
        }

        public static <F, S> StreamCodec<ByteBuf, Duo<F, S>> streamCodec(
                StreamCodec<ByteBuf, F> firstCodec,
                StreamCodec<ByteBuf, S> secondCodec
        )
        {
            return StreamCodec.composite(
                    firstCodec, Duo::first,
                    secondCodec, Duo::second,
                    Duo::new);
        }
    }

    public record Trio<F, S, T>(F first, S second, T third)
    {
        public static <F, S, T> Codec<Trio<F, S, T>> codec(
                Codec<F> firstCodec,
                Codec<S> secondCodec,
                Codec<T> thirdCodec
        )
        {
            return RecordCodecBuilder.create(instance -> instance.group(
                    firstCodec.fieldOf("first").forGetter(Trio::first),
                    secondCodec.fieldOf("second").forGetter(Trio::second),
                    thirdCodec.fieldOf("third").forGetter(Trio::third)
            ).apply(instance, Trio::new));
        }

        public static <F, S, T> Codec<Trio<F, S, T>> codec(
                Codec<F> firstCodec, String firstName,
                Codec<S> secondCodec, String secondName,
                Codec<T> thirdCodec, String thirdName
        )
        {
            return RecordCodecBuilder.create(instance -> instance.group(
                    firstCodec.fieldOf(firstName).forGetter(Trio::first),
                    secondCodec.fieldOf(secondName).forGetter(Trio::second),
                    thirdCodec.fieldOf(thirdName).forGetter(Trio::third)
            ).apply(instance, Trio::new));
        }

        public static <F, S, T> StreamCodec<ByteBuf, Trio<F, S, T>> streamCodec(
                StreamCodec<ByteBuf, F> firstCodec,
                StreamCodec<ByteBuf, S> secondCodec,
                StreamCodec<ByteBuf, T> thirdCodec
        )
        {
            return StreamCodec.composite(
                    firstCodec, Trio::first,
                    secondCodec, Trio::second,
                    thirdCodec, Trio::third,
                    Trio::new);
        }
    }

    public record Quad<F, S, T, Q>(F first, S second, T third, Q forth)
    {
        public static <F, S, T, Q> Codec<Quad<F, S, T, Q>> codec(
                Codec<F> firstCodec,
                Codec<S> secondCodec,
                Codec<T> thirdCodec,
                Codec<Q> forthCodec
        )
        {
            return RecordCodecBuilder.create(instance -> instance.group(
                    firstCodec.fieldOf("first").forGetter(Quad::first),
                    secondCodec.fieldOf("second").forGetter(Quad::second),
                    thirdCodec.fieldOf("third").forGetter(Quad::third),
                    forthCodec.fieldOf("forth").forGetter(Quad::forth)
            ).apply(instance, Quad::new));
        }

        public static <F, S, T, Q> Codec<Quad<F, S, T, Q>> codec(
                Codec<F> firstCodec, String firstName,
                Codec<S> secondCodec, String secondName,
                Codec<T> thirdCodec, String thirdName,
                Codec<Q> forthCodec, String forthName
        )
        {
            return RecordCodecBuilder.create(instance -> instance.group(
                    firstCodec.fieldOf(firstName).forGetter(Quad::first),
                    secondCodec.fieldOf(secondName).forGetter(Quad::second),
                    thirdCodec.fieldOf(thirdName).forGetter(Quad::third),
                    forthCodec.fieldOf(forthName).forGetter(Quad::forth)
            ).apply(instance, Quad::new));
        }

        public static <F, S, T, Q> StreamCodec<ByteBuf, Quad<F, S, T, Q>> streamCodec(
                StreamCodec<ByteBuf, F> firstCodec,
                StreamCodec<ByteBuf, S> secondCodec,
                StreamCodec<ByteBuf, T> thirdCodec,
                StreamCodec<ByteBuf, Q> forthCodec
        )
        {
            return StreamCodec.composite(
                    firstCodec, Quad::first,
                    secondCodec, Quad::second,
                    thirdCodec, Quad::third,
                    forthCodec, Quad::forth,
                    Quad::new);
        }
    }


    public static class InventoryManagement
    {
        public static List<ItemStack> getListFromInventory(Inventory inventory)
        {
            List<ItemStack> stacks = new ArrayList<>();

            for (ItemStack stack : inventory)
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
                    int count = stack.count();
                    stack.shrink(countRemaining);
                    countRemaining -= count;
                }
            }
        }
    }

    @EventBusSubscriber(modid = Utils.MOD_ID)
    static class Events
    {
        @SubscribeEvent
        public static void registerReloadListeners(AddServerReloadListenersEvent event)
        {
            event.addListener(rl("wdutils_data_entry_server"), new DataEntry.DataEntryReloadListener());
        }

        @SubscribeEvent
        public static void registerReloadListeners(AddClientReloadListenersEvent event)
        {
            event.addListener(rl("wdutils_data_entry_client"), new DataEntry.DataEntryReloadListener());
        }
    }
}
