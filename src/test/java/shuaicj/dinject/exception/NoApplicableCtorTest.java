package shuaicj.dinject.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.DinjectException;

/**
 * No constructor applicable.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class NoApplicableCtorTest {

    static class A {
        A(String a) {}
    }

    @Test
    public void test() {
        assertThatThrownBy(() -> Dinject.create().instance(A.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("no applicable constructor for");
    }
}
