package com.novel.service;

import com.novel.domain.dto.ChatDTO;
import com.novel.domain.vo.ChatVO;
import com.novel.domain.vo.SessionVO;
import com.novel.result.Result;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    Flux<String> continueWrite(ChatDTO dto);

    Result<SessionVO> createSession(ChatDTO dto);

   Result<List<SessionVO>> getSessions();

    Result<ChatVO> chat(String sessionId, String prompt);

    Result<List<ChatVO>> getChats(String sessionId);

    Result<Void> deleteSession(String sessionId);

    Result<Void> updateSessionTitle(String sessionId, String title);
}
