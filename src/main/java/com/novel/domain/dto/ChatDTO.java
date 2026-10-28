package com.novel.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChatDTO {
    private Integer novelId;
    private Integer chapterId;
    private String title;
    private String text;
    private String prompt;
}
