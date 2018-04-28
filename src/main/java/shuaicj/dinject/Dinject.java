package shuaicj.dinject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * The main class create dinject.
 *
 * @author shuaicj 2018/04/12
 */
public final class Dinject {

    private final Map<Id<?>, Meta> metas = new ConcurrentHashMap<>();
    private final Map<Id<?>, Object> singletons = new ConcurrentHashMap<>();

    public static Dinject create(Class<?>... moduleClasses) {
        return new Dinject(new HashSet<>(Arrays.asList(moduleClasses)));
    }

    public static Dinject create(Collection<Class<?>> moduleClasses) {
        return new Dinject(new HashSet<>(moduleClasses));
    }

    private Dinject(Collection<Class<?>> moduleClasses) {
        // instance create Dinject itself is supposed to be a singleton
        singletons.put(new Id<>(Dinject.class), this);

        parseModules(moduleClasses);
        generateDependencies();
        metas.forEach((id, meta) -> validateDependencies(id, new LinkedHashSet<>()));
    }

    public <T> T instance(Class<T> clazz) {
        return instance(clazz, null);
    }

    public <T> T instance(Class<T> clazz, String name) {
        Id<T> id = new Id<>(clazz, name);
        Object obj = singletons.get(id);
        if (obj != null) {
            return (T) obj;
        }
        Meta meta = metas.get(id);
        if (meta != null) {
            if (meta.singleton) {
                synchronized (this) {
                    obj = singletons.get(id);
                    if (obj == null) {
                    }
                }
            }
        }
        return null;
    }

    private Object instance(Class<?> clazz, Meta meta) {
        Object[] params = meta.params
                .stream()
                .map(p -> {
                    if (p.provider) {
                        return (Provider) () -> instance(p.id.clazz, metas.get(p.id));
                    } else {
                        return instance(p.id.clazz, metas.get(p.id));
                    }
                })
                .collect(Collectors.toList())
                .toArray();

        if (meta.executable instanceof Constructor) {
            return instantiateByConstructor(clazz, (Constructor) meta.executable, params);
        }
        return null;
    }

    private void parseModules(Iterable<Class<?>> moduleClasses) {
        for (Class<?> clazz : moduleClasses) {
            Constructor<?> ctor;
            try {
                ctor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new DinjectException("no-arg constructor required for module " + clazz);
            }
            ctor.setAccessible(true);

            Object module = instantiateByConstructor(clazz, ctor);

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Provides.class)) {
                    Class<?> t = m.getReturnType();
                    String n = m.isAnnotationPresent(Named.class) ? m.getAnnotation(Named.class).value() : null;
                    boolean s = m.isAnnotationPresent(Singleton.class) || t.isAnnotationPresent(Singleton.class);
                    Id<?> id = new Id<>(t, n);
                    if (metas.putIfAbsent(id, new Meta(s, m, getParams(m), module)) != null) {
                        throw new DinjectException("multiple @Provides for " + id);
                    }
                }
            }
        }
    }

    private void generateDependencies() {
        // ensure all dependencies constructable
        Queue<Id<?>> q = new LinkedList<>(metas.keySet());
        while (!q.isEmpty()) {
            Id<?> id = q.poll();
            Meta meta = metas.get(id);
            for (Param<?> p : meta.params) {
                if (!metas.containsKey(p.id)) {
                    if (p.id.name != null) {
                        throw new DinjectException("no @Provides for " + p);
                    }
                    boolean s = p.id.clazz.isAnnotationPresent(Singleton.class);
                    Constructor c = getConstructor(p.id.clazz);
                    metas.put(p.id, new Meta(s, c, getParams(c)));
                    q.offer(p.id);
                }
            }
        }
    }

    private void validateDependencies(Id<?> id, LinkedHashSet<Id<?>> path) {
        // ensure no circular dependency
        if (path.contains(id)) {
            throw new DinjectException("circular dependency " + pathToString(id, path));
        }
        path.add(id);
        for (Param<?> p : metas.get(id).params) {
            validateDependencies(p.id, path);
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

    private Object instantiateByConstructor(Class<?> clazz, Constructor ctor, Object... params) {
        try {
            return ctor.newInstance(params);
        } catch (Exception e) {
            throw new DinjectException("cannot instantiate " + clazz + " by constructor", e);
        }
    }

    private List<Param<?>> getParams(Executable executable) {
        List<Param<?>> rt = new ArrayList<>();
        Class<?>[] pt = executable.getParameterTypes();
        Type[] gt = executable.getGenericParameterTypes();
        Annotation[][] pa = executable.getParameterAnnotations();
        for (int i = 0; i < pt.length; i++) {
            boolean p = pt[i].equals(Provider.class);
            Class<?> t = p ? (Class<?>) ((ParameterizedType) gt[i]).getActualTypeArguments()[0] : pt[i];
            String n = null;
            for (Annotation a : pa[i]) {
                if (a instanceof Named) {
                    n = ((Named) a).value();
                    break;
                }
            }
            rt.add(new Param<>(new Id<>(t, n), p));
        }
        return rt;
    }

    private String pathToString(Id<?> id, LinkedHashSet<Id<?>> path) {
        StringBuilder s = new StringBuilder("{ ");
        boolean found = false;
        for (Id<?> d : path) {
            if (found) {
                s.append(" -> ");
                s.append(d);
                continue;
            }
            if (d.equals(id)) {
                found = true;
                s.append(d);
            }
        }
        s.append(" -> ");
        s.append(id);
        s.append(" }");
        return s.toString();
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

        @Override
        public String toString() {
            return clazz + (name == null ? "" : ":" + name);
        }
    }

    private static final class Param<T> {

        private final Id<T> id;
        private final boolean provider;

        Param(Id<T> id, boolean provider) {
            this.id = id;
            this.provider = provider;
        }
    }

    private static final class Meta {

        private final boolean singleton;
        private final Executable executable;
        private final List<Param<?>> params;
        private final Object module;

        Meta(boolean singleton, Executable ctor, List<Param<?>> params) {
            this(singleton, ctor, params, null);
        }

        Meta(boolean singleton, Executable method, List<Param<?>> params, Object module) {
            this.singleton = singleton;
            this.executable = method;
            this.params = params;
            this.module = module;
        }
    }
}
