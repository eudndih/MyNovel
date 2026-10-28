package com.novel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.domain.dto.ChapterDTO;
import com.novel.domain.dto.NovelDTO;
import com.novel.domain.po.Novel;
import com.novel.domain.vo.PageResult;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.result.Result;
import com.novel.service.NovelService;
import com.novel.mapper.NovelMapper;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author 钱力川
* @description 针对表【novel(小说表)】的数据库操作Service实现
* @createDate 2026-10-12 08:44:12
*/
@Service
@RequiredArgsConstructor
public class NovelServiceImpl extends ServiceImpl<NovelMapper, Novel>
    implements NovelService{
    private final NovelMapper novelMapper;
    private final com.novel.service.NovelAccessService access;
    private final com.novel.mapper.SysUserMapper users;
    private final com.novel.mapper.NovelLikeMapper likes;
    @Override
    public void addNovel(NovelDTO novelDTO) {
        Long userId = access.loginId();
        validate(novelDTO);
        Novel novel = new Novel();
        BeanUtils.copyProperties(novelDTO,novel);
        novel.setAuthorId(userId);
        novel.setStatus("DRAFT");
        novel.setViewCount(0L); novel.setLikeCount(0L); novel.setCommentCount(0L);
        novelMapper.insert(novel);
    }

    @Override
    public Result<Novel> queryNovelById(long id) {
        Novel novel = access.readable(id);
        if(novel == null){
            throw new BusinessException("小说不存在");
        }
        decorate(novel);
        return Result.success(novel);
    }

    @Override
    public Result<PageResult<Novel>> queryNovels(String keyword, String category,
                                                  String sort, int page, int size,
                                                  boolean mine) {
        int currentPage = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 100);

        LambdaQueryWrapper<Novel> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(query -> query.like(Novel::getTitle, keyword.trim())
                    .or().like(Novel::getSummary, keyword.trim()));
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(Novel::getCategory, category.trim());
        }

        if (mine) {
            Long userId = UserContext.getUserId();
            if (userId == null) {
                throw new BusinessException("请先登录");
            }
            wrapper.eq(Novel::getAuthorId, userId);
        } else {
            wrapper.eq(Novel::getStatus, "PUBLISHED");
        }

        if ("views".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc(Novel::getViewCount);
        } else if ("popular".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc(Novel::getLikeCount);
        } else {
            wrapper.orderByDesc(Novel::getCreatedAt);
        }

        Page<Novel> resultPage = novelMapper.selectPage(
                new Page<>(currentPage + 1L, pageSize), wrapper);
        resultPage.getRecords().forEach(this::decorate);
        return Result.success(PageResult.from(resultPage));
    }

    @Override
    public void updateNovel(long id, NovelDTO novelDTO) {
        Novel novel = access.author(id);
        validate(novelDTO);
        Long userId = UserContext.getUserId();
        Long authorId = novel.getAuthorId();
        if(!userId.equals(authorId)){
            throw new BusinessException("您不是作者无法修改");
        }
        Object previousStatus = novel.getStatus();
        BeanUtils.copyProperties(novelDTO,novel);
        if (novelDTO.getStatus() == null) novel.setStatus(previousStatus);
        novelMapper.updateById(novel);
    }

    @Override
    public void deleteNovel(long id) {
        Long userId = UserContext.getUserId();
        Novel novel = access.author(id);
        Long authorId = novel.getAuthorId();
        if(!userId.equals(authorId)){
            throw new BusinessException("无法删除");
        }
        novelMapper.deleteById(id);
    }

    private void decorate(Novel novel) {
        com.novel.domain.po.SysUser author = users.selectById(novel.getAuthorId());
        novel.setAuthorName(author == null ? "已注销用户" : author.getUsername());
        Long userId = UserContext.getUserId();
        novel.setLiked(userId != null && likes.selectCount(new LambdaQueryWrapper<com.novel.domain.po.NovelLike>()
                .eq(com.novel.domain.po.NovelLike::getUserId, userId)
                .eq(com.novel.domain.po.NovelLike::getNovelId, novel.getId())) > 0);
    }

    private void validate(NovelDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank() || dto.getTitle().trim().length() > 150)
            throw new BusinessException(400, "小说名称须为1至150个字符");
        if (dto.getSummary() != null && dto.getSummary().length() > 1000) throw new BusinessException(400, "简介不能超过1000字");
        if (dto.getCategory() != null && dto.getCategory().length() > 50) throw new BusinessException(400, "分类过长");
        if (dto.getCoverUrl() != null && dto.getCoverUrl().length() > 500) throw new BusinessException(400, "封面地址过长");
        if (dto.getStatus() != null && !java.util.Set.of("DRAFT", "PUBLISHED", "OFFLINE").contains(dto.getStatus()))
            throw new BusinessException(400, "无效小说状态");
    }
}




