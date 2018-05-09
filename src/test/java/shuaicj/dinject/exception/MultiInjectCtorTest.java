package shuaicj.dinject.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.inject.Inject;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.DinjectException;

/**
 * Multiple constructor with @Inject.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class MultiInjectCtorTest {

    static class A {

        @Inject A(B b) {}

        @Inject A(B b, C c) {}
    }

    static class B {}

    static class C {}

    @Test
    public void test() {
        assertThatThrownBy(() -> Dinject.create().instance(A.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("multiple @Inject for");
    }
}
