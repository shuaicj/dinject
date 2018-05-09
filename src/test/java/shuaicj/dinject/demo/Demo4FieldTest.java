package shuaicj.dinject.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import shuaicj.dinject.Dinject;

/**
 * Inject fields.
 *
 * @author shuaicj 2018/05/09
 */
public class Demo4FieldTest {

    static class A {
        @Inject B b;
    }

    static class B {}

    @Test
    public void test() {
        Dinject dinject = Dinject.create();
        A a = dinject.instance(A.class);
        assertThat(a.b).isNotNull();
    }
}
