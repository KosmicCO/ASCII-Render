package us.kosdt.arl.util.activator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ActivatorCache<T> { //TODO: Test this class

    private static final float LOAD_FACTOR = 0.75f;

    private final Map<Long, Integer> locationMap;
    private final Object[] cache;
    private final Consumer<T> activate;
    private final Consumer<T> deactivate;
    private final Function<T, Long> getID;
    private int size;

    public ActivatorCache(int casheSize, Consumer<T> activate, Consumer<T> deactivate, Function<T, Long> getID) {
        locationMap = new HashMap<>((int) Math.ceil(casheSize * (1.0 / LOAD_FACTOR)), LOAD_FACTOR);
        cache = new Object[casheSize];

        if (activate == null ^ deactivate == null) {
            throw new IllegalArgumentException("Both of activate and deactivate must either be defined of be null");
        }

        this.activate = activate;
        this.deactivate = deactivate;
        this.getID = getID;

        size = 0;
    }

    public ActivatorCache(int casheSize, Consumer<T> activate, Consumer<T> deactivate) {
        this(casheSize, activate, deactivate, null);
    }

    public ActivatorCache(int casheSize, Function<T, Long> getID) {
        this(casheSize, null, null, getID);
    }

    public ActivatorCache(int cacheSize) {
        this(cacheSize, null, null, null);
    }

    public void clear() {
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] != null) {
                deactivate((T) cache[i]);
                cache[i] = null;
            }
        }
        size = 0;
    }

    public void put(T t) {
        long id = getID(t);
        activate(t);
        if (!locationMap.containsKey(id)) {
            if (size < cache.length) {
                locationMap.put(id, size);
                size++;
            } else {
                int rep = getRandomIndex();
                T deact = (T) cache[rep];
                deactivate(deact);
                locationMap.remove(getID(deact));
                cache[rep] = t;
                locationMap.put(id, rep);
            }
        }
    }

    private int getRandomIndex() {
        return (int) (Math.random() * size);
    }

    private void activate(T t) {
        if (activate == null) {
            ((Activatable) t).activate();
        } else {
            activate.accept(t);
        }
    }

    private void deactivate(T t) {
        if (deactivate == null) {
            ((Activatable) t).deactivate();
        } else {
            activate.accept(t);
        }
    }

    private long getID(T t) {
        if (getID == null) {
            return ((Identifiable) t).getID();
        }
        return getID.apply(t);
    }
}
