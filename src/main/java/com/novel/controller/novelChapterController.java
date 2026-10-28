package com.novel.controller;

import com.novel.domain.po.NovelChapter;
import com.novel.domain.dto.ChapterDTO;
import com.novel.result.Result;
import com.novel.service.NovelChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chapters")
@AllArgsConstructor
@Tag(name = "章节接口")
public class novelChapterController {
    private final NovelChapterService novelChapterService;

    @GetMapping("{chapterId}")
    @Operation(summary = "查询章节正文")
    public Result<NovelChapter> queryContent(@PathVariable Integer chapterId) {
        return novelChapterService.queryContent(chapterId);
    }

    @PutMapping("/{chapterId}")
    @Operation(summary = "修改章节")
    public Result<Void> updateChapter(@PathVariable("chapterId") Long chapterId,
                                      @RequestBody ChapterDTO chapterDTO) {
        return novelChapterService.updateChapter(chapterId, chapterDTO);
    }

    @DeleteMapping("/{chapterId}")
    @Operation(summary = "删除章节")
    public Result<Void> deleteChapter(@PathVariable("chapterId") Long chapterId) {
        return novelChapterService.deleteChapter(chapterId);
    }

    @PostMapping("{chapterId}/read")
    public Result<Void> read(@PathVariable Integer chapterId) {
        return novelChapterService.read(chapterId);
    }
}
