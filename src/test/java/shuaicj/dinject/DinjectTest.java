package shuaicj.dinject;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

/**
 * Test dinject.
 *
 * @author shuaicj 2018/04/12
 */
public class DinjectTest {

    // @Test
    // public void example1() {
    //     Dinject dinject = Dinject.create();
    //     Example1.A a1 = dinject.instance(Example1.A.class);
    //     Example1.A a2 = dinject.instance(Example1.A.class);
    //     assertThat(a1).isNotNull();
    //     assertThat(a2).isNotNull();
    //     assertThat(a1).isNotEqualTo(a2);
    // }
    //
    // @Test
    // public void example2() {
    //     Dinject dinject = Dinject.create();
    //     Example2.A a1 = dinject.instance(Example2.A.class);
    //     Example2.A a2 = dinject.instance(Example2.A.class);
    //     assertThat(a1).isNotNull();
    //     assertThat(a2).isNotNull();
    //     assertThat(a1).isEqualTo(a2);
    // }
    //
    // @Test
    // public void example3() {
    //     Dinject dinject = Dinject.create();
    //     Example3.A a = dinject.instance(Example3.A.class);
    //     assertThat(a).isNotNull();
    //     assertThat(a.b).isNotNull();
    // }
    //
    // @Test
    // public void example4() {
    //     Dinject dinject = Dinject.create();
    //     Example4.A a = dinject.instance(Example4.A.class);
    //     assertThat(a.b).isNotNull();
    // }
    //
    // @Test
    // public void example5() {
    //     Dinject dinject = Dinject.create(Example5.Mod.class);
    //     Example5.I i = dinject.instance(Example5.I.class);
    //     assertThat(i).isNotNull();
    // }
    //
    // @Test
    // public void example6() {
    //     Dinject dinject = Dinject.create(Example6.Mod.class);
    //     Example6.A a = dinject.instance(Example6.A.class);
    //     assertThat(a.b.name).isEqualTo("b2");
    // }

    @Test
    public void moduleDoesNotHaveNoArgConstructor() {
        assertThatThrownBy(() -> Dinject.create(ModuleDoesNotHaveNoArgConstructor.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("no-arg constructor required for module");
    }

    @SuppressWarnings("unused")
    static class ModuleDoesNotHaveNoArgConstructor {
        ModuleDoesNotHaveNoArgConstructor(int a) {}
    }

    @Test
    public void multipleProvides() {
        assertThatThrownBy(() -> Dinject.create(MultipleProvides.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("multiple @Provides for");
    }

    @SuppressWarnings("unused")
    static class MultipleProvides {

        @Provides A a1() { return new A(); }
        @Provides A a2() { return new A(); }

        static class A {}
    }

    @Test
    public void multipleInjectConstructors() {
        // assertThatThrownBy(() -> Dinject.create(MultipleInjectConstructors.class))
        //         .isInstanceOf(DinjectException.class)
        //         .hasMessageStartingWith("multiple @Provides for");
    }

    @SuppressWarnings("unused")
    static class MultipleInjectConstructors {
        // @Inject MultipleInjectConstructors(String a) {}
        // @Inject MultipleInjectConstructors(String a, String b) {}
    }
}
