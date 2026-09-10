package com.wdiscute.utils.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.wdiscute.utils.DataEntry;
import com.wdiscute.utils.Utils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DataEntryProvider<T> implements DataProvider
{
    private final DataEntry<T> dataEntry;
    private final PackOutput output;
    private final T data;

    public DataEntryProvider(PackOutput output, DataEntry<T> dataEntry, T data)
    {
        this.output = output;
        this.dataEntry = dataEntry;
        this.data = data;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput)
    {

        JsonElement json = dataEntry.codec()
                .encodeStart(JsonOps.INSTANCE, data)
                .getOrThrow(false, Utils::nothing);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(dataEntry.rl().getNamespace())
                .resolve(dataEntry.rl().getPath() + ".json");

        return DataProvider.saveStable(cachedOutput, json, path);
    }

    @Override
    public String getName()
    {
        return toString();
    }

    public static class MultiEntry<T> implements DataProvider
    {
        private final DataEntry.MultiEntry<T> dataEntry;
        private final PackOutput output;
        private final List<T> data;
        private String customDatapackName;

        public MultiEntry(PackOutput output, DataEntry.MultiEntry<T> dataEntry, List<T> data)
        {
            this.output = output;
            this.dataEntry = dataEntry;
            this.data = data;
        }

        public void setCustomDatapackName(String name)
        {
            customDatapackName = customDatapackName;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cachedOutput)
        {

            JsonElement json = dataEntry.codec()
                    .encodeStart(JsonOps.INSTANCE, data)
                    .getOrThrow(false, Utils::nothing);

            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(Utils.orElse(customDatapackName, dataEntry.path().getNamespace()))
                    .resolve(dataEntry.path().getNamespace())
                    .resolve(dataEntry.path().getPath() + ".json");

            return DataProvider.saveStable(cachedOutput, json, path);
        }

        @Override
        public String getName()
        {
            return toString();
        }
    }
}
