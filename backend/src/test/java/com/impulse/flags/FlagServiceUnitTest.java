package com.impulse.flags;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FlagServiceUnitTest {

    @Test
    void isOn_defaults(){
        com.impulse.common.flags.FlagService fs = new com.impulse.common.flags.FlagService();
        // The real service reads a YAML file when managed by Spring. For a plain unit test
        // inject a small flags map via reflection so behaviour is deterministic.
        try {
            java.lang.reflect.Field f = fs.getClass().getDeclaredField("flags");
            f.setAccessible(true);
            f.set(fs, java.util.Map.of("activation", java.util.Map.of("quickwin", true, "missing", false)));
        } catch (ReflectiveOperationException e){
            throw new RuntimeException(e);
        }
        assertThat(fs.isOn("activation.quickwin")).isTrue();
        assertThat(fs.isOn("activation.missing")).isFalse();
    }
}
