package cn.allay.jegenerator.mappings.type;


import lombok.Builder;
import lombok.Value;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Builder
@Value
public class BlockMappings{
    cn.allay.api.block.type.BlockState bedrockAir;
    cn.allay.api.block.type.BlockState bedrockWater;
    cn.allay.api.block.type.BlockState[] blockMappings;

    public cn.allay.api.block.type.BlockState getBedrockBlock(int javaRuntimeId) {
        if (javaRuntimeId < 0 || javaRuntimeId >= this.blockMappings.length) {
            return bedrockAir;
        }
        cn.allay.api.block.type.BlockState blockMapping = this.blockMappings[javaRuntimeId];
        return blockMapping == null ? bedrockAir : blockMapping;
    }

    public cn.allay.api.block.type.BlockState getBedrockBlock(BlockState jeBlockState) {
        return getBedrockBlock(Block.BLOCK_STATE_REGISTRY.getId(jeBlockState));
    }
}
