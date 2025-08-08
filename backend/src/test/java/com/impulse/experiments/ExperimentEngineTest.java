package com.impulse.experiments;

import org.junit.jupiter.api.Test;import static org.assertj.core.api.Assertions.*;

public class ExperimentEngineTest {
    @Test
    void deterministicVariant(){
        ExperimentEngine e = new ExperimentEngine();
        String v1 = e.variant("onboarding_flow", 123L);
        String v2 = e.variant("onboarding_flow", 123L);
        assertThat(v1).isEqualTo(v2);
    }
}
