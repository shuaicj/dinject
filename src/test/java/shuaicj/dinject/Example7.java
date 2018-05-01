package shuaicj.dinject;

import javax.inject.Inject;
import javax.inject.Provider;

@SuppressWarnings("unused")
class Example7 {

    static class A {

        final Provider<B> b;

        @Inject A(Provider<B> b) { this.b = b; }
    }

    static class B {

        final Provider<C> c;

        @Inject B(Provider<C> c) { this.c = c; }
    }

    static class C {

        final Provider<A> a;

        @Inject C(Provider<A> a) { this.a = a; }
    }
}
