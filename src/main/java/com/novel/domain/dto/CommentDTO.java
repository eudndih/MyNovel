package com.novel.domain.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Long novelId;
    private Long parentId;
    private String content;
}
