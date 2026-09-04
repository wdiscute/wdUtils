package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public sealed interface EntryOrTag<T>
{
    default String getTranslation(String prefix)
    {
        if (this instanceof Entry)
        {
            Entry<T> entry = (Entry<T>) this;
            return prefix + "." + entry.key().location().toLanguageKey();
        }

        if (this instanceof Tag)
        {
            Tag<T> tag = (Tag<T>) this;
            return "tag." + tag.tag().location().toLanguageKey();
        }

        return "uuuuhhh something went wrong! Oops...";
    }

    boolean matches(Holder<T> entry, Registry<T> registry);

    record Entry<T>(ResourceKey<T> key) implements EntryOrTag<T>
    {
        @Override
        public boolean matches(Holder<T> entry, Registry<T> registry)
        {
            Optional<ResourceKey<T>> rk = entry.unwrapKey();

            if (!rk.isPresent()) return false;

            return rk.equals(key);
        }
    }

    record Tag<T>(TagKey<T> tag) implements EntryOrTag<T>
    {
        @Override
        public boolean matches(Holder<T> entry, Registry<T> registry)
        {
            Optional<HolderSet.Named<T>> set = registry.getTag(tag);

            return set.map(holders -> holders.contains(entry)).orElse(false);
        }
    }

    static <T> Codec<EntryOrTag<T>> codec(ResourceKey<? extends Registry<T>> registryKey)
    {
        return Codec.STRING.comapFlatMap(
                s ->
                {
                    if (s.startsWith("#"))
                    {
                        ResourceLocation loc = ResourceLocation.tryParse(s.substring(1));
                        if (loc == null)
                        {
                            return DataResult.error(() -> "Invalid tag id: " + s);
                        }
                        return DataResult.success(new Tag<>(TagKey.create(registryKey, loc)));
                    }
                    else
                    {
                        ResourceLocation loc = ResourceLocation.tryParse(s);
                        if (loc == null)
                        {
                            return DataResult.error(() -> "Invalid resource id: " + s);
                        }
                        return DataResult.success(new Entry<>(ResourceKey.create(registryKey, loc)));
                    }
                },
                entryOrTag ->
                {
                    if (entryOrTag instanceof Entry<T> e)
                        return e.key().location().toString();

                    if (entryOrTag instanceof Tag<T> t)
                        return "#" + t.tag().location();

                    throw new IllegalStateException("Unknown EntryOrTag type: " + entryOrTag);
                }
        );
    }
}
