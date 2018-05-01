package shuaicj.dinject;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@SuppressWarnings("unused")
class Example8 {

    static class A {

        final Provider<B> b;

        @Inject A(Provider<B> b) { this.b = b; }
    }

    @Singleton
    static class B {}
}
