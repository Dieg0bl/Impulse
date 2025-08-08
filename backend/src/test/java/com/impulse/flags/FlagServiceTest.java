package com.impulse.flags;

import com.impulse.common.flags.FlagService;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field; // usado para inyectar mapa (reflection)
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

public class FlagServiceTest {
    @Test
    void nestedIsOn() throws Exception {
        FlagService fs = new FlagService();
        Field f = FlagService.class.getDeclaredField("flags"); f.setAccessible(true);
        f.set(fs, Map.of("activation", Map.of("quickwin", true)));
        assertThat(fs.isOn("activation.quickwin")).isTrue();
        assertThat(fs.isOn("activation.missing")).isFalse();
        assertThat(fs.isOn("activation.quickwin.extra" )).isFalse();
    }
}
