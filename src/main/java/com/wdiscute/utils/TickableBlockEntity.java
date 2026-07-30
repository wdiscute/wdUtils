package com.wdiscute.utils;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public interface TickableBlockEntity
{
    default void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        tick(level, pos, state);
    }

    default void tickClient(Level level, BlockPos pos, BlockState state)
    {
        tick(level, pos, state);
    }

    default void tick(Level level, BlockPos pos, BlockState state)
    {

    }

    static <T extends BlockEntity> BlockEntityTicker<T> getTicketHelper(Level level)
    {
        if (level.isClientSide())
        {
            return (l, bp, bs, be) ->
            {
                if (be instanceof TickableBlockEntity tbe) tbe.tickClient(level, bp, bs);
            };
        }
        else
        {
            return (l, bp, bs, be) ->
            {
                if (be instanceof TickableBlockEntity tbe) tbe.tickServer((ServerLevel) level, bp, bs);
            };
        }
    }
}
