package com.impulse.economy;

import java.io.Serializable;
import java.util.Objects;

public class CurrencyBalanceId implements Serializable {
    private String userId;
    private String currencyId;
    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyBalanceId that = (CurrencyBalanceId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(currencyId, that.currencyId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, currencyId);
    }
}
