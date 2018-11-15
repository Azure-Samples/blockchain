package net.corda.workbench.commons.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple registry for basic DI
 */
public class Registry {
    private Map<Class, Object> registry = new HashMap<>();


    public Registry() {
    }

    public Registry(Object object) {
        registry.put(object.getClass(), object);
    }

    public Registry(Object... objects) {
        for (Object o : objects) {
            registry.put(o.getClass(), o);
        }
    }


    /**
     * @deprecated Avoid use of global instance - its too fragile.
     */
    @Deprecated
    public static Registry INSTANCE = new Registry();

    public Registry store(Object object) {
        registry.put(object.getClass(), object);
        return this;
    }

    public <T> T retrieve(Class<T> clazz) {

        if (registry.containsKey(clazz)) {
            return (T) registry.get(clazz);
        }

        for (Entry<Class, Object> entry : registry.entrySet()) {
            for (Class _interface : entry.getValue().getClass().getInterfaces()) {
                if (_interface.equals(clazz)) {
                    return (T) entry.getValue();
                }
            }
        }

        throw new RuntimeException("Class " + clazz + " in not in the registry");

    }

    public <T> T retrieveOrElse(Class<T> clazz, T other) {
        try {
            return retrieve(clazz);
        } catch (RuntimeException ex) {
            return other;
        }
    }

    public void flush() {
        registry = new HashMap<>();
    }

    /**
     * Make a copy of the original registry with the stored
     * object overridden
     *
     * @param object
     * @return
     */
    public Registry overide(Object object) {
        Registry overridden = new Registry();
        overridden.registry = new HashMap<>(this.registry);

        return overridden.store(object);
    }

    /**
     * Make a copy of the original registry with the stored
     * objects overridden.
     *
     * @param objects
     * @return
     */
    public Registry overide(Object... objects) {
        Registry overridden = new Registry();
        overridden.registry = new HashMap<>(this.registry);

        for (Object o : objects) {
            overridden.store(o);
        }
        return overridden;
    }
}
