package shuaicj.dinject;

@SuppressWarnings("unused")
class Example5 {

    static class Mod {
        @Provides I i() { return new A(); }
    }

    interface I {
        void nothing();
    }

    static class A implements I {
        @Override public void nothing() {}
    }
}
