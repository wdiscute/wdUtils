package com.wdiscute.utils.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.wdiscute.utils.DataEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class DataEntryProvider<T> implements DataProvider
{

    private final DataEntry<T> dataEntry;
    private final PackOutput output;
    private final T data;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public DataEntryProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            DataEntry<T> dataEntry,
            T data
    )
    {
        this.output = output;
        this.dataEntry = dataEntry;
        this.data = data;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput)
    {
        return registries.thenCompose(lookup ->
        {
            DynamicOps<JsonElement> ops =
                    RegistryOps.create(JsonOps.INSTANCE, lookup);

            JsonElement json = dataEntry.codec()
                    .encodeStart(ops, data)
                    .getOrThrow();

            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(dataEntry.rl().getNamespace())
                    .resolve(dataEntry.rl().getPath() + ".json");

            return DataProvider.saveStable(cachedOutput, json, path);
        });
    }

    @Override
    public String getName()
    {
        return toString();
    }
}