package com.wdiscute.utils;

import com.google.errorprone.annotations.DoNotCall;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Optional;

public record MaybeStack(@DoNotCall @Deprecated ResourceLocation rl, @DoNotCall @Deprecated ItemStack stack)
{
    public static final MaybeStack EMPTY = new MaybeStack(ItemStack.EMPTY);

    public MaybeStack(ResourceLocation rl)
    {
        this(rl, ItemStack.EMPTY);
    }

    //if rl is registered, sets itemstack as the default instance
    public static MaybeStack of(ResourceLocation rl)
    {
        return BuiltInRegistries.ITEM.getOptional(rl).map(item -> new MaybeStack(item.getDefaultInstance())).orElseGet(() -> new MaybeStack(rl));
    }

    public MaybeStack(String ns, String path)
    {
        this(ResourceLocation.fromNamespaceAndPath(ns, path), ItemStack.EMPTY);
    }

    public MaybeStack(DeferredItem<Item> item)
    {
        this(BuiltInRegistries.ITEM.getKey(item.get()), item.toStack());
    }

    public MaybeStack(DeferredBlock<Block> block)
    {
        this(BuiltInRegistries.ITEM.getKey(block.asItem()), block.toStack());
    }

    public MaybeStack(ItemStack stack)
    {
        this(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack);
    }

    public MaybeStack(Item item)
    {
        this(BuiltInRegistries.ITEM.getKey(item), item.getDefaultInstance());
    }

    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    public ItemStack toStack()
    {
        if (!isEmpty()) return stack.copy();
        return BuiltInRegistries.ITEM.getOptional(rl)
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    public Item toItem()
    {
        return toStack().getItem();
    }

    public static final Codec<MaybeStack> CODEC = Codec.either(
            ItemStack.CODEC,
            ResourceLocation.CODEC
    ).xmap(
            either -> either.map(MaybeStack::new, MaybeStack::of),
            maybeStack -> !maybeStack.isEmpty()
                    ? Either.left(maybeStack.toStack())
                    : Either.right(maybeStack.rl())
    );


    public static final StreamCodec<RegistryFriendlyByteBuf, MaybeStack> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, MaybeStack::rl,
                    ItemStack.OPTIONAL_STREAM_CODEC, MaybeStack::stack,
                    MaybeStack::new
            );
}
