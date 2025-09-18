package com.impulse.shared.utils;

/** Simple pagination parameters holder */
public record Pagination(int page, int size) {
    public Pagination {
        if (page < 0) throw new IllegalArgumentException("page >= 0");
        if (size <= 0 || size > 500) throw new IllegalArgumentException("size 1..500");
    }
}
