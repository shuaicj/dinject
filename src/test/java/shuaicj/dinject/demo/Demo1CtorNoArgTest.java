package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Inject via no-arg constructor.
 *
 * @author shuaicj 2018/05/09
 */
public class Demo1CtorNoArgTest {

    static class A {}

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        A a1 = dinject.instance(A.class);
        A a2 = dinject.instance(A.class);
        assertThat(a1).isNotNull();
        assertThat(a2).isNotNull();
        assertThat(a1).isNotEqualTo(a2);
    }
}
