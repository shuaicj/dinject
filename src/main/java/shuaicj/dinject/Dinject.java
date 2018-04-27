package shuaicj.dinject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * The main class of dinject.
 *
 * @author shuaicj 2018/04/12
 */
public final class Dinject {

    private final Map<Id<?>, Meta> metas = null;
    private final Map<Id<?>, Object> singletons = new ConcurrentHashMap<>();

    public static Dinject of(Class<?>... moduleClasses) {
        return new Dinject(Arrays.asList(moduleClasses));
    }

    public static Dinject of(Iterable<Class<?>> moduleClasses) {
        return new Dinject(moduleClasses);
    }

    private Dinject(Iterable<Class<?>> moduleClasses) {
        // instance of Dinject itself is supposed to be a singleton
        singletons.put(new Id<>(Dinject.class), this);

        // init metas
        Map<Id<?>, Meta> metas = new HashMap<>();
        checkModules(metas, moduleClasses);
        checkDependencies(metas);
    }

    public <T> T instance(Class<T> clazz) {
        return null;
    }

    private void checkModules(Map<Id<?>, Meta> metas, Iterable<Class<?>> moduleClasses) {
        for (Class<?> clazz : moduleClasses) {
            Constructor<?> ctor;
            try {
                ctor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new DinjectException("no-arg constructor required for module " + clazz);
            }
            ctor.setAccessible(true);

            Object module;
            try {
                module = ctor.newInstance();
            } catch (Exception e) {
                throw new DinjectException("cannot instantiate " + clazz, e);
            }

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Provides.class)) {
                    Class<?> t = m.getReturnType();
                    String n = m.isAnnotationPresent(Named.class) ? m.getAnnotation(Named.class).value() : null;
                    boolean s = m.isAnnotationPresent(Singleton.class) || t.isAnnotationPresent(Singleton.class);
                    if (metas.putIfAbsent(new Id<>(t, n), new Meta(s, m, module)) != null) {
                        throw new DinjectException("multiple @Provides for " + t + (n == null ? "" : " named " + n));
                    }
                }
            }
        }
    }

    private void checkDependencies(Map<Id<?>, Meta> metas) {
        Queue<Id<?>> q = new LinkedList<>(metas.keySet());
        while (!q.isEmpty()) {
            Id<?> id = q.poll();
            Meta meta = metas.get(id);
            if (meta == null) {
                Constructor c = getConstructor(id.clazz);
                meta = new Meta(c.isAnnotationPresent(Singleton.class), c);
                metas.put(id, meta);
            }
        }
    }

    private Constructor getConstructor(Class<?> clazz) {
        Constructor ctor = null;
        Constructor none = null;
        for (Constructor c : clazz.getDeclaredConstructors()) {
            if (c.getParameterCount() == 0) {
                none = c;
            }
            if (c.isAnnotationPresent(Inject.class)) {
                if (ctor != null) {
                    throw new DinjectException("multiple @Inject for " + clazz);
                }
                ctor = c;
            }
        }

        if (ctor == null && none == null) {
            throw new DinjectException("no applicable constructor for " + clazz);
        }

        ctor = ctor == null ? none : ctor;
        ctor.setAccessible(true);
        return ctor;
    }

    private static final class Id<T> {

        private final Class<T> clazz;
        private final String name;

        Id(Class<T> clazz) {
            this(clazz, null);
        }

        Id(Class<T> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id<?> id = (Id<?>) o;
            return Objects.equals(clazz, id.clazz) && Objects.equals(name, id.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, name);
        }
    }

    private static final class Meta {

        private final boolean singleton;
        private final Executable type;
        private final Object module;

        Meta(boolean singleton, Executable type) {
            this(singleton, type, null);
        }

        Meta(boolean singleton, Executable type, Object module) {
            this.singleton = singleton;
            this.type = type;
            this.module = module;
        }
    }
}
