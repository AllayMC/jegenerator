package cn.allay.jegenerator;

import cn.allay.api.world.generator.WorldGenerator;
import net.minecraft.obfuscate.DontObfuscate;

@DontObfuscate
public final class JeGeneratorMain {
    public static void setup() {
        try {
            Class.forName("cn.allay.jegenerator.mappings.MappingRegistries");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static WorldGenerator overworld() {
        return new JeGenerator(0);
    }

    public static WorldGenerator nether() {
        return new JeGenerator(-1);
    }

    public static WorldGenerator end() {
        return new JeGenerator(1);
    }
}
