package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public sealed interface EntryOrTag<T>
{
    default String getTranslation()
    {
        if (this instanceof Entry<T>(ResourceKey<T> key))
            return "biome." + key.identifier().toLanguageKey();
        if (this instanceof Tag<T>(TagKey<T> tag))
            return "tag." + tag.location().toLanguageKey();
        return "uuuuhhh something went wrong! Oops...";
    }

    boolean matches(Holder<T> biome, Registry<T> registry);

    record Entry<T>(ResourceKey<T> key) implements EntryOrTag<T>
    {
        @Override
        public boolean matches(Holder<T> biome, Registry<T> registry)
        {
            ResourceKey<T> rk = biome.getKey();

            if(rk == null) return false;

            return rk.equals(key);
        }
    }

    record Tag<T>(TagKey<T> tag) implements EntryOrTag<T>
    {
        @Override
        public boolean matches(Holder<T> biome, Registry<T> registry)
        {
            Optional<HolderSet.Named<T>> set = registry.get(tag);

            return set.map(holders -> holders.contains(biome)).orElse(false);
        }
    }

    static <T> Codec<EntryOrTag<T>> codec(ResourceKey<? extends Registry<T>> registryKey)
    {
        return Codec.STRING.comapFlatMap(
                s ->
                {
                    if (s.startsWith("#"))
                    {
                        Identifier loc = Identifier.tryParse(s.substring(1));
                        if (loc == null)
                        {
                            return DataResult.error(() -> "Invalid tag id: " + s);
                        }
                        return DataResult.success(new Tag<>(TagKey.create(registryKey, loc)));
                    }
                    else
                    {
                        Identifier loc = Identifier.tryParse(s);
                        if (loc == null)
                        {
                            return DataResult.error(() -> "Invalid resource id: " + s);
                        }
                        return DataResult.success(new Entry<>(ResourceKey.create(registryKey, loc)));
                    }
                },
                entryOrTag -> switch (entryOrTag)
                {
                    case Entry<T> e -> e.key().identifier().toString();
                    case Tag<T> t -> "#" + t.tag().location();
                }
        );
    }
}
