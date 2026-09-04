package com.wdiscute.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

public record MaybeStack(ResourceLocation identifier, int count, CompoundTag patch)
{
    public static final MaybeStack EMPTY = new MaybeStack(Items.AIR);

    public MaybeStack(ResourceLocation rl)
    {
        this(rl, 1, new CompoundTag());
    }

    public MaybeStack(ResourceLocation rl, int count)
    {
        this(rl, count, new CompoundTag());
    }

    public MaybeStack(Item item, int count)
    {
        this(BuiltInRegistries.ITEM.getKey(item), count, new CompoundTag());
    }

    //if identifier is registered, sets itemstack as the default instance
    public static MaybeStack of(ResourceLocation rl)
    {
        return BuiltInRegistries.ITEM.getOptional(rl).map(item -> new MaybeStack(item.getDefaultInstance())).orElseGet(() -> new MaybeStack(rl));
    }

    public MaybeStack(String ns, String path)
    {
        this(ResourceLocation.fromNamespaceAndPath(ns, path));
    }

//    public MaybeStack(DeferredItem<Item> item)
//    {
//        this(BuiltInRegistries.ITEM.getKey(item.get()));
//    }
//
//    public MaybeStack(DeferredBlock<Block> block)
//    {
//        this(BuiltInRegistries.ITEM.getKey(block.asItem()));
//    }

    public MaybeStack(ItemStack stack)
    {
        this(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getCount(), stack.serializeNBT());
    }

    public MaybeStack(Item item)
    {
        this(BuiltInRegistries.ITEM.getKey(item));
    }

    public boolean isEmpty()
    {
        return toStack().isEmpty();
    }

    public ItemStack toStack()
    {
        ItemStack stack = BuiltInRegistries.ITEM.getOptional(identifier)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);

        stack.setCount(count);
        stack.deserializeNBT(patch);
        return stack;
    }

    public Item toItem()
    {
        return BuiltInRegistries.ITEM.getOptional(identifier).orElse(Items.AIR);
    }

    public static final Codec<MaybeStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("identifier").forGetter(MaybeStack::identifier),
            Codec.INT.optionalFieldOf("count", 1).forGetter(MaybeStack::count),
            CompoundTag.CODEC.optionalFieldOf("data_components_patch", new CompoundTag()).forGetter(MaybeStack::patch)
    ).apply(instance, MaybeStack::new));

//    public static final StreamCodec<RegistryFriendlyByteBuf, MaybeStack> STREAM_CODEC =
//            StreamCodec.composite(
//                    ResourceLocation.STREAM_CODEC, MaybeStack::identifier,
//                    ByteBufCodecs.INT, MaybeStack::count,
//                    DataComponentPatch.STREAM_CODEC, MaybeStack::patch,
//                    MaybeStack::new
//            );
}
