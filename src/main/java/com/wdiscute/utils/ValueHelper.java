package com.wdiscute.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public interface ValueHelper
{
    static <T> void store(String name, Codec<T> codec, T data, CompoundTag compoundTag)
    {
        if (data == null)
            return;

        codec.encodeStart(NbtOps.INSTANCE, data)
                .resultOrPartial(LogUtils.getLogger()::error)
                .ifPresent(tag -> compoundTag.put(name, tag));
    }

    static <T> Optional<T> read(String name, Codec<T> codec, CompoundTag compoundTag)
    {
        Tag tag = compoundTag.get(name);

        return codec.decode(NbtOps.INSTANCE, tag)
                .resultOrPartial(LogUtils.getLogger()::error)
                .map(Pair::getFirst);
    }
}
