package shuaicj.dinject;

import javax.inject.Inject;

@SuppressWarnings("unused")
class ExceptionExample3 {

    static class A {

        @Inject A(String a) {}

        @Inject A(String a, String b) {}
    }
}
