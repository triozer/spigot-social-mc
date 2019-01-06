package fr.triozer.smc.api.utils;

import java.util.stream.Stream;

/**
 * Abstract generic manager
 *
 * @param <K> key
 * @param <V> value
 * @author Cédric / Triozer
 */
public interface AbstractManager<K, V> {

    /**
     * Get registered generic value
     *
     * @param key key
     * @return registered value
     */
    V get(K key);

    /**
     * Add given generic value
     *
     * @param value a generic value
     */
    void add(V value);

    /**
     * Add multiple given generic values
     *
     * @param values generic values
     */
    default void addAll(V... values) {
        Stream.of(values).forEach(this::add);
    }

    /**
     * Remove given generic parameter
     *
     * @param value a generic value
     */
    void remove(V value);

    /**
     * Remove multiple given generic values
     *
     * @param values generic values
     */
    default void removeAll(V... values) {
        Stream.of(values).forEach(this::remove);
    }

    /**
     * Get all values {@link AbstractManager#add(Object)}
     *
     * @return a {@link Stream} of all values
     */
    Stream<V> values();

}