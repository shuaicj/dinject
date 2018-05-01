package shuaicj.dinject;

import javax.inject.Inject;

@SuppressWarnings("unused")
class ExceptionExample5 {

    static class A {

        @Inject A(B b) {}
    }

    static class B {

        @Inject B(C c) {}
    }

    static class C {

        @Inject C(A a) {}
    }
}
