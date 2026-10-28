package com.novel.service;

import com.novel.domain.dto.ChapterDTO;
import com.novel.domain.po.NovelChapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.result.Result;

import java.util.List;

/**
* @author 钱力川
* @description 针对表【novel_chapter(小说章节表)】的数据库操作Service
* @createDate 2026-10-12 08:44:12
*/
public interface NovelChapterService extends IService<NovelChapter> {

    Result<Void> addChapter(Integer novelId,ChapterDTO chapterDTO);

    Result<List<NovelChapter>> queryChapter(Integer novelId);
    Result<List<NovelChapter>> queryChapter(Integer novelId, boolean includeDrafts);

    Result<NovelChapter> queryContent(Integer chapterId);

    Result<Void> updateChapter(Long chapterId, ChapterDTO chapterDTO);

    Result<Void> deleteChapter(Long chapterId);

    Result<Void> read(Integer chapterId);
}
