package shuaicj.dinject;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("unused")
class Example9 {

    static class A {

        final Dinject dinject;
        final B b;

        @Inject A(Dinject dinject) {
            this.dinject = dinject;
            this.b = dinject.instance(B.class);
        }
    }

    @Singleton
    static class B {

        final Dinject dinject;

        @Inject B(Dinject dinject) { this.dinject = dinject; }
    }
}
