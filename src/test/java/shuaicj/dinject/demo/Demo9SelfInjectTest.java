package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Inject Dinject itself.
 *
 * @author shuaicj 2018/05/09
 */
public class Demo9SelfInjectTest {

    static class A {

        final Dinject dinject;
        final B b;

        @Inject A(Dinject dinject) {
            this.dinject = dinject;
            this.b = dinject.instance(B.class);
        }
    }

    @Singleton
    static class B {

        final Dinject dinject;

        @Inject B(Dinject dinject) { this.dinject = dinject; }
    }

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        A a = dinject.instance(A.class);
        B b = dinject.instance(B.class);
        assertThat(a.dinject).isEqualTo(b.dinject);
        assertThat(a.b).isEqualTo(b);
    }
}
