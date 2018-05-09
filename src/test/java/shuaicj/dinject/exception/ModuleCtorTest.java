package shuaicj.dinject.exception;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;
import shuaicj.dinject.Dinject;
import shuaicj.dinject.DinjectException;

/**
 * Modules must have no-arg constructor.
 *
 * @author shuaicj 2018/05/09
 */
@SuppressWarnings("unused")
public class ModuleCtorTest {

    static class Module {
        Module(int i) {}
    }

    @Test
    public void test() {
        assertThatThrownBy(() -> Dinject.create(Module.class))
                .isInstanceOf(DinjectException.class)
                .hasMessageStartingWith("no-arg constructor required for module");
    }
}
