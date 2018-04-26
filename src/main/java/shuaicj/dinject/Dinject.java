package shuaicj.dinject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main class of dinject.
 *
 * @author shuaicj 2018/04/12
 */
public final class Dinject {

    private final Map<Index<?>, Object> singletons = new ConcurrentHashMap<>();

    public static Dinject of(Class<?>... modules) {
        return new Dinject(Arrays.asList(modules));
    }

    public static Dinject of(Iterable<Class<?>> modules) {
        return new Dinject(modules);
    }

    private Dinject(Iterable<Class<?>> modules) {
        for (Class<?> clazz : modules) {
            Constructor<?> ctor = null;
            try {
                ctor = clazz.getDeclaredConstructor();
            } catch (Exception e) {
                throw new DinjectException("no-arg constructor required for module " + clazz, e);
            }

            Object obj = null;
            try {
                obj = ctor.newInstance();
            } catch (Exception e) {
                throw new DinjectException("cannot instantiate " + clazz, e);
            }
            singletons.put(new Index(clazz), obj);
        }
    }

    public <T> T instance(Class<T> clazz) {
        return null;
    }

    private static final class Index<T> {

        private final Class<T> clazz;
        private final String name;

        Index(Class<T> clazz) {
            this(clazz, null);
        }

        Index(Class<T> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Index<?> index = (Index<?>) o;
            return Objects.equals(clazz, index.clazz) && Objects.equals(name, index.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, name);
        }
    }
}
