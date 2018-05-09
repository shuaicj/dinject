package shuaicj.dinject.detailed.named;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.Provides;

/**
 * Usage of combination with @Named, @Singleton, and Provider.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class NamedProviderSingletonTest {

    static class Module {

        @Provides @Named("c1") C c1() { return new C(); }

        @Provides @Named("c2") @Singleton C c2() { return new C(); }
    }

    static class A {

        @Inject @Named("c1") C c1;

        @Inject @Named("c2") C c2;
    }

    static class B {

        @Inject @Named("c1") Provider<C> c1;

        @Inject @Named("c2") Provider<C> c2;
    }

    static class C {}

    @Test
    public void test() {
        Dinject dinject = Dinject.create(Module.class);
        A a1 = dinject.instance(A.class);
        A a2 = dinject.instance(A.class);
        B b1 = dinject.instance(B.class);
        B b2 = dinject.instance(B.class);
        assertThat(a1.c1).isNotEqualTo(a2.c1);
        assertThat(a1.c2).isEqualTo(a2.c2);
        assertThat(b1.c1.get()).isNotEqualTo(b2.c1.get());
        assertThat(b1.c2.get()).isEqualTo(b2.c2.get());
    }
}
