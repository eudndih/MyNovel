package com.novel.controller;

import com.novel.domain.dto.ChapterDTO;
import com.novel.domain.dto.NovelDTO;
import com.novel.domain.po.Novel;
import com.novel.domain.po.NovelChapter;
import com.novel.result.Result;
import com.novel.domain.vo.PageResult;
import com.novel.service.NovelChapterService;
import com.novel.service.NovelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/novels")
@RequiredArgsConstructor
@Tag(name = "小说接口")
public class novelController {
    private final NovelService novelService;
    private final NovelChapterService chapterService;
    @GetMapping
    @Operation(summary = "分页查询小说")
    public Result<PageResult<Novel>> queryNovels(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "false") boolean mine) {
        return novelService.queryNovels(keyword, category, sort, page, size, mine);
    }

    @PostMapping
    @Operation(summary = "增加小说")
    public Result<Void> addNovel(@RequestBody NovelDTO novelDTO) {
        novelService.addNovel(novelDTO);
        return Result.success();
    }

    @GetMapping("{id}")
    @Operation(summary = "查询小说")
    public Result<Novel> queryNovelById(@PathVariable("id") long id) {
        return novelService.queryNovelById(id);
    }

    @PutMapping("{id}")
    @Operation(summary = "修改小说")
    public Result<Void> updateNovel(@PathVariable("id") long id, @RequestBody NovelDTO novelDTO) {
        novelService.updateNovel(id,novelDTO);
        return Result.success();
    }

    @DeleteMapping("{id}")
    @Operation(summary = "删除小说")
    public Result<Void> deleteNovel(@PathVariable("id") long id) {
        novelService.deleteNovel(id);
        return Result.success();
    }

    private final NovelChapterService novelChapterService;
    @PostMapping("{novelId}/chapters")
    @Operation(summary = "新建章节")
    public Result<Void> addChapter(@PathVariable("novelId") Integer novelId, @RequestBody ChapterDTO chapterDTO) {
        return novelChapterService.addChapter(novelId,chapterDTO);
    }

    @GetMapping("{novelId}/chapters")
    @Operation(summary = "查询章节")
    public Result<List<NovelChapter>> queryChapter(@PathVariable("novelId") Integer novelId,
            @RequestParam(defaultValue = "false") boolean includeDrafts){
        return novelChapterService.queryChapter(novelId, includeDrafts);
    }
}
