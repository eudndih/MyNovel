package com.novel.domain.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> content;
    private long totalElements;
    private long totalPages;
    private long number;
    private long size;

    public static <T> PageResult<T> from(IPage<T> page) {
        long totalPages = page.getSize() == 0
                ? 0
                : (page.getTotal() + page.getSize() - 1) / page.getSize();
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                totalPages,
                page.getCurrent() - 1,
                page.getSize()
        );
    }
}
