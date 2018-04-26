package shuaicj.dinject;

import javax.inject.Inject;
import javax.inject.Named;

@SuppressWarnings("unused")
class Example6 {

    static class Mod {

        @Provides @Named("b1") B b1() { return new B("b1"); }

        @Provides @Named("b2") B b2() { return new B("b2"); }
    }

    static class A {

        final B b;

        @Inject A(@Named("b2") B b) { this.b = b; }
    }

    static class B {

        final String name;

        B(String name) { this.name = name; }
    }
}
