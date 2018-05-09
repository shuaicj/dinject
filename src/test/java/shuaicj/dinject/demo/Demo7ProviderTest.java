package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Usage of Provider, which can solve circular dependencies or lazy initiation.
 *
 * @author shuaicj 2018/05/09
 */
public class Demo7ProviderTest {

    static class A {

        final Provider<B> b;

        @Inject A(Provider<B> b) { this.b = b; }
    }

    static class B {

        final Provider<C> c;

        @Inject B(Provider<C> c) { this.c = c; }
    }

    static class C {

        final Provider<A> a;

        @Inject C(Provider<A> a) { this.a = a; }
    }

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        A a = dinject.instance(A.class);
        assertThat(a).isNotNull();
        assertThat(a.b.get().getClass()).isEqualTo(B.class);
        assertThat(a.b.get().c.get().getClass()).isEqualTo(C.class);
    }
}
