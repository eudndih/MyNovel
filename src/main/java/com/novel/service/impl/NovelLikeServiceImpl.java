package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.domain.po.Novel;
import com.novel.domain.po.NovelLike;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.mapper.NovelMapper;
import com.novel.result.Result;
import com.novel.service.NovelLikeService;
import com.novel.mapper.NovelLikeMapper;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author 钱力川
* @description 针对表【novel_like(小说点赞表)】的数据库操作Service实现
* @createDate 2026-10-12 08:44:12
*/
@Service
@RequiredArgsConstructor
public class NovelLikeServiceImpl extends ServiceImpl<NovelLikeMapper, NovelLike>
    implements NovelLikeService{
    private final NovelLikeMapper novelLikeMapper;
    private final NovelMapper  novelMapper;
    private final com.novel.service.NovelAccessService access;
    @Override
    @Transactional
    public Result<Void> like(Long novelId) {
        Long userId = access.loginId();
        access.readable(novelId);
        Novel novel = novelMapper.selectOne(new LambdaQueryWrapper<Novel>().eq(Novel::getId, novelId).last("FOR UPDATE"));
        if(novel == null){
            throw new BusinessException("小说不存在无法点赞");
        }
        if (novelLikeMapper.selectCount(new LambdaQueryWrapper<NovelLike>()
                .eq(NovelLike::getNovelId, novelId).eq(NovelLike::getUserId, userId)) > 0) return Result.success();
        NovelLike novelLike = new NovelLike();
        novelLike.setUserId(userId);
        novelLike.setNovelId(novelId);
        novelLikeMapper.insert(novelLike);
        novelMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Novel>()
                .eq(Novel::getId, novelId).setSql("like_count = like_count + 1"));
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> unLike(Long novelId) {
        Long userId = access.loginId();
        access.readable(novelId);
        novelMapper.selectOne(new LambdaQueryWrapper<Novel>().eq(Novel::getId, novelId).last("FOR UPDATE"));
        NovelLike novelLike = novelLikeMapper.selectOne(new LambdaQueryWrapper<NovelLike>()
                .eq(NovelLike::getNovelId, novelId)
                .eq(NovelLike::getUserId, userId));
        if(novelLike == null){
            return Result.success();
        }
        novelLikeMapper.deleteById(novelLike.getId());
        novelMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Novel>()
                .eq(Novel::getId, novelId).setSql("like_count = GREATEST(like_count - 1, 0)"));
        return Result.success();
    }
}




