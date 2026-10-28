package com.novel.domain.vo;

import com.novel.domain.po.Novel;
import lombok.Data;
import java.util.Date;

@Data
public class ReadingHistoryVO {
    private Novel novel;
    private Long chapterId;
    private String chapterTitle;
    private Date lastReadAt;
}
