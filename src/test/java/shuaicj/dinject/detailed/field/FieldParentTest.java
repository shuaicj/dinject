package shuaicj.dinject.detailed.field;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Inject;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Inject field from parent.
 *
 * @author shuaicj 2018/05/09
 */
public class FieldParentTest {

    static class A {

        @Inject C c1;
    }

    static class B extends A {

        @Inject C c2;
    }

    static class C {

        static final AtomicLong counter = new AtomicLong();

        final long count;

        C() { count = counter.incrementAndGet(); }
    }

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        B b = dinject.instance(B.class);
        assertThat(b.c1.count).isEqualTo(1L);
        assertThat(b.c2.count).isEqualTo(2L);
    }
}
