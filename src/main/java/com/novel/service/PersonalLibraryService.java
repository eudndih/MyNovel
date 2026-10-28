package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.novel.domain.po.*;
import com.novel.domain.vo.*;
import com.novel.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalLibraryService {
    private final NovelAccessService access;
    private final ReadingHistoryMapper history;
    private final NovelMapper novels;
    private final NovelChapterMapper chapters;
    private final NovelLikeMapper likes;

    @Transactional
    public void record(Long userId, NovelChapter chapter) {
        // 同小说的行锁由阅读上报调用方持有。
        ReadingHistory row = history.selectOne(new LambdaQueryWrapper<ReadingHistory>()
                .eq(ReadingHistory::getUserId, userId).eq(ReadingHistory::getNovelId, chapter.getNovelId()));
        if (row == null) {
            row = new ReadingHistory(); row.setUserId(userId); row.setNovelId(chapter.getNovelId());
            row.setChapterId(chapter.getId()); row.setLastReadAt(new Date()); history.insert(row);
        } else {
            row.setChapterId(chapter.getId()); row.setLastReadAt(new Date()); history.updateById(row);
        }
    }

    public PageResult<ReadingHistoryVO> history(int page, int size) {
        Long userId = access.loginId();
        // 隐藏已下架或已删除的小说、章节，分页总数与可见记录一致。
        List<ReadingHistoryVO> visible = new ArrayList<>();
        for (ReadingHistory row : history.selectList(new LambdaQueryWrapper<ReadingHistory>()
                .eq(ReadingHistory::getUserId, userId).orderByDesc(ReadingHistory::getLastReadAt))) {
            Novel novel = novels.selectById(row.getNovelId());
            NovelChapter chapter = chapters.selectById(row.getChapterId());
            if (novel == null || chapter == null) continue;
            if (!access.owner(novel) && (!"PUBLISHED".equals(novel.getStatus()) || !"PUBLISHED".equals(chapter.getStatus()))) continue;
            ReadingHistoryVO vo = new ReadingHistoryVO(); vo.setNovel(novel); vo.setChapterId(chapter.getId());
            vo.setChapterTitle(chapter.getTitle()); vo.setLastReadAt(row.getLastReadAt()); visible.add(vo);
        }
        int length = Math.min(Math.max(size, 1), 100);
        int number = Math.max(page, 0);
        int from = (int) Math.min((long) number * length, visible.size());
        return new PageResult<>(visible.subList(from, Math.min(from + length, visible.size())),
                visible.size(), (visible.size() + length - 1L) / length, number, length);
    }

    public PageResult<Novel> liked(int page, int size) {
        Long userId = access.loginId();
        List<Long> ids = likes.selectList(new LambdaQueryWrapper<NovelLike>().eq(NovelLike::getUserId, userId))
                .stream().map(NovelLike::getNovelId).collect(Collectors.toList());
        int length = Math.min(Math.max(size, 1), 100);
        int number = Math.max(page, 0);
        if (ids.isEmpty()) return new PageResult<>(Collections.emptyList(), 0, 0, number, length);
        return PageResult.from(novels.selectPage(new Page<>(number + 1L, length),
                new LambdaQueryWrapper<Novel>().in(Novel::getId, ids)
                        .and(q -> q.eq(Novel::getStatus, "PUBLISHED").or().eq(Novel::getAuthorId, userId))
                        .orderByDesc(Novel::getCreatedAt)));
    }
}
