package com.novel.domain.vo;

import lombok.Data;
import java.util.Date;

@Data
public class CommentVO {
    private Long id;
    private Long novelId;
    private Long parentId;
    private Long userId;
    private String username;
    private String replyToUsername;
    private String content;
    private Boolean blocked;
    private Long likeCount;
    private Boolean liked;
    private Date createdAt;
}
