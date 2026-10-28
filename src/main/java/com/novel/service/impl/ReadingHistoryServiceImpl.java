package com.novel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.novel.domain.po.ReadingHistory;
import com.novel.service.ReadingHistoryService;
import com.novel.mapper.ReadingHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 钱力川
* @description 针对表【reading_history(阅读历史表)】的数据库操作Service实现
* @createDate 2026-10-12 08:44:12
*/
@Service
public class ReadingHistoryServiceImpl extends ServiceImpl<ReadingHistoryMapper, ReadingHistory>
    implements ReadingHistoryService{

}




