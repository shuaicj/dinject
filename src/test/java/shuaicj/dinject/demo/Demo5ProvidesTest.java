package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.Provides;

/**
 * Usage of @Provides.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class Demo5ProvidesTest {

    static class Module {
        @Provides I i() { return new A(); }
    }

    interface I {
        void nothing();
    }

    static class A implements I {
        @Override public void nothing() {}
    }

    @Test
    public void test() {
        Dinject dinject = Dinject.create(Module.class);
        I i = dinject.instance(I.class);
        assertThat(i).isNotNull();
        assertThat(i.getClass()).isEqualTo(A.class);
    }
}
