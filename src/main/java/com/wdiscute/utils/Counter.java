package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
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

    public int mostCommon()
    {
        return map.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);
    }

    public int leastCommon()
    {
        return map.values().stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElse(0);
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

    public Map<T, Integer> toMap()
    {
        return Collections.unmodifiableMap(map);
    }

    public static <T> Codec<Counter<T>> codec(Codec<T> codec)
    {
        return codec(codec, "counter");
    }

    public static <T, B extends ByteBuf> StreamCodec<B, Counter<T>> streamCodec(StreamCodec<B, T> codec)
    {
        StreamCodec<B, Map<T, Integer>> mapCodec = ByteBufCodecs.map(
                HashMap::new, codec,
                ByteBufCodecs.VAR_INT
        );

        return mapCodec.map(Counter::new, counter -> counter.map);
    }
}
