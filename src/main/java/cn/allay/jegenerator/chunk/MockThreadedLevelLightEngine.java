package cn.allay.jegenerator.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class MockThreadedLevelLightEngine extends ThreadedLevelLightEngine {
    public MockThreadedLevelLightEngine(LightChunkGetter chunkProvider, ChunkMap chunkStorage, boolean hasBlockLight, ProcessorMailbox<Runnable> processor, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> executor) {
        super(chunkProvider, chunkStorage, hasBlockLight, processor, executor);
    }

    @Override
    public int relight(Set<ChunkPos> chunks_param, Consumer<ChunkPos> chunkLightCallback, IntConsumer onComplete) {
        return 0;
    }

    @Override
    public boolean hasLightWork() {
        return false;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int ambientDarkness) {
        return 15;
    }

    @Override
    public void checkBlock(BlockPos pos) {
    }

    @Override
    protected void updateChunkStatus(ChunkPos pos) {
    }

    @Override
    public void propagateLightSources(ChunkPos chunkPos) {

    }

    @Override
    public void setLightEnabled(ChunkPos pos, boolean retainData) {
    }

    @Override
    public void queueSectionData(LightLayer lightType, SectionPos pos, @Nullable DataLayer nibbles) {
    }

    @Override
    public void retainData(ChunkPos pos, boolean retainData) {

    }
}
