package com.novel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.novel.domain.dto.CommentDTO;
import com.novel.domain.po.*;
import com.novel.domain.vo.CommentVO;
import com.novel.exception.myExceptions.BusinessException;
import com.novel.mapper.*;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentInteractionService {
    private final NovelAccessService access;
    private final NovelCommentMapper comments;
    private final CommentLikeMapper likes;
    private final SysUserMapper users;
    private final NovelMapper novels;

    // 新增、屏蔽、点赞均先锁同一小说，避免并发屏蔽后仍可回复。
    private void lockNovel(Long id) {
        if (novels.selectOne(new LambdaQueryWrapper<Novel>().eq(Novel::getId, id)
                .last("FOR UPDATE")) == null) throw new BusinessException(404, "小说不存在");
    }

    private Map<Long, NovelComment> byId(List<NovelComment> rows) {
        return rows.stream().collect(Collectors.toMap(NovelComment::getId, Function.identity()));
    }

    // 父评论被屏蔽时，一并隐藏该讨论分支，但不修改子评论自身的屏蔽标记。
    private boolean visible(NovelComment row, Map<Long, NovelComment> all) {
        Set<Long> seen = new HashSet<>();
        while (row != null) {
            if (!seen.add(row.getId()) || Integer.valueOf(1).equals(row.getBlocked())) return false;
            if (row.getParentId() == null) return true;
            row = all.get(row.getParentId());
        }
        return false;
    }

    public List<CommentVO> list(Long novelId, boolean includeBlocked) {
        Novel novel = access.readable(novelId);
        if (includeBlocked && !access.owner(novel)) throw new BusinessException(403, "只有作者可查看屏蔽评论");
        List<NovelComment> rows = comments.selectList(new LambdaQueryWrapper<NovelComment>()
                .eq(NovelComment::getNovelId, novelId).orderByAsc(NovelComment::getCreatedAt, NovelComment::getId));
        if (rows.isEmpty()) return Collections.emptyList();
        Map<Long, NovelComment> all = byId(rows);
        Set<Long> userIds = rows.stream().map(NovelComment::getUserId).collect(Collectors.toSet());
        Map<Long, SysUser> people = users.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        List<CommentLike> relations = likes.selectList(new LambdaQueryWrapper<CommentLike>()
                .in(CommentLike::getCommentId, all.keySet()));
        Map<Long, Long> counts = relations.stream().collect(Collectors.groupingBy(CommentLike::getCommentId, Collectors.counting()));
        Set<Long> mine = relations.stream().filter(l -> l.getUserId().equals(UserContext.getUserId()))
                .map(CommentLike::getCommentId).collect(Collectors.toSet());
        List<CommentVO> result = new ArrayList<>();
        for (NovelComment row : rows) {
            if (!includeBlocked && !visible(row, all)) continue;
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(row, vo);
            vo.setBlocked(Integer.valueOf(1).equals(row.getBlocked()));
            vo.setUsername(people.containsKey(row.getUserId()) ? people.get(row.getUserId()).getUsername() : "已注销用户");
            NovelComment parent = all.get(row.getParentId());
            if (parent != null) vo.setReplyToUsername(people.containsKey(parent.getUserId())
                    ? people.get(parent.getUserId()).getUsername() : "已注销用户");
            vo.setLikeCount(counts.getOrDefault(row.getId(), 0L));
            vo.setLiked(mine.contains(row.getId()));
            result.add(vo);
        }
        return result;
    }

    @Transactional
    public void add(CommentDTO dto) {
        Long userId = access.loginId();
        if (dto == null || dto.getNovelId() == null || dto.getContent() == null
                || dto.getContent().isBlank() || dto.getContent().trim().length() > 1000)
            throw new BusinessException(400, "评论须为1至1000个字符");
        lockNovel(dto.getNovelId());
        access.readable(dto.getNovelId());
        if (dto.getParentId() != null) {
            NovelComment parent = comments.selectById(dto.getParentId());
            if (parent == null || !dto.getNovelId().equals(parent.getNovelId()))
                throw new BusinessException(400, "被回复评论不存在或不属于该小说");
            Map<Long, NovelComment> all = byId(comments.selectList(new LambdaQueryWrapper<NovelComment>()
                    .eq(NovelComment::getNovelId, dto.getNovelId())));
            if (!visible(parent, all)) throw new BusinessException(403, "该评论已被屏蔽，无法回复");
        }
        NovelComment row = new NovelComment();
        row.setNovelId(dto.getNovelId()); row.setParentId(dto.getParentId()); row.setUserId(userId);
        row.setContent(dto.getContent().trim()); row.setBlocked(0); row.setCreatedAt(new Date());
        comments.insert(row);
        novels.update(null, new LambdaUpdateWrapper<Novel>().eq(Novel::getId, dto.getNovelId())
                .setSql("comment_count = comment_count + 1"));
    }

    @Transactional
    public void block(Long id, Boolean blocked) {
        if (blocked == null) throw new BusinessException(400, "blocked不能为空");
        NovelComment row = requireComment(id);
        lockNovel(row.getNovelId());
        access.author(row.getNovelId());
        comments.update(null, new LambdaUpdateWrapper<NovelComment>().eq(NovelComment::getId, id)
                .set(NovelComment::getBlocked, blocked ? 1 : 0)
                .set(NovelComment::getBlockedAt, blocked ? new Date() : null));
    }

    private NovelComment requireComment(Long id) {
        NovelComment row = comments.selectById(id);
        if (row == null) throw new BusinessException(404, "评论不存在");
        return row;
    }

    @Transactional
    public void like(Long id, boolean add) {
        Long userId = access.loginId();
        NovelComment row = requireComment(id);
        lockNovel(row.getNovelId());
        access.readable(row.getNovelId());
        if (add && !visible(row, byId(comments.selectList(new LambdaQueryWrapper<NovelComment>()
                .eq(NovelComment::getNovelId, row.getNovelId())))))
            throw new BusinessException(403, "无法点赞已屏蔽的评论");
        LambdaQueryWrapper<CommentLike> query = new LambdaQueryWrapper<CommentLike>()
                .eq(CommentLike::getUserId, userId).eq(CommentLike::getCommentId, id);
        if (!add) { likes.delete(query); return; }
        if (likes.selectCount(query) > 0) return;
        CommentLike relation = new CommentLike();
        relation.setCommentId(id); relation.setUserId(userId); relation.setCreatedAt(new Date());
        try { likes.insert(relation); } catch (DuplicateKeyException ignored) { /* 唯一键保证幂等 */ }
    }
}
