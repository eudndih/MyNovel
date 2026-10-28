package com.novel.exception.handler;

import com.novel.exception.myExceptions.BusinessException;
import com.novel.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
@Slf4j
public class exceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> BusinessException(BusinessException e) {
      log.error(e.getMessage());
      return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public Result<Void> duplicate(org.springframework.dao.DuplicateKeyException e) {
        return Result.fail(409, "数据已存在，请勿重复提交");
    }

    @ExceptionHandler({org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    public Result<Void> invalid(Exception e) { return Result.fail(400, "请求参数格式不正确"); }

    @ExceptionHandler(Exception.class)
    public Result<Void> unexpected(Exception e) {
        log.error("请求处理失败", e);
        return Result.fail(500, "服务暂时不可用，请稍后重试");
    }
}
