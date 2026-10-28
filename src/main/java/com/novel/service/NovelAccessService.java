package com.novel.service;

import com.novel.domain.po.Novel;
import com.novel.domain.po.NovelChapter;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.mapper.NovelChapterMapper;
import com.novel.mapper.NovelMapper;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NovelAccessService {
    private final NovelMapper novels;
    private final NovelChapterMapper chapters;

    public Long loginId() {
        Long id = UserContext.getUserId();
        if (id == null) throw new BusinessException(401, "请先登录");
        return id;
    }

    public boolean owner(Novel novel) {
        return novel.getAuthorId().equals(UserContext.getUserId());
    }

    public Novel readable(Long id) {
        Novel novel = novels.selectById(id);
        if (novel == null) throw new BusinessException(404, "小说不存在");
        if (!"PUBLISHED".equals(novel.getStatus()) && !owner(novel))
            throw new BusinessException(403, "小说尚未公开");
        return novel;
    }

    public Novel author(Long id) {
        loginId();
        Novel novel = readable(id);
        if (!owner(novel)) throw new BusinessException(403, "只有小说作者可以操作");
        return novel;
    }

    public NovelChapter chapter(Long id) {
        NovelChapter chapter = chapters.selectById(id);
        if (chapter == null) throw new BusinessException(404, "章节不存在");
        Novel novel = readable(chapter.getNovelId());
        if (!"PUBLISHED".equals(chapter.getStatus()) && !owner(novel))
            throw new BusinessException(403, "章节尚未公开");
        return chapter;
    }
}
