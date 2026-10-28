package com.novel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.domain.po.NovelComment;
import com.novel.service.NovelCommentService;
import com.novel.mapper.NovelCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author 钱力川
* @description 针对表【novel_comment(小说评论表)】的数据库操作Service实现
* @createDate 2026-10-12 08:44:12
*/
@Service
public class NovelCommentServiceImpl extends ServiceImpl<NovelCommentMapper, NovelComment>
    implements NovelCommentService{

}




