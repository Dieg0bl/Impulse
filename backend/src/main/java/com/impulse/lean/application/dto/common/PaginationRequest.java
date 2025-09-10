package com.impulse.lean.application.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * IMPULSE LEAN v1 - Pagination Request DTO
 * 
 * Standard pagination parameters for API requests
 * 
 * @author IMPULSE Team
 * @version 1.0.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationRequest {

    @NotNull(message = "Page number is required")
    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page = 0;

    @NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be positive")
    private Integer size = 20;

    private String sortBy;
    private String sortDirection = "DESC";
    private String search;

    // Constructors
    public PaginationRequest() {}

    public PaginationRequest(Integer page, Integer size) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 20;
    }

    public PaginationRequest(Integer page, Integer size, String sortBy, String sortDirection) {
        this(page, size);
        this.sortBy = sortBy;
        this.sortDirection = sortDirection != null ? sortDirection : "DESC";
    }

    // Business methods
    public boolean hasSearch() {
        return search != null && !search.trim().isEmpty();
    }

    public boolean hasSorting() {
        return sortBy != null && !sortBy.trim().isEmpty();
    }

    public String getSafeSearch() {
        return hasSearch() ? search.trim() : null;
    }

    public String getSafeSortBy() {
        return hasSorting() ? sortBy.trim() : null;
    }

    public String getSafeSortDirection() {
        if (sortDirection == null) return "DESC";
        String upper = sortDirection.toUpperCase();
        return "ASC".equals(upper) ? "ASC" : "DESC";
    }

    public int getOffset() {
        return page * size;
    }

    public boolean isValidPageSize() {
        return size > 0 && size <= 100; // Max 100 items per page
    }

    // Getters and Setters
    public Integer getPage() { return page; }
    public void setPage(Integer page) { 
        this.page = page != null ? Math.max(0, page) : 0; 
    }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { 
        this.size = size != null ? Math.max(1, Math.min(100, size)) : 20; 
    }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    @Override
    public String toString() {
        return "PaginationRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", search='" + search + '\'' +
                '}';
    }
}
