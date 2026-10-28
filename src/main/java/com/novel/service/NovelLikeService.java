package com.novel.service;

import com.novel.domain.po.NovelLike;
import com.baomidou.mybatisplus.extension.service.IService;
import com.novel.result.Result;

/**
* @author 钱力川
* @description 针对表【novel_like(小说点赞表)】的数据库操作Service
* @createDate 2026-10-12 08:44:12
*/
public interface NovelLikeService extends IService<NovelLike> {

    Result<Void> like(Long novelId);

    Result<Void> unLike(Long novelId);
}
