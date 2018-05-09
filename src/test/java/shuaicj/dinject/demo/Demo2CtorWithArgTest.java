package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Inject via constructor with args.
 *
 * @author shuaicj 2018/05/09
 */
public class Demo2CtorWithArgTest {

    static class A {

        final B b;
        final C c;

        @Inject A(B b, C c) {
            this.b = b;
            this.c = c;
        }
    }

    static class B {}

    static class C {}

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        A a = dinject.instance(A.class);
        assertThat(a).isNotNull();
        assertThat(a.b).isNotNull();
        assertThat(a.c).isNotNull();
    }
}
