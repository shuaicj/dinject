package shuaicj.dinject.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.inject.Inject;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.DinjectException;

/**
 * Circular dependency.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class CircularDependencyTest {

    static class A {

        @Inject A(B b) {}
    }

    static class B {

        @Inject C c;
    }

    static class C {

        @Inject C(A a) {}
    }

    @Test
    public void test() {
        assertThatThrownBy(() -> Dinject.create().instance(A.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("circular dependency");
    }
}
