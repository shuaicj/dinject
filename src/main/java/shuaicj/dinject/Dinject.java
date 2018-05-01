package shuaicj.dinject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main class create dinject.
 *
 * @author shuaicj 2018/04/12
 */
public final class Dinject {

    private final Map<Id, Meta> metas = new ConcurrentHashMap<>();
    private final Map<Id, Object> singletons = new ConcurrentHashMap<>();

    public static Dinject create(Class<?>... moduleClasses) {
        return new Dinject(new HashSet<>(Arrays.asList(moduleClasses)));
    }

    public static Dinject create(Collection<Class<?>> moduleClasses) {
        return new Dinject(new HashSet<>(moduleClasses));
    }

    private Dinject(Collection<Class<?>> moduleClasses) {
        // instance of Dinject itself is supposed to be a singleton
        singletons.put(new Id(Dinject.class), this);
        // parse modules and all dependencies
        parseModules(moduleClasses).forEach(id -> parseDependencies(id, new LinkedHashSet<>()));
    }

    public <T> T instance(Class<T> clazz) {
        return instance(clazz, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T instance(Class<T> clazz, String name) {
        Id id = new Id(clazz, name);
        Object obj = singletons.get(id);
        if (obj != null) {
            return (T) obj;
        }
        Meta meta = metas.get(id);
        if (meta == null) {
            parseDependencies(id, new LinkedHashSet<>());
            meta = metas.get(id);
        }
        if (meta.singleton) {
            synchronized (this) {
                obj = singletons.get(id);
                if (obj == null) {
                    obj = instance(meta);
                    singletons.put(id, obj);
                }
                return (T) obj;
            }
        }
        return (T) instance(meta);
    }

    private Object instance(Meta meta) {
        Object[] params = meta.params
                .stream()
                .map(p -> {
                    if (p.provider) {
                        return (Provider) () -> instance(p.id.clazz, p.id.name);
                    }
                    return instance(p.id.clazz, p.id.name);
                }).toArray();

        if (meta.executable instanceof Constructor) {
            return instantiateByConstructor((Constructor<?>) meta.executable, params);
        }
        return instantiateByMeduleMethod((Method) meta.executable, meta.module, params);
    }

    private Set<Id> parseModules(Iterable<Class<?>> moduleClasses) {
        Set<Id> roots = new HashSet<>();
        for (Class<?> clazz : moduleClasses) {
            Constructor<?> ctor;
            try {
                ctor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new DinjectException("no-arg constructor required for module " + clazz.getName());
            }
            ctor.setAccessible(true);

            Object module = instantiateByConstructor(ctor);

            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Provides.class)) {
                    m.setAccessible(true);
                    Class<?> t = m.getReturnType();
                    String n = m.isAnnotationPresent(Named.class) ? m.getAnnotation(Named.class).value() : null;
                    boolean s = m.isAnnotationPresent(Singleton.class) || t.isAnnotationPresent(Singleton.class);
                    Id id = new Id(t, n);
                    if (metas.putIfAbsent(id, new Meta(s, m, getParams(m), module)) != null) {
                        throw new DinjectException("multiple @Provides for " + id);
                    }
                    roots.add(id);
                }
            }
        }
        return roots;
    }

    private void parseDependencies(Id id, LinkedHashSet<Id> path) {
        // exclude self
        if (id.clazz.equals(Dinject.class)) {
            return;
        }
        // ensure no circular dependency
        if (path.contains(id)) {
            throw new DinjectException("circular dependency " + pathToString(id, path));
        }
        // return immediately if it's duplicate check
        Meta meta = metas.get(id);
        if (path.isEmpty() && meta != null && meta.executable instanceof Constructor) {
            return;
        }
        path.add(id);
        // ensure meta is created
        if (meta == null) {
            boolean s = id.clazz.isAnnotationPresent(Singleton.class);
            Constructor<?> c = getConstructor(id.clazz);
            metas.putIfAbsent(id, new Meta(s, c, getParams(c)));
            meta = metas.get(id);
        }
        // parse recursively
        for (Param p : meta.params) {
            if (p.provider) {
                parseDependencies(p.id, new LinkedHashSet<>());
            } else {
                parseDependencies(p.id, path);
            }
        }
    }

    private Constructor<?> getConstructor(Class<?> clazz) {
        Constructor ctor = null;
        Constructor none = null;
        for (Constructor c : clazz.getDeclaredConstructors()) {
            if (c.getParameterCount() == 0) {
                none = c;
            }
            if (c.isAnnotationPresent(Inject.class)) {
                if (ctor != null) {
                    throw new DinjectException("multiple @Inject for " + clazz.getName());
                }
                ctor = c;
            }
        }

        if (ctor == null && none == null) {
            throw new DinjectException("no applicable constructor for " + clazz.getName());
        }

        ctor = ctor == null ? none : ctor;
        ctor.setAccessible(true);
        return ctor;
    }

    private Object instantiateByConstructor(Constructor<?> ctor, Object... params) {
        try {
            return ctor.newInstance(params);
        } catch (Exception e) {
            throw new DinjectException("cannot instantiate " + ctor.getDeclaringClass() + " by constructor", e);
        }
    }

    private Object instantiateByMeduleMethod(Method method, Object module, Object... params) {
        try {
            return method.invoke(module, params);
        } catch (Exception e) {
            throw new DinjectException("cannot instantiate " + method.getReturnType() + " by module method", e);
        }
    }

    private List<Param> getParams(Executable executable) {
        List<Param> rt = new ArrayList<>();
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
            rt.add(new Param(new Id(t, n), p));
        }
        return rt;
    }

    private String pathToString(Id id, LinkedHashSet<Id> path) {
        StringBuilder s = new StringBuilder("{ ");
        boolean found = false;
        for (Id d : path) {
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

    private static final class Id {

        private final Class<?> clazz;
        private final String name;

        Id(Class<?> clazz) {
            this(clazz, null);
        }

        Id(Class<?> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(clazz, id.clazz) && Objects.equals(name, id.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, name);
        }

        @Override
        public String toString() {
            return clazz.getName() + (name == null ? "" : ":" + name);
        }
    }

    private static final class Param {

        private final Id id;
        private final boolean provider;

        Param(Id id, boolean provider) {
            this.id = id;
            this.provider = provider;
        }
    }

    private static final class Meta {

        private final boolean singleton;
        private final Executable executable;
        private final List<Param> params;
        private final Object module;

        Meta(boolean singleton, Executable ctor, List<Param> params) {
            this(singleton, ctor, params, null);
        }

        Meta(boolean singleton, Executable method, List<Param> params, Object module) {
            this.singleton = singleton;
            this.executable = method;
            this.params = params;
            this.module = module;
        }
    }
}
