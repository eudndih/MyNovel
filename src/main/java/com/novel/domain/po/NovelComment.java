package com.novel.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 小说评论表
 * @TableName novel_comment
 */
@TableName(value ="novel_comment")
@Data
public class NovelComment {
    /**
     * 评论ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 小说ID，关联 novel.id
     */
    private Long novelId;

    /**
     * 用户ID，关联 sys_user.id
     */
    private Long userId;

    /**
     * 被回复的评论ID；NULL表示一级评论
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 是否屏蔽：0-否，1-是
     */
    private Integer blocked;

    /**
     * 屏蔽时间
     */
    private Date blockedAt;

    /**
     * 创建时间
     */
    private Date createdAt;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        NovelComment other = (NovelComment) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getNovelId() == null ? other.getNovelId() == null : this.getNovelId().equals(other.getNovelId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getBlocked() == null ? other.getBlocked() == null : this.getBlocked().equals(other.getBlocked()))
            && (this.getBlockedAt() == null ? other.getBlockedAt() == null : this.getBlockedAt().equals(other.getBlockedAt()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getNovelId() == null) ? 0 : getNovelId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getBlocked() == null) ? 0 : getBlocked().hashCode());
        result = prime * result + ((getBlockedAt() == null) ? 0 : getBlockedAt().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", novelId=").append(novelId);
        sb.append(", userId=").append(userId);
        sb.append(", parentId=").append(parentId);
        sb.append(", content=").append(content);
        sb.append(", blocked=").append(blocked);
        sb.append(", blockedAt=").append(blockedAt);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}
