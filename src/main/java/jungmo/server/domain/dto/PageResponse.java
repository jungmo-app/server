package jungmo.server.domain.dto;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResponse<T> {

    private final List<T> content;
    private final int currentPage;
    private final int totalPage;
    private final long totalElements;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPage = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }
}
