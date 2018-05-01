package shuaicj.dinject;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * Test dinject.
 *
 * @author shuaicj 2018/04/12
 */
public class DinjectTest {

    @Test
    public void example1() {
        Dinject dinject = Dinject.create();
        Example1.A a1 = dinject.instance(Example1.A.class);
        Example1.A a2 = dinject.instance(Example1.A.class);
        assertThat(a1).isNotNull();
        assertThat(a2).isNotNull();
        assertThat(a1).isNotEqualTo(a2);
    }

    @Test
    public void example2() {
        Dinject dinject = Dinject.create();
        Example2.A a1 = dinject.instance(Example2.A.class);
        Example2.A a2 = dinject.instance(Example2.A.class);
        assertThat(a1).isNotNull();
        assertThat(a2).isNotNull();
        assertThat(a1).isEqualTo(a2);
    }

    @Test
    public void example3() {
        Dinject dinject = Dinject.create();
        Example3.A a = dinject.instance(Example3.A.class);
        assertThat(a).isNotNull();
        assertThat(a.b).isNotNull();
    }

    // @Test
    // public void example4() {
    //     Dinject dinject = Dinject.create();
    //     Example4.A a = dinject.instance(Example4.A.class);
    //     assertThat(a.b).isNotNull();
    // }

    @Test
    public void example5() {
        Dinject dinject = Dinject.create(Example5.Mod.class);
        Example5.I i = dinject.instance(Example5.I.class);
        assertThat(i).isNotNull();
        assertThat(i.getClass()).isEqualTo(Example5.A.class);
    }

    @Test
    public void example6() {
        Dinject dinject = Dinject.create(Example6.Mod.class);
        Example6.A a = dinject.instance(Example6.A.class);
        assertThat(a.b.name).isEqualTo("b2");
    }

    @Test
    public void example7() {
        Dinject dinject = Dinject.create();
        Example7.A a = dinject.instance(Example7.A.class);
        assertThat(a).isNotNull();
        assertThat(a.b.get().getClass()).isEqualTo(Example7.B.class);
        assertThat(a.b.get().c.get().getClass()).isEqualTo(Example7.C.class);
    }

    @Test
    public void example8() {
        Dinject dinject = Dinject.create();
        Example8.A a1 = dinject.instance(Example8.A.class);
        Example8.A a2 = dinject.instance(Example8.A.class);
        assertThat(a1.b.get()).isEqualTo(a2.b.get());
    }

    @Test
    public void example9() {
        Dinject dinject = Dinject.create();
        Example9.A a = dinject.instance(Example9.A.class);
        Example9.B b = dinject.instance(Example9.B.class);
        assertThat(a.dinject).isEqualTo(b.dinject);
        assertThat(a.b).isEqualTo(b);
    }

    @Test
    public void exceptionExample1() {
        assertThatThrownBy(() -> Dinject.create(ExceptionExample1.Mod.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("no-arg constructor required for module");
    }

    @Test
    public void exceptionExample2() {
        assertThatThrownBy(() -> Dinject.create(ExceptionExample2.Mod.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("multiple @Provides for");
    }

    @Test
    public void exceptionExample3() {
        assertThatThrownBy(() -> Dinject.create().instance(ExceptionExample3.A.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("multiple @Inject for");
    }

    @Test
    public void exceptionExample4() {
        assertThatThrownBy(() -> Dinject.create().instance(ExceptionExample4.A.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("no applicable constructor for");
    }

    @Test
    public void exceptionExample5() {
        assertThatThrownBy(() -> Dinject.create().instance(ExceptionExample5.A.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("circular dependency");
    }
}
