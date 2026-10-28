package com.novel.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionVO {

    /**
     * 会话 ID，也可以改成 String 类型
     */
    private String id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 最近更新时间
     */
    private LocalDateTime updatedAt;
}