package com.novel.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评论点赞关系表
 * @TableName comment_like
 */
@TableName("comment_like")
@Data
public class CommentLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 被点赞的评论ID */
    private Long commentId;

    /** 点赞用户ID */
    private Long userId;

    private Date createdAt;
}
