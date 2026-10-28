package com.novel.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 阅读历史表
 * @TableName reading_history
 */
@TableName(value ="reading_history")
@Data
public class ReadingHistory {
    /**
     * 阅读记录ID，主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID，关联 sys_user.id
     */
    private Long userId;

    /**
     * 小说ID，关联 novel.id
     */
    private Long novelId;

    /**
     * 章节ID，关联 novel_chapter.id
     */
    private Long chapterId;

    /**
     * 最后阅读时间
     */
    private Date lastReadAt;

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
        ReadingHistory other = (ReadingHistory) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getNovelId() == null ? other.getNovelId() == null : this.getNovelId().equals(other.getNovelId()))
            && (this.getChapterId() == null ? other.getChapterId() == null : this.getChapterId().equals(other.getChapterId()))
            && (this.getLastReadAt() == null ? other.getLastReadAt() == null : this.getLastReadAt().equals(other.getLastReadAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getNovelId() == null) ? 0 : getNovelId().hashCode());
        result = prime * result + ((getChapterId() == null) ? 0 : getChapterId().hashCode());
        result = prime * result + ((getLastReadAt() == null) ? 0 : getLastReadAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", novelId=").append(novelId);
        sb.append(", chapterId=").append(chapterId);
        sb.append(", lastReadAt=").append(lastReadAt);
        sb.append("]");
        return sb.toString();
    }
}