package shuaicj.dinject.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.DinjectException;
import shuaicj.dinject.Provides;

/**
 * The same type defined in multiple @Provides.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class MultiProvidesTest {

    static class A {}

    static class Module {

        @Provides A a1() { return new A(); }

        @Provides A a2() { return new A(); }
    }

    static class Module1 {

        @Provides A a() { return new A(); }
    }

    static class Module2 {

        @Provides A a() { return new A(); }
    }

    @Test
    public void test1() {
        assertThatThrownBy(() -> Dinject.create(Module.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("multiple @Provides for");
    }

    @Test
    public void test2() {
        assertThatThrownBy(() -> Dinject.create(Module1.class, Module2.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("multiple @Provides for");
    }
}
