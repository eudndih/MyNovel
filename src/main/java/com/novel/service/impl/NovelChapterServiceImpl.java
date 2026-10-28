package com.novel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.domain.dto.ChapterDTO;
import com.novel.domain.po.Novel;
import com.novel.domain.po.NovelChapter;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.mapper.NovelMapper;
import com.novel.result.Result;
import com.novel.service.NovelChapterService;
import com.novel.mapper.NovelChapterMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.novel.utils.UserContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Date;

/**
* @author 钱力川
* @description 针对表【novel_chapter(小说章节表)】的数据库操作Service实现
* @createDate 2026-10-12 08:44:12
*/
@Service
@RequiredArgsConstructor
public class NovelChapterServiceImpl extends ServiceImpl<NovelChapterMapper, NovelChapter>
    implements NovelChapterService{
    private final NovelChapterMapper novelChapterMapper;
    private final NovelMapper novelMapper;
    private final com.novel.service.NovelAccessService access;
    private final com.novel.service.PersonalLibraryService library;
    @Override
    @Transactional
    public Result<Void> addChapter(Integer novelId,ChapterDTO chapterDTO) {
        access.author(novelId.longValue());
        validate(chapterDTO);
        Novel novel = novelMapper.selectOne(new LambdaQueryWrapper<Novel>()
                .eq(Novel::getId, novelId).last("FOR UPDATE"));
        if(novel == null){
            throw new BusinessException("小说不存在");
        }
        NovelChapter lastChapter = novelChapterMapper.selectOne(
                new LambdaQueryWrapper<NovelChapter>()
                        .eq(NovelChapter::getNovelId, novelId)
                        .orderByDesc(NovelChapter::getChapterNo)
                        .last("LIMIT 1"));
        int chapterNo=0;
        if(lastChapter != null){
            chapterNo=lastChapter.getChapterNo();
        }
        NovelChapter chapter = new NovelChapter();
        BeanUtils.copyProperties(chapterDTO,chapter);
        chapter.setNovelId(novel.getId());
        chapter.setChapterNo(chapterNo+1);
        chapter.setWordCount(countWords(chapterDTO.getContent()));
        chapter.setTitle(chapterDTO.getTitle().trim());
        chapter.setCreatedAt(new Date()); chapter.setUpdatedAt(new Date());
        novelChapterMapper.insert(chapter);
        return Result.success();
    }

    @Override
    public Result<List<NovelChapter>> queryChapter(Integer novelId) {
        return queryChapter(novelId, false);
    }

    @Override
    public Result<List<NovelChapter>> queryChapter(Integer novelId, boolean includeDrafts) {
        Novel novel = access.readable(novelId.longValue());
        if (includeDrafts) access.author(novel.getId());
        List<NovelChapter> chapters = novelChapterMapper.selectList(new LambdaQueryWrapper<NovelChapter>()
                .eq(NovelChapter::getNovelId, novelId)
                .eq(!includeDrafts, NovelChapter::getStatus, "PUBLISHED")
                .orderByAsc(NovelChapter::getChapterNo));
        chapters.forEach(c -> c.setContent(null));
        return Result.success(chapters);
    }

    @Override
    public Result<NovelChapter> queryContent(Integer chapterId) {
        NovelChapter chapter = access.chapter(chapterId.longValue());
        return Result.success(chapter);
    }

    @Override
    @Transactional
    public Result<Void> updateChapter(Long chapterId, ChapterDTO dto) {
        checkChapterAuthor(chapterId);
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BusinessException("章节标题不能为空");
        }
        if (dto.getTitle().trim().length() > 200) {
            throw new BusinessException("章节标题不能超过200个字符");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BusinessException("章节正文不能为空");
        }
        if (!"DRAFT".equals(dto.getStatus()) && !"PUBLISHED".equals(dto.getStatus())) {
            throw new BusinessException("章节状态只能为DRAFT或PUBLISHED");
        }

        String text = dto.getContent().replaceAll("(?U)\\s", "");
        int wordCount = text.codePointCount(0, text.length());
        // 只修改允许编辑的字段，保留所属小说和章节序号。
        int affected = novelChapterMapper.update(null, new LambdaUpdateWrapper<NovelChapter>()
                .eq(NovelChapter::getId, chapterId)
                .set(NovelChapter::getTitle, dto.getTitle().trim())
                .set(NovelChapter::getContent, dto.getContent())
                .set(NovelChapter::getStatus, dto.getStatus())
                .set(NovelChapter::getWordCount, wordCount)
                .set(NovelChapter::getUpdatedAt, new Date()));
        if (affected == 0) {
            throw new BusinessException("章节不存在或已被删除");
        }
        return Result.success("章节修改成功", null);
    }

    @Override
    @Transactional
    public Result<Void> deleteChapter(Long chapterId) {
        checkChapterAuthor(chapterId);
        if (novelChapterMapper.deleteById(chapterId) == 0) {
            throw new BusinessException("章节不存在或已被删除");
        }
        return Result.success("章节删除成功", null);
    }

    @Override
    @Transactional
    public Result<Void> read(Integer chapterId) {
        NovelChapter chapter = access.chapter(chapterId.longValue());
        if(chapter == null){
            throw new BusinessException("章节不存在");
        }
        Long novelId = chapter.getNovelId();
        Novel novel = novelMapper.selectOne(new LambdaQueryWrapper<Novel>()
                .eq(Novel::getId, novelId).last("FOR UPDATE"));
        if(novel == null){
            throw new BusinessException("小说不存在");
        }
        // 登录用户5分钟内重复打开同一章节不重复计数，阅读记录仍更新。
        Long userId = UserContext.getUserId();
        String key = "read:" + novelId + ":" + chapterId;
        jakarta.servlet.http.HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes)
                org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest();
        jakarta.servlet.http.HttpSession session = request.getSession();
        String sessionKey = (userId == null ? "guest" : userId) + ":" + key;
        synchronized (session) {
            Long last = (Long) session.getAttribute(sessionKey);
            long now = System.currentTimeMillis();
            if (last == null || now - last >= 300_000) {
                novelMapper.update(null, new LambdaUpdateWrapper<Novel>().eq(Novel::getId, novelId)
                        .setSql("view_count = view_count + 1"));
                session.setAttribute(sessionKey, now);
            }
        }
        if (userId != null) library.record(userId, chapter);
        return Result.success();
    }

    private void checkChapterAuthor(Long chapterId) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("请先登录");
        }
        NovelChapter chapter = novelChapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new BusinessException("章节不存在");
        }
        Novel novel = novelMapper.selectById(chapter.getNovelId());
        if (novel == null) {
            throw new BusinessException("小说不存在");
        }
        if (!userId.equals(novel.getAuthorId())) {
            throw new BusinessException("只有小说作者可以操作该章节");
        }
    }

    private int countWords(String content) {
        String text = content.replaceAll("(?U)\\s", "");
        return text.codePointCount(0, text.length());
    }

    private void validate(ChapterDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank() || dto.getTitle().trim().length() > 200)
            throw new BusinessException(400, "章节标题须为1至200个字符");
        if (dto.getContent() == null || dto.getContent().isBlank()) throw new BusinessException(400, "正文不能为空");
        if (!"DRAFT".equals(dto.getStatus()) && !"PUBLISHED".equals(dto.getStatus()))
            throw new BusinessException(400, "无效章节状态");
    }
}




