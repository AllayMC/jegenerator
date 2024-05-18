package cn.allay.jegenerator.mappings.loader;

import cn.allay.api.registry.RegistryLoader;
import net.minecraft.server.Main;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;

public class NbtRegistryLoader implements RegistryLoader<String, NbtMap> {

    @Override
    public NbtMap load(String input) {
        try (NBTInputStream nbtInputStream = NbtUtils.createNetworkReader(Main.class.getClassLoader().getResourceAsStream(input), true, true)) {
            return (NbtMap) nbtInputStream.readTag();
        } catch (Exception e) {
            throw new AssertionError("Failed to load registrations for " + input, e);
        }
    }
}
