package cn.allay.jegenerator.mappings.type;

import cn.allay.jegenerator.utils.BlockUtils;
import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;

@Builder
@Value
public class BlockMapping {

    public static BlockMapping DEFAULT = BlockMapping.builder().javaIdentifier("minecraft:air").build();

    String javaIdentifier;

    int javaBlockId;

    float hardness;
    boolean canBreakWithHand;

    /**
     * The index of this collision in collision.json
     */
    int collisionIndex;
    @Nullable
    String pickItem;

    /**
     * @return the identifier without the additional block states
     */
    public String getCleanJavaIdentifier() {
        return BlockUtils.getCleanIdentifier(javaIdentifier);
    }

    /**
     * @return the corresponding Java identifier for this item
     */
    public String getItemIdentifier() {
        if (pickItem != null && !pickItem.equals("minecraft:air")) {
            // Spawners can have air as their pick item which we are not interested in.
            return pickItem;
        }

        return getCleanJavaIdentifier();
    }

    /**
     * Get the item a Java client would receive when pressing
     * the Pick Block key on a specific Java block state.
     *
     * @return The Java identifier of the item
     */
    public String getPickItem() {
        if (pickItem != null) {
            return pickItem;
        }

        return getCleanJavaIdentifier();
    }
}
