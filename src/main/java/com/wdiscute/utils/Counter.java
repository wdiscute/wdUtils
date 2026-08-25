package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Counter<T>
{
    private final Map<T, Integer> map;

    public Counter()
    {
        this.map = new HashMap<>();
    }

    public Counter(Map<T, Integer> map)
    {
        this.map = new HashMap<>(map);
    }

    public Counter<T> add(T entry)
    {
        return add(entry, 1);
    }

    public Counter<T> add(T entry, int count)
    {
        map.merge(entry, count, Integer::sum);
        return this;
    }

    public Counter<T> set(T entry, int count)
    {
        map.put(entry, count);
        return this;
    }

    public Counter<T> reset(T entry)
    {
        map.remove(entry);
        return this;
    }

    public int get(T entry)
    {
        return map.getOrDefault(entry, 0);
    }

    public static <T> Codec<Counter<T>> codec(Codec<T> codec, String name)
    {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.unboundedMap(codec, Codec.INT)
                                .fieldOf(name)
                                .forGetter(o -> Collections.unmodifiableMap(o.map))
                ).apply(instance, Counter::new)
        );
    }

    public static <T> Codec<Counter<T>> codec(Codec<T> codec)
    {
        return codec(codec, "counter");
    }

    public static <T> StreamCodec<ByteBuf, Counter<T>> streamCodec(StreamCodec<ByteBuf, T> codec)
    {
        StreamCodec<ByteBuf, Map<T, Integer>> mapCodec = ByteBufCodecs.map(
                HashMap::new, codec,
                ByteBufCodecs.VAR_INT
        );

        return mapCodec.map(Counter::new, counter -> counter.map);
    }

    public static <T> StreamCodec<FriendlyByteBuf, Counter<T>> streamCodecFriendly(StreamCodec<FriendlyByteBuf, T> codec)
    {
        StreamCodec<FriendlyByteBuf, Map<T, Integer>> mapCodec = ByteBufCodecs.map(
                HashMap::new, codec,
                ByteBufCodecs.VAR_INT
        );

        return mapCodec.map(Counter::new, counter -> counter.map);
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, Counter<T>> streamCodecRegistryFriendly(StreamCodec<RegistryFriendlyByteBuf, T> codec)
    {
        StreamCodec<RegistryFriendlyByteBuf, Map<T, Integer>> mapCodec = ByteBufCodecs.map(
                HashMap::new, codec,
                ByteBufCodecs.VAR_INT
        );

        return mapCodec.map(Counter::new, counter -> counter.map);
    }
}
