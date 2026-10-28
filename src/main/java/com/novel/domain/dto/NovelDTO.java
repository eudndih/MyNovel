package com.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NovelDTO {

    private String title;

    /**
     * 小说简介
     */
    private String summary;

    /**
     * 小说分类
     */
    private String category;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 小说状态：DRAFT-草稿，PUBLISHED-已发布，OFFLINE-已下架
     */
    private String status;
}
