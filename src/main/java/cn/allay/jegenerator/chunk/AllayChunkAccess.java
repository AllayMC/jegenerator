package cn.allay.jegenerator.chunk;

import cn.allay.api.world.chunk.UnsafeChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.TickContainerAccess;
import org.jetbrains.annotations.Nullable;

public class AllayChunkAccess extends ChunkAccess {
    public AllayChunkAccess(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable LevelChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return null;
    }

    @Nullable
    @Override
    public BlockState getBlockStateIfLoaded(BlockPos blockposition) {
        return null;
    }

    @Nullable
    @Override
    public FluidState getFluidIfLoaded(BlockPos blockposition) {
        return null;
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return null;
    }

    @Nullable
    @Override
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        return null;
    }

    @Override
    public void setBlockEntity(BlockEntity blockEntity) {

    }

    @Override
    public void addEntity(Entity entity) {

    }

    @Override
    public ChunkStatus getStatus() {
        return null;
    }

    @Override
    public void removeBlockEntity(BlockPos pos) {

    }

    @Nullable
    @Override
    public CompoundTag getBlockEntityNbtForSaving(BlockPos pos) {
        return null;
    }

    @Override
    public TickContainerAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public TickContainerAccess<Fluid> getFluidTicks() {
        return null;
    }

    @Override
    public TicksToSave getTicksForSerialization() {
        return null;
    }
}
