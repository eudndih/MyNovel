package com.novel.controller;

import com.novel.domain.po.Novel;
import com.novel.domain.vo.*;
import com.novel.result.Result;
import com.novel.service.PersonalLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Tag(name = "个人书架")
public class PersonalLibraryController {
    private final PersonalLibraryService library;

    @GetMapping("/reading-history")
    @Operation(summary = "我的阅读记录")
    public Result<PageResult<ReadingHistoryVO>> history(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "12") int size) {
        return Result.success(library.history(page, size));
    }

    @GetMapping("/likes")
    @Operation(summary = "我点赞的小说")
    public Result<PageResult<Novel>> likes(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "12") int size) {
        return Result.success(library.liked(page, size));
    }
}
