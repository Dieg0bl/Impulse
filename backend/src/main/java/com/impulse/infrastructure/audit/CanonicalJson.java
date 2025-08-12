package com.impulse.infrastructure.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class CanonicalJson {
    private static final ObjectMapper M = new ObjectMapper()
        .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private CanonicalJson() {}
    public static String write(Object obj) {
        try { return M.writeValueAsString(obj); } catch (Exception e) { throw new IllegalArgumentException(e); }
    }
}
