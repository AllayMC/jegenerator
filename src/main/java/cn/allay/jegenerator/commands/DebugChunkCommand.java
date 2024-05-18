package cn.allay.jegenerator.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.concurrent.atomic.AtomicInteger;

public class DebugChunkCommand {
    public DebugChunkCommand() {
    }

    static AtomicInteger i = new AtomicInteger(0);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dc").executes((commandcontext) -> {
                long l = System.currentTimeMillis();
                var level = MinecraftServer.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, LevelStem.OVERWORLD.location()));
                ChunkAccess jeChunk = level.getChunkSource().getChunkAtIfLoadedImmediately(i.get(),i.get());
                if (jeChunk == null) {
                    level.getChunkSource().getChunk(i.get(), i.get(), ChunkStatus.FULL, true);
                }
                i.getAndIncrement();
                System.out.println("time cost : " + (System.currentTimeMillis() - l) + " ms");
                return 0;
            }));
    }
}
