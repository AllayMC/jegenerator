package cn.allay.jegenerator.mappings.populator;

import cn.allay.api.block.interfaces.liquid.BlockWaterBehavior;
import cn.allay.api.block.palette.BlockStateHashPalette;
import cn.allay.api.block.property.type.BlockPropertyType;
import cn.allay.api.data.VanillaBlockPropertyTypes;
import cn.allay.jegenerator.mappings.MappingRegistries;
import cn.allay.jegenerator.mappings.type.BedrockBlockDefinition;
import cn.allay.jegenerator.mappings.type.BlockMappings;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.Main;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_20_10;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_20_30;
import org.cloudburstmc.blockstateupdater.BlockStateUpdater_1_20_40;
import org.cloudburstmc.blockstateupdater.util.TagUtils;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY;

/**
 * Populates the block registries.
 */
public final class BlockRegistryPopulator {
    @FunctionalInterface
    private interface Remapper {

        NbtMap remap(NbtMap tag);

        static Remapper of(BlockStateUpdater... updaters) {
            CompoundTagUpdaterContext context = new CompoundTagUpdaterContext();
            for (BlockStateUpdater updater : updaters) {
                updater.registerUpdaters(context);
            }

            return tag -> {
                NbtMapBuilder updated = context.update(tag, 0).toBuilder();
                updated.remove("version"); // we already removed this, but the context adds it. remove it again.
                return updated.build();
            };
        }
    }


    public static BlockMappings registerBedrockBlocks() {
        Remapper mapper = Remapper.of(BlockStateUpdater_1_20_10.INSTANCE, BlockStateUpdater_1_20_30.INSTANCE, BlockStateUpdater_1_20_40.INSTANCE);

        // We can keep this strong as nothing should be garbage collected
        // Safe to intern since Cloudburst NBT is immutable
        Interner<NbtMap> statesInterner = Interners.newStrongInterner();

        List<NbtMap> vanillaBlockStates;
        List<NbtMap> blockStates;
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("bedrock/block_palette.1_20_40.nbt");
             NBTInputStream nbtInputStream = new NBTInputStream(new DataInputStream(new GZIPInputStream(stream)), true, true)) {
            NbtMap blockPalette = (NbtMap) nbtInputStream.readTag();

            vanillaBlockStates = new ArrayList<>(blockPalette.getList("blocks", NbtType.COMPOUND));
            for (int i = 0; i < vanillaBlockStates.size(); i++) {
                NbtMapBuilder builder = vanillaBlockStates.get(i).toBuilder();
                builder.remove("version"); // Remove all nbt tags which are not needed for differentiating states
                builder.remove("name_hash"); // Quick workaround - was added in 1.19.20
                builder.putCompound("states", statesInterner.intern((NbtMap) builder.remove("states")));
                vanillaBlockStates.set(i, builder.build());
            }

            blockStates = new ArrayList<>(vanillaBlockStates);
        } catch (Exception e) {
            throw new AssertionError("Unable to get blocks from runtime block states", e);
        }

        Object2ObjectMap<NbtMap, BedrockBlockDefinition> blockStateOrderedMap = new Object2ObjectOpenHashMap<>(blockStates.size());
        Int2ObjectOpenHashMap<BedrockBlockDefinition> bedrockRuntimeMap = new Int2ObjectOpenHashMap<>(blockStates.size());
        for (int i = 0; i < blockStates.size(); i++) {
            NbtMap tag = blockStates.get(i);
            if (blockStateOrderedMap.containsKey(tag)) {
                throw new AssertionError("Duplicate block states in Bedrock palette: " + tag);
            }
            Map<String, Object> mutable = (Map<String, Object>) TagUtils.toMutable(tag);
            Integer networkId = (Integer) mutable.remove("network_id");
            NbtMap nbtMap = NbtMap.fromMap(mutable);
            BedrockBlockDefinition block = new BedrockBlockDefinition(networkId, nbtMap);
            blockStateOrderedMap.put(nbtMap, block);
            bedrockRuntimeMap.put(networkId.intValue(), block);
        }


        cn.allay.api.block.type.BlockState[] blocks = new cn.allay.api.block.type.BlockState[BLOCK_STATE_REGISTRY.size()];
        JsonNode blocksJson;
        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("bedrock/blocks.json")) {
            blocksJson = MappingRegistries.JSON_MAPPER.readTree(stream);
        } catch (Exception e) {
            throw new AssertionError("Unable to load Java block mappings", e);
        }

        Iterator<Map.Entry<String, JsonNode>> blocksIterator = blocksJson.fields();
        cn.allay.api.block.type.BlockState airDefinition = null;
        cn.allay.api.block.type.BlockState flowWaterDefinition = null;
        int javaRuntimeId = -1;
        while (blocksIterator.hasNext()) {
            javaRuntimeId++;
            Map.Entry<String, JsonNode> entry = blocksIterator.next();
            String javaId = entry.getKey();
            NbtMap bedrockTag = buildBedrockState(entry.getValue());
            NbtMap remapBedrockTag = mapper.remap(bedrockTag);
            BedrockBlockDefinition vanillaBedrockDefinition = blockStateOrderedMap.get(remapBedrockTag);
            if (vanillaBedrockDefinition != null) {
                blocks[javaRuntimeId] = BlockStateHashPalette.getRegistry().get(vanillaBedrockDefinition.getRuntimeId());
                switch (javaId) {
                    case "minecraft:air" -> airDefinition = BlockStateHashPalette.getRegistry().get(vanillaBedrockDefinition.getRuntimeId());
                    case "minecraft:water[level=15]" -> flowWaterDefinition = BlockStateHashPalette.getRegistry().get(vanillaBedrockDefinition.getRuntimeId());
                }
            } else {
                blocks[javaRuntimeId] = null;
            }
        }
        return BlockMappings.builder()
            .bedrockAir(airDefinition)
            .bedrockWater(flowWaterDefinition)
            .blockMappings(blocks)
            .build();
    }

    @NotNull
    private static NbtMap buildBedrockState(JsonNode node) {
        NbtMapBuilder tagBuilder = NbtMap.builder();
        String bedrockIdentifier = node.get("bedrock_identifier").textValue();
        tagBuilder.putString("name", bedrockIdentifier);

        NbtMapBuilder statesBuilder = NbtMap.builder();

        // check for states
        JsonNode states = node.get("bedrock_states");
        if (states != null) {
            Iterator<Map.Entry<String, JsonNode>> statesIterator = states.fields();

            while (statesIterator.hasNext()) {
                Map.Entry<String, JsonNode> stateEntry = statesIterator.next();
                JsonNode stateValue = stateEntry.getValue();
                switch (stateValue.getNodeType()) {
                    case BOOLEAN -> statesBuilder.putBoolean(stateEntry.getKey(), stateValue.booleanValue());
                    case STRING -> statesBuilder.putString(stateEntry.getKey(), stateValue.textValue());
                    case NUMBER -> statesBuilder.putInt(stateEntry.getKey(), stateValue.intValue());
                }
            }
        }
        tagBuilder.put("states", statesBuilder.build());
        return tagBuilder.build();
    }
}
