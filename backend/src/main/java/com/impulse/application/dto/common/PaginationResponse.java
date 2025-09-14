package com.impulse.application.dto.common;

import java.util.List;

/**
 * DTO para respuestas paginadas
 */
public class PaginationResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    private String sortBy;
    private String sortDirection;

    public PaginationResponse() {
    }

    public PaginationResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.empty = content == null || content.isEmpty();
    }

    public PaginationResponse(List<T> content, int page, int size, long totalElements, String sortBy, String sortDirection) {
        this(content, page, size, totalElements);
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
        this.empty = content == null || content.isEmpty();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        this.first = page == 0;
        this.last = page >= totalPages - 1;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        if (totalElements > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.last = page >= totalPages - 1;
        }
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        if (size > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
            this.last = page >= totalPages - 1;
        }
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        this.last = page >= totalPages - 1;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
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
        return "PaginationResponse{" +
                "content=" + content +
                ", page=" + page +
                ", size=" + size +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", empty=" + empty +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
