package shuaicj.dinject.detailed.singleton;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Singleton;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.Provides;

/**
 * Singletons defined with @Provides and @Singleton together.
 *
 * @author shuaicj 2018/05/09
 */
public class SingletonProvidesTest {

    @SuppressWarnings("unused")
    static class Module {

        @Provides
        @Singleton
        A a() { return new A(); }
    }

    static class A {}

    @Test
    public void test() {
        Dinject dinject = Dinject.create(Module.class);
        A a1 = dinject.instance(A.class);
        A a2 = dinject.instance(A.class);
        assertThat(a1).isNotNull();
        assertThat(a2).isNotNull();
        assertThat(a1).isEqualTo(a2);
    }
}
