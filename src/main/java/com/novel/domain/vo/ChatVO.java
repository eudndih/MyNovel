package com.novel.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatVO {
    private String sessionId;
    private String role;
    private String content;
    private LocalDateTime time;
}
