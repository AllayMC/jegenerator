package cn.allay.jegenerator.mappings;

import cn.allay.jegenerator.mappings.loader.RegistryLoaders;
import cn.allay.jegenerator.mappings.populator.BiomeRegistryLoader;
import cn.allay.jegenerator.mappings.populator.BlockRegistryPopulator;
import cn.allay.jegenerator.mappings.type.BlockMappings;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class MappingRegistries {
    public static final ObjectMapper JSON_MAPPER = new ObjectMapper()
        .enable(JsonParser.Feature.IGNORE_UNDEFINED)
        .enable(JsonParser.Feature.ALLOW_COMMENTS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
        .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

    /**
     * A mapped registry which stores Java biome identifiers and their Bedrock biome identifier.
     */
    public static final SimpleMappingRegistry<Int2IntArrayMap> BIOME = SimpleMappingRegistry.create("mappings/biomes.json", BiomeRegistryLoader::new);

    /**
     * A versioned registry which holds {@link BlockMappings} for each version. These block mappings contain
     * primarily Bedrock version-specific data.
     */
    public static final BlockMappings BLOCKS = BlockRegistryPopulator.registerBedrockBlocks();
}
