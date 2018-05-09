package shuaicj.dinject.detailed.field;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Inject field via Provider.
 *
 * @author shuaicj 2018/05/09
 */
public class FieldProviderTest {

    static class A {

        @Inject Provider<B> b;
    }

    @Singleton
    static class B {}

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        A a1 = dinject.instance(A.class);
        A a2 = dinject.instance(A.class);
        assertThat(a1.b.get().getClass()).isEqualTo(B.class);
        assertThat(a1.b.get()).isEqualTo(a2.b.get());
    }
}
