package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public class Counter<T>
{
    private final Map<T, Integer> map;

    public Counter(Map<T, Integer> map)
    {
        this.map = new HashMap<>(map);
    }

    public Counter<T> add(T entry)
    {
        map.put(entry, map.getOrDefault(entry, 0) + 1);
        return this;
    }

    public Codec<Counter<T>> codec(Codec<T> codec)
    {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.unboundedMap(codec, Codec.INT)
                                .fieldOf("counter")
                                .forGetter(o -> o.map)
                ).apply(instance, Counter::new)
        );
    }
}
