package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.Provides;

/**
 * Multiple modules.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class Demo8MultiModuleTest {

    static class Module1 {
        @Provides A a(B b) { return new A(b); }
    }

    static class Module2 {
        @Provides B b(C c) { return new B(c); }
    }

    static class A {

        final B b;

        @Inject A(B b) { this.b = b; }
    }

    static class B {

        final C c;

        @Inject B(C c) { this.c = c; }
    }

    @Singleton
    static class C {}

    @Test
    public void test1() {
        test(Dinject.create(Module1.class, Module2.class));
    }

    @Test
    public void test2() {
        test(Dinject.create(Arrays.asList(Module1.class, Module2.class)));
    }

    private void test(Dinject dinject) {
        A a = dinject.instance(A.class);
        B b = dinject.instance(B.class);
        assertThat(a).isNotNull();
        assertThat(a.getClass()).isEqualTo(A.class);
        assertThat(b).isNotNull();
        assertThat(b.getClass()).isEqualTo(B.class);
        assertThat(a.b).isNotEqualTo(b);
        assertThat(a.b.c).isEqualTo(b.c);
    }
}
