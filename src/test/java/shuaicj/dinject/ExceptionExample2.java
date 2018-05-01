package shuaicj.dinject;

@SuppressWarnings("unused")
class ExceptionExample2 {

    static class Mod {

        @Provides A a1() { return new A(); }

        @Provides A a2() { return new A(); }
    }

    static class A {}
}
