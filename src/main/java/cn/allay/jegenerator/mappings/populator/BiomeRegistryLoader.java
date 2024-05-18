/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package cn.allay.jegenerator.mappings.populator;

import cn.allay.api.registry.RegistryLoader;
import cn.allay.jegenerator.mappings.MappingRegistries;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.util.concurrent.MoreExecutors;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Main;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.world.flag.FeatureFlags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class BiomeRegistryLoader implements RegistryLoader<String, Int2IntArrayMap> {

    @Override
    public Int2IntArrayMap load(String input) {
        // Populate available packs
        PackRepository resourceRepository = ServerPacksSource.createVanillaTrustedRepository();
        resourceRepository.reload();
        // Set up resource manager
        MultiPackResourceManager resourceManager = new MultiPackResourceManager(PackType.SERVER_DATA, resourceRepository.getAvailablePacks().stream().map(Pack::open).toList());
        LayeredRegistryAccess<RegistryLayer> layers = RegistryLayer.createRegistryAccess();
        layers = WorldLoader.loadAndReplaceLayer(resourceManager, layers, RegistryLayer.WORLDGEN, RegistryDataLoader.WORLDGEN_REGISTRIES);
        var REGISTRY_CUSTOM = layers.compositeAccess().freeze();
        // Register vanilla pack
        var DATA_PACK = ReloadableServerResources.loadResources(resourceManager, REGISTRY_CUSTOM, FeatureFlags.REGISTRY.allFlags(), Commands.CommandSelection.DEDICATED, 0, MoreExecutors.directExecutor(), MoreExecutors.directExecutor()).join();
        // Bind tags
        DATA_PACK.updateRegistryTags(REGISTRY_CUSTOM);
        // Biome shortcut
        var biomeRegistry = REGISTRY_CUSTOM.registryOrThrow(Registries.BIOME);

        // As of Bedrock Edition 1.17.10 with the experimental toggle, any unmapped biome identifier sent to the client
        // crashes the client. Therefore, we need to have a list of all valid Bedrock biome IDs with which we can use from.
        // The server sends the corresponding Java network IDs, so we don't need to worry about that now.

        // Reference variable for Jackson to read off of
        TypeReference<Map<String, BiomeEntry>> biomeEntriesType = new TypeReference<>() {
        };
        Map<String, BiomeEntry> biomeEntries;

        try (InputStream stream = Main.class.getClassLoader().getResourceAsStream("bedrock/biomes.json")) {
            biomeEntries = MappingRegistries.JSON_MAPPER.readValue(stream, biomeEntriesType);
        } catch (IOException e) {
            throw new AssertionError("Unable to load Bedrock runtime biomes", e);
        }

        Int2IntArrayMap biomes = new Int2IntArrayMap(biomeRegistry.size());
        for (Map.Entry<String, BiomeEntry> biome : biomeEntries.entrySet()) {
            // Java Edition integer ID -> Bedrock integer ID
            int id = biomeRegistry.getId(biomeRegistry.get(new ResourceLocation(biome.getKey())));
            biomes.put(id, biome.getValue().bedrockId);
        }
        return biomes;
    }

    private static class BiomeEntry {
        /**
         * The Bedrock network ID for this biome.
         */
        @JsonProperty("bedrock_id")
        private int bedrockId;
    }
}
