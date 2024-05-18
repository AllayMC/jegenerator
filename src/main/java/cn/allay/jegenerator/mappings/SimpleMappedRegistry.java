package cn.allay.jegenerator.mappings;


import cn.allay.api.registry.RegistryLoader;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A variant of {@link AbstractMappedRegistry} with {@link Map} as the defined type. Unlike
 * {@link MappedMappingRegistry}, this registry does not support specifying your own Map class,
 * and only permits operations the {@link Map} interface does, unless you manually cast.
 *
 * @param <K> the key
 * @param <V> the value
 */
public class SimpleMappedRegistry<K, V> extends AbstractMappedRegistry<K, V, Map<K, V>> {
    protected <I> SimpleMappedRegistry(I input, RegistryLoader<I, Map<K, V>> registryLoader) {
        super(input, registryLoader);
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader}. The
     * input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader
     */
    public static <I, K, V> SimpleMappedRegistry<K, V> create(RegistryLoader<I, Map<K, V>> registryLoader) {
        return new SimpleMappedRegistry<>(null, registryLoader);
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader} and input.
     *
     * @param input the input
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader
     */
    public static <I, K, V> SimpleMappedRegistry<K, V> create(I input, RegistryLoader<I, Map<K, V>> registryLoader) {
        return new SimpleMappedRegistry<>(input, registryLoader);
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader} supplier.
     * The input type is not specified here, meaning the loader return type is either
     * predefined, or the registry is populated at a later point.
     *
     * @param registryLoader the registry loader supplier
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, K, V> SimpleMappedRegistry<K, V> create(Supplier<RegistryLoader<I, Map<K, V>>> registryLoader) {
        return new SimpleMappedRegistry<>(null, registryLoader.get());
    }

    /**
     * Creates a new mapped registry with the given {@link RegistryLoader} and input.
     *
     * @param registryLoader the registry loader
     * @param <I> the input
     * @param <K> the map key
     * @param <V> the map value
     * @return a new registry with the given RegistryLoader supplier
     */
    public static <I, K, V> SimpleMappedRegistry<K, V> create(I input, Supplier<RegistryLoader<I, Map<K, V>>> registryLoader) {
        return new SimpleMappedRegistry<>(input, registryLoader.get());
    }
}
