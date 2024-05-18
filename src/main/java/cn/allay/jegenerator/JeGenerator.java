package cn.allay.jegenerator;

import cn.allay.api.world.GeneratorType;
import cn.allay.api.world.chunk.UnsafeChunk;
import cn.allay.api.world.generator.ChunkGenerateContext;
import cn.allay.api.world.generator.WorldGenerator;
import cn.allay.jegenerator.utils.ChunkUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class JeGenerator implements WorldGenerator {
    final ServerLevel jeLevel;
    final ResourceKey<LevelStem> dim;

    public JeGenerator(int dimId) {
        switch (dimId) {
            case -1 -> dim = LevelStem.NETHER;
            case 1 -> dim = LevelStem.END;
            default -> dim = LevelStem.OVERWORLD;
        }
        jeLevel = MinecraftServer.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dim.location()));
    }

    public JeGenerator(ResourceKey<LevelStem> dim) {
        this.dim = dim;
        jeLevel = MinecraftServer.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dim.location()));
    }

    @Override
    public void generate(ChunkGenerateContext chunkGenerateContext) {
        final UnsafeChunk chunk = chunkGenerateContext.chunk();
        ChunkAccess jeChunk = jeLevel.getChunkSource().getChunkAtIfLoadedImmediately(chunk.getX(), chunk.getZ());
        if (jeChunk == null) {
            jeChunk = CompletableFuture.supplyAsync(() -> jeLevel.getChunkSource().getChunk(chunk.getX(), chunk.getZ(), ChunkStatus.FULL, true), jeLevel.getChunkSource().mainThreadProcessor).join();
        }
        ChunkUtils.convertChunk(jeChunk, chunk);
    }

    @Override
    public String getGeneratorName() {
        return "JEGenerator_" + dim.location().getPath().toUpperCase(Locale.ENGLISH);
    }

    @Override
    public GeneratorType getType() {
        return switch (dim.location().getPath()) {
            case "the_nether" -> GeneratorType.NETHER;
            case "the_end" -> GeneratorType.THE_END;
            default -> GeneratorType.INFINITE;
        };
    }
}
