package com.wdiscute.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.nikdo53.neobackports.io.StreamCodec;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record DataEntry<T>(ResourceLocation rl, Codec<T> codec)
{
    private static final Gson GSON = new Gson();
    public static final Map<DataEntry<?>, Object> MAP = new HashMap<>();
    public static final Map<ResourceLocation, DataEntry<?>> SYNC_ENTRIES_BY_ID = new HashMap<>();
    public static final Map<DataEntry<?>, StreamCodec<?>> STREAM_CODECS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public T get()
    {
        return (T) MAP.get(this);
    }

    public DataEntry<T> sync(StreamCodec<T> streamCodec)
    {
        STREAM_CODECS.put(this, streamCodec);
        SYNC_ENTRIES_BY_ID.put(rl, this);
        return this;
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

                        //if parsed correctly put new value, otherwise put old value
                        result.resultOrPartial(error -> LogUtils.getLogger().error("Failed to parse {}: {}", entry.rl, error))
                                .ifPresentOrElse(value -> values.put(entry, value), () -> values.put(entry, MAP.get(entry)));

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

    public record MultiEntry<T>(ResourceLocation path, Codec<List<T>> codec)
    {
        private static final Gson GSON = new Gson();
        public static final Map<MultiEntry<?>, List<?>> MAP = new HashMap<>();
        public static final Map<ResourceLocation, MultiEntry<?>> SYNC_ENTRIES_BY_ID = new HashMap<>();
        public static final Map<MultiEntry<?>, StreamCodec<?>> STREAM_CODECS = new HashMap<>();

        @SuppressWarnings("unchecked")
        public List<T> get()
        {
            return (List<T>) MAP.get(this);
        }

        public static <T> MultiEntry<T> register(ResourceLocation path, Codec<T> codec)
        {
            MultiEntry<T> entry = new MultiEntry<>(path, codec.listOf());
            MAP.put(entry, List.of());
            return entry;
        }

        public MultiEntry<T> sync(StreamCodec<T> streamCodec)
        {
            STREAM_CODECS.put(this, streamCodec);
            SYNC_ENTRIES_BY_ID.put(path, this);
            return this;
        }

        public static class ListDataEntryReloadListener extends SimplePreparableReloadListener<Map<MultiEntry<?>, List<?>>>
        {
            @Override
            protected Map<MultiEntry<?>, List<?>> prepare(ResourceManager resourceManager, ProfilerFiller profiler)
            {
                Map<MultiEntry<?>, List<?>> values = new HashMap<>();

                //for each registered data entry
                for (MultiEntry<?> dataEntry : MultiEntry.MAP.keySet())
                {
                    var availableJsons = FileToIdConverter.json(dataEntry.path.getNamespace())
                            .listMatchingResourceStacks(resourceManager)
                            .entrySet()
                            .stream()
                            .filter(o -> o.getKey().getPath()
                                    .equals(dataEntry.path.getNamespace() + "/" + dataEntry.path().getPath() + ".json"))
                            .toList();

                    availableJsons.forEach(System.out::println);

                    var availableResources = availableJsons
                            .stream()
                            .flatMap(o -> o.getValue().stream().findAny().stream())
                            .toList();

                    for (Resource resource : availableResources)
                    {
                        try (BufferedReader reader = resource.openAsReader())
                        {
                            JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);

                            var result = dataEntry.codec().parse(JsonOps.INSTANCE, json);

                            List currentList = new ArrayList<>(values.getOrDefault(dataEntry, List.of()));

                            result.resultOrPartial(error -> LogUtils.getLogger().error("Failed to parse {}: {}", dataEntry.path, error))
                                    .ifPresent(currentList::addAll);

                            values.put(dataEntry, currentList);

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                return values;
            }

            @Override
            protected void apply(Map<MultiEntry<?>, List<?>> values, ResourceManager resourceManager, ProfilerFiller profiler)
            {
                MultiEntry.MAP.putAll(values);
            }
        }
    }
}
