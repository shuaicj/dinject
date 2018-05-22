# dinject

A lightweight dependency injector for Java with minimum size about 11KB,
generally based on [javax-inject](https://github.com/javax-inject/javax-inject).

### Get Started

#### Add in Maven pom:
```xml
<dependencies>
    <dependency>
        <groupId>com.github.shuaicj</groupId>
        <artifactId>dinject</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### Usage examples:
1. Java classes with no-arg constructor can be instantiated directly:
```java
class A {}

Dinject dinject = Dinject.create();
A a = dinject.instance(A.class);
```

2. Constructor injection:
```java
class A {

    final B b;
    final C c;

    @Inject A(B b, C c) {
        this.b = b;
        this.c = c;
    }
}

class B {}

class C {}

A a = dinject.instance(A.class);
```

3. Define singletons:
```java
@Singleton
class A {}
```

4. Field injection:
```java
class A {
    @Inject B b;
}

class B {}

A a = dinject.instance(A.class);
```

5. Custom instances with `@Provides` and modules:
```java
class Module {
    @Provides I i() { return new A(); }
}

interface I {}

class A implements I {}

Dinject dinject = Dinject.create(Module.class);
I i = dinject.instance(I.class);
```

6. Naming instances with `@Named`:
```java
class Module {

    @Provides @Named("b1") B b1() { return new B("b1"); }

    @Provides @Named("b2") B b2() { return new B("b2"); }
}

class A {

    final B b;

    @Inject A(@Named("b2") B b) { this.b = b; }
}

class B {

    final String name;

    B(String name) { this.name = name; }
}

A a = dinject.instance(A.class);
B b1 = dinject.instance(B.class, "b1");
B b2 = dinject.instance(B.class, "b2");
```

7. Solve circular dependencies or lazy initiation with `@Provider`:
```java
class A {

    final Provider<B> b;

    @Inject A(Provider<B> b) { this.b = b; }
}

class B {

    final Provider<C> c;

    @Inject B(Provider<C> c) { this.c = c; }
}

class C {

    final Provider<A> a;

    @Inject C(Provider<A> a) { this.a = a; }
}

A a = dinject.instance(A.class);
```

8. Init `dinject` with multiple modules:
```java
class Module1 {
    @Provides A a() { return new A(); }
}

class Module2 {
    @Provides B b() { return new B(); }
}

Dinject dinject = Dinject.create(Module1.class, Module2.class);
```

9. Inject `dinject` itself:
```java
class A {

    final Dinject dinject;

    @Inject A(Dinject dinject) { this.dinject = dinject; }
}

A a = dinject.instance(A.class);
```