package cn.allay.jegenerator.utils;

import cn.allay.api.data.VanillaBiomeId;
import cn.allay.api.world.chunk.UnsafeChunk;
import cn.allay.jegenerator.mappings.MappingRegistries;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChunkUtils {
    private static final cn.allay.api.block.type.BlockState WATER = MappingRegistries.BLOCKS.getBedrockWater();
    private static final Registry<Biome> BIOME_REGISTRY = MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.BIOME);
    private static final Int2IntArrayMap BIOME_MAPPINGS = MappingRegistries.BIOME.get();

    public static void convertChunk(final ChunkAccess input, final UnsafeChunk output) {
        if (input == null) {
            System.out.println(output.getX() + ":" + output.getZ() + " convert is null!");
            return;
        }
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = input.getMinBuildHeight(); y < input.getMaxBuildHeight(); y++) {
                    Holder<Biome> noiseBiome = input.getNoiseBiome(x >> 2, y >> 2, z >> 2);
                    int id = BIOME_REGISTRY.getId(noiseBiome.value());
                    int biome = BIOME_MAPPINGS.getOrDefault(id, 1);
                    output.setBiome(x, y, z, VanillaBiomeId.fromId(biome));

                    CompoundTag blockEntityNBT = input.getBlockEntityNbtForSaving(new BlockPos(x, y, z));
                    //todo translate blockEntity NBT and block
                    if(blockEntityNBT!=null){
                        continue;
                    }

                    BlockState blockState = input.getBlockState(x, y, z);
                    boolean hasWater = blockState.getFluidState().is(FluidTags.WATER) || (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED));
                    cn.allay.api.block.type.BlockState bedrockBlock = MappingRegistries.BLOCKS.getBedrockBlock(blockState);
                    output.setBlockState(x, y, z, bedrockBlock);
                    if (hasWater) {
                        output.setBlockState(x, y, z, WATER, 1);
                    }
                }

                int height = input.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                output.setHeight(x, z, height);
            }
        }
    }
}
