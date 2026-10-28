package com.novel.tolls;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.novel.domain.po.Novel;
import com.novel.mapper.NovelMapper;
import com.novel.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class novelTolls {
    private final NovelMapper novelMapper;
    @Tool(description = "根据小说分类查询相关小说")
    public List<Novel> queryNovelsByCategory(String category) {
        List<Novel> novels = novelMapper.selectList(new LambdaQueryWrapper<Novel>()
                .eq(Novel::getCategory, category)
                .eq(Novel::getStatus, "PUBLISHED"));
        if (novels.isEmpty()) {
            return Collections.emptyList();
        }
        return novels;
    }

    @Tool(description = "查询我创造了那些小说")
    public List<Novel> queryMyNovels(ToolContext toolContext) {
        Long userId =(Long) toolContext.getContext().get("userId");
        List<Novel> novels = novelMapper.selectList(new LambdaQueryWrapper<Novel>()
                .eq(Novel::getAuthorId, userId));
        if (novels.isEmpty()) {
            return Collections.emptyList();
        }
        return novels;
    }
}
