package io.oreto.toil;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;


/**
 * Provide thread-safe lazy initialized static instances of objects
 * @param <T> The type of object to provide
 */
public class Curator<T> {
    // Using AtomicReference to ensure thread-safety and visibility across parallel gets/sets
    // https://stackoverflow.com/questions/14676997/java-memory-visibility-and-atomicreferences
    // https://stackoverflow.com/questions/3964211/when-to-use-atomicreference-in-java
    private final AtomicReference<T> value = new AtomicReference<>();
    private final Supplier<T> defaultSupplier;

    private T defaultSupplied;

    // ------------- Constructors -----------

    public static <T> Curator<T> noDefault() {
        return lazyDefault(() -> {throw new NoSuchElementException("No default provided, nor was any value set");});
    }

    /**
     * Set a default for this type
     * @param def The default object to provide for this type
     * @return The curator
     * @param <T> Type of instance
     */
    public static <T> Curator<T> withDefault(T def) {
        return new Curator<>(() -> def);
    }

    /**
     * Set a default supplier for this type
     * @param defaultSupplier The default supplier to provide for this type
     * @return The curator
     * @param <T> Type of supplier
     */
    public static <T> Curator<T> lazyDefault(Supplier<T> defaultSupplier) {
        return new Curator<>(defaultSupplier);
    }

    private Curator(Supplier<T> defaultSupplier) {
        if (defaultSupplier == null)
            throw new NullPointerException("Cannot have a null supplier");
        this.defaultSupplier = defaultSupplier;
    }

    // --------------------------------------
    /**
     * @return the most recently set value, or if never set, the default.
     */
    public T get() {
        T value = this.value.get();
        return value == null ? setDefault() : value;
    }

    /**
     * If value has never been set, the default is not invoked
     * @return true if the value has been permanently set to any user-supplied value, or the default
     */
    public boolean isSet() {
        return value.get() != null;
    }

    /**
     * Set the value for this type
     * If set is called with data that is incompatible with the previous get/set calls,
     * an exception will be thrown, and no data will be changed.
     * @param newVal The value
     * @return The new value
     */
    public T set(T newVal) {
        if (newVal == null)
            throw new NullPointerException("Cannot set value to null");
        boolean updated = value.compareAndSet(null, newVal);
        if (updated || value.get().equals(newVal))
            return newVal;
        else
            throw new IllegalArgumentException(String.format("New value: %s, does not match existing value: %s"
                    , newVal, value.get()));
    }

    /**
     * Lazy initializer for the default
     * @return The default value
     */
    private synchronized T setDefault() {
        if (defaultSupplied == null) {
            defaultSupplied = defaultSupplier.get();
            if (defaultSupplied == null)
                throw new NullPointerException("Default value should not be null");
            return set(defaultSupplied);
        } else {
            return defaultSupplied;
        }
    }
}
