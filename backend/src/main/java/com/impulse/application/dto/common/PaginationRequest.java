package com.impulse.application.dto.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitudes de paginación
 */
public class PaginationRequest {

    @NotNull(message = "El número de página no puede ser nulo")
    @Min(value = 0, message = "El número de página debe ser mayor o igual a 0")
    private Integer page;

    @NotNull(message = "El tamaño de página no puede ser nulo")
    @Min(value = 1, message = "El tamaño de página debe ser mayor a 0")
    private Integer size;

    private String sortBy;
    private String sortDirection;

    public PaginationRequest() {
        this.page = 0;
        this.size = 10;
        this.sortDirection = "ASC";
    }

    public PaginationRequest(Integer page, Integer size) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
        this.sortDirection = "ASC";
    }

    public PaginationRequest(Integer page, Integer size, String sortBy, String sortDirection) {
        this.page = page != null ? page : 0;
        this.size = size != null ? size : 10;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection != null ? sortDirection : "ASC";
    }

    // Getters and Setters
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "PaginationRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
