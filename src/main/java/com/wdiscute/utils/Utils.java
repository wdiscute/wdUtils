package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.*;

@Mod(Utils.MOD_ID)
public class Utils
{
    public static final String MOD_ID = "wdutils";

    public static final Random r = new Random();

    public Utils()
    {
    }

    public static ResourceLocation rl(String ns, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }

    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
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

    public static Holder<EntityType<?>> holderEntity(String ns, String path)
    {
        return Holder.Reference.createStandAlone(BuiltInRegistries.ENTITY_TYPE.holderOwner(), ResourceKey.create(Registries.ENTITY_TYPE, rl(ns, path)));
    }

    //0-1
    public static int toColorF(float red, float green, float blue, float alpha)
    {
        int r = Math.round(Math.clamp(red, 0, 1) * 255);
        int g = Math.round(Math.clamp(green, 0, 1) * 255);
        int b = Math.round(Math.clamp(blue, 0, 1) * 255);
        int a = Math.round(Math.clamp(alpha, 0, 1) * 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    //0-1
    public static int toColorI(int red, int green, int blue, int alpha)
    {
        return (Math.clamp(alpha, 0, 255) << 24) | (Math.clamp(red, 0, 255) << 16) | (Math.clamp(green, 0, 255) << 8) | Math.clamp(blue, 0, 255);
    }

    //0x00ff0000 -> returns 0-255
    public static int intToRed(int packedColor)
    {
        return packedColor >> 16 & 255;
    }

    //0x0000ff00 -> returns 0-255
    public static int intToGreen(int packedColor)
    {
        return packedColor >> 8 & 255;
    }

    //0x000000ff -> returns 0-255
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
