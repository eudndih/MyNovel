package com.novel.controller;

import com.novel.result.Result;
import com.novel.service.NovelLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/like")
@RequiredArgsConstructor
@Tag(name="用户点赞接口")
public class likeController {
    private final NovelLikeService  novelLikeService;
    private final com.novel.service.CommentInteractionService comments;

    @PostMapping("comment/{commentId}")
    public Result<Void> likeComment(@PathVariable Long commentId) {
        comments.like(commentId, true);
        return Result.success();
    }

    @DeleteMapping("comment/{commentId}")
    public Result<Void> unlikeComment(@PathVariable Long commentId) {
        comments.like(commentId, false);
        return Result.success();
    }

    @PostMapping("novel/{novelId}")
    @Operation(summary = "点赞")
    public Result<Void> like(@PathVariable Long novelId) {
        return novelLikeService.like(novelId);
    }

    @DeleteMapping("novel/{novelId}")
    @Operation(summary = "取消点赞")
    public Result<Void> unLike(@PathVariable Long novelId) {
        return novelLikeService.unLike(novelId);
    }
}
