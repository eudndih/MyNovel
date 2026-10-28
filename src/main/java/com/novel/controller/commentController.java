package com.novel.controller;

import com.novel.service.CommentInteractionService;
import com.novel.domain.dto.CommentDTO;
import com.novel.domain.dto.CommentBlockDTO;
import com.novel.domain.vo.CommentVO;
import com.novel.result.Result;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "评论接口")
public class commentController {
    private final CommentInteractionService interactions;

    public commentController(CommentInteractionService interactions) {
        this.interactions = interactions;
    }

    @GetMapping("/novel/{novelId}")
    @Operation(summary = "查询小说评论和回复")
    public Result<List<CommentVO>> list(@PathVariable("novelId") Long novelId,
            @RequestParam(name = "includeBlocked", defaultValue = "false") boolean includeBlocked) {
        return Result.success(interactions.list(novelId, includeBlocked));
    }

    @PostMapping
    @Operation(summary = "发表评论或回复")
    public Result<Void> add(@RequestBody CommentDTO dto) {
        interactions.add(dto);
        return Result.success();
    }

    @PutMapping("/{commentId}/block")
    @Operation(summary = "屏蔽或恢复评论")
    public Result<Void> block(@PathVariable("commentId") Long commentId, @RequestBody CommentBlockDTO dto) {
        interactions.block(commentId, dto.getBlocked());
        return Result.success();
    }


}
