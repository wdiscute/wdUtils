package com.wdiscute.utils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Set;
import java.util.function.Consumer;

public record TeleportTransition(ServerLevel sl, Vec3 pos, Vec3 movements, float pitch, float yaw, Consumer<Player> onTeleport)
{
    public void teleport(ServerPlayer player)
    {
        player.teleportTo(sl, pos.x, pos.y, pos.z, Set.of(), pitch, yaw);
        onTeleport.accept(player);
    }
}
