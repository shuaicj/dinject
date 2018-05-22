package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.Provides;

/**
 * Usage of @Named.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class Demo6NamedTest {

    static class Module {

        @Provides @Named("b1") B b1() { return new B("b1"); }

        @Provides @Named("b2") B b2() { return new B("b2"); }
    }

    static class A {

        final B b;

        @Inject A(@Named("b2") B b) { this.b = b; }
    }

    static class B {

        final String name;

        B(String name) { this.name = name; }
    }

    @Test
    public void test() {
        Dinject dinject = Dinject.create(Module.class);
        A a = dinject.instance(A.class);
        B b1 = dinject.instance(B.class, "b1");
        B b2 = dinject.instance(B.class, "b2");
        assertThat(a.b.name).isEqualTo(b2.name).isEqualTo("b2");
        assertThat(b1.name).isNotEqualTo(b2.name);
    }
}
