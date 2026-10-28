package com.novel.service;

import com.novel.domain.dto.ChapterDTO;
import com.novel.domain.dto.NovelDTO;
import com.novel.domain.po.Novel;
import com.novel.domain.vo.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.result.Result;

/**
* @author 钱力川
* @description 针对表【novel(小说表)】的数据库操作Service
* @createDate 2026-10-12 08:44:12
*/
public interface NovelService extends IService<Novel> {

    void addNovel(NovelDTO novelDTO);

    Result<Novel> queryNovelById(long id);

    void updateNovel(long id, NovelDTO novelDTO);

    void deleteNovel(long id);

    Result<PageResult<Novel>> queryNovels(String keyword, String category,
                                          String sort, int page, int size,
                                          boolean mine);
}
