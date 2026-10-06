package com.wdiscute.utils;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class NBTCodecHelper
{
    public static <T> void store(String name, Codec<T> codec, T data, CompoundTag compoundTag)
    {
        if (data == null)
            return;

        codec.encodeStart(NbtOps.INSTANCE, data)
                .resultOrPartial(LogUtils.getLogger()::error)
                .ifPresent(tag -> compoundTag.put(name, tag));
    }

    public static <T> T read(String name, Codec<T> codec, CompoundTag compoundTag, Supplier<T> orElse)
    {
        Tag tag = compoundTag.get(name);

        return codec.decode(NbtOps.INSTANCE, tag)
                .resultOrPartial(LogUtils.getLogger()::error)
                .map(Pair::getFirst).orElseGet(orElse);
    }

    public static <T> @Nullable T read(String name, Codec<T> codec, CompoundTag compoundTag)
    {
        if (!compoundTag.contains(name))
            return null;

        return read(name, codec, compoundTag, () -> null);
    }
}
