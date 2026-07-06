package com.wdiscute.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public record DataEntry<T>(ResourceLocation rl, Codec<T> codec)
{
    private static final Gson GSON = new Gson();
    public static final Map<DataEntry<?>, Object> MAP = new HashMap<>();

    @SuppressWarnings("unchecked")
    public T get()
    {
        return (T) MAP.get(this);
    }

    public static <T> DataEntry<T> register(ResourceLocation rl, Codec<T> codec, T defaultValue)
    {
        DataEntry<T> entry = new DataEntry<>(rl, codec);
        MAP.put(entry, defaultValue);
        return entry;
    }

    public static class DataEntryReloadListener extends SimplePreparableReloadListener<Map<DataEntry<?>, Object>>
    {
        @Override
        protected Map<DataEntry<?>, Object> prepare(ResourceManager resourceManager, ProfilerFiller profiler)
        {
            Map<DataEntry<?>, Object> values = new HashMap<>(DataEntry.MAP);

            for (DataEntry<?> entry : DataEntry.MAP.keySet())
            {
                ResourceLocation file = Utils.rl(entry.rl().getNamespace(), entry.rl().getPath() + ".json");

                resourceManager.getResource(file).ifPresent(resource ->
                {
                    try (BufferedReader reader = resource.openAsReader())
                    {
                        JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);

                        DataResult<?> result = entry.codec().parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(error -> LogUtils.getLogger().error("Failed to parse {}: {}", entry.rl, error))
                                .ifPresent(value -> values.put(entry, value));

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });
            }

            return values;
        }

        @Override
        protected void apply(Map<DataEntry<?>, Object> values, ResourceManager resourceManager, ProfilerFiller profiler)
        {
            DataEntry.MAP.clear();
            DataEntry.MAP.putAll(values);
        }
    }
}
