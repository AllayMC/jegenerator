package cn.allay.jegenerator.mappings.type;

import lombok.Getter;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;

public class BedrockBlockDefinition implements BlockDefinition {
    private final int blockHash;

    @Getter
    private final NbtMap state;

    public BedrockBlockDefinition(int blockHash, NbtMap state) {
        this.blockHash = blockHash;
        this.state = state;
    }

    /**
     *
     * @return blockHash It is not RuntimeID just for implements
     */
    @Override
    public int getRuntimeId() {
        return blockHash;
    }

    @Override
    public String toString() {
        return "BedrockBlock{" + state.getString("name") + "}";
    }
}
